package com.marcosmoreiradev.uensdesktop.modules.auditoria.application;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.auditoria.AuditoriaApi;
import com.marcosmoreiradev.uensdesktop.api.modules.auditoria.dto.AuditoriaEventoListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.auditoria.dto.CrearAuditoriaReporteRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudCreatedResponseDto;
import java.time.LocalDate;

public final class AuditoriaService {

    private final AuditoriaApi auditoriaApi;

    public AuditoriaService(AuditoriaApi auditoriaApi) {
        this.auditoriaApi = auditoriaApi;
    }

    public ApiResult<PageResponse<AuditoriaEventoListItemDto>> listar(
            int page,
            int size,
            String query,
            String modulo,
            String accion,
            String resultado,
            String actorLogin,
            LocalDate fechaDesde,
            LocalDate fechaHasta) {
        return auditoriaApi.listar(page, size, query, modulo, accion, resultado, actorLogin, fechaDesde, fechaHasta);
    }

    public ApiResult<PageResponse<AuditoriaEventoListItemDto>> listar(AuditoriaEventosQuery query) {
        return listar(
                query.page(),
                query.size(),
                query.query(),
                query.modulo(),
                query.accion(),
                query.resultado(),
                query.actorLogin(),
                query.fechaDesde(),
                query.fechaHasta());
    }

    public ApiResult<ReporteSolicitudCreatedResponseDto> solicitarReporte(CrearAuditoriaReporteRequestDto request) {
        return auditoriaApi.solicitarReporte(request);
    }
}
