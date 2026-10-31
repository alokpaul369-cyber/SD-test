package com.medix.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.medix.common.enums.ErrorCode;

import java.time.Instant;
import java.util.List;

/**
 * Error body returned by the server for every non-2xx response.
 *
 * @param timestamp   when the error happened (UTC)
 * @param status      HTTP status code
 * @param code        machine-readable error code
 * @param message     human-readable message, safe to show in the UI
 * @param path        request path, e.g. "/api/reservations"
 * @param fieldErrors per-field problems; only present for VALIDATION_FAILED
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(
        Instant timestamp,
        int status,
        ErrorCode code,
        String message,
        String path,
        List<FieldViolation> fieldErrors
) {

    public ApiError {
        fieldErrors = fieldErrors == null ? List.of() : List.copyOf(fieldErrors);
    }

    public static ApiError of(ErrorCode code, String message, String path) {
        return new ApiError(Instant.now(), code.httpStatus(), code, message, path, List.of());
    }

    /**
     * @param field   request field path, e.g. "items[0].quantity"
     * @param message what is wrong with it
     */
    public record FieldViolation(String field, String message) {
    }
}
