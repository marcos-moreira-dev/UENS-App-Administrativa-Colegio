package com.marcosmoreiradev.uensdesktop.modules.dashboard.application;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.modules.dashboard.DashboardApi;
import com.marcosmoreiradev.uensdesktop.api.modules.dashboard.dto.DashboardResumenDto;

public final class DashboardService {

    private final DashboardApi dashboardApi;

    public DashboardService(DashboardApi dashboardApi) {
        this.dashboardApi = dashboardApi;
    }

    public ApiResult<DashboardResumenDto> obtenerResumen() {
        return dashboardApi.getResumen();
    }
}
