package com.audio.controller.product;

import com.audio.dto.*;
import com.audio.service.ProductService;
import com.audio.service.UserService;
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
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductCreateDto createDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(createDto));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable("id") UUID id) {
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
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        return ResponseEntity.ok(productService.searchProducts(
                name, minPrice, maxPrice, isActive, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable("id") UUID id,
            @RequestBody ProductUpdateDto updateDto) {
        return ResponseEntity.ok(productService.updateProduct(id, updateDto));
    }

    @PutMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDto> updateProductPhoto(
            @PathVariable("id") UUID id,
            @RequestParam(name = "file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(productService.updateProductPhoto(id, file));
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<ProductResponseDto> deleteProductPhoto(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(productService.deleteProductPhoto(id));
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
    public ResponseEntity<ProductResponseDto> setProductActiveStatus(
            @PathVariable("id") UUID id,
            @RequestParam(name = "isActive") boolean isActive) {
        return ResponseEntity.ok(productService.setProductActiveStatus(id, isActive));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponseDto> updateStockQuantity(
            @PathVariable("id") UUID id,
            @RequestParam(name = "quantity") int quantity) {
        return ResponseEntity.ok(productService.updateStockQuantity(id, quantity));
    }

}