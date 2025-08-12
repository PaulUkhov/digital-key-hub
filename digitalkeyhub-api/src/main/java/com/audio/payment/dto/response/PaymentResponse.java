package com.audio.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing payment details")
public class PaymentResponse {
    @Schema(
            description = "Unique identifier of the payment",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID paymentId;

    @Schema(
            description = "Client secret for Stripe payment confirmation",
            example = "pi_1Hjkjs2eZvKYlo2C0ZJZ6X2H_secret_XYZ123"
    )
    private String clientSecret;

    @Schema(
            description = "Payment amount",
            example = "99.99"
    )
    private BigDecimal amount;
}