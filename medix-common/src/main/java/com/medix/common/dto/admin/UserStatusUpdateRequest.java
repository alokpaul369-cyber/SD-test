package com.medix.common.dto.admin;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Admin enables or disables a user. Disabling also revokes the user's refresh tokens.
 */
public record UserStatusUpdateRequest(
        @NotNull Boolean enabled,
        @Size(max = 500) String reason
) {
}
