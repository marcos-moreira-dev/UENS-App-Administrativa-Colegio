package com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.storage;

import com.marcosmoreiradev.uensbackend.common.exception.base.InfrastructureException;
import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ReporteErrorCodes;
import com.marcosmoreiradev.uensbackend.config.properties.ReportOutputProperties;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.model.DocumentDownloadResource;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.model.StoredDocument;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.port.DocumentContentWriter;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.port.DocumentStoragePort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

/**
 * Adapter local que simula un repositorio documental usando filesystem. Es el
 * punto de reemplazo natural para integrar despues proveedores de mercado.
 */
@Repository
public class LocalFilesystemDocumentStorageAdapter implements DocumentStoragePort {

    private final ReportOutputProperties properties;

    public LocalFilesystemDocumentStorageAdapter(ReportOutputProperties properties) {
        this.properties = properties;
    }

    @Override
    public StoredDocument store(String namespace, String fileName, String mimeType, DocumentContentWriter writer) {
        Path basePath = resolveBasePath();
        String normalizedNamespace = sanitizeSegment(namespace == null ? "default" : namespace);
        String normalizedFileName = sanitizeFileName(fileName);
        Path relativePath = Paths.get(normalizedNamespace, normalizedFileName);
        Path fullPath = basePath.resolve(relativePath).normalize();

        if (!fullPath.startsWith(basePath)) {
            throw new InfrastructureException(
                    ReporteErrorCodes.SYS_REP_02_FALLO_PROCESAMIENTO,
                    "La ruta del documento es invalida para el repositorio local."
            );
        }

        try {
            Files.createDirectories(fullPath.getParent());
            writer.writeTo(fullPath);
            return new StoredDocument(
                    normalizeKey(relativePath),
                    fullPath.getFileName().toString(),
                    mimeType,
                    Files.size(fullPath),
                    Instant.now()
            );
        } catch (Exception ex) {
            deleteSilently(fullPath);
            throw new InfrastructureException(
                    ReporteErrorCodes.SYS_REP_02_FALLO_PROCESAMIENTO,
                    "No fue posible almacenar el documento en el repositorio local.",
                    null,
                    ex
            );
        }
    }

    @Override
    public DocumentDownloadResource load(String documentKey) {
        Path basePath = resolveBasePath();
        Path fullPath = basePath.resolve(documentKey).normalize();
        if (!fullPath.startsWith(basePath)) {
            throw new InfrastructureException(
                    ReporteErrorCodes.SYS_REP_02_FALLO_PROCESAMIENTO,
                    "La clave del documento es invalida para el repositorio local."
            );
        }
        if (!Files.exists(fullPath)) {
            throw new ResourceNotFoundException("El archivo de reporte no existe en disco.");
        }
        try {
            return new DocumentDownloadResource(documentKey, new FileSystemResource(fullPath), Files.size(fullPath));
        } catch (Exception ex) {
            throw new InfrastructureException(
                    ReporteErrorCodes.SYS_REP_02_FALLO_PROCESAMIENTO,
                    "No fue posible cargar el documento del repositorio local.",
                    null,
                    ex
            );
        }
    }

    @Override
    public void delete(String documentKey) {
        if (documentKey == null || documentKey.isBlank()) {
            return;
        }
        Path basePath = resolveBasePath();
        Path fullPath = basePath.resolve(documentKey).normalize();
        if (!fullPath.startsWith(basePath)) {
            return;
        }
        deleteSilently(fullPath);
    }

    private Path resolveBasePath() {
        return Paths.get(properties.dir()).toAbsolutePath().normalize();
    }

    private static void deleteSilently(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (Exception ignored) {
        }
    }

    private static String sanitizeSegment(String value) {
        return value.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    private static String sanitizeFileName(String value) {
        return sanitizeSegment(value == null || value.isBlank() ? "documento.bin" : value);
    }

    private static String normalizeKey(Path relativePath) {
        return relativePath.toString().replace('\\', '/');
    }
}
