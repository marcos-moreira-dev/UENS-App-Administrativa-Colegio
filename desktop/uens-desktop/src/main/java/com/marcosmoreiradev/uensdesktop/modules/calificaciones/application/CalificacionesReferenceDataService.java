package com.marcosmoreiradev.uensdesktop.modules.calificaciones.application;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.ClasesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.dto.ClaseListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.EstudiantesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteListItemDto;
import com.marcosmoreiradev.uensdesktop.common.util.PageFetchSupport;

public final class CalificacionesReferenceDataService {

    private static final int REFERENCE_PAGE_SIZE = 100;

    private final EstudiantesApi estudiantesApi;
    private final ClasesApi clasesApi;

    public CalificacionesReferenceDataService(
            EstudiantesApi estudiantesApi,
            ClasesApi clasesApi) {
        this.estudiantesApi = estudiantesApi;
        this.clasesApi = clasesApi;
    }

    public ApiResult<PageResponse<EstudianteListItemDto>> listarEstudiantes() {
        ApiResult<PageResponse<EstudianteListItemDto>> activos = PageFetchSupport.loadAllPages(
                REFERENCE_PAGE_SIZE,
                page -> estudiantesApi.listar(page, REFERENCE_PAGE_SIZE, null, "ACTIVO", null, null));
        if (activos.isSuccess() && activos.data().map(page -> !page.getItems().isEmpty()).orElse(false)) {
            return activos;
        }
        return PageFetchSupport.loadAllPages(
                REFERENCE_PAGE_SIZE,
                page -> estudiantesApi.listar(page, REFERENCE_PAGE_SIZE, null, null, null, null));
    }

    public ApiResult<PageResponse<ClaseListItemDto>> listarClases() {
        ApiResult<PageResponse<ClaseListItemDto>> activas = PageFetchSupport.loadAllPages(
                REFERENCE_PAGE_SIZE,
                page -> clasesApi.listar(page, REFERENCE_PAGE_SIZE, "ACTIVO", null, null, null, null));
        if (activas.isSuccess() && activas.data().map(page -> !page.getItems().isEmpty()).orElse(false)) {
            return activas;
        }
        return PageFetchSupport.loadAllPages(
                REFERENCE_PAGE_SIZE,
                page -> clasesApi.listar(page, REFERENCE_PAGE_SIZE, null, null, null, null, null));
    }
}
