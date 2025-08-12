package com.audio.product.controller;

import com.audio.product.dto.request.ProductCreateRequest;
import com.audio.product.dto.request.ProductUpdateRequest;
import com.audio.product.dto.response.ProductResponse;
import com.audio.product.mapper.ProductMapper;
import com.audio.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Product Management", description = "API for product operations")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @Operation(
            summary = "Create product",
            description = "Create a new product in the system",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product creation details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductCreateRequest.class))
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Product created successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
    )
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {
        var serviceResponse = productService.createProduct(
                productMapper.toServiceRequest(request)
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productMapper.toResponse(serviceResponse));
    }

    @Operation(
            summary = "Update product",
            description = "Update existing product details",
            parameters = {
                    @Parameter(name = "id", description = "ID of the product to update", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product update details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductUpdateRequest.class))
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product updated successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
    )
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

    @Operation(
            summary = "Get product details",
            description = "Retrieve product details by ID",
            parameters = {
                    @Parameter(name = "id", description = "ID of the product to retrieve", required = true)
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product details retrieved",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
    )
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable("id") @NotNull UUID id) {
        var resp = productMapper.toResponse(productService.getProductById(id));
        return ResponseEntity.ok(resp);
    }

    @Operation(
            summary = "List all products",
            description = "Get paginated list of all products",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "20"),
                    @Parameter(name = "sort", description = "Sorting criteria (field,direction)", example = "name,asc")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Paginated list of products",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping("/all")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
                productService.getAllProducts(pageable)
                        .map(productMapper::toResponse)
        );
    }

    @Operation(
            summary = "Search products",
            description = "Search products with filters",
            parameters = {
                    @Parameter(name = "name", description = "Product name filter (partial match)"),
                    @Parameter(name = "minPrice", description = "Minimum price filter"),
                    @Parameter(name = "maxPrice", description = "Maximum price filter"),
                    @Parameter(name = "isActive", description = "Active status filter"),
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "20"),
                    @Parameter(name = "sort", description = "Sorting criteria (field,direction)", example = "price,desc")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Paginated search results",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
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

    @Operation(
            summary = "Update product photo",
            description = "Upload or update product photo",
            parameters = {
                    @Parameter(name = "id", description = "ID of the product", required = true),
                    @Parameter(name = "file", description = "Image file to upload (JPEG/PNG)", required = true)
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product photo updated",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
    )
    @PutMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateProductPhoto(
            @PathVariable("id") UUID id,
            @RequestParam(name = "file") MultipartFile file) throws IOException {
        var serviceResponse = productService.updateProductPhoto(id, file);
        return ResponseEntity.ok(productMapper.toResponse(serviceResponse));
    }

    @Operation(
            summary = "Delete product photo",
            description = "Remove product photo",
            parameters = {
                    @Parameter(name = "id", description = "ID of the product", required = true)
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product photo deleted",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
    )
    @DeleteMapping("/{id}/photo")
    public ResponseEntity<ProductResponse> deleteProductPhoto(
            @PathVariable("id") UUID id) {
        var serviceResponse = productService.deleteProductPhoto(id);
        return ResponseEntity.ok(productMapper.toResponse(serviceResponse));
    }

    @Operation(
            summary = "Get product photo",
            description = "Retrieve product photo as binary data",
            parameters = {
                    @Parameter(name = "id", description = "ID of the product", required = true)
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product photo retrieved",
            content = @Content(mediaType = "image/jpeg")
    )
    @GetMapping(value = "/{id}/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getProductPhoto(
            @PathVariable("id") UUID id) throws IOException {
        return ResponseEntity.ok(productService.getProductPhoto(id));
    }

    @Operation(
            summary = "Delete product",
            description = "Permanently delete a product",
            parameters = {
                    @Parameter(name = "id", description = "ID of the product to delete", required = true)
            }
    )
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable("id") UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update product status",
            description = "Activate or deactivate a product",
            parameters = {
                    @Parameter(name = "id", description = "ID of the product", required = true),
                    @Parameter(name = "isActive", description = "New active status (true/false)", required = true)
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product status updated",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponse> setProductActiveStatus(
            @PathVariable("id") UUID id,
            @RequestParam(name = "isActive") boolean isActive) {
        var serviceResponse = productService.setProductActiveStatus(id, isActive);
        return ResponseEntity.ok(productMapper.toResponse(serviceResponse));
    }

    @Operation(
            summary = "Update stock quantity",
            description = "Update product stock quantity",
            parameters = {
                    @Parameter(name = "id", description = "ID of the product", required = true),
                    @Parameter(name = "quantity", description = "New stock quantity (0-9999)", required = true)
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Stock quantity updated",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
    )
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> updateStockQuantity(
            @PathVariable("id") UUID id,
            @RequestParam(name = "quantity") int quantity) {
        var serviceResponse = productService.updateStockQuantity(id, quantity);
        return ResponseEntity.ok(productMapper.toResponse(serviceResponse));
    }
}