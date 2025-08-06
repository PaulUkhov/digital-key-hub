package com.audio.service.impl;

import com.audio.dto.RegisterDto;
import com.audio.dto.UserResponseDto;
import com.audio.entity.ProfileEntity;
import com.audio.entity.UserEntity;
import com.audio.mapper.UserMapper;
import com.audio.repository.UserRepository;
import com.audio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;

    @Override
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

    @Override
    @Cacheable(value = "users", key = "#id")
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findById(UUID id) {
        return userRepo.findById(id)
                .map(userMapper::toUserResponseDto);
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findByEmail(String email) {
        return userRepo.findByEmail(email)
                .map(userMapper::toUserResponseDto);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(UUID userId) {
        userRepo.deleteById(userId);
    }

    @Override
    public boolean existsById(UUID userId) {
        return userRepo.existsById(userId);
    }
}