package com.marcosmoreiradev.uensbackend.modules.clase.application.mapper;

import com.marcosmoreiradev.uensbackend.modules.clase.api.dto.ClaseListItemDto;
import com.marcosmoreiradev.uensbackend.modules.clase.api.dto.ClaseResponseDto;
import com.marcosmoreiradev.uensbackend.modules.clase.infrastructure.persistence.entity.ClaseJpaEntity;
import org.springframework.stereotype.Component;

@Component
/**
 * Define la responsabilidad de ClaseDtoMapper dentro del backend UENS.
 * Contexto: modulo clase, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: traducir estructuras entre DTOs API, modelos de aplicacion y entidades persistentes.
 */
public class ClaseDtoMapper {

/**
 * Implementa la operacion 'toResponseDto' del modulo clase en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ClaseResponseDto toResponseDto(ClaseJpaEntity entity) {
        return new ClaseResponseDto(
                entity.getId(),
                entity.getSeccion().getId(),
                entity.getAsignatura().getId(),
                entity.getDocente() == null ? null : entity.getDocente().getId(),
                entity.getDiaSemana(),
                entity.getHoraInicio(),
                entity.getHoraFin(),
                entity.getEstado()
        );
    }

/**
 * Implementa la operacion 'toListItemDto' del modulo clase en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ClaseListItemDto toListItemDto(ClaseJpaEntity entity) {
        return new ClaseListItemDto(
                entity.getId(),
                entity.getSeccion().getId(),
                entity.getAsignatura().getId(),
                entity.getDocente() == null ? null : entity.getDocente().getId(),
                entity.getDiaSemana(),
                entity.getHoraInicio(),
                entity.getHoraFin(),
                entity.getEstado()
        );
    }
}

