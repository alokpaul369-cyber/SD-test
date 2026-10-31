package com.medix.common.dto.reservation;

import jakarta.validation.constraints.Size;

/**
 * Customer cancels their own active reservation.
 *
 * @param reason optional reason shown to the pharmacy
 */
public record CancelReservationRequest(
        @Size(max = 500) String reason
) {
}
