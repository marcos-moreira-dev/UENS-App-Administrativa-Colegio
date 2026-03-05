package com.marcosmoreiradev.uensbackend.modules.reporte.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.BusinessRuleException;
import com.marcosmoreiradev.uensbackend.common.exception.base.InfrastructureException;
import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ReporteErrorCodes;
import com.marcosmoreiradev.uensbackend.common.exception.base.AuthException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.AuthErrorCodes;
import com.marcosmoreiradev.uensbackend.common.pagination.PageableFactory;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.model.DocumentDownloadResource;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.ReporteSolicitudDetalleResponseDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.ReporteSolicitudListItemDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.ReporteSolicitudResultadoResponseDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.mapper.ReportePayloadDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.mapper.ReporteSolicitudDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.model.ReporteArchivoDescarga;
import com.marcosmoreiradev.uensbackend.modules.reporte.application.port.DocumentStoragePort;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.repository.ReporteSolicitudQueueJpaRepository;
import com.marcosmoreiradev.uensbackend.security.user.CurrentAuthenticatedUserService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Resuelve consultas sobre la cola de reportes y la metadata de salida sin
 * acoplar la capa application a excepciones HTTP directas.
 */
@Service
public class ReporteSolicitudQueryService {

    private static final Set<String> SORT_WHITELIST = Set.of("id", "fechaSolicitud", "estado", "tipoReporte");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("fechaSolicitud"));
    private static final Set<String> ALLOWED_REPORT_MIME_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    private final ReporteSolicitudQueueJpaRepository repository;
    private final ReporteSolicitudDtoMapper mapper;
    private final ReportePayloadDtoMapper payloadMapper;
    private final PageableFactory pageableFactory;
    private final CurrentAuthenticatedUserService currentAuthenticatedUserService;
    private final DocumentStoragePort documentStoragePort;

    public ReporteSolicitudQueryService(
            ReporteSolicitudQueueJpaRepository repository,
            ReporteSolicitudDtoMapper mapper,
            ReportePayloadDtoMapper payloadMapper,
            PageableFactory pageableFactory,
            CurrentAuthenticatedUserService currentAuthenticatedUserService,
            DocumentStoragePort documentStoragePort
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.payloadMapper = payloadMapper;
        this.pageableFactory = pageableFactory;
        this.currentAuthenticatedUserService = currentAuthenticatedUserService;
        this.documentStoragePort = documentStoragePort;
    }

    /**
     * Lista solicitudes de reporte usando filtros opcionales y paginacion
     * comun compartida con el resto del backend.
     *
     * @param q texto libre para coincidencias parciales
     * @param tipoReporte tipo de reporte solicitado
     * @param estado estado actual de la solicitud
     * @param page pagina base cero
     * @param size tamano solicitado
     * @param sort criterios de ordenamiento permitidos
     * @return pagina de solicitudes de reporte
     */
    @Transactional(readOnly = true)
    public Page<ReporteSolicitudListItemDto> listar(
            String q,
            String tipoReporte,
            String estado,
            Integer page,
            Integer size,
            List<String> sort
    ) {
        Pageable pageable = pageableFactory.from(page, size, sort, DEFAULT_SORT, SORT_WHITELIST);
        Specification<ReporteSolicitudQueueJpaEntity> spec = buildSpec(q, tipoReporte, estado);
        spec = restrictToCurrentOwnerIfNeeded(spec);
        return repository.findAll(spec, pageable).map(mapper::toListItemDto);
    }

    /**
     * Obtiene el detalle completo de una solicitud de reporte.
     *
     * @param solicitudId identificador tecnico de la solicitud
     * @return detalle listo para API
     */
    @Transactional(readOnly = true)
    public ReporteSolicitudDetalleResponseDto obtenerDetalle(Long solicitudId) {
        return mapper.toDetalleDto(getOrThrow(solicitudId));
    }

    /**
     * Obtiene el estado resumido de una solicitud de reporte.
     *
     * @param solicitudId identificador tecnico de la solicitud
     * @return resumen de estado y metadata
     */
    @Transactional(readOnly = true)
    public ReporteSolicitudResultadoResponseDto obtenerEstado(Long solicitudId) {
        return mapper.toResultadoDto(getOrThrow(solicitudId));
    }

    /**
     * Devuelve el resultado solo cuando la solicitud ya fue completada.
     *
     * @param solicitudId identificador tecnico de la solicitud
     * @return resultado consolidado de la solicitud
     */
    @Transactional(readOnly = true)
    public ReporteSolicitudResultadoResponseDto obtenerResultado(Long solicitudId) {
        ReporteSolicitudQueueJpaEntity entity = getOrThrow(solicitudId);
        if (!"COMPLETADA".equalsIgnoreCase(entity.getEstado())) {
            throw new BusinessRuleException(ReporteErrorCodes.RN_REP_02_RESULTADO_NO_LISTO);
        }
        return mapper.toResultadoDto(entity);
    }

    /**
     * Resuelve la metadata de descarga del archivo generado por el worker.
     *
     * @param solicitudId identificador tecnico de la solicitud
     * @return descriptor de descarga para controller/API
     */
    @Transactional(readOnly = true)
    public ReporteArchivoDescarga obtenerArchivo(Long solicitudId) {
        ReporteSolicitudQueueJpaEntity entity = getOrThrow(solicitudId);
        if (!"COMPLETADA".equalsIgnoreCase(entity.getEstado())) {
            throw new BusinessRuleException(
                    ReporteErrorCodes.RN_REP_06_ESTADO_REPORTE_NO_PERMITE_OPERACION,
                    "El archivo del reporte aun no esta disponible."
            );
        }

        Map<String, Object> result = payloadMapper.toMap(entity.getResultadoJson());
        Object archivoRaw = result.get("archivo");
        if (!(archivoRaw instanceof Map<?, ?> archivoMapRaw)) {
            throw new ResourceNotFoundException("No existe metadata de archivo para la solicitud.");
        }

        String rutaRelativa = readString(archivoMapRaw.get("rutaRelativa"));
        String nombreArchivo = readString(archivoMapRaw.get("nombreArchivo"));
        String mimeType = readString(archivoMapRaw.get("mimeType"));

        if (rutaRelativa == null || rutaRelativa.isBlank()) {
            throw new ResourceNotFoundException("No existe ruta de archivo para la solicitud.");
        }

        try {
            DocumentDownloadResource loadedDocument = documentStoragePort.load(rutaRelativa);
            return new ReporteArchivoDescarga(
                    loadedDocument.resource(),
                    sanitizeFileName(nombreArchivo, loadedDocument.resource().getFilename()),
                    normalizeMimeType(mimeType),
                    loadedDocument.sizeBytes()
            );
        } catch (InfrastructureException ex) {
            throw ex;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InfrastructureException(
                    ReporteErrorCodes.SYS_REP_02_FALLO_PROCESAMIENTO,
                    "No fue posible leer el archivo de reporte.",
                    null,
                    ex
            );
        }
    }

    private ReporteSolicitudQueueJpaEntity getOrThrow(Long solicitudId) {
        ReporteSolicitudQueueJpaEntity entity = repository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de reporte no encontrada."));
        if (!canCurrentUserAccess(entity)) {
            throw new ResourceNotFoundException("Solicitud de reporte no encontrada.");
        }
        return entity;
    }

    private static Specification<ReporteSolicitudQueueJpaEntity> buildSpec(String q, String tipoReporte, String estado) {
        String qNorm = normalize(q);
        String tipo = normalizeUpper(tipoReporte);
        String estadoNorm = normalizeUpper(estado);

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (qNorm != null) {
                String like = "%" + qNorm.toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("tipoReporte")), like),
                        cb.like(cb.lower(root.get("estado")), like)
                ));
            }
            if (tipo != null) {
                predicates.add(cb.equal(root.get("tipoReporte"), tipo));
            }
            if (estadoNorm != null) {
                predicates.add(cb.equal(root.get("estado"), estadoNorm));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private Specification<ReporteSolicitudQueueJpaEntity> restrictToCurrentOwnerIfNeeded(
            Specification<ReporteSolicitudQueueJpaEntity> baseSpec
    ) {
        if (currentAuthenticatedUserService.isAdmin()) {
            return baseSpec;
        }
        Long currentUserId = currentAuthenticatedUserService.getCurrentUserIdOrNull();
        if (currentUserId == null) {
            throw new AuthException(
                    AuthErrorCodes.AUTH_04_SIN_PERMISOS,
                    "No fue posible determinar el usuario autenticado para la consulta de reportes."
            );
        }
        return baseSpec.and((root, query, cb) -> cb.equal(root.get("solicitadoPorUsuario"), currentUserId));
    }

    private boolean canCurrentUserAccess(ReporteSolicitudQueueJpaEntity entity) {
        if (currentAuthenticatedUserService.isAdmin()) {
            return true;
        }
        Long currentUserId = currentAuthenticatedUserService.getCurrentUserIdOrNull();
        return currentUserId != null && currentUserId.equals(entity.getSolicitadoPorUsuario());
    }

    private static String normalizeUpper(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
    }

    private static String readString(Object value) {
        return value == null ? null : value.toString();
    }

    private static String sanitizeFileName(String candidate, String fallback) {
        String value = candidate == null || candidate.isBlank() ? fallback : candidate;
        String sanitized = value
                .replace("\\", "_")
                .replace("/", "_")
                .replace("\r", "_")
                .replace("\n", "_");
        return sanitized.isBlank() ? fallback : sanitized;
    }

    private static String normalizeMimeType(String mimeType) {
        if (mimeType == null || mimeType.isBlank()) {
            return "application/octet-stream";
        }
        String normalized = mimeType.trim().toLowerCase(Locale.ROOT);
        return ALLOWED_REPORT_MIME_TYPES.contains(normalized)
                ? normalized
                : "application/octet-stream";
    }
}
