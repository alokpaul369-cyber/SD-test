package com.medix.client.api.service;

import com.medix.client.util.AppConfig;

/**
 * Creates a {@link ServiceBundle} for one api.mode. Implementations are discovered with
 * {@link java.util.ServiceLoader}: list the class name in
 * {@code META-INF/services/com.medix.client.api.service.ServiceProvider}.
 * Implementations need a public no-arg constructor.
 */
public interface ServiceProvider {

    /** The api.mode this provider serves. */
    AppConfig.ApiMode mode();

    ServiceBundle create(AppConfig config);
}
