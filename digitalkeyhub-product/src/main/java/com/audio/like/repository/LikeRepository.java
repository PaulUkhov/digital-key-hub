package com.audio.like.repository;

import com.audio.like.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<LikeEntity, UUID> {
    long countByEntityIdAndEntityType(UUID entityId, String entityType);
    boolean existsByEntityIdAndEntityTypeAndUserId(UUID entityId, String entityType, UUID userId);
    Optional<LikeEntity> findByEntityIdAndEntityTypeAndUserId(UUID entityId, String entityType, UUID userId);

}
