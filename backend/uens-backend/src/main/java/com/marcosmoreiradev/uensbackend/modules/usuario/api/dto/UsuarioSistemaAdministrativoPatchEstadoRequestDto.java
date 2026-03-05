package com.marcosmoreiradev.uensbackend.modules.usuario.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Contrato de cambio rapido de estado para usuarios administrativos.
 */
public record UsuarioSistemaAdministrativoPatchEstadoRequestDto(
        @NotBlank(message = "El estado es obligatorio.")
        @Size(max = 10, message = "El estado no debe exceder 10 caracteres.")
        String estado
) {
}
