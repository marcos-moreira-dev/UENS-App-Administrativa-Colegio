package com.marcosmoreiradev.uensbackend.modules.estudiante.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.pagination.PageableFactory;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteListItemDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteResponseDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.application.mapper.EstudianteDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.estudiante.application.validator.EstudianteFilterValidator;
import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.entity.EstudianteJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.repository.EstudianteJpaRepository;
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

@Service
/**
 * Define la responsabilidad de EstudianteQueryService dentro del backend UENS.
 * Contexto: modulo estudiante, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: resolver consultas con filtros, paginacion y ordenamiento consistente para clientes administrativos.
 */
public class EstudianteQueryService {

    private static final Set<String> SORT_WHITELIST = Set.of(
            "id",
            "nombres",
            "apellidos",
            "fechaNacimiento",
            "estado"
    );
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc("apellidos"), Sort.Order.asc("nombres"));

    private final EstudianteJpaRepository repository;
    private final EstudianteDtoMapper mapper;
    private final EstudianteFilterValidator filterValidator;
    private final PageableFactory pageableFactory;
/**
 * Construye la instancia de EstudianteQueryService para operar en el modulo estudiante.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param repository adaptador de persistencia que opera sobre tablas del esquema V2 3FN
     * @param mapper componente de mapeo entre DTOs, modelos de dominio y entidades
     * @param filterValidator validador de filtros de consulta para estado y criterios permitidos en listados de estudiantes
 */

    public EstudianteQueryService(
            EstudianteJpaRepository repository,
            EstudianteDtoMapper mapper,
            EstudianteFilterValidator filterValidator,
            PageableFactory pageableFactory
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.filterValidator = filterValidator;
        this.pageableFactory = pageableFactory;
    }

    @Transactional(readOnly = true)
/**
 * Implementa la operacion 'obtenerPorId' del modulo estudiante en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public EstudianteResponseDto obtenerPorId(Long id) {
        EstudianteJpaEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado."));
        return mapper.toResponseDto(entity);
    }
/**
 * Implementa la operacion 'listar' del modulo estudiante en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param q filtro de busqueda textual para consultas por coincidencia parcial
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
     * @param seccionId identificador de la seccion academica (grado/paralelo/anio lectivo)
     * @param representanteLegalId identificador del representante legal principal del estudiante
     * @param page indice de pagina solicitado (base cero) para paginacion de resultados
     * @param size tamano de pagina solicitado respetando limites del contrato API
     * @param sort criterios de ordenamiento solicitados por cliente segun whitelist permitida
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */

    @Transactional(readOnly = true)
    public Page<EstudianteListItemDto> listar(
            String q,
            String estado,
            Long seccionId,
            Long representanteLegalId,
            Integer page,
            Integer size,
            List<String> sort
    ) {
        filterValidator.validarEstado(estado);

        Pageable pageable = pageableFactory.from(page, size, sort, DEFAULT_SORT, SORT_WHITELIST);
        Specification<EstudianteJpaEntity> spec = buildSpecification(q, estado, seccionId, representanteLegalId);
        return repository.findAll(spec, pageable)
                .map(mapper::toListItemDto);
    }
/**
 * Metodo de soporte interno 'buildSpecification' para mantener cohesion en EstudianteQueryService.
 * Contexto: modulo estudiante, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param q filtro de busqueda textual para consultas por coincidencia parcial
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
     * @param seccionId identificador de la seccion academica (grado/paralelo/anio lectivo)
     * @param representanteLegalId identificador del representante legal principal del estudiante
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */

    private static Specification<EstudianteJpaEntity> buildSpecification(
            String q,
            String estado,
            Long seccionId,
            Long representanteLegalId
    ) {
        String qNorm = normalize(q);
        String estadoNorm = normalizeUpper(estado);

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (estadoNorm != null) {
                predicates.add(cb.equal(root.get("estado"), estadoNorm));
            }
            if (seccionId != null) {
                predicates.add(cb.equal(root.get("seccion").get("id"), seccionId));
            }
            if (representanteLegalId != null) {
                predicates.add(cb.equal(root.get("representanteLegal").get("id"), representanteLegalId));
            }
            if (qNorm != null) {
                String like = "%" + qNorm.toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nombres")), like),
                        cb.like(cb.lower(root.get("apellidos")), like)
                ));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

/**
 * Metodo de soporte interno 'normalize' para mantener cohesion en EstudianteQueryService.
 * Contexto: modulo estudiante, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

/**
 * Metodo de soporte interno 'normalizeUpper' para mantener cohesion en EstudianteQueryService.
 * Contexto: modulo estudiante, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalizeUpper(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
    }
}
