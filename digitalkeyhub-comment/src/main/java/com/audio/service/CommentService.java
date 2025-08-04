package com.audio.service;

import com.audio.dto.CommentDto;
import com.audio.dto.UserDto;
import com.audio.dto.UserResponseDto;
import com.audio.entity.CommentEntity;
import com.audio.exception.AccessDeniedException;
import com.audio.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;

    public List<CommentDto> getCommentsForEntity(UUID entityId, String entityType) {
        return commentRepository.findByEntityIdAndEntityTypeOrderByCreatedAtDesc(entityId, entityType)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public CommentDto addComment(UUID entityId, String entityType, UUID userId, String content) {
        CommentEntity comment = CommentEntity.builder()
                .entityId(entityId)
                .entityType(entityType)
                .userId(userId)
                .content(content)
                .build();

        return mapToDto(commentRepository.save(comment));
    }

    public void deleteComment(UUID commentId, UUID userId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("You can delete only your own comments");
        }

        commentRepository.delete(comment);
    }

    private CommentDto mapToDto(CommentEntity entity) {
        UserResponseDto user = userService.findById(entity.getUserId())
                .orElseThrow(()-> new EntityNotFoundException("User not found"));

        return CommentDto.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .user(new UserDto(user.id(), user.email()))
                .build();
    }
}
