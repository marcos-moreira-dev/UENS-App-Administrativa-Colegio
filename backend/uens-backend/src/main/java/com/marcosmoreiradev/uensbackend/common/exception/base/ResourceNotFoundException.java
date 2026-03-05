package com.marcosmoreiradev.uensbackend.common.exception.base;

import com.marcosmoreiradev.uensbackend.common.exception.codes.ApiErrorCodes;

/**
 * Senala que el recurso solicitado no existe dentro del contexto del caso de
 * uso y debe traducirse a un {@code 404} consistente.
 */
public class ResourceNotFoundException extends ApplicationException {

    /**
     * Crea la excepcion usando el codigo API estandar de recurso inexistente.
     *
     * @param message mensaje funcional estable para el cliente y soporte
     */
    public ResourceNotFoundException(String message) {
        super(ApiErrorCodes.API_04_RECURSO_NO_ENCONTRADO, message);
    }
}
