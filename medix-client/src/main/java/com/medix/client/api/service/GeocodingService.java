package com.medix.client.api.service;

import com.medix.common.dto.geo.GeocodeResult;

import java.util.List;
import java.util.Optional;

/**
 * Address lookup through the server's geocoding proxy (OpenStreetMap Nominatim, cached in Redis).
 * The client never calls Nominatim directly.
 * <p>
 * All methods block and throw {@link ApiException} on failure: call them from a SwingWorker, never on the EDT.
 */
public interface GeocodingService {

    /**
     * Finds up to 5 places matching an address or place name.
     * <p>{@code GET /api/geo/search?q={query}} – public (rate limited)
     *
     * @param query at least 3 characters
     */
    List<GeocodeResult> search(String query);

    /**
     * Finds the address at a coordinate, if any.
     * <p>{@code GET /api/geo/reverse?lat={lat}&lng={lng}} – public (rate limited); 404 NOT_FOUND maps to an empty Optional
     */
    Optional<GeocodeResult> reverse(double lat, double lng);
}
