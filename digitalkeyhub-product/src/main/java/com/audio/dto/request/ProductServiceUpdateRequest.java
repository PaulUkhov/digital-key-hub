package com.audio.dto.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductServiceUpdateRequest(
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String sku,
        Boolean isActive,
        String photoUrl) {
    public ProductServiceUpdateRequest(String name,
                                       String description,
                                       BigDecimal price,
                                       Integer stockQuantity,
                                       String sku,
                                       Boolean isActive) {
        this(name, description, price, stockQuantity, sku, isActive, "");
    }
}