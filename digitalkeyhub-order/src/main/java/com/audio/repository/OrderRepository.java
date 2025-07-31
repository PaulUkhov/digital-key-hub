package com.audio.repository;

import com.audio.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    Optional<OrderEntity> findByIdAndUserId(UUID orderId, UUID userId);

    List<OrderEntity> findByUserId(UUID userId);
}
