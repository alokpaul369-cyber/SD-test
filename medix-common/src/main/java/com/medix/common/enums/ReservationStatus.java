package com.medix.common.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * Reservation lifecycle.
 * <pre>
 * PENDING -> CONFIRMED -> READY_FOR_PICKUP -> COMPLETED
 *    |           |               |
 *    +-----------+---------------+--> CANCELLED / EXPIRED
 * </pre>
 */
public enum ReservationStatus {
    PENDING,
    CONFIRMED,
    READY_FOR_PICKUP,
    COMPLETED,
    CANCELLED,
    EXPIRED;

    /** Statuses in which stock is still held for the customer. */
    public boolean isActive() {
        return this == PENDING || this == CONFIRMED || this == READY_FOR_PICKUP;
    }

    /** Statuses this status may move to. Terminal statuses return an empty set. */
    public Set<ReservationStatus> allowedNext() {
        return switch (this) {
            case PENDING -> EnumSet.of(CONFIRMED, CANCELLED, EXPIRED);
            case CONFIRMED -> EnumSet.of(READY_FOR_PICKUP, COMPLETED, CANCELLED, EXPIRED);
            case READY_FOR_PICKUP -> EnumSet.of(COMPLETED, CANCELLED, EXPIRED);
            case COMPLETED, CANCELLED, EXPIRED -> EnumSet.noneOf(ReservationStatus.class);
        };
    }

    public boolean canTransitionTo(ReservationStatus next) {
        return allowedNext().contains(next);
    }
}
