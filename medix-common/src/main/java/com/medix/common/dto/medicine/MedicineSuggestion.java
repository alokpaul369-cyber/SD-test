package com.medix.common.dto.medicine;

import com.medix.common.enums.DosageForm;

/**
 * Short medicine info for autocomplete, search results, inventory rows and reservation lines.
 */
public record MedicineSuggestion(
        Long id,
        String name,
        String genericName,
        DosageForm dosageForm,
        String strength
) {

    /** e.g. "Napa 500 mg (TABLET)". */
    public String displayName() {
        return name + " " + strength + " (" + dosageForm + ")";
    }
}
