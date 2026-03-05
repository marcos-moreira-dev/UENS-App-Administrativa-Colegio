package com.marcosmoreiradev.uens_backend.modules.reporte.infrastructure.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.marcosmoreiradev.uensbackend.common.exception.base.InfrastructureException;
import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.config.properties.ReportOutputProperties;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.model.DocumentDownloadResource;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.model.StoredDocument;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.storage.LocalFilesystemDocumentStorageAdapter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LocalFilesystemDocumentStorageAdapterTest {

    @TempDir
    Path tempDir;

    @Test
    void storeAndLoadRoundTripUsesOpaqueDocumentKey() throws Exception {
        LocalFilesystemDocumentStorageAdapter adapter =
                new LocalFilesystemDocumentStorageAdapter(new ReportOutputProperties(tempDir.toString(), "", 30, 20));

        StoredDocument stored = adapter.store(
                "reportes",
                "reporte final.pdf",
                "application/pdf",
                outputPath -> Files.writeString(outputPath, "demo", StandardCharsets.UTF_8)
        );

        DocumentDownloadResource loaded = adapter.load(stored.documentKey());

        assertThat(stored.documentKey()).isEqualTo("reportes/reporte_final.pdf");
        assertThat(stored.fileName()).isEqualTo("reporte_final.pdf");
        assertThat(loaded.resource().exists()).isTrue();
        assertThat(loaded.resource().getFilename()).isEqualTo("reporte_final.pdf");
        assertThat(loaded.sizeBytes()).isEqualTo(4L);
    }

    @Test
    void loadRejectsDocumentKeysThatEscapeBaseDirectory() {
        LocalFilesystemDocumentStorageAdapter adapter =
                new LocalFilesystemDocumentStorageAdapter(new ReportOutputProperties(tempDir.toString(), "", 30, 20));

        assertThatThrownBy(() -> adapter.load("../fuera.pdf"))
                .isInstanceOf(InfrastructureException.class)
                .hasMessage("La clave del documento es invalida para el repositorio local.");
    }

    @Test
    void loadFailsWhenDocumentDoesNotExist() {
        LocalFilesystemDocumentStorageAdapter adapter =
                new LocalFilesystemDocumentStorageAdapter(new ReportOutputProperties(tempDir.toString(), "", 30, 20));

        assertThatThrownBy(() -> adapter.load("reportes/faltante.pdf"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("El archivo de reporte no existe en disco.");
    }
}
