package com.marcosmoreiradev.uensbackend.common.exception.codes;

import org.springframework.http.HttpStatus;

/**
 * Define la responsabilidad de ErrorCode dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */

public interface ErrorCode {
/**
 * Implementa la operacion 'code' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return salida util para continuar con la capa llamadora.
 */
    String code();
/**
 * Implementa la operacion 'httpStatus' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return salida util para continuar con la capa llamadora.
 */
    HttpStatus httpStatus();
/**
 * Implementa la operacion 'defaultMessage' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return salida util para continuar con la capa llamadora.
 */
    String defaultMessage();
}
