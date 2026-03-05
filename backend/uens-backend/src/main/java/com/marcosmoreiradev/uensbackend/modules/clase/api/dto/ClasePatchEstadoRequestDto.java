package com.marcosmoreiradev.uensbackend.modules.clase.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Define la responsabilidad de ClasePatchEstadoRequestDto dentro del backend UENS.
 * Contexto: modulo clase, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record ClasePatchEstadoRequestDto(
        @NotBlank(message = "El estado es obligatorio.")
        @Pattern(regexp = "^(?i)(ACTIVO|INACTIVO)$", message = "El estado debe ser ACTIVO o INACTIVO.")
        String estado
) {
}

