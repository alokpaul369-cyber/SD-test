package com.medix.client.api.service;

import com.medix.common.dto.PageResponse;
import com.medix.common.dto.pharmacy.NearbyPharmacyResponse;
import com.medix.common.dto.search.AvailabilityResult;
import com.medix.common.dto.search.AvailabilitySearchQuery;

import java.util.List;

/**
 * Medicine availability and nearby pharmacy search.
 * <p>
 * All methods block and throw {@link ApiException} on failure: call them from a SwingWorker, never on the EDT.
 */
public interface SearchService {

    /**
     * Finds APPROVED pharmacies within the radius that have the medicine available.
     * <p>{@code GET /api/search/availability?q=&medicineId=&lat=&lng=&radiusKm=&openNowOnly=&minQuantity=&sort=&page=&size=} – public
     */
    PageResponse<AvailabilityResult> searchAvailability(AvailabilitySearchQuery query);

    /**
     * Lists APPROVED pharmacies within the radius, nearest first (max 100).
     * <p>{@code GET /api/search/pharmacies/nearby?lat={lat}&lng={lng}&radiusKm={radiusKm}} – public
     *
     * @param radiusKm 0.5-50
     */
    List<NearbyPharmacyResponse> nearbyPharmacies(double lat, double lng, double radiusKm);
}
