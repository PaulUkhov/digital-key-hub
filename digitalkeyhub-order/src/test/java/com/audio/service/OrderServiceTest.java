package com.audio.service;

import com.audio.dto.*;
import com.audio.entity.*;
import com.audio.enums.OrderStatus;
import com.audio.exception.*;
import com.audio.mapper.OrderMapper;
import com.audio.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private final UUID testUserId = UUID.randomUUID();
    private final UUID testProductId = UUID.randomUUID();
    private final UUID testOrderId = UUID.randomUUID();

    @Test
    @Transactional
    void createOrder_Success() {
        // Arrange
        OrderCreateDto createDto = new OrderCreateDto(
                List.of(new OrderItemDto(testProductId, 2))
        );

        ProductResponseDto product = new ProductResponseDto(
                testProductId, "Test Product", "Desc",
                BigDecimal.valueOf(100),
                10,
                "SKU123", null, true, null, null
        );

        OrderItemEntity itemEntity = OrderItemEntity.builder()
                .productId(testProductId)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(100))
                .subtotal(BigDecimal.valueOf(200))
                .build();

        OrderEntity savedOrder = OrderEntity.builder()
                .id(testOrderId)
                .userId(testUserId)
                .status(OrderStatus.CREATED)
                .totalAmount(BigDecimal.valueOf(200))
                .items(List.of(itemEntity))
                .build();


        when(productService.getProductById(testProductId)).thenReturn(product);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);
        when(orderMapper.toOrderDto(savedOrder)).thenReturn(new OrderDto(
                testOrderId, testUserId, List.of(), OrderStatus.CREATED, BigDecimal.valueOf(200), null
        ));

        // Act
        OrderDto result = orderService.createOrder(createDto, testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(testOrderId, result.getId());

    }

    @Test
    void createOrder_EmptyItems_ShouldThrowException() {
        // Arrange
        OrderCreateDto createDto = new OrderCreateDto(List.of());

        // Act & Assert
        assertThrows(OrderOperationException.class, () ->
                orderService.createOrder(createDto, testUserId));
    }


    @Test
    void getOrder_Success() {
        // Arrange
        OrderEntity order = OrderEntity.builder()
                .id(testOrderId)
                .userId(testUserId)
                .build();

        when(orderRepository.findByIdAndUserId(testOrderId, testUserId))
                .thenReturn(Optional.of(order));
        when(orderMapper.toOrderDto(order)).thenReturn(new OrderDto(
                testOrderId, testUserId, List.of(), null, null, null
        ));

        // Act
        OrderDto result = orderService.getOrder(testOrderId, testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(testOrderId, result.getId());
    }

    @Test
    void getOrder_NotFound_ShouldThrowException() {
        // Arrange
        when(orderRepository.findByIdAndUserId(testOrderId, testUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () ->
                orderService.getOrder(testOrderId, testUserId));
    }

    @Test
    void getUserOrders_Success() {
        // Arrange
        OrderEntity order = OrderEntity.builder()
                .id(testOrderId)
                .userId(testUserId)
                .build();

        when(orderRepository.findByUserId(testUserId))
                .thenReturn(List.of(order));
        when(orderMapper.toOrderDto(order)).thenReturn(new OrderDto(
                testOrderId, testUserId, List.of(), null, null, null
        ));

        // Act
        List<OrderDto> result = orderService.getUserOrders(testUserId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(testOrderId, result.get(0).getId());
    }

    @Test
    void cancelOrder_Success() {
        // Arrange
        OrderItemEntity item = OrderItemEntity.builder()
                .productId(testProductId)
                .quantity(2)
                .build();

        OrderEntity order = OrderEntity.builder()
                .id(testOrderId)
                .userId(testUserId)
                .status(OrderStatus.CREATED)
                .items(List.of(item))
                .build();

        when(orderRepository.findByIdAndUserId(testOrderId, testUserId))
                .thenReturn(Optional.of(order));

        // Act
        orderService.cancelOrder(testOrderId, testUserId);

        // Assert
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(productService).updateStockQuantity(testProductId, 2);
    }

    @Test
    void cancelOrder_NotFound_ShouldThrowException() {
        // Arrange
        when(orderRepository.findByIdAndUserId(testOrderId, testUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () ->
                orderService.cancelOrder(testOrderId, testUserId));
    }

    @Test
    void cancelOrder_InvalidStatus_ShouldThrowException() {
        // Arrange
        OrderEntity order = OrderEntity.builder()
                .id(testOrderId)
                .userId(testUserId)
                .status(OrderStatus.PAID)
                .build();

        when(orderRepository.findByIdAndUserId(testOrderId, testUserId))
                .thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(OrderOperationException.class, () ->
                orderService.cancelOrder(testOrderId, testUserId));
    }

    @Test
    void calculateTotalAmount_ShouldReturnCorrectSum() {
        // Arrange
        OrderItemEntity item1 = OrderItemEntity.builder()
                .subtotal(BigDecimal.valueOf(100))
                .build();

        OrderItemEntity item2 = OrderItemEntity.builder()
                .subtotal(BigDecimal.valueOf(50))
                .build();

        // Act
        BigDecimal result = orderService.calculateTotalAmount(List.of(item1, item2));

        // Assert
        assertEquals(BigDecimal.valueOf(150), result);
    }
}