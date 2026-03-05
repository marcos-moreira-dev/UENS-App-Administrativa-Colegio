package com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto;

/**
 * Request payload used to update an existing representative.
 */
public record RepresentanteLegalUpdateRequestDto(
        String nombres,
        String apellidos,
        String telefono,
        String correoElectronico) {
}
