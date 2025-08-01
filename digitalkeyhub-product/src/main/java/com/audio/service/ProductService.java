package com.audio.service;

import com.audio.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

public interface ProductService {
    ProductResponseDto createProduct(ProductCreateDto createDto);
    ProductResponseDto getProductById(UUID id);
    Page<ProductResponseDto> getAllProducts(Pageable pageable);
    Page<ProductResponseDto> searchProducts(String name, BigDecimal minPrice,
                                            BigDecimal maxPrice, Boolean isActive,
                                            Pageable pageable);
    ProductResponseDto updateProduct(UUID id, ProductUpdateDto updateDto);
    ProductResponseDto updateProductPhoto(UUID id, MultipartFile image) throws IOException;
    ProductResponseDto deleteProductPhoto(UUID id);
    byte[] getProductPhoto(UUID id) throws IOException;
    void deleteProduct(UUID id);
    ProductResponseDto setProductActiveStatus(UUID id, boolean isActive);
    ProductResponseDto updateStockQuantity(UUID id, int quantityChange);
}
