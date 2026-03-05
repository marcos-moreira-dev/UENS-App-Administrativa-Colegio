package com.marcosmoreiradev.uensbackend.modules.docente.application.mapper;

import com.marcosmoreiradev.uensbackend.modules.docente.api.dto.DocenteCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.docente.api.dto.DocenteListItemDto;
import com.marcosmoreiradev.uensbackend.modules.docente.api.dto.DocentePatchEstadoRequestDto;
import com.marcosmoreiradev.uensbackend.modules.docente.api.dto.DocenteResponseDto;
import com.marcosmoreiradev.uensbackend.modules.docente.api.dto.DocenteUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.docente.infrastructure.persistence.entity.DocenteJpaEntity;
import org.springframework.stereotype.Component;

@Component
/**
 * Define la responsabilidad de DocenteDtoMapper dentro del backend UENS.
 * Contexto: modulo docente, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: traducir estructuras entre DTOs API, modelos de aplicacion y entidades persistentes.
 */
public class DocenteDtoMapper {

/**
 * Implementa la operacion 'toEntityForCreate' del modulo docente en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public DocenteJpaEntity toEntityForCreate(DocenteCreateRequestDto dto) {
        return DocenteJpaEntity.crear(
                normalizeText(dto.nombres()),
                normalizeText(dto.apellidos()),
                normalizeText(dto.telefono()),
                normalizeEmail(dto.correoElectronico())
        );
    }

/**
 * Implementa la operacion 'applyUpdate' del modulo docente en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
 */
    public void applyUpdate(DocenteJpaEntity entity, DocenteUpdateRequestDto dto) {
        entity.actualizarDatos(
                normalizeText(dto.nombres()),
                normalizeText(dto.apellidos()),
                normalizeText(dto.telefono()),
                normalizeEmail(dto.correoElectronico())
        );
        applyEstado(entity, dto.estado());
    }

/**
 * Implementa la operacion 'applyPatchEstado' del modulo docente en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
 */
    public void applyPatchEstado(DocenteJpaEntity entity, DocentePatchEstadoRequestDto dto) {
        applyEstado(entity, dto.estado());
    }

/**
 * Implementa la operacion 'toResponseDto' del modulo docente en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public DocenteResponseDto toResponseDto(DocenteJpaEntity entity) {
        return new DocenteResponseDto(
                entity.getId(),
                entity.getNombres(),
                entity.getApellidos(),
                entity.getTelefono(),
                entity.getCorreoElectronico(),
                entity.getEstado()
        );
    }

/**
 * Implementa la operacion 'toListItemDto' del modulo docente en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public DocenteListItemDto toListItemDto(DocenteJpaEntity entity) {
        return new DocenteListItemDto(
                entity.getId(),
                entity.getNombres(),
                entity.getApellidos(),
                entity.getTelefono(),
                entity.getCorreoElectronico(),
                entity.getEstado()
        );
    }

/**
 * Metodo de soporte interno 'applyEstado' para mantener cohesion en DocenteDtoMapper.
 * Contexto: modulo docente, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @param estadoRaw dato de entrada relevante para ejecutar esta operacion: 'estadoRaw'
 */
    private static void applyEstado(DocenteJpaEntity entity, String estadoRaw) {
        String estado = normalizeUpper(estadoRaw);
        if ("INACTIVO".equals(estado)) {
            entity.inactivar();
            return;
        }
        entity.activar();
    }

/**
 * Metodo de soporte interno 'normalizeText' para mantener cohesion en DocenteDtoMapper.
 * Contexto: modulo docente, capa application, con foco en reglas y consistencia tecnica de fase 1.
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
 * Metodo de soporte interno 'normalizeUpper' para mantener cohesion en DocenteDtoMapper.
 * Contexto: modulo docente, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalizeUpper(String value) {
        String normalized = normalizeText(value);
        return normalized == null ? null : normalized.toUpperCase();
    }

/**
 * Metodo de soporte interno 'normalizeEmail' para mantener cohesion en DocenteDtoMapper.
 * Contexto: modulo docente, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalizeEmail(String value) {
        String normalized = normalizeText(value);
        return normalized == null ? null : normalized.toLowerCase();
    }
}

