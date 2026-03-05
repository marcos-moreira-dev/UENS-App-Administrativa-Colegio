package com.marcosmoreiradev.uensdesktop.app;

import com.marcosmoreiradev.uensdesktop.api.client.ApiClient;
import com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.AsignaturasApi;
import com.marcosmoreiradev.uensdesktop.api.modules.auditoria.AuditoriaApi;
import com.marcosmoreiradev.uensdesktop.api.modules.auth.AuthApi;
import com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.CalificacionesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.clases.ClasesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.dashboard.DashboardApi;
import com.marcosmoreiradev.uensdesktop.api.modules.docentes.DocentesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.EstudiantesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.ReportesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.representantes.RepresentantesApi;
import com.marcosmoreiradev.uensdesktop.api.modules.secciones.SeccionesApi;
import com.marcosmoreiradev.uensdesktop.modules.asignaturas.application.AsignaturasService;
import com.marcosmoreiradev.uensdesktop.modules.auditoria.application.AuditoriaService;
import com.marcosmoreiradev.uensdesktop.modules.auth.application.AuthService;
import com.marcosmoreiradev.uensdesktop.modules.calificaciones.application.CalificacionesReferenceDataService;
import com.marcosmoreiradev.uensdesktop.modules.calificaciones.application.CalificacionesService;
import com.marcosmoreiradev.uensdesktop.modules.clases.application.ClasesReferenceDataService;
import com.marcosmoreiradev.uensdesktop.modules.clases.application.ClasesService;
import com.marcosmoreiradev.uensdesktop.modules.dashboard.application.DashboardService;
import com.marcosmoreiradev.uensdesktop.modules.docentes.application.DocentesService;
import com.marcosmoreiradev.uensdesktop.modules.estudiantes.application.EstudiantesReferenceDataService;
import com.marcosmoreiradev.uensdesktop.modules.estudiantes.application.EstudiantesService;
import com.marcosmoreiradev.uensdesktop.modules.reportes.application.ReportePollingService;
import com.marcosmoreiradev.uensdesktop.modules.reportes.application.ReportesReferenceDataService;
import com.marcosmoreiradev.uensdesktop.modules.reportes.application.ReportesService;
import com.marcosmoreiradev.uensdesktop.modules.representantes.application.RepresentantesService;
import com.marcosmoreiradev.uensdesktop.modules.secciones.application.SeccionesService;

/**
 * Registry of application services shared by the desktop controllers.
 *
 * <p>The class centralizes singleton service creation so controllers receive cohesive use-case
 * entry points instead of constructing APIs or services on their own.
 */
public final class ApplicationServices {

    private final AuthService authService;
    private final DashboardService dashboardService;
    private final EstudiantesService estudiantesService;
    private final EstudiantesReferenceDataService estudiantesReferenceDataService;
    private final RepresentantesService representantesService;
    private final DocentesService docentesService;
    private final SeccionesService seccionesService;
    private final AsignaturasService asignaturasService;
    private final ClasesService clasesService;
    private final ClasesReferenceDataService clasesReferenceDataService;
    private final CalificacionesService calificacionesService;
    private final CalificacionesReferenceDataService calificacionesReferenceDataService;
    private final ReportesService reportesService;
    private final ReportesReferenceDataService reportesReferenceDataService;
    private final AuditoriaService auditoriaService;

    private ApplicationServices(
            AuthService authService,
            DashboardService dashboardService,
            EstudiantesService estudiantesService,
            EstudiantesReferenceDataService estudiantesReferenceDataService,
            RepresentantesService representantesService,
            DocentesService docentesService,
            SeccionesService seccionesService,
            AsignaturasService asignaturasService,
            ClasesService clasesService,
            ClasesReferenceDataService clasesReferenceDataService,
            CalificacionesService calificacionesService,
            CalificacionesReferenceDataService calificacionesReferenceDataService,
            ReportesService reportesService,
            ReportesReferenceDataService reportesReferenceDataService,
            AuditoriaService auditoriaService) {
        this.authService = authService;
        this.dashboardService = dashboardService;
        this.estudiantesService = estudiantesService;
        this.estudiantesReferenceDataService = estudiantesReferenceDataService;
        this.representantesService = representantesService;
        this.docentesService = docentesService;
        this.seccionesService = seccionesService;
        this.asignaturasService = asignaturasService;
        this.clasesService = clasesService;
        this.clasesReferenceDataService = clasesReferenceDataService;
        this.calificacionesService = calificacionesService;
        this.calificacionesReferenceDataService = calificacionesReferenceDataService;
        this.reportesService = reportesService;
        this.reportesReferenceDataService = reportesReferenceDataService;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Creates the shared service registry from the singleton API client.
     *
     * @param apiClient transport client already configured with session and base URL
     * @return registry containing reusable services and reference-data helpers
     */
    public static ApplicationServices create(ApiClient apiClient) {
        AuthApi authApi = new AuthApi(apiClient);
        DashboardApi dashboardApi = new DashboardApi(apiClient);
        RepresentantesApi representantesApi = new RepresentantesApi(apiClient);
        DocentesApi docentesApi = new DocentesApi(apiClient);
        SeccionesApi seccionesApi = new SeccionesApi(apiClient);
        AsignaturasApi asignaturasApi = new AsignaturasApi(apiClient);
        ClasesApi clasesApi = new ClasesApi(apiClient);
        EstudiantesApi estudiantesApi = new EstudiantesApi(apiClient);
        CalificacionesApi calificacionesApi = new CalificacionesApi(apiClient);
        ReportesApi reportesApi = new ReportesApi(apiClient);
        AuditoriaApi auditoriaApi = new AuditoriaApi(apiClient);

        AuthService authService = new AuthService(authApi);
        DashboardService dashboardService = new DashboardService(dashboardApi);
        RepresentantesService representantesService = new RepresentantesService(representantesApi);
        DocentesService docentesService = new DocentesService(docentesApi);
        SeccionesService seccionesService = new SeccionesService(seccionesApi);
        AsignaturasService asignaturasService = new AsignaturasService(asignaturasApi);
        ClasesService clasesService = new ClasesService(clasesApi);
        EstudiantesService estudiantesService = new EstudiantesService(estudiantesApi);
        CalificacionesService calificacionesService = new CalificacionesService(calificacionesApi);
        ReportesService reportesService = new ReportesService(reportesApi);
        AuditoriaService auditoriaService = new AuditoriaService(auditoriaApi);

        return new ApplicationServices(
                authService,
                dashboardService,
                estudiantesService,
                new EstudiantesReferenceDataService(representantesApi, seccionesApi),
                representantesService,
                docentesService,
                seccionesService,
                asignaturasService,
                clasesService,
                new ClasesReferenceDataService(seccionesApi, asignaturasApi, docentesApi),
                calificacionesService,
                new CalificacionesReferenceDataService(estudiantesApi, clasesApi),
                reportesService,
                new ReportesReferenceDataService(seccionesApi),
                auditoriaService);
    }

    /**
     * @return authentication use cases for login, logout and session bootstrap
     */
    public AuthService authService() {
        return authService;
    }

    /**
     * @return dashboard read model service
     */
    public DashboardService dashboardService() {
        return dashboardService;
    }

    /**
     * @return students use-case service
     */
    public EstudiantesService estudiantesService() {
        return estudiantesService;
    }

    /**
     * @return service that loads student reference catalogs such as representatives and sections
     */
    public EstudiantesReferenceDataService estudiantesReferenceDataService() {
        return estudiantesReferenceDataService;
    }

    /**
     * @return representatives CRUD service
     */
    public RepresentantesService representantesService() {
        return representantesService;
    }

    /**
     * @return teachers CRUD service
     */
    public DocentesService docentesService() {
        return docentesService;
    }

    /**
     * @return sections CRUD service
     */
    public SeccionesService seccionesService() {
        return seccionesService;
    }

    /**
     * @return subjects CRUD service
     */
    public AsignaturasService asignaturasService() {
        return asignaturasService;
    }

    /**
     * @return classes CRUD service
     */
    public ClasesService clasesService() {
        return clasesService;
    }

    /**
     * @return reference-data service for classes filters and forms
     */
    public ClasesReferenceDataService clasesReferenceDataService() {
        return clasesReferenceDataService;
    }

    /**
     * @return grades CRUD service
     */
    public CalificacionesService calificacionesService() {
        return calificacionesService;
    }

    /**
     * @return reference-data service used by grades filters and forms
     */
    public CalificacionesReferenceDataService calificacionesReferenceDataService() {
        return calificacionesReferenceDataService;
    }

    /**
     * @return asynchronous reports service
     */
    public ReportesService reportesService() {
        return reportesService;
    }

    /**
     * @return reports reference-data service
     */
    public ReportesReferenceDataService reportesReferenceDataService() {
        return reportesReferenceDataService;
    }

    /**
     * Creates a short-lived polling helper bound to the shared reports service.
     *
     * @return new polling service instance ready to monitor a report request
     */
    public ReportePollingService createReportePollingService() {
        return new ReportePollingService(reportesService);
    }

    /**
     * @return auditing service used by the admin module
     */
    public AuditoriaService auditoriaService() {
        return auditoriaService;
    }
}
