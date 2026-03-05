package com.marcosmoreiradev.uensdesktop.modules.reportes.presenter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcosmoreiradev.uensdesktop.api.modules.reportes.dto.ReporteSolicitudDetailResponseDto;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ReportesPresenter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int MAX_PREVIEW_ITEMS = 3;

    public ReporteSolicitudDetailPresentation presentDetail(ReporteSolicitudDetailResponseDto detail) {
        return new ReporteSolicitudDetailPresentation(
                String.valueOf(detail.solicitudId()),
                formatTipoReporte(detail.tipoReporte()),
                formatEstado(detail.estado()),
                String.valueOf(detail.intentos()),
                formatDateTime(detail.fechaSolicitud()),
                formatDateTime(detail.fechaActualizacion()),
                formatJsonBlock(detail.parametrosJson()),
                formatJsonBlock(detail.resultadoJson()),
                formatJsonBlock(detail.errorDetalle()),
                isDownloadAvailable(detail.estado()),
                isErrorState(detail.estado()));
    }

    public String formatTipoReporte(String value) {
        if (value == null) {
            return "-";
        }
        return switch (value) {
            case "LISTADO_ESTUDIANTES_POR_SECCION" -> "Listado por secci\u00f3n";
            case "CALIFICACIONES_POR_SECCION_Y_PARCIAL" -> "Calificaciones por parcial";
            case "AUDITORIA_ADMIN_OPERACIONES" -> "Auditor\u00eda admin";
            default -> value;
        };
    }

    public String formatEstado(String value) {
        if (value == null) {
            return "-";
        }
        return switch (value) {
            case "EN_PROCESO" -> "En proceso";
            case "PENDIENTE" -> "Pendiente";
            case "COMPLETADA" -> "Completada";
            case "ERROR" -> "Error";
            default -> value;
        };
    }

    public String statusStyleClass(String estado) {
        if (estado == null) {
            return "status-pendiente";
        }
        return switch (estado) {
            case "COMPLETADA" -> "status-completada";
            case "ERROR" -> "status-error";
            case "EN_PROCESO" -> "status-en-proceso";
            default -> "status-pendiente";
        };
    }

    public String formatDateTime(LocalDateTime value) {
        return value == null ? "-" : value.format(DATE_TIME_FORMATTER);
    }

    public String formatJsonBlock(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        try {
            JsonNode root = OBJECT_MAPPER.readTree(value);
            if (isReportResult(root)) {
                return formatReportResult(root);
            }
            String formatted = formatNode(root, 0).trim();
            return formatted.isBlank() ? value : formatted;
        } catch (Exception ex) {
            return value;
        }
    }

    public boolean isDownloadAvailable(String estado) {
        return "COMPLETADA".equalsIgnoreCase(estado);
    }

    public boolean isErrorState(String estado) {
        return "ERROR".equalsIgnoreCase(estado);
    }

    public String buildPollingStatus(long solicitudId, String estado) {
        return "Solicitud #" + solicitudId + " en estado " + formatEstado(estado) + ".";
    }

    public String buildPollingFinishedStatus(long solicitudId, String estado) {
        return "Seguimiento finalizado para solicitud #" + solicitudId + ": " + formatEstado(estado) + ".";
    }

    public String buildDownloadPathLabel(Path path) {
        return "Ruta del archivo:\n" + formatDownloadPath(path);
    }

    private String formatReportResult(JsonNode root) {
        StringBuilder builder = new StringBuilder();
        JsonNode archivo = root.path("archivo");
        JsonNode payload = root.path("payload");

        if (archivo.isObject() && archivo.size() > 0) {
            builder.append("Archivo generado\n");
            appendIfPresent(builder, "Nombre", archivo.get("nombreArchivo"));
            appendIfPresent(builder, "Formato", archivo.get("formato"));
            appendIfPresent(builder, "Tipo MIME", archivo.get("mimeType"));
            appendIfPresent(builder, "Tama\u00f1o (bytes)", archivo.get("tamanoBytes"));
            appendIfPresent(builder, "Generado", archivo.get("generadoEn"));
            appendIfPresent(builder, "Ruta relativa", archivo.get("rutaRelativa"));
        }

        if (payload.isObject() && payload.size() > 0) {
            if (!builder.isEmpty()) {
                builder.append("\n");
            }
            builder.append("Resumen del reporte\n");
            appendPayloadSummary(builder, payload);
        }

        return builder.toString().trim();
    }

    private void appendPayloadSummary(StringBuilder builder, JsonNode payload) {
        Map<String, String> summaryFields = new LinkedHashMap<>();
        summaryFields.put("tipoReporte", "Tipo");
        summaryFields.put("seccion", "Secci\u00f3n");
        summaryFields.put("numeroParcial", "Parcial");
        summaryFields.put("totalEstudiantes", "Total estudiantes");
        summaryFields.put("totalCalificaciones", "Total calificaciones");
        summaryFields.put("totalEventos", "Total eventos");
        summaryFields.put("totalExitos", "Eventos exitosos");
        summaryFields.put("totalErrores", "Eventos con error");
        summaryFields.put("promedioGeneral", "Promedio general");
        summaryFields.put("generadoEn", "Generado");

        for (Map.Entry<String, String> entry : summaryFields.entrySet()) {
            appendIfPresent(builder, entry.getValue(), payload.get(entry.getKey()));
        }

        JsonNode filtros = payload.get("filtros");
        if (filtros != null && filtros.isObject() && filtros.size() > 0) {
            builder.append("\nFiltros aplicados\n");
            appendObjectFields(builder, filtros, 0);
        }

        JsonNode items = payload.get("items");
        if (items != null && items.isArray()) {
            builder.append("\nFilas incluidas: ").append(items.size());
            appendItemsPreview(builder, items);
        }
    }

    private void appendItemsPreview(StringBuilder builder, JsonNode items) {
        int limit = Math.min(items.size(), MAX_PREVIEW_ITEMS);
        if (limit == 0) {
            return;
        }
        builder.append("\nVista r\u00e1pida\n");
        for (int index = 0; index < limit; index++) {
            JsonNode item = items.get(index);
            builder.append(index + 1).append(". ");
            if (item == null || item.isNull()) {
                builder.append("-\n");
                continue;
            }
            if (item.isObject()) {
                builder.append(compactObjectPreview(item)).append('\n');
            } else {
                builder.append(scalarText(item)).append('\n');
            }
        }
    }

    private String compactObjectPreview(JsonNode objectNode) {
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
        int count = 0;
        while (fields.hasNext() && count < 4) {
            Map.Entry<String, JsonNode> entry = fields.next();
            if (entry.getValue().isContainerNode()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(" | ");
            }
            builder.append(prettyKey(entry.getKey())).append(": ").append(scalarText(entry.getValue()));
            count++;
        }
        return builder.isEmpty() ? "-" : builder.toString();
    }

    private String formatNode(JsonNode node, int depth) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return "-";
        }
        if (node.isValueNode()) {
            return scalarText(node);
        }
        if (node.isObject()) {
            StringBuilder builder = new StringBuilder();
            appendObjectFields(builder, node, depth);
            return builder.toString();
        }
        if (node.isArray()) {
            StringBuilder builder = new StringBuilder();
            appendArray(builder, node, depth);
            return builder.toString();
        }
        return node.toString();
    }

    private void appendObjectFields(StringBuilder builder, JsonNode objectNode, int depth) {
        Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            appendField(builder, prettyKey(entry.getKey()), entry.getValue(), depth);
        }
    }

    private void appendField(StringBuilder builder, String label, JsonNode value, int depth) {
        String indent = "  ".repeat(depth);
        if (value == null || value.isNull() || value.isMissingNode()) {
            builder.append(indent).append(label).append(": -\n");
            return;
        }
        if (value.isValueNode()) {
            builder.append(indent).append(label).append(": ").append(scalarText(value)).append('\n');
            return;
        }
        builder.append(indent).append(label).append(":\n");
        if (value.isObject()) {
            appendObjectFields(builder, value, depth + 1);
            return;
        }
        appendArray(builder, value, depth + 1);
    }

    private void appendArray(StringBuilder builder, JsonNode arrayNode, int depth) {
        String indent = "  ".repeat(depth);
        if (!arrayNode.isArray() || arrayNode.isEmpty()) {
            builder.append(indent).append("-\n");
            return;
        }
        for (JsonNode item : arrayNode) {
            if (item.isValueNode()) {
                builder.append(indent).append("- ").append(scalarText(item)).append('\n');
                continue;
            }
            builder.append(indent).append("-\n");
            if (item.isObject()) {
                appendObjectFields(builder, item, depth + 1);
            } else {
                appendArray(builder, item, depth + 1);
            }
        }
    }

    private void appendIfPresent(StringBuilder builder, String label, JsonNode value) {
        if (value == null || value.isNull() || value.isMissingNode()) {
            return;
        }
        String normalized = scalarText(value);
        if (normalized.isBlank() || "-".equals(normalized)) {
            return;
        }
        builder.append(label).append(": ").append(normalized).append('\n');
    }

    private boolean isReportResult(JsonNode root) {
        return root != null && root.isObject() && (root.has("archivo") || root.has("payload"));
    }

    private String scalarText(JsonNode value) {
        if (value == null || value.isNull()) {
            return "-";
        }
        if (value.isTextual()) {
            return value.asText().isBlank() ? "-" : value.asText();
        }
        return value.asText();
    }

    private String prettyKey(String rawKey) {
        return switch (rawKey) {
            case "requestId" -> "Request ID";
            case "mimeType" -> "Tipo MIME";
            case "tamanoBytes" -> "Tama\u00f1o (bytes)";
            case "nombreArchivo" -> "Nombre";
            case "rutaRelativa" -> "Ruta relativa";
            case "fechaDesde" -> "Fecha desde";
            case "fechaHasta" -> "Fecha hasta";
            case "actorLogin" -> "Actor login";
            case "actorRol" -> "Rol";
            case "ipOrigen" -> "IP origen";
            case "fechaEvento" -> "Fecha evento";
            case "generadoEn" -> "Generado";
            default -> titleCase(rawKey.replace('_', ' '));
        };
    }

    private String titleCase(String rawValue) {
        String spaced = rawValue.replaceAll("([a-z0-9])([A-Z])", "$1 $2");
        String[] parts = spaced.trim().split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1).toLowerCase());
            }
        }
        return builder.isEmpty() ? rawValue : builder.toString();
    }

    private String formatDownloadPath(Path path) {
        if (path == null) {
            return "-";
        }
        String value = path.toString();
        if (value.isBlank()) {
            return "-";
        }
        if (value.contains("\\")) {
            return value.replace("\\", "\\\n");
        }
        if (value.contains("/")) {
            return value.replace("/", "/\n");
        }
        return value;
    }

    public record ReporteSolicitudDetailPresentation(
            String solicitudId,
            String tipoReporte,
            String estado,
            String intentos,
            String fechaSolicitud,
            String fechaActualizacion,
            String parametrosJson,
            String resultadoJson,
            String errorDetalle,
            boolean downloadAvailable,
            boolean errorState) {
    }
}
