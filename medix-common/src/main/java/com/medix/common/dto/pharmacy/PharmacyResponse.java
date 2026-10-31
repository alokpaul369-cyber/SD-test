package com.medix.common.dto.pharmacy;

import com.medix.common.enums.PharmacyStatus;

import java.time.Instant;
import java.util.List;

/**
 * Full pharmacy details.
 *
 * @param openNow      computed by the server at response time in the pharmacy time zone
 * @param statusReason admin note for REJECTED/SUSPENDED, otherwise null
 */
public record PharmacyResponse(
        Long id,
        String name,
        String licenseNumber,
        String phone,
        String email,
        String addressLine,
        String city,
        String postalCode,
        double latitude,
        double longitude,
        PharmacyStatus status,
        String statusReason,
        boolean open24Hours,
        List<OpeningHours> openingHours,
        boolean openNow,
        Long ownerId,
        String ownerName,
        Instant createdAt,
        Instant updatedAt
) {

    public PharmacyResponse {
        openingHours = openingHours == null ? List.of() : List.copyOf(openingHours);
    }

    public PharmacySummary toSummary() {
        return new PharmacySummary(id, name, addressLine, city, phone, latitude, longitude);
    }
}
