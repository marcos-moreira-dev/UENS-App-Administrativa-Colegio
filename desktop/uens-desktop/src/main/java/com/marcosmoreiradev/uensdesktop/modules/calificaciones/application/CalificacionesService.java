package com.marcosmoreiradev.uensdesktop.modules.calificaciones.application;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.CalificacionesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto.CalificacionCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto.CalificacionListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto.CalificacionResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto.CalificacionUpdateRequestDto;

public final class CalificacionesService {

    private final CalificacionesApi calificacionesApi;

    public CalificacionesService(CalificacionesApi calificacionesApi) {
        this.calificacionesApi = calificacionesApi;
    }

    public ApiResult<PageResponse<CalificacionListItemDto>> listar(
            int page,
            int size,
            Long estudianteId,
            Long claseId,
            Integer numeroParcial) {
        return listar(new CalificacionesListQuery(page, size, estudianteId, claseId, numeroParcial));
    }

    public ApiResult<PageResponse<CalificacionListItemDto>> listar(CalificacionesListQuery query) {
        return calificacionesApi.listar(
                query.page(),
                query.size(),
                query.estudianteId(),
                query.claseId(),
                query.numeroParcial());
    }

    public ApiResult<CalificacionResponseDto> obtenerPorId(long calificacionId) {
        return calificacionesApi.obtenerPorId(calificacionId);
    }

    public ApiResult<CalificacionResponseDto> crear(CalificacionCreateRequestDto request) {
        return calificacionesApi.crear(request);
    }

    public ApiResult<CalificacionResponseDto> actualizar(long calificacionId, CalificacionUpdateRequestDto request) {
        return calificacionesApi.actualizar(calificacionId, request);
    }
}
