package com.marcosmoreiradev.uensbackend.common.pagination;

import com.marcosmoreiradev.uensbackend.common.api.response.ErrorDetailDto;
import com.marcosmoreiradev.uensbackend.common.constants.PaginationConstants;
import com.marcosmoreiradev.uensbackend.common.exception.base.ValidationException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ApiErrorCodes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
/**
 * Define la responsabilidad de PageableFactory dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class PageableFactory {

    private final PageSortParser pageSortParser;
/**
 * Construye la instancia de PageableFactory para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param pageSortParser dato de entrada relevante para ejecutar esta operacion: 'pageSortParser'
 */

    public PageableFactory(PageSortParser pageSortParser) {
        this.pageSortParser = pageSortParser;
    }

    public Pageable from(
            Integer page,
            Integer size,
            List<String> sortParams,
            Sort defaultSort,
            Set<String> sortWhitelist
    ) {
        int resolvedPage = page == null ? PaginationConstants.DEFAULT_PAGE : page;
        int resolvedSize = size == null ? PaginationConstants.DEFAULT_SIZE : size;

        validatePage(resolvedPage);
        validateSize(resolvedSize);

        Sort sort = pageSortParser.parseOrDefault(sortParams, sortWhitelist, defaultSort);
        return PageRequest.of(resolvedPage, resolvedSize, sort);
    }

/**
 * Metodo de soporte interno 'validatePage' para mantener cohesion en PageableFactory.
 * Contexto: modulo core, capa common, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param page indice de pagina solicitado (base cero) para paginacion de resultados
 */
    private void validatePage(int page) {
        if (page >= 0) {
            return;
        }

        throw new ValidationException(
                ApiErrorCodes.VR_02_PARAMETRO_INVALIDO,
                "El parametro 'page' debe ser mayor o igual a 0.",
                List.of(ErrorDetailDto.of("page", "InvalidPage", "El valor de page es invalido.", page))
        );
    }

/**
 * Metodo de soporte interno 'validateSize' para mantener cohesion en PageableFactory.
 * Contexto: modulo core, capa common, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param size tamano de pagina solicitado respetando limites del contrato API
 */
    private void validateSize(int size) {
        if (size >= 1 && size <= PaginationConstants.MAX_SIZE) {
            return;
        }

        throw new ValidationException(
                ApiErrorCodes.VR_05_RANGO_NUMERICO_INVALIDO,
                "El parametro 'size' debe estar entre 1 y " + PaginationConstants.MAX_SIZE + ".",
                List.of(ErrorDetailDto.of("size", "InvalidSize", "El valor de size esta fuera de rango.", size))
        );
    }
}

