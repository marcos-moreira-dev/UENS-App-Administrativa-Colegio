package com.marcosmoreiradev.uensbackend.modules.reporte.application.processor;

import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;

/**
 * Define la responsabilidad de ReporteDataProcessor dentro del backend UENS.
 * Contexto: modulo reporte, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */

public interface ReporteDataProcessor {

/**
 * Implementa la operacion 'soporta' del modulo reporte en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param tipoReporte tipo funcional de reporte solicitado para procesamiento asincrono
     * @return salida util para continuar con la capa llamadora.
 */
    boolean soporta(String tipoReporte);

/**
 * Implementa la operacion 'procesar' del modulo reporte en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param solicitud dato de entrada relevante para ejecutar esta operacion: 'solicitud'
     * @return salida util para continuar con la capa llamadora.
 */
    Object procesar(ReporteSolicitudQueueJpaEntity solicitud);
}

