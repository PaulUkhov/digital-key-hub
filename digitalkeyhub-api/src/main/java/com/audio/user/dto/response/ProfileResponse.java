package com.audio.user.dto.response;

import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String name,
        String bio,
        String avatarUrl
) {}
