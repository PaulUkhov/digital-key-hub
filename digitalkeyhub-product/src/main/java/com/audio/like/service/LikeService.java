package com.audio.like.service;

import com.audio.like.entity.LikeEntity;
import com.audio.exception.ProductNotFoundException;
import com.audio.exception.UserNotFoundException;
import com.audio.like.repository.LikeRepository;
import com.audio.repository.ProductRepository;
import com.audio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public long getLikesCount(UUID entityId, String entityType) {
        return likeRepository.countByEntityIdAndEntityType(entityId, entityType);
    }

    public void toggleLike(UUID productId, String entityType, UUID userId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }

        if (!userService.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        Optional<LikeEntity> existingLike = likeRepository
                .findByEntityIdAndEntityTypeAndUserId(productId, entityType, userId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            LikeEntity newLike = LikeEntity.builder()
                    .entityId(productId)
                    .entityType(entityType)
                    .userId(userId)
                    .build();
            likeRepository.save(newLike);
        }
    }

    public boolean checkIfLiked(UUID entityId, String entityType, UUID userId) {
        return likeRepository.existsByEntityIdAndEntityTypeAndUserId(entityId, entityType, userId);
    }
}