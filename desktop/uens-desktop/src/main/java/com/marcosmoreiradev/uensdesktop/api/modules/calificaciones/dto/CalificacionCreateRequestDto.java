package com.marcosmoreiradev.uensdesktop.api.modules.calificaciones.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request payload used to create a grade.
 */
public record CalificacionCreateRequestDto(
        Integer numeroParcial,
        BigDecimal nota,
        LocalDate fechaRegistro,
        String observacion,
        Long estudianteId,
        Long claseId) {
}
