package com.marcosmoreiradev.uens_backend.modules.reporte.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.BusinessRuleException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ReporteErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.auditoria.application.AuditoriaEventService;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.CrearReporteSolicitudRequestDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.ReporteSolicitudCreadaResponseDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.ReporteSolicitudCommandService;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.mapper.ReportePayloadDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.mapper.ReporteSolicitudDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.validator.CrearReporteSolicitudRequestValidator;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.repository.ReporteSolicitudQueueJpaRepository;
import com.marcosmoreiradev.uensbackend.security.user.CurrentAuthenticatedUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
/**
 * Define la responsabilidad de ReporteSolicitudCommandServiceTest dentro del backend UENS.
 * Contexto: modulo reporte, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
class ReporteSolicitudCommandServiceTest {

    @Mock
    private ReporteSolicitudQueueJpaRepository repository;
    @Mock
    private ReporteSolicitudDtoMapper mapper;
    @Mock
    private ReportePayloadDtoMapper payloadMapper;
    @Mock
    private AuditoriaEventService auditoriaEventService;
    @Mock
    private CurrentAuthenticatedUserService currentAuthenticatedUserService;

    private ReporteSolicitudCommandService service;

    @BeforeEach
    void setUp() {
        service = new ReporteSolicitudCommandService(
                repository,
                mapper,
                payloadMapper,
                new CrearReporteSolicitudRequestValidator(),
                auditoriaEventService,
                currentAuthenticatedUserService
        );
    }

    @Test
    void crearSolicitudNormalizesTipoAndPersists() {
        CrearReporteSolicitudRequestDto request = new CrearReporteSolicitudRequestDto(
                "  listado_estudiantes_por_seccion ",
                "xlsx",
                7L,
                null,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 30)
        );

        when(payloadMapper.toJson(any())).thenReturn("{\"seccionId\":7}");

        ReporteSolicitudQueueJpaEntity saved = ReporteSolicitudQueueJpaEntity.crear(
                "LISTADO_ESTUDIANTES_POR_SECCION",
                "{\"seccionId\":7}",
                null
        );
        ReflectionTestUtils.setField(saved, "id", 9L);
        ReflectionTestUtils.setField(saved, "fechaSolicitud", LocalDateTime.of(2025, 1, 1, 8, 0));
        when(repository.save(any())).thenReturn(saved);

        ReporteSolicitudCreadaResponseDto mapped = new ReporteSolicitudCreadaResponseDto(
                9L,
                "LISTADO_ESTUDIANTES_POR_SECCION",
                "PENDIENTE",
                LocalDateTime.of(2025, 1, 1, 8, 0)
        );
        when(mapper.toCreadaDto(saved)).thenReturn(mapped);

        ReporteSolicitudCreadaResponseDto result = service.crearSolicitud(request);

        ArgumentCaptor<ReporteSolicitudQueueJpaEntity> captor = ArgumentCaptor.forClass(ReporteSolicitudQueueJpaEntity.class);
        verify(repository).save(captor.capture());
        assertEquals("LISTADO_ESTUDIANTES_POR_SECCION", captor.getValue().getTipoReporte());
        assertEquals(9L, result.solicitudId());
    }

    @Test
    void reintentarFailsWhenEstadoIsNotError() {
        ReporteSolicitudQueueJpaEntity entity = ReporteSolicitudQueueJpaEntity.crear("X", "{}", 1L);
        ReflectionTestUtils.setField(entity, "id", 55L);
        ReflectionTestUtils.setField(entity, "estado", "PENDIENTE");
        when(repository.findById(55L)).thenReturn(Optional.of(entity));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.reintentar(55L));

        assertEquals(ReporteErrorCodes.RN_REP_04_REINTENTO_NO_PERMITIDO, ex.getErrorCode());
    }

    @Test
    void reintentarRequeuesWhenEstadoError() {
        ReporteSolicitudQueueJpaEntity entity = ReporteSolicitudQueueJpaEntity.crear("X", "{}", 1L);
        ReflectionTestUtils.setField(entity, "id", 56L);
        ReflectionTestUtils.setField(entity, "estado", "ERROR");
        ReflectionTestUtils.setField(entity, "fechaSolicitud", LocalDateTime.of(2025, 1, 1, 9, 0));

        when(repository.findById(56L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toCreadaDto(entity)).thenReturn(new ReporteSolicitudCreadaResponseDto(
                56L, "X", "PENDIENTE", LocalDateTime.of(2025, 1, 1, 9, 0)
        ));

        ReporteSolicitudCreadaResponseDto result = service.reintentar(56L);

        assertEquals("PENDIENTE", entity.getEstado());
        assertEquals(56L, result.solicitudId());
    }
}
