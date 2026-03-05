package com.marcosmoreiradev.uensbackend.modules.representante.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.BusinessRuleException;
import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.RepresentanteErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.representante.api.dto.RepresentanteLegalCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.representante.api.dto.RepresentanteLegalResponseDto;
import com.marcosmoreiradev.uensbackend.modules.representante.api.dto.RepresentanteLegalUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.representante.application.mapper.RepresentanteLegalDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.representante.infrastructure.persistence.entity.RepresentanteLegalJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.representante.infrastructure.persistence.repository.RepresentanteLegalJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
/**
 * Define la responsabilidad de RepresentanteLegalCommandService dentro del backend UENS.
 * Contexto: modulo representante, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: ejecutar operaciones de escritura aplicando reglas de negocio y validaciones de fase 1.
 */
public class RepresentanteLegalCommandService {

    private final RepresentanteLegalJpaRepository repository;
    private final RepresentanteLegalDtoMapper mapper;
/**
 * Construye la instancia de RepresentanteLegalCommandService para operar en el modulo representante.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param repository adaptador de persistencia que opera sobre tablas del esquema V2 3FN
     * @param mapper componente de mapeo entre DTOs, modelos de dominio y entidades
 */

    public RepresentanteLegalCommandService(RepresentanteLegalJpaRepository repository, RepresentanteLegalDtoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
/**
 * Implementa la operacion 'crear' del modulo representante en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public RepresentanteLegalResponseDto crear(RepresentanteLegalCreateRequestDto dto) {
        String correo = normalizeEmail(dto.correoElectronico());
        if (correo != null && repository.existsByCorreoElectronicoIgnoreCase(correo)) {
            throw new BusinessRuleException(RepresentanteErrorCodes.RN_RLG_01_CORREO_DUPLICADO);
        }

        RepresentanteLegalJpaEntity saved = repository.save(mapper.toEntityForCreate(dto));
        return mapper.toResponseDto(saved);
    }

    @Transactional
/**
 * Implementa la operacion 'actualizar' del modulo representante en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public RepresentanteLegalResponseDto actualizar(Long id, RepresentanteLegalUpdateRequestDto dto) {
        RepresentanteLegalJpaEntity entity = getOrThrow(id);

        String correo = normalizeEmail(dto.correoElectronico());
        if (correo != null && repository.existsByCorreoElectronicoIgnoreCaseAndIdNot(correo, id)) {
            throw new BusinessRuleException(
                    RepresentanteErrorCodes.RN_RLG_01_CORREO_DUPLICADO,
                    "Ya existe otro representante con el mismo correo."
            );
        }

        mapper.applyUpdate(entity, dto);
        return mapper.toResponseDto(repository.save(entity));
    }

/**
 * Metodo de soporte interno 'getOrThrow' para mantener cohesion en RepresentanteLegalCommandService.
 * Contexto: modulo representante, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private RepresentanteLegalJpaEntity getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Representante legal no encontrado."));
    }

/**
 * Metodo de soporte interno 'normalizeEmail' para mantener cohesion en RepresentanteLegalCommandService.
 * Contexto: modulo representante, capa application, con foco en reglas y consistencia tecnica de fase 1.
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
