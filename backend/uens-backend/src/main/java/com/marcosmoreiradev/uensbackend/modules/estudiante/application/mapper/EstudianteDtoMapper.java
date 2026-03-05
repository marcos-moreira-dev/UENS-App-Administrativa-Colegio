package com.marcosmoreiradev.uensbackend.modules.estudiante.application.mapper;

import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteListItemDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteResponseDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.entity.EstudianteJpaEntity;
import org.springframework.stereotype.Component;

@Component
/**
 * Define la responsabilidad de EstudianteDtoMapper dentro del backend UENS.
 * Contexto: modulo estudiante, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: traducir estructuras entre DTOs API, modelos de aplicacion y entidades persistentes.
 */
public class EstudianteDtoMapper {

/**
 * Implementa la operacion 'toResponseDto' del modulo estudiante en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public EstudianteResponseDto toResponseDto(EstudianteJpaEntity entity) {
        return new EstudianteResponseDto(
                entity.getId(),
                entity.getNombres(),
                entity.getApellidos(),
                entity.getFechaNacimiento(),
                entity.getEstado(),
                entity.getRepresentanteLegal().getId(),
                entity.getSeccion() == null ? null : entity.getSeccion().getId()
        );
    }

/**
 * Implementa la operacion 'toListItemDto' del modulo estudiante en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public EstudianteListItemDto toListItemDto(EstudianteJpaEntity entity) {
        return new EstudianteListItemDto(
                entity.getId(),
                entity.getNombres(),
                entity.getApellidos(),
                entity.getFechaNacimiento(),
                entity.getEstado(),
                entity.getRepresentanteLegal().getId(),
                entity.getSeccion() == null ? null : entity.getSeccion().getId()
        );
    }
}

