package com.marcosmoreiradev.uensbackend.modules.auditoria.application;

import com.marcosmoreiradev.uensbackend.modules.auditoria.api.dto.CrearAuditoriaReporteRequestDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.ReporteSolicitudCreadaResponseDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.ReporteSolicitudCommandService;
import com.marcosmoreiradev.uensbackend.security.user.CurrentAuthenticatedUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuditoriaReporteService {

    private final ReporteSolicitudCommandService reporteSolicitudCommandService;
    private final AuditoriaEventService auditoriaEventService;
    private final CurrentAuthenticatedUserService currentAuthenticatedUserService;

    public AuditoriaReporteService(
            ReporteSolicitudCommandService reporteSolicitudCommandService,
            AuditoriaEventService auditoriaEventService,
            CurrentAuthenticatedUserService currentAuthenticatedUserService
    ) {
        this.reporteSolicitudCommandService = reporteSolicitudCommandService;
        this.auditoriaEventService = auditoriaEventService;
        this.currentAuthenticatedUserService = currentAuthenticatedUserService;
    }

    @Transactional
    public ReporteSolicitudCreadaResponseDto solicitarReporte(CrearAuditoriaReporteRequestDto request) {
        currentAuthenticatedUserService.ensureAdmin("Solo ADMIN puede solicitar reportes de auditoria.");
        LocalDate fechaHasta = request.fechaHasta();
        LocalDate fechaDesde = request.fechaDesde();
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new IllegalArgumentException("FechaDesde no puede ser mayor que FechaHasta.");
        }

        Map<String, Object> filtros = new LinkedHashMap<>();
        filtros.put("fechaDesde", request.fechaDesde());
        filtros.put("fechaHasta", request.fechaHasta());
        filtros.put("modulo", request.modulo());
        filtros.put("accion", request.accion());
        filtros.put("resultado", request.resultado());
        filtros.put("actorLogin", request.actorLogin());
        filtros.put("incluirDetalle", request.incluirDetalle() == null || request.incluirDetalle());

        ReporteSolicitudCreadaResponseDto creada = reporteSolicitudCommandService.crearSolicitudAuditoria(
                request.formatoSalida(),
                filtros
        );

        auditoriaEventService.registrarEvento(
                "AUDITORIA",
                "REPORTE_AUDITORIA_SOLICITADO",
                "REPORTE_SOLICITUD_QUEUE",
                creada.solicitudId().toString(),
                "EXITO",
                "Se registro solicitud de reporte de auditoria."
        );
        return creada;
    }
}
