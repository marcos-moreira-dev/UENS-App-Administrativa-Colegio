package com.marcosmoreiradev.uensbackend.modules.usuario.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Contrato de entrada para registrar usuarios administrativos.
 */
public record UsuarioSistemaAdministrativoCreateRequestDto(
        @NotBlank(message = "El login es obligatorio.")
        @Size(max = 80, message = "El login no debe exceder 80 caracteres.")
        String nombreLogin,

        @NotBlank(message = "La contrasena es obligatoria.")
        @Size(min = 8, max = 120, message = "La contrasena debe tener entre 8 y 120 caracteres.")
        String password,

        @NotBlank(message = "El rol es obligatorio.")
        @Size(max = 20, message = "El rol no debe exceder 20 caracteres.")
        String rol,

        @NotBlank(message = "El estado es obligatorio.")
        @Size(max = 10, message = "El estado no debe exceder 10 caracteres.")
        String estado
) {
}
