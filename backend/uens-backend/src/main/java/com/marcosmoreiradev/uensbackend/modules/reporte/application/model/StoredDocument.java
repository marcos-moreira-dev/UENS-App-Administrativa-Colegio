package com.marcosmoreiradev.uensbackend.modules.reporte.application.model;

import java.time.Instant;

/**
 * Metadata devuelta por el repositorio documental despues de almacenar un
 * archivo exportado.
 *
 * @param documentKey clave opaca o relativa del documento dentro del proveedor
 * @param fileName nombre almacenado para el archivo
 * @param mimeType tipo MIME asociado
 * @param sizeBytes tamano final del documento
 * @param storedAt instante de persistencia
 */
public record StoredDocument(
        String documentKey,
        String fileName,
        String mimeType,
        long sizeBytes,
        Instant storedAt
) {
}
