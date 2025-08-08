package com.audio.service;

import com.audio.dto.response.UserServiceResponse;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<UserServiceResponse> findById(UUID id);
    Optional<UserServiceResponse> findByEmail(String email);
    void deleteUser(UUID userId);
    boolean existsById(UUID userId);
}