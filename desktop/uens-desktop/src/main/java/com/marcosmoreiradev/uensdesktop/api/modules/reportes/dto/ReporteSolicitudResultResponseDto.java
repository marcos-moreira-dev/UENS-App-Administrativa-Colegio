package com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto;

/**
 * Processing result snapshot returned while polling a report request.
 *
 * @param solicitudId backend identifier of the report request
 * @param estado current backend processing state
 * @param resultadoJson backend-produced result metadata when available
 * @param errorDetalle backend-provided failure detail when generation fails
 */
public record ReporteSolicitudResultResponseDto(
        Long solicitudId,
        String estado,
        String resultadoJson,
        String errorDetalle) {
}
