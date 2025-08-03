package com.audio.controller.auth;

import com.audio.configSecurity.dto.AuthResponse;
import com.audio.configSecurity.dto.LoginRequest;
import com.audio.configSecurity.dto.RegisterRequest;
import com.audio.configSecurity.exception.AuthException;
import com.audio.repository.UserRepository;
import com.audio.configSecurity.service.AuthService;
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
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Email already in use");
        }
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.email() == null || loginRequest.password() == null || loginRequest.password().isEmpty()) {
            throw new AuthException("Invalid request");
        }
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}