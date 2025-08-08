package com.audio.user.controller;

import com.audio.dto.response.ProfileServiceResponse;
import com.audio.dto.response.UserServiceResponse;
import com.audio.service.ProfileService;
import com.audio.service.UserService;
import com.audio.user.dto.request.ProfileUpdateRequest;
import com.audio.user.dto.response.ProfileResponse;
import com.audio.user.dto.response.UserResponse;
import com.audio.user.mapper.UserMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProfileService profileService;
    private final UserMapper userMapper;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable("userId") UUID userId) {
        return userService.findById(userId)
                .map(userMapper::toUserResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponse> getUserByEmail(
            @RequestParam(name = "email") @Email @NotBlank String email) {
        return userService.findByEmail(email)
                .map(userMapper::toUserResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{userId}/profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody ProfileUpdateRequest request) {
        ProfileServiceResponse serviceResponse = profileService.updateProfile(
                userId,
                userMapper.toServiceRequest(request)
        );
        return ResponseEntity.ok(userMapper.toProfileResponse(serviceResponse));
    }

    @PatchMapping("/{userId}/avatar")
    public ResponseEntity<ProfileResponse> updateAvatar(
            @PathVariable("userId") UUID userId,
            @RequestParam("file") @NotNull MultipartFile file) {
        ProfileServiceResponse serviceResponse = profileService.updateAvatar(userId, file);
        return ResponseEntity.ok(userMapper.toProfileResponse(serviceResponse));
    }
}