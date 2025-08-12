package com.audio.mapper;

import com.audio.dto.response.ProfileServiceResponse;
import com.audio.dto.response.UserServiceInfoResponse;
import com.audio.dto.response.UserServiceResponse;
import com.audio.entity.ProfileEntity;
import com.audio.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {

    @Override
    public UserServiceResponse toUserResponseDto(UserEntity user) {
        return new UserServiceResponse(
                user.getId(),
                user.getEmail(),
                toProfileResponseDto(user.getProfile())
        );
    }

    @Override
    public UserServiceInfoResponse toUserServiceInfoDto(UserEntity user) {
        return new UserServiceInfoResponse(
                user.getId(),
                user.getEmail()
        );
    }


    @Override
    public ProfileServiceResponse toProfileResponseDto(ProfileEntity profile) {
        return new ProfileServiceResponse(
                profile.getId(),
                profile.getName(),
                profile.getBio(),
                profile.getAvatarUrl()
        );
    }
}