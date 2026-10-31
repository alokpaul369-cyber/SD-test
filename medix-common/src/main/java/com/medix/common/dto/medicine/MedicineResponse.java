package com.medix.common.dto.medicine;

import com.medix.common.enums.DosageForm;

/**
 * A medicine in the global catalog.
 */
public record MedicineResponse(
        Long id,
        String name,
        String genericName,
        String manufacturer,
        DosageForm dosageForm,
        String strength,
        String category,
        boolean prescriptionRequired,
        String description
) {

    public MedicineSuggestion toSuggestion() {
        return new MedicineSuggestion(id, name, genericName, dosageForm, strength);
    }
}
