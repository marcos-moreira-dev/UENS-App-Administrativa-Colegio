package com.marcosmoreiradev.uensbackend.modules.representante.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.pagination.PageableFactory;
import com.marcosmoreiradev.uensbackend.modules.representante.api.dto.RepresentanteLegalListItemDto;
import com.marcosmoreiradev.uensbackend.modules.representante.api.dto.RepresentanteLegalResponseDto;
import com.marcosmoreiradev.uensbackend.modules.representante.application.mapper.RepresentanteLegalDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.representante.infrastructure.persistence.entity.RepresentanteLegalJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.representante.infrastructure.persistence.repository.RepresentanteLegalJpaRepository;
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
 * Expone consultas paginadas de representantes legales sin duplicar logica de
 * ordenamiento ni validacion de paginacion.
 */
@Service
public class RepresentanteLegalQueryService {

    private static final Set<String> SORT_WHITELIST = Set.of("id", "nombres", "apellidos", "telefono", "correoElectronico");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc("apellidos"), Sort.Order.asc("nombres"));

    private final RepresentanteLegalJpaRepository repository;
    private final RepresentanteLegalDtoMapper mapper;
    private final PageableFactory pageableFactory;

    public RepresentanteLegalQueryService(
            RepresentanteLegalJpaRepository repository,
            RepresentanteLegalDtoMapper mapper,
            PageableFactory pageableFactory
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.pageableFactory = pageableFactory;
    }

    /**
     * Obtiene un representante legal por id.
     *
     * @param id identificador tecnico del representante
     * @return DTO de detalle para API
     */
    @Transactional(readOnly = true)
    public RepresentanteLegalResponseDto obtenerPorId(Long id) {
        RepresentanteLegalJpaEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Representante legal no encontrado."));
        return mapper.toResponseDto(entity);
    }

    /**
     * Lista representantes legales por texto libre.
     *
     * @param q texto libre de busqueda
     * @param page pagina base cero
     * @param size tamano solicitado
     * @param sort criterios de ordenamiento permitidos
     * @return pagina de representantes resumidos
     */
    @Transactional(readOnly = true)
    public Page<RepresentanteLegalListItemDto> listar(String q, Integer page, Integer size, List<String> sort) {
        Pageable pageable = pageableFactory.from(page, size, sort, DEFAULT_SORT, SORT_WHITELIST);
        Specification<RepresentanteLegalJpaEntity> spec = buildSpec(q);
        return repository.findAll(spec, pageable).map(mapper::toListItemDto);
    }

    private static Specification<RepresentanteLegalJpaEntity> buildSpec(String q) {
        String qNorm = normalize(q);
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
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
}
