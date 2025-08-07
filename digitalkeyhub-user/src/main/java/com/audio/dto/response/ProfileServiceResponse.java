package com.audio.dto.response;

import java.util.UUID;

public record ProfileServiceResponse(
        UUID id,
        String name,
        String bio,
        String avatarUrl
) {}
