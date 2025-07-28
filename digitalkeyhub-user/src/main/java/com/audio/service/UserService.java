package com.audio.service;

import com.audio.dto.ProfileDto;
import com.audio.dto.ProfileResponseDto;
import com.audio.dto.RegisterDto;
import com.audio.dto.UserResponseDto;
import com.audio.entity.ProfileEntity;
import com.audio.entity.UserEntity;
import com.audio.exception.ProfileNotFoundException;
import com.audio.mapper.UserMapper;
import com.audio.repository.ProfileRepository;
import com.audio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final ProfileRepository profileRepo;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDto createUser(RegisterDto dto) {
        UserEntity user = new UserEntity();
        user.setEmail(dto.email());
        user.setPassword(dto.password());

        ProfileEntity profile = new ProfileEntity();
        profile.setUser(user);
        user.setProfile(profile);

        UserEntity savedUser = userRepo.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    @Transactional
    public ProfileResponseDto updateProfile(UUID userId, ProfileDto dto) {
        ProfileEntity profile = profileRepo.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        profile.setName(dto.name());
        profile.setBio(dto.bio());

        ProfileEntity savedProfile = profileRepo.save(profile);
        return userMapper.toProfileResponseDto(savedProfile);
    }

    @Transactional
    public ProfileResponseDto updateAvatar(UUID userId, MultipartFile image) {
        ProfileEntity profile = profileRepo.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        // TODO: реализовать логику обновления аватара

        return userMapper.toProfileResponseDto(profile);
    }

    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findById(UUID id) {
        return userRepo.findById(id)
                .map(userMapper::toUserResponseDto);
    }

    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findByEmail(String email) {
        return userRepo.findByEmail(email)
                .map(userMapper::toUserResponseDto);
    }
}