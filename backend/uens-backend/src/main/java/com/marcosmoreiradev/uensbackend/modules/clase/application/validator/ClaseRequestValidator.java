package com.marcosmoreiradev.uensbackend.modules.clase.application.validator;

import com.marcosmoreiradev.uensbackend.common.exception.base.ValidationException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ApiErrorCodes;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
/**
 * Define la responsabilidad de ClaseRequestValidator dentro del backend UENS.
 * Contexto: modulo clase, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class ClaseRequestValidator {

/**
 * Implementa la operacion 'validarRangoHorario' del modulo clase en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param horaInicio dato de entrada relevante para ejecutar esta operacion: 'horaInicio'
     * @param horaFin dato de entrada relevante para ejecutar esta operacion: 'horaFin'
 */
    public void validarRangoHorario(LocalTime horaInicio, LocalTime horaFin) {
        if (horaInicio == null || horaFin == null) {
            return;
        }
        if (!horaFin.isAfter(horaInicio)) {
            throw new ValidationException(ApiErrorCodes.VR_02_PARAMETRO_INVALIDO, "La hora de fin debe ser mayor que la hora de inicio.");
        }
    }
}
