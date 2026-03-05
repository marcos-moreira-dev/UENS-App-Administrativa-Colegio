package com.marcosmoreiradev.uensdesktop.api.modules.clases;

import com.marcosmoreiradev.uensdesktop.api.client.ApiClient;
import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClaseCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClaseListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClasePatchEstadoRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClaseResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClaseUpdateRequestDto;

/**
 * Endpoint wrapper for academic class scheduling operations.
 */
public final class ClasesApi {

    private static final String CLASES_PATH = "/api/v1/clases";

    private final ApiClient apiClient;

    /**
     * Creates the classes API wrapper around the shared transport client.
     *
     * @param apiClient shared low-level client used to perform HTTP requests
     */
    public ClasesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Lists class schedules using pagination and optional academic filters.
     *
     * @param page zero-based page index
     * @param size requested page size
     * @param estado optional state filter
     * @param seccionId optional section filter
     * @param asignaturaId optional subject filter
     * @param docenteId optional teacher filter
     * @param diaSemana optional day-of-week filter
     * @return paginated list of class summaries
     */
    public ApiResult<PageResponse<ClaseListItemDto>> listar(
            int page,
            int size,
            String estado,
            Long seccionId,
            Long asignaturaId,
            Long docenteId,
            String diaSemana) {
        StringBuilder builder = new StringBuilder(CLASES_PATH)
                .append("?page=").append(Math.max(page, 0))
                .append("&size=").append(Math.max(size, 1));
        if (estado != null && !estado.isBlank()) {
            builder.append("&estado=").append(estado.trim());
        }
        if (seccionId != null) {
            builder.append("&seccionId=").append(seccionId);
        }
        if (asignaturaId != null) {
            builder.append("&asignaturaId=").append(asignaturaId);
        }
        if (docenteId != null) {
            builder.append("&docenteId=").append(docenteId);
        }
        if (diaSemana != null && !diaSemana.isBlank()) {
            builder.append("&diaSemana=").append(diaSemana.trim());
        }
        return apiClient.getPage(builder.toString(), ClaseListItemDto.class, true);
    }

    /**
     * Loads full class detail.
     *
     * @param claseId backend identifier of the class
     * @return class detail response
     */
    public ApiResult<ClaseResponseDto> obtenerPorId(long claseId) {
        return apiClient.get(CLASES_PATH + "/" + claseId, ClaseResponseDto.class, true);
    }

    /**
     * Creates a class schedule entry.
     *
     * @param request class data captured by the form
     * @return created class detail
     */
    public ApiResult<ClaseResponseDto> crear(ClaseCreateRequestDto request) {
        return apiClient.post(CLASES_PATH, request, ClaseResponseDto.class, true);
    }

    /**
     * Updates an existing class schedule entry.
     *
     * @param claseId backend identifier of the class to update
     * @param request new class data
     * @return updated class detail
     */
    public ApiResult<ClaseResponseDto> actualizar(long claseId, ClaseUpdateRequestDto request) {
        return apiClient.put(CLASES_PATH + "/" + claseId, request, ClaseResponseDto.class, true);
    }

    /**
     * Changes the operational state of a class.
     *
     * @param claseId backend identifier of the class to patch
     * @param estado target state value accepted by the backend
     * @return updated class detail
     */
    public ApiResult<ClaseResponseDto> cambiarEstado(long claseId, String estado) {
        return apiClient.patch(
                CLASES_PATH + "/" + claseId + "/estado",
                new ClasePatchEstadoRequestDto(estado),
                ClaseResponseDto.class,
                true);
    }
}
