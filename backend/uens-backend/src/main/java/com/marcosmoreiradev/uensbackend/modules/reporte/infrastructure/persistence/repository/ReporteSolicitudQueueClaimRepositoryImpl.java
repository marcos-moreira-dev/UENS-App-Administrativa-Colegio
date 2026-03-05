package com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.repository;

import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
/**
 * Define la responsabilidad de ReporteSolicitudQueueClaimRepositoryImpl dentro del backend UENS.
 * Contexto: modulo reporte, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class ReporteSolicitudQueueClaimRepositoryImpl implements ReporteSolicitudQueueClaimRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
/**
 * Implementa la operacion 'claimPendientes' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param batchSize dato de entrada relevante para ejecutar esta operacion: 'batchSize'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public List<ReporteSolicitudQueueJpaEntity> claimPendientes(int batchSize) {
        int safeBatch = Math.max(batchSize, 0);
        if (safeBatch == 0) {
            return List.of();
        }

        // PostgreSQL path: lock + claim atomico + returning ids.
        try {
            @SuppressWarnings("unchecked")
            List<Number> rawIds = entityManager.createNativeQuery("""
                    WITH candidates AS (
                        SELECT pk_id
                        FROM reporte_solicitud_queue
                        WHERE estado = 'PENDIENTE'
                        ORDER BY fecha_solicitud ASC
                        FOR UPDATE SKIP LOCKED
                        LIMIT :batchSize
                    )
                    UPDATE reporte_solicitud_queue q
                    SET estado = 'EN_PROCESO',
                        intentos = q.intentos + 1,
                        fecha_actualizacion = CURRENT_TIMESTAMP
                    FROM candidates c
                    WHERE q.pk_id = c.pk_id
                    RETURNING q.pk_id
                    """)
                    .setParameter("batchSize", safeBatch)
                    .getResultList();

            if (rawIds.isEmpty()) {
                return List.of();
            }

            List<Long> ids = new ArrayList<>(rawIds.size());
            for (Number rawId : rawIds) {
                ids.add(rawId.longValue());
            }

            return entityManager.createQuery("""
                            select r
                            from ReporteSolicitudQueueJpaEntity r
                            where r.id in :ids
                            order by r.fechaSolicitud asc
                            """, ReporteSolicitudQueueJpaEntity.class)
                    .setParameter("ids", ids)
                    .getResultList();
        } catch (RuntimeException ex) {
            // Fallback compatible para motores que no soporten SKIP LOCKED/RETURNING.
            List<ReporteSolicitudQueueJpaEntity> pendientes = entityManager.createQuery("""
                            select r
                            from ReporteSolicitudQueueJpaEntity r
                            where r.estado = 'PENDIENTE'
                            order by r.fechaSolicitud asc
                            """, ReporteSolicitudQueueJpaEntity.class)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .setMaxResults(safeBatch)
                    .getResultList();

            for (ReporteSolicitudQueueJpaEntity pendiente : pendientes) {
                pendiente.marcarEnProceso();
            }
            entityManager.flush();
            return pendientes;
        }
    }
}

