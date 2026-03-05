package com.marcosmoreiradev.uensbackend.modules.dashboard.application;

import com.marcosmoreiradev.uensbackend.modules.asignatura.infrastructure.persistence.repository.AsignaturaJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.calificacion.infrastructure.persistence.repository.CalificacionJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.clase.infrastructure.persistence.repository.ClaseJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.dashboard.api.dto.DashboardResumenResponseDto;
import com.marcosmoreiradev.uensbackend.modules.dashboard.application.mapper.DashboardDtoMapper;
import com.marcosmoreiradev.uensbackend.modules.docente.infrastructure.persistence.repository.DocenteJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.repository.EstudianteJpaRepository;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.repository.SeccionJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * Define la responsabilidad de DashboardQueryService dentro del backend UENS.
 * Contexto: modulo dashboard, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: resolver consultas con filtros, paginacion y ordenamiento consistente para clientes administrativos.
 */
public class DashboardQueryService {

    private final EstudianteJpaRepository estudianteRepository;
    private final DocenteJpaRepository docenteRepository;
    private final SeccionJpaRepository seccionRepository;
    private final AsignaturaJpaRepository asignaturaRepository;
    private final ClaseJpaRepository claseRepository;
    private final CalificacionJpaRepository calificacionRepository;
    private final DashboardDtoMapper mapper;
/**
 * Construye la instancia de DashboardQueryService para operar en el modulo dashboard.
 * Contexto: capa application con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param estudianteRepository dato de entrada relevante para ejecutar esta operacion: 'estudianteRepository'
     * @param docenteRepository dato de entrada relevante para ejecutar esta operacion: 'docenteRepository'
     * @param seccionRepository dato de entrada relevante para ejecutar esta operacion: 'seccionRepository'
     * @param asignaturaRepository dato de entrada relevante para ejecutar esta operacion: 'asignaturaRepository'
     * @param claseRepository dato de entrada relevante para ejecutar esta operacion: 'claseRepository'
     * @param calificacionRepository dato de entrada relevante para ejecutar esta operacion: 'calificacionRepository'
     * @param mapper componente de mapeo entre DTOs, modelos de dominio y entidades
 */

    public DashboardQueryService(
            EstudianteJpaRepository estudianteRepository,
            DocenteJpaRepository docenteRepository,
            SeccionJpaRepository seccionRepository,
            AsignaturaJpaRepository asignaturaRepository,
            ClaseJpaRepository claseRepository,
            CalificacionJpaRepository calificacionRepository,
            DashboardDtoMapper mapper
    ) {
        this.estudianteRepository = estudianteRepository;
        this.docenteRepository = docenteRepository;
        this.seccionRepository = seccionRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.claseRepository = claseRepository;
        this.calificacionRepository = calificacionRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
/**
 * Implementa la operacion 'obtenerResumen' del modulo dashboard en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public DashboardResumenResponseDto obtenerResumen() {
        return mapper.toResumenDto(
                estudianteRepository.count(),
                docenteRepository.count(),
                seccionRepository.count(),
                asignaturaRepository.count(),
                claseRepository.count(),
                calificacionRepository.count()
        );
    }
}

