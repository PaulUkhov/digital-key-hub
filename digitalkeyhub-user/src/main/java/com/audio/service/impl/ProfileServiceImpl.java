package com.audio.service.impl;

import com.audio.dto.ProfileDto;
import com.audio.dto.ProfileResponseDto;
import com.audio.entity.ProfileEntity;
import com.audio.exception.ProfileNotFoundException;
import com.audio.mapper.UserMapper;
import com.audio.repository.ProfileRepository;
import com.audio.service.FileStorageService;
import com.audio.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepo;
    private final UserMapper userMapper;
    private final FileStorageService storageService;

    @Override
    @Transactional
    public ProfileResponseDto updateProfile(UUID userId, ProfileDto dto) {
        ProfileEntity profile = profileRepo.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        profile.setName(dto.name());
        profile.setBio(dto.bio());

        ProfileEntity savedProfile = profileRepo.save(profile);
        return userMapper.toProfileResponseDto(savedProfile);
    }

    @Override
    @Transactional
    public ProfileResponseDto updateAvatar(UUID userId, MultipartFile image) {
        ProfileEntity profile = profileRepo.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        try {
            if (profile.getAvatarUrl() != null) {
                storageService.deleteFile(profile.getAvatarUrl());
            }

            String extension = getFileExtension(image.getOriginalFilename());
            String newFileName = "avatar_" + userId + "_" + System.currentTimeMillis() + "." + extension;

            String filePath = storageService.uploadFile(image, newFileName);

            profile.setAvatarUrl(filePath);
            ProfileEntity savedProfile = profileRepo.save(profile);

            return userMapper.toProfileResponseDto(savedProfile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update avatar", e);
        }
    }


    String getFileExtension(String filename) {
        if (filename == null) {
            return "jpg";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "jpg";
        }
        return filename.substring(lastDot + 1);
    }
}
