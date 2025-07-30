package com.audio.mapper;

import com.audio.dto.ProfileResponseDto;
import com.audio.dto.UserResponseDto;
import com.audio.entity.ProfileEntity;
import com.audio.entity.UserEntity;

public interface UserMapper {
    UserResponseDto toUserResponseDto(UserEntity user);
    ProfileResponseDto toProfileResponseDto(ProfileEntity profile);
}
