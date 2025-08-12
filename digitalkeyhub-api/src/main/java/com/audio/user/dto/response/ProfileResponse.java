package com.audio.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response object representing user profile")
public record ProfileResponse(
        @Schema(description = "Unique identifier of the profile", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "User's display name", example = "John Doe")
        String name,

        @Schema(description = "Short biography or description", example = "Music enthusiast and audio professional")
        String bio,

        @Schema(description = "URL to user's avatar image", example = "https://example.com/avatars/user123.jpg")
        String avatarUrl
) {}