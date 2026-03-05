package com.marcosmoreiradev.uensbackend.modules.representante.application.mapper;

import com.marcosmoreiradev.uensbackend.modules.representante.api.dto.RepresentanteLegalCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.representante.api.dto.RepresentanteLegalListItemDto;
import com.marcosmoreiradev.uensbackend.modules.representante.api.dto.RepresentanteLegalResponseDto;
import com.marcosmoreiradev.uensbackend.modules.representante.api.dto.RepresentanteLegalUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.representante.infrastructure.persistence.entity.RepresentanteLegalJpaEntity;
import org.springframework.stereotype.Component;

@Component
/**
 * Define la responsabilidad de RepresentanteLegalDtoMapper dentro del backend UENS.
 * Contexto: modulo representante, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: traducir estructuras entre DTOs API, modelos de aplicacion y entidades persistentes.
 */
public class RepresentanteLegalDtoMapper {

/**
 * Implementa la operacion 'toEntityForCreate' del modulo representante en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public RepresentanteLegalJpaEntity toEntityForCreate(RepresentanteLegalCreateRequestDto dto) {
        return new RepresentanteLegalJpaEntity(
                normalizeText(dto.nombres()),
                normalizeText(dto.apellidos()),
                normalizeText(dto.telefono()),
                normalizeEmail(dto.correoElectronico())
        );
    }

/**
 * Implementa la operacion 'applyUpdate' del modulo representante en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
 */
    public void applyUpdate(RepresentanteLegalJpaEntity entity, RepresentanteLegalUpdateRequestDto dto) {
        entity.actualizarDatos(
                normalizeText(dto.nombres()),
                normalizeText(dto.apellidos()),
                normalizeText(dto.telefono()),
                normalizeEmail(dto.correoElectronico())
        );
    }

/**
 * Implementa la operacion 'toResponseDto' del modulo representante en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public RepresentanteLegalResponseDto toResponseDto(RepresentanteLegalJpaEntity entity) {
        return new RepresentanteLegalResponseDto(
                entity.getId(),
                entity.getNombres(),
                entity.getApellidos(),
                entity.getTelefono(),
                entity.getCorreoElectronico()
        );
    }

/**
 * Implementa la operacion 'toListItemDto' del modulo representante en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public RepresentanteLegalListItemDto toListItemDto(RepresentanteLegalJpaEntity entity) {
        return new RepresentanteLegalListItemDto(
                entity.getId(),
                entity.getNombres(),
                entity.getApellidos(),
                entity.getTelefono(),
                entity.getCorreoElectronico()
        );
    }

/**
 * Metodo de soporte interno 'normalizeText' para mantener cohesion en RepresentanteLegalDtoMapper.
 * Contexto: modulo representante, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

/**
 * Metodo de soporte interno 'normalizeEmail' para mantener cohesion en RepresentanteLegalDtoMapper.
 * Contexto: modulo representante, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalizeEmail(String value) {
        String normalized = normalizeText(value);
        return normalized == null ? null : normalized.toLowerCase();
    }
}

