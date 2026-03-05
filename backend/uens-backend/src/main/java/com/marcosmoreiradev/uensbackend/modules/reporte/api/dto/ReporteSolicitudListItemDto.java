package com.marcosmoreiradev.uensbackend.modules.reporte.api.dto;

import java.time.LocalDateTime;

/**
 * Define la responsabilidad de ReporteSolicitudListItemDto dentro del backend UENS.
 * Contexto: modulo reporte, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record ReporteSolicitudListItemDto(
        Long solicitudId,
        String tipoReporte,
        String estado,
        LocalDateTime fechaSolicitud,
        LocalDateTime fechaActualizacion,
        Integer intentos
) {
}

