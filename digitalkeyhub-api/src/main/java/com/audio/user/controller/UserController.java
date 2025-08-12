package com.audio.user.controller;

import com.audio.dto.response.ProfileServiceResponse;
import com.audio.dto.response.UserServiceResponse;
import com.audio.service.ProfileService;
import com.audio.service.UserService;
import com.audio.user.dto.request.ProfileUpdateRequest;
import com.audio.user.dto.response.ProfileResponse;
import com.audio.user.dto.response.UserResponse;
import com.audio.user.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "User Management", description = "API for user operations")
@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProfileService profileService;
    private final UserMapper userMapper;

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves user details by their unique identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(
            @Parameter(description = "ID of the user to retrieve", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("userId") UUID userId) {
        return userService.findById(userId)
                .map(userMapper::toUserResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Search user by email",
            description = "Retrieves user details by their email address"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid email format")
    })
    @GetMapping("/search")
    public ResponseEntity<UserResponse> getUserByEmail(
            @Parameter(description = "Email address of the user to find", required = true, example = "user@example.com")
            @RequestParam(name = "email") @Email @NotBlank String email) {
        return userService.findByEmail(email)
                .map(userMapper::toUserResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update user profile",
            description = "Updates the profile information for a user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PatchMapping("/{userId}/profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @Parameter(description = "ID of the user whose profile to update", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody ProfileUpdateRequest request) {
        ProfileServiceResponse serviceResponse = profileService.updateProfile(
                userId,
                userMapper.toServiceRequest(request)
        );
        return ResponseEntity.ok(userMapper.toProfileResponse(serviceResponse));
    }

    @Operation(
            summary = "Update user avatar",
            description = "Updates the avatar image for a user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avatar updated successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid file format or size")
    })
    @PatchMapping("/{userId}/avatar")
    public ResponseEntity<ProfileResponse> updateAvatar(
            @Parameter(description = "ID of the user whose avatar to update", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("userId") UUID userId,
            @Parameter(description = "Image file to upload as avatar (JPEG/PNG)", required = true)
            @RequestParam("file") @NotNull MultipartFile file) {
        ProfileServiceResponse serviceResponse = profileService.updateAvatar(userId, file);
        return ResponseEntity.ok(userMapper.toProfileResponse(serviceResponse));
    }
}