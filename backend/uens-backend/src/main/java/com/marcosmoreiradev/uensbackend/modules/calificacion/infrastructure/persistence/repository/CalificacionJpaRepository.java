package com.marcosmoreiradev.uensbackend.modules.calificacion.infrastructure.persistence.repository;

import com.marcosmoreiradev.uensbackend.modules.calificacion.infrastructure.persistence.entity.CalificacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Define la responsabilidad de CalificacionJpaRepository dentro del backend UENS.
 * Contexto: modulo calificacion, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: encapsular acceso a datos y consultas sobre el esquema relacional V2 3FN.
 */
public interface CalificacionJpaRepository extends JpaRepository<CalificacionJpaEntity, Long>, JpaSpecificationExecutor<CalificacionJpaEntity> {

/**
 * Implementa la operacion 'existsByEstudiante_IdAndClase_IdAndNumeroParcial' del modulo calificacion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param estudianteId identificador del estudiante dentro del dominio academico
     * @param claseId identificador de la clase ofertada por seccion y asignatura
     * @param numeroParcial numero de parcial academico permitido en fase 1 (1 o 2)
     * @return salida util para continuar con la capa llamadora.
 */
    boolean existsByEstudiante_IdAndClase_IdAndNumeroParcial(Long estudianteId, Long claseId, Integer numeroParcial);

    boolean existsByEstudiante_IdAndClase_IdAndNumeroParcialAndIdNot(
            Long estudianteId,
            Long claseId,
            Integer numeroParcial,
            Long id
    );
}

