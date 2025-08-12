package com.audio.like.service;

import com.audio.exception.ProductNotFoundException;
import com.audio.exception.UserNotFoundException;

import java.util.UUID;

public interface LikeService {
    long getLikesCount(UUID entityId, String entityType);

    void toggleLike(UUID productId, String entityType, UUID userId)
            throws ProductNotFoundException, UserNotFoundException;

    boolean checkIfLiked(UUID entityId, String entityType, UUID userId);
}