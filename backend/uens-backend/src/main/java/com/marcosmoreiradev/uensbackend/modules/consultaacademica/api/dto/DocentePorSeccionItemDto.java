package com.marcosmoreiradev.uensbackend.modules.consultaacademica.api.dto;

/**
 * Resultado de consulta de docentes asociados operativamente a una seccion.
 */
public record DocentePorSeccionItemDto(
        Long docenteId,
        String nombres,
        String apellidos,
        String correoElectronico,
        String telefono,
        String estado,
        long clasesActivasAsignadas
) {
}
