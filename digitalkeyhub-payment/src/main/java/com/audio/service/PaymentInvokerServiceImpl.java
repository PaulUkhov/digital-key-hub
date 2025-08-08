package com.audio.service;

import com.audio.dto.request.PaymentServiceRequest;
import com.audio.dto.response.OrderServiceResponse;
import com.audio.dto.response.PaymentServiceResponse;
import com.audio.entity.PaymentEntity;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Service
public class PaymentInvokerServiceImpl implements PaymentInvokerService {

    private final PaymentService paymentService;


    @Override
    public PaymentServiceResponse createPayment(PaymentServiceRequest request) {
        try {
            CompletableFuture<PaymentServiceResponse> future = paymentService.createPaymentAsync(request);
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to create payment", e);
        }
    }

    public CompletableFuture<Void> handlePaymentWebhook(String payload, String sigHeader) {
        return paymentService.handlePaymentWebhook(payload, sigHeader);
    }

    public CompletableFuture<Void> completePayment(PaymentIntent paymentIntent) {
        return paymentService.completePayment(paymentIntent);
    }

    public CompletableFuture<Void> sendPaymentReceiptEmail(OrderServiceResponse order, PaymentEntity payment) {
        return paymentService.sendPaymentReceiptEmail(order, payment);
    }

    public PaymentServiceResponse initiatePayment(UUID orderId, UUID userId) {
        try {
            CompletableFuture<PaymentServiceResponse> future = paymentService.initiatePayment(orderId, userId);
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to initiate payment", e);
        }
    }
}