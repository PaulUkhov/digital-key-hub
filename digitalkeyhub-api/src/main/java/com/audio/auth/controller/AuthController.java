package com.audio.auth.controller;

import com.audio.auth.dto.request.LoginRequest;
import com.audio.auth.dto.request.RegisterRequest;
import com.audio.auth.dto.response.AuthResponse;
import com.audio.auth.mapper.AuthMapper;
import com.audio.repository.UserRepository;
import com.audio.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Email already in use");
        }

        return ResponseEntity.ok(authService.register(AuthMapper.toServiceRegisterRequest(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        var response = authService.login(AuthMapper.toServiceLoginRequest(request));
        return ResponseEntity.ok(AuthMapper.toAuthResponse(response));
    }
}