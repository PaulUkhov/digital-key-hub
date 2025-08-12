package com.audio.service;

import com.audio.client.StripeClient;
import com.audio.dto.request.PaymentServiceRequest;
import com.audio.dto.response.*;
import com.audio.entity.*;
import com.audio.enums.OrderStatus;
import com.audio.enums.PaymentStatus;
import com.audio.exception.*;
import com.audio.repository.PaymentRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {
    private static final String CURRENCY = "USD";
    private static final String DEFAULT_SUPPORT_EMAIL = "support@audio.com";
    private static final String DEFAULT_BASE_URL = "https://audio.com";

    private final PaymentRepository paymentRepository;
    private final StripeClient stripeClient;
    private final OrderService orderService;
    private final EmailService emailService;
    private final ProductService productService;
    private final UserService userService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Value("${stripe.secret-key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<PaymentServiceResponse> initiatePayment(UUID orderId, UUID userId) {
        OrderEntity order = orderService.getOrderEntity(orderId, userId);

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new OrderOperationException("Order cannot be paid");
        }

        PaymentServiceRequest request = new PaymentServiceRequest(
                orderId,
                userId,
                order.getTotalAmount(),
                CURRENCY
        );
        return createPaymentAsync(request);
    }

    @Async
    public CompletableFuture<PaymentServiceResponse> createPaymentAsync(PaymentServiceRequest request) {
        try {
            PaymentIntent intent = stripeClient.createPaymentIntent(request);

            PaymentEntity payment = buildPaymentEntity(request, intent);
            paymentRepository.save(payment);

            PaymentServiceResponse response = buildPaymentResponse(payment, intent);
            return CompletableFuture.completedFuture(response);
        } catch (Exception e) {
            CompletableFuture<PaymentServiceResponse> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new PaymentProcessingException("Failed to create payment: " + e.getMessage()));
            return failedFuture;
        }
    }

@Async
    public CompletableFuture<Void> handlePaymentWebhook(String payload, String sigHeader) {
        try {
            log.info("Processing payment webhook event");
            Event event = validateWebhookEvent(payload, sigHeader);
            processWebhookEvent(event);
            return CompletableFuture.completedFuture(null);
        } catch (SignatureVerificationException e) {
            log.error("Invalid webhook signature", e);
            CompletableFuture<Void> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new InvalidWebhookSignatureException("Invalid signature"));
            return failedFuture;
        } catch (Exception e) {
            log.error("Webhook processing failed", e);
            CompletableFuture<Void> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new PaymentProcessingException("Webhook error: " + e.getMessage()));
            return failedFuture;
        }
    }

    private void processWebhookEvent(Event event) throws StripeException {
        String eventType = event.getType();
        log.info("Processing event type: {}", eventType);

        PaymentIntent paymentIntent = parsePaymentIntent(event);

        switch (eventType) {
            case "payment_intent.succeeded":
                handleSuccessfulPayment(paymentIntent);
                break;
            case "payment_intent.payment_failed":
                handleFailedPayment(paymentIntent);
                break;
            default:
                log.warn("Unhandled event type: {}", eventType);
        }
    }

    private void handleSuccessfulPayment(PaymentIntent paymentIntent) {
        log.info("Payment succeeded: {}", paymentIntent.getId());
        completePayment(paymentIntent);
    }

    private void handleFailedPayment(PaymentIntent paymentIntent) {
        String errorMessage = paymentIntent.getLastPaymentError() != null
                ? paymentIntent.getLastPaymentError().getMessage()
                : "Unknown error";
        log.warn("Payment failed: {}, Error: {}", paymentIntent.getId(), errorMessage);
        failPayment(paymentIntent.getId(), errorMessage);
    }
@Async
   public CompletableFuture<Void> completePayment(PaymentIntent paymentIntent) {
        String stripePaymentId = paymentIntent.getId();
        log.info("Completing payment: {}", stripePaymentId);

        try {
            PaymentEntity payment = paymentRepository.findByStripePaymentId(stripePaymentId)
                    .orElseGet(() -> createNewPaymentEntity(paymentIntent));

            updatePaymentAsSuccessful(payment);
            processOrderCompletion(payment);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(
                    new PaymentProcessingException("Failed to complete payment: " + e.getMessage()));
        }
    }


    private PaymentEntity createNewPaymentEntity(PaymentIntent paymentIntent) {
        PaymentEntity payment = new PaymentEntity();
        payment.setStripePaymentId(paymentIntent.getId());
        payment.setAmount(convertStripeAmountToBigDecimal(paymentIntent.getAmount()));
        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setCompletedAt(LocalDateTime.now());

        extractOrderIdFromMetadata(paymentIntent).ifPresent(payment::setOrderId);

        return payment;
    }

    private Optional<UUID> extractOrderIdFromMetadata(PaymentIntent paymentIntent) {
        if (paymentIntent.getMetadata() != null && paymentIntent.getMetadata().containsKey("order_id")) {
            try {
                return Optional.of(UUID.fromString(paymentIntent.getMetadata().get("order_id")));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid order_id in payment metadata", e);
            }
        }
        return Optional.empty();
    }

    private void updatePaymentAsSuccessful(PaymentEntity payment) {
        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    private void processOrderCompletion(PaymentEntity payment) {
        if (payment.getOrderId() != null) {
            OrderServiceResponse order = orderService.completeOrder(payment.getOrderId());
            sendPaymentReceiptEmail(order, payment);
        }
    }

    @Async
    public CompletableFuture<Void> sendPaymentReceiptEmail(OrderServiceResponse order, PaymentEntity payment) {
        try {
            UserServiceResponse user = userService.findById(order.getUserId())
                    .orElseThrow(() -> new UserNotFoundException(order.getUserId()));

            Map<String, Object> emailData = new HashMap<>();

            emailData.put("orderId", order.getId());
            emailData.put("amount", payment.getAmount());

            addDigitalContent(order, emailData);

            emailService.sendPaymentReceipt(
                    user.email(),
                    "Your Purchase - Digital Key Included #" + order.getId(),
                    emailData
            );
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Failed to send payment receipt email for order {}", order.getId(), e);
            return CompletableFuture.failedFuture(
                    new PaymentProcessingException("Failed to send payment receipt email: " + e.getMessage()));

        }
    }

    private void addDigitalContent(OrderServiceResponse order, Map<String, Object> emailData) {
        for (OrderItemServiceResponse item : order.getItems()) {
            ProductServiceResponsePaid product = productService.getProductForPaid(item.getProductId());
            if (product.digitalContent() != null && !product.digitalContent().isEmpty()) {
                emailData.put("digitalKey", product.digitalContent());
                emailData.put("downloadLink",
                        String.format("%s/download/%s", DEFAULT_BASE_URL, item.getId()));
                break;
            }
        }
        if (!emailData.containsKey("digitalKey")) {
            emailData.put("digitalKey", "No digital key provided");
        }
    }


    private void failPayment(String stripePaymentId, String errorMessage) {
        PaymentEntity payment = paymentRepository.findByStripePaymentId(stripePaymentId)
                .orElseThrow(() -> new PaymentNotFoundException(stripePaymentId));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setErrorMessage(errorMessage);
        paymentRepository.save(payment);

        if (payment.getOrderId() != null) {
            orderService.failOrder(payment.getOrderId(), errorMessage);
        }
    }

    private PaymentEntity buildPaymentEntity(PaymentServiceRequest request, PaymentIntent intent) {
        return PaymentEntity.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .stripePaymentId(intent.getId())
                .status(PaymentStatus.REQUIRES_PAYMENT_METHOD)
                .build();
    }

    private PaymentServiceResponse buildPaymentResponse(PaymentEntity payment, PaymentIntent intent) {
        return new PaymentServiceResponse(
                payment.getId(),
                intent.getClientSecret(),
                payment.getAmount()
        );
    }

    private BigDecimal convertStripeAmountToBigDecimal(long stripeAmount) {
        return BigDecimal.valueOf(stripeAmount / 100.0);
    }

    private Event validateWebhookEvent(String payload, String sigHeader) throws StripeException {
        return Webhook.constructEvent(payload, sigHeader, webhookSecret);
    }

    private PaymentIntent parsePaymentIntent(Event event) throws StripeException {
        JsonObject jsonObject = JsonParser.parseString(event.getDataObjectDeserializer().getRawJson())
                .getAsJsonObject();
        return PaymentIntent.retrieve(jsonObject.get("id").getAsString());
    }
}
