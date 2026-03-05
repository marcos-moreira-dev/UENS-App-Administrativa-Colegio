package com.marcosmoreiradev.uensbackend.modules.calificacion.application.mapper;

import com.marcosmoreiradev.uensbackend.modules.calificacion.api.dto.CalificacionListItemDto;
import com.marcosmoreiradev.uensbackend.modules.calificacion.api.dto.CalificacionResponseDto;
import com.marcosmoreiradev.uensbackend.modules.calificacion.infrastructure.persistence.entity.CalificacionJpaEntity;
import org.springframework.stereotype.Component;

@Component
/**
 * Define la responsabilidad de CalificacionDtoMapper dentro del backend UENS.
 * Contexto: modulo calificacion, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: traducir estructuras entre DTOs API, modelos de aplicacion y entidades persistentes.
 */
public class CalificacionDtoMapper {

/**
 * Implementa la operacion 'toResponseDto' del modulo calificacion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public CalificacionResponseDto toResponseDto(CalificacionJpaEntity entity) {
        return new CalificacionResponseDto(
                entity.getId(),
                entity.getNumeroParcial(),
                entity.getNota(),
                entity.getFechaRegistro(),
                entity.getObservacion(),
                entity.getEstudiante().getId(),
                entity.getClase().getId()
        );
    }

/**
 * Implementa la operacion 'toListItemDto' del modulo calificacion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public CalificacionListItemDto toListItemDto(CalificacionJpaEntity entity) {
        return new CalificacionListItemDto(
                entity.getId(),
                entity.getNumeroParcial(),
                entity.getNota(),
                entity.getFechaRegistro(),
                entity.getObservacion(),
                entity.getEstudiante().getId(),
                entity.getClase().getId()
        );
    }
}

