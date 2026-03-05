package com.marcosmoreiradev.uensbackend.modules.dashboard.infrastructure.query;

/**
 * Define la responsabilidad de DashboardQueryRepository dentro del backend UENS.
 * Contexto: modulo dashboard, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: encapsular acceso a datos y consultas sobre el esquema relacional V2 3FN.
 */

public interface DashboardQueryRepository {

/**
 * Implementa la operacion 'obtenerResumen' del modulo dashboard en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return salida util para continuar con la capa llamadora.
 */
    DashboardResumenProjection obtenerResumen();

/**
 * Define la responsabilidad de DashboardResumenProjection dentro del backend UENS.
 * Contexto: modulo dashboard, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
    record DashboardResumenProjection(
            long totalEstudiantes,
            long totalDocentes,
            long totalSecciones,
            long totalAsignaturas,
            long totalClases,
            long totalCalificaciones
    ) {
    }
}

