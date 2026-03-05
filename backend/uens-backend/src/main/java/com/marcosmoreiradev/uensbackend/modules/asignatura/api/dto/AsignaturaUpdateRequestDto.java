package com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Define la responsabilidad de AsignaturaUpdateRequestDto dentro del backend UENS.
 * Contexto: modulo asignatura, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */
public record AsignaturaUpdateRequestDto(

        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 120, message = "El nombre no debe exceder 120 caracteres.")
        String nombre,

        @NotBlank(message = "El área es obligatoria.")
        @Size(max = 60, message = "El área no debe exceder 60 caracteres.")
        String area,

        @Size(max = 500, message = "La descripción no debe exceder 500 caracteres.")
        String descripcion,

        @NotNull(message = "El grado es obligatorio.")
        @Min(value = 1, message = "El grado debe ser mínimo 1.")
        @Max(value = 7, message = "El grado debe ser máximo 7.")
        Integer grado,

        @NotBlank(message = "El estado es obligatorio.")
        @Size(max = 20, message = "El estado no debe exceder 20 caracteres.")
        String estado
) {}