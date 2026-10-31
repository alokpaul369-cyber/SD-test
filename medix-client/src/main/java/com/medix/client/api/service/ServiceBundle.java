package com.medix.client.api.service;

import java.util.Objects;

/**
 * One complete set of service implementations (all mock or all http).
 */
public record ServiceBundle(
        AuthService auth,
        MedicineService medicine,
        SearchService search,
        PharmacyService pharmacy,
        InventoryService inventory,
        ReservationService reservation,
        AlertService alert,
        AdminService admin,
        GeocodingService geocoding
) {

    public ServiceBundle {
        Objects.requireNonNull(auth, "auth");
        Objects.requireNonNull(medicine, "medicine");
        Objects.requireNonNull(search, "search");
        Objects.requireNonNull(pharmacy, "pharmacy");
        Objects.requireNonNull(inventory, "inventory");
        Objects.requireNonNull(reservation, "reservation");
        Objects.requireNonNull(alert, "alert");
        Objects.requireNonNull(admin, "admin");
        Objects.requireNonNull(geocoding, "geocoding");
    }
}
