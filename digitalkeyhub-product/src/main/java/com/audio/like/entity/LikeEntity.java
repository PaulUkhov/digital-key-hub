package com.audio.like.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"entity_id", "entity_type", "user_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}