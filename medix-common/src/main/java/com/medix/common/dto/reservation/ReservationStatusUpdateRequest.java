package com.medix.common.dto.reservation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medix.common.enums.ReservationStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Pharmacy owner moves a reservation to CONFIRMED, READY_FOR_PICKUP or CANCELLED.
 * (COMPLETED goes through pickup-code verification; EXPIRED is set by the scheduler.)
 *
 * @param reason required for CANCELLED; shown to the customer
 */
public record ReservationStatusUpdateRequest(
        @NotNull ReservationStatus status,
        @Size(max = 500) String reason
) {

    @JsonIgnore
    @AssertTrue(message = "status must be CONFIRMED, READY_FOR_PICKUP or CANCELLED")
    public boolean isOwnerSettableStatus() {
        return status == null
                || status == ReservationStatus.CONFIRMED
                || status == ReservationStatus.READY_FOR_PICKUP
                || status == ReservationStatus.CANCELLED;
    }

    @JsonIgnore
    @AssertTrue(message = "A reason is required when cancelling a reservation")
    public boolean isReasonPresentWhenRequired() {
        return status != ReservationStatus.CANCELLED || (reason != null && !reason.isBlank());
    }
}
