package com.medix.common.dto.medicine;

import com.medix.common.enums.DosageForm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Admin creates or updates a catalog medicine. The combination name + strength + dosageForm must be unique.
 *
 * @param strength e.g. "500 mg", "5 mg/5 ml"
 */
public record MedicineRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 150) String genericName,
        @Size(max = 150) String manufacturer,
        @NotNull DosageForm dosageForm,
        @NotBlank @Size(max = 50) String strength,
        @Size(max = 100) String category,
        boolean prescriptionRequired,
        @Size(max = 2000) String description
) {
}
