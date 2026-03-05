package com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Define la responsabilidad de EstudiantePatchEstadoRequestDto dentro del backend UENS.
 * Contexto: modulo estudiante, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record EstudiantePatchEstadoRequestDto(
        @NotBlank(message = "El estado es obligatorio.")
        @Pattern(regexp = "^(?i)(ACTIVO|INACTIVO)$", message = "El estado debe ser ACTIVO o INACTIVO.")
        String estado
) {
}

