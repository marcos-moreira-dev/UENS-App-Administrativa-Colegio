package com.marcosmoreiradev.uensdesktop.api.modules.estudiantes;

import com.marcosmoreiradev.uensdesktop.api.client.ApiClient;
import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.AsignarSeccionVigenteRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudiantePatchEstadoRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteUpdateRequestDto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Endpoint wrapper for student catalog and lifecycle operations.
 */
public final class EstudiantesApi {

    private static final String ESTUDIANTES_PATH = "/api/v1/estudiantes";

    private final ApiClient apiClient;

    /**
     * Creates the students API wrapper around the shared transport client.
     *
     * @param apiClient shared low-level client used to perform HTTP requests
     */
    public EstudiantesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Lists students using pagination and optional academic filters.
     *
     * @param page zero-based page index
     * @param size requested page size
     * @param query optional free-text search
     * @param estado optional state filter
     * @param seccionId optional current-section filter
     * @param representanteLegalId optional representative filter
     * @return paginated list of student summaries
     */
    public ApiResult<PageResponse<EstudianteListItemDto>> listar(
            int page,
            int size,
            String query,
            String estado,
            Long seccionId,
            Long representanteLegalId) {
        return apiClient.getPage(
                buildListPath(page, size, query, estado, seccionId, representanteLegalId),
                EstudianteListItemDto.class,
                true);
    }

    /**
     * Loads full student detail.
     *
     * @param estudianteId backend identifier of the student
     * @return student detail response
     */
    public ApiResult<EstudianteResponseDto> obtenerPorId(long estudianteId) {
        return apiClient.get(ESTUDIANTES_PATH + "/" + estudianteId, EstudianteResponseDto.class, true);
    }

    /**
     * Creates a student.
     *
     * @param request student data captured by the form
     * @return created student detail
     */
    public ApiResult<EstudianteResponseDto> crear(EstudianteCreateRequestDto request) {
        return apiClient.post(ESTUDIANTES_PATH, request, EstudianteResponseDto.class, true);
    }

    /**
     * Updates an existing student.
     *
     * @param estudianteId backend identifier of the student to update
     * @param request new student data
     * @return updated student detail
     */
    public ApiResult<EstudianteResponseDto> actualizar(long estudianteId, EstudianteUpdateRequestDto request) {
        return apiClient.put(ESTUDIANTES_PATH + "/" + estudianteId, request, EstudianteResponseDto.class, true);
    }

    /**
     * Changes the operational state of a student.
     *
     * @param estudianteId backend identifier of the student to patch
     * @param estado target state value accepted by the backend
     * @return updated student detail
     */
    public ApiResult<EstudianteResponseDto> cambiarEstado(long estudianteId, String estado) {
        return apiClient.patch(
                ESTUDIANTES_PATH + "/" + estudianteId + "/estado",
                new EstudiantePatchEstadoRequestDto(estado),
                EstudianteResponseDto.class,
                true);
    }

    /**
     * Reassigns the current section of a student.
     *
     * @param estudianteId backend identifier of the student to update
     * @param seccionId backend identifier of the new active section
     * @return updated student detail
     */
    public ApiResult<EstudianteResponseDto> asignarSeccionVigente(long estudianteId, long seccionId) {
        return apiClient.put(
                ESTUDIANTES_PATH + "/" + estudianteId + "/seccion-vigente",
                new AsignarSeccionVigenteRequestDto(seccionId),
                EstudianteResponseDto.class,
                true);
    }

    /**
     * Builds the query string used by the student list endpoint.
     *
     * @return backend path including query parameters
     */
    private String buildListPath(
            int page,
            int size,
            String query,
            String estado,
            Long seccionId,
            Long representanteLegalId) {
        StringBuilder builder = new StringBuilder(ESTUDIANTES_PATH)
                .append("?page=").append(Math.max(page, 0))
                .append("&size=").append(Math.max(size, 1));

        if (query != null && !query.isBlank()) {
            builder.append("&q=").append(encode(query.trim()));
        }
        if (estado != null && !estado.isBlank()) {
            builder.append("&estado=").append(encode(estado.trim()));
        }
        if (seccionId != null) {
            builder.append("&seccionId=").append(seccionId);
        }
        if (representanteLegalId != null) {
            builder.append("&representanteLegalId=").append(representanteLegalId);
        }

        return builder.toString();
    }

    /**
     * URL-encodes a free-text value before placing it in the query string.
     *
     * @param value free-text value that will travel in the request URL
     * @return encoded value safe for the query string
     */
    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
