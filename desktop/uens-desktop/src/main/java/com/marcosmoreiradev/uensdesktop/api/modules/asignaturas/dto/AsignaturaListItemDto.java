package com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto;

/**
 * Compact subject projection used by listings and combo boxes.
 */
public record AsignaturaListItemDto(
        Long id,
        String nombre,
        String area,
        Integer grado,
        String estado) {

    /**
     * Builds the subject label shown in search controls and tables.
     *
     * @return display label with subject name, area and grade
     */
    public String displayName() {
        return nombre + " (" + area + ", " + grado + "EGB)";
    }
}
