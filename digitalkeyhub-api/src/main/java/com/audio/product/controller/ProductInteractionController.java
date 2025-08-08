package com.audio.product.controller;

import com.audio.dto.CommentServiceResponse;
import com.audio.product.dto.request.CreateCommentRequest;
import com.audio.product.dto.response.CommentResponse;
import com.audio.product.dto.response.ProductDetailsResponse;
import com.audio.product.mapper.ProductMapper;
import com.audio.like.service.LikeService;
import com.audio.service.CommentService;
import com.audio.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/{productId}")
@RequiredArgsConstructor
public class ProductInteractionController {
    private final ProductService productService;
    private final LikeService likeService;
    private final CommentService commentService;
    private final ProductMapper productMapper;

    @GetMapping("/details")
    public ResponseEntity<ProductDetailsResponse> getProductDetails(
            @PathVariable("productId") UUID productId,
            @RequestParam(name = "userId", required = false) UUID userId) {

        var productDetailsResponse =
                productMapper.toDetailsResponse(productService.getProductDetailsById(productId, userId));
        return ResponseEntity.status(HttpStatus.OK).body(productDetailsResponse);
    }

    @PostMapping("/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable("productId") UUID productId,
            @Valid @RequestBody CreateCommentRequest request,
            @RequestParam("userId") UUID userId) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productMapper.toCommentResponse(commentService.addComment(
                        productId,
                        "PRODUCT",
                        userId,
                        request.getContent()
                )));
    }

    @PostMapping("/likes")
    public ResponseEntity<Void> toggleLike(
            @PathVariable("productId") UUID productId,
            @RequestParam("userId") UUID userId) {

        likeService.toggleLike(productId, "PRODUCT", userId);
        return ResponseEntity.ok().build();
    }
}