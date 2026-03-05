package com.marcosmoreiradev.uensbackend.modules.dashboard.application.mapper;

import com.marcosmoreiradev.uensbackend.modules.dashboard.api.dto.DashboardResumenResponseDto;
import org.springframework.stereotype.Component;

@Component
/**
 * Define la responsabilidad de DashboardDtoMapper dentro del backend UENS.
 * Contexto: modulo dashboard, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: traducir estructuras entre DTOs API, modelos de aplicacion y entidades persistentes.
 */
public class DashboardDtoMapper {
/**
 * Implementa la operacion 'toResumenDto' del modulo dashboard en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param totalEstudiantes dato de entrada relevante para ejecutar esta operacion: 'totalEstudiantes'
     * @param totalDocentes dato de entrada relevante para ejecutar esta operacion: 'totalDocentes'
     * @param totalSecciones dato de entrada relevante para ejecutar esta operacion: 'totalSecciones'
     * @param totalAsignaturas dato de entrada relevante para ejecutar esta operacion: 'totalAsignaturas'
     * @param totalClases dato de entrada relevante para ejecutar esta operacion: 'totalClases'
     * @param totalCalificaciones dato de entrada relevante para ejecutar esta operacion: 'totalCalificaciones'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */

    public DashboardResumenResponseDto toResumenDto(
            long totalEstudiantes,
            long totalDocentes,
            long totalSecciones,
            long totalAsignaturas,
            long totalClases,
            long totalCalificaciones
    ) {
        return new DashboardResumenResponseDto(
                totalEstudiantes,
                totalDocentes,
                totalSecciones,
                totalAsignaturas,
                totalClases,
                totalCalificaciones
        );
    }
}

