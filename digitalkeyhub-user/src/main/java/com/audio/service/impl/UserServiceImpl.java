package com.audio.service.impl;

import com.audio.dto.response.UserServiceResponse;
import com.audio.mapper.UserMapper;
import com.audio.repository.UserRepository;
import com.audio.service.UserService;
import lombok.RequiredArgsConstructor;
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
    @Transactional(readOnly = true)
    public Optional<UserServiceResponse> findById(UUID id) {
        return userRepo.findById(id)
                .map(userMapper::toUserResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserServiceResponse> findByEmail(String email) {
        return userRepo.findByEmail(email)
                .map(userMapper::toUserResponseDto);
    }

    @Override
    public void deleteUser(UUID userId) {
        userRepo.deleteById(userId);
    }

    @Override
    public boolean existsById(UUID userId) {
        return userRepo.existsById(userId);
    }
}