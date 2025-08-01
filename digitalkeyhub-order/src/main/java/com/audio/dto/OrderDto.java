package com.audio.dto;

import com.audio.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private UUID id;

    private UUID userId;

    private List<OrderItemDto> items = new ArrayList<>();

    private OrderStatus status;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt = LocalDateTime.now();
}
