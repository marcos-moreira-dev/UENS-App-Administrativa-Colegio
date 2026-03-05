package com.marcosmoreiradev.uensbackend.common.pagination;

import com.marcosmoreiradev.uensbackend.common.api.response.ErrorDetailDto;
import com.marcosmoreiradev.uensbackend.common.exception.base.ValidationException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ApiErrorCodes;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
/**
 * Define la responsabilidad de PageSortParser dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class PageSortParser {

    private final SortWhitelistValidator sortWhitelistValidator;
/**
 * Construye la instancia de PageSortParser para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param sortWhitelistValidator dato de entrada relevante para ejecutar esta operacion: 'sortWhitelistValidator'
 */

    public PageSortParser(SortWhitelistValidator sortWhitelistValidator) {
        this.sortWhitelistValidator = sortWhitelistValidator;
    }

/**
 * Implementa la operacion 'parseOrDefault' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param sortParams dato de entrada relevante para ejecutar esta operacion: 'sortParams'
     * @param whitelist dato de entrada relevante para ejecutar esta operacion: 'whitelist'
     * @param defaultSort dato de entrada relevante para ejecutar esta operacion: 'defaultSort'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Sort parseOrDefault(List<String> sortParams, Set<String> whitelist, Sort defaultSort) {
        if (sortParams == null || sortParams.isEmpty()) {
            return defaultSort;
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String raw : sortParams) {
            if (raw == null || raw.isBlank()) {
                continue;
            }

            String[] parts = raw.split(",", 2);
            String field = parts[0].trim();
            sortWhitelistValidator.validateField(field, whitelist);

            String rawDirection = parts.length > 1 ? parts[1].trim() : "asc";
            Sort.Direction direction = parseDirection(rawDirection);
            orders.add(new Sort.Order(direction, field));
        }

        if (orders.isEmpty()) {
            return defaultSort;
        }

        return Sort.by(orders);
    }

/**
 * Metodo de soporte interno 'parseDirection' para mantener cohesion en PageSortParser.
 * Contexto: modulo core, capa common, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param rawDirection dato de entrada relevante para ejecutar esta operacion: 'rawDirection'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private Sort.Direction parseDirection(String rawDirection) {
        String normalized = rawDirection.toLowerCase(Locale.ROOT);
        if ("asc".equals(normalized)) {
            return Sort.Direction.ASC;
        }
        if ("desc".equals(normalized)) {
            return Sort.Direction.DESC;
        }

        throw new ValidationException(
                ApiErrorCodes.VR_02_PARAMETRO_INVALIDO,
                "Direccion de ordenamiento invalida: " + rawDirection + ". Use asc o desc.",
                List.of(ErrorDetailDto.of("sort", "InvalidSortDirection", "Direccion de ordenamiento invalida.", rawDirection))
        );
    }
}

