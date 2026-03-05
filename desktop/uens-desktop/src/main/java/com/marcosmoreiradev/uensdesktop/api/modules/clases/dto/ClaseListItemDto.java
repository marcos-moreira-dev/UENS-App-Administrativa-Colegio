package com.marcosmoreiradev.uensdesktop.api.modules.clases.dto;

import java.time.LocalTime;

/**
 * Compact class projection used by listings.
 */
public record ClaseListItemDto(
        Long id,
        Long seccionId,
        Long asignaturaId,
        Long docenteId,
        String diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        String estado) {
}
