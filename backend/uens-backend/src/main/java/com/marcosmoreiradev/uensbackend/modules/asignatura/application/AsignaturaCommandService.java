package com.marcosmoreiradev.uensbackend.modules.asignatura.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.BusinessRuleException;
import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.AsignaturaErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaPatchEstadoRequestDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaResponseDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.api.dto.AsignaturaUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.asignatura.application.mapper.AsignaturaDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.asignatura.infrastructure.persistence.entity.AsignaturaJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.asignatura.infrastructure.persistence.repository.AsignaturaJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * Define la responsabilidad de AsignaturaCommandService dentro del backend UENS.
 * Contexto: modulo asignatura, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: ejecutar operaciones de escritura aplicando reglas de negocio y validaciones de fase 1.
 */
public class AsignaturaCommandService {

    private final AsignaturaJpaRepository asignaturaJpaRepository;
    private final AsignaturaDtoMapper asignaturaDtoMapper;
/**
 * Construye la instancia de AsignaturaCommandService para operar en el modulo asignatura.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param asignaturaJpaRepository dato de entrada relevante para ejecutar esta operacion: 'asignaturaJpaRepository'
     * @param asignaturaDtoMapper dato de entrada relevante para ejecutar esta operacion: 'asignaturaDtoMapper'
 */

    public AsignaturaCommandService(
            AsignaturaJpaRepository asignaturaJpaRepository,
            AsignaturaDtoMapper asignaturaDtoMapper
    ) {
        this.asignaturaJpaRepository = asignaturaJpaRepository;
        this.asignaturaDtoMapper = asignaturaDtoMapper;
    }

    @Transactional
/**
 * Implementa la operacion 'crear' del modulo asignatura en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public AsignaturaResponseDto crear(AsignaturaCreateRequestDto dto) {
        String nombre = normalize(dto.nombre());
        Integer grado = dto.grado();

        if (asignaturaJpaRepository.existsByNombreIgnoreCaseAndGrado(nombre, grado)) {
            throw new BusinessRuleException(
                    AsignaturaErrorCodes.RN_ASI_01_REGISTRO_DUPLICADO,
                    "Ya existe una asignatura con el mismo nombre para el mismo grado."
            );
        }

        AsignaturaJpaEntity entity = asignaturaDtoMapper.toEntityForCreate(dto);
        AsignaturaJpaEntity saved = asignaturaJpaRepository.save(entity);
        return asignaturaDtoMapper.toResponseDto(saved);
    }

    @Transactional
/**
 * Implementa la operacion 'actualizar' del modulo asignatura en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param asignaturaId identificador de la asignatura dentro del catalogo academico
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public AsignaturaResponseDto actualizar(Long asignaturaId, AsignaturaUpdateRequestDto dto) {
        AsignaturaJpaEntity entity = getOrThrow(asignaturaId);

        String nombre = normalize(dto.nombre());
        Integer grado = dto.grado();
        if (asignaturaJpaRepository.existsByNombreIgnoreCaseAndGradoAndIdNot(nombre, grado, asignaturaId)) {
            throw new BusinessRuleException(
                    AsignaturaErrorCodes.RN_ASI_01_REGISTRO_DUPLICADO,
                    "Ya existe otra asignatura con el mismo nombre para el mismo grado."
            );
        }

        asignaturaDtoMapper.applyUpdate(entity, dto);
        AsignaturaJpaEntity saved = asignaturaJpaRepository.save(entity);
        return asignaturaDtoMapper.toResponseDto(saved);
    }

    @Transactional
/**
 * Implementa la operacion 'cambiarEstado' del modulo asignatura en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param asignaturaId identificador de la asignatura dentro del catalogo academico
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public AsignaturaResponseDto cambiarEstado(Long asignaturaId, AsignaturaPatchEstadoRequestDto dto) {
        AsignaturaJpaEntity entity = getOrThrow(asignaturaId);
        asignaturaDtoMapper.applyPatchEstado(entity, dto);
        AsignaturaJpaEntity saved = asignaturaJpaRepository.save(entity);
        return asignaturaDtoMapper.toResponseDto(saved);
    }

/**
 * Metodo de soporte interno 'getOrThrow' para mantener cohesion en AsignaturaCommandService.
 * Contexto: modulo asignatura, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private AsignaturaJpaEntity getOrThrow(Long id) {
        return asignaturaJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Asignatura no encontrada."
                ));
    }

/**
 * Metodo de soporte interno 'normalize' para mantener cohesion en AsignaturaCommandService.
 * Contexto: modulo asignatura, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
