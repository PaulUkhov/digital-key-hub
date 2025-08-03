package com.audio.client;

import com.audio.dto.PaymentRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripeClient {
    PaymentIntent createPaymentIntent(PaymentRequest request) throws StripeException;
}
