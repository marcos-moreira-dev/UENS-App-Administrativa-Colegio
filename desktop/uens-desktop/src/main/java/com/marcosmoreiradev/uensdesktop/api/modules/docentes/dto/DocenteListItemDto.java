package com.marcosmoreiradev.uensdesktop.api.modules.docentes.dto;

/**
 * Compact teacher projection used by listings and combo boxes.
 */
public record DocenteListItemDto(
        Long id,
        String nombres,
        String apellidos,
        String telefono,
        String correoElectronico,
        String estado) {

    /**
     * Formats the teacher name in the way most screens present it to the operator.
     *
     * @return display label built from first names and last names
     */
    public String displayName() {
        return nombres + " " + apellidos;
    }
}
