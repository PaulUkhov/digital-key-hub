package com.audio.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response object representing user details")
public record UserResponse(
        @Schema(description = "Unique identifier of the user", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "User's email address", example = "user@example.com")
        String email,

        @Schema(description = "User's profile information")
        ProfileResponse profile
) {}