package com.medix.client.api.service;

import com.medix.common.dto.PageResponse;
import com.medix.common.dto.medicine.MedicineRequest;
import com.medix.common.dto.medicine.MedicineResponse;
import com.medix.common.dto.medicine.MedicineSuggestion;

import java.util.List;

/**
 * The global medicine catalog.
 * <p>
 * All methods block and throw {@link ApiException} on failure: call them from a SwingWorker, never on the EDT.
 */
public interface MedicineService {

    /**
     * Autocomplete by name or generic name (prefix + fuzzy match).
     * <p>{@code GET /api/medicines/suggest?q={prefix}&limit={limit}} – public
     *
     * @param prefix at least 2 characters
     * @param limit  1-20
     */
    List<MedicineSuggestion> suggest(String prefix, int limit);

    /**
     * Lists the catalog, optionally filtered.
     * <p>{@code GET /api/medicines?q={q}&page={page}&size={size}} – public
     *
     * @param q optional name/generic name/manufacturer contains; null for all
     */
    PageResponse<MedicineResponse> list(String q, int page, int size);

    /**
     * Returns one medicine.
     * <p>{@code GET /api/medicines/{id}} – public
     */
    MedicineResponse get(long id);

    /**
     * Adds a medicine to the catalog.
     * <p>{@code POST /api/medicines} – ADMIN
     */
    MedicineResponse create(MedicineRequest request);

    /**
     * Updates a catalog medicine.
     * <p>{@code PUT /api/medicines/{id}} – ADMIN
     */
    MedicineResponse update(long id, MedicineRequest request);

    /**
     * Deletes a catalog medicine that is not used in any inventory or reservation (else MEDICINE_IN_USE).
     * <p>{@code DELETE /api/medicines/{id}} – ADMIN
     */
    void delete(long id);
}
