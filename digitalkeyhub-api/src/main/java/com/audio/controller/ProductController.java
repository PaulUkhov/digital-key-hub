package com.audio.controller;

import com.audio.dto.*;
import com.audio.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductCreateDto createDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(createDto));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/all")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ProductResponseDto>> searchProducts(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean isActive) {

        return ResponseEntity.ok(productService.searchProducts(
                name, minPrice, maxPrice, isActive, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable UUID id,
            @RequestBody ProductUpdateDto updateDto) {
        return ResponseEntity.ok(productService.updateProduct(id, updateDto));
    }

    @PutMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDto> updateProductPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(productService.updateProductPhoto(id, file));
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<ProductResponseDto> deleteProductPhoto(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.deleteProductPhoto(id));
    }

    @GetMapping(value = "/{id}/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getProductPhoto(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductPhoto(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponseDto> setProductActiveStatus(
            @PathVariable UUID id,
            @RequestParam boolean isActive) {
        return ResponseEntity.ok(productService.setProductActiveStatus(id, isActive));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponseDto> updateStockQuantity(
            @PathVariable UUID id,
            @RequestParam int quantity) {
        return ResponseEntity.ok(productService.updateStockQuantity(id, quantity));
    }
}