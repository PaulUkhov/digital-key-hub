package com.audio.mapper;

import com.audio.dto.ProfileResponseDto;
import com.audio.dto.UserResponseDto;
import com.audio.entity.ProfileEntity;
import com.audio.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserResponseDto toUserResponseDto(UserEntity user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                toProfileResponseDto(user.getProfile())
        );
    }

    public ProfileResponseDto toProfileResponseDto(ProfileEntity profile) {
        return new ProfileResponseDto(
                profile.getId(),
                profile.getName(),
                profile.getBio()
        );
    }
}