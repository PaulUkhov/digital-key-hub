package com.audio.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemServiceResponse {

    private UUID id;

    private UUID productId;

    private int quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;

    public OrderItemServiceResponse(UUID testProductId, int quantity) {
        this.id = testProductId;
        this.productId = testProductId;
    }

    public OrderItemServiceResponse(UUID productId, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }
}
