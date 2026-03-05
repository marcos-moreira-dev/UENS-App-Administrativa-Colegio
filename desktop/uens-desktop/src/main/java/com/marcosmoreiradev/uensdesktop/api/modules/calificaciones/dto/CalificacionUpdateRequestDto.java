package com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request payload used to update an existing grade.
 */
public record CalificacionUpdateRequestDto(
        Integer numeroParcial,
        BigDecimal nota,
        LocalDate fechaRegistro,
        String observacion,
        Long estudianteId,
        Long claseId) {
}
