package com.marcosmoreiradev.uensbackend.security.jwt;

/**
 * Define la responsabilidad de JwtInvalidException dentro del backend UENS.
 * Contexto: modulo core, capa security, arquitectura monolito modular Spring Boot.
 * Alcance: modelar errores tecnicos o funcionales que se traducen a respuestas API trazables.
 */

public class JwtInvalidException extends Exception {
/**
 * Construye la instancia de JwtInvalidException para operar en el modulo core.
 * Contexto: capa security con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
 */

    public JwtInvalidException(String message) {
        super(message);
    }

/**
 * Construye la instancia de JwtInvalidException para operar en el modulo core.
 * Contexto: capa security con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @param cause causa raiz de la excepcion para trazabilidad tecnica
 */
    public JwtInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
