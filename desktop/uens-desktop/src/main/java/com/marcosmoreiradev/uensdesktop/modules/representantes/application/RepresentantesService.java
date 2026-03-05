package com.marcosmoreiradev.uensdesktop.modules.representantes.application;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.representantes.RepresentantesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto.RepresentanteLegalCreateRequestDto;
import com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto.RepresentanteLegalListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto.RepresentanteLegalResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto.RepresentanteLegalUpdateRequestDto;

public final class RepresentantesService {

    private final RepresentantesApi representantesApi;

    public RepresentantesService(RepresentantesApi representantesApi) {
        this.representantesApi = representantesApi;
    }

    public ApiResult<PageResponse<RepresentanteLegalListItemDto>> listar(int page, int size, String query) {
        return representantesApi.listar(page, size, query);
    }

    public ApiResult<RepresentanteLegalResponseDto> obtenerPorId(long representanteId) {
        return representantesApi.obtenerPorId(representanteId);
    }

    public ApiResult<RepresentanteLegalResponseDto> crear(RepresentanteLegalCreateRequestDto request) {
        return representantesApi.crear(request);
    }

    public ApiResult<RepresentanteLegalResponseDto> actualizar(
            long representanteId,
            RepresentanteLegalUpdateRequestDto request) {
        return representantesApi.actualizar(representanteId, request);
    }
}
