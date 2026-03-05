package com.marcosmoreiradev.uens_backend.modules.reporte.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.BusinessRuleException;
import com.marcosmoreiradev.uensbackend.common.exception.base.InfrastructureException;
import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.pagination.PageSortParser;
import com.marcosmoreiradev.uensbackend.common.pagination.PageableFactory;
import com.marcosmoreiradev.uensbackend.common.pagination.SortWhitelistValidator;
import com.marcosmoreiradev.uensbackend.config.JacksonConfig;
import com.marcosmoreiradev.uensbackend.config.properties.ReportOutputProperties;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.ReporteSolicitudQueryService;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.mapper.ReportePayloadDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.mapper.ReporteSolicitudDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.model.ReporteArchivoDescarga;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.storage.LocalFilesystemDocumentStorageAdapter;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.repository.ReporteSolicitudQueueJpaRepository;
import com.marcosmoreiradev.uensbackend.security.user.CurrentAuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReporteSolicitudQueryServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void obtenerArchivoReturnsSanitizedMetadataForCompletedRequest() throws Exception {
        Path reportFile = tempDir.resolve("reportes").resolve("reporte_demo.pdf");
        Files.createDirectories(reportFile.getParent());
        Files.writeString(reportFile, "demo");

        ReporteSolicitudQueueJpaEntity entity = completedEntity("""
                {
                  "archivo": {
                    "rutaRelativa": "reporte_demo.pdf",
                    "nombreArchivo": "../Reporte Final.pdf",
                    "mimeType": "application/pdf"
                  }
                }
                """);

        ReporteSolicitudQueryService service = buildService(entity, reportFile.getParent());

        ReporteArchivoDescarga result = service.obtenerArchivo(10L);

        assertThat(result.resource().exists()).isTrue();
        assertThat(result.resource().getFilename()).isEqualTo("reporte_demo.pdf");
        assertThat(result.nombreArchivo()).isEqualTo(".._Reporte Final.pdf");
        assertThat(result.mimeType()).isEqualTo("application/pdf");
        assertThat(result.tamanoBytes()).isEqualTo(Files.size(reportFile));
    }

    @Test
    void obtenerArchivoRejectsRequestsThatAreNotCompleted() {
        ReporteSolicitudQueueJpaEntity entity = ReporteSolicitudQueueJpaEntity.crear("LISTADO_ESTUDIANTES_POR_SECCION", "{}", 1L);
        ReflectionTestUtils.setField(entity, "id", 11L);
        ReflectionTestUtils.setField(entity, "estado", "EN_PROCESO");

        ReporteSolicitudQueryService service = buildService(entity, tempDir);

        assertThatThrownBy(() -> service.obtenerArchivo(11L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("El archivo del reporte aun no esta disponible.");
    }

    @Test
    void obtenerArchivoRejectsRelativePathsOutsideConfiguredDirectory() {
        ReporteSolicitudQueueJpaEntity entity = completedEntity("""
                {
                  "archivo": {
                    "rutaRelativa": "../otro/reporte.pdf",
                    "nombreArchivo": "reporte.pdf",
                    "mimeType": "application/pdf"
                  }
                }
                """);

        ReporteSolicitudQueryService service = buildService(entity, tempDir);

        assertThatThrownBy(() -> service.obtenerArchivo(10L))
                .isInstanceOf(InfrastructureException.class)
                .hasMessage("La clave del documento es invalida para el repositorio local.");
    }

    @Test
    void obtenerArchivoFailsWhenPhysicalFileDoesNotExist() {
        ReporteSolicitudQueueJpaEntity entity = completedEntity("""
                {
                  "archivo": {
                    "rutaRelativa": "faltante.pdf",
                    "nombreArchivo": "faltante.pdf",
                    "mimeType": "application/pdf"
                  }
                }
                """);

        ReporteSolicitudQueryService service = buildService(entity, tempDir);

        assertThatThrownBy(() -> service.obtenerArchivo(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("El archivo de reporte no existe en disco.");
    }

    @Test
    void obtenerArchivoHidesRequestsOwnedByAnotherUser() {
        ReporteSolicitudQueueJpaEntity entity = completedEntity("""
                {
                  "archivo": {
                    "rutaRelativa": "reporte_demo.pdf",
                    "nombreArchivo": "reporte_demo.pdf",
                    "mimeType": "application/pdf"
                  }
                }
                """);

        ReporteSolicitudQueryService service = buildService(entity, tempDir, false, 99L);

        assertThatThrownBy(() -> service.obtenerArchivo(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Solicitud de reporte no encontrada.");
    }

    private ReporteSolicitudQueryService buildService(ReporteSolicitudQueueJpaEntity entity, Path outputDir) {
        return buildService(entity, outputDir, true, 1L);
    }

    private ReporteSolicitudQueryService buildService(
            ReporteSolicitudQueueJpaEntity entity,
            Path outputDir,
            boolean admin,
            Long currentUserId
    ) {
        ReporteSolicitudQueueJpaRepository repository = mock(ReporteSolicitudQueueJpaRepository.class);
        CurrentAuthenticatedUserService currentAuthenticatedUserService = mock(CurrentAuthenticatedUserService.class);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(currentAuthenticatedUserService.isAdmin()).thenReturn(admin);
        when(currentAuthenticatedUserService.getCurrentUserIdOrNull()).thenReturn(currentUserId);

        return new ReporteSolicitudQueryService(
                repository,
                new ReporteSolicitudDtoMapper(),
                new ReportePayloadDtoMapper(new JacksonConfig().objectMapper()),
                new PageableFactory(new PageSortParser(new SortWhitelistValidator())),
                currentAuthenticatedUserService,
                new LocalFilesystemDocumentStorageAdapter(new ReportOutputProperties(outputDir.toString(), "", 30, 20))
        );
    }

    private ReporteSolicitudQueueJpaEntity completedEntity(String resultadoJson) {
        ReporteSolicitudQueueJpaEntity entity = ReporteSolicitudQueueJpaEntity.crear("LISTADO_ESTUDIANTES_POR_SECCION", "{}", 1L);
        ReflectionTestUtils.setField(entity, "id", 10L);
        ReflectionTestUtils.setField(entity, "estado", "COMPLETADA");
        ReflectionTestUtils.setField(entity, "resultadoJson", resultadoJson);
        ReflectionTestUtils.setField(entity, "fechaSolicitud", LocalDateTime.now());
        ReflectionTestUtils.setField(entity, "fechaActualizacion", LocalDateTime.now());
        return entity;
    }
}
