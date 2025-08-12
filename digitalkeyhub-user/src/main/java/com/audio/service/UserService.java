package com.audio.service;

import com.audio.dto.response.UserServiceInfoResponse;
import com.audio.dto.response.UserServiceResponse;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<UserServiceResponse> findById(UUID id);
    Optional<UserServiceInfoResponse> findInfoById(UUID id);
    Optional<UserServiceResponse> findByEmail(String email);
    Optional<UserServiceInfoResponse> findByUsername(String username);
    void deleteById(UUID userId);
    boolean existsById(UUID userId);
    boolean existsByEmail(String email);
}