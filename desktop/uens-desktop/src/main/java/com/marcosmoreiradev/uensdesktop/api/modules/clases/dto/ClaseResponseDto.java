package com.marcosmoreiradev.uensdesktop.api.modules.clases.dto;

import java.time.LocalTime;

/**
 * Full class detail returned by create, update and detail endpoints.
 */
public record ClaseResponseDto(
        Long id,
        Long seccionId,
        Long asignaturaId,
        Long docenteId,
        String diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        String estado) {
}
