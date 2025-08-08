package com.audio.converter;

import com.audio.service.JwtService;
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