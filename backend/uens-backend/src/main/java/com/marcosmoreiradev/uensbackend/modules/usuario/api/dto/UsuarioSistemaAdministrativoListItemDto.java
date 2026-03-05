package com.marcosmoreiradev.uensbackend.modules.usuario.api.dto;

/**
 * DTO resumido para listados de usuarios administrativos.
 */
public record UsuarioSistemaAdministrativoListItemDto(
        Long id,
        String nombreLogin,
        String rol,
        String estado
) {
}
