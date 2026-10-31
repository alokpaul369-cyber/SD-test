package com.medix.common.dto.geo;

/**
 * An address match from the server's geocoding proxy (OpenStreetMap Nominatim, cached in Redis).
 *
 * @param displayName full address as returned by the geocoder
 */
public record GeocodeResult(
        String displayName,
        double latitude,
        double longitude,
        String city,
        String postalCode
) {
}
