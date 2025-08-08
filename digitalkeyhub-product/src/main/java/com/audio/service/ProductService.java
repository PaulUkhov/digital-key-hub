package com.audio.service;

import com.audio.dto.*;
import com.audio.dto.request.ProductServiceCreateRequest;
import com.audio.dto.request.ProductServiceUpdateRequest;
import com.audio.dto.response.ProductServiceDetailsResponse;
import com.audio.dto.response.ProductServiceResponse;
import com.audio.dto.response.ProductServiceResponsePaid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

public interface ProductService {
    ProductServiceResponse createProduct(ProductServiceCreateRequest createDto);
    ProductServiceResponse getProductById(UUID id);
    Page<ProductServiceResponse> getAllProducts(Pageable pageable);
    Page<ProductServiceResponse> searchProducts(String name, BigDecimal minPrice,
                                                BigDecimal maxPrice, Boolean isActive,
                                                Pageable pageable);
    ProductServiceResponse updateProduct(UUID id, ProductServiceUpdateRequest updateDto);
    ProductServiceResponse updateProductPhoto(UUID id, MultipartFile image) throws IOException;
    ProductServiceResponse deleteProductPhoto(UUID id);
    byte[] getProductPhoto(UUID id) throws IOException;
    void deleteProduct(UUID id);
    ProductServiceResponse setProductActiveStatus(UUID id, boolean isActive);
    ProductServiceResponse updateStockQuantity(UUID id, int quantityChange);

    ProductServiceResponsePaid getProductForPaid(UUID productId);
    ProductServiceDetailsResponse getProductDetailsById(UUID productId, UUID currentUserId);
    CommentServiceResponse addCommentToProduct(UUID productId, UUID userId, String content);
}
