package com.marcosmoreiradev.uensbackend.modules.reporte.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

/**
 * Define la responsabilidad de CrearReporteSolicitudRequestDto dentro del backend UENS.
 * Contexto: modulo reporte, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record CrearReporteSolicitudRequestDto(
        @NotBlank(message = "El tipo de reporte es obligatorio.")
        @Pattern(
                regexp = "^(?i)(LISTADO_ESTUDIANTES_POR_SECCION|CALIFICACIONES_POR_SECCION_Y_PARCIAL)$",
                message = "Tipo de reporte no soportado en V1."
        )
        String tipoReporte,
        @Pattern(regexp = "^(?i)(XLSX|PDF|DOCX)$", message = "Formato de salida no soportado en V1.")
        String formatoSalida,
        Long seccionId,
        @Min(value = 1, message = "El numero de parcial minimo es 1.")
        @Max(value = 2, message = "El numero de parcial maximo es 2.")
        Integer numeroParcial,
        LocalDate fechaDesde,
        LocalDate fechaHasta
) {
}
