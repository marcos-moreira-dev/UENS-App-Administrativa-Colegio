package com.marcosmoreiradev.uensbackend.modules.calificacion.application.validator;

import com.marcosmoreiradev.uensbackend.common.exception.base.ValidationException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ApiErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
/**
 * Define la responsabilidad de CalificacionRequestValidator dentro del backend UENS.
 * Contexto: modulo calificacion, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class CalificacionRequestValidator {

/**
 * Implementa la operacion 'validarRangoNota' del modulo calificacion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param nota nota numerica registrada en escala institucional definida para fase 1
 */
    public void validarRangoNota(BigDecimal nota) {
        if (nota == null) {
            return;
        }
        if (nota.compareTo(BigDecimal.ZERO) < 0 || nota.compareTo(new BigDecimal("10.00")) > 0) {
            throw new ValidationException(ApiErrorCodes.VR_05_RANGO_NUMERICO_INVALIDO, "La nota debe estar entre 0 y 10.");
        }
    }
}
