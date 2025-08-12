package com.audio.mapper;

import com.audio.dto.response.ProfileServiceResponse;
import com.audio.dto.response.UserServiceInfoResponse;
import com.audio.dto.response.UserServiceResponse;
import com.audio.entity.ProfileEntity;
import com.audio.entity.UserEntity;

public interface UserMapper {
    UserServiceResponse toUserResponseDto(UserEntity user);
    UserServiceInfoResponse toUserServiceInfoDto(UserEntity user);
    ProfileServiceResponse toProfileResponseDto(ProfileEntity profile);

}
