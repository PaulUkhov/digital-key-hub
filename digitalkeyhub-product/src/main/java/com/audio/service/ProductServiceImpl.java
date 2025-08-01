package com.audio.service;

import com.audio.dto.*;
import com.audio.entity.ProductEntity;
import com.audio.exception.ProductNotFoundException;
import com.audio.mapper.ProductMapper;
import com.audio.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final FileStorageService storageService;

    @Transactional
    public ProductResponseDto createProduct(ProductCreateDto createDto) {
        ProductEntity product = productMapper.toEntity(createDto);
        ProductEntity savedProduct = productRepository.save(product);
        return productMapper.toResponseDto(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(UUID id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toResponseDto(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> searchProducts(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean isActive,
            Pageable pageable) {
        return productRepository.findByFilters(
                name,
                minPrice,
                maxPrice,
                isActive,
                pageable
        ).map(productMapper::toResponseDto);
    }

    @Transactional
    public ProductResponseDto updateProduct(UUID id, ProductUpdateDto updateDto) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productMapper.updateEntity(updateDto, product);
        ProductEntity updatedProduct = productRepository.save(product);
        return productMapper.toResponseDto(updatedProduct);
    }

    @Transactional
    public ProductResponseDto updateProductPhoto(UUID id, MultipartFile image) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        try {
            if (product.getPhotoUrl() != null) {
                storageService.deleteFile(product.getPhotoUrl());
            }

            String extension = getFileExtension(image.getOriginalFilename());
            String newFileName = "product_" + id + "_" + System.currentTimeMillis() + "." + extension;
            String filePath = storageService.uploadFile(image, newFileName);

            product.setPhotoUrl(filePath);
            ProductEntity updatedProduct = productRepository.save(product);
            return productMapper.toResponseDto(updatedProduct);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update product photo", e);
        }
    }

    @Transactional
    public ProductResponseDto deleteProductPhoto(UUID id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (product.getPhotoUrl() != null) {
            try {
                storageService.deleteFile(product.getPhotoUrl());
                product.setPhotoUrl(null);
                ProductEntity updatedProduct = productRepository.save(product);
                return productMapper.toResponseDto(updatedProduct);
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete product photo", e);
            }
        }
        return productMapper.toResponseDto(product);
    }

    @Transactional(readOnly = true)
    public byte[] getProductPhoto(UUID id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (product.getPhotoUrl() == null) {
            throw new RuntimeException("Product photo not found");
        }

        try (InputStream inputStream = storageService.getFile(product.getPhotoUrl())) {
            return inputStream.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get product photo", e);
        }
    }

    @Transactional
    public void deleteProduct(UUID id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (product.getPhotoUrl() != null) {
            try {
                storageService.deleteFile(product.getPhotoUrl());
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete product photo", e);
            }
        }

        productRepository.delete(product);
    }

    @Transactional
    public ProductResponseDto setProductActiveStatus(UUID id, boolean isActive) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setIsActive(isActive);
        ProductEntity updatedProduct = productRepository.save(product);
        return productMapper.toResponseDto(updatedProduct);
    }

    @Transactional
    public ProductResponseDto updateStockQuantity(UUID id, int quantityChange) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        int newQuantity = product.getStockQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock quantity");
        }

        product.setStockQuantity(newQuantity);
        ProductEntity updatedProduct = productRepository.save(product);
        return productMapper.toResponseDto(updatedProduct);
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "jpg";
        int lastDot = filename.lastIndexOf('.');
        return lastDot == -1 ? "jpg" : filename.substring(lastDot + 1);
    }
}