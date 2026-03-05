package com.marcosmoreiradev.uensbackend.modules.reporte.api.dto;

import java.time.LocalDateTime;

/**
 * Define la responsabilidad de ReporteSolicitudCreadaResponseDto dentro del backend UENS.
 * Contexto: modulo reporte, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record ReporteSolicitudCreadaResponseDto(
        Long solicitudId,
        String tipoReporte,
        String estado,
        LocalDateTime fechaSolicitud
) {
}

