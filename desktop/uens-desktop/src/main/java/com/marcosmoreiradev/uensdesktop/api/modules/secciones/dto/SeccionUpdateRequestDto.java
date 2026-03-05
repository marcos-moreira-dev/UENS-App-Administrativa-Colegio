package com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto;

/**
 * Request payload used to update an existing section.
 */
public record SeccionUpdateRequestDto(
        Integer grado,
        String paralelo,
        Integer cupoMaximo,
        String anioLectivo,
        String estado) {
}
