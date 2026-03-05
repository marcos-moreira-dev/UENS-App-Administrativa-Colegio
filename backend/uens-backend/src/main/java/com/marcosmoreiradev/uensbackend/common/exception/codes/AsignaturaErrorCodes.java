package com.marcosmoreiradev.uensbackend.common.exception.codes;

import org.springframework.http.HttpStatus;

/**
 * Codigos de error estables para reglas de negocio del modulo de asignaturas.
 */
public enum AsignaturaErrorCodes implements ErrorCode {

    RN_ASI_01_REGISTRO_DUPLICADO(
            "RN-ASI-01-REGISTRO_DUPLICADO",
            HttpStatus.CONFLICT,
            "Ya existe una asignatura con el mismo nombre para el mismo grado."
    );

    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;

    AsignaturaErrorCodes(String code, HttpStatus httpStatus, String defaultMessage) {
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
