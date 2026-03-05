package com.marcosmoreiradev.uensbackend.modules.reporte.application.export.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Modelo intermedio de presentacion usado para desacoplar los datos de un
 * reporte de la forma especifica en la que cada exportador los renderiza.
 *
 * @param organizationName nombre de la institucion o sistema que emite el reporte
 * @param title titulo principal del documento
 * @param subtitle subtitulo o descripcion breve del alcance del reporte
 * @param summary metadatos principales en formato etiqueta/valor
 * @param filters filtros aplicados durante la generacion
 * @param columns encabezados tabulares del bloque principal
 * @param rows filas tabulares ya normalizadas a texto
 * @param emptyMessage mensaje a mostrar cuando no existan registros
 */
public record ReporteDocumentModel(
        String organizationName,
        String title,
        String subtitle,
        Map<String, String> summary,
        Map<String, String> filters,
        List<String> columns,
        List<List<String>> rows,
        String emptyMessage
) {

    public ReporteDocumentModel {
        organizationName = hasText(organizationName)
                ? organizationName
                : "Unidad Educativa Ni\u00f1itos So\u00f1adores";
        title = hasText(title) ? title : "Reporte";
        subtitle = subtitle == null ? "" : subtitle.trim();
        summary = immutableOrderedMap(summary);
        filters = immutableOrderedMap(filters);
        columns = columns == null ? List.of() : List.copyOf(columns);
        rows = rows == null
                ? List.of()
                : rows.stream().map(row -> row == null ? List.<String>of() : List.copyOf(row)).toList();
        emptyMessage = hasText(emptyMessage) ? emptyMessage : "Sin registros disponibles.";
    }

    public boolean hasSummary() {
        return !summary.isEmpty();
    }

    public boolean hasFilters() {
        return !filters.isEmpty();
    }

    public boolean hasRows() {
        return !rows.isEmpty();
    }

    private static Map<String, String> immutableOrderedMap(Map<String, String> source) {
        if (source == null || source.isEmpty()) {
            return Map.of();
        }
        return Collections.unmodifiableMap(new LinkedHashMap<>(source));
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
