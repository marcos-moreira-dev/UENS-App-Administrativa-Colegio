package com.marcosmoreiradev.uensbackend.common.exception.base;

import com.marcosmoreiradev.uensbackend.common.exception.codes.ApiErrorCodes;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ErrorCode;

/**
 * Representa fallos tecnicos de infraestructura o integracion que no deben
 * exponerse como errores de negocio aunque requieran trazabilidad estable.
 */
public class InfrastructureException extends SystemException {

    /**
     * Crea una excepcion de infraestructura con el codigo generico interno.
     *
     * @param message mensaje tecnico controlado que puede registrarse y exponerse de forma segura
     */
    public InfrastructureException(String message) {
        super(ApiErrorCodes.SYS_01_ERROR_INTERNO, message);
    }

    /**
     * Crea una excepcion de infraestructura con el codigo generico interno.
     *
     * @param message mensaje tecnico controlado que puede registrarse y exponerse de forma segura
     * @param cause causa raiz para soporte y mantenimiento
     */
    public InfrastructureException(String message, Throwable cause) {
        super(ApiErrorCodes.SYS_01_ERROR_INTERNO, message, cause);
    }

    /**
     * Crea una excepcion de infraestructura con un codigo especifico de modulo.
     *
     * @param errorCode codigo estable para correlacion funcional y tecnica
     * @param message mensaje tecnico controlado que puede registrarse y exponerse de forma segura
     */
    public InfrastructureException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Crea una excepcion de infraestructura con un codigo especifico de modulo.
     *
     * @param errorCode codigo estable para correlacion funcional y tecnica
     * @param message mensaje tecnico controlado que puede registrarse y exponerse de forma segura
     * @param cause causa raiz para soporte y mantenimiento
     */
    public InfrastructureException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * Crea una excepcion de infraestructura con detalles tecnicos adjuntos.
     *
     * @param errorCode codigo estable para correlacion funcional y tecnica
     * @param message mensaje tecnico controlado que puede registrarse y exponerse de forma segura
     * @param details metadata adicional util para soporte
     * @param cause causa raiz para soporte y mantenimiento
     */
    public InfrastructureException(ErrorCode errorCode, String message, Object details, Throwable cause) {
        super(errorCode, message, details, cause);
    }
}
