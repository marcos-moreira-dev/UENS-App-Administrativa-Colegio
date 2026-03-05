package com.marcosmoreiradev.uensdesktop.api.modules.reportes;

import com.marcosmoreiradev.uensdesktop.api.client.ApiClient;
import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.client.BinaryPayload;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudCreatedResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudDetailResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudResultResponseDto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Endpoint wrapper for asynchronous report requests, status polling and file downloads.
 */
public final class ReportesApi {

    private static final String REPORTES_PATH = "/api/v1/reportes/solicitudes";

    private final ApiClient apiClient;

    /**
     * Creates the reports API wrapper around the shared transport client.
     *
     * @param apiClient shared low-level client used to perform HTTP requests
     */
    public ReportesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Creates a new asynchronous report request.
     *
     * @param request report parameters chosen by the operator
     * @return created request metadata with the generated identifier
     */
    public ApiResult<ReporteSolicitudCreatedResponseDto> crear(ReporteSolicitudCreateRequestDto request) {
        return apiClient.post(REPORTES_PATH, request, ReporteSolicitudCreatedResponseDto.class, true);
    }

    /**
     * Lists report requests using pagination and optional business filters.
     *
     * @param page zero-based page index
     * @param size requested page size
     * @param query optional free-text search
     * @param tipoReporte optional report-type filter
     * @param estado optional request-state filter
     * @return paginated list of report request summaries
     */
    public ApiResult<PageResponse<ReporteSolicitudListItemDto>> listar(
            int page,
            int size,
            String query,
            String tipoReporte,
            String estado) {
        StringBuilder builder = new StringBuilder(REPORTES_PATH)
                .append("?page=").append(Math.max(page, 0))
                .append("&size=").append(Math.max(size, 1));
        if (query != null && !query.isBlank()) {
            builder.append("&q=").append(URLEncoder.encode(query.trim(), StandardCharsets.UTF_8));
        }
        if (tipoReporte != null && !tipoReporte.isBlank()) {
            builder.append("&tipoReporte=").append(URLEncoder.encode(tipoReporte.trim(), StandardCharsets.UTF_8));
        }
        if (estado != null && !estado.isBlank()) {
            builder.append("&estado=").append(URLEncoder.encode(estado.trim(), StandardCharsets.UTF_8));
        }
        return apiClient.getPage(builder.toString(), ReporteSolicitudListItemDto.class, true);
    }

    /**
     * Loads the detail metadata of a report request.
     *
     * @param solicitudId backend identifier of the report request
     * @return report request detail
     */
    public ApiResult<ReporteSolicitudDetailResponseDto> obtenerDetalle(long solicitudId) {
        return apiClient.get(REPORTES_PATH + "/" + solicitudId, ReporteSolicitudDetailResponseDto.class, true);
    }

    /**
     * Retrieves the current backend processing state of a report request.
     *
     * @param solicitudId backend identifier of the report request
     * @return current processing state and result metadata
     */
    public ApiResult<ReporteSolicitudResultResponseDto> obtenerEstado(long solicitudId) {
        return apiClient.get(REPORTES_PATH + "/" + solicitudId + "/estado", ReporteSolicitudResultResponseDto.class, true);
    }

    /**
     * Retrieves the final result snapshot of a report request.
     *
     * @param solicitudId backend identifier of the report request
     * @return result metadata returned by the backend
     */
    public ApiResult<ReporteSolicitudResultResponseDto> obtenerResultado(long solicitudId) {
        return apiClient.get(REPORTES_PATH + "/" + solicitudId + "/resultado", ReporteSolicitudResultResponseDto.class, true);
    }

    /**
     * Downloads the generated report file.
     *
     * @param solicitudId backend identifier of the report request
     * @return binary payload with the generated file
     */
    public ApiResult<BinaryPayload> descargarArchivo(long solicitudId) {
        return apiClient.getBinary(REPORTES_PATH + "/" + solicitudId + "/archivo", true);
    }

    /**
     * Requests a backend retry for a previously failed report.
     *
     * @param solicitudId backend identifier of the report request
     * @return metadata for the new retry request
     */
    public ApiResult<ReporteSolicitudCreatedResponseDto> reintentar(long solicitudId) {
        return apiClient.postWithoutBody(REPORTES_PATH + "/" + solicitudId + "/reintentar", ReporteSolicitudCreatedResponseDto.class, true);
    }
}
