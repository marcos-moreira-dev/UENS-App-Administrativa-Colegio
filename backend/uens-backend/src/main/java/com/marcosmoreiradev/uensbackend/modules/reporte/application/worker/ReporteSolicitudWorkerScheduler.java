package com.marcosmoreiradev.uensbackend.modules.reporte.application.worker;

import com.marcosmoreiradev.uensbackend.config.properties.ReportQueueProperties;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.ReporteSolicitudWorkerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
/**
 * Define la responsabilidad de ReporteSolicitudWorkerScheduler dentro del backend UENS.
 * Contexto: modulo reporte, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: disparar procesos internos recurrentes para tareas asincronas del backend.
 */
public class ReporteSolicitudWorkerScheduler {

    private final ReporteSolicitudWorkerService workerService;
    private final ReportQueueProperties properties;
/**
 * Construye la instancia de ReporteSolicitudWorkerScheduler para operar en el modulo reporte.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param workerService servicio que ejecuta ciclo de procesamiento asincrono de reportes
     * @param properties propiedades tipadas que gobiernan limites y frecuencia de procesamiento
 */

    public ReporteSolicitudWorkerScheduler(ReporteSolicitudWorkerService workerService, ReportQueueProperties properties) {
        this.workerService = workerService;
        this.properties = properties;
    }

    @Scheduled(
            scheduler = "reportQueueTaskScheduler",
            initialDelayString = "${app.report.queue.initial-delay-ms:5000}",
            fixedDelayString = "${app.report.queue.fixed-delay-ms:10000}"
    )
/**
 * Implementa la operacion 'ejecutar' del modulo reporte en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void ejecutar() {
        if (!properties.enabled()) {
            return;
        }
        workerService.procesarPendientes();
    }
}

