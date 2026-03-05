package com.marcosmoreiradev.uensbackend.common.exception.base;

import com.marcosmoreiradev.uensbackend.common.exception.codes.ErrorCode;

import java.util.Objects;

public abstract class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object details;
/**
 * Construye la instancia de ApiException para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param errorCode dato de entrada relevante para ejecutar esta operacion: 'errorCode'
 */

    protected ApiException(ErrorCode errorCode) {
        super(Objects.requireNonNull(errorCode, "errorCode is required").defaultMessage());
        this.errorCode = errorCode;
        this.details = null;
    }
/**
 * Construye la instancia de ApiException para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param errorCode dato de entrada relevante para ejecutar esta operacion: 'errorCode'
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
 */

    protected ApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode is required");
        this.details = null;
    }
/**
 * Construye la instancia de ApiException para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param errorCode dato de entrada relevante para ejecutar esta operacion: 'errorCode'
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @param details dato de entrada relevante para ejecutar esta operacion: 'details'
 */

    protected ApiException(ErrorCode errorCode, String message, Object details) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode is required");
        this.details = details;
    }

/**
 * Construye la instancia de ApiException para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param errorCode dato de entrada relevante para ejecutar esta operacion: 'errorCode'
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @param details dato de entrada relevante para ejecutar esta operacion: 'details'
     * @param cause causa raiz de la excepcion para trazabilidad tecnica
     * @return salida util para continuar con la capa llamadora.
 */
    protected ApiException(ErrorCode errorCode, String message, Object details, Throwable cause) {
        super(message, cause);
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode is required");
        this.details = details;
    }

    public ErrorCode getErrorCode() { return errorCode; }
    public Object getDetails() { return details; }
}
