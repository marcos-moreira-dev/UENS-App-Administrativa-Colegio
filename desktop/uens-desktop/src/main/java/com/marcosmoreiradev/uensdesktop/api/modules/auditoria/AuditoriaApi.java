package com.marcosmoreiradev.uensdesktop.api.modules.auditoria;

import com.marcosmoreiradev.uensdesktop.api.client.ApiClient;
import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.auditoria.dto.AuditoriaEventoListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.auditoria.dto.CrearAuditoriaReporteRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudCreatedResponseDto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * Endpoint wrapper for operational audit search and report generation.
 */
public final class AuditoriaApi {

    private static final String AUDITORIA_PATH = "/api/v1/auditoria";

    private final ApiClient apiClient;

    /**
     * Creates the audit API wrapper around the shared transport client.
     *
     * @param apiClient shared low-level client used to perform HTTP requests
     */
    public AuditoriaApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Lists audit events using pagination and optional operational filters.
     *
     * @param page zero-based page index
     * @param size requested page size
     * @param query optional free-text search
     * @param modulo optional module filter
     * @param accion optional action filter
     * @param resultado optional result filter
     * @param actorLogin optional actor filter
     * @param fechaDesde optional start date filter
     * @param fechaHasta optional end date filter
     * @return paginated list of audit events
     */
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
        StringBuilder builder = new StringBuilder(AUDITORIA_PATH)
                .append("/eventos?page=").append(Math.max(page, 0))
                .append("&size=").append(Math.max(size, 1));
        appendQuery(builder, "q", query);
        appendQuery(builder, "modulo", modulo);
        appendQuery(builder, "accion", accion);
        appendQuery(builder, "resultado", resultado);
        appendQuery(builder, "actorLogin", actorLogin);
        if (fechaDesde != null) {
            builder.append("&fechaDesde=").append(fechaDesde);
        }
        if (fechaHasta != null) {
            builder.append("&fechaHasta=").append(fechaHasta);
        }
        return apiClient.getPage(builder.toString(), AuditoriaEventoListItemDto.class, true);
    }

    /**
     * Requests an asynchronous audit report generation.
     *
     * @param request parameters that scope the requested audit report
     * @return metadata for the created report request
     */
    public ApiResult<ReporteSolicitudCreatedResponseDto> solicitarReporte(CrearAuditoriaReporteRequestDto request) {
        return apiClient.post(AUDITORIA_PATH + "/reportes/solicitudes", request, ReporteSolicitudCreatedResponseDto.class, true);
    }

    /**
     * Appends a URL-encoded query parameter only when the value is meaningful.
     *
     * @param builder path builder being assembled
     * @param key query parameter name
     * @param value candidate value
     */
    private void appendQuery(StringBuilder builder, String key, String value) {
        if (value != null && !value.isBlank()) {
            builder.append("&").append(key).append("=").append(URLEncoder.encode(value.trim(), StandardCharsets.UTF_8));
        }
    }
}
