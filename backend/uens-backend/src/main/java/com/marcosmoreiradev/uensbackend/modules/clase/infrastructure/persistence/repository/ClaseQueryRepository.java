package com.marcosmoreiradev.uensbackend.modules.clase.infrastructure.persistence.repository;

import com.marcosmoreiradev.uensbackend.modules.clase.infrastructure.persistence.entity.ClaseJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

/**
 * Define la responsabilidad de ClaseQueryRepository dentro del backend UENS.
 * Contexto: modulo clase, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: encapsular acceso a datos y consultas sobre el esquema relacional V2 3FN.
 */

public interface ClaseQueryRepository extends Repository<ClaseJpaEntity, Long> {

    @Query("select count(c) from ClaseJpaEntity c where c.estado = :estado")
/**
 * Implementa la operacion 'contarPorEstado' del modulo clase en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
     * @return salida util para continuar con la capa llamadora.
 */
    long contarPorEstado(String estado);
}

