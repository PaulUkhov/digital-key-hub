package com.audio.mapper;

import com.audio.dto.ProductCreateDto;
import com.audio.dto.ProductResponseDto;
import com.audio.dto.ProductUpdateDto;
import com.audio.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductEntity toEntity(ProductCreateDto createDto) {
        if (createDto == null) {
            return null;
        }

        ProductEntity entity = new ProductEntity();
        entity.setName(createDto.name());
        entity.setDescription(createDto.description());
        entity.setPrice(createDto.price());
        entity.setStockQuantity(createDto.stockQuantity());
        entity.setSku(createDto.sku());
        entity.setIsActive(createDto.isActive() != null ? createDto.isActive() : true);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return entity;
    }

    @Override
    public ProductResponseDto toResponseDto(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ProductResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStockQuantity(),
                entity.getSku(),
                entity.getPhotoUrl(),
                entity.getIsActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Override
    public void updateEntity(ProductUpdateDto updateDto, ProductEntity entity) {
        if (updateDto == null || entity == null) {
            return;
        }

        if (updateDto.name() != null) {
            entity.setName(updateDto.name());
        }
        if (updateDto.description() != null) {
            entity.setDescription(updateDto.description());
        }
        if (updateDto.price() != null) {
            entity.setPrice(updateDto.price());
        }
        if (updateDto.stockQuantity() != null) {
            entity.setStockQuantity(updateDto.stockQuantity());
        }
        if (updateDto.sku() != null) {
            entity.setSku(updateDto.sku());
        }
        if (updateDto.isActive() != null) {
            entity.setIsActive(updateDto.isActive());
        }

        entity.setUpdatedAt(LocalDateTime.now());
    }
}
