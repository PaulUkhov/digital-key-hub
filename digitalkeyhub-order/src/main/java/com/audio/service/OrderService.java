package com.audio.service;


import com.audio.dto.request.OrderCreateServiceRequest;
import com.audio.dto.response.OrderItemServiceResponse;
import com.audio.dto.response.OrderServiceResponse;
import com.audio.entity.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


public interface OrderService {


    OrderServiceResponse createOrder(OrderCreateServiceRequest orderCreateDto, UUID userId);


    OrderServiceResponse getOrder(UUID orderId, UUID userId);


    List<OrderServiceResponse> getUserOrders(UUID userId);


    void cancelOrder(UUID orderId, UUID userId);

    List<OrderItemEntity> validateAndCreateItems(List<OrderItemServiceResponse> items);

    OrderItemEntity createOrderItem(OrderItemServiceResponse itemDto);

    BigDecimal calculateTotalAmount(List<OrderItemEntity> items);

    void updateProductStocks(List<OrderItemEntity> items, boolean isRestock);


    OrderServiceResponse completeOrder(UUID orderId);


    void failOrder(UUID orderId, String reason);

    OrderEntity getOrderEntity(UUID orderId);

    OrderEntity getOrderEntity(UUID orderId, UUID userId);
}
