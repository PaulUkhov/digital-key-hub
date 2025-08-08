package com.audio.user.dto.response;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        ProfileResponse profile
) {}
