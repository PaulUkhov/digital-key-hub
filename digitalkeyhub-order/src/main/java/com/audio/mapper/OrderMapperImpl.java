package com.audio.mapper;

import com.audio.dto.response.OrderServiceResponse;
import com.audio.dto.response.OrderItemServiceResponse;
import com.audio.entity.OrderEntity;
import com.audio.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderMapperImpl implements OrderMapper {
    @Override
    public OrderEntity toOrderEntity(OrderServiceResponse orderDto) {
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
    public OrderServiceResponse toOrderDto(OrderEntity orderEntity) {
        return OrderServiceResponse.builder()
                .id(orderEntity.getId())
                .userId(orderEntity.getUserId())
                .items(orderEntity.getItems().stream().map(this::toOrderItemDto).toList())
                .createdAt(orderEntity.getCreatedAt())
                .status(orderEntity.getStatus())
                .totalAmount(orderEntity.getTotalAmount())
                .build();
    }

    @Override
    public OrderItemServiceResponse toOrderItemDto(OrderItemEntity orderItem) {
        return OrderItemServiceResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProductId())
                .quantity(orderItem.getQuantity())
                .subtotal(orderItem.getSubtotal())
                .unitPrice(orderItem.getUnitPrice())
                .quantity(orderItem.getQuantity())
                .build();
    }

    @Override
    public OrderItemEntity toOrderItemEntity(OrderItemServiceResponse orderItemDto) {
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
