package com.medix.common.dto.pharmacy;

/**
 * An APPROVED pharmacy within the requested radius.
 *
 * @param distanceKm straight-line distance from the search point, rounded to 2 decimals
 */
public record NearbyPharmacyResponse(
        PharmacySummary pharmacy,
        double distanceKm,
        boolean openNow
) {
}
