package com.marcosmoreiradev.uensdesktop.ui.theme;

import javafx.scene.Scene;

public final class ThemeManager {

    private ThemeManager() {
    }

    public static void applyStyles(Scene scene) {
        TypographyManager.apply(scene);
        scene.getStylesheets().add(ThemeManager.class.getResource("/css/typography.css").toExternalForm());
        scene.getStylesheets().add(ThemeManager.class.getResource("/css/app-base.css").toExternalForm());
        scene.getStylesheets().add(ThemeManager.class.getResource("/css/shell.css").toExternalForm());
        scene.getStylesheets().add(ThemeManager.class.getResource("/css/controls.css").toExternalForm());
        scene.getStylesheets().add(ThemeManager.class.getResource("/css/table.css").toExternalForm());
        scene.getStylesheets().add(ThemeManager.class.getResource("/css/dialogs.css").toExternalForm());
        scene.getStylesheets().add(ThemeManager.class.getResource("/css/badges.css").toExternalForm());
        TypographyManager.apply(scene);
    }
}
