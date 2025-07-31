package com.audio.mapper;

import com.audio.dto.OrderDto;
import com.audio.dto.OrderItemDto;
import com.audio.entity.OrderEntity;
import com.audio.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderMapperImpl implements OrderMapper {
    @Override
    public OrderEntity toOrderEntity(OrderDto orderDto) {
        return OrderEntity.builder()
                .id(orderDto.getId())
                .userId(orderDto.getUserId())
                .items(orderDto.getItems().stream().map(this::toOrderItemEntity).toList())
                .createdAt(orderDto.getCreatedAt())
                .status(orderDto.getStatus())
                .totalAmount(orderDto.getTotalAmount())
                .build();
    }

    @Override
    public OrderDto toOrderDto(OrderEntity orderEntity) {
        return OrderDto.builder()
                .id(orderEntity.getId())
                .userId(orderEntity.getUserId())
                .items(orderEntity.getItems().stream().map(this::toOrderItemDto).toList())
                .createdAt(orderEntity.getCreatedAt())
                .status(orderEntity.getStatus())
                .totalAmount(orderEntity.getTotalAmount())
                .build();
    }

    @Override
    public OrderItemDto toOrderItemDto(OrderItemEntity orderItem) {
        return OrderItemDto.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProductId())
                .quantity(orderItem.getQuantity())
                .subtotal(orderItem.getSubtotal())
                .unitPrice(orderItem.getUnitPrice())
                .quantity(orderItem.getQuantity())
                .build();
    }

    @Override
    public OrderItemEntity toOrderItemEntity(OrderItemDto orderItemDto) {
        return OrderItemEntity.builder()
                .id(orderItemDto.getId())
                .productId(orderItemDto.getProductId())
                .quantity(orderItemDto.getQuantity())
                .subtotal(orderItemDto.getSubtotal())
                .unitPrice(orderItemDto.getUnitPrice())
                .quantity(orderItemDto.getQuantity())
                .build();
    }
}
