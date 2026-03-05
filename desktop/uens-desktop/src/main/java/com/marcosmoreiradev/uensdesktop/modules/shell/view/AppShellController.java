package com.marcosmoreiradev.uensdesktop.modules.shell.view;

import com.marcosmoreiradev.uensdesktop.app.AppContext;
import com.marcosmoreiradev.uensdesktop.app.ContextAwareController;
import com.marcosmoreiradev.uensdesktop.common.constants.UiDefaults;
import com.marcosmoreiradev.uensdesktop.nav.ViewId;
import com.marcosmoreiradev.uensdesktop.session.Role;
import com.marcosmoreiradev.uensdesktop.ui.assets.FxUiAssets;
import com.marcosmoreiradev.uensdesktop.ui.assets.UiAssetCatalog;
import com.marcosmoreiradev.uensdesktop.ui.assets.UiAssetId;
import com.marcosmoreiradev.uensdesktop.ui.assets.ViewArtworkSupport;
import com.marcosmoreiradev.uensdesktop.ui.effects.DrawerBackdropEvent;
import com.marcosmoreiradev.uensdesktop.ui.feedback.UiNotificationsState;
import com.marcosmoreiradev.uensdesktop.ui.tooltip.TooltipSupport;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

public final class AppShellController implements ContextAwareController {

    private static final PseudoClass ACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("active");

    @FXML
    private BorderPane contentHost;

    @FXML
    private BorderPane shellContainer;

    @FXML
    private StackPane rootPane;

    @FXML
    private Label sessionStatusLabel;

    @FXML
    private Label logShortcutHintLabel;

    @FXML
    private Region shellBackdropOverlay;

    @FXML
    private StackPane shellDrawerHost;

    @FXML
    private VBox toastHost;

    @FXML
    private Label toastLabel;

    @FXML
    private Button logoutNavButton;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button estudiantesButton;

    @FXML
    private Button representantesButton;

    @FXML
    private Button docentesButton;

    @FXML
    private Button seccionesButton;

    @FXML
    private Button asignaturasButton;

    @FXML
    private Button clasesButton;

    @FXML
    private Button calificacionesButton;

    @FXML
    private Button reportesButton;

    @FXML
    private Button auditoriaButton;

    @FXML
    private ImageView brandLogoView;

    @FXML
    private ImageView sidebarArtworkView;

    private AppContext appContext;
    private final PauseTransition toastTimer = new PauseTransition(Duration.millis(UiDefaults.TOAST_AUTO_HIDE_MS));
    private AudioClip logoutSound;
    private Timeline shellBackdropTimeline;
    private DrawerMount activeDrawerMount;
    private final GaussianBlur shellBlur = new GaussianBlur(0.0);
    private boolean initialized;
    private boolean contextApplied;

    @FXML
    private void initialize() {
        FxUiAssets.audioClip(UiAssetId.LOGOUT_TICK).ifPresent(audioClip -> {
            logoutSound = audioClip;
            logoutSound.setVolume(UiDefaults.LOGOUT_SOUND_VOLUME);
        });
        FxUiAssets.image(UiAssetId.BRAND_ICON).ifPresent(brandLogoView::setImage);
        shellContainer.setEffect(shellBlur);
        toastHost.setVisible(false);
        toastHost.setManaged(false);
        toastTimer.setOnFinished(event -> hideToast());
        installNavigationTooltips();
        rootPane.addEventHandler(DrawerBackdropEvent.ANY, this::handleDrawerBackdropEvent);
        refreshArtwork(ViewId.LOGIN);
        initialized = true;
        applyContextIfReady();
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
        applyContextIfReady();
    }

    public void activateInitialView() {
        appContext.navigator().setContentHost(contentHost);
        refreshSessionStatus();
        appContext.navigator().navigate(ViewId.LOGIN);
        refreshActiveNavigation(ViewId.LOGIN);
    }

    @FXML
    private void onGoToDashboard() {
        appContext.navigator().navigate(ViewId.DASHBOARD);
        refreshSessionStatus();
    }

    @FXML
    private void onLogoutNav() {
        playLogoutSound();
        appContext.sessionState().logout();
        appContext.navigator().navigate(ViewId.LOGIN);
        refreshSessionStatus();
    }

    private void refreshSessionStatus() {
        boolean loggedIn = appContext.sessionState().usuario().isPresent();
        String sessionLabel = appContext.sessionState().usuario()
                .map(usuario -> "Sesi\u00f3n: " + usuario.login() + " (" + usuario.rol().name() + ")")
                .orElse("Sesi\u00f3n: Invitado");
        sessionStatusLabel.setText(sessionLabel);
        sessionStatusLabel.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.98);"
                        + "-fx-font-weight: 800;"
                        + "-fx-background-color: rgba(14,24,34,0.28);"
                        + "-fx-border-color: rgba(255,255,255,0.26);"
                        + "-fx-background-radius: 999;"
                        + "-fx-border-radius: 999;"
                        + "-fx-padding: 8 14 8 14;"
        );
        boolean disabled = !loggedIn;
        boolean admin = appContext.sessionState().role().map(role -> role == Role.ADMIN).orElse(false);
        logShortcutHintLabel.setVisible(!loggedIn);
        logShortcutHintLabel.setManaged(!loggedIn);
        setNavButtonAvailability(logoutNavButton, loggedIn);
        setNavButtonAvailability(dashboardButton, loggedIn);
        setNavButtonAvailability(estudiantesButton, loggedIn);
        setNavButtonAvailability(representantesButton, loggedIn);
        setNavButtonAvailability(docentesButton, loggedIn);
        setNavButtonAvailability(seccionesButton, loggedIn);
        setNavButtonAvailability(asignaturasButton, loggedIn);
        setNavButtonAvailability(clasesButton, loggedIn);
        setNavButtonAvailability(calificacionesButton, loggedIn);
        setNavButtonAvailability(reportesButton, loggedIn);
        setNavButtonAvailability(auditoriaButton, loggedIn && admin);
        logoutNavButton.setDisable(!loggedIn);
        dashboardButton.setDisable(disabled);
        estudiantesButton.setDisable(disabled);
        representantesButton.setDisable(disabled);
        docentesButton.setDisable(disabled);
        seccionesButton.setDisable(disabled);
        asignaturasButton.setDisable(disabled);
        clasesButton.setDisable(disabled);
        calificacionesButton.setDisable(disabled);
        reportesButton.setDisable(disabled);
        auditoriaButton.setDisable(disabled || !admin);
    }

    @FXML
    private void onGoToEstudiantes() {
        appContext.navigator().navigate(ViewId.ESTUDIANTES);
        refreshSessionStatus();
    }

    @FXML
    private void onGoToRepresentantes() {
        appContext.navigator().navigate(ViewId.REPRESENTANTES);
        refreshSessionStatus();
    }

    @FXML
    private void onGoToDocentes() {
        appContext.navigator().navigate(ViewId.DOCENTES);
        refreshSessionStatus();
    }

    @FXML
    private void onGoToSecciones() {
        appContext.navigator().navigate(ViewId.SECCIONES);
        refreshSessionStatus();
    }

    @FXML
    private void onGoToAsignaturas() {
        appContext.navigator().navigate(ViewId.ASIGNATURAS);
        refreshSessionStatus();
    }

    @FXML
    private void onGoToClases() {
        appContext.navigator().navigate(ViewId.CLASES);
        refreshSessionStatus();
    }

    @FXML
    private void onGoToCalificaciones() {
        appContext.navigator().navigate(ViewId.CALIFICACIONES);
        refreshSessionStatus();
    }

    @FXML
    private void onGoToReportes() {
        appContext.navigator().navigate(ViewId.REPORTES);
        refreshSessionStatus();
    }

    @FXML
    private void onGoToAuditoria() {
        appContext.navigator().navigate(ViewId.AUDITORIA);
        refreshSessionStatus();
    }

    private void applyContextIfReady() {
        if (!initialized || appContext == null || contextApplied) {
            return;
        }
        contextApplied = true;
        this.appContext.sessionState().usuarioProperty().addListener((observable, oldValue, newValue) -> {
            refreshSessionStatus();
            if (oldValue != null && newValue == null) {
                appContext.navigator().navigate(ViewId.LOGIN);
            }
        });
        this.appContext.navigator().currentViewProperty().addListener((observable, oldValue, newValue) -> {
            refreshActiveNavigation(newValue);
            refreshArtwork(newValue);
        });
        this.appContext.uiNotifications().flashMessageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showToast(newValue);
            }
        });
    }

    private void showToast(UiNotificationsState.FlashMessage flashMessage) {
        toastLabel.setText(flashMessage.message());
        toastHost.getStyleClass().removeAll("toast-info", "toast-success", "toast-warning", "toast-error");
        toastHost.getStyleClass().add(switch (flashMessage.level()) {
            case SUCCESS -> "toast-success";
            case WARNING -> "toast-warning";
            case ERROR -> "toast-error";
            case INFO -> "toast-info";
        });
        toastHost.setVisible(true);
        toastHost.setManaged(true);
        toastTimer.playFromStart();
    }

    private void hideToast() {
        toastHost.setVisible(false);
        toastHost.setManaged(false);
    }

    private void playLogoutSound() {
        if (logoutSound == null) {
            return;
        }
        try {
            logoutSound.play();
        } catch (RuntimeException ignored) {
            // Ignore audio backend issues so logout flow is never blocked.
        }
    }

    private void refreshActiveNavigation(ViewId currentView) {
        setNavButtonState(logoutNavButton, false);
        setNavButtonState(dashboardButton, currentView == ViewId.DASHBOARD);
        setNavButtonState(estudiantesButton, currentView == ViewId.ESTUDIANTES);
        setNavButtonState(representantesButton, currentView == ViewId.REPRESENTANTES);
        setNavButtonState(docentesButton, currentView == ViewId.DOCENTES);
        setNavButtonState(seccionesButton, currentView == ViewId.SECCIONES);
        setNavButtonState(asignaturasButton, currentView == ViewId.ASIGNATURAS);
        setNavButtonState(clasesButton, currentView == ViewId.CLASES);
        setNavButtonState(calificacionesButton, currentView == ViewId.CALIFICACIONES);
        setNavButtonState(reportesButton, currentView == ViewId.REPORTES);
        setNavButtonState(auditoriaButton, currentView == ViewId.AUDITORIA);
    }

    private void refreshArtwork(ViewId currentView) {
        ViewId resolvedView = currentView == null ? ViewId.LOGIN : currentView;
        if (resolvedView == ViewId.LOGIN) {
            sidebarArtworkView.setImage(null);
            sidebarArtworkView.setVisible(false);
            ViewArtworkSupport.applyToView(rootPane, resolvedView);
            return;
        }
        sidebarArtworkView.setVisible(true);
        FxUiAssets.image(UiAssetCatalog.artworkFor(resolvedView)).ifPresent(image -> {
            sidebarArtworkView.setImage(image);
            sidebarArtworkView.setOpacity(UiDefaults.SIDEBAR_ARTWORK_OPACITY);
        });
        ViewArtworkSupport.applyToView(rootPane, resolvedView);
    }

    private void installNavigationTooltips() {
        TooltipSupport.install(logoutNavButton,
                "Cierra la sesi\u00f3n administrativa actual y devuelve el escritorio a la pantalla de acceso.");
        TooltipSupport.install(dashboardButton,
                "Resume matr\u00edcula, carga acad\u00e9mica y pulso operativo para abrir la jornada con contexto.");
        TooltipSupport.install(estudiantesButton,
                "Permite registrar, corregir y consultar fichas estudiantiles ligadas a secci\u00f3n y representante.");
        TooltipSupport.install(representantesButton,
                "Administra responsables legales para mantener contacto, autorizaciones y v\u00ednculos con estudiantes.");
        TooltipSupport.install(docentesButton,
                "Gestiona la planta docente disponible para asignar clases, horarios y seguimiento acad\u00e9mico.");
        TooltipSupport.install(seccionesButton,
                "Organiza grado, paralelo, cupo y a\u00f1o lectivo para sostener matr\u00edcula y distribuci\u00f3n acad\u00e9mica.");
        TooltipSupport.install(asignaturasButton,
                "Mantiene el cat\u00e1logo curricular por grado que luego se usa en clases, reportes y evaluaci\u00f3n.");
        TooltipSupport.install(clasesButton,
                "Programa la oferta real por secci\u00f3n, asignatura, docente y horario dentro del per\u00edodo lectivo.");
        TooltipSupport.install(calificacionesButton,
                "Registra y consulta notas por parcial sobre clases reales para seguimiento del rendimiento.");
        TooltipSupport.install(reportesButton,
                "Solicita reportes as\u00edncronos, revisa estados y descarga evidencias para gesti\u00f3n administrativa.");
        TooltipSupport.install(auditoriaButton,
                "Supervisa qui\u00e9n hizo qu\u00e9, cu\u00e1ndo y con qu\u00e9 resultado para control interno y trazabilidad.");
    }

    private void setNavButtonState(Button button, boolean active) {
        if (button == null) {
            return;
        }
        button.pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, active);
    }

    private void setNavButtonAvailability(Button button, boolean visible) {
        if (button == null) {
            return;
        }
        button.setVisible(visible);
        button.setManaged(visible);
    }

    private void handleDrawerBackdropEvent(DrawerBackdropEvent drawerEvent) {
        if (drawerEvent.getEventType() == DrawerBackdropEvent.SHOW) {
            showShellBackdrop(drawerEvent);
        } else if (drawerEvent.getEventType() == DrawerBackdropEvent.HIDE) {
            hideShellBackdrop();
        }
    }

    private void showShellBackdrop(DrawerBackdropEvent event) {
        if (shellBackdropTimeline != null) {
            shellBackdropTimeline.stop();
        }
        mountDrawer(event);
        shellBackdropOverlay.setStyle("-fx-background-color: rgba(6, 12, 20, " + UiDefaults.DRAWER_BACKDROP_DIM_OPACITY + ");");
        shellBackdropOverlay.setManaged(true);
        shellBackdropOverlay.setVisible(true);
        shellBackdropOverlay.setMouseTransparent(false);
        shellDrawerHost.setManaged(true);
        shellDrawerHost.setVisible(true);
        shellDrawerHost.setMouseTransparent(false);
        shellBackdropTimeline = new Timeline(
                new KeyFrame(
                        Duration.millis(UiDefaults.DRAWER_BACKDROP_ANIMATION_MS),
                        new KeyValue(shellBackdropOverlay.opacityProperty(), 1.0),
                        new KeyValue(shellBlur.radiusProperty(), UiDefaults.DRAWER_BACKDROP_BLUR_RADIUS)));
        shellBackdropTimeline.playFromStart();
    }

    private void hideShellBackdrop() {
        if (shellBackdropTimeline != null) {
            shellBackdropTimeline.stop();
        }
        shellBackdropTimeline = new Timeline(
                new KeyFrame(
                        Duration.millis(UiDefaults.DRAWER_BACKDROP_ANIMATION_MS),
                        finishEvent -> {
                            shellBackdropOverlay.setMouseTransparent(true);
                            shellBackdropOverlay.setVisible(false);
                            shellBackdropOverlay.setManaged(false);
                            shellDrawerHost.setMouseTransparent(true);
                            shellDrawerHost.setVisible(false);
                            shellDrawerHost.setManaged(false);
                            unmountDrawer();
                        },
                        new KeyValue(shellBackdropOverlay.opacityProperty(), 0.0),
                        new KeyValue(shellBlur.radiusProperty(), 0.0)));
        shellBackdropTimeline.playFromStart();
    }

    private void mountDrawer(DrawerBackdropEvent event) {
        if (event.drawerNode() == null || event.originHost() == null) {
            return;
        }
        if (activeDrawerMount != null && activeDrawerMount.drawerNode() == event.drawerNode()) {
            return;
        }
        unmountDrawer();
        StackPane.setAlignment(event.drawerNode(), Pos.CENTER);
        StackPane.setMargin(event.drawerNode(), new javafx.geometry.Insets(38));
        event.drawerNode().setMaxHeight(Math.max(520.0, rootPane.getHeight() - 76.0));
        event.originHost().getChildren().remove(event.drawerNode());
        shellDrawerHost.getChildren().setAll(event.drawerNode());
        activeDrawerMount = new DrawerMount(event.drawerNode(), event.originHost());
    }

    private void unmountDrawer() {
        if (activeDrawerMount == null) {
            return;
        }
        shellDrawerHost.getChildren().remove(activeDrawerMount.drawerNode());
        StackPane.setAlignment(activeDrawerMount.drawerNode(), Pos.CENTER_RIGHT);
        StackPane.setMargin(activeDrawerMount.drawerNode(), null);
        activeDrawerMount.drawerNode().setMaxHeight(Region.USE_COMPUTED_SIZE);
        if (!activeDrawerMount.originHost().getChildren().contains(activeDrawerMount.drawerNode())) {
            activeDrawerMount.originHost().getChildren().add(activeDrawerMount.drawerNode());
        }
        activeDrawerMount = null;
    }

    private record DrawerMount(Region drawerNode, StackPane originHost) {
    }
}
