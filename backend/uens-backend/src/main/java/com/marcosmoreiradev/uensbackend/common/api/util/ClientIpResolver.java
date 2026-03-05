package com.marcosmoreiradev.uensbackend.common.api.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * Resuelve la IP remota efectiva usada por politicas de rate limit y
 * trazabilidad operativa. Por defecto usa {@code remoteAddr} para evitar
 * confiar en cabeceras reenviadas sin un proxy confiable delante.
 */
@Component
public class ClientIpResolver {

    /**
     * Devuelve una clave estable de IP para controles de abuso y registros.
     *
     * @param request request HTTP actual
     * @return IP remota o {@code unknown} cuando el contenedor no la expone
     */
    public String resolve(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String remoteAddr = request.getRemoteAddr();
        if (remoteAddr == null || remoteAddr.isBlank()) {
            return "unknown";
        }
        return remoteAddr.trim();
    }
}
