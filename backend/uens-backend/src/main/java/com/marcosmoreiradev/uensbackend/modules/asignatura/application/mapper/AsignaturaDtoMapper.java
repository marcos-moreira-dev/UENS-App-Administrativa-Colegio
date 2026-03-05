package com.marcosmoreiradev.uensbackend.modules.asignatura.application.mapper;

import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaListItemDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaPatchEstadoRequestDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaResponseDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.infrastructure.persistence.entity.AsignaturaJpaEntity;
import org.springframework.stereotype.Component;

@Component
/**
 * Define la responsabilidad de AsignaturaDtoMapper dentro del backend UENS.
 * Contexto: modulo asignatura, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: traducir estructuras entre DTOs API, modelos de aplicacion y entidades persistentes.
 */
public class AsignaturaDtoMapper {

/**
 * Implementa la operacion 'toEntityForCreate' del modulo asignatura en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public AsignaturaJpaEntity toEntityForCreate(AsignaturaCreateRequestDto dto) {
        return AsignaturaJpaEntity.crear(
                normalizeText(dto.nombre()),
                normalizeText(dto.area()),
                normalizeText(dto.descripcion()),
                dto.grado()
        );
    }

/**
 * Implementa la operacion 'applyUpdate' del modulo asignatura en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
 */
    public void applyUpdate(AsignaturaJpaEntity entity, AsignaturaUpdateRequestDto dto) {
        entity.actualizarDatos(
                normalizeText(dto.nombre()),
                normalizeText(dto.area()),
                normalizeText(dto.descripcion()),
                dto.grado()
        );
        applyEstado(entity, dto.estado());
    }

/**
 * Implementa la operacion 'applyPatchEstado' del modulo asignatura en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
 */
    public void applyPatchEstado(AsignaturaJpaEntity entity, AsignaturaPatchEstadoRequestDto dto) {
        applyEstado(entity, dto.estado());
    }

/**
 * Implementa la operacion 'toResponseDto' del modulo asignatura en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public AsignaturaResponseDto toResponseDto(AsignaturaJpaEntity entity) {
        return new AsignaturaResponseDto(
                entity.getId(),
                entity.getNombre(),
                entity.getArea(),
                entity.getDescripcion(),
                entity.getGrado(),
                entity.getEstado()
        );
    }

/**
 * Implementa la operacion 'toListItemDto' del modulo asignatura en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public AsignaturaListItemDto toListItemDto(AsignaturaJpaEntity entity) {
        return new AsignaturaListItemDto(
                entity.getId(),
                entity.getNombre(),
                entity.getArea(),
                entity.getGrado(),
                entity.getEstado()
        );
    }

/**
 * Metodo de soporte interno 'applyEstado' para mantener cohesion en AsignaturaDtoMapper.
 * Contexto: modulo asignatura, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @param estadoRaw dato de entrada relevante para ejecutar esta operacion: 'estadoRaw'
 */
    private static void applyEstado(AsignaturaJpaEntity entity, String estadoRaw) {
        String estado = normalizeUpper(estadoRaw);
        if ("INACTIVO".equals(estado)) {
            entity.inactivar();
            return;
        }
        entity.activar();
    }

/**
 * Metodo de soporte interno 'normalizeText' para mantener cohesion en AsignaturaDtoMapper.
 * Contexto: modulo asignatura, capa application, con foco en reglas y consistencia tecnica de fase 1.
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
 * Metodo de soporte interno 'normalizeUpper' para mantener cohesion en AsignaturaDtoMapper.
 * Contexto: modulo asignatura, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalizeUpper(String value) {
        String normalized = normalizeText(value);
        return normalized == null ? null : normalized.toUpperCase();
    }
}

