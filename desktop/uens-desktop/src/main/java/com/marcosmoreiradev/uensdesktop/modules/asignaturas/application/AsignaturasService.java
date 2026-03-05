package com.marcosmoreiradev.uensdesktop.modules.asignaturas.application;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.AsignaturasApi;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaUpdateRequestDto;

public final class AsignaturasService {

    private final AsignaturasApi asignaturasApi;

    public AsignaturasService(AsignaturasApi asignaturasApi) {
        this.asignaturasApi = asignaturasApi;
    }

    public ApiResult<PageResponse<AsignaturaListItemDto>> listar(
            int page,
            int size,
            String query,
            String estado,
            Integer grado,
            String area) {
        return listar(new AsignaturasListQuery(page, size, query, estado, grado, area));
    }

    public ApiResult<PageResponse<AsignaturaListItemDto>> listar(AsignaturasListQuery query) {
        return asignaturasApi.listar(
                query.page(),
                query.size(),
                query.query(),
                query.estado(),
                query.grado(),
                query.area());
    }

    public ApiResult<AsignaturaResponseDto> obtenerPorId(long asignaturaId) {
        return asignaturasApi.obtenerPorId(asignaturaId);
    }

    public ApiResult<AsignaturaResponseDto> crear(AsignaturaCreateRequestDto request) {
        return asignaturasApi.crear(request);
    }

    public ApiResult<AsignaturaResponseDto> actualizar(long asignaturaId, AsignaturaUpdateRequestDto request) {
        return asignaturasApi.actualizar(asignaturaId, request);
    }

    public ApiResult<AsignaturaResponseDto> cambiarEstado(long asignaturaId, String estado) {
        return asignaturasApi.cambiarEstado(asignaturaId, estado);
    }
}
