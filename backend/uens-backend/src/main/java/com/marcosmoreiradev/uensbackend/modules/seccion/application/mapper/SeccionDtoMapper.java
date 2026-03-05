package com.marcosmoreiradev.uensbackend.modules.seccion.application.mapper;

import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionListItemDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionPatchEstadoRequestDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionResponseDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.entity.SeccionJpaEntity;
import org.springframework.stereotype.Component;

@Component
/**
 * Define la responsabilidad de SeccionDtoMapper dentro del backend UENS.
 * Contexto: modulo seccion, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: traducir estructuras entre DTOs API, modelos de aplicacion y entidades persistentes.
 */
public class SeccionDtoMapper {

/**
 * Implementa la operacion 'toEntityForCreate' del modulo seccion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public SeccionJpaEntity toEntityForCreate(SeccionCreateRequestDto dto) {
        return SeccionJpaEntity.crear(
                dto.grado(),
                normalizeText(dto.paralelo()),
                dto.cupoMaximo(),
                normalizeText(dto.anioLectivo())
        );
    }

/**
 * Implementa la operacion 'applyUpdate' del modulo seccion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
 */
    public void applyUpdate(SeccionJpaEntity entity, SeccionUpdateRequestDto dto) {
        entity.actualizarDatos(
                dto.grado(),
                normalizeText(dto.paralelo()),
                dto.cupoMaximo(),
                normalizeText(dto.anioLectivo())
        );
        applyEstado(entity, dto.estado());
    }

/**
 * Implementa la operacion 'applyPatchEstado' del modulo seccion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
 */
    public void applyPatchEstado(SeccionJpaEntity entity, SeccionPatchEstadoRequestDto dto) {
        applyEstado(entity, dto.estado());
    }

/**
 * Implementa la operacion 'toResponseDto' del modulo seccion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public SeccionResponseDto toResponseDto(SeccionJpaEntity entity) {
        return new SeccionResponseDto(
                entity.getId(),
                entity.getGrado(),
                entity.getParalelo(),
                entity.getCupoMaximo(),
                entity.getAnioLectivo(),
                entity.getEstado()
        );
    }

/**
 * Implementa la operacion 'toListItemDto' del modulo seccion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public SeccionListItemDto toListItemDto(SeccionJpaEntity entity) {
        return new SeccionListItemDto(
                entity.getId(),
                entity.getGrado(),
                entity.getParalelo(),
                entity.getCupoMaximo(),
                entity.getAnioLectivo(),
                entity.getEstado()
        );
    }

/**
 * Metodo de soporte interno 'applyEstado' para mantener cohesion en SeccionDtoMapper.
 * Contexto: modulo seccion, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @param estadoRaw dato de entrada relevante para ejecutar esta operacion: 'estadoRaw'
 */
    private static void applyEstado(SeccionJpaEntity entity, String estadoRaw) {
        String estado = normalizeUpper(estadoRaw);
        if ("INACTIVO".equals(estado)) {
            entity.inactivar();
            return;
        }
        entity.activar();
    }

/**
 * Metodo de soporte interno 'normalizeText' para mantener cohesion en SeccionDtoMapper.
 * Contexto: modulo seccion, capa application, con foco en reglas y consistencia tecnica de fase 1.
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
 * Metodo de soporte interno 'normalizeUpper' para mantener cohesion en SeccionDtoMapper.
 * Contexto: modulo seccion, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalizeUpper(String value) {
        String normalized = normalizeText(value);
        return normalized == null ? null : normalized.toUpperCase();
    }
}

