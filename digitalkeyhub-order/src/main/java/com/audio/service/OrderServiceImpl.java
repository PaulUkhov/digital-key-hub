package com.audio.service;

import com.audio.dto.*;
import com.audio.entity.*;
import com.audio.enums.OrderStatus;
import com.audio.exception.*;
import com.audio.mapper.OrderMapper;
import com.audio.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final OrderMapper orderMapper;


    @Transactional
    public OrderDto createOrder(OrderCreateDto orderCreateDto, UUID userId) {
        List<OrderItemEntity> orderItems = validateAndCreateItems(orderCreateDto.getItems());

        BigDecimal totalAmount = calculateTotalAmount(orderItems);

        OrderEntity order = OrderEntity.builder()
                .userId(userId)
                .items(orderItems)
                .status(OrderStatus.CREATED)
                .totalAmount(totalAmount)
                .build();

        orderItems.forEach(item -> item.setOrder(order));

        OrderEntity savedOrder = orderRepository.save(order);

        updateProductStocks(orderItems, false);

        return orderMapper.toOrderDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrder(UUID orderId, UUID userId) {
        OrderEntity order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderMapper.toOrderDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrders(UUID userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toOrderDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelOrder(UUID orderId, UUID userId) {
        OrderEntity order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new OrderOperationException("Cannot cancel order in status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        updateProductStocks(order.getItems(), true);
    }

    public List<OrderItemEntity> validateAndCreateItems(List<OrderItemDto> items) {
        if (items == null || items.isEmpty()) {
            throw new OrderOperationException("Order must contain at least one item");
        }

        return items.stream()
                .map(this::createOrderItem)
                .collect(Collectors.toList());
    }

    public OrderItemEntity createOrderItem(OrderItemDto itemDto) {

        ProductResponseDto product = productService.getProductById(itemDto.getProductId());

        if (product.stockQuantity() < itemDto.getQuantity()) {
            throw new InsufficientStockException(
                    "Not enough stock for product: " + product.id() +
                            ". Available: " + product.stockQuantity() +
                            ", requested: " + itemDto.getQuantity());
        }

        BigDecimal subtotal = product.price().multiply(BigDecimal.valueOf(itemDto.getQuantity()));

        return OrderItemEntity.builder()
                .productId(product.id())
                .quantity(itemDto.getQuantity())
                .unitPrice(product.price())
                .subtotal(subtotal)
                .build();
    }

    public BigDecimal calculateTotalAmount(List<OrderItemEntity> items) {
        return items.stream()
                .map(OrderItemEntity::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void updateProductStocks(List<OrderItemEntity> items, boolean isRestock) {
        items.forEach(item -> {
            int quantityChange = isRestock ? item.getQuantity() : -item.getQuantity();
            productService.updateStockQuantity(item.getProductId(), quantityChange);
        });
    }

    @Transactional
    public OrderDto completeOrder(UUID orderId) {
        OrderEntity order = getOrderEntity(orderId);
        order.setStatus(OrderStatus.PAID);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Transactional
    public void failOrder(UUID orderId, String reason) {
        OrderEntity order = getOrderEntity(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        updateProductStocks(order.getItems(), true);
    }

    public OrderEntity getOrderEntity(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public OrderEntity getOrderEntity(UUID orderId, UUID userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}