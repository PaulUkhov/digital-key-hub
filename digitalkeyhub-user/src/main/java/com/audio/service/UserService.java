package com.audio.service;

import com.audio.dto.ProfileDto;
import com.audio.dto.ProfileResponseDto;
import com.audio.dto.RegisterDto;
import com.audio.dto.UserResponseDto;
import com.audio.exception.ProfileNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponseDto createUser(RegisterDto dto);

    ProfileResponseDto updateProfile(UUID userId, ProfileDto dto)
            throws ProfileNotFoundException;

    ProfileResponseDto updateAvatar(UUID userId, MultipartFile image)
            throws ProfileNotFoundException;

    Optional<UserResponseDto> findById(UUID id);

    Optional<UserResponseDto> findByEmail(String email);

    void deleteUser(UUID userId);

    boolean existsById(UUID userId);
}