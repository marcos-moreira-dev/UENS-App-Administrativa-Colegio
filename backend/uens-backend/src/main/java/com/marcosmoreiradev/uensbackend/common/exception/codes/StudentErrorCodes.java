package com.marcosmoreiradev.uensbackend.common.exception.codes;

import org.springframework.http.HttpStatus;

/**
 * Define la responsabilidad de StudentErrorCodes dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */

public enum StudentErrorCodes implements ErrorCode {

    RN_EST_01_ESTUDIANTE_DUPLICADO("RN-EST-01-ESTUDIANTE_DUPLICADO", HttpStatus.CONFLICT, "Ya existe un estudiante con esos datos."),
    RN_EST_02_CAMBIO_SECCION_NO_PERMITIDO("RN-EST-02-CAMBIO_SECCION_NO_PERMITIDO", HttpStatus.CONFLICT, "No se puede cambiar la seccion vigente."),
    RN_EST_03_SECCION_NO_DISPONIBLE("RN-EST-03-SECCION_NO_DISPONIBLE", HttpStatus.CONFLICT, "La seccion no esta disponible."),
    RN_EST_04_CUPO_SECCION_AGOTADO("RN-EST-04-CUPO_SECCION_AGOTADO", HttpStatus.CONFLICT, "La seccion no tiene cupo disponible.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;

    StudentErrorCodes(String code, HttpStatus httpStatus, String defaultMessage) {
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

