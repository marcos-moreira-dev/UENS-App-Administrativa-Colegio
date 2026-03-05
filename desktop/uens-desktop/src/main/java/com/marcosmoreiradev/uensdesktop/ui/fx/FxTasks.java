package com.marcosmoreiradev.uensdesktop.ui.fx;

import javafx.concurrent.Task;

public final class FxTasks {

    private FxTasks() {
    }

    public static <T> Task<T> taskOf(java.util.concurrent.Callable<T> callable) {
        return new Task<>() {
            @Override
            protected T call() throws Exception {
                return callable.call();
            }
        };
    }
}
