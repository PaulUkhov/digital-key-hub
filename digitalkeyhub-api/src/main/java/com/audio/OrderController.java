package com.audio.controller;

import com.audio.dto.OrderCreateDto;
import com.audio.dto.OrderDto;
import com.audio.service.OrderService;
import jakarta.validation.Valid;
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

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @RequestBody @Valid OrderCreateDto createDto,
            @RequestParam("userId") UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(createDto, userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(
            @PathVariable("orderId") UUID orderId,
            @RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(orderService.getOrder(orderId, userId));
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getUserOrders(
            @RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable("orderId") UUID orderId,
            @RequestParam("userId") UUID userId) {
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }
}