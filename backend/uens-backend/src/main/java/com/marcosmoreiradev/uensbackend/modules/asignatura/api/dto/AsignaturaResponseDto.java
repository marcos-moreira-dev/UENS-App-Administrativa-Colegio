package com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto;

/**
 * Define la responsabilidad de AsignaturaResponseDto dentro del backend UENS.
 * Contexto: modulo asignatura, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */
public record AsignaturaResponseDto(
        Long id,
        String nombre,
        String area,
        String descripcion,
        Integer grado,
        String estado
) {}