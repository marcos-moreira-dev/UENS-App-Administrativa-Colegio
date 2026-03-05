package com.marcosmoreiradev.uensbackend.modules.clase.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalTime;

/**
 * Define la responsabilidad de ClaseUpdateRequestDto dentro del backend UENS.
 * Contexto: modulo clase, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record ClaseUpdateRequestDto(
        @NotNull(message = "La seccion es obligatoria.")
        Long seccionId,

        @NotNull(message = "La asignatura es obligatoria.")
        Long asignaturaId,

        Long docenteId,

        @NotBlank(message = "El dia de la semana es obligatorio.")
        @Pattern(regexp = "^(?i)(LUNES|MARTES|MIERCOLES|JUEVES|VIERNES|SABADO)$", message = "Dia de semana invalido.")
        String diaSemana,

        @NotNull(message = "La hora de inicio es obligatoria.")
        LocalTime horaInicio,

        @NotNull(message = "La hora de fin es obligatoria.")
        LocalTime horaFin,

        @Pattern(regexp = "^(?i)(ACTIVO|INACTIVO)$", message = "El estado debe ser ACTIVO o INACTIVO.")
        String estado
) {
}

