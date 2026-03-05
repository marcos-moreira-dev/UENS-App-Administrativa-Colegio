package com.marcosmoreiradev.uens_backend.security.jwt;

import com.marcosmoreiradev.uensbackend.config.properties.JwtProperties;
import com.marcosmoreiradev.uensbackend.security.jwt.JwtExpiredException;
import com.marcosmoreiradev.uensbackend.security.jwt.JwtInvalidException;
import com.marcosmoreiradev.uensbackend.security.jwt.JwtPrincipal;
import com.marcosmoreiradev.uensbackend.security.jwt.JwtTokenService;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Define la responsabilidad de JwtTokenServiceTest dentro del backend UENS.
 * Contexto: modulo core, capa security, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */

class JwtTokenServiceTest {

    @Test
    void generateAndParseValidToken() throws Exception {
        JwtTokenService service = new JwtTokenService(
                new JwtProperties("secret-123456", 3600, "uens-test"),
                new ObjectMapper()
        );

        String token = service.generateToken("admin", "ADMIN");
        JwtPrincipal principal = service.parseAndValidate(token);

        assertNotNull(token);
        assertEquals("admin", principal.subject());
        assertEquals("ADMIN", principal.role());
        assertTrue(principal.expiresAt().isAfter(principal.issuedAt()));
    }

    @Test
    void parseFailsWhenSignatureIsModified() {
        JwtTokenService service = new JwtTokenService(
                new JwtProperties("secret-123456", 3600, "uens-test"),
                new ObjectMapper()
        );

        String token = service.generateToken("admin", "ADMIN");
        String[] parts = token.split("\\.");
        String tampered = parts[0] + "." + parts[1] + "A." + parts[2];

        assertThrows(JwtInvalidException.class, () -> service.parseAndValidate(tampered));
    }

    @Test
    void parseFailsWhenExpired() throws InterruptedException {
        JwtTokenService service = new JwtTokenService(
                new JwtProperties("secret-123456", 1, "uens-test"),
                new ObjectMapper()
        );

        String token = service.generateToken("admin", "ADMIN");
        Thread.sleep(1200);

        assertThrows(JwtExpiredException.class, () -> service.parseAndValidate(token));
    }

    @Test
    void generateFailsWhenRoleIsUnsupported() {
        JwtTokenService service = new JwtTokenService(
                new JwtProperties("secret-123456", 3600, "uens-test"),
                new ObjectMapper()
        );

        assertThrows(IllegalStateException.class, () -> service.generateToken("admin", "OTRO"));
    }
}

