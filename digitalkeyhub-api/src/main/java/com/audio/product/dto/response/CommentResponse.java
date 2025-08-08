package com.audio.product.dto.response;

import com.audio.dto.response.UserServiceInfoResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentResponse(
        UUID id,
        String content,
        LocalDateTime createdAt,
        UserServiceInfoResponse user
) {}
