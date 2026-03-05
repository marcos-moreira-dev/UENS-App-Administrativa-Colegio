package com.marcosmoreiradev.uensbackend.modules.docente.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.BusinessRuleException;
import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.DocenteErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.docente.api.dto.DocenteCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.docente.api.dto.DocentePatchEstadoRequestDto;
import com.marcosmoreiradev.uensbackend.modules.docente.api.dto.DocenteResponseDto;
import com.marcosmoreiradev.uensbackend.modules.docente.api.dto.DocenteUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.docente.application.mapper.DocenteDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.docente.infrastructure.persistence.entity.DocenteJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.docente.infrastructure.persistence.repository.DocenteJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
/**
 * Define la responsabilidad de DocenteCommandService dentro del backend UENS.
 * Contexto: modulo docente, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: ejecutar operaciones de escritura aplicando reglas de negocio y validaciones de fase 1.
 */
public class DocenteCommandService {

    private final DocenteJpaRepository repository;
    private final DocenteDtoMapper mapper;
/**
 * Construye la instancia de DocenteCommandService para operar en el modulo docente.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param repository adaptador de persistencia que opera sobre tablas del esquema V2 3FN
     * @param mapper componente de mapeo entre DTOs, modelos de dominio y entidades
 */

    public DocenteCommandService(DocenteJpaRepository repository, DocenteDtoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
/**
 * Implementa la operacion 'crear' del modulo docente en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public DocenteResponseDto crear(DocenteCreateRequestDto dto) {
        String correo = normalizeEmail(dto.correoElectronico());
        if (correo != null && repository.existsByCorreoElectronicoIgnoreCase(correo)) {
            throw new BusinessRuleException(DocenteErrorCodes.RN_DOC_01_CORREO_DUPLICADO);
        }

        DocenteJpaEntity saved = repository.save(mapper.toEntityForCreate(dto));
        return mapper.toResponseDto(saved);
    }

    @Transactional
/**
 * Implementa la operacion 'actualizar' del modulo docente en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public DocenteResponseDto actualizar(Long id, DocenteUpdateRequestDto dto) {
        DocenteJpaEntity entity = getOrThrow(id);

        String correo = normalizeEmail(dto.correoElectronico());
        if (correo != null && repository.existsByCorreoElectronicoIgnoreCaseAndIdNot(correo, id)) {
            throw new BusinessRuleException(
                    DocenteErrorCodes.RN_DOC_01_CORREO_DUPLICADO,
                    "Ya existe otro docente con el mismo correo."
            );
        }

        mapper.applyUpdate(entity, dto);
        DocenteJpaEntity saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Transactional
/**
 * Implementa la operacion 'cambiarEstado' del modulo docente en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public DocenteResponseDto cambiarEstado(Long id, DocentePatchEstadoRequestDto dto) {
        DocenteJpaEntity entity = getOrThrow(id);
        mapper.applyPatchEstado(entity, dto);
        DocenteJpaEntity saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

/**
 * Metodo de soporte interno 'getOrThrow' para mantener cohesion en DocenteCommandService.
 * Contexto: modulo docente, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private DocenteJpaEntity getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado."));
    }

/**
 * Metodo de soporte interno 'normalizeEmail' para mantener cohesion en DocenteCommandService.
 * Contexto: modulo docente, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalizeEmail(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }
}
