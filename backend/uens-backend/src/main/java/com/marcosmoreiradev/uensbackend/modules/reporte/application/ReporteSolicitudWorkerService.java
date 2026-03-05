package com.marcosmoreiradev.uensbackend.modules.reporte.application;

import com.marcosmoreiradev.uensbackend.config.properties.ReportQueueProperties;
import com.marcosmoreiradev.uensbackend.modules.auditoria.application.AuditoriaEventService;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.ReporteArchivoGenerado;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.mapper.ReportePayloadDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.processor.ReporteDataProcessor;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.processor.ReporteDataProcessorSelector;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.repository.ReporteSolicitudQueueClaimRepository;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.repository.ReporteSolicitudQueueJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
/**
 * Define la responsabilidad de ReporteSolicitudWorkerService dentro del backend UENS.
 * Contexto: modulo reporte, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: orquestar comportamiento de aplicacion entre dominio, persistencia y seguridad.
 */
public class ReporteSolicitudWorkerService {

    private static final Logger log = LoggerFactory.getLogger(ReporteSolicitudWorkerService.class);

    private final ReporteSolicitudQueueJpaRepository repository;
    private final ReporteSolicitudQueueClaimRepository claimRepository;
    private final ReporteDataProcessorSelector processorSelector;
    private final ReportePayloadDtoMapper payloadMapper;
    private final ReporteFileGenerationService fileGenerationService;
    private final ReportQueueProperties properties;
    private final AuditoriaEventService auditoriaEventService;
/**
 * Construye la instancia de ReporteSolicitudWorkerService para operar en el modulo reporte.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param repository adaptador de persistencia que opera sobre tablas del esquema V2 3FN
     * @param claimRepository repositorio especializado para tomar items de cola con estrategia de claim
     * @param processorSelector selector de procesador segun tipo de reporte solicitado
     * @param payloadMapper mapper para serializar/deserializar payload de solicitudes de reporte
     * @param properties propiedades tipadas que gobiernan limites y frecuencia de procesamiento
     * @param auditoriaEventService servicio de auditoria transversal para registrar resultado de procesamiento
 */

    public ReporteSolicitudWorkerService(
            ReporteSolicitudQueueJpaRepository repository,
            ReporteSolicitudQueueClaimRepository claimRepository,
            ReporteDataProcessorSelector processorSelector,
            ReportePayloadDtoMapper payloadMapper,
            ReporteFileGenerationService fileGenerationService,
            ReportQueueProperties properties,
            AuditoriaEventService auditoriaEventService
    ) {
        this.repository = repository;
        this.claimRepository = claimRepository;
        this.processorSelector = processorSelector;
        this.payloadMapper = payloadMapper;
        this.fileGenerationService = fileGenerationService;
        this.properties = properties;
        this.auditoriaEventService = auditoriaEventService;
    }

    @Transactional
/**
 * Implementa la operacion 'procesarPendientes' del modulo reporte en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void procesarPendientes() {
        List<ReporteSolicitudQueueJpaEntity> pendientes = claimRepository.claimPendientes(properties.claimBatchSize());

        for (ReporteSolicitudQueueJpaEntity solicitud : pendientes) {
            ReporteArchivoGenerado archivoGenerado = null;
            try {
                ReporteDataProcessor processor = processorSelector.seleccionar(solicitud.getTipoReporte());
                Object payload = processor.procesar(solicitud);
                String formatoSalida = payloadMapper.readText(solicitud.getParametrosJson(), "formatoSalida", "XLSX");
                archivoGenerado = fileGenerationService.generar(solicitud, payload, formatoSalida);

                Map<String, Object> resultado = new LinkedHashMap<>();
                resultado.put("payload", payload);
                resultado.put("archivo", archivoToMap(archivoGenerado));
                solicitud.marcarCompletada(payloadMapper.toJson(resultado));
                repository.save(solicitud);
                auditoriaEventService.registrarEvento(
                        "REPORTE",
                        "WORKER_SOLICITUD_COMPLETADA",
                        "REPORTE_SOLICITUD_QUEUE",
                        solicitud.getId().toString(),
                        "EXITO",
                        "Solicitud procesada correctamente."
                );
            } catch (Exception ex) {
                fileGenerationService.eliminarSilenciosamente(archivoGenerado);
                String error = ex.getMessage() == null ? "Error no controlado en worker." : ex.getMessage();
                log.error(
                        "Error procesando solicitud de reporte: solicitudId={}, tipo={}, intentosActuales={}, estadoActual={}, error={}",
                        solicitud.getId(),
                        solicitud.getTipoReporte(),
                        solicitud.getIntentos(),
                        solicitud.getEstado(),
                        error,
                        ex
                );
                if (solicitud.getIntentos() < properties.maxAttempts()) {
                    solicitud.marcarPendienteParaReintento(error);
                } else {
                    solicitud.marcarError(error);
                }
                repository.save(solicitud);
                auditoriaEventService.registrarEvento(
                        "REPORTE",
                        "WORKER_SOLICITUD_ERROR",
                        "REPORTE_SOLICITUD_QUEUE",
                        solicitud.getId().toString(),
                        "ERROR",
                        error
                );
            }
        }
    }

    private Map<String, Object> archivoToMap(ReporteArchivoGenerado archivo) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("nombreArchivo", archivo.nombreArchivo());
        map.put("rutaRelativa", archivo.rutaRelativa());
        map.put("mimeType", archivo.mimeType());
        map.put("formato", archivo.formato());
        map.put("tamanoBytes", archivo.tamanoBytes());
        map.put("generadoEn", Objects.toString(archivo.generadoEn(), ""));
        return map;
    }
}
