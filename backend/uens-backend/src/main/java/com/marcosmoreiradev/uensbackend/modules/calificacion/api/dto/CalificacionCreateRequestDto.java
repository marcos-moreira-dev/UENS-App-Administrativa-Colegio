package com.marcosmoreiradev.uensbackend.modules.calificacion.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Define la responsabilidad de CalificacionCreateRequestDto dentro del backend UENS.
 * Contexto: modulo calificacion, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record CalificacionCreateRequestDto(
        @NotNull(message = "El numero parcial es obligatorio.")
        @Min(value = 1, message = "El numero parcial debe ser 1 o 2.")
        @Max(value = 2, message = "El numero parcial debe ser 1 o 2.")
        Integer numeroParcial,

        @NotNull(message = "La nota es obligatoria.")
        @DecimalMin(value = "0.00", message = "La nota debe ser >= 0.")
        @DecimalMax(value = "10.00", message = "La nota debe ser <= 10.")
        BigDecimal nota,

        LocalDate fechaRegistro,

        @Size(max = 500, message = "La observacion no debe exceder 500 caracteres.")
        String observacion,

        @NotNull(message = "El estudiante es obligatorio.")
        Long estudianteId,

        @NotNull(message = "La clase es obligatoria.")
        Long claseId
) {
}

