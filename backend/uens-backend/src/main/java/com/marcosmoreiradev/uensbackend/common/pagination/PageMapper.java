package com.marcosmoreiradev.uensbackend.common.pagination;

import com.marcosmoreiradev.uensbackend.common.api.response.PageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
/**
 * Define la responsabilidad de PageMapper dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: traducir estructuras entre DTOs API, modelos de aplicacion y entidades persistentes.
 */
public class PageMapper {

/**
 * Implementa la operacion 'toPageResponseDto' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param page indice de pagina solicitado (base cero) para paginacion de resultados
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public <T> PageResponseDto<T> toPageResponseDto(Page<T> page) {
        return new PageResponseDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumberOfElements(),
                page.isFirst(),
                page.isLast(),
                summarizeSort(page.getSort())
        );
    }

/**
 * Metodo de soporte interno 'summarizeSort' para mantener cohesion en PageMapper.
 * Contexto: modulo core, capa common, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param sort criterios de ordenamiento solicitados por cliente segun whitelist permitida
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private String summarizeSort(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return null;
        }

        return sort.stream()
                .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                .collect(Collectors.joining(";"));
    }
}

