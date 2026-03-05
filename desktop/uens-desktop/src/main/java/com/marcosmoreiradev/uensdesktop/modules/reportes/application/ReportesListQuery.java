package com.marcosmoreiradev.uensdesktop.modules.reportes.application;

public record ReportesListQuery(
        int page,
        int size,
        String query,
        String tipoReporte,
        String estado) {
}
