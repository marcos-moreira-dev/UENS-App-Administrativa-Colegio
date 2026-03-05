package com.marcosmoreiradev.uensbackend.modules.representante.api.dto;

/**
 * Define la responsabilidad de RepresentanteLegalListItemDto dentro del backend UENS.
 * Contexto: modulo representante, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record RepresentanteLegalListItemDto(
        Long id,
        String nombres,
        String apellidos,
        String telefono,
        String correoElectronico
) {
}

