package com.marcosmoreiradev.uensbackend.modules.clase.application;

import com.marcosmoreiradev.uensbackend.common.exception.base.BusinessRuleException;
import com.marcosmoreiradev.uensbackend.common.exception.base.ResourceNotFoundException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ClaseErrorCodes;
import com.marcosmoreiradev.uensbackend.modules.asignatura.infrastructure.persistence.entity.AsignaturaJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.asignatura.infrastructure.persistence.repository.AsignaturaJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.clase.api.dto.ClaseCreateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.clase.api.dto.ClasePatchEstadoRequestDto;
import com.marcosmoreiradev.uensbackend.modules.clase.api.dto.ClaseResponseDto;
import com.marcosmoreiradev.uensbackend.modules.clase.api.dto.ClaseUpdateRequestDto;
import com.marcosmoreiradev.uensbackend.modules.clase.application.mapper.ClaseDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.clase.application.validator.ClaseRequestValidator;
import com.marcosmoreiradev.uensbackend.modules.clase.infrastructure.persistence.entity.ClaseJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.clase.infrastructure.persistence.repository.ClaseJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.docente.infrastructure.persistence.entity.DocenteJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.docente.infrastructure.persistence.repository.DocenteJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.entity.SeccionJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.repository.SeccionJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
/**
 * Define la responsabilidad de ClaseCommandService dentro del backend UENS.
 * Contexto: modulo clase, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: ejecutar operaciones de escritura aplicando reglas de negocio y validaciones de fase 1.
 */
public class ClaseCommandService {

    private final ClaseJpaRepository claseRepository;
    private final SeccionJpaRepository seccionRepository;
    private final AsignaturaJpaRepository asignaturaRepository;
    private final DocenteJpaRepository docenteRepository;
    private final ClaseDtoMapper mapper;
    private final ClaseRequestValidator validator;
/**
 * Construye la instancia de ClaseCommandService para operar en el modulo clase.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param claseRepository dato de entrada relevante para ejecutar esta operacion: 'claseRepository'
     * @param seccionRepository dato de entrada relevante para ejecutar esta operacion: 'seccionRepository'
     * @param asignaturaRepository dato de entrada relevante para ejecutar esta operacion: 'asignaturaRepository'
     * @param docenteRepository dato de entrada relevante para ejecutar esta operacion: 'docenteRepository'
     * @param mapper componente de mapeo entre DTOs, modelos de dominio y entidades
     * @param validator dato de entrada relevante para ejecutar esta operacion: 'validator'
 */

    public ClaseCommandService(
            ClaseJpaRepository claseRepository,
            SeccionJpaRepository seccionRepository,
            AsignaturaJpaRepository asignaturaRepository,
            DocenteJpaRepository docenteRepository,
            ClaseDtoMapper mapper,
            ClaseRequestValidator validator
    ) {
        this.claseRepository = claseRepository;
        this.seccionRepository = seccionRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.docenteRepository = docenteRepository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Transactional
/**
 * Implementa la operacion 'crear' del modulo clase en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ClaseResponseDto crear(ClaseCreateRequestDto dto) {
        validator.validarRangoHorario(dto.horaInicio(), dto.horaFin());
        String diaSemana = normalizeUpper(dto.diaSemana());

        SeccionJpaEntity seccion = getSeccionActivaOrThrow(dto.seccionId());
        AsignaturaJpaEntity asignatura = getAsignaturaActivaOrThrow(dto.asignaturaId());
        validarCoherenciaGrado(seccion, asignatura);
        DocenteJpaEntity docente = getDocenteActivoOrNull(dto.docenteId());

        validarReglasOperacionCreacion(
                dto.seccionId(), dto.asignaturaId(), dto.docenteId(), diaSemana, dto.horaInicio(), dto.horaFin()
        );

        ClaseJpaEntity saved = claseRepository.save(
                ClaseJpaEntity.crear(seccion, asignatura, docente, diaSemana, dto.horaInicio(), dto.horaFin())
        );
        return mapper.toResponseDto(saved);
    }

    @Transactional
/**
 * Implementa la operacion 'actualizar' del modulo clase en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ClaseResponseDto actualizar(Long id, ClaseUpdateRequestDto dto) {
        validator.validarRangoHorario(dto.horaInicio(), dto.horaFin());
        ClaseJpaEntity entity = getOrThrow(id);
        String diaSemana = normalizeUpper(dto.diaSemana());

        SeccionJpaEntity seccion = getSeccionActivaOrThrow(dto.seccionId());
        AsignaturaJpaEntity asignatura = getAsignaturaActivaOrThrow(dto.asignaturaId());
        validarCoherenciaGrado(seccion, asignatura);
        DocenteJpaEntity docente = getDocenteActivoOrNull(dto.docenteId());

        validarReglasOperacionActualizacion(
                id, dto.seccionId(), dto.asignaturaId(), dto.docenteId(), diaSemana, dto.horaInicio(), dto.horaFin()
        );

        entity.actualizarDatos(seccion, asignatura, docente, diaSemana, dto.horaInicio(), dto.horaFin());
        String estado = normalizeUpper(dto.estado());
        if ("INACTIVO".equals(estado)) {
            entity.inactivar();
        } else if ("ACTIVO".equals(estado)) {
            entity.activar();
        }
        return mapper.toResponseDto(claseRepository.save(entity));
    }

    @Transactional
/**
 * Implementa la operacion 'cambiarEstado' del modulo clase en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param dto objeto de transferencia con datos ya validados para ejecutar el caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ClaseResponseDto cambiarEstado(Long id, ClasePatchEstadoRequestDto dto) {
        ClaseJpaEntity entity = getOrThrow(id);
        if ("INACTIVO".equals(normalizeUpper(dto.estado()))) {
            entity.inactivar();
        } else {
            entity.activar();
        }
        return mapper.toResponseDto(claseRepository.save(entity));
    }
/**
 * Metodo de soporte interno 'validarReglasOperacionCreacion' para mantener cohesion en ClaseCommandService.
 * Contexto: modulo clase, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param seccionId identificador de la seccion academica (grado/paralelo/anio lectivo)
     * @param asignaturaId identificador de la asignatura dentro del catalogo academico
     * @param docenteId identificador del docente dentro del dominio academico
     * @param diaSemana dato de entrada relevante para ejecutar esta operacion: 'diaSemana'
     * @param horaInicio dato de entrada relevante para ejecutar esta operacion: 'horaInicio'
     * @param horaFin dato de entrada relevante para ejecutar esta operacion: 'horaFin'
 */

    private void validarReglasOperacionCreacion(
            Long seccionId,
            Long asignaturaId,
            Long docenteId,
            String diaSemana,
            java.time.LocalTime horaInicio,
            java.time.LocalTime horaFin
    ) {
        if (claseRepository.existsBySeccion_IdAndAsignatura_IdAndDiaSemanaAndHoraInicioAndHoraFin(
                seccionId, asignaturaId, diaSemana, horaInicio, horaFin
        )) {
            throw new BusinessRuleException(ClaseErrorCodes.RN_CLA_01_REGISTRO_DUPLICADO);
        }

        if (claseRepository.existsBySeccion_IdAndDiaSemanaAndHoraInicioLessThanAndHoraFinGreaterThan(
                seccionId, diaSemana, horaFin, horaInicio
        )) {
            throw new BusinessRuleException(ClaseErrorCodes.RN_CLA_04_SOLAPAMIENTO_HORARIO_SECCION);
        }

        if (docenteId != null && claseRepository.existsByDocente_IdAndDiaSemanaAndHoraInicioLessThanAndHoraFinGreaterThan(
                docenteId, diaSemana, horaFin, horaInicio
        )) {
            throw new BusinessRuleException(ClaseErrorCodes.RN_CLA_03_SOLAPAMIENTO_HORARIO_DOCENTE);
        }
    }
/**
 * Metodo de soporte interno 'validarReglasOperacionActualizacion' para mantener cohesion en ClaseCommandService.
 * Contexto: modulo clase, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param seccionId identificador de la seccion academica (grado/paralelo/anio lectivo)
     * @param asignaturaId identificador de la asignatura dentro del catalogo academico
     * @param docenteId identificador del docente dentro del dominio academico
     * @param diaSemana dato de entrada relevante para ejecutar esta operacion: 'diaSemana'
     * @param horaInicio dato de entrada relevante para ejecutar esta operacion: 'horaInicio'
     * @param horaFin dato de entrada relevante para ejecutar esta operacion: 'horaFin'
 */

    private void validarReglasOperacionActualizacion(
            Long id,
            Long seccionId,
            Long asignaturaId,
            Long docenteId,
            String diaSemana,
            java.time.LocalTime horaInicio,
            java.time.LocalTime horaFin
    ) {
        if (claseRepository.existsBySeccion_IdAndAsignatura_IdAndDiaSemanaAndHoraInicioAndHoraFinAndIdNot(
                seccionId, asignaturaId, diaSemana, horaInicio, horaFin, id
        )) {
            throw new BusinessRuleException(ClaseErrorCodes.RN_CLA_01_REGISTRO_DUPLICADO);
        }

        if (claseRepository.existsBySeccion_IdAndDiaSemanaAndHoraInicioLessThanAndHoraFinGreaterThanAndIdNot(
                seccionId, diaSemana, horaFin, horaInicio, id
        )) {
            throw new BusinessRuleException(ClaseErrorCodes.RN_CLA_04_SOLAPAMIENTO_HORARIO_SECCION);
        }

        if (docenteId != null && claseRepository.existsByDocente_IdAndDiaSemanaAndHoraInicioLessThanAndHoraFinGreaterThanAndIdNot(
                docenteId, diaSemana, horaFin, horaInicio, id
        )) {
            throw new BusinessRuleException(ClaseErrorCodes.RN_CLA_03_SOLAPAMIENTO_HORARIO_DOCENTE);
        }
    }

/**
 * Metodo de soporte interno 'getOrThrow' para mantener cohesion en ClaseCommandService.
 * Contexto: modulo clase, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private ClaseJpaEntity getOrThrow(Long id) {
        return claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada."));
    }

/**
 * Metodo de soporte interno 'getSeccionActivaOrThrow' para mantener cohesion en ClaseCommandService.
 * Contexto: modulo clase, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private SeccionJpaEntity getSeccionActivaOrThrow(Long id) {
        SeccionJpaEntity seccion = seccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seccion no encontrada."));
        if (!"ACTIVO".equalsIgnoreCase(seccion.getEstado())) {
            throw new BusinessRuleException(ClaseErrorCodes.RN_CLA_06_SECCION_NO_DISPONIBLE);
        }
        return seccion;
    }

/**
 * Metodo de soporte interno 'getAsignaturaActivaOrThrow' para mantener cohesion en ClaseCommandService.
 * Contexto: modulo clase, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private AsignaturaJpaEntity getAsignaturaActivaOrThrow(Long id) {
        AsignaturaJpaEntity asignatura = asignaturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura no encontrada."));
        if (!"ACTIVO".equalsIgnoreCase(asignatura.getEstado())) {
            throw new BusinessRuleException(ClaseErrorCodes.RN_CLA_07_ASIGNATURA_NO_DISPONIBLE);
        }
        return asignatura;
    }

/**
 * Metodo de soporte interno 'getDocenteActivoOrNull' para mantener cohesion en ClaseCommandService.
 * Contexto: modulo clase, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private DocenteJpaEntity getDocenteActivoOrNull(Long id) {
        if (id == null) {
            return null;
        }
        DocenteJpaEntity docente = docenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado."));
        if (!"ACTIVO".equalsIgnoreCase(docente.getEstado())) {
            throw new BusinessRuleException(ClaseErrorCodes.RN_CLA_08_DOCENTE_NO_DISPONIBLE);
        }
        return docente;
    }

/**
 * Metodo de soporte interno 'validarCoherenciaGrado' para mantener cohesion en ClaseCommandService.
 * Contexto: modulo clase, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param seccion dato de entrada relevante para ejecutar esta operacion: 'seccion'
     * @param asignatura dato de entrada relevante para ejecutar esta operacion: 'asignatura'
 */
    private static void validarCoherenciaGrado(SeccionJpaEntity seccion, AsignaturaJpaEntity asignatura) {
        if (!seccion.getGrado().equals(asignatura.getGrado())) {
            throw new BusinessRuleException(
                    ClaseErrorCodes.RN_CLA_02_CONTEXTO_ACADEMICO_INVALIDO,
                    "La relacion academica seccion-asignatura no es valida."
            );
        }
    }

/**
 * Metodo de soporte interno 'normalizeUpper' para mantener cohesion en ClaseCommandService.
 * Contexto: modulo clase, capa application, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalizeUpper(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized.toUpperCase(Locale.ROOT);
    }
}
