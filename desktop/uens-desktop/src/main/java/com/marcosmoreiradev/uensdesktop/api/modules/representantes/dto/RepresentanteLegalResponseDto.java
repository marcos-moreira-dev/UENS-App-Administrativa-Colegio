package com.marcosmoreiradev.uensdesktop.api.modules.representantes.dto;

/**
 * Full representative detail returned by create, update and detail endpoints.
 */
public record RepresentanteLegalResponseDto(
        Long id,
        String nombres,
        String apellidos,
        String telefono,
        String correoElectronico) {

    /**
     * Formats the representative name in the way most screens present it to the operator.
     *
     * @return display label built from first names and last names
     */
    public String displayName() {
        return nombres + " " + apellidos;
    }
}
