package com.marcosmoreiradev.uensbackend.common.constants;

public final class PaginationConstants {
/**
 * Construye la instancia de PaginationConstants para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 */

    private PaginationConstants() {
    }

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;
}
