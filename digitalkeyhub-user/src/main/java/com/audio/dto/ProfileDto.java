package com.audio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileDto(
        @NotBlank String name,
        @Size(max = 500) String bio
) {}



