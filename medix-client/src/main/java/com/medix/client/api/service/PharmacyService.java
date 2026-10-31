package com.medix.client.api.service;

import com.medix.common.dto.pharmacy.PharmacyRequest;
import com.medix.common.dto.pharmacy.PharmacyResponse;

import java.util.List;

/**
 * Pharmacy profiles. Admin status changes are in {@link AdminService}.
 * <p>
 * All methods block and throw {@link ApiException} on failure: call them from a SwingWorker, never on the EDT.
 */
public interface PharmacyService {

    /**
     * Returns a pharmacy. Non-APPROVED pharmacies are visible only to their owner and admins (else NOT_FOUND).
     * <p>{@code GET /api/pharmacies/{id}} – public
     */
    PharmacyResponse get(long id);

    /**
     * Lists the current owner's pharmacies (any status).
     * <p>{@code GET /api/pharmacies/mine} – PHARMACY_OWNER
     */
    List<PharmacyResponse> myPharmacies();

    /**
     * Registers an additional pharmacy for the current owner, in status PENDING.
     * <p>{@code POST /api/pharmacies} – PHARMACY_OWNER
     */
    PharmacyResponse create(PharmacyRequest request);

    /**
     * Updates a pharmacy profile. A REJECTED pharmacy goes back to PENDING.
     * <p>{@code PUT /api/pharmacies/{id}} – PHARMACY_OWNER (own pharmacy), ADMIN
     */
    PharmacyResponse update(long id, PharmacyRequest request);
}
