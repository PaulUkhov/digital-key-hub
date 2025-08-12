package com.audio.dto.response;

import java.util.UUID;

/**
 * DTO for representing user service response
 *
 * @param id unique user identifier
 * @param email user's email address
 * @param profile user's profile information (nullable)
 */
public record UserServiceResponse(
        UUID id,
        String email,
        ProfileServiceResponse profile
) {
    /**
     * Alternative constructor for cases when profile is not available
     *
     * @param userId unique user identifier
     * @param email user's email address
     */
    public UserServiceResponse(UUID userId, String email) {
        this(userId, email, null);
    }
}