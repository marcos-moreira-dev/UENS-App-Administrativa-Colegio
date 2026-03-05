package com.marcosmoreiradev.uensbackend.modules.reporte.api.dto;

import java.time.LocalDateTime;

/**
 * Define la responsabilidad de ReporteSolicitudDetalleResponseDto dentro del backend UENS.
 * Contexto: modulo reporte, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record ReporteSolicitudDetalleResponseDto(
        Long solicitudId,
        String tipoReporte,
        String estado,
        String parametrosJson,
        String resultadoJson,
        String errorDetalle,
        Integer intentos,
        LocalDateTime fechaSolicitud,
        LocalDateTime fechaActualizacion
) {
}

