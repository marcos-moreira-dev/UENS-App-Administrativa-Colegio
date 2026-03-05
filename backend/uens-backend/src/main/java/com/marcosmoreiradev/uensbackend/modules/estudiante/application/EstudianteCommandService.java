package com.marcosmoreiradev.uensbackend.modules.estudiante.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.BusinessRuleException;
import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.StudentErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.AsignarSeccionVigenteRequestDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudiantePatchEstadoRequestDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteResponseDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.api.dto.EstudianteUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.estudiante.application.mapper.EstudianteDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.estudiante.application.validator.EstudianteRequestValidator;
import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.entity.EstudianteJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.repository.EstudianteJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.representante.infrastructure.persistence.entity.RepresentanteLegalJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.representante.infrastructure.persistence.repository.RepresentanteLegalJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.entity.SeccionJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.repository.SeccionJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
/**
 * Define la responsabilidad de EstudianteCommandService dentro del backend UENS.
 * Contexto: modulo estudiante, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: ejecutar operaciones de escritura aplicando reglas de negocio y validaciones de fase 1.
 */
public class EstudianteCommandService {

    private final EstudianteJpaRepository estudianteRepository;
    private final RepresentanteLegalJpaRepository representanteRepository;
    private final SeccionJpaRepository seccionRepository;
    private final EstudianteDtoMapper mapper;
    private final EstudianteRequestValidator validator;
/**
 * Construye la instancia de EstudianteCommandService para operar en el modulo estudiante.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param estudianteRepository dato de entrada relevante para ejecutar esta operacion: 'estudianteRepository'
     * @param representanteRepository dato de entrada relevante para ejecutar esta operacion: 'representanteRepository'
     * @param seccionRepository dato de entrada relevante para ejecutar esta operacion: 'seccionRepository'
     * @param mapper componente de mapeo entre DTOs, modelos de dominio y entidades
     * @param validator dato de entrada relevante para ejecutar esta operacion: 'validator'
 */

    public EstudianteCommandService(
            EstudianteJpaRepository estudianteRepository,
            RepresentanteLegalJpaRepository representanteRepository,
            SeccionJpaRepository seccionRepository,
            EstudianteDtoMapper mapper,
            EstudianteRequestValidator validator
    ) {
        this.estudianteRepository = estudianteRepository;
        this.representanteRepository = representanteRepository;
        this.seccionRepository = seccionRepository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Transactional
/**
 * Implementa la operacion 'crear' del modulo estudiante en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public EstudianteResponseDto crear(EstudianteCreateRequestDto dto) {
        validator.validarFechaNacimiento(dto.fechaNacimiento());
        String nombres = normalizeText(dto.nombres());
        String apellidos = normalizeText(dto.apellidos());

        if (estudianteRepository.existsByNombresIgnoreCaseAndApellidosIgnoreCaseAndFechaNacimiento(
                nombres, apellidos, dto.fechaNacimiento()
        )) {
            throw new BusinessRuleException(
                    StudentErrorCodes.RN_EST_01_ESTUDIANTE_DUPLICADO,
                    "Ya existe un estudiante con esos datos."
            );
        }

        RepresentanteLegalJpaEntity representante = representanteRepository.findById(dto.representanteLegalId())
                .orElseThrow(() -> new ResourceNotFoundException("Representante legal no encontrado."));

        SeccionJpaEntity seccion = resolverSeccionConCupo(dto.seccionId(), null);

        EstudianteJpaEntity saved = estudianteRepository.save(
                EstudianteJpaEntity.crear(nombres, apellidos, dto.fechaNacimiento(), representante, seccion)
        );
        return mapper.toResponseDto(saved);
    }

    @Transactional
/**
 * Implementa la operacion 'actualizar' del modulo estudiante en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public EstudianteResponseDto actualizar(Long id, EstudianteUpdateRequestDto dto) {
        validator.validarFechaNacimiento(dto.fechaNacimiento());
        EstudianteJpaEntity entity = getOrThrow(id);

        String nombres = normalizeText(dto.nombres());
        String apellidos = normalizeText(dto.apellidos());
        if (estudianteRepository.existsByNombresIgnoreCaseAndApellidosIgnoreCaseAndFechaNacimientoAndIdNot(
                nombres, apellidos, dto.fechaNacimiento(), id
        )) {
            throw new BusinessRuleException(
                    StudentErrorCodes.RN_EST_01_ESTUDIANTE_DUPLICADO,
                    "Ya existe otro estudiante con esos datos."
            );
        }

        RepresentanteLegalJpaEntity representante = representanteRepository.findById(dto.representanteLegalId())
                .orElseThrow(() -> new ResourceNotFoundException("Representante legal no encontrado."));

        SeccionJpaEntity seccion = resolverSeccionConCupo(dto.seccionId(), entity);
        entity.actualizarDatos(nombres, apellidos, dto.fechaNacimiento(), representante, seccion);

        String estado = normalizeUpper(dto.estado());
        if ("INACTIVO".equals(estado)) {
            entity.inactivar();
        } else if ("ACTIVO".equals(estado)) {
            entity.activar();
        }

        return mapper.toResponseDto(estudianteRepository.save(entity));
    }

    @Transactional
/**
 * Implementa la operacion 'cambiarEstado' del modulo estudiante en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public EstudianteResponseDto cambiarEstado(Long id, EstudiantePatchEstadoRequestDto dto) {
        EstudianteJpaEntity entity = getOrThrow(id);
        if ("INACTIVO".equals(normalizeUpper(dto.estado()))) {
            entity.inactivar();
        } else {
            entity.activar();
        }
        return mapper.toResponseDto(estudianteRepository.save(entity));
    }

    @Transactional
/**
 * Implementa la operacion 'asignarSeccionVigente' del modulo estudiante en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public EstudianteResponseDto asignarSeccionVigente(Long id, AsignarSeccionVigenteRequestDto dto) {
        EstudianteJpaEntity entity = getOrThrow(id);
        SeccionJpaEntity seccion = resolverSeccionConCupo(dto.seccionId(), entity);
        entity.asignarSeccion(seccion);
        return mapper.toResponseDto(estudianteRepository.save(entity));
    }

/**
 * Metodo de soporte interno 'getOrThrow' para mantener cohesion en EstudianteCommandService.
 * Contexto: modulo estudiante, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private EstudianteJpaEntity getOrThrow(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado."));
    }

/**
 * Metodo de soporte interno 'resolverSeccionConCupo' para mantener cohesion en EstudianteCommandService.
 * Contexto: modulo estudiante, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param seccionId identificador de la seccion academica (grado/paralelo/anio lectivo)
     * @param actual dato de entrada relevante para ejecutar esta operacion: 'actual'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private SeccionJpaEntity resolverSeccionConCupo(Long seccionId, EstudianteJpaEntity actual) {
        if (seccionId == null) {
            return null;
        }

        SeccionJpaEntity seccion = seccionRepository.findById(seccionId)
                .orElseThrow(() -> new ResourceNotFoundException("Seccion no encontrada."));

        if (!"ACTIVO".equalsIgnoreCase(seccion.getEstado())) {
            throw new BusinessRuleException(StudentErrorCodes.RN_EST_03_SECCION_NO_DISPONIBLE);
        }

        Long seccionActualId = actual != null && actual.getSeccion() != null ? actual.getSeccion().getId() : null;
        if (seccionActualId != null && seccionActualId.equals(seccionId)) {
            return seccion;
        }

        long ocupados = estudianteRepository.countBySeccion_Id(seccionId);
        if (ocupados >= seccion.getCupoMaximo()) {
            throw new BusinessRuleException(StudentErrorCodes.RN_EST_04_CUPO_SECCION_AGOTADO);
        }

        return seccion;
    }

/**
 * Metodo de soporte interno 'normalizeText' para mantener cohesion en EstudianteCommandService.
 * Contexto: modulo estudiante, capa application, con foco en reglas y consistencia tecnica de fase 1.
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
 * Metodo de soporte interno 'normalizeUpper' para mantener cohesion en EstudianteCommandService.
 * Contexto: modulo estudiante, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalizeUpper(String value) {
        String normalized = normalizeText(value);
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
    }
}
