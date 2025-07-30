package com.audio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponseDto(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String sku,
        String photoUrl,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}