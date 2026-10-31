package com.medix.common.dto.alert;

import com.medix.common.enums.AlertType;

import java.time.Instant;

/**
 * An in-app alert for the current user. Related ids are null when not applicable.
 */
public record AlertResponse(
        Long id,
        AlertType type,
        String title,
        String message,
        Long pharmacyId,
        String pharmacyName,
        Long inventoryItemId,
        Long reservationId,
        boolean read,
        Instant createdAt
) {
}
