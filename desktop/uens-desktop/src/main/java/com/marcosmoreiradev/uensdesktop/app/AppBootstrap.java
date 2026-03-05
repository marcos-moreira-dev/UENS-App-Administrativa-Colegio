package com.marcosmoreiradev.uensdesktop.app;

import com.marcosmoreiradev.uensdesktop.modules.shell.view.AppShellController;
import com.marcosmoreiradev.uensdesktop.ui.theme.ThemeManager;
import com.marcosmoreiradev.uensdesktop.ui.view.ViewEnhancer;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import com.marcosmoreiradev.uensdesktop.nav.ViewId;

public final class AppBootstrap {

    public BootstrapResult bootstrap() {
        AppContext context = AppContext.createDefault();
        Parent root = loadRoot(context);

        Scene scene = new Scene(root, 1280, 800);
        ThemeManager.applyStyles(scene);
        return new BootstrapResult(scene, context);
    }

    private Parent loadRoot(AppContext context) {
        FXMLLoader loader = new FXMLLoader(
                AppBootstrap.class.getResource("/fxml/shell/AppShell.fxml"),
                context.resourceBundle());
        loader.setControllerFactory(context.controllerFactory());

        try {
            Parent root = loader.load();
            ViewEnhancer.enhance(root, ViewId.LOGIN);
            AppShellController controller = loader.getController();
            controller.activateInitialView();
            return root;
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo cargar AppShell.fxml", ex);
        }
    }

    public record BootstrapResult(Scene scene, AppContext context) {
    }
}
