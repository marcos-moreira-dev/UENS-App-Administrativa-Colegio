package com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Define la responsabilidad de AsignaturaCreateRequestDto dentro del backend UENS.
 * Contexto: modulo asignatura, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */
public record AsignaturaCreateRequestDto(

        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 120, message = "El nombre no puede exceder 120 caracteres.")
        String nombre,

        @NotBlank(message = "El área es obligatoria.")
        @Size(max = 60, message = "El área no puede exceder 60 caracteres.")
        String area,

        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres.")
        String descripcion,

        @Min(value = 1, message = "El grado debe ser mayor o igual a 1.")
        @Max(value = 7, message = "El grado debe ser menor o igual a 7.")
        Integer grado
) {}