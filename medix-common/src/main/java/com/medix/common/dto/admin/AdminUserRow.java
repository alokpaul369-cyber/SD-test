package com.medix.common.dto.admin;

import com.medix.common.enums.Role;

import java.time.Instant;

/**
 * One row in the admin user table.
 *
 * @param enabled     false = account disabled, cannot log in
 * @param lastLoginAt null if the user never logged in
 */
public record AdminUserRow(
        Long id,
        String fullName,
        String email,
        String phone,
        Role role,
        boolean enabled,
        Instant createdAt,
        Instant lastLoginAt
) {
}
