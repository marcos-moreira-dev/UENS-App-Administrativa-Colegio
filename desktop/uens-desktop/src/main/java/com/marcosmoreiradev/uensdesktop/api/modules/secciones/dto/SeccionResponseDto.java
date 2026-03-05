package com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto;

/**
 * Full section detail returned by create, update and detail endpoints.
 */
public record SeccionResponseDto(
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
