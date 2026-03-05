package com.marcosmoreiradev.uensbackend.common.pagination;

import com.marcosmoreiradev.uensbackend.common.api.response.ErrorDetailDto;
import com.marcosmoreiradev.uensbackend.common.exception.base.ValidationException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ApiErrorCodes;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
/**
 * Define la responsabilidad de SortWhitelistValidator dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class SortWhitelistValidator {

/**
 * Implementa la operacion 'validateField' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param field dato de entrada relevante para ejecutar esta operacion: 'field'
     * @param whitelist dato de entrada relevante para ejecutar esta operacion: 'whitelist'
 */
    public void validateField(String field, Set<String> whitelist) {
        if (field == null || field.isBlank() || whitelist == null || !whitelist.contains(field)) {
            throw new ValidationException(
                    ApiErrorCodes.VR_02_PARAMETRO_INVALIDO,
                    "El campo de ordenamiento '" + field + "' no esta permitido.",
                    List.of(ErrorDetailDto.of("sort", "InvalidSortField", "Campo de ordenamiento no permitido.", field))
            );
        }
    }
}

