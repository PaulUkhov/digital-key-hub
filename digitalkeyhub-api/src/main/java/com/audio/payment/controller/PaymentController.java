package com.audio.payment.controller;

import com.audio.payment.dto.request.PaymentRequest;
import com.audio.payment.dto.response.PaymentResponse;
import com.audio.payment.mappper.PaymentMapper;
import com.audio.service.PaymentInvokerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Payment", description = "Payment processing API")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentInvokerService paymentInvokerService;
    private final PaymentMapper paymentMapper;

    @Operation(
            summary = "Create payment",
            description = "Processes a new payment for the specified order"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment processed successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid payment data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    @PostMapping
    public PaymentResponse createPayment(
            @Parameter(description = "Payment request details", required = true)
            @Valid @RequestBody PaymentRequest request,

            @Parameter(
                    description = "ID of the user making the payment",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @RequestParam("userId") UUID userId) {
        return paymentMapper.toResponse(
                paymentInvokerService.createPayment(
                        paymentMapper.toServiceRequest(request, userId)
                )
        );
    }

    @Operation(
            summary = "Handle payment webhook",
            description = "Processes payment webhook notifications from Stripe"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Webhook processed successfully"
    )
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @Parameter(
                    description = "Raw webhook payload",
                    required = true
            )
            @RequestBody String payload,

            @Parameter(
                    description = "Stripe signature header for verification",
                    required = true
            )
            @RequestHeader("Stripe-Signature") String sigHeader) {
        log.info("Received payment webhook");
        paymentInvokerService.handlePaymentWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Initiate payment",
            description = "Initiates a new payment for the specified order"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment initiated successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    @PostMapping("/{orderId}/payment")
    public PaymentResponse initiatePayment(
            @Parameter(
                    description = "ID of the order to pay for",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @PathVariable("orderId") UUID orderId,

            @Parameter(
                    description = "ID of the user making the payment",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @RequestParam("userId") UUID userId) {
        return paymentMapper.toResponse(
                paymentInvokerService.initiatePayment(orderId, userId)
        );
    }
}