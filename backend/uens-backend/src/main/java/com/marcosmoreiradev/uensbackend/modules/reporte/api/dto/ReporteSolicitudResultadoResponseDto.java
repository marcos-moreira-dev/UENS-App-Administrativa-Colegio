package com.marcosmoreiradev.uensbackend.modules.reporte.api.dto;

/**
 * Define la responsabilidad de ReporteSolicitudResultadoResponseDto dentro del backend UENS.
 * Contexto: modulo reporte, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record ReporteSolicitudResultadoResponseDto(
        Long solicitudId,
        String estado,
        String resultadoJson,
        String errorDetalle
) {
}

