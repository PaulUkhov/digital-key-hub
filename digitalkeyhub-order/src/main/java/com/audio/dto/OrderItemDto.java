package com.audio.dto;

import com.audio.entity.OrderEntity;
import jakarta.persistence.*;
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
}
