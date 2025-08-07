package com.audio.dto.response;

import java.util.UUID;

public record UserServiceResponse(
        UUID id,
        String email,
        ProfileServiceResponse profile
) {}
