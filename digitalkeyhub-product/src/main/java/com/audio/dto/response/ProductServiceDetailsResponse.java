package com.audio.dto.response;

import com.audio.dto.CommentServiceResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ProductServiceDetailsResponse(
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
