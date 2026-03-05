package com.marcosmoreiradev.uensbackend.modules.reporte.application.mapper;

import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.ReporteSolicitudCreadaResponseDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.ReporteSolicitudDetalleResponseDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.ReporteSolicitudListItemDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.api.dto.ReporteSolicitudResultadoResponseDto;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import org.springframework.stereotype.Component;

@Component
/**
 * Define la responsabilidad de ReporteSolicitudDtoMapper dentro del backend UENS.
 * Contexto: modulo reporte, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: traducir estructuras entre DTOs API, modelos de aplicacion y entidades persistentes.
 */
public class ReporteSolicitudDtoMapper {

/**
 * Implementa la operacion 'toCreadaDto' del modulo reporte en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ReporteSolicitudCreadaResponseDto toCreadaDto(ReporteSolicitudQueueJpaEntity entity) {
        return new ReporteSolicitudCreadaResponseDto(
                entity.getId(),
                entity.getTipoReporte(),
                entity.getEstado(),
                entity.getFechaSolicitud()
        );
    }

/**
 * Implementa la operacion 'toListItemDto' del modulo reporte en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ReporteSolicitudListItemDto toListItemDto(ReporteSolicitudQueueJpaEntity entity) {
        return new ReporteSolicitudListItemDto(
                entity.getId(),
                entity.getTipoReporte(),
                entity.getEstado(),
                entity.getFechaSolicitud(),
                entity.getFechaActualizacion(),
                entity.getIntentos()
        );
    }

/**
 * Implementa la operacion 'toDetalleDto' del modulo reporte en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ReporteSolicitudDetalleResponseDto toDetalleDto(ReporteSolicitudQueueJpaEntity entity) {
        return new ReporteSolicitudDetalleResponseDto(
                entity.getId(),
                entity.getTipoReporte(),
                entity.getEstado(),
                entity.getParametrosJson(),
                entity.getResultadoJson(),
                entity.getErrorDetalle(),
                entity.getIntentos(),
                entity.getFechaSolicitud(),
                entity.getFechaActualizacion()
        );
    }

/**
 * Implementa la operacion 'toResultadoDto' del modulo reporte en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param entity dato de entrada relevante para ejecutar esta operacion: 'entity'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ReporteSolicitudResultadoResponseDto toResultadoDto(ReporteSolicitudQueueJpaEntity entity) {
        return new ReporteSolicitudResultadoResponseDto(
                entity.getId(),
                entity.getEstado(),
                entity.getResultadoJson(),
                entity.getErrorDetalle()
        );
    }
}

