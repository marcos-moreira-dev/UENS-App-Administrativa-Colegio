package com.marcosmoreiradev.uensdesktop.modules.reportes.application;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudResultResponseDto;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Polls the backend for asynchronous report status changes and stops automatically when a terminal
 * state is reached.
 */
public final class ReportePollingService implements AutoCloseable {

    private static final long DEFAULT_POLLING_INTERVAL_SECONDS = 3;

    private final ReportesService reportesService;
    private final ScheduledExecutorService scheduler;
    private final long pollingInterval;
    private final TimeUnit pollingIntervalUnit;
    private final Map<Long, ScheduledFuture<?>> activePolls = new ConcurrentHashMap<>();

    /**
     * Creates a polling service with the production scheduler configuration.
     *
     * @param reportesService service used to query the backend report status
     */
    public ReportePollingService(ReportesService reportesService) {
        this(reportesService, newScheduler(), DEFAULT_POLLING_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    ReportePollingService(
            ReportesService reportesService,
            ScheduledExecutorService scheduler,
            long pollingInterval,
            TimeUnit pollingIntervalUnit) {
        this.reportesService = reportesService;
        this.scheduler = scheduler;
        this.pollingInterval = pollingInterval;
        this.pollingIntervalUnit = pollingIntervalUnit;
    }

    /**
     * Starts or replaces the polling cycle associated with a report request.
     *
     * @param solicitudId backend identifier of the report request to monitor
     * @param callback consumer that receives each polling result
     */
    public void startPolling(
            long solicitudId,
            Consumer<ApiResult<ReporteSolicitudResultResponseDto>> callback) {
        stopPolling(solicitudId);
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            ApiResult<ReporteSolicitudResultResponseDto> result = reportesService.obtenerEstado(solicitudId);
            callback.accept(result);
            if (result.isSuccess()) {
                String estado = result.data().orElseThrow().estado();
                if (isTerminalState(estado)) {
                    stopPolling(solicitudId);
                }
            }
        }, 0, pollingInterval, pollingIntervalUnit);
        activePolls.put(solicitudId, future);
    }

    /**
     * Stops the polling cycle associated with a specific report request.
     *
     * @param solicitudId backend identifier of the report request
     */
    public void stopPolling(long solicitudId) {
        ScheduledFuture<?> future = activePolls.remove(solicitudId);
        if (future != null) {
            future.cancel(false);
        }
    }

    /**
     * Stops every active polling cycle managed by this service.
     */
    public void stopAll() {
        activePolls.keySet().forEach(this::stopPolling);
    }

    /**
     * Stops every polling cycle and shuts down the scheduler owned by this service.
     */
    @Override
    public void close() {
        stopAll();
        scheduler.shutdownNow();
    }

    private boolean isTerminalState(String estado) {
        return "COMPLETADA".equalsIgnoreCase(estado) || "ERROR".equalsIgnoreCase(estado);
    }

    private static ScheduledExecutorService newScheduler() {
        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable, "uens-reportes-polling");
            thread.setDaemon(true);
            return thread;
        };
        return Executors.newSingleThreadScheduledExecutor(threadFactory);
    }
}
