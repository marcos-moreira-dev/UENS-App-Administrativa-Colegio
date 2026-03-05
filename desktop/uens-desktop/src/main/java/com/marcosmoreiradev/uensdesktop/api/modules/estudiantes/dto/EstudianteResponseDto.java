package com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto;

import java.time.LocalDate;

/**
 * Full student detail returned by create, update and detail endpoints.
 */
public record EstudianteResponseDto(
        Long id,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        String estado,
        Long representanteLegalId,
        Long seccionId) {
}
