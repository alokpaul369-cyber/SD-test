package com.medix.common.dto.pharmacy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medix.common.validation.Latitude;
import com.medix.common.validation.Longitude;
import com.medix.common.validation.PhoneNumber;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Objects;

/**
 * Create or update a pharmacy. Updating an APPROVED pharmacy keeps it APPROVED;
 * updating a REJECTED one puts it back to PENDING.
 *
 * @param openingHours one entry per day (up to 7, no duplicate days); ignored when open24Hours is true
 */
public record PharmacyRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 50) String licenseNumber,
        @NotBlank @PhoneNumber String phone,
        @Email @Size(max = 254) String email,
        @NotBlank @Size(max = 255) String addressLine,
        @NotBlank @Size(max = 100) String city,
        @Size(max = 20) String postalCode,
        @NotNull @Latitude Double latitude,
        @NotNull @Longitude Double longitude,
        boolean open24Hours,
        @NotNull @Size(max = 7) List<@NotNull @Valid OpeningHours> openingHours
) {

    public PharmacyRequest {
        openingHours = openingHours == null ? null : List.copyOf(openingHours);
    }

    @JsonIgnore
    @AssertTrue(message = "Each day may appear only once in openingHours")
    public boolean isOpeningDaysUnique() {
        if (openingHours == null) {
            return true;
        }
        List<DayOfWeek> days = openingHours.stream().filter(Objects::nonNull).map(OpeningHours::day).toList();
        return days.stream().distinct().count() == days.size();
    }
}
