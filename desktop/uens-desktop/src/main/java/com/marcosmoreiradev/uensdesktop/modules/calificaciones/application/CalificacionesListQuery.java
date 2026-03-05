package com.marcosmoreiradev.uensdesktop.modules.calificaciones.application;

public record CalificacionesListQuery(
        int page,
        int size,
        Long estudianteId,
        Long claseId,
        Integer numeroParcial) {
}
