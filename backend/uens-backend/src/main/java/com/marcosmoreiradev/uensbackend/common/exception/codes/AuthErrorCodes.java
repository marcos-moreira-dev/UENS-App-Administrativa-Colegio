package com.marcosmoreiradev.uensbackend.common.exception.codes;

import org.springframework.http.HttpStatus;

/**
 * Define la responsabilidad de AuthErrorCodes dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */

public enum AuthErrorCodes implements ErrorCode {

    AUTH_01_CREDENCIALES_INVALIDAS("AUTH-01-CREDENCIALES_INVALIDAS", HttpStatus.UNAUTHORIZED, "Credenciales invalidas."),
    AUTH_02_TOKEN_INVALIDO("AUTH-02-TOKEN_INVALIDO", HttpStatus.UNAUTHORIZED, "Token invalido."),
    AUTH_03_TOKEN_EXPIRADO("AUTH-03-TOKEN_EXPIRADO", HttpStatus.UNAUTHORIZED, "Token expirado."),
    AUTH_04_SIN_PERMISOS("AUTH-04-SIN_PERMISOS", HttpStatus.FORBIDDEN, "No tiene permisos para este recurso."),
    AUTH_05_USUARIO_INACTIVO("AUTH-05-USUARIO_INACTIVO", HttpStatus.UNAUTHORIZED, "El usuario esta inactivo."),
    AUTH_06_LOGIN_TEMPORALMENTE_BLOQUEADO("AUTH-06-LOGIN_TEMPORALMENTE_BLOQUEADO", HttpStatus.TOO_MANY_REQUESTS, "Demasiados intentos fallidos. Espere antes de reintentar."),
    AUTH_07_RATE_LIMIT_LOGIN_EXCEDIDO("AUTH-07-RATE_LIMIT_LOGIN_EXCEDIDO", HttpStatus.TOO_MANY_REQUESTS, "Se excedio el limite temporal de intentos de inicio de sesion."),
    AUTH_08_REFRESH_TOKEN_INVALIDO("AUTH-08-REFRESH_TOKEN_INVALIDO", HttpStatus.UNAUTHORIZED, "Refresh token invalido."),
    AUTH_09_REFRESH_TOKEN_EXPIRADO("AUTH-09-REFRESH_TOKEN_EXPIRADO", HttpStatus.UNAUTHORIZED, "Refresh token expirado."),
    AUTH_10_ACCESO_DENEGADO("AUTH-10-ACCESO_DENEGADO", HttpStatus.FORBIDDEN, "Acceso denegado.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;

    AuthErrorCodes(String code, HttpStatus httpStatus, String defaultMessage) {
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

/**
 * Implementa la operacion 'fromCode' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param rawCode dato de entrada relevante para ejecutar esta operacion: 'rawCode'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static AuthErrorCodes fromCode(String rawCode) {
        if (rawCode == null || rawCode.isBlank()) {
            return AUTH_02_TOKEN_INVALIDO;
        }

        for (AuthErrorCodes value : values()) {
            if (value.code.equals(rawCode)) {
                return value;
            }
        }

        return AUTH_02_TOKEN_INVALIDO;
    }
}
