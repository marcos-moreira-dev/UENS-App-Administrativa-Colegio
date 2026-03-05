package com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto;

import java.time.LocalDate;

/**
 * Parameters required to create an asynchronous report request.
 *
 * @param tipoReporte backend report type identifier
 * @param formatoSalida desired output format such as PDF or XLSX
 * @param seccionId optional section scope for the requested report
 * @param numeroParcial optional partial number when the report depends on grading periods
 * @param fechaDesde optional start date for range-based reports
 * @param fechaHasta optional end date for range-based reports
 */
public record ReporteSolicitudCreateRequestDto(
        String tipoReporte,
        String formatoSalida,
        Long seccionId,
        Integer numeroParcial,
        LocalDate fechaDesde,
        LocalDate fechaHasta) {
}
