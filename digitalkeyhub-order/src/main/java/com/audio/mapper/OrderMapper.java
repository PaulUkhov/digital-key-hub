package com.audio.mapper;

import com.audio.dto.response.OrderServiceResponse;
import com.audio.dto.response.OrderItemServiceResponse;
import com.audio.entity.OrderEntity;
import com.audio.entity.OrderItemEntity;

public interface OrderMapper {
    OrderEntity toOrderEntity(OrderServiceResponse orderDto);

    OrderServiceResponse toOrderDto(OrderEntity orderEntity);

    OrderItemServiceResponse toOrderItemDto(OrderItemEntity orderItem);

    OrderItemEntity toOrderItemEntity(OrderItemServiceResponse orderItemDto);
}
