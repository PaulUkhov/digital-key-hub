package com.audio.controller.user;

import com.audio.dto.ProfileDto;
import com.audio.dto.ProfileResponseDto;
import com.audio.dto.RegisterDto;
import com.audio.dto.UserResponseDto;
import com.audio.service.ProfileService;
import com.audio.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegisterDto dto) {
        UserResponseDto response = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUser(
            @PathVariable("userId") UUID userId) {
        return userService.findById(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponseDto> getUserByEmail(
            @RequestParam(name = "email") @Email @NotBlank String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{userId}/profile")
    public ResponseEntity<ProfileResponseDto> updateProfile(
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody ProfileDto dto) {
        return ResponseEntity.ok(profileService.updateProfile(userId, dto));
    }

    @PatchMapping("/{userId}/avatar")
    public ResponseEntity<ProfileResponseDto> updateAvatar(
            @PathVariable("userId") UUID userId,
            @RequestParam("file") @NotNull MultipartFile file) {
        return ResponseEntity.ok(profileService.updateAvatar(userId, file));
    }
}