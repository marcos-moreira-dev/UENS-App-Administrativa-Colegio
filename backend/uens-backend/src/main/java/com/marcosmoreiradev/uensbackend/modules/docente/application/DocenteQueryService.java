package com.marcosmoreiradev.uensbackend.modules.docente.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.pagination.PageableFactory;
import com.marcosmoreiradev.uensbackend.modules.docente.api.dto.DocenteListItemDto;
import com.marcosmoreiradev.uensbackend.modules.docente.api.dto.DocenteResponseDto;
import com.marcosmoreiradev.uensbackend.modules.docente.application.mapper.DocenteDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.docente.infrastructure.persistence.entity.DocenteJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.docente.infrastructure.persistence.repository.DocenteJpaRepository;
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
 * Centraliza las lecturas del modulo docente con el mismo esquema de filtros y
 * paginacion compartido por el resto del backend administrativo.
 */
@Service
public class DocenteQueryService {

    private static final Set<String> SORT_WHITELIST = Set.of("id", "nombres", "apellidos", "estado");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc("apellidos"), Sort.Order.asc("nombres"));

    private final DocenteJpaRepository repository;
    private final DocenteDtoMapper mapper;
    private final PageableFactory pageableFactory;

    public DocenteQueryService(
            DocenteJpaRepository repository,
            DocenteDtoMapper mapper,
            PageableFactory pageableFactory
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.pageableFactory = pageableFactory;
    }

    /**
     * Obtiene un docente por id.
     *
     * @param id identificador tecnico del docente
     * @return DTO de detalle para API
     */
    @Transactional(readOnly = true)
    public DocenteResponseDto obtenerPorId(Long id) {
        DocenteJpaEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado."));
        return mapper.toResponseDto(entity);
    }

    /**
     * Lista docentes por texto libre y estado.
     *
     * @param q texto libre para coincidencias parciales
     * @param estado estado operativo del docente
     * @param page pagina base cero
     * @param size tamano solicitado
     * @param sort criterios de ordenamiento permitidos
     * @return pagina de docentes resumidos
     */
    @Transactional(readOnly = true)
    public Page<DocenteListItemDto> listar(String q, String estado, Integer page, Integer size, List<String> sort) {
        Pageable pageable = pageableFactory.from(page, size, sort, DEFAULT_SORT, SORT_WHITELIST);
        Specification<DocenteJpaEntity> spec = buildSpecification(q, estado);
        return repository.findAll(spec, pageable).map(mapper::toListItemDto);
    }

    private static Specification<DocenteJpaEntity> buildSpecification(String q, String estado) {
        String qNorm = normalize(q);
        String estadoNorm = normalizeUpper(estado);

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (estadoNorm != null) {
                predicates.add(cb.equal(root.get("estado"), estadoNorm));
            }
            if (qNorm != null) {
                String like = "%" + qNorm.toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nombres")), like),
                        cb.like(cb.lower(root.get("apellidos")), like),
                        cb.like(cb.coalesce(cb.lower(root.get("correoElectronico")), ""), like),
                        cb.like(cb.coalesce(cb.lower(root.get("telefono")), ""), like)
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
