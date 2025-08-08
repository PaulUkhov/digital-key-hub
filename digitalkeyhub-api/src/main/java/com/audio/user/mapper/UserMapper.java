package com.audio.user.mapper;

import com.audio.dto.request.ProfileUpdateServiceRequest;
import com.audio.dto.response.ProfileServiceResponse;
import com.audio.dto.response.UserServiceResponse;
import com.audio.user.dto.request.ProfileUpdateRequest;
import com.audio.user.dto.response.ProfileResponse;
import com.audio.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public ProfileUpdateServiceRequest toServiceRequest(ProfileUpdateRequest request) {
        return new ProfileUpdateServiceRequest(
                request.name(),
                request.bio()
        );
    }

    public ProfileResponse toProfileResponse(ProfileServiceResponse serviceResponse) {
        return new ProfileResponse(
                serviceResponse.id(),
                serviceResponse.name(),
                serviceResponse.bio(),
                serviceResponse.avatarUrl()
        );
    }

    public UserResponse toUserResponse(UserServiceResponse serviceResponse) {
        return new UserResponse(
                serviceResponse.id(),
                serviceResponse.email(),
                toProfileResponse(serviceResponse.profile())
        );
    }
}