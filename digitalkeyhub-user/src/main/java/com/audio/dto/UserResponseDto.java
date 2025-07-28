package com.audio.dto;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String email,
        ProfileResponseDto profile
) {}
