package com.marcosmoreiradev.uensbackend.modules.reporte.application.model;

import org.springframework.core.io.Resource;

/**
 * Metadata necesaria para retornar un archivo de reporte al cliente HTTP.
 *
 * @param resource recurso cargado desde el repositorio documental
 * @param nombreArchivo nombre sugerido para el header de descarga
 * @param mimeType tipo MIME usado en la respuesta HTTP
 * @param tamanoBytes tamano de archivo en bytes
 */
public record ReporteArchivoDescarga(
        Resource resource,
        String nombreArchivo,
        String mimeType,
        long tamanoBytes
) {
}
