package com.audio.dto;

import com.audio.dto.response.UserServiceInfoResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentServiceResponse(
        UUID id,
        String content,
        LocalDateTime createdAt,
        UserServiceInfoResponse user
) {}
