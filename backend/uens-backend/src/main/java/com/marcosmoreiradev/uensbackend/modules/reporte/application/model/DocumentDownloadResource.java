package com.marcosmoreiradev.uensbackend.modules.reporte.application.model;

import org.springframework.core.io.Resource;

/**
 * Recurso documental cargado desde el proveedor seleccionado para descarga
 * HTTP o reprocesamiento controlado.
 *
 * @param documentKey clave opaca del documento
 * @param resource recurso cargado desde el storage activo
 * @param sizeBytes tamano del documento en bytes
 */
public record DocumentDownloadResource(
        String documentKey,
        Resource resource,
        long sizeBytes
) {
}
