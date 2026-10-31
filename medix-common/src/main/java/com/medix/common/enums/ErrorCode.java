package com.medix.common.enums;

/**
 * Machine-readable error codes returned in {@code ApiError.code}. Each code has a fixed HTTP status.
 */
public enum ErrorCode {
    VALIDATION_FAILED(400),
    MALFORMED_REQUEST(400),
    CSV_INVALID_FORMAT(400),
    UNAUTHORIZED(401),
    INVALID_CREDENTIALS(401),
    TOKEN_EXPIRED(401),
    INVALID_REFRESH_TOKEN(401),
    FORBIDDEN(403),
    ACCOUNT_DISABLED(403),
    NOT_FOUND(404),
    EMAIL_ALREADY_EXISTS(409),
    LICENSE_ALREADY_EXISTS(409),
    MEDICINE_ALREADY_EXISTS(409),
    MEDICINE_IN_USE(409),
    PHARMACY_NOT_APPROVED(409),
    INSUFFICIENT_STOCK(409),
    INVALID_STATUS_TRANSITION(409),
    RESERVATION_LIMIT_REACHED(409),
    CONCURRENT_MODIFICATION(409),
    FILE_TOO_LARGE(413),
    INVALID_PICKUP_CODE(422),
    RATE_LIMITED(429),
    INTERNAL_ERROR(500),
    GEOCODING_UNAVAILABLE(503),
    /** Client-only: the server could not be reached or the response was unreadable. Never sent by the server. */
    NETWORK_ERROR(0);

    private final int httpStatus;

    ErrorCode(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int httpStatus() {
        return httpStatus;
    }
}
