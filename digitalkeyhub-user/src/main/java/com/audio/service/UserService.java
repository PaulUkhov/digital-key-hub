package com.audio.service;

import com.audio.dto.ProfileDto;
import com.audio.dto.RegisterDto;
import com.audio.entity.ProfileEntity;
import com.audio.entity.UserEntity;
import com.audio.exception.ProfileNotFoundException;
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

    @Transactional
    public UserEntity createUser(RegisterDto dto) {
        UserEntity user = new UserEntity();
        user.setEmail(dto.email());
        user.setPassword(dto.password());

        ProfileEntity profile = new ProfileEntity();
        profile.setUser(user);
        user.setProfile(profile);

        return userRepo.save(user);
    }

    @Transactional
    public ProfileEntity updateProfile(UUID userId, ProfileDto dto) {
        ProfileEntity profile = profileRepo.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        profile.setName(dto.name());
        profile.setBio(dto.bio());

        return profileRepo.save(profile);
    }

    @Transactional
    public void updateAvatar(UUID userId, MultipartFile image) {
        // TODO
    }


    @Transactional(readOnly = true)
    public Optional<UserEntity> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}