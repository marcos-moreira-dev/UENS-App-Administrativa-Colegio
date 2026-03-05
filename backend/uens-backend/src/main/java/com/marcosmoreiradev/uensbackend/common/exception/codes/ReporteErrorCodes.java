package com.marcosmoreiradev.uensbackend.common.exception.codes;

import org.springframework.http.HttpStatus;

/**
 * Define la responsabilidad de ReporteErrorCodes dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */

public enum ReporteErrorCodes implements ErrorCode {

    RN_REP_01_TIPO_NO_HABILITADO_V1("RN-REP-01-TIPO_NO_HABILITADO_V1", HttpStatus.NOT_IMPLEMENTED, "El tipo de reporte solicitado aun no esta habilitado en V1."),
    RN_REP_02_RESULTADO_NO_LISTO("RN-REP-02-RESULTADO_NO_LISTO", HttpStatus.CONFLICT, "El resultado del reporte aun no esta disponible."),
    RN_REP_03_SOLICITUD_NO_CANCELABLE("RN-REP-03-SOLICITUD_NO_CANCELABLE", HttpStatus.CONFLICT, "La solicitud no puede cancelarse en su estado actual."),
    RN_REP_04_REINTENTO_NO_PERMITIDO("RN-REP-04-REINTENTO_NO_PERMITIDO", HttpStatus.CONFLICT, "El reintento no esta permitido para la solicitud actual."),
    RN_REP_05_TRANSICION_ESTADO_INVALIDA("RN-REP-05-TRANSICION_ESTADO_INVALIDA", HttpStatus.CONFLICT, "La transicion de estado del reporte no es valida."),
    RN_REP_06_ESTADO_REPORTE_NO_PERMITE_OPERACION("RN-REP-06-ESTADO_REPORTE_NO_PERMITE_OPERACION", HttpStatus.CONFLICT, "El estado del reporte no permite ejecutar esta operacion."),
    SYS_REP_01_ERROR_WORKER("SYS-REP-01-ERROR_WORKER", HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error interno en el worker de reportes."),
    SYS_REP_02_FALLO_PROCESAMIENTO("SYS-REP-02-FALLO_PROCESAMIENTO", HttpStatus.INTERNAL_SERVER_ERROR, "Fallo el procesamiento interno de la solicitud de reporte.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ReporteErrorCodes(String code, HttpStatus httpStatus, String defaultMessage) {
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

