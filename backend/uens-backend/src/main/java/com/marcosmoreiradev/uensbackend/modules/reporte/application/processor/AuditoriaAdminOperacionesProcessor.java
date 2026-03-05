package com.marcosmoreiradev.uensbackend.modules.reporte.application.processor;

import com.marcosmoreiradev.uensbackend.modules.auditoria.infrastructure.persistence.entity.AuditoriaEventoJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.auditoria.infrastructure.persistence.repository.AuditoriaEventoJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.mapper.ReportePayloadDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class AuditoriaAdminOperacionesProcessor implements ReporteDataProcessor {

    public static final String TIPO = "AUDITORIA_ADMIN_OPERACIONES";

    private final AuditoriaEventoJpaRepository auditoriaEventoRepository;
    private final ReportePayloadDtoMapper payloadMapper;

    public AuditoriaAdminOperacionesProcessor(
            AuditoriaEventoJpaRepository auditoriaEventoRepository,
            ReportePayloadDtoMapper payloadMapper
    ) {
        this.auditoriaEventoRepository = auditoriaEventoRepository;
        this.payloadMapper = payloadMapper;
    }

    @Override
    public boolean soporta(String tipoReporte) {
        return TIPO.equalsIgnoreCase(tipoReporte);
    }

    @Override
    public Object procesar(ReporteSolicitudQueueJpaEntity solicitud) {
        Map<String, Object> params = payloadMapper.toMap(solicitud.getParametrosJson());
        LocalDate fechaDesde = parseDate(params.get("fechaDesde"));
        LocalDate fechaHasta = parseDate(params.get("fechaHasta"));
        String modulo = normalizeUpper(readString(params.get("modulo")));
        String accion = normalizeUpper(readString(params.get("accion")));
        String resultado = normalizeUpper(readString(params.get("resultado")));
        String actorLogin = normalize(readString(params.get("actorLogin")));
        boolean incluirDetalle = readBoolean(params.get("incluirDetalle"), true);

        int maxItems = normalizeMaxItems(params.get("maxItems"));

        Specification<AuditoriaEventoJpaEntity> spec = buildSpec(
                modulo,
                accion,
                resultado,
                actorLogin,
                fechaDesde,
                fechaHasta
        );

        List<AuditoriaEventoJpaEntity> eventos = auditoriaEventoRepository
                .findAll(spec, PageRequest.of(0, maxItems, Sort.by(Sort.Order.desc("fechaEvento"))))
                .getContent();

        long totalExitos = eventos.stream().filter(e -> "EXITO".equalsIgnoreCase(e.getResultado())).count();
        long totalErrores = eventos.stream().filter(e -> "ERROR".equalsIgnoreCase(e.getResultado())).count();

        Map<String, Object> filtros = new LinkedHashMap<>();
        filtros.put("fechaDesde", fechaDesde);
        filtros.put("fechaHasta", fechaHasta);
        filtros.put("modulo", modulo);
        filtros.put("accion", accion);
        filtros.put("resultado", resultado);
        filtros.put("actorLogin", actorLogin);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("tipoReporte", TIPO);
        payload.put("generadoEn", LocalDateTime.now());
        payload.put("filtros", filtros);
        payload.put("totalEventos", eventos.size());
        payload.put("totalExitos", totalExitos);
        payload.put("totalErrores", totalErrores);
        payload.put("items", eventos.stream().map(evento -> toItem(evento, incluirDetalle)).toList());
        return payload;
    }

    private static Map<String, Object> toItem(AuditoriaEventoJpaEntity evento, boolean incluirDetalle) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("eventoId", evento.getId());
        item.put("fechaEvento", evento.getFechaEvento());
        item.put("modulo", evento.getModulo());
        item.put("accion", evento.getAccion());
        item.put("entidad", evento.getEntidad());
        item.put("entidadId", evento.getEntidadId());
        item.put("resultado", evento.getResultado());
        item.put("actorLogin", evento.getActorLogin());
        item.put("actorRol", evento.getActorRol());
        item.put("requestId", evento.getRequestId());
        item.put("ipOrigen", evento.getIpOrigen());
        if (incluirDetalle) {
            item.put("detalle", evento.getDetalle());
        }
        return item;
    }

    private static Specification<AuditoriaEventoJpaEntity> buildSpec(
            String modulo,
            String accion,
            String resultado,
            String actorLogin,
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {
        LocalDateTime fromDateTime = fechaDesde == null ? null : fechaDesde.atStartOfDay();
        LocalDateTime toDateTime = fechaHasta == null ? null : fechaHasta.plusDays(1L).atStartOfDay();

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (modulo != null) {
                predicates.add(cb.equal(root.get("modulo"), modulo));
            }
            if (accion != null) {
                predicates.add(cb.equal(root.get("accion"), accion));
            }
            if (resultado != null) {
                predicates.add(cb.equal(root.get("resultado"), resultado));
            }
            if (actorLogin != null) {
                predicates.add(cb.equal(cb.lower(root.get("actorLogin")), actorLogin.toLowerCase(Locale.ROOT)));
            }
            if (fromDateTime != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaEvento"), fromDateTime));
            }
            if (toDateTime != null) {
                predicates.add(cb.lessThan(root.get("fechaEvento"), toDateTime));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static LocalDate parseDate(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(value.toString());
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String readString(Object value) {
        if (value == null) {
            return null;
        }
        String normalized = value.toString().trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private static String normalizeUpper(String value) {
        return value == null ? null : value.toUpperCase(Locale.ROOT);
    }

    private static boolean readBoolean(Object value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.toString());
    }

    private static int normalizeMaxItems(Object value) {
        if (value == null) {
            return 1000;
        }
        try {
            int parsed = Integer.parseInt(value.toString());
            if (parsed < 1) {
                return 1000;
            }
            return Math.min(parsed, 5000);
        } catch (Exception ignored) {
            return 1000;
        }
    }
}
