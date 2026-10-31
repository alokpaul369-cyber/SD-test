package com.medix.common.dto.pharmacy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medix.common.enums.PharmacyStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Admin approves, rejects or suspends a pharmacy.
 *
 * @param reason required for REJECTED and SUSPENDED; shown to the owner
 */
public record PharmacyStatusUpdateRequest(
        @NotNull PharmacyStatus status,
        @Size(max = 500) String reason
) {

    @JsonIgnore
    @AssertTrue(message = "A reason is required when rejecting or suspending a pharmacy")
    public boolean isReasonPresentWhenRequired() {
        boolean needsReason = status == PharmacyStatus.REJECTED || status == PharmacyStatus.SUSPENDED;
        return !needsReason || (reason != null && !reason.isBlank());
    }
}
