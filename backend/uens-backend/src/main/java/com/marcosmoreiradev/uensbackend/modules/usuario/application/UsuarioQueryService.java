package com.marcosmoreiradev.uensbackend.modules.usuario.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.pagination.PageableFactory;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoListItemDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoResponseDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.application.mapper.UsuarioSistemaAdministrativoDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.usuario.application.validator.UsuarioSistemaAdministrativoRequestValidator;
import com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.entity.UsuarioSistemaAdministrativoJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.repository.UsuarioSistemaAdministrativoJpaRepository;
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
 * Lecturas del modulo de usuarios administrativos.
 */
@Service
public class UsuarioQueryService {

    private static final Set<String> SORT_WHITELIST = Set.of("id", "nombreLogin", "rol", "estado");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc("nombreLogin"));

    private final UsuarioSistemaAdministrativoJpaRepository repository;
    private final UsuarioSistemaAdministrativoDtoMapper mapper;
    private final UsuarioSistemaAdministrativoRequestValidator validator;
    private final PageableFactory pageableFactory;

    public UsuarioQueryService(
            UsuarioSistemaAdministrativoJpaRepository repository,
            UsuarioSistemaAdministrativoDtoMapper mapper,
            UsuarioSistemaAdministrativoRequestValidator validator,
            PageableFactory pageableFactory
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
        this.pageableFactory = pageableFactory;
    }

    @Transactional(readOnly = true)
    public UsuarioSistemaAdministrativoResponseDto obtenerPorId(Long usuarioId) {
        UsuarioSistemaAdministrativoJpaEntity entity = repository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario del sistema no encontrado."));
        return mapper.toResponseDto(entity);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioSistemaAdministrativoListItemDto> listar(
            String q,
            String estado,
            String rol,
            Integer page,
            Integer size,
            List<String> sort
    ) {
        if (estado != null && !estado.isBlank()) {
            validator.validarEstado(estado);
        }
        if (rol != null && !rol.isBlank()) {
            validator.validarRol(rol);
        }

        Pageable pageable = pageableFactory.from(page, size, sort, DEFAULT_SORT, SORT_WHITELIST);
        Specification<UsuarioSistemaAdministrativoJpaEntity> spec = buildSpecification(q, estado, rol);
        return repository.findAll(spec, pageable).map(mapper::toListItemDto);
    }

    private static Specification<UsuarioSistemaAdministrativoJpaEntity> buildSpecification(String q, String estado, String rol) {
        String qNorm = normalize(q);
        String estadoNorm = normalizeUpper(estado);
        String rolNorm = normalizeUpper(rol);

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (estadoNorm != null) {
                predicates.add(cb.equal(root.get("estado"), estadoNorm));
            }
            if (rolNorm != null) {
                predicates.add(cb.equal(root.get("rol"), rolNorm));
            }
            if (qNorm != null) {
                String like = "%" + qNorm.toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nombreLogin")), like),
                        cb.like(cb.lower(root.get("rol")), like),
                        cb.like(cb.lower(root.get("estado")), like)
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
