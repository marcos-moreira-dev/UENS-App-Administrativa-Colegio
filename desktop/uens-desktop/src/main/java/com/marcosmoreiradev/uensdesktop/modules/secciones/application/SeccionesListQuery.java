package com.marcosmoreiradev.uensdesktop.modules.secciones.application;

public record SeccionesListQuery(
        int page,
        int size,
        String query,
        String estado,
        Integer grado,
        String paralelo,
        String anioLectivo) {
}
