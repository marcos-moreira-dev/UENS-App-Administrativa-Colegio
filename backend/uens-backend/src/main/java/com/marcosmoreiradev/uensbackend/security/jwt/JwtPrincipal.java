package com.marcosmoreiradev.uensbackend.security.jwt;

import java.time.Instant;

/**
 * Define la responsabilidad de JwtPrincipal dentro del backend UENS.
 * Contexto: modulo core, capa security, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */

public record JwtPrincipal(
        String subject,
        String role,
        Instant issuedAt,
        Instant expiresAt
) {}
