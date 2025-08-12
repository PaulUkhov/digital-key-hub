package com.audio.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request object for order item details")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

    @Schema(description = "Unique identifier of the product", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Product ID is required")
    private UUID productId;

    @Schema(description = "Quantity of the product", required = true, minimum = "1", maximum = "999", example = "2")
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 999, message = "Quantity cannot exceed 999")
    private Integer quantity;

    @Schema(description = "Price per unit of the product", minimum = "0.01", example = "19.99")
    @DecimalMin(value = "0.01", message = "Unit price must be at least 0.01")
    private BigDecimal unitPrice;

    @Schema(description = "Subtotal for this item (quantity × unit price)", minimum = "0.01", example = "39.98")
    @DecimalMin(value = "0.01", message = "Subtotal must be at least 0.01")
    private BigDecimal subtotal;

    public OrderItemRequest(UUID productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }
}