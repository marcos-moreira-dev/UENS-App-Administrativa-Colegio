package com.marcosmoreiradev.uensbackend.modules.docente.api.dto;

/**
 * Define la responsabilidad de DocenteListItemDto dentro del backend UENS.
 * Contexto: modulo docente, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record DocenteListItemDto(
        Long id,
        String nombres,
        String apellidos,
        String telefono,
        String correoElectronico,
        String estado
) {
}

