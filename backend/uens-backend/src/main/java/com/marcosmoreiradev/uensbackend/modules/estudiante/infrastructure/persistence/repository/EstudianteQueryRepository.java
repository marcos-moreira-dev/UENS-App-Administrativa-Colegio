package com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.repository;

import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.entity.EstudianteJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

/**
 * Define la responsabilidad de EstudianteQueryRepository dentro del backend UENS.
 * Contexto: modulo estudiante, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: encapsular acceso a datos y consultas sobre el esquema relacional V2 3FN.
 */

public interface EstudianteQueryRepository extends Repository<EstudianteJpaEntity, Long> {

    @Query("select count(e) from EstudianteJpaEntity e where e.estado = :estado")
/**
 * Implementa la operacion 'contarPorEstado' del modulo estudiante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
     * @return salida util para continuar con la capa llamadora.
 */
    long contarPorEstado(String estado);
}

