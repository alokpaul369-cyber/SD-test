package com.medix.client.api.service;

import com.medix.client.util.AppConfig;

import java.util.ServiceLoader;

/**
 * Single access point for services in the UI. Call {@link #init(AppConfig)} once at startup;
 * the implementation set (mock or http) is chosen by {@code api.mode} in client.properties.
 * <p>
 * UI code uses e.g. {@code ServiceFactory.search().searchAvailability(query)} inside a SwingWorker,
 * never on the Event Dispatch Thread.
 */
public final class ServiceFactory {

    private static volatile ServiceBundle bundle;

    private ServiceFactory() {
    }

    /**
     * Picks the {@link ServiceProvider} whose mode matches {@code config.apiMode()} and creates its services.
     *
     * @throws IllegalStateException if no provider for the mode is on the classpath
     */
    public static synchronized void init(AppConfig config) {
        ServiceProvider provider = ServiceLoader.load(ServiceProvider.class).stream()
                .map(ServiceLoader.Provider::get)
                .filter(p -> p.mode() == config.apiMode())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No service implementation found for api.mode=" + config.apiMode().name().toLowerCase()
                                + ". Register a ServiceProvider in META-INF/services/" + ServiceProvider.class.getName()));
        bundle = provider.create(config);
    }

    /** Replaces all services; intended for tests. */
    public static synchronized void install(ServiceBundle services) {
        bundle = services;
    }

    public static boolean isInitialized() {
        return bundle != null;
    }

    public static AuthService auth() {
        return bundle().auth();
    }

    public static MedicineService medicine() {
        return bundle().medicine();
    }

    public static SearchService search() {
        return bundle().search();
    }

    public static PharmacyService pharmacy() {
        return bundle().pharmacy();
    }

    public static InventoryService inventory() {
        return bundle().inventory();
    }

    public static ReservationService reservation() {
        return bundle().reservation();
    }

    public static AlertService alert() {
        return bundle().alert();
    }

    public static AdminService admin() {
        return bundle().admin();
    }

    public static GeocodingService geocoding() {
        return bundle().geocoding();
    }

    private static ServiceBundle bundle() {
        ServiceBundle current = bundle;
        if (current == null) {
            throw new IllegalStateException("ServiceFactory.init(config) has not been called");
        }
        return current;
    }
}
