package com.marcosmoreiradev.uensbackend.common.exception.codes;

import org.springframework.http.HttpStatus;

/**
 * Define la responsabilidad de ClaseErrorCodes dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */

public enum ClaseErrorCodes implements ErrorCode {

    RN_CLA_01_REGISTRO_DUPLICADO("RN-CLA-01-REGISTRO_DUPLICADO", HttpStatus.CONFLICT, "Ya existe una clase con la misma combinacion operativa."),
    RN_CLA_02_CONTEXTO_ACADEMICO_INVALIDO("RN-CLA-02-CONTEXTO_ACADEMICO_INVALIDO", HttpStatus.CONFLICT, "La relacion academica seccion-asignatura-docente no es valida."),
    RN_CLA_03_SOLAPAMIENTO_HORARIO_DOCENTE("RN-CLA-03-SOLAPAMIENTO_HORARIO_DOCENTE", HttpStatus.CONFLICT, "El docente ya tiene una clase en ese horario."),
    RN_CLA_04_SOLAPAMIENTO_HORARIO_SECCION("RN-CLA-04-SOLAPAMIENTO_HORARIO_SECCION", HttpStatus.CONFLICT, "La seccion ya tiene una clase en ese horario."),
    RN_CLA_05_TRANSICION_ESTADO_INVALIDA("RN-CLA-05-TRANSICION_ESTADO_INVALIDA", HttpStatus.CONFLICT, "La transicion de estado de la clase no es valida."),
    RN_CLA_06_SECCION_NO_DISPONIBLE("RN-CLA-06-SECCION_NO_DISPONIBLE", HttpStatus.CONFLICT, "La seccion asociada no esta disponible."),
    RN_CLA_07_ASIGNATURA_NO_DISPONIBLE("RN-CLA-07-ASIGNATURA_NO_DISPONIBLE", HttpStatus.CONFLICT, "La asignatura no esta disponible."),
    RN_CLA_08_DOCENTE_NO_DISPONIBLE("RN-CLA-08-DOCENTE_NO_DISPONIBLE", HttpStatus.CONFLICT, "El docente no esta disponible.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ClaseErrorCodes(String code, HttpStatus httpStatus, String defaultMessage) {
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
