package com.marcosmoreiradev.uensbackend.modules.reporte.application.port;

import com.marcosmoreiradev.uensbackend.modules.reporte.application.model.DocumentDownloadResource;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.model.StoredDocument;

/**
 * Puerto de almacenamiento documental para reportes y otros archivos
 * exportables. Permite comenzar en filesystem local y migrar luego a S3,
 * MinIO, Blob Storage u otro proveedor.
 */
public interface DocumentStoragePort {

    /**
     * Almacena un documento usando el writer proporcionado.
     *
     * @param namespace agrupador logico del documento dentro del repositorio
     * @param fileName nombre preferido del archivo resultante
     * @param mimeType tipo MIME esperado
     * @param writer estrategia que materializa el contenido
     * @return metadata del documento persistido
     */
    StoredDocument store(String namespace, String fileName, String mimeType, DocumentContentWriter writer);

    /**
     * Carga un documento previamente almacenado.
     *
     * @param documentKey clave opaca del documento dentro del proveedor
     * @return recurso listo para descarga
     */
    DocumentDownloadResource load(String documentKey);

    /**
     * Elimina un documento del proveedor actual si existe.
     *
     * @param documentKey clave opaca del documento
     */
    void delete(String documentKey);
}
