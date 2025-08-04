package com.audio.repository;

import com.audio.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {
    List<CommentEntity> findByEntityIdAndEntityTypeOrderByCreatedAtDesc(UUID entityId, String entityType);
}
