package com.marcosmoreiradev.uensbackend.common.exception.codes;

import org.springframework.http.HttpStatus;

/**
 * Define la responsabilidad de SeccionErrorCodes dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */

public enum SeccionErrorCodes implements ErrorCode {

    RN_SEC_01_CUPO_AGOTADO("RN-SEC-01-CUPO_AGOTADO", HttpStatus.CONFLICT, "La seccion no tiene cupos disponibles."),
    RN_SEC_02_SECCION_NO_DISPONIBLE("RN-SEC-02-SECCION_NO_DISPONIBLE", HttpStatus.CONFLICT, "La seccion no esta disponible."),
    RN_SEC_03_TRANSICION_ESTADO_INVALIDA("RN-SEC-03-TRANSICION_ESTADO_INVALIDA", HttpStatus.CONFLICT, "La transicion de estado de la seccion no es valida."),
    RN_SEC_04_SECCION_DUPLICADA("RN-SEC-04-SECCION_DUPLICADA", HttpStatus.CONFLICT, "Ya existe una seccion con esa combinacion.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;

    SeccionErrorCodes(String code, HttpStatus httpStatus, String defaultMessage) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    @Override
/**
 * Implementa la operacion 'code' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String code() {
        return code;
    }

    @Override
/**
 * Implementa la operacion 'httpStatus' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
/**
 * Implementa la operacion 'defaultMessage' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String defaultMessage() {
        return defaultMessage;
    }
}

