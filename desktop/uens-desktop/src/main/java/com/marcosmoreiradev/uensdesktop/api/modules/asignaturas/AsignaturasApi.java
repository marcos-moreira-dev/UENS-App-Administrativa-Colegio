package com.marcosmoreiradev.uensdesktop.api.modules.asignaturas;

import com.marcosmoreiradev.uensdesktop.api.client.ApiClient;
import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaPatchEstadoRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaUpdateRequestDto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Endpoint wrapper for subject catalog and CRUD operations.
 */
public final class AsignaturasApi {

    private static final String ASIGNATURAS_PATH = "/api/v1/asignaturas";

    private final ApiClient apiClient;

    /**
     * Creates the subjects API wrapper around the shared transport client.
     *
     * @param apiClient shared low-level client used to perform HTTP requests
     */
    public AsignaturasApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Lists subjects using pagination and optional academic filters.
     *
     * @param page zero-based page index
     * @param size requested page size
     * @param query optional free-text search
     * @param estado optional state filter
     * @param grado optional grade filter
     * @param area optional academic area filter
     * @return paginated list of subject summaries
     */
    public ApiResult<PageResponse<AsignaturaListItemDto>> listar(
            int page,
            int size,
            String query,
            String estado,
            Integer grado,
            String area) {
        return apiClient.getPage(buildListPath(page, size, query, estado, grado, area), AsignaturaListItemDto.class, true);
    }

    /**
     * Loads full subject detail.
     *
     * @param asignaturaId backend identifier of the subject
     * @return subject detail response
     */
    public ApiResult<AsignaturaResponseDto> obtenerPorId(long asignaturaId) {
        return apiClient.get(ASIGNATURAS_PATH + "/" + asignaturaId, AsignaturaResponseDto.class, true);
    }

    /**
     * Creates a subject.
     *
     * @param request subject data captured by the form
     * @return created subject detail
     */
    public ApiResult<AsignaturaResponseDto> crear(AsignaturaCreateRequestDto request) {
        return apiClient.post(ASIGNATURAS_PATH, request, AsignaturaResponseDto.class, true);
    }

    /**
     * Updates an existing subject.
     *
     * @param asignaturaId backend identifier of the subject to update
     * @param request new subject data
     * @return updated subject detail
     */
    public ApiResult<AsignaturaResponseDto> actualizar(long asignaturaId, AsignaturaUpdateRequestDto request) {
        return apiClient.put(ASIGNATURAS_PATH + "/" + asignaturaId, request, AsignaturaResponseDto.class, true);
    }

    /**
     * Changes the operational state of a subject.
     *
     * @param asignaturaId backend identifier of the subject to patch
     * @param estado target state value accepted by the backend
     * @return updated subject detail
     */
    public ApiResult<AsignaturaResponseDto> cambiarEstado(long asignaturaId, String estado) {
        return apiClient.patch(
                ASIGNATURAS_PATH + "/" + asignaturaId + "/estado",
                new AsignaturaPatchEstadoRequestDto(estado),
                AsignaturaResponseDto.class,
                true);
    }

    /**
     * Builds the query string used by the subject list endpoint.
     *
     * @return backend path including query parameters
     */
    private String buildListPath(
            int page,
            int size,
            String query,
            String estado,
            Integer grado,
            String area) {
        StringBuilder builder = new StringBuilder(ASIGNATURAS_PATH)
                .append("?page=").append(Math.max(page, 0))
                .append("&size=").append(Math.max(size, 1));
        if (query != null && !query.isBlank()) {
            builder.append("&q=").append(URLEncoder.encode(query.trim(), StandardCharsets.UTF_8));
        }
        if (estado != null && !estado.isBlank()) {
            builder.append("&estado=").append(URLEncoder.encode(estado.trim(), StandardCharsets.UTF_8));
        }
        if (grado != null) {
            builder.append("&grado=").append(grado);
        }
        if (area != null && !area.isBlank()) {
            builder.append("&area=").append(URLEncoder.encode(area.trim(), StandardCharsets.UTF_8));
        }
        return builder.toString();
    }
}
