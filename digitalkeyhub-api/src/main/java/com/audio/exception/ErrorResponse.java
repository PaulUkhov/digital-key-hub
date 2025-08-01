package com.audio.exception;

public record ErrorResponse(
        int status,
        String error,
        String message,
        long timestamp
) {}
