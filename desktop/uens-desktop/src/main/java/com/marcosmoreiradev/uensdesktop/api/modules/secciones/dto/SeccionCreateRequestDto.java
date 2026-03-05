package com.marcosmoreiradev.uensdesktop.api.modules.secciones.dto;

/**
 * Request payload used to create a section.
 */
public record SeccionCreateRequestDto(
        Integer grado,
        String paralelo,
        Integer cupoMaximo,
        String anioLectivo) {
}
