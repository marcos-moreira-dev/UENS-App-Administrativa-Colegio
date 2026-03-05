package com.marcosmoreiradev.uensbackend.modules.usuario.api.dto;

/**
 * DTO de detalle para usuarios administrativos.
 */
public record UsuarioSistemaAdministrativoResponseDto(
        Long id,
        String nombreLogin,
        String rol,
        String estado
) {
}
