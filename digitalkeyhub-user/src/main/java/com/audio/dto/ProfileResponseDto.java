package com.audio.dto;

import java.util.UUID;

public record ProfileResponseDto(
        UUID id,
        String name,
        String bio,
        String avatarUrl
) {}
