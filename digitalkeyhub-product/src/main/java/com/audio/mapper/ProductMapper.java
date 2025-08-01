package com.audio.mapper;

import com.audio.dto.ProductCreateDto;
import com.audio.dto.ProductResponseDto;
import com.audio.dto.ProductUpdateDto;
import com.audio.entity.ProductEntity;

public interface ProductMapper {
    ProductEntity toEntity(ProductCreateDto createDto);

    ProductResponseDto toResponseDto(ProductEntity entity);

    void updateEntity(ProductUpdateDto updateDto, ProductEntity entity);
}
