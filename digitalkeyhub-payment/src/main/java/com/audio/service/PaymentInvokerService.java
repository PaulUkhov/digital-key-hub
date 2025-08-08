package com.audio.service;

import com.audio.dto.request.PaymentServiceRequest;
import com.audio.dto.response.PaymentServiceResponse;

public interface PaymentInvokerService {
    PaymentServiceResponse createPayment(PaymentServiceRequest request);
}
