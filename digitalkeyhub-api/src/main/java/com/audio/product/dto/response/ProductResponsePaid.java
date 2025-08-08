package com.audio.product.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record ProductResponsePaid(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String sku,
        String photoUrl,
        Boolean isActive,
        String digitalContent,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
