package com.audio.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    private UUID id;

    private UUID productId;

    private int quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;

    public OrderItemDto(UUID testProductId, int quantity) {
        this.id = testProductId;
        this.productId = testProductId;
    }
}
