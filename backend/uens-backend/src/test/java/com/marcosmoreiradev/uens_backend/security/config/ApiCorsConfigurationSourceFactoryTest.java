package com.marcosmoreiradev.uens_backend.security.config;

import com.marcosmoreiradev.uensbackend.config.properties.SecurityCorsProperties;
import com.marcosmoreiradev.uensbackend.security.config.ApiCorsConfigurationSourceFactory;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiCorsConfigurationSourceFactoryTest {

    @Test
    void createRegistersConfiguredCorsPolicyForApiRoutes() {
        ApiCorsConfigurationSourceFactory factory = new ApiCorsConfigurationSourceFactory(
                new SecurityCorsProperties(
                        true,
                        List.of("http://localhost:5173"),
                        List.of("GET", "POST", "OPTIONS"),
                        List.of("Authorization", "Content-Type"),
                        List.of("X-Request-Id"),
                        false,
                        900L
                )
        );

        CorsConfigurationSource source = factory.create();
        CorsConfiguration configuration = source.getCorsConfiguration(new MockHttpServletRequest("OPTIONS", "/api/v1/auth/login"));

        assertThat(configuration).isNotNull();
        assertThat(configuration.getAllowedOrigins()).containsExactly("http://localhost:5173");
        assertThat(configuration.getAllowedMethods()).containsExactly("GET", "POST", "OPTIONS");
        assertThat(configuration.getAllowedHeaders()).containsExactly("Authorization", "Content-Type");
        assertThat(configuration.getExposedHeaders()).containsExactly("X-Request-Id");
        assertThat(configuration.getAllowCredentials()).isFalse();
        assertThat(configuration.getMaxAge()).isEqualTo(900L);
    }

    @Test
    void createReturnsEmptySourceWhenCorsIsDisabled() {
        ApiCorsConfigurationSourceFactory factory = new ApiCorsConfigurationSourceFactory(
                new SecurityCorsProperties(false, List.of("http://localhost:5173"), null, null, null, false, 900L)
        );

        CorsConfigurationSource source = factory.create();

        assertThat(source.getCorsConfiguration(new MockHttpServletRequest("OPTIONS", "/api/v1/auth/login"))).isNull();
    }
}
