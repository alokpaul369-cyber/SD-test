package com.medix.common.dto.auth;

/**
 * Returned by register, register-owner, login and refresh.
 *
 * @param accessToken      JWT to send as "Authorization: Bearer &lt;accessToken&gt;"
 * @param refreshToken     opaque token, single use; each refresh returns a new one
 * @param tokenType        always "Bearer"
 * @param expiresInSeconds access token lifetime in seconds
 * @param user             the logged-in user
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        UserResponse user
) {

    @Override
    public String toString() {
        return "TokenResponse[tokenType=" + tokenType + ", expiresInSeconds=" + expiresInSeconds + ", user=" + user + "]";
    }
}
