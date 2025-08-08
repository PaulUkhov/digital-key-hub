package com.audio.order.mapper;


import com.audio.dto.request.OrderCreateServiceRequest;
import com.audio.dto.response.OrderItemServiceResponse;
import com.audio.dto.response.OrderServiceResponse;
import com.audio.order.dto.request.OrderCreateRequest;
import com.audio.order.dto.request.OrderItemRequest;
import com.audio.order.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    public OrderCreateServiceRequest toServiceRequest(OrderCreateRequest request) {
        return new OrderCreateServiceRequest(
                request.getItems().stream()
                        .map(this::toServiceItem)
                        .collect(Collectors.toList())
        );
    }

    public OrderItemServiceResponse toServiceItem(OrderItemRequest item) {
        return new OrderItemServiceResponse(
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }

    public OrderResponse toResponse(OrderServiceResponse serviceResponse) {
        return OrderResponse.builder()
                .id(serviceResponse.getId())
                .userId(serviceResponse.getUserId())
                .items(serviceResponse.getItems())
                .status(serviceResponse.getStatus())
                .totalAmount(serviceResponse.getTotalAmount())
                .createdAt(serviceResponse.getCreatedAt())
                .build();
    }

    public List<OrderResponse> toResponseList(List<OrderServiceResponse> serviceResponses) {
        return serviceResponses.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}