package com.marcosmoreiradev.uensbackend.common.exception.codes;

import org.springframework.http.HttpStatus;

/**
 * Define la responsabilidad de CalificacionErrorCodes dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */

public enum CalificacionErrorCodes implements ErrorCode {

    RN_CAL_01_CONTEXTO_ACADEMICO_INVALIDO("RN-CAL-01-CONTEXTO_ACADEMICO_INVALIDO", HttpStatus.CONFLICT, "El contexto academico de la calificacion no es valido."),
    RN_CAL_02_REGISTRO_DUPLICADO("RN-CAL-02-REGISTRO_DUPLICADO", HttpStatus.CONFLICT, "Ya existe una calificacion para ese estudiante, clase y parcial."),
    RN_CAL_03_EDICION_NO_PERMITIDA("RN-CAL-03-EDICION_NO_PERMITIDA", HttpStatus.CONFLICT, "La calificacion no puede editarse en el estado o periodo actual."),
    RN_CAL_04_CIERRE_DE_PERIODO("RN-CAL-04-CIERRE_DE_PERIODO", HttpStatus.CONFLICT, "No se permiten cambios de calificaciones en un periodo cerrado."),
    RN_CAL_05_ESTADO_NO_PERMITE_OPERACION("RN-CAL-05-ESTADO_NO_PERMITE_OPERACION", HttpStatus.CONFLICT, "El estado actual no permite registrar o modificar la calificacion.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;

    CalificacionErrorCodes(String code, HttpStatus httpStatus, String defaultMessage) {
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

