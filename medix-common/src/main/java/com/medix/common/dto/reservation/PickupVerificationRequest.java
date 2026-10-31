package com.medix.common.dto.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Pharmacy owner enters the customer's pickup code to complete the reservation.
 */
public record PickupVerificationRequest(
        @NotBlank @Pattern(regexp = "^[A-Za-z2-9]{6}$", message = "Pickup code is 6 letters/digits") String pickupCode
) {
}
