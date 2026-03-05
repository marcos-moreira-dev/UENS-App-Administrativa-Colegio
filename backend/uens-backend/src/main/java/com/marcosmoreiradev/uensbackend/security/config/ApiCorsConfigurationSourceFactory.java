package com.marcosmoreiradev.uensbackend.security.config;

import com.marcosmoreiradev.uensbackend.config.properties.SecurityCorsProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Fabrica la configuracion CORS usada por Spring Security para centralizar una
 * unica politica declarativa y documentable sobre los endpoints HTTP.
 */
@Component
public class ApiCorsConfigurationSourceFactory {

    private final SecurityCorsProperties properties;

    public ApiCorsConfigurationSourceFactory(SecurityCorsProperties properties) {
        this.properties = properties;
    }

    /**
     * Construye la politica CORS del backend.
     *
     * @return configuration source listo para registrarse en Spring Security
     */
    public CorsConfigurationSource create() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        if (!properties.enabled()) {
            return source;
        }

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(properties.allowedOrigins());
        configuration.setAllowedMethods(properties.allowedMethods());
        configuration.setAllowedHeaders(properties.allowedHeaders());
        configuration.setExposedHeaders(properties.exposedHeaders());
        configuration.setAllowCredentials(properties.allowCredentials());
        configuration.setMaxAge(properties.maxAgeSeconds());

        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/v3/api-docs/**", configuration);
        return source;
    }
}
