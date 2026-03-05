package com.marcosmoreiradev.uensbackend.common.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Define la responsabilidad de ErrorDetailDto dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */
public record ErrorDetailDto(
        String field,
        String code,
        String message,
        Object rejectedValue
) {
/**
 * Construye la instancia de ErrorDetailDto para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param field dato de entrada relevante para ejecutar esta operacion: 'field'
     * @param code dato de entrada relevante para ejecutar esta operacion: 'code'
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
 */
    public ErrorDetailDto(String field, String code, String message) {
        this(field, code, message, null);
    }

/**
 * Implementa la operacion 'of' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param field dato de entrada relevante para ejecutar esta operacion: 'field'
     * @param code dato de entrada relevante para ejecutar esta operacion: 'code'
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static ErrorDetailDto of(String field, String code, String message) {
        return new ErrorDetailDto(field, code, message);
    }

/**
 * Implementa la operacion 'of' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param field dato de entrada relevante para ejecutar esta operacion: 'field'
     * @param code dato de entrada relevante para ejecutar esta operacion: 'code'
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @param rejectedValue dato de entrada relevante para ejecutar esta operacion: 'rejectedValue'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static ErrorDetailDto of(String field, String code, String message, Object rejectedValue) {
        return new ErrorDetailDto(field, code, message, rejectedValue);
    }
}

