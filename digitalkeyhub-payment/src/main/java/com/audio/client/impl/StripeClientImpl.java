package com.audio.client.impl;

import com.audio.client.StripeClient;
import com.audio.dto.request.PaymentServiceRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeClientImpl implements StripeClient {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public PaymentIntent createPaymentIntent(PaymentServiceRequest request) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)).longValue());
        params.put("currency", request.getCurrency());
        params.put("description", "Payment for order #" + request.getOrderId());
        params.put("metadata", Map.of(
                "order_id", request.getOrderId().toString(),
                "user_id", request.getUserId().toString()
        ));
        params.put("automatic_payment_methods", Map.of("enabled", true));

        return PaymentIntent.create(params);
    }
}
