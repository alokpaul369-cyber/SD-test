package com.medix.common.dto.reservation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * One reservation line.
 *
 * @param quantity 1-20 units
 */
public record ReservationItemRequest(
        @NotNull Long medicineId,
        @Positive @Max(20) int quantity
) {
}
