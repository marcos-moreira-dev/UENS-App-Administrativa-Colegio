package com.marcosmoreiradev.uensbackend.modules.calificacion.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.BusinessRuleException;
import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.CalificacionErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.calificacion.api.dto.CalificacionCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.calificacion.api.dto.CalificacionResponseDto;
import com.marcosmoreiradev.uensbackend.modules.calificacion.api.dto.CalificacionUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.calificacion.application.mapper.CalificacionDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.calificacion.application.validator.CalificacionRequestValidator;
import com.marcosmoreiradev.uensbackend.modules.calificacion.infrastructure.persistence.entity.CalificacionJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.calificacion.infrastructure.persistence.repository.CalificacionJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.clase.infrastructure.persistence.entity.ClaseJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.clase.infrastructure.persistence.repository.ClaseJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.entity.EstudianteJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.repository.EstudianteJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
/**
 * Define la responsabilidad de CalificacionCommandService dentro del backend UENS.
 * Contexto: modulo calificacion, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: ejecutar operaciones de escritura aplicando reglas de negocio y validaciones de fase 1.
 */
public class CalificacionCommandService {

    private final CalificacionJpaRepository calificacionRepository;
    private final EstudianteJpaRepository estudianteRepository;
    private final ClaseJpaRepository claseRepository;
    private final CalificacionDtoMapper mapper;
    private final CalificacionRequestValidator validator;
/**
 * Construye la instancia de CalificacionCommandService para operar en el modulo calificacion.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param calificacionRepository dato de entrada relevante para ejecutar esta operacion: 'calificacionRepository'
     * @param estudianteRepository dato de entrada relevante para ejecutar esta operacion: 'estudianteRepository'
     * @param claseRepository dato de entrada relevante para ejecutar esta operacion: 'claseRepository'
     * @param mapper componente de mapeo entre DTOs, modelos de dominio y entidades
     * @param validator dato de entrada relevante para ejecutar esta operacion: 'validator'
 */

    public CalificacionCommandService(
            CalificacionJpaRepository calificacionRepository,
            EstudianteJpaRepository estudianteRepository,
            ClaseJpaRepository claseRepository,
            CalificacionDtoMapper mapper,
            CalificacionRequestValidator validator
    ) {
        this.calificacionRepository = calificacionRepository;
        this.estudianteRepository = estudianteRepository;
        this.claseRepository = claseRepository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Transactional
/**
 * Implementa la operacion 'crear' del modulo calificacion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public CalificacionResponseDto crear(CalificacionCreateRequestDto dto) {
        validator.validarRangoNota(dto.nota());
        EstudianteJpaEntity estudiante = getEstudianteOrThrow(dto.estudianteId());
        ClaseJpaEntity clase = getClaseOrThrow(dto.claseId());
        validarContextoAcademico(estudiante, clase);

        if (calificacionRepository.existsByEstudiante_IdAndClase_IdAndNumeroParcial(
                dto.estudianteId(), dto.claseId(), dto.numeroParcial()
        )) {
            throw new BusinessRuleException(CalificacionErrorCodes.RN_CAL_02_REGISTRO_DUPLICADO);
        }

        CalificacionJpaEntity saved = calificacionRepository.save(CalificacionJpaEntity.crear(
                dto.numeroParcial(),
                dto.nota(),
                dto.fechaRegistro() == null ? LocalDate.now() : dto.fechaRegistro(),
                normalizeText(dto.observacion()),
                estudiante,
                clase
        ));
        return mapper.toResponseDto(saved);
    }

    @Transactional
/**
 * Implementa la operacion 'actualizar' del modulo calificacion en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public CalificacionResponseDto actualizar(Long id, CalificacionUpdateRequestDto dto) {
        validator.validarRangoNota(dto.nota());
        CalificacionJpaEntity entity = getOrThrow(id);
        EstudianteJpaEntity estudiante = getEstudianteOrThrow(dto.estudianteId());
        ClaseJpaEntity clase = getClaseOrThrow(dto.claseId());
        validarContextoAcademico(estudiante, clase);

        if (calificacionRepository.existsByEstudiante_IdAndClase_IdAndNumeroParcialAndIdNot(
                dto.estudianteId(), dto.claseId(), dto.numeroParcial(), id
        )) {
            throw new BusinessRuleException(
                    CalificacionErrorCodes.RN_CAL_02_REGISTRO_DUPLICADO,
                    "Ya existe otra calificacion para ese estudiante, clase y parcial."
            );
        }

        entity.actualizarDatos(
                dto.numeroParcial(),
                dto.nota(),
                dto.fechaRegistro() == null ? entity.getFechaRegistro() : dto.fechaRegistro(),
                normalizeText(dto.observacion()),
                estudiante,
                clase
        );
        return mapper.toResponseDto(calificacionRepository.save(entity));
    }

/**
 * Metodo de soporte interno 'getOrThrow' para mantener cohesion en CalificacionCommandService.
 * Contexto: modulo calificacion, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private CalificacionJpaEntity getOrThrow(Long id) {
        return calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificacion no encontrada."));
    }

/**
 * Metodo de soporte interno 'getEstudianteOrThrow' para mantener cohesion en CalificacionCommandService.
 * Contexto: modulo calificacion, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private EstudianteJpaEntity getEstudianteOrThrow(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado."));
    }

/**
 * Metodo de soporte interno 'getClaseOrThrow' para mantener cohesion en CalificacionCommandService.
 * Contexto: modulo calificacion, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private ClaseJpaEntity getClaseOrThrow(Long id) {
        return claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada."));
    }

/**
 * Metodo de soporte interno 'validarContextoAcademico' para mantener cohesion en CalificacionCommandService.
 * Contexto: modulo calificacion, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param estudiante dato de entrada relevante para ejecutar esta operacion: 'estudiante'
     * @param clase dato de entrada relevante para ejecutar esta operacion: 'clase'
 */
    private static void validarContextoAcademico(EstudianteJpaEntity estudiante, ClaseJpaEntity clase) {
        if (estudiante.getSeccion() == null || !estudiante.getSeccion().getId().equals(clase.getSeccion().getId())) {
            throw new BusinessRuleException(CalificacionErrorCodes.RN_CAL_01_CONTEXTO_ACADEMICO_INVALIDO);
        }
    }

/**
 * Metodo de soporte interno 'normalizeText' para mantener cohesion en CalificacionCommandService.
 * Contexto: modulo calificacion, capa application, con foco en reglas y consistencia tecnica de fase 1.
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
}
