package com.marcosmoreiradev.uensbackend.modules.reporte.application.processor;

import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.entity.EstudianteJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.repository.EstudianteJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.entity.SeccionJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.repository.SeccionJpaRepository;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
/**
 * Define la responsabilidad de ListadoEstudiantesPorSeccionProcessor dentro del backend UENS.
 * Contexto: modulo reporte, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class ListadoEstudiantesPorSeccionProcessor implements ReporteDataProcessor {

    private static final String TIPO = "LISTADO_ESTUDIANTES_POR_SECCION";

    private final EstudianteJpaRepository estudianteRepository;
    private final SeccionJpaRepository seccionRepository;
    private final ObjectMapper objectMapper;
/**
 * Construye la instancia de ListadoEstudiantesPorSeccionProcessor para operar en el modulo reporte.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param estudianteRepository dato de entrada relevante para ejecutar esta operacion: 'estudianteRepository'
     * @param seccionRepository dato de entrada relevante para ejecutar esta operacion: 'seccionRepository'
     * @param objectMapper serializador JSON usado para claims, payloads o campos JSONB
 */

    public ListadoEstudiantesPorSeccionProcessor(
            EstudianteJpaRepository estudianteRepository,
            SeccionJpaRepository seccionRepository,
            ObjectMapper objectMapper
    ) {
        this.estudianteRepository = estudianteRepository;
        this.seccionRepository = seccionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
/**
 * Implementa la operacion 'soporta' del modulo reporte en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param tipoReporte tipo funcional de reporte solicitado para procesamiento asincrono
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public boolean soporta(String tipoReporte) {
        return TIPO.equalsIgnoreCase(tipoReporte);
    }

    @Override
/**
 * Implementa la operacion 'procesar' del modulo reporte en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param solicitud dato de entrada relevante para ejecutar esta operacion: 'solicitud'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Object procesar(ReporteSolicitudQueueJpaEntity solicitud) {
        JsonNode params = parseJson(solicitud.getParametrosJson());
        Long seccionId = params.path("seccionId").isNumber() ? params.path("seccionId").asLong() : null;

        List<EstudianteJpaEntity> estudiantes = estudianteRepository.findAll().stream()
                .filter(e -> e.getSeccion() != null && Objects.equals(e.getSeccion().getId(), seccionId))
                .toList();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("tipoReporte", TIPO);
        payload.put("seccionId", seccionId);
        payload.put("seccion", resolveSeccionDisplayName(seccionId));
        payload.put("totalEstudiantes", estudiantes.size());
        payload.put("generadoEn", LocalDateTime.now());
        payload.put("items", estudiantes.stream()
                .map(e -> Map.of(
                        "id", e.getId(),
                        "nombres", e.getNombres(),
                        "apellidos", e.getApellidos(),
                        "estado", e.getEstado()
                ))
                .toList());

        return payload;
    }

    private String resolveSeccionDisplayName(Long seccionId) {
        if (seccionId == null) {
            return null;
        }
        return seccionRepository.findById(seccionId)
                .map(this::formatSeccion)
                .orElse("Seccion #" + seccionId);
    }

    private String formatSeccion(SeccionJpaEntity seccion) {
        return seccion.getGrado() + " " + seccion.getParalelo() + " - " + seccion.getAnioLectivo();
    }

/**
 * Metodo de soporte interno 'parseJson' para mantener cohesion en ListadoEstudiantesPorSeccionProcessor.
 * Contexto: modulo reporte, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param rawJson dato de entrada relevante para ejecutar esta operacion: 'rawJson'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private JsonNode parseJson(String rawJson) {
        try {
            if (rawJson == null || rawJson.isBlank()) {
                return objectMapper.createObjectNode();
            }
            return objectMapper.readTree(rawJson);
        } catch (Exception ex) {
            return objectMapper.createObjectNode();
        }
    }
}
