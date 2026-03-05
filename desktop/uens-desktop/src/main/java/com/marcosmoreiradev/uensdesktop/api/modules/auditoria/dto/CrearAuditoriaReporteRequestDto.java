package com.marcosmoreiradev.uensdesktop.api.modules.auditoria.dto;

import java.time.LocalDate;

/**
 * Parameters used to request an asynchronous audit report.
 *
 * @param formatoSalida desired output format such as PDF or XLSX
 * @param fechaDesde optional start date filter
 * @param fechaHasta optional end date filter
 * @param modulo optional module filter
 * @param accion optional action filter
 * @param resultado optional outcome filter
 * @param actorLogin optional actor login filter
 * @param incluirDetalle whether the backend should include the detailed event payload
 */
public record CrearAuditoriaReporteRequestDto(
        String formatoSalida,
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        String modulo,
        String accion,
        String resultado,
        String actorLogin,
        Boolean incluirDetalle) {
}
