package com.marcosmoreiradev.uensbackend.common.exception.codes;

import org.springframework.http.HttpStatus;

/**
 * Codigos de error estables para el modulo de usuarios administrativos.
 */
public enum UsuarioErrorCodes implements ErrorCode {

    RN_USR_01_LOGIN_DUPLICADO(
            "RN-USR-01-LOGIN_DUPLICADO",
            HttpStatus.CONFLICT,
            "Ya existe un usuario del sistema con el mismo login."
    );

    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;

    UsuarioErrorCodes(String code, HttpStatus httpStatus, String defaultMessage) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }
}
