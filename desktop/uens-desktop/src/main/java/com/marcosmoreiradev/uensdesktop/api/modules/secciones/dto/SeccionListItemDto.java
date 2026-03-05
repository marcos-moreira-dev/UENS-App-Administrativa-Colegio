package com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto;

/**
 * Compact section projection used by listings and combo boxes.
 */
public record SeccionListItemDto(
        Long id,
        Integer grado,
        String paralelo,
        Integer cupoMaximo,
        String anioLectivo,
        String estado) {

    /**
     * Builds the human-readable academic label used across the UI.
     *
     * @return display label with grade, parallel and academic year
     */
    public String displayName() {
        return grado + " " + paralelo + " - " + anioLectivo;
    }
}
