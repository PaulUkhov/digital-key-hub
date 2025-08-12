package com.audio.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request object for updating user profile")
public record ProfileUpdateRequest(
        @Schema(description = "User's display name", required = true, example = "John Doe")
        @NotBlank String name,

        @Schema(description = "Short biography or description", example = "Music enthusiast and audio professional")
        @Size(max = 500) String bio
) {}