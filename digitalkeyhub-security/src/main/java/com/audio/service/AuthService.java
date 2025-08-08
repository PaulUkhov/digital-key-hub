package com.audio.service;

import com.audio.repository.RoleRepository;
import com.audio.user.SecurityUser;
import com.audio.user.UserDetailsServiceImpl;
import com.audio.entity.Role;
import com.audio.entity.UserEntity;
import com.audio.repository.UserRepository;
import com.audio.dto.response.AuthServiceResponse;
import com.audio.dto.request.LoginServiceRequest;
import com.audio.dto.request.RegisterServiceRequest;
import com.audio.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Transactional
    public AuthServiceResponse register(RegisterServiceRequest request) {
        if (request.email() == null || request.email().isEmpty() ||
                request.password() == null || request.password().isEmpty()) {
            throw new AuthException("Invalid request");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException("Email already in use");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ROLE_USER");
                    return roleRepository.save(newRole);
                });

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.email());
        userEntity.setPassword(encoder.encode(request.password()));
        userEntity.setRoles(Set.of(userRole));
        UserEntity savedUser = userRepository.save(userEntity);

        UserDetails userDetails = new SecurityUser(savedUser);
        return new AuthServiceResponse(
                jwtService.generateToken(userDetails),
                jwtService.generateRefreshToken(userDetails)
        );
    }

    public AuthServiceResponse login(LoginServiceRequest request) {
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(request.email());
        } catch (Exception e) {
            throw new AuthException("Invalid email or password");
        }

        if (!encoder.matches(request.password(), userDetails.getPassword())) {
            throw new AuthException("Invalid password");
        }

        return new AuthServiceResponse(
                jwtService.generateToken(userDetails),
                jwtService.generateRefreshToken(userDetails)
        );
    }
}