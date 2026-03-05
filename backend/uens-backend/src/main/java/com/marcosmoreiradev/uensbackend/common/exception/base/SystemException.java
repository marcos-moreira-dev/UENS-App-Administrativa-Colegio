package com.marcosmoreiradev.uensbackend.common.exception.base;

import com.marcosmoreiradev.uensbackend.common.exception.codes.ErrorCode;

/**
 * Define la responsabilidad de SystemException dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: modelar errores tecnicos o funcionales que se traducen a respuestas API trazables.
 */

public class SystemException extends ApiException {
/**
 * Construye la instancia de SystemException para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param errorCode dato de entrada relevante para ejecutar esta operacion: 'errorCode'
 */
    public SystemException(ErrorCode errorCode) {
        super(errorCode);
    }
/**
 * Construye la instancia de SystemException para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param errorCode dato de entrada relevante para ejecutar esta operacion: 'errorCode'
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
 */

    public SystemException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
/**
 * Construye la instancia de SystemException para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param errorCode dato de entrada relevante para ejecutar esta operacion: 'errorCode'
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @param details dato de entrada relevante para ejecutar esta operacion: 'details'
 */

    public SystemException(ErrorCode errorCode, String message, Object details) {
        super(errorCode, message, details);
    }
/**
 * Construye la instancia de SystemException para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param errorCode dato de entrada relevante para ejecutar esta operacion: 'errorCode'
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @param cause causa raiz de la excepcion para trazabilidad tecnica
 */

    public SystemException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, null, cause);
    }

/**
 * Construye la instancia de SystemException para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param errorCode dato de entrada relevante para ejecutar esta operacion: 'errorCode'
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @param details dato de entrada relevante para ejecutar esta operacion: 'details'
     * @param cause causa raiz de la excepcion para trazabilidad tecnica
 */
    public SystemException(ErrorCode errorCode, String message, Object details, Throwable cause) {
        super(errorCode, message, details, cause);
    }
}

