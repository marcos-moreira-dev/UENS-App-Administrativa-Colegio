package com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto;

/**
 * Request payload used to update an existing teacher.
 */
public record DocenteUpdateRequestDto(
        String nombres,
        String apellidos,
        String telefono,
        String correoElectronico,
        String estado) {
}
