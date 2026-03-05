package com.marcosmoreiradev.uensdesktop.ui.command;

import com.marcosmoreiradev.uensdesktop.ui.fx.FxExecutors;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Factory methods for common command shapes used by the desktop controllers.
 */
public final class UiCommands {

    private UiCommands() {
    }

    /**
     * Wraps an in-thread action in a {@link UiCommand}.
     *
     * @param action work that should run immediately on command execution
     * @return command that delegates directly to the provided action
     */
    public static UiCommand action(Runnable action) {
        Objects.requireNonNull(action, "action");
        return action::run;
    }

    /**
     * Wraps a background operation and marshals its successful result back to the JavaFX thread.
     *
     * @param task blocking or I/O work that must run off the UI thread
     * @param onSuccess callback that consumes the result in the UI thread
     * @param <T> result type returned by the task
     * @return command that schedules the task in the shared I/O executor
     */
    public static <T> UiCommand io(Supplier<T> task, Consumer<? super T> onSuccess) {
        Objects.requireNonNull(task, "task");
        Objects.requireNonNull(onSuccess, "onSuccess");
        return () -> FxExecutors.submitIo(task, onSuccess);
    }
}
