package com.audio.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record ProductServiceResponsePaid(
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
