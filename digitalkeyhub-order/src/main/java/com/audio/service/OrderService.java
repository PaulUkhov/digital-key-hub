package com.audio.service;


import com.audio.dto.*;
import com.audio.entity.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


public interface OrderService {


    OrderDto createOrder(OrderCreateDto orderCreateDto, UUID userId);


    OrderDto getOrder(UUID orderId, UUID userId);


    List<OrderDto> getUserOrders(UUID userId);


    void cancelOrder(UUID orderId, UUID userId);

    List<OrderItemEntity> validateAndCreateItems(List<OrderItemDto> items);

    OrderItemEntity createOrderItem(OrderItemDto itemDto);

    BigDecimal calculateTotalAmount(List<OrderItemEntity> items);

    void updateProductStocks(List<OrderItemEntity> items, boolean isRestock);


    OrderDto completeOrder(UUID orderId);


    void failOrder(UUID orderId, String reason);

    OrderEntity getOrderEntity(UUID orderId);

    OrderEntity getOrderEntity(UUID orderId, UUID userId);
}
