package com.audio.dto;

import java.math.BigDecimal;

public record ProductUpdateDto(
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String sku,
        Boolean isActive) {}