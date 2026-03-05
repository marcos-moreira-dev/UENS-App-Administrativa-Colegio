package com.marcosmoreiradev.uensbackend.security.jwt;

/**
 * Define la responsabilidad de JwtExpiredException dentro del backend UENS.
 * Contexto: modulo core, capa security, arquitectura monolito modular Spring Boot.
 * Alcance: modelar errores tecnicos o funcionales que se traducen a respuestas API trazables.
 */

public class JwtExpiredException extends Exception {
/**
 * Construye la instancia de JwtExpiredException para operar en el modulo core.
 * Contexto: capa security con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
 */
    public JwtExpiredException(String message) {
        super(message);
    }
}
