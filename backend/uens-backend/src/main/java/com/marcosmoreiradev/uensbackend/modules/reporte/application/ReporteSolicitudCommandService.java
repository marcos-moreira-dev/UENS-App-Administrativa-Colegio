package com.marcosmoreiradev.uensbackend.modules.reporte.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.BusinessRuleException;
import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ReporteErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.auditoria.application.AuditoriaEventService;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.CrearReporteSolicitudRequestDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.ReporteSolicitudCreadaResponseDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.mapper.ReportePayloadDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.mapper.ReporteSolicitudDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.processor.AuditoriaAdminOperacionesProcessor;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.validator.CrearReporteSolicitudRequestValidator;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.repository.ReporteSolicitudQueueJpaRepository;
import com.marcosmoreiradev.uensbackend.security.user.CurrentAuthenticatedUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Service
/**
 * Define la responsabilidad de ReporteSolicitudCommandService dentro del backend UENS.
 * Contexto: modulo reporte, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: ejecutar operaciones de escritura aplicando reglas de negocio y validaciones de fase 1.
 */
public class ReporteSolicitudCommandService {

    private final ReporteSolicitudQueueJpaRepository repository;
    private final ReporteSolicitudDtoMapper mapper;
    private final ReportePayloadDtoMapper payloadMapper;
    private final CrearReporteSolicitudRequestValidator validator;
    private final AuditoriaEventService auditoriaEventService;
    private final CurrentAuthenticatedUserService currentAuthenticatedUserService;
/**
 * Construye la instancia de ReporteSolicitudCommandService para operar en el modulo reporte.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param repository adaptador de persistencia que opera sobre tablas del esquema V2 3FN
     * @param mapper componente de mapeo entre DTOs, modelos de dominio y entidades
     * @param payloadMapper mapper para serializar/deserializar payload de solicitudes de reporte
     * @param validator dato de entrada relevante para ejecutar esta operacion: 'validator'
     * @param auditoriaEventService servicio transversal para registrar eventos de auditoria operativa
     * @param currentAuthenticatedUserService fachada para consultar el usuario autenticado actual
 */

    public ReporteSolicitudCommandService(
            ReporteSolicitudQueueJpaRepository repository,
            ReporteSolicitudDtoMapper mapper,
            ReportePayloadDtoMapper payloadMapper,
            CrearReporteSolicitudRequestValidator validator,
            AuditoriaEventService auditoriaEventService,
            CurrentAuthenticatedUserService currentAuthenticatedUserService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.payloadMapper = payloadMapper;
        this.validator = validator;
        this.auditoriaEventService = auditoriaEventService;
        this.currentAuthenticatedUserService = currentAuthenticatedUserService;
    }

    @Transactional
/**
 * Implementa la operacion 'crearSolicitud' del modulo reporte en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ReporteSolicitudCreadaResponseDto crearSolicitud(CrearReporteSolicitudRequestDto request) {
        validator.validar(request);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("formatoSalida", normalizeFormato(request.formatoSalida()));
        params.put("seccionId", request.seccionId());
        params.put("numeroParcial", request.numeroParcial());
        params.put("fechaDesde", request.fechaDesde());
        params.put("fechaHasta", request.fechaHasta());

        String tipo = request.tipoReporte().trim().toUpperCase(Locale.ROOT);
        ReporteSolicitudCreadaResponseDto created = crearSolicitudInterna(tipo, params);
        auditoriaEventService.registrarEvento(
                "REPORTE",
                "SOLICITUD_CREADA",
                "REPORTE_SOLICITUD_QUEUE",
                created.solicitudId().toString(),
                "EXITO",
                "Solicitud de reporte encolada. tipo=" + tipo
        );
        return created;
    }

    @Transactional
    public ReporteSolicitudCreadaResponseDto crearSolicitudAuditoria(String formatoSalida, Map<String, Object> filtros) {
        ensureCurrentUserIsAdmin();

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("formatoSalida", normalizeFormato(formatoSalida));
        if (filtros != null) {
            params.putAll(filtros);
        }

        ReporteSolicitudCreadaResponseDto created = crearSolicitudInterna(
                AuditoriaAdminOperacionesProcessor.TIPO,
                params
        );
        auditoriaEventService.registrarEvento(
                "AUDITORIA",
                "SOLICITUD_REPORTE_AUDITORIA_CREADA",
                "REPORTE_SOLICITUD_QUEUE",
                created.solicitudId().toString(),
                "EXITO",
                "Solicitud de reporte de auditoria encolada."
        );
        return created;
    }

    @Transactional
/**
 * Implementa la operacion 'reintentar' del modulo reporte en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param solicitudId identificador de la solicitud de reporte en la cola DB queue
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ReporteSolicitudCreadaResponseDto reintentar(Long solicitudId) {
        ReporteSolicitudQueueJpaEntity entity = repository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de reporte no encontrada."));

        if (!"ERROR".equalsIgnoreCase(entity.getEstado())) {
            throw new BusinessRuleException(ReporteErrorCodes.RN_REP_04_REINTENTO_NO_PERMITIDO);
        }

        entity.reencolar();
        ReporteSolicitudQueueJpaEntity saved = repository.save(entity);
        auditoriaEventService.registrarEvento(
                "REPORTE",
                "SOLICITUD_REINTENTADA",
                "REPORTE_SOLICITUD_QUEUE",
                saved.getId().toString(),
                "EXITO",
                "Solicitud de reporte reencolada manualmente."
        );
        return mapper.toCreadaDto(saved);
    }

    private ReporteSolicitudCreadaResponseDto crearSolicitudInterna(String tipoReporte, Map<String, Object> params) {
        ReporteSolicitudQueueJpaEntity saved = repository.save(
                ReporteSolicitudQueueJpaEntity.crear(
                        tipoReporte,
                        payloadMapper.toJson(params),
                        resolveCurrentUserId()
                )
        );
        return mapper.toCreadaDto(saved);
    }

/**
 * Metodo de soporte interno 'resolveCurrentUserId' para mantener cohesion en ReporteSolicitudCommandService.
 * Contexto: modulo reporte, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private Long resolveCurrentUserId() {
        return currentAuthenticatedUserService.getCurrentUserIdOrNull();
    }

    private void ensureCurrentUserIsAdmin() {
        currentAuthenticatedUserService.ensureAdmin("Solo ADMIN puede solicitar reportes de auditoria.");
    }

    private static String normalizeFormato(String formatoSalida) {
        if (formatoSalida == null || formatoSalida.isBlank()) {
            return "XLSX";
        }
        return formatoSalida.trim().toUpperCase(Locale.ROOT);
    }
}
