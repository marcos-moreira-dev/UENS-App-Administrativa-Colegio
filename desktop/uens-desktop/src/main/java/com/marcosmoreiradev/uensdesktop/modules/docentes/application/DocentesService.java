package com.marcosmoreiradev.uensdesktop.modules.docentes.application;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.DocentesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto.DocenteCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto.DocenteListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto.DocenteResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto.DocenteUpdateRequestDto;

public final class DocentesService {

    private final DocentesApi docentesApi;

    public DocentesService(DocentesApi docentesApi) {
        this.docentesApi = docentesApi;
    }

    public ApiResult<PageResponse<DocenteListItemDto>> listar(int page, int size, String query, String estado) {
        return docentesApi.listar(page, size, query, estado);
    }

    public ApiResult<DocenteResponseDto> obtenerPorId(long docenteId) {
        return docentesApi.obtenerPorId(docenteId);
    }

    public ApiResult<DocenteResponseDto> crear(DocenteCreateRequestDto request) {
        return docentesApi.crear(request);
    }

    public ApiResult<DocenteResponseDto> actualizar(long docenteId, DocenteUpdateRequestDto request) {
        return docentesApi.actualizar(docenteId, request);
    }

    public ApiResult<DocenteResponseDto> cambiarEstado(long docenteId, String estado) {
        return docentesApi.cambiarEstado(docenteId, estado);
    }
}
