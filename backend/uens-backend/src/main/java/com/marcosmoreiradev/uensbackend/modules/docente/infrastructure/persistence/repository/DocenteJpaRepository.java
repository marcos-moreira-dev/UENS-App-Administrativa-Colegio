package com.marcosmoreiradev.uensbackend.modules.docente.infrastructure.persistence.repository;

import com.marcosmoreiradev.uensbackend.modules.docente.infrastructure.persistence.entity.DocenteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Define la responsabilidad de DocenteJpaRepository dentro del backend UENS.
 * Contexto: modulo docente, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: encapsular acceso a datos y consultas sobre el esquema relacional V2 3FN.
 */
public interface DocenteJpaRepository extends JpaRepository<DocenteJpaEntity, Long>, JpaSpecificationExecutor<DocenteJpaEntity> {

/**
 * Implementa la operacion 'existsByCorreoElectronicoIgnoreCase' del modulo docente en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param correoElectronico dato de entrada relevante para ejecutar esta operacion: 'correoElectronico'
     * @return salida util para continuar con la capa llamadora.
 */
    boolean existsByCorreoElectronicoIgnoreCase(String correoElectronico);

/**
 * Implementa la operacion 'existsByCorreoElectronicoIgnoreCaseAndIdNot' del modulo docente en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param correoElectronico dato de entrada relevante para ejecutar esta operacion: 'correoElectronico'
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return salida util para continuar con la capa llamadora.
 */
    boolean existsByCorreoElectronicoIgnoreCaseAndIdNot(String correoElectronico, Long id);
}

