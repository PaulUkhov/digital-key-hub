package com.audio.configSecurity.converter;

import com.audio.configSecurity.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationConverter {
    private final JwtService jwtService;

    public Authentication convert(String jwtToken) {
        return jwtService.parseToken(jwtToken);
    }
}