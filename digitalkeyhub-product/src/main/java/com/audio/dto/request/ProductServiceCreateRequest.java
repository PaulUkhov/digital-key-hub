package com.audio.dto.request;

import java.math.BigDecimal;

public record ProductServiceCreateRequest(
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String sku,
        Boolean isActive) {}
