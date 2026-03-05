package com.marcosmoreiradev.uensdesktop.modules.clases.application;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.ClasesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClaseCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClaseListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClaseResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClaseUpdateRequestDto;

public final class ClasesService {

    private final ClasesApi clasesApi;

    public ClasesService(ClasesApi clasesApi) {
        this.clasesApi = clasesApi;
    }

    public ApiResult<PageResponse<ClaseListItemDto>> listar(
            int page,
            int size,
            String estado,
            Long seccionId,
            Long asignaturaId,
            Long docenteId,
            String diaSemana) {
        return listar(new ClasesListQuery(page, size, estado, seccionId, asignaturaId, docenteId, diaSemana));
    }

    public ApiResult<PageResponse<ClaseListItemDto>> listar(ClasesListQuery query) {
        return clasesApi.listar(
                query.page(),
                query.size(),
                query.estado(),
                query.seccionId(),
                query.asignaturaId(),
                query.docenteId(),
                query.diaSemana());
    }

    public ApiResult<ClaseResponseDto> obtenerPorId(long claseId) {
        return clasesApi.obtenerPorId(claseId);
    }

    public ApiResult<ClaseResponseDto> crear(ClaseCreateRequestDto request) {
        return clasesApi.crear(request);
    }

    public ApiResult<ClaseResponseDto> actualizar(long claseId, ClaseUpdateRequestDto request) {
        return clasesApi.actualizar(claseId, request);
    }

    public ApiResult<ClaseResponseDto> cambiarEstado(long claseId, String estado) {
        return clasesApi.cambiarEstado(claseId, estado);
    }
}
