package com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Define la responsabilidad de AsignaturaPatchEstadoRequestDto dentro del backend UENS.
 * Contexto: modulo asignatura, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */
public record AsignaturaPatchEstadoRequestDto(

        @NotBlank(message = "El estado es obligatorio.")
        String estado

) {}