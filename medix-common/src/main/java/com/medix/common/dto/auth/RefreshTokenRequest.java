package com.medix.common.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Used to get a new token pair and to log out (revoke the refresh token).
 */
public record RefreshTokenRequest(
        @NotBlank @Size(max = 512) String refreshToken
) {

    @Override
    public String toString() {
        return "RefreshTokenRequest[refreshToken=***]";
    }
}
