package com.marcosmoreiradev.uensbackend.modules.reporte.application.export.assembly;

import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.model.ReporteDocumentModel;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Construye la vista documental del listado de estudiantes por seccion.
 */
@Component
public class ListadoEstudiantesReporteDocumentModelAssembler extends AbstractReporteDocumentModelAssembler {

    private static final String TIPO = "LISTADO_ESTUDIANTES_POR_SECCION";

    public ListadoEstudiantesReporteDocumentModelAssembler(ObjectMapper objectMapper) {
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

        Map<String, String> summary = newOrderedSection();
        putIfHasText(summary, "Seccion", seccion);
        putIfHasText(summary, "Total estudiantes", root.get("totalEstudiantes"));
        putIfHasText(summary, "Generado", formatDateTime(root.get("generadoEn")));
        putIfHasText(summary, "Solicitud", solicitud.getId());

        List<List<String>> rows = readItems(root).stream()
                .map(item -> List.of(
                        firstNonBlank(item.get("id"), "-"),
                        firstNonBlank(item.get("nombres"), "-"),
                        firstNonBlank(item.get("apellidos"), "-"),
                        firstNonBlank(item.get("estado"), "-")
                ))
                .toList();

        return new ReporteDocumentModel(
                null,
                "Listado de Estudiantes por Seccion",
                "Estudiantes vigentes asociados a " + firstNonBlank(seccion, "la seccion seleccionada"),
                summary,
                Map.of(),
                List.of("ID", "Nombres", "Apellidos", "Estado"),
                rows,
                "No existen estudiantes asociados a la seccion solicitada."
        );
    }

    private String buildSeccionFallback(Object seccionId) {
        String normalized = normalize(seccionId);
        return normalized == null ? null : "Seccion #" + normalized;
    }
}
