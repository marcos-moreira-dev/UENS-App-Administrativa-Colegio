package com.marcosmoreiradev.uensbackend.common.exception.codes;

import org.springframework.http.HttpStatus;

/**
 * Define la responsabilidad de ApiErrorCodes dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */

public enum ApiErrorCodes implements ErrorCode {

    // VR-*
    VR_01_REQUEST_INVALIDO("VR-01-REQUEST_INVALIDO", HttpStatus.BAD_REQUEST, "La solicitud contiene errores de validacion."),
    VR_02_PARAMETRO_INVALIDO("VR-02-PARAMETRO_INVALIDO", HttpStatus.BAD_REQUEST, "Parametro invalido."),
    VR_03_CUERPO_JSON_INVALIDO("VR-03-CUERPO_JSON_INVALIDO", HttpStatus.BAD_REQUEST, "Cuerpo JSON invalido."),
    VR_04_FORMATO_INVALIDO("VR-04-FORMATO_INVALIDO", HttpStatus.BAD_REQUEST, "Formato invalido."),
    VR_05_RANGO_NUMERICO_INVALIDO("VR-05-RANGO_NUMERICO_INVALIDO", HttpStatus.BAD_REQUEST, "Rango numerico invalido."),
    VR_06_VALOR_ENUM_INVALIDO("VR-06-VALOR_ENUM_INVALIDO", HttpStatus.BAD_REQUEST, "Valor de enumeracion invalido."),
    VR_07_CAMPO_REQUERIDO("VR-07-CAMPO_REQUERIDO", HttpStatus.BAD_REQUEST, "Falta un campo requerido."),

    // API-*
    API_01_REQUEST_MALFORMADO("API-01-REQUEST_MALFORMADO", HttpStatus.BAD_REQUEST, "La solicitud esta mal formada."),
    API_03_TIPO_CONTENIDO_NO_SOPORTADO("API-03-TIPO_CONTENIDO_NO_SOPORTADO", HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de contenido no soportado."),
    API_04_RECURSO_NO_ENCONTRADO("API-04-RECURSO_NO_ENCONTRADO", HttpStatus.NOT_FOUND, "El recurso solicitado no existe."),
    API_05_METODO_NO_PERMITIDO("API-05-METODO_NO_PERMITIDO", HttpStatus.METHOD_NOT_ALLOWED, "Metodo HTTP no permitido."),
    API_06_RUTA_NO_ENCONTRADA("API-06-RUTA_NO_ENCONTRADA", HttpStatus.NOT_FOUND, "Ruta no encontrada."),
    API_07_CONFLICTO_OPERACION("API-07-CONFLICTO_OPERACION", HttpStatus.CONFLICT, "La operacion no puede completarse por un conflicto de estado o datos."),
    API_08_RESPUESTA_NO_ACEPTABLE("API-08-RESPUESTA_NO_ACEPTABLE", HttpStatus.NOT_ACCEPTABLE, "No se puede generar una respuesta compatible con el encabezado Accept."),
    API_10_ENDPOINT_EN_CONSTRUCCION("API-10-ENDPOINT_EN_CONSTRUCCION", HttpStatus.NOT_IMPLEMENTED, "Endpoint en construccion."),

    // SYS-*
    SYS_01_ERROR_INTERNO("SYS-01-ERROR_INTERNO", HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error interno.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ApiErrorCodes(String code, HttpStatus httpStatus, String defaultMessage) {
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

