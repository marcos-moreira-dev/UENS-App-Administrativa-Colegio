package com.marcosmoreiradev.uensbackend.modules.seccion.application.validator;

import com.marcosmoreiradev.uensbackend.common.exception.base.ValidationException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ApiErrorCodes;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Component
/**
 * Define la responsabilidad de SeccionFilterValidator dentro del backend UENS.
 * Contexto: modulo seccion, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class SeccionFilterValidator {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("ACTIVO", "INACTIVO");

/**
 * Implementa la operacion 'validarEstado' del modulo seccion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
 */
    public void validarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return;
        }
        String normalizado = estado.trim().toUpperCase(Locale.ROOT);
        if (!ESTADOS_VALIDOS.contains(normalizado)) {
            throw new ValidationException(ApiErrorCodes.VR_02_PARAMETRO_INVALIDO, "El filtro 'estado' debe ser ACTIVO o INACTIVO.");
        }
    }
}
