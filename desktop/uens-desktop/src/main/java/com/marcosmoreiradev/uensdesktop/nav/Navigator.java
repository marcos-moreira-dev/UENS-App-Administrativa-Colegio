package com.marcosmoreiradev.uensdesktop.nav;

import com.marcosmoreiradev.uensdesktop.app.AppContext;
import com.marcosmoreiradev.uensdesktop.ui.view.ViewEnhancer;
import java.io.IOException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

/**
 * Centralizes view loading and role-aware navigation inside the desktop shell.
 */
public final class Navigator {

    private final ViewRegistry viewRegistry;
    private final com.marcosmoreiradev.uensdesktop.session.SessionState sessionState;
    private final ObjectProperty<ViewId> currentView = new SimpleObjectProperty<>(ViewId.LOGIN);
    private BorderPane contentHost;
    private AppContext appContext;

    /**
     * Creates a navigator bound to the registered routes and the shared session state.
     *
     * @param viewRegistry route registry that maps logical views to FXML definitions
     * @param sessionState authenticated session used for access checks
     */
    public Navigator(ViewRegistry viewRegistry, com.marcosmoreiradev.uensdesktop.session.SessionState sessionState) {
        this.viewRegistry = viewRegistry;
        this.sessionState = sessionState;
    }

    /**
     * Validates whether the current session may open the requested view.
     *
     * @param viewId target logical view
     * @return {@code true} when the route exists and the current role is allowed
     */
    public boolean canNavigate(ViewId viewId) {
        return viewRegistry.find(viewId)
                .map(route -> route.publicView() || sessionState.role().map(route.allowedRoles()::contains).orElse(false))
                .orElse(false);
    }

    /**
     * Loads and displays the requested view when the route is allowed for the current session.
     *
     * @param viewId target logical view to render in the content host
     */
    public void navigate(ViewId viewId) {
        ensureReady();
        if (canNavigate(viewId)) {
            currentView.set(viewId);
            contentHost.setCenter(loadView(viewId));
        }
    }

    /**
     * Defines the shell container where loaded views should be rendered.
     *
     * @param contentHost border pane that hosts the active view in its center region
     */
    public void setContentHost(BorderPane contentHost) {
        this.contentHost = contentHost;
    }

    /**
     * Exposes the currently displayed logical view for shell bindings.
     *
     * @return observable property containing the active view id
     */
    public ObjectProperty<ViewId> currentViewProperty() {
        return currentView;
    }

    /**
     * Injects the runtime application context required to load controllers and resources.
     *
     * @param appContext fully wired runtime context
     */
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }

    /**
     * Loads the FXML associated with the requested route and applies cross-cutting view enhancers.
     *
     * @param viewId logical view to materialize
     * @return JavaFX root node for the loaded view
     */
    private Parent loadView(ViewId viewId) {
        RouteDefinition route = viewRegistry.find(viewId)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no registrada: " + viewId));
        FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(route.fxmlPath()), appContext.resourceBundle());
        loader.setControllerFactory(appContext.controllerFactory());
        try {
            Parent root = loader.load();
            ViewEnhancer.enhance(root, viewId);
            return root;
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo cargar la vista " + route.fxmlPath(), ex);
        }
    }

    /**
     * Ensures the navigator has the minimal runtime wiring required to replace views safely.
     */
    private void ensureReady() {
        if (contentHost == null || appContext == null) {
            throw new IllegalStateException("Navigator no inicializado");
        }
    }
}
