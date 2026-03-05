package com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.repository;

import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;

import java.util.List;

/**
 * Define la responsabilidad de ReporteSolicitudQueueClaimRepository dentro del backend UENS.
 * Contexto: modulo reporte, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: encapsular acceso a datos y consultas sobre el esquema relacional V2 3FN.
 */

public interface ReporteSolicitudQueueClaimRepository {

/**
 * Implementa la operacion 'claimPendientes' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param batchSize dato de entrada relevante para ejecutar esta operacion: 'batchSize'
     * @return salida util para continuar con la capa llamadora.
 */
    List<ReporteSolicitudQueueJpaEntity> claimPendientes(int batchSize);
}

