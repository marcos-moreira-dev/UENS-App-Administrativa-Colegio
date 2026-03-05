package com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Define la responsabilidad de AsignarSeccionVigenteRequestDto dentro del backend UENS.
 * Contexto: modulo estudiante, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record AsignarSeccionVigenteRequestDto(
        @NotNull(message = "La seccion es obligatoria.")
        Long seccionId
) {
}

