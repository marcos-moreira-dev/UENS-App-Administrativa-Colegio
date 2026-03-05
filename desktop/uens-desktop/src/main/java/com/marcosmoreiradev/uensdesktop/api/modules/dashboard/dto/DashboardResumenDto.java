package com.marcosmoreiradev.uensdesktop.api.modules.dashboard.dto;

/**
 * Aggregated dashboard metrics shown by the desktop home screen.
 *
 * @param totalEstudiantes total number of registered students
 * @param totalDocentes total number of registered teachers
 * @param totalSecciones total number of registered sections
 * @param totalAsignaturas total number of registered subjects
 * @param totalClases total number of registered classes
 * @param totalCalificaciones total number of recorded grades
 */
public record DashboardResumenDto(
        long totalEstudiantes,
        long totalDocentes,
        long totalSecciones,
        long totalAsignaturas,
        long totalClases,
        long totalCalificaciones) {
}
