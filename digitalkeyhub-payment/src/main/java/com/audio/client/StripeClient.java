package com.audio.client;

import com.audio.dto.request.PaymentServiceRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripeClient {
    PaymentIntent createPaymentIntent(PaymentServiceRequest request) throws StripeException;
}
