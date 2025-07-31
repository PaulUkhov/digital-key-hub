package com.audio.mapper;

import com.audio.dto.OrderDto;
import com.audio.dto.OrderItemDto;
import com.audio.entity.OrderEntity;
import com.audio.entity.OrderItemEntity;

public interface OrderMapper {
    OrderEntity toOrderEntity(OrderDto orderDto);

    OrderDto toOrderDto(OrderEntity orderEntity);

    OrderItemDto toOrderItemDto(OrderItemEntity orderItem);

    OrderItemEntity toOrderItemEntity(OrderItemDto orderItemDto);
}
