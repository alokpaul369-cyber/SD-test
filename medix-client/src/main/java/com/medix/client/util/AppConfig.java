package com.medix.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Locale;
import java.util.Properties;

/**
 * Client configuration loaded from client.properties on the classpath.
 * Any key can be overridden with a JVM system property, e.g. -Dapi.mode=http.
 */
public record AppConfig(ApiMode apiMode, URI serverUrl) {

    public enum ApiMode { MOCK, HTTP }

    private static final String RESOURCE = "/client.properties";

    public static AppConfig load() {
        var props = new Properties();
        try (InputStream in = AppConfig.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException(RESOURCE + " not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + RESOURCE, e);
        }

        String mode = value(props, "api.mode", "mock");
        String url = value(props, "server.url", "http://localhost:8080");

        var apiMode = switch (mode.trim().toLowerCase(Locale.ROOT)) {
            case "mock" -> ApiMode.MOCK;
            case "http" -> ApiMode.HTTP;
            default -> throw new IllegalStateException("api.mode must be 'mock' or 'http' but was: " + mode);
        };
        return new AppConfig(apiMode, URI.create(url.trim()));
    }

    private static String value(Properties props, String key, String fallback) {
        return System.getProperty(key, props.getProperty(key, fallback));
    }
}
