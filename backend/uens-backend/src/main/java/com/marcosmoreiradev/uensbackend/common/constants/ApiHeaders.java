package com.marcosmoreiradev.uensbackend.common.constants;

public final class ApiHeaders {
/**
 * Construye la instancia de ApiHeaders para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 */

    private ApiHeaders() {
    }

    public static final String AUTHORIZATION = "Authorization";
    public static final String REQUEST_ID = "X-Request-Id";
    public static final String BEARER_PREFIX = "Bearer ";
}
