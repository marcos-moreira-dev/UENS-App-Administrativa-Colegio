package com.marcosmoreiradev.uensbackend.modules.consultaacademica.application.model;

/**
 * Fila de lectura interna para asociacion seccion-docente derivada de clases.
 */
public record SeccionPorDocenteRow(
        Long seccionId,
        Short grado,
        String paralelo,
        String anioLectivo,
        String estado,
        Long clasesActivasAsignadas
) {
}
