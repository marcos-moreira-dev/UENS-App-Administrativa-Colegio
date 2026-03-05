package com.marcosmoreiradev.uensdesktop.api.modules.clases.dto;

import java.time.LocalTime;

/**
 * Request payload used to update an existing class schedule entry.
 */
public record ClaseUpdateRequestDto(
        Long seccionId,
        Long asignaturaId,
        Long docenteId,
        String diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        String estado) {
}
