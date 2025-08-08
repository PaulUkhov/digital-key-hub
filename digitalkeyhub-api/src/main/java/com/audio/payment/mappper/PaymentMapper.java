package com.audio.payment.mappper;

import com.audio.dto.request.PaymentServiceRequest;
import com.audio.dto.response.PaymentServiceResponse;
import com.audio.payment.dto.request.PaymentRequest;
import com.audio.payment.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

    public PaymentServiceRequest toServiceRequest(PaymentRequest request, UUID userId) {
        return PaymentServiceRequest.builder()
                .orderId(request.getOrderId())
                .userId(userId)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .build();
    }

    public PaymentResponse toResponse(PaymentServiceResponse serviceResponse) {
        return PaymentResponse.builder()
                .paymentId(serviceResponse.getPaymentId())
                .clientSecret(serviceResponse.getClientSecret())
                .amount(serviceResponse.getAmount())
                .build();
    }
}