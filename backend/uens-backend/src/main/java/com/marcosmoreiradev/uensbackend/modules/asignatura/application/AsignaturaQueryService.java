package com.marcosmoreiradev.uensbackend.modules.asignatura.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.pagination.PageableFactory;
import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaListItemDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaResponseDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.application.mapper.AsignaturaDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.asignatura.infrastructure.persistence.entity.AsignaturaJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.asignatura.infrastructure.persistence.repository.AsignaturaJpaRepository;
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
import java.util.Set;

/**
 * Resuelve consultas de asignaturas con filtros, paginacion y ordenamiento
 * compatibles con el contrato consumido por el frontend.
 */
@Service
public class AsignaturaQueryService {

    private static final Set<String> SORT_WHITELIST = Set.of("id", "nombre", "grado", "area", "estado");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc("grado"), Sort.Order.asc("nombre"));

    private final AsignaturaJpaRepository asignaturaJpaRepository;
    private final AsignaturaDtoMapper asignaturaDtoMapper;
    private final PageableFactory pageableFactory;

    public AsignaturaQueryService(
            AsignaturaJpaRepository asignaturaJpaRepository,
            AsignaturaDtoMapper asignaturaDtoMapper,
            PageableFactory pageableFactory
    ) {
        this.asignaturaJpaRepository = asignaturaJpaRepository;
        this.asignaturaDtoMapper = asignaturaDtoMapper;
        this.pageableFactory = pageableFactory;
    }

    /**
     * Obtiene una asignatura individual por su identificador tecnico.
     *
     * @param asignaturaId identificador de la asignatura en persistencia
     * @return DTO de detalle listo para API
     */
    @Transactional(readOnly = true)
    public AsignaturaResponseDto obtenerPorId(Long asignaturaId) {
        AsignaturaJpaEntity entity = asignaturaJpaRepository.findById(asignaturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura no encontrada."));
        return asignaturaDtoMapper.toResponseDto(entity);
    }

    /**
     * Lista asignaturas usando filtros funcionales y utilidades comunes de
     * paginacion para mantener trazabilidad uniforme de errores.
     *
     * @param q texto libre de busqueda
     * @param estado estado operativo de la asignatura
     * @param grado grado academico asociado
     * @param area area academica de la asignatura
     * @param page pagina base cero
     * @param size tamano solicitado
     * @param sort criterios de ordenamiento permitidos
     * @return pagina de resultados resumidos
     */
    @Transactional(readOnly = true)
    public Page<AsignaturaListItemDto> listar(
            String q,
            String estado,
            Integer grado,
            String area,
            Integer page,
            Integer size,
            List<String> sort
    ) {
        Pageable pageable = pageableFactory.from(page, size, sort, DEFAULT_SORT, SORT_WHITELIST);
        Specification<AsignaturaJpaEntity> spec = buildSpecification(q, estado, grado, area);
        return asignaturaJpaRepository.findAll(spec, pageable).map(asignaturaDtoMapper::toListItemDto);
    }

    private static Specification<AsignaturaJpaEntity> buildSpecification(
            String q,
            String estado,
            Integer grado,
            String area
    ) {
        String qNorm = normalize(q);
        String estadoNorm = normalizeUpper(estado);
        String areaNorm = normalize(area);

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (estadoNorm != null) {
                predicates.add(cb.equal(root.get("estado"), estadoNorm));
            }
            if (grado != null) {
                predicates.add(cb.equal(root.get("grado"), grado));
            }
            if (areaNorm != null) {
                predicates.add(cb.like(cb.lower(root.get("area")), "%" + areaNorm.toLowerCase(Locale.ROOT) + "%"));
            }
            if (qNorm != null) {
                String like = "%" + qNorm.toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nombre")), like),
                        cb.like(cb.coalesce(cb.lower(root.get("descripcion")), ""), like),
                        cb.like(cb.coalesce(cb.lower(root.get("area")), ""), like)
                ));
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

    private static String normalizeUpper(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
    }
}
