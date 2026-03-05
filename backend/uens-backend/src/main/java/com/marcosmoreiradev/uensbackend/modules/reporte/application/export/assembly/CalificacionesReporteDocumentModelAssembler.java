package com.marcosmoreiradev.uensbackend.modules.reporte.application.export.assembly;

import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.model.ReporteDocumentModel;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Construye la vista documental del reporte de calificaciones por seccion y parcial.
 */
@Component
public class CalificacionesReporteDocumentModelAssembler extends AbstractReporteDocumentModelAssembler {

    private static final String TIPO = "CALIFICACIONES_POR_SECCION_Y_PARCIAL";

    public CalificacionesReporteDocumentModelAssembler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean soporta(String tipoReporte) {
        return TIPO.equalsIgnoreCase(tipoReporte);
    }

    @Override
    public ReporteDocumentModel assemble(ReporteSolicitudQueueJpaEntity solicitud, Object payload) {
        Map<String, Object> root = toMap(payload);
        String seccion = firstNonBlank(root.get("seccion"), buildSeccionFallback(root.get("seccionId")));
        String parcial = firstNonBlank(root.get("numeroParcial"), "-");

        Map<String, String> summary = newOrderedSection();
        putIfHasText(summary, "Seccion", seccion);
        putIfHasText(summary, "Parcial", parcial);
        putIfHasText(summary, "Total registros", root.get("totalCalificaciones"));
        putIfHasText(summary, "Promedio general", formatDecimal(root.get("promedioGeneral")));
        putIfHasText(summary, "Generado", formatDateTime(root.get("generadoEn")));
        putIfHasText(summary, "Solicitud", solicitud.getId());

        List<List<String>> rows = readItems(root).stream()
                .map(item -> List.of(
                        firstNonBlank(item.get("calificacionId"), "-"),
                        firstNonBlank(item.get("estudiante"), "-"),
                        firstNonBlank(item.get("asignatura"), "-"),
                        firstNonBlank(item.get("nota"), "-"),
                        firstNonBlank(formatDate(item.get("fechaRegistro")), "-"),
                        firstNonBlank(item.get("observacion"), "-")
                ))
                .toList();

        return new ReporteDocumentModel(
                null,
                "Calificaciones por Seccion y Parcial",
                "Resumen academico de " + firstNonBlank(seccion, "la seccion seleccionada") + " - Parcial " + parcial,
                summary,
                Map.of(),
                List.of("ID", "Estudiante", "Asignatura", "Nota", "Fecha registro", "Observacion"),
                rows,
                "No existen calificaciones para los filtros seleccionados."
        );
    }

    private String buildSeccionFallback(Object seccionId) {
        String normalized = normalize(seccionId);
        return normalized == null ? null : "Seccion #" + normalized;
    }
}
