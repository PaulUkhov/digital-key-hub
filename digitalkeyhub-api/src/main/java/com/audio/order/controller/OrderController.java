package com.audio.order.controller;

import com.audio.order.dto.request.OrderCreateRequest;
import com.audio.order.dto.response.OrderResponse;
import com.audio.order.mapper.OrderMapper;
import com.audio.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Order Management", description = "API for order operations")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Operation(
            summary = "Create new order",
            description = "Creates a new order for the specified user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User or product not found")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody @Valid OrderCreateRequest request,
            @Parameter(description = "ID of the user placing the order", required = true)
            @RequestParam("userId") @NotNull UUID userId) {
        OrderResponse response = orderMapper.toResponse(
                orderService.createOrder(
                        orderMapper.toServiceRequest(request),
                        userId
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get order details",
            description = "Retrieves details of a specific order"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order details retrieved",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "ID of the order to retrieve", required = true)
            @PathVariable("orderId") @NotNull UUID orderId,
            @Parameter(description = "ID of the user who owns the order", required = true)
            @RequestParam("userId") @NotNull UUID userId) {
        return ResponseEntity.ok(
                orderMapper.toResponse(
                        orderService.getOrder(orderId, userId)
                )
        );
    }

    @Operation(
            summary = "Get user orders",
            description = "Retrieves all orders for a specific user"
    )
    @ApiResponse(responseCode = "200", description = "List of user orders",
            content = @Content(schema = @Schema(implementation = OrderResponse[].class)))
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @Parameter(description = "ID of the user", required = true)
            @RequestParam("userId") @NotNull UUID userId) {
        return ResponseEntity.ok(
                orderMapper.toResponseList(
                        orderService.getUserOrders(userId)
                )
        );
    }

    @Operation(
            summary = "Cancel order",
            description = "Cancels an existing order"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled")
    })
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "ID of the order to cancel", required = true)
            @PathVariable("orderId") @NotNull UUID orderId,
            @Parameter(description = "ID of the user who owns the order", required = true)
            @RequestParam("userId") @NotNull UUID userId) {
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }
}