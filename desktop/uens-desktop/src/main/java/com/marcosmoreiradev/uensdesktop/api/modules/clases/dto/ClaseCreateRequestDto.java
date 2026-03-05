package com.marcosmoreiradev.uensdesktop.api.modules.clases.dto;

import java.time.LocalTime;

/**
 * Request payload used to create a class schedule entry.
 */
public record ClaseCreateRequestDto(
        Long seccionId,
        Long asignaturaId,
        Long docenteId,
        String diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin) {
}
