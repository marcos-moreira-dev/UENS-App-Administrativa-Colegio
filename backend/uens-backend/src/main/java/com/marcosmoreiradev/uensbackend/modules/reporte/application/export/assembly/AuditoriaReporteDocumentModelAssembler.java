package com.marcosmoreiradev.uensbackend.modules.reporte.application.export.assembly;

import com.marcosmoreiradev.uensbackend.modules.reporte.application.export.model.ReporteDocumentModel;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Construye la vista documental del reporte operativo de auditoria administrativa.
 */
@Component
public class AuditoriaReporteDocumentModelAssembler extends AbstractReporteDocumentModelAssembler {

    private static final String TIPO = "AUDITORIA_ADMIN_OPERACIONES";

    public AuditoriaReporteDocumentModelAssembler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean soporta(String tipoReporte) {
        return TIPO.equalsIgnoreCase(tipoReporte);
    }

    @Override
    public ReporteDocumentModel assemble(ReporteSolicitudQueueJpaEntity solicitud, Object payload) {
        Map<String, Object> root = toMap(payload);

        Map<String, String> summary = newOrderedSection();
        putIfHasText(summary, "Total eventos", root.get("totalEventos"));
        putIfHasText(summary, "Exitos", root.get("totalExitos"));
        putIfHasText(summary, "Errores", root.get("totalErrores"));
        putIfHasText(summary, "Generado", formatDateTime(root.get("generadoEn")));
        putIfHasText(summary, "Solicitud", solicitud.getId());

        Map<String, Object> filtrosRaw = new java.util.LinkedHashMap<>();
        if (root.get("filtros") instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                filtrosRaw.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        }

        Map<String, String> filters = newOrderedSection();
        putIfHasText(filters, "Fecha desde", formatDate(filtrosRaw.get("fechaDesde")));
        putIfHasText(filters, "Fecha hasta", formatDate(filtrosRaw.get("fechaHasta")));
        putIfHasText(filters, "Modulo", filtrosRaw.get("modulo"));
        putIfHasText(filters, "Accion", filtrosRaw.get("accion"));
        putIfHasText(filters, "Resultado", filtrosRaw.get("resultado"));
        putIfHasText(filters, "Actor login", filtrosRaw.get("actorLogin"));

        List<List<String>> rows = readItems(root).stream()
                .map(item -> List.of(
                        firstNonBlank(formatDateTime(item.get("fechaEvento")), "-"),
                        firstNonBlank(item.get("modulo"), "-"),
                        firstNonBlank(item.get("accion"), "-"),
                        firstNonBlank(item.get("entidad"), "-"),
                        firstNonBlank(item.get("entidadId"), "-"),
                        firstNonBlank(item.get("resultado"), "-"),
                        firstNonBlank(item.get("actorLogin"), "-"),
                        firstNonBlank(item.get("actorRol"), "-"),
                        firstNonBlank(item.get("requestId"), "-"),
                        firstNonBlank(item.get("ipOrigen"), "-"),
                        firstNonBlank(item.get("detalle"), "-")
                ))
                .toList();

        return new ReporteDocumentModel(
                null,
                "Auditoria de Operaciones Administrativas",
                "Trazabilidad operativa de acciones administrativas registradas por el backend",
                summary,
                filters,
                List.of("Fecha", "Modulo", "Accion", "Entidad", "Entidad ID", "Resultado", "Actor", "Rol", "Request ID", "IP", "Detalle"),
                rows,
                "No existen eventos de auditoria para los filtros seleccionados."
        );
    }
}
