package com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto;

import java.time.LocalDate;

/**
 * Request payload used to create a student.
 */
public record EstudianteCreateRequestDto(
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        Long representanteLegalId,
        Long seccionId) {
}
