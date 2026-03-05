package com.marcosmoreiradev.uensbackend.modules.reporte.application.export;

import com.marcosmoreiradev.uensbackend.common.exception.base.ApplicationException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ApiErrorCodes;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * Selecciona el exportador de archivos adecuado para un formato de salida.
 */
@Component
public class ReporteFileExporterSelector {

    private final List<ReporteFileExporter> exporters;

    /**
     * Crea el selector con la lista de exportadores registrados en Spring.
     *
     * @param exporters implementaciones disponibles de {@link ReporteFileExporter}
     */
    public ReporteFileExporterSelector(List<ReporteFileExporter> exporters) {
        this.exporters = exporters;
    }

    /**
     * Resuelve el exportador que soporta el formato solicitado.
     *
     * @param formato formato de salida solicitado; si viene vacio se asume XLSX
     * @return exportador compatible con el formato
     * @throws ApplicationException cuando no existe implementacion para el formato
     */
    public ReporteFileExporter seleccionar(String formato) {
        String normalized = normalizeFormato(formato);
        return exporters.stream()
                .filter(exporter -> exporter.soporta(normalized))
                .findFirst()
                .orElseThrow(() -> new ApplicationException(
                        ApiErrorCodes.API_10_ENDPOINT_EN_CONSTRUCCION,
                        "Formato de salida no soportado en V1."
                ));
    }

    private static String normalizeFormato(String formato) {
        if (formato == null || formato.isBlank()) {
            return "XLSX";
        }
        return formato.trim().toUpperCase(Locale.ROOT);
    }
}
