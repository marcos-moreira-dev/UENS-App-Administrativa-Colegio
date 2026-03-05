package com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Full grade detail returned by create, update and detail endpoints.
 */
public record CalificacionResponseDto(
        Long id,
        Integer numeroParcial,
        BigDecimal nota,
        LocalDate fechaRegistro,
        String observacion,
        Long estudianteId,
        Long claseId) {
}
