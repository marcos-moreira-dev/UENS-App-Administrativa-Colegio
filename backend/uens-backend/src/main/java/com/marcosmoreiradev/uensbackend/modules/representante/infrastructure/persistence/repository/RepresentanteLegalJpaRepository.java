package com.marcosmoreiradev.uensbackend.modules.representante.infrastructure.persistence.repository;

import com.marcosmoreiradev.uensbackend.modules.representante.infrastructure.persistence.entity.RepresentanteLegalJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Define la responsabilidad de RepresentanteLegalJpaRepository dentro del backend UENS.
 * Contexto: modulo representante, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: encapsular acceso a datos y consultas sobre el esquema relacional V2 3FN.
 */

public interface RepresentanteLegalJpaRepository extends JpaRepository<RepresentanteLegalJpaEntity, Long>, JpaSpecificationExecutor<RepresentanteLegalJpaEntity> {

/**
 * Implementa la operacion 'existsByCorreoElectronicoIgnoreCase' del modulo representante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param correoElectronico dato de entrada relevante para ejecutar esta operacion: 'correoElectronico'
     * @return salida util para continuar con la capa llamadora.
 */
    boolean existsByCorreoElectronicoIgnoreCase(String correoElectronico);

/**
 * Implementa la operacion 'existsByCorreoElectronicoIgnoreCaseAndIdNot' del modulo representante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param correoElectronico dato de entrada relevante para ejecutar esta operacion: 'correoElectronico'
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return salida util para continuar con la capa llamadora.
 */
    boolean existsByCorreoElectronicoIgnoreCaseAndIdNot(String correoElectronico, Long id);
}

