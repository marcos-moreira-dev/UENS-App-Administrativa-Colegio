package com.marcosmoreiradev.uensbackend.modules.reporte.application.mapper;

import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;

/**
 * Utilidades de serializacion/deserializacion para payloads de reportes.
 */
@Component
public class ReportePayloadDtoMapper {

    private final ObjectMapper objectMapper;

    /**
     * Crea el mapper con el {@link ObjectMapper} compartido de la aplicacion.
     *
     * @param objectMapper serializador/deserializador JSON configurado en Spring
     */
    public ReportePayloadDtoMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Convierte un objeto Java a cadena JSON.
     *
     * @param value objeto a serializar
     * @return representacion JSON del objeto
     * @throws IllegalStateException cuando la serializacion falla
     */
    public String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new IllegalStateException("No fue posible serializar el payload de reporte.", ex);
        }
    }

    /**
     * Convierte texto JSON a {@link JsonNode}. Si el texto es invalido retorna un objeto vacio.
     *
     * @param rawJson contenido JSON en texto
     * @return nodo JSON parseado o un objeto vacio
     */
    public JsonNode toJsonNode(String rawJson) {
        try {
            if (rawJson == null || rawJson.isBlank()) {
                return objectMapper.createObjectNode();
            }
            return objectMapper.readTree(rawJson);
        } catch (Exception ex) {
            return objectMapper.createObjectNode();
        }
    }

    /**
     * Obtiene un campo textual desde un JSON de entrada con valor por defecto.
     *
     * @param rawJson contenido JSON en texto
     * @param fieldName nombre del campo a leer
     * @param defaultValue valor a usar cuando el campo no existe o esta vacio
     * @return valor textual del campo o el valor por defecto
     */
    public String readText(String rawJson, String fieldName, String defaultValue) {
        JsonNode root = toJsonNode(rawJson);
        JsonNode value = root.path(fieldName);
        if (value.isMissingNode() || value.isNull() || value.asString().isBlank()) {
            return defaultValue;
        }
        return value.asString();
    }

    /**
     * Convierte texto JSON a un mapa plano.
     *
     * @param rawJson contenido JSON en texto
     * @return mapa con los datos parseados o mapa vacio si falla el parseo
     */
    public Map<String, Object> toMap(String rawJson) {
        try {
            if (rawJson == null || rawJson.isBlank()) {
                return Collections.emptyMap();
            }
            return objectMapper.readValue(rawJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            return Collections.emptyMap();
        }
    }
}
