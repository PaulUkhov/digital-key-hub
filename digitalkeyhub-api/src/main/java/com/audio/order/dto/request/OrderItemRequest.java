package com.audio.order.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 999, message = "Quantity cannot exceed 999")
    private Integer quantity;

    @DecimalMin(value = "0.01", message = "Unit price must be at least 0.01")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.01", message = "Subtotal must be at least 0.01")
    private BigDecimal subtotal;

    public OrderItemRequest(UUID productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }
}