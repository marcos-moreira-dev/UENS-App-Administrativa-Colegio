package com.marcosmoreiradev.uensbackend.modules.asignatura.infrastructure.persistence.repository;

import com.marcosmoreiradev.uensbackend.modules.asignatura.infrastructure.persistence.entity.AsignaturaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
/**
 * Define la responsabilidad de AsignaturaJpaRepository dentro del backend UENS.
 * Contexto: modulo asignatura, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: encapsular acceso a datos y consultas sobre el esquema relacional V2 3FN.
 */
public interface AsignaturaJpaRepository extends JpaRepository<AsignaturaJpaEntity, Long>, JpaSpecificationExecutor<AsignaturaJpaEntity> {

/**
 * Implementa la operacion 'existsByNombreIgnoreCaseAndGrado' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param nombre dato de entrada relevante para ejecutar esta operacion: 'nombre'
     * @param grado grado academico de Educacion General Basica para filtrar/validar datos
     * @return salida util para continuar con la capa llamadora.
 */
    boolean existsByNombreIgnoreCaseAndGrado(String nombre, Integer grado);

/**
 * Implementa la operacion 'existsByNombreIgnoreCaseAndGradoAndIdNot' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param nombre dato de entrada relevante para ejecutar esta operacion: 'nombre'
     * @param grado grado academico de Educacion General Basica para filtrar/validar datos
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return salida util para continuar con la capa llamadora.
 */
    boolean existsByNombreIgnoreCaseAndGradoAndIdNot(String nombre, Integer grado, Long id);

/**
 * Implementa la operacion 'findByIdAndEstado' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
     * @return salida util para continuar con la capa llamadora.
 */
    Optional<AsignaturaJpaEntity> findByIdAndEstado(Long id, String estado);

/**
 * Implementa la operacion 'existsByIdAndEstado' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
     * @return salida util para continuar con la capa llamadora.
 */
    boolean existsByIdAndEstado(Long id, String estado);
}

