package com.marcosmoreiradev.uensbackend.modules.auditoria.api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CrearAuditoriaReporteRequestDto(
        @Pattern(regexp = "^(?i)(XLSX|PDF|DOCX)$", message = "Formato de salida no soportado.")
        String formatoSalida,
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        @Size(max = 80, message = "El filtro modulo admite maximo 80 caracteres.")
        String modulo,
        @Size(max = 120, message = "El filtro accion admite maximo 120 caracteres.")
        String accion,
        @Pattern(regexp = "^(?i)(EXITO|ERROR|INFO|ADVERTENCIA)$", message = "El filtro resultado no es valido.")
        String resultado,
        @Size(max = 80, message = "El filtro actorLogin admite maximo 80 caracteres.")
        String actorLogin,
        Boolean incluirDetalle
) {
}

