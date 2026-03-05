package com.marcosmoreiradev.uensdesktop.ui.fx;

import javafx.application.Platform;

/**
 * Small bridge for safely switching work onto the JavaFX Application Thread.
 */
public final class FxThreading {

    private FxThreading() {
    }

    /**
     * Runs the given action immediately when already on the JavaFX thread, or schedules it via
     * {@link Platform#runLater(Runnable)} otherwise.
     *
     * @param runnable UI work that must execute in the JavaFX thread
     */
    public static void runOnUiThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }
}
