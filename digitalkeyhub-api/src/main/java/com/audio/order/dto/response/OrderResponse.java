package com.audio.order.dto.response;

import com.audio.dto.response.OrderItemServiceResponse;
import com.audio.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Schema(description = "Response object representing an order")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    @Schema(description = "Unique identifier of the order",
            example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "ID of the user who placed the order",
            example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @ArraySchema(
            arraySchema = @Schema(description = "List of order items"),
            schema = @Schema(description = "Order item containing product details"),
            minItems = 1
    )
    private List<OrderItemServiceResponse> items = new ArrayList<>();

    @Schema(description = "Current status of the order. Possible values: " +
            "CREATED, PROCESSING, PAID, SHIPPED, DELIVERED, CANCELLED, REFUNDED, FAILED, COMPLETED")
    private OrderStatus status;

    @Schema(description = "Total amount of the order",
            example = "99.99")
    private BigDecimal totalAmount;

    @Schema(description = "Date and time when the order was created",
            example = "2023-05-15T10:30:00")
    private LocalDateTime createdAt = LocalDateTime.now();
}