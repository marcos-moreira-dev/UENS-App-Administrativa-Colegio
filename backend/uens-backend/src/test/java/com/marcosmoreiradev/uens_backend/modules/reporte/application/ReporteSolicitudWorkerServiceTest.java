package com.marcosmoreiradev.uens_backend.modules.reporte.application;

import com.marcosmoreiradev.uensbackend.config.properties.ReportQueueProperties;
import com.marcosmoreiradev.uensbackend.modules.auditoria.application.AuditoriaEventService;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.ReporteFileGenerationService;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.ReporteSolicitudWorkerService;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.ReporteArchivoGenerado;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.mapper.ReportePayloadDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.processor.ReporteDataProcessor;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.processor.ReporteDataProcessorSelector;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.repository.ReporteSolicitudQueueClaimRepository;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.repository.ReporteSolicitudQueueJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
/**
 * Define la responsabilidad de ReporteSolicitudWorkerServiceTest dentro del backend UENS.
 * Contexto: modulo reporte, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
class ReporteSolicitudWorkerServiceTest {

    @Mock
    private ReporteSolicitudQueueJpaRepository repository;
    @Mock
    private ReporteSolicitudQueueClaimRepository claimRepository;
    @Mock
    private ReporteDataProcessorSelector processorSelector;
    @Mock
    private ReportePayloadDtoMapper payloadMapper;
    @Mock
    private ReporteDataProcessor processor;
    @Mock
    private ReporteFileGenerationService fileGenerationService;
    @Mock
    private AuditoriaEventService auditoriaEventService;

    private ReporteSolicitudWorkerService service;

    @BeforeEach
    void setUp() {
        service = new ReporteSolicitudWorkerService(
                repository,
                claimRepository,
                processorSelector,
                payloadMapper,
                fileGenerationService,
                new ReportQueueProperties(true, 5000, 10000, 2, 10, 3),
                auditoriaEventService
        );
    }

    @Test
    void procesarPendientesMarksAsCompletadaWhenProcessorSucceeds() {
        ReporteSolicitudQueueJpaEntity solicitud = ReporteSolicitudQueueJpaEntity.crear("TIPO", "{}", 1L);
        ReflectionTestUtils.setField(solicitud, "id", 101L);
        ReflectionTestUtils.setField(solicitud, "estado", "EN_PROCESO");

        when(claimRepository.claimPendientes(10)).thenReturn(List.of(solicitud));
        when(processorSelector.seleccionar("TIPO")).thenReturn(processor);
        when(processor.procesar(solicitud)).thenReturn(List.of("ok"));
        when(payloadMapper.readText("{}", "formatoSalida", "XLSX")).thenReturn("XLSX");
        when(fileGenerationService.generar(any(), any(), any())).thenReturn(
                new ReporteArchivoGenerado(
                        "reporte.xlsx",
                        "reporte.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "XLSX",
                        128L,
                        LocalDateTime.now()
                )
        );
        when(payloadMapper.toJson(any())).thenReturn("{\"resultado\":\"ok\"}");

        service.procesarPendientes();

        assertEquals("COMPLETADA", solicitud.getEstado());
        verify(repository).save(solicitud);
    }

    @Test
    void procesarPendientesKeepsPendingWhenBelowMaxAttempts() {
        ReporteSolicitudQueueJpaEntity solicitud = ReporteSolicitudQueueJpaEntity.crear("TIPO", "{}", 1L);
        ReflectionTestUtils.setField(solicitud, "id", 102L);
        ReflectionTestUtils.setField(solicitud, "estado", "EN_PROCESO");
        ReflectionTestUtils.setField(solicitud, "intentos", 2);

        when(claimRepository.claimPendientes(10)).thenReturn(List.of(solicitud));
        when(processorSelector.seleccionar("TIPO")).thenReturn(processor);
        when(processor.procesar(solicitud)).thenThrow(new RuntimeException("fallo temporal"));

        service.procesarPendientes();

        assertEquals("PENDIENTE", solicitud.getEstado());
        assertEquals("fallo temporal", solicitud.getErrorDetalle());
    }

    @Test
    void procesarPendientesMarksErrorWhenMaxAttemptsReached() {
        ReporteSolicitudQueueJpaEntity solicitud = ReporteSolicitudQueueJpaEntity.crear("TIPO", "{}", 1L);
        ReflectionTestUtils.setField(solicitud, "id", 103L);
        ReflectionTestUtils.setField(solicitud, "estado", "EN_PROCESO");
        ReflectionTestUtils.setField(solicitud, "intentos", 3);

        when(claimRepository.claimPendientes(10)).thenReturn(List.of(solicitud));
        when(processorSelector.seleccionar("TIPO")).thenReturn(processor);
        when(processor.procesar(solicitud)).thenThrow(new RuntimeException("error permanente"));

        service.procesarPendientes();

        assertEquals("ERROR", solicitud.getEstado());
        assertEquals("error permanente", solicitud.getErrorDetalle());
    }
}
