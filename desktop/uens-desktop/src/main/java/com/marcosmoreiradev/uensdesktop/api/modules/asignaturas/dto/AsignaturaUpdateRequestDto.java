package com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto;

/**
 * Request payload used to update an existing subject.
 */
public record AsignaturaUpdateRequestDto(
        String nombre,
        String area,
        String descripcion,
        Integer grado,
        String estado) {
}
