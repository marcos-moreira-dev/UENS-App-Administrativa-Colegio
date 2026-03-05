package com.marcosmoreiradev.uensdesktop.modules.estudiantes.application;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.EstudiantesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteUpdateRequestDto;

public final class EstudiantesService {

    private final EstudiantesApi estudiantesApi;

    public EstudiantesService(EstudiantesApi estudiantesApi) {
        this.estudiantesApi = estudiantesApi;
    }

    public ApiResult<PageResponse<EstudianteListItemDto>> listar(
            int page,
            int size,
            String query,
            String estado,
            Long seccionId,
            Long representanteLegalId) {
        return estudiantesApi.listar(page, size, query, estado, seccionId, representanteLegalId);
    }

    public ApiResult<PageResponse<EstudianteListItemDto>> listar(EstudiantesListQuery query) {
        return listar(
                query.page(),
                query.size(),
                query.query(),
                query.estado(),
                query.seccionId(),
                query.representanteLegalId());
    }

    public ApiResult<EstudianteResponseDto> obtenerPorId(long estudianteId) {
        return estudiantesApi.obtenerPorId(estudianteId);
    }

    public ApiResult<EstudianteResponseDto> crear(EstudianteCreateRequestDto request) {
        return estudiantesApi.crear(request);
    }

    public ApiResult<EstudianteResponseDto> actualizar(long estudianteId, EstudianteUpdateRequestDto request) {
        return estudiantesApi.actualizar(estudianteId, request);
    }

    public ApiResult<EstudianteResponseDto> cambiarEstado(long estudianteId, String estado) {
        return estudiantesApi.cambiarEstado(estudianteId, estado);
    }

    public ApiResult<EstudianteResponseDto> asignarSeccionVigente(long estudianteId, long seccionId) {
        return estudiantesApi.asignarSeccionVigente(estudianteId, seccionId);
    }
}
