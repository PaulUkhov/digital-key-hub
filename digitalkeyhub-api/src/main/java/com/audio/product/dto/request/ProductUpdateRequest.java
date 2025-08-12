package com.audio.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Request object for updating a product")
public record ProductUpdateRequest(
        @Schema(description = "Name of the product (2-100 characters)", required = true, example = "Updated Product Name")
        @NotBlank(message = "Product name cannot be blank")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @Schema(description = "Description of the product (500 characters max)", example = "Updated product description")
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @Schema(description = "Price of the product (0.01-999999.99)", required = true, example = "49.99")
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
        @DecimalMax(value = "999999.99", message = "Price cannot exceed 999999.99")
        BigDecimal price,

        @Schema(description = "Stock quantity (0-9999)", required = true, example = "100")
        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock cannot be negative")
        @Max(value = 9999, message = "Stock cannot exceed 9999")
        Integer stockQuantity,

        @Schema(description = "SKU identifier (3-50 characters, uppercase alphanumeric)", required = true, example = "UPD-12345")
        @NotBlank(message = "SKU cannot be blank")
        @Pattern(regexp = "^[A-Z0-9-]+$", message = "SKU must contain only uppercase letters, numbers and hyphens")
        @Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
        String sku,

        @Schema(description = "Active status of the product", required = true, example = "true")
        @NotNull(message = "Active status is required")
        Boolean isActive
) {}