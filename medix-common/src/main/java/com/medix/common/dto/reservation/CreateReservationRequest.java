package com.medix.common.dto.reservation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Objects;

/**
 * Customer reserves one or more medicines at a single APPROVED pharmacy.
 * Stock is held immediately; the reservation expires if not picked up in time.
 *
 * @param items 1-10 lines, each medicine at most once
 * @param note  optional note for the pharmacist
 */
public record CreateReservationRequest(
        @NotNull Long pharmacyId,
        @NotEmpty @Size(max = 10) List<@NotNull @Valid ReservationItemRequest> items,
        @Size(max = 500) String note
) {

    public CreateReservationRequest {
        items = items == null ? null : List.copyOf(items);
    }

    @JsonIgnore
    @AssertTrue(message = "Each medicine may appear only once per reservation")
    public boolean isMedicinesUnique() {
        if (items == null) {
            return true;
        }
        List<Long> ids = items.stream().filter(Objects::nonNull).map(ReservationItemRequest::medicineId).toList();
        return ids.stream().distinct().count() == ids.size();
    }
}
