package com.marcosmoreiradev.uensdesktop.api.modules.docentes;

import com.marcosmoreiradev.uensdesktop.api.client.ApiClient;
import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto.DocenteCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto.DocenteListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto.DocentePatchEstadoRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto.DocenteResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto.DocenteUpdateRequestDto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Endpoint wrapper for teacher catalog and CRUD operations.
 */
public final class DocentesApi {

    private static final String DOCENTES_PATH = "/api/v1/docentes";

    private final ApiClient apiClient;

    /**
     * Creates the teachers API wrapper around the shared transport client.
     *
     * @param apiClient shared low-level client used to perform HTTP requests
     */
    public DocentesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Lists teachers using the supplied pagination and optional filters.
     *
     * @param page zero-based page index
     * @param size requested page size
     * @param query optional search text
     * @param estado optional backend status filter
     * @return paginated list of teacher summaries
     */
    public ApiResult<PageResponse<DocenteListItemDto>> listar(int page, int size, String query, String estado) {
        return apiClient.getPage(buildListPath(page, size, query, estado), DocenteListItemDto.class, true);
    }

    /**
     * Loads full teacher detail.
     *
     * @param docenteId backend identifier of the teacher
     * @return teacher detail response
     */
    public ApiResult<DocenteResponseDto> obtenerPorId(long docenteId) {
        return apiClient.get(DOCENTES_PATH + "/" + docenteId, DocenteResponseDto.class, true);
    }

    /**
     * Creates a teacher.
     *
     * @param request teacher data captured by the form
     * @return created teacher detail
     */
    public ApiResult<DocenteResponseDto> crear(DocenteCreateRequestDto request) {
        return apiClient.post(DOCENTES_PATH, request, DocenteResponseDto.class, true);
    }

    /**
     * Updates an existing teacher.
     *
     * @param docenteId backend identifier of the teacher to update
     * @param request new teacher data
     * @return updated teacher detail
     */
    public ApiResult<DocenteResponseDto> actualizar(long docenteId, DocenteUpdateRequestDto request) {
        return apiClient.put(DOCENTES_PATH + "/" + docenteId, request, DocenteResponseDto.class, true);
    }

    /**
     * Changes the operational state of a teacher.
     *
     * @param docenteId backend identifier of the teacher to patch
     * @param estado target state value accepted by the backend
     * @return updated teacher detail
     */
    public ApiResult<DocenteResponseDto> cambiarEstado(long docenteId, String estado) {
        return apiClient.patch(
                DOCENTES_PATH + "/" + docenteId + "/estado",
                new DocentePatchEstadoRequestDto(estado),
                DocenteResponseDto.class,
                true);
    }

    /**
     * Builds the query string used by the teacher list endpoint.
     *
     * @param page zero-based page index
     * @param size requested page size
     * @param query optional search text
     * @param estado optional backend status filter
     * @return backend path including query parameters
     */
    private String buildListPath(int page, int size, String query, String estado) {
        StringBuilder builder = new StringBuilder(DOCENTES_PATH)
                .append("?page=").append(Math.max(page, 0))
                .append("&size=").append(Math.max(size, 1));
        if (query != null && !query.isBlank()) {
            builder.append("&q=").append(URLEncoder.encode(query.trim(), StandardCharsets.UTF_8));
        }
        if (estado != null && !estado.isBlank()) {
            builder.append("&estado=").append(URLEncoder.encode(estado.trim(), StandardCharsets.UTF_8));
        }
        return builder.toString();
    }
}
