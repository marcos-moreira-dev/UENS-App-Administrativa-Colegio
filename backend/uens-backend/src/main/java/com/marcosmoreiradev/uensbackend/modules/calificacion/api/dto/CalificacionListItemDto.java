package com.marcosmoreiradev.uensbackend.modules.calificacion.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Define la responsabilidad de CalificacionListItemDto dentro del backend UENS.
 * Contexto: modulo calificacion, capa api, arquitectura monolito modular Spring Boot.
 * Alcance: definir contrato de intercambio entre API y capas internas.
 */

public record CalificacionListItemDto(
        Long id,
        Integer numeroParcial,
        BigDecimal nota,
        LocalDate fechaRegistro,
        String observacion,
        Long estudianteId,
        Long claseId
) {
}

