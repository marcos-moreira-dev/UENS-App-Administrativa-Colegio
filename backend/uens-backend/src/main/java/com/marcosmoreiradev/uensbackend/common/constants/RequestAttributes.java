package com.marcosmoreiradev.uensbackend.common.constants;

public final class RequestAttributes {
/**
 * Construye la instancia de RequestAttributes para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 */

    private RequestAttributes() {
    }

    public static final String REQUEST_ID = "requestId";
    public static final String AUTH_ERROR_CODE = "authErrorCode";
}
