package com.marcosmoreiradev.uensbackend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Encapsula la politica CORS para clientes web presentes o futuros sin afectar
 * al desktop JavaFX, que no depende del modelo de seguridad del navegador.
 */
@Validated
@ConfigurationProperties(prefix = "app.security.cors")
public record SecurityCorsProperties(
        Boolean enabled,
        List<String> allowedOrigins,
        List<String> allowedMethods,
        List<String> allowedHeaders,
        List<String> exposedHeaders,
        Boolean allowCredentials,
        Long maxAgeSeconds
) {

    public SecurityCorsProperties {
        enabled = enabled == null ? Boolean.TRUE : enabled;
        allowedOrigins = sanitize(allowedOrigins == null ? List.of() : allowedOrigins);
        allowedMethods = sanitize(allowedMethods == null
                ? List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                : allowedMethods);
        allowedHeaders = sanitize(allowedHeaders == null
                ? List.of("Authorization", "Content-Type", "Accept", "X-Request-Id")
                : allowedHeaders);
        exposedHeaders = sanitize(exposedHeaders == null
                ? List.of("X-Request-Id", "Content-Disposition")
                : exposedHeaders);
        allowCredentials = allowCredentials == null ? Boolean.FALSE : allowCredentials;
        maxAgeSeconds = maxAgeSeconds == null ? 1800L : maxAgeSeconds;
    }

    private static List<String> sanitize(List<String> values) {
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toList();
    }
}
