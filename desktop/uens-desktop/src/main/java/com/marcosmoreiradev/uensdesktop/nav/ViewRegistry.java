package com.marcosmoreiradev.uensdesktop.nav;

import com.marcosmoreiradev.uensdesktop.session.Role;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Registers the logical views exposed by the desktop and the roles allowed to open them.
 */
public final class ViewRegistry {

    private final Map<ViewId, RouteDefinition> routes = new EnumMap<>(ViewId.class);

    /**
     * Populates the default route map used by the desktop shell.
     */
    public ViewRegistry() {
        routes.put(ViewId.LOGIN, new RouteDefinition("/fxml/auth/LoginView.fxml", Set.of(), true));
        routes.put(ViewId.DASHBOARD, new RouteDefinition("/fxml/dashboard/DashboardView.fxml", Set.of(Role.ADMIN, Role.SECRETARIA), false));
        routes.put(ViewId.ESTUDIANTES, new RouteDefinition("/fxml/estudiantes/EstudiantesView.fxml", Set.of(Role.ADMIN, Role.SECRETARIA), false));
        routes.put(ViewId.REPRESENTANTES, new RouteDefinition("/fxml/representantes/RepresentantesView.fxml", Set.of(Role.ADMIN, Role.SECRETARIA), false));
        routes.put(ViewId.DOCENTES, new RouteDefinition("/fxml/docentes/DocentesView.fxml", Set.of(Role.ADMIN, Role.SECRETARIA), false));
        routes.put(ViewId.SECCIONES, new RouteDefinition("/fxml/secciones/SeccionesView.fxml", Set.of(Role.ADMIN, Role.SECRETARIA), false));
        routes.put(ViewId.ASIGNATURAS, new RouteDefinition("/fxml/asignaturas/AsignaturasView.fxml", Set.of(Role.ADMIN, Role.SECRETARIA), false));
        routes.put(ViewId.CLASES, new RouteDefinition("/fxml/clases/ClasesView.fxml", Set.of(Role.ADMIN, Role.SECRETARIA), false));
        routes.put(ViewId.CALIFICACIONES, new RouteDefinition("/fxml/calificaciones/CalificacionesView.fxml", Set.of(Role.ADMIN, Role.SECRETARIA), false));
        routes.put(ViewId.REPORTES, new RouteDefinition("/fxml/reportes/ReportesView.fxml", Set.of(Role.ADMIN, Role.SECRETARIA), false));
        routes.put(ViewId.AUDITORIA, new RouteDefinition("/fxml/auditoria/AuditoriaView.fxml", Set.of(Role.ADMIN), false));
    }

    /**
     * Finds the route configuration for a logical view id.
     *
     * @param viewId logical view requested by navigation
     * @return optional route definition when the view is registered
     */
    public Optional<RouteDefinition> find(ViewId viewId) {
        return Optional.ofNullable(routes.get(viewId));
    }
}
