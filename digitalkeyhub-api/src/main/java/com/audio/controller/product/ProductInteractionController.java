package com.audio.controller.product;

import com.audio.dto.*;
import com.audio.like.service.LikeService;
import com.audio.service.CommentService;
import com.audio.service.ProductService;
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

    @GetMapping("/details")
    public ResponseEntity<ProductDetailsDto> getProductDetails(
            @PathVariable("productId") UUID productId,
            @RequestParam(name = "userId",required = false) UUID userId) {

        return ResponseEntity.ok(productService.getProductDetailsById(productId, userId));
    }

    @PostMapping("/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable("productId") UUID productId,
            @RequestBody CreateCommentRequest request,
            @RequestParam("userId") UUID userId) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(productId, "PRODUCT", userId, request.getContent()));
    }

    @PostMapping("/likes")
    public ResponseEntity<Void> toggleLike(
            @PathVariable("productId") UUID productId,
            @RequestParam("userId") UUID userId) {

        likeService.toggleLike(productId, "PRODUCT", userId);
        return ResponseEntity.ok().build();
    }
}