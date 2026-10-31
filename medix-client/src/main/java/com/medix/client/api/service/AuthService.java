package com.medix.client.api.service;

import com.medix.common.dto.auth.LoginRequest;
import com.medix.common.dto.auth.RegisterOwnerRequest;
import com.medix.common.dto.auth.RegisterRequest;
import com.medix.common.dto.auth.TokenResponse;
import com.medix.common.dto.auth.UserResponse;

/**
 * Registration, login and session tokens.
 * <p>
 * All methods block and throw {@link ApiException} on failure: call them from a SwingWorker, never on the EDT.
 * Implementations store the returned tokens in the client session; the UI never handles tokens itself.
 */
public interface AuthService {

    /**
     * Registers a customer and logs them in.
     * <p>{@code POST /api/auth/register} – public
     */
    TokenResponse register(RegisterRequest request);

    /**
     * Registers a pharmacy owner with their first pharmacy (status PENDING) and logs them in.
     * <p>{@code POST /api/auth/register-owner} – public
     */
    TokenResponse registerOwner(RegisterOwnerRequest request);

    /**
     * Logs in with email and password.
     * <p>{@code POST /api/auth/login} – public
     */
    TokenResponse login(LoginRequest request);

    /**
     * Exchanges the stored refresh token for a new token pair. HTTP implementations also call this
     * automatically once when a request fails with TOKEN_EXPIRED.
     * <p>{@code POST /api/auth/refresh} – public (needs a valid refresh token)
     */
    TokenResponse refresh();

    /**
     * Revokes the stored refresh token and clears the client session. Never throws for an already-invalid token.
     * <p>{@code POST /api/auth/logout} – authenticated (any role)
     */
    void logout();

    /**
     * Returns the logged-in user.
     * <p>{@code GET /api/auth/me} – authenticated (any role)
     */
    UserResponse currentUser();
}
