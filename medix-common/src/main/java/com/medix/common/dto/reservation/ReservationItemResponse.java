package com.medix.common.dto.reservation;

import com.medix.common.dto.medicine.MedicineSuggestion;

import java.math.BigDecimal;

/**
 * One reservation line with the price fixed at reservation time.
 */
public record ReservationItemResponse(
        MedicineSuggestion medicine,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}
