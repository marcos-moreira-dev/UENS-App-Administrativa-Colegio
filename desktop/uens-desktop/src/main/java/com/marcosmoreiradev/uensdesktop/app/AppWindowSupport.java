package com.marcosmoreiradev.uensdesktop.app;

import com.marcosmoreiradev.uensdesktop.common.constants.UiDefaults;
import com.marcosmoreiradev.uensdesktop.common.util.DesktopFileSupport;
import com.marcosmoreiradev.uensdesktop.modules.auth.application.AuthService;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

final class AppWindowSupport {

    private final Stage stage;
    private final Scene scene;
    private final AppContext appContext;
    private final AuthService authService;
    private final AtomicBoolean shutdownRequested = new AtomicBoolean(false);
    private final AtomicBoolean exitTriggered = new AtomicBoolean(false);

    private AppWindowSupport(Stage stage, Scene scene, AppContext appContext) {
        this.stage = stage;
        this.scene = scene;
        this.appContext = appContext;
        this.authService = appContext.services().authService();
    }

    static void install(Stage stage, Scene scene, AppContext appContext) {
        new AppWindowSupport(stage, scene, appContext).attach();
    }

    private void attach() {
        stage.setFullScreenExitHint(UiDefaults.FULLSCREEN_EXIT_HINT);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
        stage.setOnCloseRequest(this::handleCloseRequest);
    }

    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.F11) {
            stage.setFullScreen(!stage.isFullScreen());
            event.consume();
            return;
        }
        if (event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.L) {
            exportFrontendLogReport();
            event.consume();
        }
    }

    private void handleCloseRequest(WindowEvent event) {
        if (!shutdownRequested.compareAndSet(false, true)) {
            event.consume();
            return;
        }

        event.consume();
        if (appContext.sessionState().token().isEmpty()) {
            exitApplication();
            return;
        }

        if (!confirmForcedLogout()) {
            shutdownRequested.set(false);
            return;
        }

        scene.getRoot().setDisable(true);
        PauseTransition fallbackExit = new PauseTransition(Duration.millis(UiDefaults.APP_CLOSE_LOGOUT_TIMEOUT_MS));
        fallbackExit.setOnFinished(ignored -> exitApplication());
        fallbackExit.playFromStart();

        Thread remoteLogoutThread = new Thread(() -> {
            try {
                authService.logout(appContext.sessionState().refreshToken().orElse(null));
            } catch (RuntimeException ignored) {
                // Logout remoto es best-effort; el cierre local no debe depender del backend.
            }

            Platform.runLater(() -> {
                fallbackExit.stop();
                exitApplication();
            });
        }, "uens-window-close-logout");
        remoteLogoutThread.setDaemon(true);
        remoteLogoutThread.start();
    }

    private boolean confirmForcedLogout() {
        return appContext.feedback().confirm(
                stage,
                "Confirmar cierre de la aplicaci\u00f3n",
                "La sesi\u00f3n actual se cerrar\u00e1 de forma obligatoria",
                "Est\u00e1s saliendo con el bot\u00f3n X de la ventana.\n\n"
                        + "UENS Desktop intentar\u00e1 cerrar sesi\u00f3n en el backend y limpiar\u00e1 la sesi\u00f3n local aunque el servidor no responda.\n"
                        + "Este cierre forzado evita dejar tokens activos y ayuda a la trazabilidad para auditor\u00eda.\n\n"
                        + "Si prefieres el flujo normal, cancela y usa el bot\u00f3n Cerrar sesi\u00f3n.",
                "Cerrar sesi\u00f3n y salir",
                "Cancelar");
    }

    private void exportFrontendLogReport() {
        Path targetPath = chooseLogReportTargetPath();
        if (targetPath == null) {
            return;
        }
        try {
            Path reportPath = FrontendLogReportExporter.export(appContext, stage, targetPath);
            showExportSuccessDialog(reportPath);
        } catch (RuntimeException ex) {
            showExportFailureDialog();
        }
    }

    private Path chooseLogReportTargetPath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar informe de logs del frontend");
        File initialDirectory = FrontendLogReportExporter.defaultExportDirectory().toFile();
        if (initialDirectory.isDirectory()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }
        fileChooser.setInitialFileName(FrontendLogReportExporter.defaultFileName());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Markdown (*.md)", "*.md"),
                new FileChooser.ExtensionFilter("Texto (*.txt)", "*.txt"));
        File selectedFile = fileChooser.showSaveDialog(stage);
        return selectedFile == null ? null : selectedFile.toPath();
    }

    private void showExportSuccessDialog(Path reportPath) {
        boolean revealed = DesktopFileSupport.revealInFileExplorer(reportPath);
        appContext.feedback().showInformation(
                stage,
                "Informe de logs exportado",
                "El reporte del frontend se guard\u00f3 correctamente",
                "Archivo generado en:\n"
                        + reportPath.toAbsolutePath()
                        + (revealed
                        ? "\n\nSe abri\u00f3 el Explorador en la carpeta del archivo."
                        : "\n\nNo fue posible abrir el Explorador autom\u00e1ticamente.")
                        + "\n\nUsa este informe para soporte, diagn\u00f3stico o evidencia operativa.");
    }

    private void showExportFailureDialog() {
        appContext.feedback().showError(
                stage,
                "No se pudo exportar el informe",
                "Fall\u00f3 la generaci\u00f3n del reporte de logs",
                "No fue posible crear el archivo solicitado.\n\nReintenta con otra ubicaci\u00f3n o revisa permisos de escritura.");
    }

    private void exitApplication() {
        if (!exitTriggered.compareAndSet(false, true)) {
            return;
        }
        stage.setOnCloseRequest(null);
        appContext.sessionState().logout();
        Platform.exit();
    }
}
