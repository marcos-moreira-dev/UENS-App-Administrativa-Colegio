package com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto;

import java.time.LocalDate;

/**
 * Compact student projection used by listings and combo boxes.
 */
public record EstudianteListItemDto(
        Long id,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        String estado,
        Long representanteLegalId,
        Long seccionId) {

    /**
     * Builds the student label used in search controls and selections.
     *
     * @return display label in "apellido, nombre" format
     */
    public String displayName() {
        return apellidos + ", " + nombres;
    }
}
