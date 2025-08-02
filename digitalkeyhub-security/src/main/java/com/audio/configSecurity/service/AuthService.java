package com.audio.configSecurity.service;

import com.audio.configSecurity.user.SecurityUser;
import com.audio.configSecurity.user.UserDetailsServiceImpl;
import com.audio.entity.UserEntity;
import com.audio.repository.UserRepository;
import com.audio.configSecurity.dto.AuthResponse;
import com.audio.configSecurity.dto.LoginRequest;
import com.audio.configSecurity.dto.RegisterRequest;
import com.audio.configSecurity.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        if (request.email() == null || request.email().isEmpty() ||
                request.password() == null || request.password().isEmpty()) {
            throw new AuthException("Invalid request");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException("Email already in use");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.email());
        userEntity.setPassword(encoder.encode(request.password()));
        UserEntity savedUser = userRepository.save(userEntity);

        UserDetails userDetails = new SecurityUser(savedUser);
        return new AuthResponse(
                jwtService.generateToken(userDetails),
                jwtService.generateRefreshToken(userDetails)
        );
    }

    public AuthResponse login(LoginRequest request) {
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(request.email());
        } catch (Exception e) {
            throw new AuthException("Invalid email or password");
        }

        if (!encoder.matches(request.password(), userDetails.getPassword())) {
            throw new AuthException("Invalid password");
        }

        return new AuthResponse(
                jwtService.generateToken(userDetails),
                jwtService.generateRefreshToken(userDetails)
        );
    }
}