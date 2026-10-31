package com.medix.client.api.service;

import com.medix.common.dto.ApiError;
import com.medix.common.enums.ErrorCode;

import java.io.Serial;
import java.util.List;

/**
 * Thrown by every service method when a call fails. Wraps the server's {@link ApiError}
 * (or a client-made one with {@link ErrorCode#NETWORK_ERROR} when the server could not be reached).
 * The message is safe to show to the user.
 */
public class ApiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient ApiError error;

    public ApiException(ApiError error) {
        this(error, null);
    }

    public ApiException(ApiError error, Throwable cause) {
        super(error.message(), cause);
        this.error = error;
    }

    public static ApiException of(ErrorCode code, String message) {
        return new ApiException(ApiError.of(code, message, null));
    }

    public static ApiException network(String message, Throwable cause) {
        return new ApiException(ApiError.of(ErrorCode.NETWORK_ERROR, message, null), cause);
    }

    public ApiError error() {
        return error;
    }

    public ErrorCode code() {
        return error.code();
    }

    public int status() {
        return error.status();
    }

    public List<ApiError.FieldViolation> fieldErrors() {
        return error.fieldErrors();
    }

    public boolean isValidationError() {
        return error.code() == ErrorCode.VALIDATION_FAILED;
    }

    /** True when the user must log in again (missing/expired/invalid token). */
    public boolean isAuthenticationError() {
        return switch (error.code()) {
            case UNAUTHORIZED, TOKEN_EXPIRED, INVALID_REFRESH_TOKEN -> true;
            default -> false;
        };
    }

    public boolean isNetworkError() {
        return error.code() == ErrorCode.NETWORK_ERROR;
    }
}
