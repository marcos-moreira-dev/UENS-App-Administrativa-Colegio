package com.marcosmoreiradev.uensdesktop.api.modules.calificaciones;

import com.marcosmoreiradev.uensdesktop.api.client.ApiClient;
import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto.CalificacionCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto.CalificacionListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto.CalificacionResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto.CalificacionUpdateRequestDto;

/**
 * Endpoint wrapper for grade registration and retrieval.
 */
public final class CalificacionesApi {

    private static final String CALIFICACIONES_PATH = "/api/v1/calificaciones";

    private final ApiClient apiClient;

    /**
     * Creates the grades API wrapper around the shared transport client.
     *
     * @param apiClient shared low-level client used to perform HTTP requests
     */
    public CalificacionesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Lists grades using pagination and optional student/class/partial filters.
     *
     * @param page zero-based page index
     * @param size requested page size
     * @param estudianteId optional student filter
     * @param claseId optional class filter
     * @param numeroParcial optional partial number filter
     * @return paginated list of grade summaries
     */
    public ApiResult<PageResponse<CalificacionListItemDto>> listar(
            int page,
            int size,
            Long estudianteId,
            Long claseId,
            Integer numeroParcial) {
        StringBuilder builder = new StringBuilder(CALIFICACIONES_PATH)
                .append("?page=").append(Math.max(page, 0))
                .append("&size=").append(Math.max(size, 1));
        if (estudianteId != null) {
            builder.append("&estudianteId=").append(estudianteId);
        }
        if (claseId != null) {
            builder.append("&claseId=").append(claseId);
        }
        if (numeroParcial != null) {
            builder.append("&numeroParcial=").append(numeroParcial);
        }
        return apiClient.getPage(builder.toString(), CalificacionListItemDto.class, true);
    }

    /**
     * Loads full grade detail.
     *
     * @param calificacionId backend identifier of the grade
     * @return grade detail response
     */
    public ApiResult<CalificacionResponseDto> obtenerPorId(long calificacionId) {
        return apiClient.get(CALIFICACIONES_PATH + "/" + calificacionId, CalificacionResponseDto.class, true);
    }

    /**
     * Creates a grade.
     *
     * @param request grade data captured by the form
     * @return created grade detail
     */
    public ApiResult<CalificacionResponseDto> crear(CalificacionCreateRequestDto request) {
        return apiClient.post(CALIFICACIONES_PATH, request, CalificacionResponseDto.class, true);
    }

    /**
     * Updates an existing grade.
     *
     * @param calificacionId backend identifier of the grade to update
     * @param request new grade data
     * @return updated grade detail
     */
    public ApiResult<CalificacionResponseDto> actualizar(long calificacionId, CalificacionUpdateRequestDto request) {
        return apiClient.put(CALIFICACIONES_PATH + "/" + calificacionId, request, CalificacionResponseDto.class, true);
    }
}
