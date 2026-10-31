package com.medix.common.enums;

/**
 * Kinds of in-app alerts. The recipient is shown in brackets.
 */
public enum AlertType {
    /** Available quantity at or below the item's low-stock threshold [owner]. */
    LOW_STOCK,
    /** Available quantity reached zero [owner]. */
    OUT_OF_STOCK,
    /** A batch expires within the expiry warning window [owner]. */
    EXPIRING_SOON,
    /** A batch has passed its expiry date [owner]. */
    EXPIRED,
    /** A customer created a reservation [owner]. */
    RESERVATION_CREATED,
    /** A reservation changed status [customer, owner]. */
    RESERVATION_STATUS_CHANGED,
    /** A new or edited pharmacy is waiting for review [admin]. */
    PHARMACY_PENDING_REVIEW,
    /** An admin changed the pharmacy status [owner]. */
    PHARMACY_STATUS_CHANGED
}
