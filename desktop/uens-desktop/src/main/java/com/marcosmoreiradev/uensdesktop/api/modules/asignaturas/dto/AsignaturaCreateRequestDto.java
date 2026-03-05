package com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto;

/**
 * Request payload used to create a subject.
 */
public record AsignaturaCreateRequestDto(
        String nombre,
        String area,
        String descripcion,
        Integer grado) {
}
