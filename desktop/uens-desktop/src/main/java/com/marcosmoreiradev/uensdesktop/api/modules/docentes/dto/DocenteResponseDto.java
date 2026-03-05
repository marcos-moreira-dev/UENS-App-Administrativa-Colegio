package com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto;

/**
 * Full teacher detail returned by create, update and detail endpoints.
 */
public record DocenteResponseDto(
        Long id,
        String nombres,
        String apellidos,
        String telefono,
        String correoElectronico,
        String estado) {
}
