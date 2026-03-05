package com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto;

/**
 * Request payload used to create a representative.
 */
public record RepresentanteLegalCreateRequestDto(
        String nombres,
        String apellidos,
        String telefono,
        String correoElectronico) {
}
