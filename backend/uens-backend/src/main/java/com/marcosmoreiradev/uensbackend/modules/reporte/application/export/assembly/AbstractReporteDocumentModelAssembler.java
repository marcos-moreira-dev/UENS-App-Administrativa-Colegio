package com.marcosmoreiradev.uensbackend.modules.reporte.application.export.assembly;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Base utilitaria para ensambladores de documentos de reporte.
 */
abstract class AbstractReporteDocumentModelAssembler implements ReporteDocumentModelAssembler {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ObjectMapper objectMapper;

    protected AbstractReporteDocumentModelAssembler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected Map<String, Object> toMap(Object payload) {
        if (payload == null) {
            return Map.of();
        }
        return objectMapper.convertValue(payload, new TypeReference<Map<String, Object>>() {});
    }

    protected List<Map<String, Object>> readItems(Map<String, Object> root) {
        Object itemsRaw = root.get("items");
        if (!(itemsRaw instanceof List<?> items)) {
            return List.of();
        }
        return items.stream()
                .map(item -> objectMapper.convertValue(item, new TypeReference<Map<String, Object>>() {}))
                .toList();
    }

    protected Map<String, String> newOrderedSection() {
        return new LinkedHashMap<>();
    }

    protected void putIfHasText(Map<String, String> target, String key, Object value) {
        String normalized = normalize(value);
        if (normalized != null) {
            target.put(key, normalized);
        }
    }

    protected String normalize(Object value) {
        if (value == null) {
            return null;
        }
        String normalized = value.toString().trim();
        return normalized.isEmpty() ? null : normalized;
    }

    protected String formatDateTime(Object value) {
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.format(DATE_TIME_FORMATTER);
        }
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(normalized).format(DATE_TIME_FORMATTER);
        } catch (Exception ignored) {
            return normalized;
        }
    }

    protected String formatDate(Object value) {
        if (value instanceof LocalDate localDate) {
            return localDate.format(DATE_FORMATTER);
        }
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }
        try {
            return LocalDate.parse(normalized).format(DATE_FORMATTER);
        } catch (Exception ignored) {
            return normalized;
        }
    }

    protected String formatDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.stripTrailingZeros().toPlainString();
        }
        return Objects.toString(value, null);
    }

    protected String firstNonBlank(Object... values) {
        for (Object value : values) {
            String normalized = normalize(value);
            if (normalized != null) {
                return normalized;
            }
        }
        return null;
    }
}
