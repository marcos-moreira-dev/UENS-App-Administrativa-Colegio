package com.marcosmoreiradev.uensdesktop.modules.reportes.application;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.SeccionesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionListItemDto;
import com.marcosmoreiradev.uensdesktop.common.util.PageFetchSupport;

public final class ReportesReferenceDataService {

    private static final int REFERENCE_PAGE_SIZE = 100;

    private final SeccionesApi seccionesApi;

    public ReportesReferenceDataService(SeccionesApi seccionesApi) {
        this.seccionesApi = seccionesApi;
    }

    public ApiResult<PageResponse<SeccionListItemDto>> listarSeccionesDisponibles() {
        ApiResult<PageResponse<SeccionListItemDto>> activas = PageFetchSupport.loadAllPages(
                REFERENCE_PAGE_SIZE,
                page -> seccionesApi.listar(page, REFERENCE_PAGE_SIZE, null, "ACTIVO", null, null, null));
        if (activas.isSuccess() && activas.data().map(page -> !page.getItems().isEmpty()).orElse(false)) {
            return activas;
        }
        return PageFetchSupport.loadAllPages(
                REFERENCE_PAGE_SIZE,
                page -> seccionesApi.listar(page, REFERENCE_PAGE_SIZE, null, null, null, null, null));
    }
}
