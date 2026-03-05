package com.marcosmoreiradev.uens_backend.modules.reporte.api;

import com.marcosmoreiradev.uensbackend.modules.reporte.api.ReporteSolicitudController;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.ReporteSolicitudCommandService;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.ReporteSolicitudQueryService;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.model.ReporteArchivoDescarga;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteSolicitudControllerTest {

    @TempDir
    Path tempDir;

    @Mock
    private ReporteSolicitudCommandService commandService;
    @Mock
    private ReporteSolicitudQueryService queryService;

    private ReporteSolicitudController controller;

    @BeforeEach
    void setUp() {
        controller = new ReporteSolicitudController(commandService, queryService);
    }

    @Test
    void descargarArchivoReturnsAttachmentHeadersAndSecurityDirectives() throws Exception {
        Path file = tempDir.resolve("reporte.pdf");
        Files.writeString(file, "demo");
        when(queryService.obtenerArchivo(55L)).thenReturn(
                new ReporteArchivoDescarga(new FileSystemResource(file), "Reporte Final.pdf", "application/pdf", Files.size(file))
        );

        ResponseEntity<Resource> response = controller.descargarArchivo(55L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getHeaders().getContentType()).hasToString("application/pdf");
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .contains("attachment")
                .contains("filename*=")
                .contains("Reporte%20Final.pdf");
        assertThat(response.getHeaders().getCacheControl()).contains("no-store");
        assertThat(response.getHeaders().getFirst("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().exists()).isTrue();
    }
}
