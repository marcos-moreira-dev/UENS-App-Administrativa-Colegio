package com.marcosmoreiradev.uensdesktop.modules.secciones.application;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.SeccionesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionUpdateRequestDto;

public final class SeccionesService {

    private final SeccionesApi seccionesApi;

    public SeccionesService(SeccionesApi seccionesApi) {
        this.seccionesApi = seccionesApi;
    }

    public ApiResult<PageResponse<SeccionListItemDto>> listar(
            int page,
            int size,
            String query,
            String estado,
            Integer grado,
            String paralelo,
            String anioLectivo) {
        return listar(new SeccionesListQuery(page, size, query, estado, grado, paralelo, anioLectivo));
    }

    public ApiResult<PageResponse<SeccionListItemDto>> listar(SeccionesListQuery query) {
        return seccionesApi.listar(
                query.page(),
                query.size(),
                query.query(),
                query.estado(),
                query.grado(),
                query.paralelo(),
                query.anioLectivo());
    }

    public ApiResult<SeccionResponseDto> obtenerPorId(long seccionId) {
        return seccionesApi.obtenerPorId(seccionId);
    }

    public ApiResult<SeccionResponseDto> crear(SeccionCreateRequestDto request) {
        return seccionesApi.crear(request);
    }

    public ApiResult<SeccionResponseDto> actualizar(long seccionId, SeccionUpdateRequestDto request) {
        return seccionesApi.actualizar(seccionId, request);
    }

    public ApiResult<SeccionResponseDto> cambiarEstado(long seccionId, String estado) {
        return seccionesApi.cambiarEstado(seccionId, estado);
    }
}
