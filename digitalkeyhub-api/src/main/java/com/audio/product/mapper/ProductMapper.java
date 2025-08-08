package com.audio.product.mapper;

import com.audio.dto.CommentServiceResponse;
import com.audio.dto.request.ProductServiceCreateRequest;
import com.audio.dto.request.ProductServiceUpdateRequest;
import com.audio.dto.response.ProductServiceDetailsResponse;
import com.audio.dto.response.ProductServiceResponse;
import com.audio.dto.response.ProductServiceResponsePaid;
import com.audio.product.dto.request.ProductCreateRequest;
import com.audio.product.dto.request.ProductUpdateRequest;
import com.audio.product.dto.response.CommentResponse;
import com.audio.product.dto.response.ProductDetailsResponse;
import com.audio.product.dto.response.ProductResponse;
import com.audio.product.dto.response.ProductResponsePaid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    public CommentResponse toCommentResponse(CommentServiceResponse commentServiceResponse) {
        return new CommentResponse(commentServiceResponse.id(),
                commentServiceResponse.content(),
                commentServiceResponse.createdAt(),
                commentServiceResponse.user());
    }

    public ProductServiceCreateRequest toServiceRequest(ProductCreateRequest request) {
        return new ProductServiceCreateRequest(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                request.sku(),
                request.isActive()
        );
    }

    public ProductServiceUpdateRequest toServiceRequest(ProductUpdateRequest request) {
        return new ProductServiceUpdateRequest(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                request.sku(),
                request.isActive()
        );
    }

    public ProductResponse toResponse(ProductServiceResponse serviceResponse) {
        return new ProductResponse(
                serviceResponse.id(),
                serviceResponse.name(),
                serviceResponse.description(),
                serviceResponse.price(),
                serviceResponse.stockQuantity(),
                serviceResponse.sku(),
                serviceResponse.photoUrl(),
                serviceResponse.isActive(),
                serviceResponse.createdAt(),
                serviceResponse.updatedAt()
        );
    }

    public ProductDetailsResponse toDetailsResponse(ProductServiceDetailsResponse serviceResponse) {
        return new ProductDetailsResponse(
                serviceResponse.id(),
                serviceResponse.name(),
                serviceResponse.description(),
                serviceResponse.price(),
                serviceResponse.stockQuantity(),
                serviceResponse.sku(),
                serviceResponse.photoUrl(),
                serviceResponse.isActive(),
                serviceResponse.createdAt(),
                serviceResponse.updatedAt(),
                serviceResponse.likesCount(),
                serviceResponse.likedByCurrentUser(),
                serviceResponse.recentComments()
        );
    }

    public ProductResponsePaid toPaidResponse(ProductServiceResponsePaid serviceResponse) {
        return new ProductResponsePaid(
                serviceResponse.id(),
                serviceResponse.name(),
                serviceResponse.description(),
                serviceResponse.price(),
                serviceResponse.stockQuantity(),
                serviceResponse.sku(),
                serviceResponse.photoUrl(),
                serviceResponse.isActive(),
                serviceResponse.digitalContent(),
                serviceResponse.createdAt(),
                serviceResponse.updatedAt()
        );
    }
}