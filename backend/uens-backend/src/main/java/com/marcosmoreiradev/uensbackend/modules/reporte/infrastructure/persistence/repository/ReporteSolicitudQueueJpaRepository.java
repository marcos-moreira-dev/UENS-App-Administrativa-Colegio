package com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.repository;

import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
/**
 * Define la responsabilidad de ReporteSolicitudQueueJpaRepository dentro del backend UENS.
 * Contexto: modulo reporte, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: encapsular acceso a datos y consultas sobre el esquema relacional V2 3FN.
 */
public interface ReporteSolicitudQueueJpaRepository extends JpaRepository<ReporteSolicitudQueueJpaEntity, Long>, JpaSpecificationExecutor<ReporteSolicitudQueueJpaEntity> {

/**
 * Implementa la operacion 'findByEstadoOrderByFechaSolicitudAsc' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
     * @param pageable dato de entrada relevante para ejecutar esta operacion: 'pageable'
     * @return salida util para continuar con la capa llamadora.
 */
    List<ReporteSolicitudQueueJpaEntity> findByEstadoOrderByFechaSolicitudAsc(String estado, Pageable pageable);
}

