package com.audio.service;

import com.audio.dto.RegisterDto;
import com.audio.dto.UserResponseDto;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponseDto createUser(RegisterDto dto);
    Optional<UserResponseDto> findById(UUID id);
    Optional<UserResponseDto> findByEmail(String email);
    void deleteUser(UUID userId);
    boolean existsById(UUID userId);
}