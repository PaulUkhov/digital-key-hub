package com.audio.service;

import com.audio.client.StripeClient;
import com.audio.dto.*;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final StripeClient stripeClient;
    private final OrderService orderService;
    private final EmailService emailService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Value("${stripe.secret-key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public PaymentResponse initiatePayment(UUID orderId, UUID userId) {
        OrderEntity order = orderService.getOrderEntity(orderId, userId);

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new OrderOperationException("Order cannot be paid");
        }

        PaymentRequest request = new PaymentRequest(
                orderId,
                userId,
                order.getTotalAmount(),
                "USD"
        );

        return createPayment(request);
    }

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        try {
            PaymentIntent intent = stripeClient.createPaymentIntent(request);

            PaymentEntity payment = PaymentEntity.builder()
                    .orderId(request.getOrderId())
                    .userId(request.getUserId())
                    .amount(request.getAmount())
                    .stripePaymentId(intent.getId())
                    .status(PaymentStatus.REQUIRES_PAYMENT_METHOD)
                    .build();

            paymentRepository.save(payment);

            return new PaymentResponse(
                    payment.getId(),
                    intent.getClientSecret(),
                    payment.getAmount()
            );
        } catch (Exception e) {
            throw new PaymentProcessingException("Failed to create payment: " + e.getMessage());
        }
    }

    @Transactional
    public void handlePaymentWebhook(String payload, String sigHeader) {
        try {
            log.info("Received webhook payload");

            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            log.info("Processing event type: {}", event.getType());

            if ("payment_intent.succeeded".equals(event.getType())) {
                PaymentIntent paymentIntent = parsePaymentIntent(event);
                log.info("Payment succeeded: {}", paymentIntent.getId());
                completePayment(paymentIntent);
            } else if ("payment_intent.payment_failed".equals(event.getType())) {
                PaymentIntent paymentIntent = parsePaymentIntent(event);
                log.warn("Payment failed: {}", paymentIntent.getId());
                failPayment(paymentIntent.getId(),
                        paymentIntent.getLastPaymentError() != null ?
                                paymentIntent.getLastPaymentError().getMessage() : "Unknown error");
            }
        } catch (SignatureVerificationException e) {
            log.error("Invalid webhook signature", e);
            throw new InvalidWebhookSignatureException("Invalid signature");
        } catch (Exception e) {
            log.error("Webhook processing failed", e);
            throw new PaymentProcessingException("Webhook error: " + e.getMessage());
        }
    }

    private PaymentIntent parsePaymentIntent(Event event) throws StripeException {
        JsonObject jsonObject = JsonParser.parseString(event.getDataObjectDeserializer().getRawJson())
                .getAsJsonObject();
        String paymentIntentId = jsonObject.get("id").getAsString();
        return PaymentIntent.retrieve(paymentIntentId);
    }

    void completePayment(PaymentIntent paymentIntent) {
        String stripePaymentId = paymentIntent.getId();
        log.info("Completing payment: {}", stripePaymentId);

        PaymentEntity payment = paymentRepository.findByStripePaymentId(stripePaymentId)
                .orElseGet(() -> {
                    log.info("Creating new payment record for {}", stripePaymentId);
                    PaymentEntity newPayment = new PaymentEntity();
                    newPayment.setStripePaymentId(stripePaymentId);
                    newPayment.setAmount(BigDecimal.valueOf(paymentIntent.getAmount()));
                    newPayment.setStatus(PaymentStatus.SUCCEEDED);
                    newPayment.setCompletedAt(LocalDateTime.now());

                    if (paymentIntent.getMetadata() != null &&
                            paymentIntent.getMetadata().containsKey("order_id")) {
                        newPayment.setOrderId(
                                UUID.fromString(paymentIntent.getMetadata().get("order_id"))
                        );
                    }
                    return newPayment;
                });

        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        if (payment.getOrderId() != null) {
            orderService.completeOrder(payment.getOrderId());
        }

//        Map<String, Object> emailData = new HashMap<>();
//        emailData.put("orderId", order.getId());
//        emailData.put("paymentDate", LocalDateTime.now());
//        emailData.put("paymentMethod", "Credit Card");
//        emailData.put("productName", "Premium License Key");
//        emailData.put("amount", 49.99);
//        emailData.put("digitalContent", "License Key: ABCD-1234-EFGH-5678");
//        emailData.put("downloadLink", "https://yourdomain.com/download/123");
//        emailData.put("supportEmail", "support@yourdomain.com");
//        emailData.put("baseUrl", "https://yourdomain.com");
//
//        emailService.sendPaymentReceipt(
//                customerEmail,
//                "Your Payment Receipt - Order #123",
//                emailData
//        );
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
}