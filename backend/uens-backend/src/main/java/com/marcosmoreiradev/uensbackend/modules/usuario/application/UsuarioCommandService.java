package com.marcosmoreiradev.uensbackend.modules.usuario.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.BusinessRuleException;
import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.UsuarioErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoPatchEstadoRequestDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoResponseDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.application.mapper.UsuarioSistemaAdministrativoDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.usuario.application.port.UsuarioPasswordService;
import com.marcosmoreiradev.uensbackend.modules.usuario.application.validator.UsuarioSistemaAdministrativoRequestValidator;
import com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.entity.UsuarioSistemaAdministrativoJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.repository.UsuarioSistemaAdministrativoJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Comandos del modulo de usuarios administrativos.
 */
@Service
public class UsuarioCommandService {

    private final UsuarioSistemaAdministrativoJpaRepository repository;
    private final UsuarioSistemaAdministrativoDtoMapper mapper;
    private final UsuarioSistemaAdministrativoRequestValidator validator;
    private final UsuarioPasswordService usuarioPasswordService;

    public UsuarioCommandService(
            UsuarioSistemaAdministrativoJpaRepository repository,
            UsuarioSistemaAdministrativoDtoMapper mapper,
            UsuarioSistemaAdministrativoRequestValidator validator,
            UsuarioPasswordService usuarioPasswordService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
        this.usuarioPasswordService = usuarioPasswordService;
    }

    @Transactional
    public UsuarioSistemaAdministrativoResponseDto crear(UsuarioSistemaAdministrativoCreateRequestDto request) {
        String login = normalize(request.nombreLogin());
        String rol = normalizeUpper(request.rol());
        String estado = normalizeUpper(request.estado());

        validator.validarRol(rol);
        validator.validarEstado(estado);

        if (repository.existsByNombreLoginIgnoreCase(login)) {
            throw new BusinessRuleException(UsuarioErrorCodes.RN_USR_01_LOGIN_DUPLICADO);
        }

        UsuarioSistemaAdministrativoJpaEntity entity = UsuarioSistemaAdministrativoJpaEntity.crear(
                login,
                usuarioPasswordService.hash(request.password()),
                rol,
                estado
        );

        if ("INACTIVO".equals(estado)) {
            entity.inactivar();
        }

        return mapper.toResponseDto(repository.save(entity));
    }

    @Transactional
    public UsuarioSistemaAdministrativoResponseDto actualizar(Long usuarioId, UsuarioSistemaAdministrativoUpdateRequestDto request) {
        UsuarioSistemaAdministrativoJpaEntity entity = getOrThrow(usuarioId);
        String login = normalize(request.nombreLogin());
        String rol = normalizeUpper(request.rol());
        String estado = normalizeUpper(request.estado());

        validator.validarRol(rol);
        validator.validarEstado(estado);
        validator.validarPasswordOpcional(request.password());

        if (repository.existsByNombreLoginIgnoreCaseAndIdNot(login, usuarioId)) {
            throw new BusinessRuleException(
                    UsuarioErrorCodes.RN_USR_01_LOGIN_DUPLICADO,
                    "Ya existe otro usuario del sistema con el mismo login."
            );
        }

        entity.actualizarIdentidad(login, rol);
        applyEstado(entity, estado);

        String password = normalize(request.password());
        if (password != null) {
            entity.actualizarCredencial(usuarioPasswordService.hash(password));
        }

        return mapper.toResponseDto(repository.save(entity));
    }

    @Transactional
    public UsuarioSistemaAdministrativoResponseDto cambiarEstado(
            Long usuarioId,
            UsuarioSistemaAdministrativoPatchEstadoRequestDto request
    ) {
        UsuarioSistemaAdministrativoJpaEntity entity = getOrThrow(usuarioId);
        String estado = normalizeUpper(request.estado());
        validator.validarEstado(estado);
        applyEstado(entity, estado);
        return mapper.toResponseDto(repository.save(entity));
    }

    private UsuarioSistemaAdministrativoJpaEntity getOrThrow(Long usuarioId) {
        return repository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario del sistema no encontrado."));
    }

    private static void applyEstado(UsuarioSistemaAdministrativoJpaEntity entity, String estado) {
        if ("INACTIVO".equals(estado)) {
            entity.inactivar();
            return;
        }
        entity.activar();
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
