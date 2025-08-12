package com.audio.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response object representing a product")
public record ProductResponse(
        @Schema(description = "Unique identifier of the product", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Name of the product", example = "Premium Audio Plugin")
        String name,

        @Schema(description = "Detailed description of the product", example = "Professional grade audio plugin for music production")
        String description,

        @Schema(description = "Price of the product", example = "99.99")
        BigDecimal price,

        @Schema(description = "Current stock quantity", example = "50")
        Integer stockQuantity,

        @Schema(description = "Stock keeping unit identifier", example = "AUD-12345")
        String sku,

        @Schema(description = "URL of the product photo", example = "https://example.com/products/123.jpg")
        String photoUrl,

        @Schema(description = "Active status of the product", example = "true")
        Boolean isActive,

        @Schema(description = "Timestamp when product was created", example = "2023-05-15T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when product was last updated", example = "2023-05-16T11:45:00")
        LocalDateTime updatedAt) {}