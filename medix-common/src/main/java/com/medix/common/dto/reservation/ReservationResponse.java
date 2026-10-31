package com.medix.common.dto.reservation;

import com.medix.common.dto.pharmacy.PharmacySummary;
import com.medix.common.enums.ReservationStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * A reservation as seen by the customer, the pharmacy owner and admins.
 *
 * @param pickupCode   6-character code (A-Z, 2-9) the customer shows at the counter
 * @param totalPrice   sum of line totals
 * @param statusReason reason given when cancelled; null otherwise
 * @param expiresAt    when an active reservation will be EXPIRED automatically
 */
public record ReservationResponse(
        Long id,
        String pickupCode,
        ReservationStatus status,
        PharmacySummary pharmacy,
        Long customerId,
        String customerName,
        String customerPhone,
        List<ReservationItemResponse> items,
        BigDecimal totalPrice,
        String note,
        String statusReason,
        Instant createdAt,
        Instant expiresAt,
        Instant updatedAt
) {

    public ReservationResponse {
        items = items == null ? List.of() : List.copyOf(items);
    }
}
