package com.audio.service;

import com.audio.dto.request.PaymentServiceRequest;
import com.audio.dto.response.OrderServiceResponse;
import com.audio.dto.response.PaymentServiceResponse;
import com.audio.entity.PaymentEntity;
import com.stripe.model.PaymentIntent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PaymentInvokerService {
    PaymentServiceResponse createPayment(PaymentServiceRequest request);
    void handlePaymentWebhook(String payload, String sigHeader);
    CompletableFuture<Void> completePayment(PaymentIntent paymentIntent);
    CompletableFuture<Void> sendPaymentReceiptEmail(OrderServiceResponse order, PaymentEntity payment);
    PaymentServiceResponse initiatePayment(UUID orderId, UUID userId);


}
