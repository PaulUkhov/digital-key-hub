package com.audio.product.dto.response;

import com.audio.dto.CommentServiceResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProductDetailsResponse(
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
        List<CommentServiceResponse> recentComments
) {}
