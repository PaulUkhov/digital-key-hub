package com.audio.repository;

import com.audio.entity.UserEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findInfoById(UUID id);

    boolean existsById(@NotNull UUID id);
    boolean existsByEmail(String email);
}
