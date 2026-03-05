package com.marcosmoreiradev.uensbackend.modules.seccion.application.validator;

import com.marcosmoreiradev.uensbackend.common.exception.base.ValidationException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ApiErrorCodes;
import org.springframework.stereotype.Component;

@Component
/**
 * Define la responsabilidad de SeccionRequestValidator dentro del backend UENS.
 * Contexto: modulo seccion, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class SeccionRequestValidator {

/**
 * Implementa la operacion 'validarAnioLectivo' del modulo seccion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param anioLectivo dato de entrada relevante para ejecutar esta operacion: 'anioLectivo'
 */
    public void validarAnioLectivo(String anioLectivo) {
        if (anioLectivo == null || anioLectivo.isBlank()) {
            return;
        }

        String[] partes = anioLectivo.split("-");
        if (partes.length != 2) {
            throw new ValidationException(ApiErrorCodes.VR_04_FORMATO_INVALIDO, "El anio lectivo debe tener formato YYYY-YYYY.");
        }

        try {
            int inicio = Integer.parseInt(partes[0]);
            int fin = Integer.parseInt(partes[1]);
            if (fin != inicio + 1) {
                throw new ValidationException(ApiErrorCodes.VR_04_FORMATO_INVALIDO, "El anio lectivo debe ser consecutivo (ej. 2025-2026).");
            }
        } catch (NumberFormatException ex) {
            throw new ValidationException(ApiErrorCodes.VR_04_FORMATO_INVALIDO, "El anio lectivo debe contener solo numeros.");
        }
    }
}
