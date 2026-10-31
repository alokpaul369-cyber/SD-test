package com.medix.common.enums;

/**
 * Lifecycle of a pharmacy. Only APPROVED pharmacies appear in public search and accept reservations.
 */
public enum PharmacyStatus {
    /** Registered by an owner, waiting for admin review. */
    PENDING,
    /** Visible in search, can accept reservations. */
    APPROVED,
    /** Rejected by an admin; the owner may edit it, which puts it back to PENDING. */
    REJECTED,
    /** Temporarily blocked by an admin; hidden from search. */
    SUSPENDED
}
