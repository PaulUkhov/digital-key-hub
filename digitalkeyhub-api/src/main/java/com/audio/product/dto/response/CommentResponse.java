package com.audio.product.dto.response;

import com.audio.dto.response.UserServiceInfoResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response object representing a product comment")
@Builder
public record CommentResponse(
        @Schema(description = "Unique identifier of the comment", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Content of the comment", example = "Great product, works perfectly!")
        String content,

        @Schema(description = "Timestamp when comment was created", example = "2023-05-15T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "User who created the comment")
        UserServiceInfoResponse user) {}