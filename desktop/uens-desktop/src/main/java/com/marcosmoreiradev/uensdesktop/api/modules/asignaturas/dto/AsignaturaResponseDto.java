package com.marcosmoreiradev.uensdesktop.api.modules.asignaturas.dto;

/**
 * Full subject detail returned by create, update and detail endpoints.
 */
public record AsignaturaResponseDto(
        Long id,
        String nombre,
        String area,
        String descripcion,
        Integer grado,
        String estado) {
}
