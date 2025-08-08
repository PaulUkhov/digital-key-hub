package com.audio.payment.controller;

import com.audio.payment.dto.request.PaymentRequest;
import com.audio.payment.dto.response.PaymentResponse;
import com.audio.payment.mappper.PaymentMapper;
import com.audio.service.PaymentInvokerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentInvokerService paymentInvokerService;
    private final PaymentMapper paymentMapper;


    @PostMapping
    public PaymentResponse createPayment(
            @Valid @RequestBody PaymentRequest request,
            @RequestParam("userId") UUID userId) {
        return paymentMapper.toResponse(
                paymentInvokerService.createPayment(
                        paymentMapper.toServiceRequest(request, userId)
                )
        );
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        log.info("Received payment webhook");
        paymentInvokerService.handlePaymentWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/payment")
    public PaymentResponse initiatePayment(
            @PathVariable("orderId") UUID orderId,
            @RequestParam("userId") UUID userId) {
        return paymentMapper.toResponse(
                paymentInvokerService.initiatePayment(orderId, userId)
        );
    }
}