package com.audio.product.controller;

import com.audio.dto.CommentServiceResponse;
import com.audio.product.dto.request.CreateCommentRequest;
import com.audio.product.dto.response.CommentResponse;
import com.audio.product.dto.response.ProductDetailsResponse;
import com.audio.product.mapper.ProductMapper;
import com.audio.like.service.LikeService;
import com.audio.service.CommentService;
import com.audio.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Product Interactions", description = "API for product interactions like comments and likes")
@RestController
@RequestMapping("/api/v1/products/{productId}")
@RequiredArgsConstructor
public class ProductInteractionController {
    private final ProductService productService;
    private final LikeService likeService;
    private final CommentService commentService;
    private final ProductMapper productMapper;

    @Operation(summary = "Get product details", description = "Get detailed product information including interactions")
    @ApiResponse(responseCode = "200", description = "Product details retrieved",
            content = @Content(schema = @Schema(implementation = ProductDetailsResponse.class)))
    @GetMapping("/details")
    public ResponseEntity<ProductDetailsResponse> getProductDetails(
            @Parameter(description = "ID of the product", required = true)
            @PathVariable("productId") UUID productId,
            @Parameter(description = "ID of the current user (optional for like status)")
            @RequestParam(name = "userId", required = false) UUID userId) {

        var productDetailsResponse =
                productMapper.toDetailsResponse(productService.getProductDetailsById(productId, userId));
        return ResponseEntity.status(HttpStatus.OK).body(productDetailsResponse);
    }

    @Operation(summary = "Add comment", description = "Add a new comment to the product")
    @ApiResponse(responseCode = "201", description = "Comment added successfully",
            content = @Content(schema = @Schema(implementation = CommentResponse.class)))
    @PostMapping("/comments")
    public ResponseEntity<CommentResponse> addComment(
            @Parameter(description = "ID of the product", required = true)
            @PathVariable("productId") UUID productId,
            @Valid @RequestBody CreateCommentRequest request,
            @Parameter(description = "ID of the user adding the comment", required = true)
            @RequestParam("userId") UUID userId) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productMapper.toCommentResponse(commentService.addComment(
                        productId,
                        "PRODUCT",
                        userId,
                        request.getContent()
                )));
    }

    @Operation(summary = "Toggle like", description = "Toggle like status for the product")
    @ApiResponse(responseCode = "200", description = "Like status updated")
    @PostMapping("/likes")
    public ResponseEntity<Void> toggleLike(
            @Parameter(description = "ID of the product", required = true)
            @PathVariable("productId") UUID productId,
            @Parameter(description = "ID of the user toggling the like", required = true)
            @RequestParam("userId") UUID userId) {

        likeService.toggleLike(productId, "PRODUCT", userId);
        return ResponseEntity.ok().build();
    }
}