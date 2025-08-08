package com.audio.exception.response;

public record ErrorResponse(
        int status,
        String error,
        String message,
        long timestamp
) {}
