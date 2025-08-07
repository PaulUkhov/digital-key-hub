package com.audio.service.impl;


import com.audio.dto.CommentServiceResponse;
import com.audio.dto.response.UserServiceInfoResponse;
import com.audio.dto.response.UserServiceResponse;
import com.audio.entity.CommentEntity;
import com.audio.exception.AccessDeniedException;
import com.audio.repository.CommentRepository;
import com.audio.service.CommentService;
import com.audio.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    public List<CommentServiceResponse> getCommentsForEntity(UUID entityId, String entityType) {
        return commentRepository.findByEntityIdAndEntityTypeOrderByCreatedAtDesc(entityId, entityType)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public CommentServiceResponse addComment(UUID entityId, String entityType, UUID userId, String content) {
        CommentEntity comment = CommentEntity.builder()
                .entityId(entityId)
                .entityType(entityType)
                .userId(userId)
                .content(content)
                .build();

        return mapToDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(UUID commentId, UUID userId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("You can delete only your own comments");
        }

        commentRepository.delete(comment);
    }

    private CommentServiceResponse mapToDto(CommentEntity entity) {
        UserServiceResponse user = userService.findById(entity.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return CommentServiceResponse.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .user(new UserServiceInfoResponse(user.id(), user.email()))
                .build();
    }
}
