package com.audio.mapper;

import com.audio.dto.request.ProductServiceCreateRequest;
import com.audio.dto.response.ProductServiceResponse;
import com.audio.dto.request.ProductServiceUpdateRequest;
import com.audio.entity.ProductEntity;

public interface ProductMapper {
    ProductEntity toEntity(ProductServiceCreateRequest createDto);

    ProductServiceResponse toResponseDto(ProductEntity entity);

    void updateEntity(ProductServiceUpdateRequest updateDto, ProductEntity entity);
}
