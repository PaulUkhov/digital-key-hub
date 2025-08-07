package com.audio.dto.response;

import java.util.UUID;

public record UserServiceInfoResponse(
        UUID id,
        String email
) {}