package com.medix.common.dto.admin;

import java.time.Instant;

/**
 * One audit log entry, newest first in listings.
 *
 * @param actorId    null for actions performed by the system (scheduled jobs)
 * @param actorEmail "system" for scheduled jobs
 * @param action     e.g. "PHARMACY_APPROVED", "USER_DISABLED", "RESERVATION_EXPIRED", "LOGIN_FAILED"
 * @param entityType e.g. "PHARMACY", "USER", "RESERVATION", "INVENTORY_ITEM", "MEDICINE"
 * @param details    short human-readable description
 */
public record AuditLogRow(
        Long id,
        Long actorId,
        String actorEmail,
        String action,
        String entityType,
        Long entityId,
        String details,
        Instant createdAt
) {
}
