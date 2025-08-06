package com.audio.service;

import com.audio.dto.ProfileDto;
import com.audio.dto.ProfileResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProfileService {
    ProfileResponseDto updateProfile(UUID userId, ProfileDto dto);
    ProfileResponseDto updateAvatar(UUID userId, MultipartFile image);
}