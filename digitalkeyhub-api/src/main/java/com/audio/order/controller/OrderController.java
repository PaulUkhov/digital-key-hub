package com.audio.order.controller;

import com.audio.order.dto.request.OrderCreateRequest;
import com.audio.order.dto.response.OrderResponse;
import com.audio.order.mapper.OrderMapper;
import com.audio.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody @Valid OrderCreateRequest request,
            @RequestParam("userId") @NotNull UUID userId) {
        OrderResponse response = orderMapper.toResponse(
                orderService.createOrder(
                        orderMapper.toServiceRequest(request),
                        userId
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable("orderId") @NotNull UUID orderId,
            @RequestParam("userId") @NotNull UUID userId) {
        return ResponseEntity.ok(
                orderMapper.toResponse(
                        orderService.getOrder(orderId, userId)
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @RequestParam("userId") @NotNull UUID userId) {
        return ResponseEntity.ok(
                orderMapper.toResponseList(
                        orderService.getUserOrders(userId)
                )
        );
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable("orderId") @NotNull UUID orderId,
            @RequestParam("userId") @NotNull UUID userId) {
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }
}