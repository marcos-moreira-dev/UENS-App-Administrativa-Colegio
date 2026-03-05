package com.marcosmoreiradev.uensdesktop.modules.asignaturas.application;

public record AsignaturasListQuery(
        int page,
        int size,
        String query,
        String estado,
        Integer grado,
        String area) {
}
