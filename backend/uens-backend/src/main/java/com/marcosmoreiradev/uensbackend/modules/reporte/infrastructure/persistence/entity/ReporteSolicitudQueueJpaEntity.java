package com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reporte_solicitud_queue")
/**
 * Define la responsabilidad de ReporteSolicitudQueueJpaEntity dentro del backend UENS.
 * Contexto: modulo reporte, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: representar el estado persistente alineado a tablas y restricciones del SQL oficial.
 */
public class ReporteSolicitudQueueJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id", nullable = false)
    private Long id;

    @Column(name = "tipo_reporte", nullable = false, length = 100)
    private String tipoReporte;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "parametros_json", columnDefinition = "TEXT")
    private String parametrosJson;

    @Column(name = "resultado_json", columnDefinition = "TEXT")
    private String resultadoJson;

    @Column(name = "error_detalle", columnDefinition = "TEXT")
    private String errorDetalle;

    @Column(name = "solicitado_por_usuario")
    private Long solicitadoPorUsuario;

    @Column(name = "intentos", nullable = false)
    private Integer intentos;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
/**
 * Construye la instancia de ReporteSolicitudQueueJpaEntity para operar en el modulo reporte.
 * Contexto: capa infrastructure con dependencias inyectadas segun la arquitectura modular UENS.
 */

    protected ReporteSolicitudQueueJpaEntity() {
    }

/**
 * Construye la instancia de ReporteSolicitudQueueJpaEntity para operar en el modulo reporte.
 * Contexto: capa infrastructure con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param tipoReporte tipo funcional de reporte solicitado para procesamiento asincrono
     * @param parametrosJson dato de entrada relevante para ejecutar esta operacion: 'parametrosJson'
     * @param solicitadoPorUsuario dato de entrada relevante para ejecutar esta operacion: 'solicitadoPorUsuario'
 */
    private ReporteSolicitudQueueJpaEntity(String tipoReporte, String parametrosJson, Long solicitadoPorUsuario) {
        this.tipoReporte = tipoReporte;
        this.parametrosJson = parametrosJson;
        this.solicitadoPorUsuario = solicitadoPorUsuario;
        this.estado = "PENDIENTE";
        this.intentos = 0;
    }

/**
 * Implementa la operacion 'crear' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param tipoReporte tipo funcional de reporte solicitado para procesamiento asincrono
     * @param parametrosJson dato de entrada relevante para ejecutar esta operacion: 'parametrosJson'
     * @param solicitadoPorUsuario dato de entrada relevante para ejecutar esta operacion: 'solicitadoPorUsuario'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static ReporteSolicitudQueueJpaEntity crear(String tipoReporte, String parametrosJson, Long solicitadoPorUsuario) {
        return new ReporteSolicitudQueueJpaEntity(tipoReporte, parametrosJson, solicitadoPorUsuario);
    }

    @PrePersist
/**
 * Implementa la operacion 'prePersist' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.fechaSolicitud = now;
        this.fechaActualizacion = now;
    }

    @PreUpdate
/**
 * Implementa la operacion 'preUpdate' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

/**
 * Implementa la operacion 'getId' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Long getId() {
        return id;
    }

/**
 * Implementa la operacion 'getTipoReporte' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getTipoReporte() {
        return tipoReporte;
    }

/**
 * Implementa la operacion 'getEstado' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getEstado() {
        return estado;
    }

/**
 * Implementa la operacion 'getParametrosJson' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getParametrosJson() {
        return parametrosJson;
    }

/**
 * Implementa la operacion 'getResultadoJson' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getResultadoJson() {
        return resultadoJson;
    }

/**
 * Implementa la operacion 'getErrorDetalle' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getErrorDetalle() {
        return errorDetalle;
    }

/**
 * Implementa la operacion 'getSolicitadoPorUsuario' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Long getSolicitadoPorUsuario() {
        return solicitadoPorUsuario;
    }

/**
 * Implementa la operacion 'getIntentos' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Integer getIntentos() {
        return intentos;
    }

/**
 * Implementa la operacion 'getFechaSolicitud' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

/**
 * Implementa la operacion 'getFechaActualizacion' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

/**
 * Implementa la operacion 'marcarEnProceso' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void marcarEnProceso() {
        this.estado = "EN_PROCESO";
        this.intentos = this.intentos + 1;
    }

/**
 * Implementa la operacion 'marcarCompletada' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param resultadoJson dato de entrada relevante para ejecutar esta operacion: 'resultadoJson'
 */
    public void marcarCompletada(String resultadoJson) {
        this.estado = "COMPLETADA";
        this.resultadoJson = resultadoJson;
        this.errorDetalle = null;
    }

/**
 * Implementa la operacion 'marcarError' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param errorDetalle dato de entrada relevante para ejecutar esta operacion: 'errorDetalle'
 */
    public void marcarError(String errorDetalle) {
        this.estado = "ERROR";
        this.errorDetalle = errorDetalle;
    }

/**
 * Implementa la operacion 'marcarPendienteParaReintento' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param errorDetalle dato de entrada relevante para ejecutar esta operacion: 'errorDetalle'
 */
    public void marcarPendienteParaReintento(String errorDetalle) {
        this.estado = "PENDIENTE";
        this.errorDetalle = errorDetalle;
    }

/**
 * Implementa la operacion 'reencolar' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void reencolar() {
        this.estado = "PENDIENTE";
        this.errorDetalle = null;
    }

    @Override
/**
 * Implementa la operacion 'equals' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param o dato de entrada relevante para ejecutar esta operacion: 'o'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReporteSolicitudQueueJpaEntity that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
/**
 * Implementa la operacion 'hashCode' del modulo reporte en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public int hashCode() {
        return 31;
    }
}

