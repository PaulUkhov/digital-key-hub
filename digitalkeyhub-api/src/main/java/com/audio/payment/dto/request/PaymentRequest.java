package com.audio.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "Request object for payment processing")
public class PaymentRequest {
    @Schema(
            description = "Unique identifier of the order being paid",
            example = "123e4567-e89b-12d3-a456-426614174000",
            required = true
    )
    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @Schema(
            description = "Payment amount",
            example = "99.99",
            minimum = "0.01",
            required = true
    )
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;

    @Schema(
            description = "Currency code (ISO 4217)",
            example = "USD",
            minLength = 3,
            maxLength = 3,
            required = true
    )
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency;
}