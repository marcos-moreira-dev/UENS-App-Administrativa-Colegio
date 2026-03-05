package com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto;

/**
 * Request payload used to create a teacher.
 */
public record DocenteCreateRequestDto(
        String nombres,
        String apellidos,
        String telefono,
        String correoElectronico) {
}
