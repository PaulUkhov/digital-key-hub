package com.audio.product.controller;

import com.audio.product.dto.request.ProductCreateRequest;
import com.audio.product.dto.request.ProductUpdateRequest;
import com.audio.product.dto.response.ProductResponse;
import com.audio.product.mapper.ProductMapper;
import com.audio.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {
        var serviceResponse = productService.createProduct(
                productMapper.toServiceRequest(request)
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productMapper.toResponse(serviceResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable("id") UUID id,
            @Valid @RequestBody ProductUpdateRequest request) {
        var serviceResponse = productService.updateProduct(
                id,
                productMapper.toServiceRequest(request)
        );
        return ResponseEntity.ok(productMapper.toResponse(serviceResponse));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") @NotNull UUID id) {
        var resp = productMapper.toResponse(productService.getProductById(id));
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/all")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
                productService.getAllProducts(pageable)
                        .map(productMapper::toResponse)
        );
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        return ResponseEntity.ok(
                productService.searchProducts(name, minPrice, maxPrice, isActive, pageable)
                        .map(productMapper::toResponse)
        );
    }

    @PutMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateProductPhoto(
            @PathVariable("id") UUID id,
            @RequestParam(name = "file") MultipartFile file) throws IOException {
        var serviceResponse = productService.updateProductPhoto(id, file);
        return ResponseEntity.ok(productMapper.toResponse(serviceResponse));
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<ProductResponse> deleteProductPhoto(@PathVariable("id") UUID id) {
        var serviceResponse = productService.deleteProductPhoto(id);
        return ResponseEntity.ok(productMapper.toResponse(serviceResponse));
    }

    @GetMapping(value = "/{id}/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getProductPhoto(@PathVariable("id") UUID id) throws IOException {
        return ResponseEntity.ok(productService.getProductPhoto(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponse> setProductActiveStatus(
            @PathVariable("id") UUID id,
            @RequestParam(name = "isActive") boolean isActive) {
        var serviceResponse = productService.setProductActiveStatus(id, isActive);
        return ResponseEntity.ok(productMapper.toResponse(serviceResponse));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> updateStockQuantity(
            @PathVariable("id") UUID id,
            @RequestParam(name = "quantity") int quantity) {
        var serviceResponse = productService.updateStockQuantity(id, quantity);
        return ResponseEntity.ok(productMapper.toResponse(serviceResponse));
    }
}