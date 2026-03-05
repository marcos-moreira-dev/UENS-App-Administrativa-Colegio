package com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto;

import java.time.LocalDateTime;

/**
 * Detailed metadata of a report request used by detail drawers and polling flows.
 *
 * @param solicitudId backend identifier of the report request
 * @param tipoReporte requested report type
 * @param estado current backend processing state
 * @param parametrosJson raw serialized parameters used by the backend
 * @param resultadoJson raw serialized result metadata when available
 * @param errorDetalle backend-provided failure detail when generation fails
 * @param intentos number of attempts executed by the backend
 * @param fechaSolicitud creation timestamp
 * @param fechaActualizacion last backend update timestamp
 */
public record ReporteSolicitudDetailResponseDto(
        Long solicitudId,
        String tipoReporte,
        String estado,
        String parametrosJson,
        String resultadoJson,
        String errorDetalle,
        Integer intentos,
        LocalDateTime fechaSolicitud,
        LocalDateTime fechaActualizacion) {
}
