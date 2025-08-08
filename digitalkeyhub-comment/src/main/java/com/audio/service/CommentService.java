package com.audio.service;

import com.audio.dto.CommentServiceResponse;
import com.audio.exception.AccessDeniedException;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    List<CommentServiceResponse> getCommentsForEntity(UUID entityId, String entityType);

    CommentServiceResponse addComment(UUID entityId, String entityType, UUID userId, String content);

    void deleteComment(UUID commentId, UUID userId)
            throws EntityNotFoundException, AccessDeniedException;
}