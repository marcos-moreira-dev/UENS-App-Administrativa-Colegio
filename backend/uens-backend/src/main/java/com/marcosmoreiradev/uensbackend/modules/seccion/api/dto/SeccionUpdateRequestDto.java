package com.marcosmoreiradev.uensbackend.modules.seccion.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Define la responsabilidad de SeccionUpdateRequestDto dentro del backend UENS.
 * Contexto: modulo seccion, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record SeccionUpdateRequestDto(
        @NotNull(message = "El grado es obligatorio.")
        @Min(value = 1, message = "El grado debe ser mayor o igual a 1.")
        @Max(value = 7, message = "El grado debe ser menor o igual a 7.")
        Integer grado,

        @NotBlank(message = "El paralelo es obligatorio.")
        @Size(max = 10, message = "El paralelo no debe exceder 10 caracteres.")
        String paralelo,

        @NotNull(message = "El cupo maximo es obligatorio.")
        @Min(value = 1, message = "El cupo maximo debe ser mayor o igual a 1.")
        @Max(value = 35, message = "El cupo maximo debe ser menor o igual a 35.")
        Integer cupoMaximo,

        @NotBlank(message = "El anio lectivo es obligatorio.")
        @Size(max = 20, message = "El anio lectivo no debe exceder 20 caracteres.")
        @Pattern(regexp = "^[0-9]{4}-[0-9]{4}$", message = "El anio lectivo debe tener formato YYYY-YYYY.")
        String anioLectivo,

        @Pattern(regexp = "^(?i)(ACTIVO|INACTIVO)$", message = "El estado debe ser ACTIVO o INACTIVO.")
        String estado
) {
}

