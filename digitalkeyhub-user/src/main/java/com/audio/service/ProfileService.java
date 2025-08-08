package com.audio.service;

import com.audio.dto.request.ProfileUpdateServiceRequest;
import com.audio.dto.response.ProfileServiceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProfileService {
    ProfileServiceResponse updateProfile(UUID userId, ProfileUpdateServiceRequest dto);
    ProfileServiceResponse updateAvatar(UUID userId, MultipartFile image);
}