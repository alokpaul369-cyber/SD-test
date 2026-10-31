package com.medix.common.dto.auth;

import com.medix.common.enums.Role;

import java.time.Instant;

/**
 * The current user (GET /api/auth/me) and the user inside a token response.
 */
public record UserResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        Role role,
        Instant createdAt
) {
}
