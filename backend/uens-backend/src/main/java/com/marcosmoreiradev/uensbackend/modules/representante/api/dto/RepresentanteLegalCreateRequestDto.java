package com.marcosmoreiradev.uensbackend.modules.representante.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Define la responsabilidad de RepresentanteLegalCreateRequestDto dentro del backend UENS.
 * Contexto: modulo representante, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record RepresentanteLegalCreateRequestDto(
        @NotBlank(message = "Los nombres son obligatorios.")
        @Size(max = 120, message = "Los nombres no deben exceder 120 caracteres.")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios.")
        @Size(max = 120, message = "Los apellidos no deben exceder 120 caracteres.")
        String apellidos,

        @Size(max = 30, message = "El telefono no debe exceder 30 caracteres.")
        String telefono,

        @Size(max = 254, message = "El correo no debe exceder 254 caracteres.")
        @Pattern(regexp = "^$|^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "El correo no tiene formato valido.")
        String correoElectronico
) {
}

