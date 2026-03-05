package com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto;

import java.time.LocalDateTime;

/**
 * Row-level summary returned by the report request listing.
 *
 * @param solicitudId backend identifier of the report request
 * @param tipoReporte requested report type
 * @param estado current backend processing state
 * @param fechaSolicitud creation timestamp
 * @param fechaActualizacion last backend update timestamp
 * @param intentos number of attempts executed by the backend
 */
public record ReporteSolicitudListItemDto(
        Long solicitudId,
        String tipoReporte,
        String estado,
        LocalDateTime fechaSolicitud,
        LocalDateTime fechaActualizacion,
        Integer intentos) {
}
