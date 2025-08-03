package com.audio.controller.payment;

import com.audio.dto.PaymentRequest;
import com.audio.dto.PaymentResponse;
import com.audio.service.PaymentService;
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
    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse createPayment(
            @RequestBody PaymentRequest request,
            @RequestParam("userId") UUID userId) {
        request.setUserId(userId);
        return paymentService.createPayment(request);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        log.info("------Payload: {} ---------", payload);
        paymentService.handlePaymentWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/payment")
    public PaymentResponse initiatePayment(
            @PathVariable("orderId") UUID orderId,
            @RequestParam("userId") UUID userId) {
        return paymentService.initiatePayment(orderId, userId);
    }
}
