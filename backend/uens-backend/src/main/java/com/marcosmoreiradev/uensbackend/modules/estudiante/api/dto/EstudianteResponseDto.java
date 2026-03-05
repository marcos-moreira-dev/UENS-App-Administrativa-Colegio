package com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto;

import java.time.LocalDate;

/**
 * Define la responsabilidad de EstudianteResponseDto dentro del backend UENS.
 * Contexto: modulo estudiante, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record EstudianteResponseDto(
        Long id,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        String estado,
        Long representanteLegalId,
        Long seccionId
) {
}

