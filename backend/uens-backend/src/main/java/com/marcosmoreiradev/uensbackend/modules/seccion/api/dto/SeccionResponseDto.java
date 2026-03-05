package com.marcosmoreiradev.uensbackend.modules.seccion.api.dto;

/**
 * Define la responsabilidad de SeccionResponseDto dentro del backend UENS.
 * Contexto: modulo seccion, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record SeccionResponseDto(
        Long id,
        Integer grado,
        String paralelo,
        Integer cupoMaximo,
        String anioLectivo,
        String estado
) {
}

