package com.medix.common.dto.pharmacy;

/**
 * Short pharmacy info embedded in search results and reservations.
 */
public record PharmacySummary(
        Long id,
        String name,
        String addressLine,
        String city,
        String phone,
        double latitude,
        double longitude
) {
}
