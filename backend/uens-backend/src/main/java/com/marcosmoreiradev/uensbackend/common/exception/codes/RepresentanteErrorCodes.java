package com.marcosmoreiradev.uensbackend.common.exception.codes;

import org.springframework.http.HttpStatus;

/**
 * Codigos de error estables para reglas de negocio del modulo de representantes.
 */
public enum RepresentanteErrorCodes implements ErrorCode {

    RN_RLG_01_CORREO_DUPLICADO(
            "RN-RLG-01-CORREO_DUPLICADO",
            HttpStatus.CONFLICT,
            "Ya existe un representante con el mismo correo."
    );

    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;

    RepresentanteErrorCodes(String code, HttpStatus httpStatus, String defaultMessage) {
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
