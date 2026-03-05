package com.marcosmoreiradev.uensbackend.modules.reporte.application.processor;

import com.marcosmoreiradev.uensbackend.modules.calificacion.infrastructure.persistence.entity.CalificacionJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.calificacion.infrastructure.persistence.repository.CalificacionJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.entity.SeccionJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.repository.SeccionJpaRepository;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Procesador para el reporte de calificaciones por seccion y parcial.
 */
@Component
public class CalificacionesPorSeccionParcialProcessor implements ReporteDataProcessor {

    private static final String TIPO = "CALIFICACIONES_POR_SECCION_Y_PARCIAL";

    private final CalificacionJpaRepository calificacionRepository;
    private final SeccionJpaRepository seccionRepository;
    private final ObjectMapper objectMapper;

    /**
     * Crea el procesador con acceso a calificaciones y soporte de parseo JSON.
     *
     * @param calificacionRepository repositorio de calificaciones persistidas
     * @param seccionRepository repositorio de secciones para enriquecer el encabezado
     * @param objectMapper mapper JSON para leer parametros de la solicitud
     */
    public CalificacionesPorSeccionParcialProcessor(
            CalificacionJpaRepository calificacionRepository,
            SeccionJpaRepository seccionRepository,
            ObjectMapper objectMapper
    ) {
        this.calificacionRepository = calificacionRepository;
        this.seccionRepository = seccionRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean soporta(String tipoReporte) {
        return TIPO.equalsIgnoreCase(tipoReporte);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object procesar(ReporteSolicitudQueueJpaEntity solicitud) {
        JsonNode params = parseJson(solicitud.getParametrosJson());
        Long seccionId = params.path("seccionId").isNumber() ? params.path("seccionId").asLong() : null;
        Integer numeroParcial = params.path("numeroParcial").isInt() ? params.path("numeroParcial").asInt() : null;

        List<CalificacionJpaEntity> calificaciones = calificacionRepository.findAll().stream()
                .filter(calificacion -> calificacion.getClase() != null && calificacion.getClase().getSeccion() != null)
                .filter(calificacion -> Objects.equals(calificacion.getClase().getSeccion().getId(), seccionId))
                .filter(calificacion -> numeroParcial == null || Objects.equals(calificacion.getNumeroParcial(), numeroParcial))
                .toList();

        BigDecimal promedioGeneral = calcularPromedio(calificaciones);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("tipoReporte", TIPO);
        payload.put("seccionId", seccionId);
        payload.put("seccion", resolveSeccionDisplayName(seccionId));
        payload.put("numeroParcial", numeroParcial);
        payload.put("totalCalificaciones", calificaciones.size());
        payload.put("promedioGeneral", promedioGeneral);
        payload.put("generadoEn", LocalDateTime.now());
        payload.put("items", calificaciones.stream()
                .map(calificacion -> Map.of(
                        "calificacionId", calificacion.getId(),
                        "estudianteId", calificacion.getEstudiante().getId(),
                        "estudiante", (calificacion.getEstudiante().getNombres() + " " + calificacion.getEstudiante().getApellidos()).trim(),
                        "asignatura", calificacion.getClase().getAsignatura().getNombre(),
                        "nota", calificacion.getNota(),
                        "fechaRegistro", calificacion.getFechaRegistro(),
                        "observacion", calificacion.getObservacion() == null ? "" : calificacion.getObservacion()
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

    private BigDecimal calcularPromedio(List<CalificacionJpaEntity> calificaciones) {
        if (calificaciones.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal sumatoria = calificaciones.stream()
                .map(CalificacionJpaEntity::getNota)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sumatoria.divide(BigDecimal.valueOf(calificaciones.size()), 2, RoundingMode.HALF_UP);
    }

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
