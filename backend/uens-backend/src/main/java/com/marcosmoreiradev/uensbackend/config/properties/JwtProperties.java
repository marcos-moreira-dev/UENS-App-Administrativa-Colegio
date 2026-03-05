package com.marcosmoreiradev.uensbackend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Validated
@ConfigurationProperties(prefix = "app.security.jwt")
/**
 * Define la responsabilidad de JwtProperties dentro del backend UENS.
 * Contexto: modulo core, capa config, arquitectura monolito modular Spring Boot.
 * Alcance: encapsular propiedades tipadas de configuracion por modulo.
 */
public record JwtProperties(
        @NotBlank @DefaultValue("dev-only-secret-change-in-production") String secret,
        @Positive @DefaultValue("3600") long expirationSeconds,
        @DefaultValue("uens-backend") String issuer
) {}

