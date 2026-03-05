package com.marcosmoreiradev.uensdesktop.app;

import com.marcosmoreiradev.uensdesktop.common.constants.AppConstants;
import com.marcosmoreiradev.uensdesktop.ui.assets.FxUiAssets;
import com.marcosmoreiradev.uensdesktop.ui.assets.UiAssetId;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        AppBootstrap bootstrap = new AppBootstrap();
        AppBootstrap.BootstrapResult bootstrapResult = bootstrap.bootstrap();
        Scene scene = bootstrapResult.scene();
        stage.setTitle(AppConstants.APP_NAME);
        FxUiAssets.image(UiAssetId.BRAND_ICON).ifPresent(stage.getIcons()::add);
        stage.setScene(scene);
        stage.setMinWidth(1024);
        stage.setMinHeight(720);
        AppWindowSupport.install(stage, scene, bootstrapResult.context());
        stage.show();
    }
}
