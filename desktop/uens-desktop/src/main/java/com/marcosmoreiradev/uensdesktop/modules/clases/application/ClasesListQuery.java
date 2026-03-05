package com.marcosmoreiradev.uensdesktop.modules.clases.application;

public record ClasesListQuery(
        int page,
        int size,
        String estado,
        Long seccionId,
        Long asignaturaId,
        Long docenteId,
        String diaSemana) {
}
