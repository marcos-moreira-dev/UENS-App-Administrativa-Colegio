package com.marcosmoreiradev.uensbackend.modules.consultaacademica.api.dto;

/**
 * Resultado de consulta de secciones asociadas operativamente a un docente.
 */
public record SeccionPorDocenteItemDto(
        Long seccionId,
        Integer grado,
        String paralelo,
        String anioLectivo,
        String estado,
        long clasesActivasAsignadas
) {
}
