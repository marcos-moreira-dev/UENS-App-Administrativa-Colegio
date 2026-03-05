package com.marcosmoreiradev.uensbackend.modules.seccion.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.BusinessRuleException;
import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.SeccionErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionPatchEstadoRequestDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionResponseDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.api.dto.SeccionUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.seccion.application.mapper.SeccionDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.seccion.application.validator.SeccionRequestValidator;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.entity.SeccionJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.repository.SeccionJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * Define la responsabilidad de SeccionCommandService dentro del backend UENS.
 * Contexto: modulo seccion, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: ejecutar operaciones de escritura aplicando reglas de negocio y validaciones de fase 1.
 */
public class SeccionCommandService {

    private final SeccionJpaRepository repository;
    private final SeccionDtoMapper mapper;
    private final SeccionRequestValidator requestValidator;
/**
 * Construye la instancia de SeccionCommandService para operar en el modulo seccion.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param repository adaptador de persistencia que opera sobre tablas del esquema V2 3FN
     * @param mapper componente de mapeo entre DTOs, modelos de dominio y entidades
     * @param requestValidator dato de entrada relevante para ejecutar esta operacion: 'requestValidator'
 */

    public SeccionCommandService(
            SeccionJpaRepository repository,
            SeccionDtoMapper mapper,
            SeccionRequestValidator requestValidator
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.requestValidator = requestValidator;
    }

    @Transactional
/**
 * Implementa la operacion 'crear' del modulo seccion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public SeccionResponseDto crear(SeccionCreateRequestDto dto) {
        requestValidator.validarAnioLectivo(dto.anioLectivo());

        String anioLectivo = normalizeText(dto.anioLectivo());
        String paralelo = normalizeUpper(dto.paralelo());

        if (repository.existsByAnioLectivoAndGradoAndParaleloIgnoreCase(anioLectivo, dto.grado(), paralelo)) {
            throw new BusinessRuleException(SeccionErrorCodes.RN_SEC_04_SECCION_DUPLICADA);
        }

        SeccionJpaEntity saved = repository.save(mapper.toEntityForCreate(dto));
        return mapper.toResponseDto(saved);
    }

    @Transactional
/**
 * Implementa la operacion 'actualizar' del modulo seccion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public SeccionResponseDto actualizar(Long id, SeccionUpdateRequestDto dto) {
        requestValidator.validarAnioLectivo(dto.anioLectivo());
        SeccionJpaEntity entity = getOrThrow(id);

        String anioLectivo = normalizeText(dto.anioLectivo());
        String paralelo = normalizeUpper(dto.paralelo());
        if (repository.existsByAnioLectivoAndGradoAndParaleloIgnoreCaseAndIdNot(anioLectivo, dto.grado(), paralelo, id)) {
            throw new BusinessRuleException(
                    SeccionErrorCodes.RN_SEC_04_SECCION_DUPLICADA,
                    "Ya existe otra seccion con esa combinacion."
            );
        }

        mapper.applyUpdate(entity, dto);
        SeccionJpaEntity saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    @Transactional
/**
 * Implementa la operacion 'cambiarEstado' del modulo seccion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public SeccionResponseDto cambiarEstado(Long id, SeccionPatchEstadoRequestDto dto) {
        SeccionJpaEntity entity = getOrThrow(id);
        mapper.applyPatchEstado(entity, dto);
        SeccionJpaEntity saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

/**
 * Metodo de soporte interno 'getOrThrow' para mantener cohesion en SeccionCommandService.
 * Contexto: modulo seccion, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private SeccionJpaEntity getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seccion no encontrada."));
    }

/**
 * Metodo de soporte interno 'normalizeText' para mantener cohesion en SeccionCommandService.
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
 * Metodo de soporte interno 'normalizeUpper' para mantener cohesion en SeccionCommandService.
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
