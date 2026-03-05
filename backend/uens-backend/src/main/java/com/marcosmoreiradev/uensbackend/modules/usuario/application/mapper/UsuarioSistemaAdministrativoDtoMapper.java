package com.marcosmoreiradev.uensbackend.modules.usuario.application.mapper;

import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoListItemDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.api.dto.UsuarioSistemaAdministrativoResponseDto;
import com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.entity.UsuarioSistemaAdministrativoJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper entre entidad de usuario administrativo y contratos expuestos por la
 * API.
 */
@Component
public class UsuarioSistemaAdministrativoDtoMapper {

    public UsuarioSistemaAdministrativoResponseDto toResponseDto(UsuarioSistemaAdministrativoJpaEntity entity) {
        return new UsuarioSistemaAdministrativoResponseDto(
                entity.getId(),
                entity.getNombreLogin(),
                entity.getRol(),
                entity.getEstado()
        );
    }

    public UsuarioSistemaAdministrativoListItemDto toListItemDto(UsuarioSistemaAdministrativoJpaEntity entity) {
        return new UsuarioSistemaAdministrativoListItemDto(
                entity.getId(),
                entity.getNombreLogin(),
                entity.getRol(),
                entity.getEstado()
        );
    }
}
