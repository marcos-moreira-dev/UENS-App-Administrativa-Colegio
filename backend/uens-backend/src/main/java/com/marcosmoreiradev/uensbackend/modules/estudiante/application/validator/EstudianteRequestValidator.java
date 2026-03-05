package com.marcosmoreiradev.uensbackend.modules.estudiante.application.validator;

import com.marcosmoreiradev.uensbackend.common.exception.base.ValidationException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ApiErrorCodes;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
/**
 * Define la responsabilidad de EstudianteRequestValidator dentro del backend UENS.
 * Contexto: modulo estudiante, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class EstudianteRequestValidator {

/**
 * Implementa la operacion 'validarFechaNacimiento' del modulo estudiante en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param fechaNacimiento dato de entrada relevante para ejecutar esta operacion: 'fechaNacimiento'
 */
    public void validarFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return;
        }
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new ValidationException(ApiErrorCodes.VR_02_PARAMETRO_INVALIDO, "La fecha de nacimiento no puede estar en el futuro.");
        }
    }
}
