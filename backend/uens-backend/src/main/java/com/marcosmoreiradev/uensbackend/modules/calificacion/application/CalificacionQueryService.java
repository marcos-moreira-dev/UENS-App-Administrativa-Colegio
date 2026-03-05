package com.marcosmoreiradev.uensbackend.modules.calificacion.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.pagination.PageableFactory;
import com.marcosmoreiradev.uensbackend.modules.calificacion.api.dto.CalificacionListItemDto;
import com.marcosmoreiradev.uensbackend.modules.calificacion.api.dto.CalificacionResponseDto;
import com.marcosmoreiradev.uensbackend.modules.calificacion.application.mapper.CalificacionDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.calificacion.infrastructure.persistence.entity.CalificacionJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.calificacion.infrastructure.persistence.repository.CalificacionJpaRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Agrupa consultas de calificaciones usando la infraestructura comun de
 * paginacion para que la capa application no dependa de detalles HTTP.
 */
@Service
public class CalificacionQueryService {

    private static final Set<String> SORT_WHITELIST = Set.of("id", "numeroParcial", "nota", "fechaRegistro");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("fechaRegistro"), Sort.Order.asc("id"));

    private final CalificacionJpaRepository repository;
    private final CalificacionDtoMapper mapper;
    private final PageableFactory pageableFactory;

    public CalificacionQueryService(
            CalificacionJpaRepository repository,
            CalificacionDtoMapper mapper,
            PageableFactory pageableFactory
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.pageableFactory = pageableFactory;
    }

    /**
     * Obtiene una calificacion por su identificador tecnico.
     *
     * @param id identificador de la calificacion
     * @return DTO de detalle listo para API
     */
    @Transactional(readOnly = true)
    public CalificacionResponseDto obtenerPorId(Long id) {
        CalificacionJpaEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificacion no encontrada."));
        return mapper.toResponseDto(entity);
    }

    /**
     * Lista calificaciones usando filtros del contexto academico.
     *
     * @param estudianteId estudiante asociado
     * @param claseId clase asociada
     * @param numeroParcial parcial academico
     * @param page pagina base cero
     * @param size tamano solicitado
     * @param sort criterios de ordenamiento permitidos
     * @return pagina de calificaciones resumidas
     */
    @Transactional(readOnly = true)
    public Page<CalificacionListItemDto> listar(
            Long estudianteId,
            Long claseId,
            Integer numeroParcial,
            Integer page,
            Integer size,
            List<String> sort
    ) {
        Pageable pageable = pageableFactory.from(page, size, sort, DEFAULT_SORT, SORT_WHITELIST);
        Specification<CalificacionJpaEntity> spec = buildSpecification(estudianteId, claseId, numeroParcial);
        return repository.findAll(spec, pageable).map(mapper::toListItemDto);
    }

    private static Specification<CalificacionJpaEntity> buildSpecification(Long estudianteId, Long claseId, Integer numeroParcial) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (estudianteId != null) {
                predicates.add(cb.equal(root.get("estudiante").get("id"), estudianteId));
            }
            if (claseId != null) {
                predicates.add(cb.equal(root.get("clase").get("id"), claseId));
            }
            if (numeroParcial != null) {
                predicates.add(cb.equal(root.get("numeroParcial"), numeroParcial));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
