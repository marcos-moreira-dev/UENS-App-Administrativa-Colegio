package com.marcosmoreiradev.uensbackend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;

/**
 * Define encabezados defensivos minimos para el backend API, priorizando una
 * postura segura por defecto y manteniendo HSTS como una capacidad opt-in.
 */
@Validated
@ConfigurationProperties(prefix = "app.security.headers")
public record SecurityHeadersProperties(
        Boolean hstsEnabled,
        @Min(60) Long hstsMaxAgeSeconds,
        Boolean hstsIncludeSubdomains,
        String referrerPolicy,
        String permissionsPolicy
) {

    public SecurityHeadersProperties {
        hstsEnabled = hstsEnabled == null ? Boolean.FALSE : hstsEnabled;
        hstsMaxAgeSeconds = hstsMaxAgeSeconds == null ? 31536000L : hstsMaxAgeSeconds;
        hstsIncludeSubdomains = hstsIncludeSubdomains == null ? Boolean.TRUE : hstsIncludeSubdomains;
        referrerPolicy = hasText(referrerPolicy) ? referrerPolicy.trim() : "no-referrer";
        permissionsPolicy = hasText(permissionsPolicy)
                ? permissionsPolicy.trim()
                : "camera=(), microphone=(), geolocation=(), payment=(), usb=()";
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
