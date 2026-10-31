package com.medix.common.enums;

/**
 * Why an inventory quantity changed. Every change to stock is recorded as a stock movement.
 */
public enum MovementType {
    /** New stock received (add stock). Positive. */
    STOCK_IN,
    /** Stock added by a CSV import. Positive. */
    CSV_IMPORT,
    /** Walk-in sale recorded by the owner. Negative. */
    SALE,
    /** Manual correction (count mismatch, damage, ...). Positive or negative. */
    ADJUSTMENT,
    /** Expired stock removed. Negative. */
    EXPIRED_REMOVAL,
    /** Stock held for a reservation. Lowers available quantity, not on-hand quantity. */
    RESERVATION_HOLD,
    /** Held stock returned after cancel/expiry. Raises available quantity. */
    RESERVATION_RELEASE,
    /** Held stock handed over to the customer. Lowers on-hand quantity. */
    RESERVATION_FULFILLED;

    /** Movement types an owner may choose in an adjust-stock request. */
    public boolean isManual() {
        return this == SALE || this == ADJUSTMENT || this == EXPIRED_REMOVAL;
    }
}
