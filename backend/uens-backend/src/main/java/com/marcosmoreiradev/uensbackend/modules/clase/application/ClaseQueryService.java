package com.marcosmoreiradev.uensbackend.modules.clase.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.pagination.PageableFactory;
import com.marcosmoreiradev.uensbackend.modules.clase.api.dto.ClaseListItemDto;
import com.marcosmoreiradev.uensbackend.modules.clase.api.dto.ClaseResponseDto;
import com.marcosmoreiradev.uensbackend.modules.clase.application.mapper.ClaseDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.clase.infrastructure.persistence.entity.ClaseJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.clase.infrastructure.persistence.repository.ClaseJpaRepository;
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
 * Define la responsabilidad de ClaseQueryService dentro del backend UENS.
 * Contexto: modulo clase, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: resolver consultas con filtros, paginacion y ordenamiento consistente para clientes administrativos.
 */
public class ClaseQueryService {

    private static final Set<String> SORT_WHITELIST = Set.of(
            "id",
            "diaSemana",
            "horaInicio",
            "horaFin",
            "estado"
    );
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc("diaSemana"), Sort.Order.asc("horaInicio"));

    private final ClaseJpaRepository repository;
    private final ClaseDtoMapper mapper;
    private final PageableFactory pageableFactory;
/**
 * Construye la instancia de ClaseQueryService para operar en el modulo clase.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param repository adaptador de persistencia que opera sobre tablas del esquema V2 3FN
     * @param mapper componente de mapeo entre DTOs, modelos de dominio y entidades
 */

    public ClaseQueryService(ClaseJpaRepository repository, ClaseDtoMapper mapper, PageableFactory pageableFactory) {
        this.repository = repository;
        this.mapper = mapper;
        this.pageableFactory = pageableFactory;
    }

    @Transactional(readOnly = true)
/**
 * Implementa la operacion 'obtenerPorId' del modulo clase en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ClaseResponseDto obtenerPorId(Long id) {
        ClaseJpaEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada."));
        return mapper.toResponseDto(entity);
    }
/**
 * Implementa la operacion 'listar' del modulo clase en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
     * @param seccionId identificador de la seccion academica (grado/paralelo/anio lectivo)
     * @param asignaturaId identificador de la asignatura dentro del catalogo academico
     * @param docenteId identificador del docente dentro del dominio academico
     * @param diaSemana dato de entrada relevante para ejecutar esta operacion: 'diaSemana'
     * @param page indice de pagina solicitado (base cero) para paginacion de resultados
     * @param size tamano de pagina solicitado respetando limites del contrato API
     * @param sort criterios de ordenamiento solicitados por cliente segun whitelist permitida
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */

    @Transactional(readOnly = true)
    public Page<ClaseListItemDto> listar(
            String estado,
            Long seccionId,
            Long asignaturaId,
            Long docenteId,
            String diaSemana,
            Integer page,
            Integer size,
            List<String> sort
    ) {
        Pageable pageable = pageableFactory.from(page, size, sort, DEFAULT_SORT, SORT_WHITELIST);
        Specification<ClaseJpaEntity> spec = buildSpecification(estado, seccionId, asignaturaId, docenteId, diaSemana);
        return repository.findAll(spec, pageable)
                .map(mapper::toListItemDto);
    }
/**
 * Metodo de soporte interno 'buildSpecification' para mantener cohesion en ClaseQueryService.
 * Contexto: modulo clase, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
     * @param seccionId identificador de la seccion academica (grado/paralelo/anio lectivo)
     * @param asignaturaId identificador de la asignatura dentro del catalogo academico
     * @param docenteId identificador del docente dentro del dominio academico
     * @param diaSemana dato de entrada relevante para ejecutar esta operacion: 'diaSemana'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */

    private static Specification<ClaseJpaEntity> buildSpecification(
            String estado,
            Long seccionId,
            Long asignaturaId,
            Long docenteId,
            String diaSemana
    ) {
        String estadoNorm = normalizeUpper(estado);
        String diaNorm = normalizeUpper(diaSemana);

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (estadoNorm != null) {
                predicates.add(cb.equal(root.get("estado"), estadoNorm));
            }
            if (seccionId != null) {
                predicates.add(cb.equal(root.get("seccion").get("id"), seccionId));
            }
            if (asignaturaId != null) {
                predicates.add(cb.equal(root.get("asignatura").get("id"), asignaturaId));
            }
            if (docenteId != null) {
                predicates.add(cb.equal(root.get("docente").get("id"), docenteId));
            }
            if (diaNorm != null) {
                predicates.add(cb.equal(root.get("diaSemana"), diaNorm));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

/**
 * Metodo de soporte interno 'normalizeUpper' para mantener cohesion en ClaseQueryService.
 * Contexto: modulo clase, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalizeUpper(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized.toUpperCase(Locale.ROOT);
    }
}
