package com.audio.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDto(
        @Email @NotBlank String email,
        @Size(min = 6) String password
) {}
