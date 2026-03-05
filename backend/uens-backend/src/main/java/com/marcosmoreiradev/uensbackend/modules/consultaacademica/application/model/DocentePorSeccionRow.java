package com.marcosmoreiradev.uensbackend.modules.consultaacademica.application.model;

/**
 * Fila de lectura interna para asociacion docente-seccion derivada de clases.
 */
public record DocentePorSeccionRow(
        Long docenteId,
        String nombres,
        String apellidos,
        String correoElectronico,
        String telefono,
        String estado,
        Long clasesActivasAsignadas
) {
}
