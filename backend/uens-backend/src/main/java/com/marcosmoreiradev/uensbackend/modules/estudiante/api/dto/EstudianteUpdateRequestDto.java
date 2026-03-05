package com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Define la responsabilidad de EstudianteUpdateRequestDto dentro del backend UENS.
 * Contexto: modulo estudiante, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record EstudianteUpdateRequestDto(
        @NotBlank(message = "Los nombres son obligatorios.")
        @Size(max = 120, message = "Los nombres no deben exceder 120 caracteres.")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios.")
        @Size(max = 120, message = "Los apellidos no deben exceder 120 caracteres.")
        String apellidos,

        @NotNull(message = "La fecha de nacimiento es obligatoria.")
        @Past(message = "La fecha de nacimiento debe estar en el pasado.")
        LocalDate fechaNacimiento,

        @NotNull(message = "El representante legal es obligatorio.")
        Long representanteLegalId,

        Long seccionId,

        @Pattern(regexp = "^(?i)(ACTIVO|INACTIVO)$", message = "El estado debe ser ACTIVO o INACTIVO.")
        String estado
) {
}

