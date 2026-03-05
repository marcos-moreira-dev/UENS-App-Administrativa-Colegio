package com.marcosmoreiradev.uensdesktop.api.modules.dashboard;

import com.marcosmoreiradev.uensdesktop.api.client.ApiClient;
import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.modules.dashboard.dto.DashboardResumenDto;

/**
 * Dashboard endpoint wrapper used to load the summary indicators shown by the shell.
 */
public final class DashboardApi {

    private static final String DASHBOARD_RESUMEN_PATH = "/api/v1/dashboard/resumen";

    private final ApiClient apiClient;

    /**
     * Creates the dashboard API wrapper around the shared transport client.
     *
     * @param apiClient shared low-level client used to perform HTTP requests
     */
    public DashboardApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Loads the dashboard summary snapshot for the authenticated user.
     *
     * @return backend summary with the main KPIs rendered by the home screen
     */
    public ApiResult<DashboardResumenDto> getResumen() {
        return apiClient.get(DASHBOARD_RESUMEN_PATH, DashboardResumenDto.class, true);
    }
}
