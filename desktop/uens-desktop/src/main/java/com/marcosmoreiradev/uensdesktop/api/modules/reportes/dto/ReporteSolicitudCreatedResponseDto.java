package com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto;

import java.time.LocalDateTime;

/**
 * Metadata returned immediately after requesting a new asynchronous report.
 *
 * @param solicitudId backend identifier assigned to the request
 * @param tipoReporte requested report type
 * @param estado initial processing state
 * @param fechaSolicitud creation timestamp reported by the backend
 */
public record ReporteSolicitudCreatedResponseDto(
        Long solicitudId,
        String tipoReporte,
        String estado,
        LocalDateTime fechaSolicitud) {
}
