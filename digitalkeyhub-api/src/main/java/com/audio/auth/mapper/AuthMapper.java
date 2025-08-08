package com.audio.auth.mapper;

import com.audio.auth.dto.request.LoginRequest;
import com.audio.auth.dto.request.RegisterRequest;
import com.audio.dto.request.LoginServiceRequest;
import com.audio.dto.request.RegisterServiceRequest;
import com.audio.dto.response.AuthServiceResponse;
import com.audio.auth.dto.response.AuthResponse;

public class AuthMapper {

    public static RegisterServiceRequest toServiceRegisterRequest(RegisterRequest request) {
        return new RegisterServiceRequest(request.email(), request.password());
    }

    public static LoginServiceRequest toServiceLoginRequest(LoginRequest request) {
        return new LoginServiceRequest(request.email(), request.password());
    }

    public static AuthResponse toAuthResponse(AuthServiceResponse response) {
        return new AuthResponse(response.access(), response.refresh());
    }
}