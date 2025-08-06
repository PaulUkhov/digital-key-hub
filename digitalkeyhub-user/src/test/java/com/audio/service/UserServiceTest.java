package com.audio.service;

import com.audio.dto.ProfileDto;
import com.audio.dto.ProfileResponseDto;
import com.audio.dto.RegisterDto;
import com.audio.dto.UserResponseDto;
import com.audio.entity.ProfileEntity;
import com.audio.entity.UserEntity;
import com.audio.exception.ProfileNotFoundException;
import com.audio.mapper.UserMapper;
import com.audio.repository.ProfileRepository;
import com.audio.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private ProfileRepository profileRepo;

    @Mock
    private UserMapper userMapper;

    @Mock
    private FileStorageService storageService;

    @Mock
    private MultipartFile image;

    @InjectMocks
    private UserService userService;

    private final UUID testUserId = UUID.randomUUID();
    private final String testEmail = "test@example.com";

    @Test
    void createUser_shouldCreateNewUser() {
        // Arrange
        RegisterDto dto = new RegisterDto(testEmail, "password");
        UserEntity savedUser = new UserEntity();
        savedUser.setId(testUserId);
        savedUser.setEmail(testEmail);
        savedUser.setProfile(new ProfileEntity()); // Профиль будет сохранен каскадно

        UserResponseDto expectedResponse = new UserResponseDto(testUserId, testEmail, null);

        when(userRepo.save(any(UserEntity.class))).thenReturn(savedUser);
        when(userMapper.toUserResponseDto(savedUser)).thenReturn(expectedResponse);

        // Act
        UserResponseDto result = userService.createUser(dto);

        // Assert
        assertNotNull(result);
        assertEquals(testEmail, result.email());
        verify(userRepo).save(any(UserEntity.class));
    }

    @Test
    void updateProfile_shouldUpdateExistingProfile() {
        // Arrange
        ProfileDto dto = new ProfileDto("New Name", "New Bio");
        ProfileEntity profile = new ProfileEntity();
        profile.setName("Old Name");
        ProfileEntity savedProfile = new ProfileEntity();
        savedProfile.setName(dto.name());
        ProfileResponseDto expectedResponse = new ProfileResponseDto(testUserId, dto.name(), dto.bio(), null);

        when(profileRepo.findByUserId(testUserId)).thenReturn(Optional.of(profile));
        when(profileRepo.save(profile)).thenReturn(savedProfile);
        when(userMapper.toProfileResponseDto(savedProfile)).thenReturn(expectedResponse);

        // Act
        ProfileResponseDto result = userService.updateProfile(testUserId, dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.name(), result.name());
        assertEquals(dto.bio(), result.bio());
        verify(profileRepo).findByUserId(testUserId);
        verify(profileRepo).save(profile);
    }

    @Test
    void updateProfile_shouldThrowExceptionWhenProfileNotFound() {
        // Arrange
        ProfileDto dto = new ProfileDto("Name", "Bio");
        when(profileRepo.findByUserId(testUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProfileNotFoundException.class,
                () -> userService.updateProfile(testUserId, dto));
    }

    @Test
    void updateAvatar_shouldUploadNewAvatar() throws Exception {
        // Arrange
        String fileName = "avatar.jpg";
        String fileUrl = "http://storage/avatar.jpg";
        ProfileEntity profile = new ProfileEntity();
        ProfileEntity savedProfile = new ProfileEntity();
        savedProfile.setAvatarUrl(fileUrl);
        ProfileResponseDto expectedResponse = new ProfileResponseDto(testUserId, null, null, fileUrl);

        when(profileRepo.findByUserId(testUserId)).thenReturn(Optional.of(profile));
        when(image.getOriginalFilename()).thenReturn(fileName);
        when(storageService.uploadFile(any(MultipartFile.class), anyString())).thenReturn(fileUrl);
        when(profileRepo.save(any(ProfileEntity.class))).thenReturn(savedProfile);
        when(userMapper.toProfileResponseDto(any(ProfileEntity.class))).thenReturn(expectedResponse);

        // Act
        ProfileResponseDto result = userService.updateAvatar(testUserId, image);

        // Assert
        assertNotNull(result);
        assertEquals(fileUrl, result.avatarUrl());
        verify(storageService).uploadFile(any(MultipartFile.class), anyString());
        verify(profileRepo).save(any(ProfileEntity.class));
    }

    @Test
    void updateAvatar_shouldDeleteOldAvatar() throws Exception {
        // Arrange
        String oldUrl = "http://storage/old.jpg";
        String newUrl = "http://storage/new.jpg";
        ProfileEntity profile = new ProfileEntity();
        profile.setAvatarUrl(oldUrl);

        when(profileRepo.findByUserId(testUserId)).thenReturn(Optional.of(profile));
        when(image.getOriginalFilename()).thenReturn("avatar.jpg");
        when(storageService.uploadFile(any(MultipartFile.class), anyString())).thenReturn(newUrl);
        when(profileRepo.save(any(ProfileEntity.class))).thenReturn(profile);

        // Act
        userService.updateAvatar(testUserId, image);

        // Assert
        verify(storageService).deleteFile(eq(oldUrl)); // Используем eq() для конкретного значения
        verify(storageService).uploadFile(any(MultipartFile.class), anyString());
    }


    @Test
    void updateAvatar_shouldHandleUploadError() throws Exception {
        // Arrange
        ProfileEntity profile = new ProfileEntity();
        when(profileRepo.findByUserId(testUserId)).thenReturn(Optional.of(profile));
        when(image.getOriginalFilename()).thenReturn("avatar.jpg");
        when(storageService.uploadFile(any(MultipartFile.class), anyString()))
                .thenThrow(new RuntimeException("Upload failed"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userService.updateAvatar(testUserId, image));
    }

    @Test
    void findById_shouldReturnUserWhenExists() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setId(testUserId);
        UserResponseDto expectedResponse = new UserResponseDto(testUserId, testEmail, null);

        when(userRepo.findById(testUserId)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDto(user)).thenReturn(expectedResponse);

        // Act
        Optional<UserResponseDto> result = userService.findById(testUserId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUserId, result.get().id());
        verify(userRepo).findById(testUserId);
    }

    @Test
    void findById_shouldReturnEmptyWhenUserNotFound() {
        // Arrange
        when(userRepo.findById(testUserId)).thenReturn(Optional.empty());

        // Act
        Optional<UserResponseDto> result = userService.findById(testUserId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findByEmail_shouldReturnUserWhenExists() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setEmail(testEmail);
        UserResponseDto expectedResponse = new UserResponseDto(testUserId, testEmail, null);

        when(userRepo.findByEmail(testEmail)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDto(user)).thenReturn(expectedResponse);

        // Act
        Optional<UserResponseDto> result = userService.findByEmail(testEmail);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testEmail, result.get().email());
        verify(userRepo).findByEmail(testEmail);
    }
}