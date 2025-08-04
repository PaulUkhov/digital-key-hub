package com.audio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProductDetailsDto(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String sku,
        String photoUrl,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        long likesCount,
        boolean likedByCurrentUser,
        List<CommentDto> recentComments
) {}
