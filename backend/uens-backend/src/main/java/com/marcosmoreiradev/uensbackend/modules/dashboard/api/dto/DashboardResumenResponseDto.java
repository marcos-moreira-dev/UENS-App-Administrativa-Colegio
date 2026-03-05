package com.marcosmoreiradev.uensbackend.modules.dashboard.api.dto;

/**
 * Define la responsabilidad de DashboardResumenResponseDto dentro del backend UENS.
 * Contexto: modulo dashboard, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record DashboardResumenResponseDto(
        long totalEstudiantes,
        long totalDocentes,
        long totalSecciones,
        long totalAsignaturas,
        long totalClases,
        long totalCalificaciones
) {
}

