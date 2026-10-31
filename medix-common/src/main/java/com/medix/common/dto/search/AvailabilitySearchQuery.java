package com.medix.common.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medix.common.enums.SearchSort;
import com.medix.common.validation.Latitude;
import com.medix.common.validation.Longitude;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Query parameters of GET /api/search/availability. Either {@code medicineId} or {@code q} is required;
 * medicineId wins when both are given.
 *
 * @param q            medicine name or generic name (fuzzy, min 2 characters)
 * @param medicineId   exact catalog medicine (e.g. picked from autocomplete)
 * @param lat          user latitude
 * @param lng          user longitude
 * @param radiusKm     search radius, 0.5-50 km (default 5)
 * @param openNowOnly  only pharmacies open right now
 * @param minQuantity  only pharmacies with at least this many available (default 1)
 * @param sort         result order (default DISTANCE)
 * @param page         0-based page (default 0)
 * @param size         page size 1-100 (default 20)
 */
public record AvailabilitySearchQuery(
        @Size(min = 2, max = 100) String q,
        Long medicineId,
        @NotNull @Latitude Double lat,
        @NotNull @Longitude Double lng,
        @DecimalMin("0.5") @DecimalMax("50.0") Double radiusKm,
        Boolean openNowOnly,
        @Min(1) Integer minQuantity,
        SearchSort sort,
        @Min(0) Integer page,
        @Min(1) @Max(100) Integer size
) {

    public static final double DEFAULT_RADIUS_KM = 5.0;
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Fills defaults so both client and server see the same effective values. */
    public AvailabilitySearchQuery {
        radiusKm = radiusKm == null ? DEFAULT_RADIUS_KM : radiusKm;
        openNowOnly = openNowOnly != null && openNowOnly;
        minQuantity = minQuantity == null ? 1 : minQuantity;
        sort = sort == null ? SearchSort.DISTANCE : sort;
        page = page == null ? 0 : page;
        size = size == null ? DEFAULT_PAGE_SIZE : size;
    }

    public static AvailabilitySearchQuery byName(String q, double lat, double lng) {
        return new AvailabilitySearchQuery(q, null, lat, lng, null, null, null, null, null, null);
    }

    public static AvailabilitySearchQuery byMedicine(long medicineId, double lat, double lng) {
        return new AvailabilitySearchQuery(null, medicineId, lat, lng, null, null, null, null, null, null);
    }

    @JsonIgnore
    @AssertTrue(message = "Either q or medicineId is required")
    public boolean isMedicineSpecified() {
        return medicineId != null || (q != null && !q.isBlank());
    }
}
