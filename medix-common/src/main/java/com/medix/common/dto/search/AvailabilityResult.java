package com.medix.common.dto.search;

import com.medix.common.dto.medicine.MedicineSuggestion;
import com.medix.common.dto.pharmacy.PharmacySummary;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * One pharmacy that has the searched medicine in stock.
 *
 * @param distanceKm        straight-line distance from the user, rounded to 2 decimals
 * @param availableQuantity on-hand minus reserved, summed over all non-expired batches
 * @param price             unit price (lowest price among the pharmacy's non-expired batches)
 * @param openNow           pharmacy is open at the time of the search
 * @param nearestExpiry     earliest expiry date among the counted batches
 * @param lastUpdated       when this pharmacy's stock for the medicine last changed
 */
public record AvailabilityResult(
        PharmacySummary pharmacy,
        MedicineSuggestion medicine,
        double distanceKm,
        int availableQuantity,
        BigDecimal price,
        boolean openNow,
        LocalDate nearestExpiry,
        Instant lastUpdated
) {
}
