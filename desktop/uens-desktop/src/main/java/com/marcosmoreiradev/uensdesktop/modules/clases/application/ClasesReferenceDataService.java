package com.marcosmoreiradev.uensdesktop.modules.clases.application;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.AsignaturasApi;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto.AsignaturaListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.DocentesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto.DocenteListItemDto;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.SeccionesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto.SeccionListItemDto;
import com.marcosmoreiradev.uensdesktop.common.util.PageFetchSupport;

public final class ClasesReferenceDataService {

    private static final int REFERENCE_PAGE_SIZE = 100;

    private final SeccionesApi seccionesApi;
    private final AsignaturasApi asignaturasApi;
    private final DocentesApi docentesApi;

    public ClasesReferenceDataService(
            SeccionesApi seccionesApi,
            AsignaturasApi asignaturasApi,
            DocentesApi docentesApi) {
        this.seccionesApi = seccionesApi;
        this.asignaturasApi = asignaturasApi;
        this.docentesApi = docentesApi;
    }

    public ApiResult<PageResponse<SeccionListItemDto>> listarSecciones() {
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

    public ApiResult<PageResponse<AsignaturaListItemDto>> listarAsignaturas() {
        ApiResult<PageResponse<AsignaturaListItemDto>> activas = PageFetchSupport.loadAllPages(
                REFERENCE_PAGE_SIZE,
                page -> asignaturasApi.listar(page, REFERENCE_PAGE_SIZE, null, "ACTIVO", null, null));
        if (activas.isSuccess() && activas.data().map(page -> !page.getItems().isEmpty()).orElse(false)) {
            return activas;
        }
        return PageFetchSupport.loadAllPages(
                REFERENCE_PAGE_SIZE,
                page -> asignaturasApi.listar(page, REFERENCE_PAGE_SIZE, null, null, null, null));
    }

    public ApiResult<PageResponse<DocenteListItemDto>> listarDocentes() {
        ApiResult<PageResponse<DocenteListItemDto>> activos = PageFetchSupport.loadAllPages(
                REFERENCE_PAGE_SIZE,
                page -> docentesApi.listar(page, REFERENCE_PAGE_SIZE, null, "ACTIVO"));
        if (activos.isSuccess() && activos.data().map(page -> !page.getItems().isEmpty()).orElse(false)) {
            return activos;
        }
        return PageFetchSupport.loadAllPages(
                REFERENCE_PAGE_SIZE,
                page -> docentesApi.listar(page, REFERENCE_PAGE_SIZE, null, null));
    }
}
