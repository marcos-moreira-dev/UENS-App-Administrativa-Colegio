package com.marcosmoreiradev.uensbackend.modules.clase.api.dto;

import java.time.LocalTime;

/**
 * Define la responsabilidad de ClaseResponseDto dentro del backend UENS.
 * Contexto: modulo clase, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record ClaseResponseDto(
        Long id,
        Long seccionId,
        Long asignaturaId,
        Long docenteId,
        String diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        String estado
) {
}

