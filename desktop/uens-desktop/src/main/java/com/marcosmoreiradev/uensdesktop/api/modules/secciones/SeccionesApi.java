package com.marcosmoreiradev.uensdesktop.api.modules.secciones;

import com.marcosmoreiradev.uensdesktop.api.client.ApiClient;
import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionPatchEstadoRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionUpdateRequestDto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Endpoint wrapper for section catalog and CRUD operations.
 */
public final class SeccionesApi {

    private static final String SECCIONES_PATH = "/api/v1/secciones";

    private final ApiClient apiClient;

    /**
     * Creates the sections API wrapper around the shared transport client.
     *
     * @param apiClient shared low-level client used to perform HTTP requests
     */
    public SeccionesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Loads a bounded catalog of sections using the default first-page settings.
     *
     * @return paginated response for the first section page
     */
    public ApiResult<PageResponse<SeccionListItemDto>> listar() {
        return listar(0, 100, null, null, null, null, null);
    }

    /**
     * Lists sections using pagination and optional academic filters.
     *
     * @param page zero-based page index
     * @param size requested page size
     * @param query optional free-text search
     * @param estado optional state filter
     * @param grado optional grade filter
     * @param paralelo optional parallel filter
     * @param anioLectivo optional academic-year filter
     * @return paginated list of section summaries
     */
    public ApiResult<PageResponse<SeccionListItemDto>> listar(
            int page,
            int size,
            String query,
            String estado,
            Integer grado,
            String paralelo,
            String anioLectivo) {
        return apiClient.getPage(buildListPath(page, size, query, estado, grado, paralelo, anioLectivo), SeccionListItemDto.class, true);
    }

    /**
     * Loads full section detail.
     *
     * @param seccionId backend identifier of the section
     * @return section detail response
     */
    public ApiResult<SeccionResponseDto> obtenerPorId(long seccionId) {
        return apiClient.get(SECCIONES_PATH + "/" + seccionId, SeccionResponseDto.class, true);
    }

    /**
     * Creates a section.
     *
     * @param request section data captured by the form
     * @return created section detail
     */
    public ApiResult<SeccionResponseDto> crear(SeccionCreateRequestDto request) {
        return apiClient.post(SECCIONES_PATH, request, SeccionResponseDto.class, true);
    }

    /**
     * Updates an existing section.
     *
     * @param seccionId backend identifier of the section to update
     * @param request new section data
     * @return updated section detail
     */
    public ApiResult<SeccionResponseDto> actualizar(long seccionId, SeccionUpdateRequestDto request) {
        return apiClient.put(SECCIONES_PATH + "/" + seccionId, request, SeccionResponseDto.class, true);
    }

    /**
     * Changes the operational state of a section.
     *
     * @param seccionId backend identifier of the section to patch
     * @param estado target state value accepted by the backend
     * @return updated section detail
     */
    public ApiResult<SeccionResponseDto> cambiarEstado(long seccionId, String estado) {
        return apiClient.patch(
                SECCIONES_PATH + "/" + seccionId + "/estado",
                new SeccionPatchEstadoRequestDto(estado),
                SeccionResponseDto.class,
                true);
    }

    /**
     * Builds the query string used by the section list endpoint.
     *
     * @return backend path including query parameters
     */
    private String buildListPath(
            int page,
            int size,
            String query,
            String estado,
            Integer grado,
            String paralelo,
            String anioLectivo) {
        StringBuilder builder = new StringBuilder(SECCIONES_PATH)
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
        if (paralelo != null && !paralelo.isBlank()) {
            builder.append("&paralelo=").append(URLEncoder.encode(paralelo.trim(), StandardCharsets.UTF_8));
        }
        if (anioLectivo != null && !anioLectivo.isBlank()) {
            builder.append("&anioLectivo=").append(URLEncoder.encode(anioLectivo.trim(), StandardCharsets.UTF_8));
        }
        return builder.toString();
    }
}
