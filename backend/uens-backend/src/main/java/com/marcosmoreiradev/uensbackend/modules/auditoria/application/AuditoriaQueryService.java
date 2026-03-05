package com.marcosmoreiradev.uensbackend.modules.auditoria.application;

import com.marcosmoreiradev.uensbackend.common.pagination.PageableFactory;
import com.marcosmoreiradev.uensbackend.modules.auditoria.api.dto.AuditoriaEventoListItemDto;
import com.marcosmoreiradev.uensbackend.modules.auditoria.application.mapper.AuditoriaDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.auditoria.infrastructure.persistence.entity.AuditoriaEventoJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.auditoria.infrastructure.persistence.repository.AuditoriaEventoJpaRepository;
import com.marcosmoreiradev.uensbackend.security.user.CurrentAuthenticatedUserService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class AuditoriaQueryService {

    private static final Set<String> SORT_WHITELIST = Set.of(
            "id",
            "modulo",
            "accion",
            "resultado",
            "actorLogin",
            "fechaEvento"
    );

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("fechaEvento"));

    private final AuditoriaEventoJpaRepository repository;
    private final AuditoriaDtoMapper mapper;
    private final PageableFactory pageableFactory;
    private final CurrentAuthenticatedUserService currentAuthenticatedUserService;

    public AuditoriaQueryService(
            AuditoriaEventoJpaRepository repository,
            AuditoriaDtoMapper mapper,
            PageableFactory pageableFactory,
            CurrentAuthenticatedUserService currentAuthenticatedUserService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.pageableFactory = pageableFactory;
        this.currentAuthenticatedUserService = currentAuthenticatedUserService;
    }

    @Transactional(readOnly = true)
    public Page<AuditoriaEventoListItemDto> listar(
            String q,
            String modulo,
            String accion,
            String resultado,
            String actorLogin,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Integer page,
            Integer size,
            List<String> sort
    ) {
        currentAuthenticatedUserService.ensureAdmin("Solo ADMIN puede consultar eventos de auditoria.");
        Pageable pageable = pageableFactory.from(page, size, sort, DEFAULT_SORT, SORT_WHITELIST);
        Specification<AuditoriaEventoJpaEntity> spec = buildSpec(
                q,
                modulo,
                accion,
                resultado,
                actorLogin,
                fechaDesde,
                fechaHasta
        );
        return repository.findAll(spec, pageable).map(mapper::toListItemDto);
    }

    private static Specification<AuditoriaEventoJpaEntity> buildSpec(
            String q,
            String modulo,
            String accion,
            String resultado,
            String actorLogin,
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {
        String qNorm = normalize(q);
        String moduloNorm = normalizeUpper(modulo);
        String accionNorm = normalizeUpper(accion);
        String resultadoNorm = normalizeUpper(resultado);
        String actorLoginNorm = normalize(actorLogin);

        LocalDateTime fromDateTime = fechaDesde == null ? null : fechaDesde.atStartOfDay();
        LocalDateTime toDateTime = fechaHasta == null ? null : fechaHasta.plusDays(1L).atStartOfDay();

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (qNorm != null) {
                String like = "%" + qNorm.toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("modulo")), like),
                        cb.like(cb.lower(root.get("accion")), like),
                        cb.like(cb.lower(root.get("detalle")), like),
                        cb.like(cb.lower(root.get("actorLogin")), like)
                ));
            }
            if (moduloNorm != null) {
                predicates.add(cb.equal(root.get("modulo"), moduloNorm));
            }
            if (accionNorm != null) {
                predicates.add(cb.equal(root.get("accion"), accionNorm));
            }
            if (resultadoNorm != null) {
                predicates.add(cb.equal(root.get("resultado"), resultadoNorm));
            }
            if (actorLoginNorm != null) {
                predicates.add(cb.equal(cb.lower(root.get("actorLogin")), actorLoginNorm.toLowerCase(Locale.ROOT)));
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
