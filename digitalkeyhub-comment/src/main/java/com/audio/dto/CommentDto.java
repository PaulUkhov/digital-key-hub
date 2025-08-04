package com.audio.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentDto(
        UUID id,
        String content,
        LocalDateTime createdAt,
        UserDto user
) {}
