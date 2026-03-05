package com.marcosmoreiradev.uensdesktop.api.modules.representantes;

import com.marcosmoreiradev.uensdesktop.api.client.ApiClient;
import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto.RepresentanteLegalCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto.RepresentanteLegalListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto.RepresentanteLegalResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto.RepresentanteLegalUpdateRequestDto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Endpoint wrapper for representative management operations.
 */
public final class RepresentantesApi {

    private static final String REPRESENTANTES_PATH = "/api/v1/representantes";

    private final ApiClient apiClient;

    /**
     * Creates the representatives API wrapper around the shared transport client.
     *
     * @param apiClient shared low-level client used to perform HTTP requests
     */
    public RepresentantesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Loads a bounded catalog of representatives using the default first-page settings.
     *
     * @return paginated response for the first representative page
     */
    public ApiResult<PageResponse<RepresentanteLegalListItemDto>> listar() {
        return listar(0, 100, null);
    }

    /**
     * Lists representatives with optional search text and pagination.
     *
     * @param page zero-based page index
     * @param size requested page size
     * @param query optional search text applied by the backend
     * @return paginated list of representative summaries
     */
    public ApiResult<PageResponse<RepresentanteLegalListItemDto>> listar(int page, int size, String query) {
        return apiClient.getPage(buildListPath(page, size, query), RepresentanteLegalListItemDto.class, true);
    }

    /**
     * Loads the full representative detail for a specific identifier.
     *
     * @param representanteId backend identifier of the representative
     * @return representative detail response
     */
    public ApiResult<RepresentanteLegalResponseDto> obtenerPorId(long representanteId) {
        return apiClient.get(REPRESENTANTES_PATH + "/" + representanteId, RepresentanteLegalResponseDto.class, true);
    }

    /**
     * Creates a new representative in the backend.
     *
     * @param request representative data validated by the form
     * @return created representative detail
     */
    public ApiResult<RepresentanteLegalResponseDto> crear(RepresentanteLegalCreateRequestDto request) {
        return apiClient.post(REPRESENTANTES_PATH, request, RepresentanteLegalResponseDto.class, true);
    }

    /**
     * Updates an existing representative.
     *
     * @param representanteId backend identifier of the representative to update
     * @param request new representative data
     * @return updated representative detail
     */
    public ApiResult<RepresentanteLegalResponseDto> actualizar(
            long representanteId,
            RepresentanteLegalUpdateRequestDto request) {
        return apiClient.put(REPRESENTANTES_PATH + "/" + representanteId, request, RepresentanteLegalResponseDto.class, true);
    }

    private String buildListPath(int page, int size, String query) {
        StringBuilder builder = new StringBuilder(REPRESENTANTES_PATH)
                .append("?page=").append(Math.max(page, 0))
                .append("&size=").append(Math.max(size, 1));
        if (query != null && !query.isBlank()) {
            builder.append("&q=").append(URLEncoder.encode(query.trim(), StandardCharsets.UTF_8));
        }
        return builder.toString();
    }
}
