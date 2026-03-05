package com.marcosmoreiradev.uens_backend.support;

import com.marcosmoreiradev.uensbackend.config.properties.JwtProperties;
import com.marcosmoreiradev.uensbackend.security.jwt.JwtTokenService;
import tools.jackson.databind.ObjectMapper;

public final class MockJwtFactory {
/**
 * Construye la instancia de MockJwtFactory para operar en el modulo core.
 * Contexto: capa core con dependencias inyectadas segun la arquitectura modular UENS.
 */

    private MockJwtFactory() {
    }

/**
 * Implementa la operacion 'tokenService' del modulo core en la capa core.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param secret clave secreta HMAC usada para firmar o verificar tokens JWT
     * @param expirationSeconds duracion en segundos para expiracion del token JWT generado en pruebas
     * @param issuer emisor esperado en el claim iss del JWT para validar origen del token
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static JwtTokenService tokenService(String secret, long expirationSeconds, String issuer) {
        return new JwtTokenService(new JwtProperties(secret, expirationSeconds, issuer), new ObjectMapper());
    }

/**
 * Implementa la operacion 'adminToken' del modulo core en la capa core.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static String adminToken() {
        JwtTokenService tokenService = tokenService("test-secret-1234567890", 3600, "uens-test");
        return tokenService.generateToken("admin", "ADMIN");
    }
}
