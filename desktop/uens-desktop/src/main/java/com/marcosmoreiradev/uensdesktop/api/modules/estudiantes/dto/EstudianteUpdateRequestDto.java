package com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto;

import java.time.LocalDate;

/**
 * Request payload used to update an existing student.
 */
public record EstudianteUpdateRequestDto(
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        Long representanteLegalId,
        Long seccionId,
        String estado) {
}
