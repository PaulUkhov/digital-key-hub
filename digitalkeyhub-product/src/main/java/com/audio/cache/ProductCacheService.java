package com.audio.cache;

import com.audio.dto.request.ProductServiceCreateRequest;
import com.audio.dto.request.ProductServiceUpdateRequest;
import com.audio.entity.ProductEntity;
import com.audio.exception.ProductNotFoundException;
import com.audio.mapper.ProductMapper;
import com.audio.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class ProductCacheService {

    private static final String PRODUCT_CACHE = "products";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @CachePut(value = PRODUCT_CACHE, key = "#result.id")
    @Transactional
    public CacheProductDto create(ProductServiceCreateRequest createDto) {
        ProductEntity product = productMapper.toEntity(createDto);
        ProductEntity saved = productRepository.save(product);
        return convertToCacheDto(saved);
    }


    @Cacheable(value = PRODUCT_CACHE, key = "#p0", unless = "#result == null")
    @Transactional(readOnly = true)
    public CacheProductDto getById(UUID id) {
        log.debug("Fetching product from DB with ID: {}", id);
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return convertToCacheDto(product);
    }


    @CachePut(value = PRODUCT_CACHE, key = "#p0")
    @Transactional
    public CacheProductDto update(UUID id, ProductServiceUpdateRequest updateDto) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productMapper.updateEntity(updateDto, product);
        ProductEntity updated = productRepository.save(product);
        return convertToCacheDto(updated);
    }

    @CachePut(value = PRODUCT_CACHE, key = "#p0")
    @Transactional
    public CacheProductDto updateActiveStatus(UUID id, boolean isActive) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setIsActive(isActive);
        ProductEntity updated = productRepository.save(product);
        return convertToCacheDto(updated);
    }

    @CachePut(value = PRODUCT_CACHE, key = "#p0")
    @Transactional
    public CacheProductDto updateStockQuantity(UUID id, int quantityChange) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        int newQuantity = product.getStockQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock quantity");
        }
        product.setStockQuantity(newQuantity);
        ProductEntity updated = productRepository.save(product);
        return convertToCacheDto(updated);
    }

    @CacheEvict(value = PRODUCT_CACHE, key = "#p0")
    @Transactional
    public void delete(UUID id) {
        productRepository.deleteById(id);
    }

    @CacheEvict(value = PRODUCT_CACHE, key = "#p0")
    public void evictFromCache(UUID id) {
    }

    private CacheProductDto convertToCacheDto(ProductEntity entity) {
        return CacheProductDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stockQuantity(entity.getStockQuantity())
                .sku(entity.getSku())
                .photoUrl(entity.getPhotoUrl())
                .digitalContent(entity.getDigitalContent())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}