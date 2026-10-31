package com.medix.common.dto.inventory;

import com.medix.common.enums.MovementType;

import java.time.Instant;

/**
 * One stock change, newest first in listings.
 *
 * @param quantityChange signed change applied
 * @param quantityAfter  on-hand quantity after the change
 * @param reservationId  set for RESERVATION_* movements
 * @param performedBy    full name of the user who made the change, or "system" for scheduled jobs
 */
public record StockMovementResponse(
        Long id,
        Long inventoryItemId,
        String medicineName,
        String batchNumber,
        MovementType type,
        int quantityChange,
        int quantityAfter,
        String reason,
        Long reservationId,
        String performedBy,
        Instant createdAt
) {
}
