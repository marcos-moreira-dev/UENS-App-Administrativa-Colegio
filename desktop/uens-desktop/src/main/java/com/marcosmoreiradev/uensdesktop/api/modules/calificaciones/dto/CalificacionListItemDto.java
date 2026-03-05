package com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Compact grade projection used by listings.
 */
public record CalificacionListItemDto(
        Long id,
        Integer numeroParcial,
        BigDecimal nota,
        LocalDate fechaRegistro,
        String observacion,
        Long estudianteId,
        Long claseId) {
}
