package com.marcosmoreiradev.uensdesktop.app;

import com.marcosmoreiradev.uensdesktop.api.client.ApiClient;
import com.marcosmoreiradev.uensdesktop.api.client.ApiConfig;
import com.marcosmoreiradev.uensdesktop.common.config.AppProperties;
import com.marcosmoreiradev.uensdesktop.common.config.AppPropertiesLoader;
import com.marcosmoreiradev.uensdesktop.common.i18n.I18n;
import com.marcosmoreiradev.uensdesktop.nav.Navigator;
import com.marcosmoreiradev.uensdesktop.ui.feedback.UiFeedbackService;
import com.marcosmoreiradev.uensdesktop.ui.feedback.UiNotificationsState;
import com.marcosmoreiradev.uensdesktop.nav.ViewRegistry;
import com.marcosmoreiradev.uensdesktop.session.SessionState;
import java.util.ResourceBundle;

/**
 * Composition root snapshot shared with controllers and reusable frontend infrastructure.
 *
 * <p>The record groups the singleton services that define the runtime environment of the desktop:
 * session, API client, navigation, feedback, service registry and i18n resources.
 *
 * @param properties resolved application properties
 * @param apiConfig effective API configuration derived from properties
 * @param sessionState shared authenticated session state
 * @param navigator central view navigator
 * @param apiClient singleton HTTP client used by the services
 * @param services registry of application services
 * @param uiNotifications transient notification state shared across screens
 * @param feedback dialog and toast facade used by controllers
 * @param controllerFactory controller factory used by FXMLLoader
 * @param resourceBundle active i18n bundle for the UI
 */
public record AppContext(
        AppProperties properties,
        ApiConfig apiConfig,
        SessionState sessionState,
        Navigator navigator,
        ApiClient apiClient,
        ApplicationServices services,
        UiNotificationsState uiNotifications,
        UiFeedbackService feedback,
        ControllerFactory controllerFactory,
        ResourceBundle resourceBundle) {

    /**
     * Builds the default desktop runtime wiring used by production launches and tests that rely on
     * the real composition root.
     *
     * @return fully wired application context with navigator and controller factory initialized
     */
    public static AppContext createDefault() {
        AppProperties properties = AppPropertiesLoader.load();
        ApiConfig apiConfig = new ApiConfig(properties.baseUrl(), properties.timeoutSeconds());
        SessionState sessionState = new SessionState();
        Navigator navigator = new Navigator(new ViewRegistry(), sessionState);
        ApiClient apiClient = new ApiClient(apiConfig, sessionState);
        ApplicationServices services = ApplicationServices.create(apiClient);
        UiNotificationsState uiNotifications = new UiNotificationsState();
        UiFeedbackService feedback = new UiFeedbackService(uiNotifications);
        ResourceBundle bundle = I18n.bundle();
        ControllerFactory controllerFactory = new ControllerFactory(null);

        AppContext context =
                new AppContext(
                        properties,
                        apiConfig,
                        sessionState,
                        navigator,
                        apiClient,
                        services,
                        uiNotifications,
                        feedback,
                        controllerFactory,
                        bundle);

        controllerFactory.setAppContext(context);
        navigator.setAppContext(context);
        return context;
    }
}
