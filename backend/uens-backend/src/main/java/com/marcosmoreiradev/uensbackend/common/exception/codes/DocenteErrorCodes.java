package com.marcosmoreiradev.uensbackend.common.exception.codes;

import org.springframework.http.HttpStatus;

/**
 * Codigos de error estables para reglas de negocio del modulo de docentes.
 */
public enum DocenteErrorCodes implements ErrorCode {

    RN_DOC_01_CORREO_DUPLICADO(
            "RN-DOC-01-CORREO_DUPLICADO",
            HttpStatus.CONFLICT,
            "Ya existe un docente con el mismo correo."
    );

    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;

    DocenteErrorCodes(String code, HttpStatus httpStatus, String defaultMessage) {
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
