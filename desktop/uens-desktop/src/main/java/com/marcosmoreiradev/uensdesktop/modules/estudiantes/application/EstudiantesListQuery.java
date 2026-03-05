package com.marcosmoreiradev.uensdesktop.modules.estudiantes.application;

public record EstudiantesListQuery(
        int page,
        int size,
        String query,
        String estado,
        Long seccionId,
        Long representanteLegalId) {
}
