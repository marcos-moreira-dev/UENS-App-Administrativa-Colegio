package com.marcosmoreiradev.uensbackend.modules.clase.infrastructure.persistence.entity;

import com.marcosmoreiradev.uensbackend.modules.asignatura.infrastructure.persistence.entity.AsignaturaJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.docente.infrastructure.persistence.entity.DocenteJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.entity.SeccionJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(
        name = "clase",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_clase_operativa",
                        columnNames = {"seccion_id", "asignatura_id", "dia_semana", "hora_inicio", "hora_fin"}
                )
        }
)
/**
 * Define la responsabilidad de ClaseJpaEntity dentro del backend UENS.
 * Contexto: modulo clase, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: representar el estado persistente alineado a tablas y restricciones del SQL oficial.
 */
public class ClaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id", nullable = false)
    private Long id;

    @Column(name = "dia_semana", nullable = false, length = 15)
    private String diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seccion_id", nullable = false)
    private SeccionJpaEntity seccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignatura_id", nullable = false)
    private AsignaturaJpaEntity asignatura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private DocenteJpaEntity docente;
/**
 * Construye la instancia de ClaseJpaEntity para operar en el modulo clase.
 * Contexto: capa infrastructure con dependencias inyectadas segun la arquitectura modular UENS.
 */

    protected ClaseJpaEntity() {
    }

    private ClaseJpaEntity(
            SeccionJpaEntity seccion,
            AsignaturaJpaEntity asignatura,
            DocenteJpaEntity docente,
            String diaSemana,
            LocalTime horaInicio,
            LocalTime horaFin,
            String estado
    ) {
        this.seccion = seccion;
        this.asignatura = asignatura;
        this.docente = docente;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
    }
/**
 * Implementa la operacion 'crear' del modulo clase en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param seccion dato de entrada relevante para ejecutar esta operacion: 'seccion'
     * @param asignatura dato de entrada relevante para ejecutar esta operacion: 'asignatura'
     * @param docente dato de entrada relevante para ejecutar esta operacion: 'docente'
     * @param diaSemana dato de entrada relevante para ejecutar esta operacion: 'diaSemana'
     * @param horaInicio dato de entrada relevante para ejecutar esta operacion: 'horaInicio'
     * @param horaFin dato de entrada relevante para ejecutar esta operacion: 'horaFin'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */

    public static ClaseJpaEntity crear(
            SeccionJpaEntity seccion,
            AsignaturaJpaEntity asignatura,
            DocenteJpaEntity docente,
            String diaSemana,
            LocalTime horaInicio,
            LocalTime horaFin
    ) {
        return new ClaseJpaEntity(seccion, asignatura, docente, diaSemana, horaInicio, horaFin, "ACTIVO");
    }

/**
 * Implementa la operacion 'getId' del modulo clase en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Long getId() {
        return id;
    }

/**
 * Implementa la operacion 'getDiaSemana' del modulo clase en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getDiaSemana() {
        return diaSemana;
    }

/**
 * Implementa la operacion 'getHoraInicio' del modulo clase en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public LocalTime getHoraInicio() {
        return horaInicio;
    }

/**
 * Implementa la operacion 'getHoraFin' del modulo clase en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public LocalTime getHoraFin() {
        return horaFin;
    }

/**
 * Implementa la operacion 'getEstado' del modulo clase en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getEstado() {
        return estado;
    }

/**
 * Implementa la operacion 'getSeccion' del modulo clase en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public SeccionJpaEntity getSeccion() {
        return seccion;
    }

/**
 * Implementa la operacion 'getAsignatura' del modulo clase en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public AsignaturaJpaEntity getAsignatura() {
        return asignatura;
    }

/**
 * Implementa la operacion 'getDocente' del modulo clase en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public DocenteJpaEntity getDocente() {
        return docente;
    }

    public void actualizarDatos(
            SeccionJpaEntity seccion,
            AsignaturaJpaEntity asignatura,
            DocenteJpaEntity docente,
            String diaSemana,
            LocalTime horaInicio,
            LocalTime horaFin
    ) {
        this.seccion = seccion;
        this.asignatura = asignatura;
        this.docente = docente;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

/**
 * Implementa la operacion 'activar' del modulo clase en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void activar() {
        this.estado = "ACTIVO";
    }

/**
 * Implementa la operacion 'inactivar' del modulo clase en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void inactivar() {
        this.estado = "INACTIVO";
    }

    @Override
/**
 * Implementa la operacion 'equals' del modulo clase en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param o dato de entrada relevante para ejecutar esta operacion: 'o'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClaseJpaEntity that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
/**
 * Implementa la operacion 'hashCode' del modulo clase en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public int hashCode() {
        return 31;
    }
}

