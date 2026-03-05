package com.marcosmoreiradev.uensdesktop.ui.fx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Shared executors and helpers for background work initiated from JavaFX controllers.
 */
public final class FxExecutors {

    private static final AtomicInteger IO_THREAD_COUNTER = new AtomicInteger();
    private static final ExecutorService IO = Executors.newCachedThreadPool(buildIoThreadFactory());

    private FxExecutors() {
    }

    /**
     * Returns the shared executor dedicated to I/O and other blocking work.
     *
     * @return daemon cached thread pool used across the desktop module
     */
    public static ExecutorService io() {
        return IO;
    }

    /**
     * Executes background work in the shared I/O executor and forwards the result to the JavaFX
     * thread.
     *
     * @param backgroundWork work that should not block the UI thread
     * @param uiConsumer callback that consumes the result in the UI thread
     * @param <T> result type produced by the background work
     */
    public static <T> void submitIo(Supplier<T> backgroundWork, Consumer<? super T> uiConsumer) {
        io().submit(() -> {
            T value = backgroundWork.get();
            FxThreading.runOnUiThread(() -> uiConsumer.accept(value));
        });
    }

    /**
     * Adapts a consumer so it always executes on the JavaFX Application Thread.
     *
     * @param uiConsumer consumer that updates UI state
     * @param <T> consumed value type
     * @return consumer wrapper safe to use from background callbacks
     */
    public static <T> Consumer<T> uiConsumer(Consumer<? super T> uiConsumer) {
        return value -> FxThreading.runOnUiThread(() -> uiConsumer.accept(value));
    }

    /**
     * Creates daemon threads with stable names for diagnostics and thread dumps.
     *
     * @return thread factory used by the shared cached pool
     */
    private static ThreadFactory buildIoThreadFactory() {
        return runnable -> {
            Thread thread = new Thread(runnable, "uens-fx-io-" + IO_THREAD_COUNTER.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
    }
}
