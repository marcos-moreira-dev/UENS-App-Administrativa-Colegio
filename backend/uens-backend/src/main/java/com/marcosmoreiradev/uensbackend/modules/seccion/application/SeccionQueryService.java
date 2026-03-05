package com.marcosmoreiradev.uensbackend.modules.seccion.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.pagination.PageableFactory;
import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionListItemDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionResponseDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.application.mapper.SeccionDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.seccion.application.validator.SeccionFilterValidator;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.entity.SeccionJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.repository.SeccionJpaRepository;
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
 * Define la responsabilidad de SeccionQueryService dentro del backend UENS.
 * Contexto: modulo seccion, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: resolver consultas con filtros, paginacion y ordenamiento consistente para clientes administrativos.
 */
public class SeccionQueryService {

    private static final Set<String> SORT_WHITELIST = Set.of(
            "id",
            "anioLectivo",
            "grado",
            "paralelo",
            "estado"
    );
    private static final Sort DEFAULT_SORT = Sort.by(
            Sort.Order.desc("anioLectivo"),
            Sort.Order.asc("grado"),
            Sort.Order.asc("paralelo")
    );

    private final SeccionJpaRepository repository;
    private final SeccionDtoMapper mapper;
    private final SeccionFilterValidator filterValidator;
    private final PageableFactory pageableFactory;
/**
 * Construye la instancia de SeccionQueryService para operar en el modulo seccion.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param repository adaptador de persistencia que opera sobre tablas del esquema V2 3FN
     * @param mapper componente de mapeo entre DTOs, modelos de dominio y entidades
     * @param filterValidator dato de entrada relevante para ejecutar esta operacion: 'filterValidator'
 */

    public SeccionQueryService(
            SeccionJpaRepository repository,
            SeccionDtoMapper mapper,
            SeccionFilterValidator filterValidator,
            PageableFactory pageableFactory
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.filterValidator = filterValidator;
        this.pageableFactory = pageableFactory;
    }

    @Transactional(readOnly = true)
/**
 * Implementa la operacion 'obtenerPorId' del modulo seccion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public SeccionResponseDto obtenerPorId(Long id) {
        SeccionJpaEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seccion no encontrada."));
        return mapper.toResponseDto(entity);
    }
/**
 * Implementa la operacion 'listar' del modulo seccion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param q filtro de busqueda textual para consultas por coincidencia parcial
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
     * @param grado grado academico de Educacion General Basica para filtrar/validar datos
     * @param paralelo dato de entrada relevante para ejecutar esta operacion: 'paralelo'
     * @param anioLectivo dato de entrada relevante para ejecutar esta operacion: 'anioLectivo'
     * @param page indice de pagina solicitado (base cero) para paginacion de resultados
     * @param size tamano de pagina solicitado respetando limites del contrato API
     * @param sort criterios de ordenamiento solicitados por cliente segun whitelist permitida
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */

    @Transactional(readOnly = true)
    public Page<SeccionListItemDto> listar(
            String q,
            String estado,
            Integer grado,
            String paralelo,
            String anioLectivo,
            Integer page,
            Integer size,
            List<String> sort
    ) {
        filterValidator.validarEstado(estado);

        Pageable pageable = pageableFactory.from(page, size, sort, DEFAULT_SORT, SORT_WHITELIST);
        Specification<SeccionJpaEntity> spec = buildSpecification(q, estado, grado, paralelo, anioLectivo);
        return repository.findAll(spec, pageable)
                .map(mapper::toListItemDto);
    }
/**
 * Metodo de soporte interno 'buildSpecification' para mantener cohesion en SeccionQueryService.
 * Contexto: modulo seccion, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param q filtro de busqueda textual para consultas por coincidencia parcial
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
     * @param grado grado academico de Educacion General Basica para filtrar/validar datos
     * @param paralelo dato de entrada relevante para ejecutar esta operacion: 'paralelo'
     * @param anioLectivo dato de entrada relevante para ejecutar esta operacion: 'anioLectivo'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */

    private static Specification<SeccionJpaEntity> buildSpecification(
            String q,
            String estado,
            Integer grado,
            String paralelo,
            String anioLectivo
    ) {
        String qNorm = normalize(q);
        String estadoNorm = normalizeUpper(estado);
        String paraleloNorm = normalize(paralelo);
        String anioLectivoNorm = normalize(anioLectivo);

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (estadoNorm != null) {
                predicates.add(cb.equal(root.get("estado"), estadoNorm));
            }
            if (grado != null) {
                predicates.add(cb.equal(root.get("grado"), grado));
            }
            if (paraleloNorm != null) {
                predicates.add(cb.like(cb.lower(root.get("paralelo")), "%" + paraleloNorm.toLowerCase(Locale.ROOT) + "%"));
            }
            if (anioLectivoNorm != null) {
                predicates.add(cb.equal(root.get("anioLectivo"), anioLectivoNorm));
            }

            if (qNorm != null) {
                String like = "%" + qNorm.toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("paralelo")), like),
                        cb.like(cb.lower(root.get("anioLectivo")), like)
                ));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

/**
 * Metodo de soporte interno 'normalize' para mantener cohesion en SeccionQueryService.
 * Contexto: modulo seccion, capa application, con foco en reglas y consistencia tecnica de fase 1.
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
 * Metodo de soporte interno 'normalizeUpper' para mantener cohesion en SeccionQueryService.
 * Contexto: modulo seccion, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalizeUpper(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
    }
}
