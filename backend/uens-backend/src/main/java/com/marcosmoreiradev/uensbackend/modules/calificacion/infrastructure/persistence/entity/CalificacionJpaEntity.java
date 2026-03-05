package com.marcosmoreiradev.uensbackend.modules.calificacion.infrastructure.persistence.entity;

import com.marcosmoreiradev.uensbackend.modules.clase.infrastructure.persistence.entity.ClaseJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.entity.EstudianteJpaEntity;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(
        name = "calificacion",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_calificacion_estudiante_clase_parcial",
                        columnNames = {"estudiante_id", "clase_id", "numero_parcial"}
                )
        }
)
/**
 * Define la responsabilidad de CalificacionJpaEntity dentro del backend UENS.
 * Contexto: modulo calificacion, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: representar el estado persistente alineado a tablas y restricciones del SQL oficial.
 */
public class CalificacionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id", nullable = false)
    private Long id;

    @Column(name = "numero_parcial", nullable = false)
    private Short numeroParcial;

    @Column(name = "nota", nullable = false, precision = 5, scale = 2)
    private BigDecimal nota;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @Column(name = "observacion")
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private EstudianteJpaEntity estudiante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clase_id", nullable = false)
    private ClaseJpaEntity clase;
/**
 * Construye la instancia de CalificacionJpaEntity para operar en el modulo calificacion.
 * Contexto: capa infrastructure con dependencias inyectadas segun la arquitectura modular UENS.
 */

    protected CalificacionJpaEntity() {
    }

    private CalificacionJpaEntity(
            Integer numeroParcial,
            BigDecimal nota,
            LocalDate fechaRegistro,
            String observacion,
            EstudianteJpaEntity estudiante,
            ClaseJpaEntity clase
    ) {
        this.numeroParcial = numeroParcial == null ? null : numeroParcial.shortValue();
        this.nota = nota;
        this.fechaRegistro = fechaRegistro;
        this.observacion = observacion;
        this.estudiante = estudiante;
        this.clase = clase;
    }
/**
 * Implementa la operacion 'crear' del modulo calificacion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param numeroParcial numero de parcial academico permitido en fase 1 (1 o 2)
     * @param nota nota numerica registrada en escala institucional definida para fase 1
     * @param fechaRegistro dato de entrada relevante para ejecutar esta operacion: 'fechaRegistro'
     * @param observacion dato de entrada relevante para ejecutar esta operacion: 'observacion'
     * @param estudiante dato de entrada relevante para ejecutar esta operacion: 'estudiante'
     * @param clase dato de entrada relevante para ejecutar esta operacion: 'clase'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */

    public static CalificacionJpaEntity crear(
            Integer numeroParcial,
            BigDecimal nota,
            LocalDate fechaRegistro,
            String observacion,
            EstudianteJpaEntity estudiante,
            ClaseJpaEntity clase
    ) {
        return new CalificacionJpaEntity(numeroParcial, nota, fechaRegistro, observacion, estudiante, clase);
    }

/**
 * Implementa la operacion 'getId' del modulo calificacion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Long getId() {
        return id;
    }

/**
 * Implementa la operacion 'getNumeroParcial' del modulo calificacion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Integer getNumeroParcial() {
        return numeroParcial == null ? null : numeroParcial.intValue();
    }

/**
 * Implementa la operacion 'getNota' del modulo calificacion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public BigDecimal getNota() {
        return nota;
    }

/**
 * Implementa la operacion 'getFechaRegistro' del modulo calificacion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

/**
 * Implementa la operacion 'getObservacion' del modulo calificacion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getObservacion() {
        return observacion;
    }

/**
 * Implementa la operacion 'getEstudiante' del modulo calificacion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public EstudianteJpaEntity getEstudiante() {
        return estudiante;
    }

/**
 * Implementa la operacion 'getClase' del modulo calificacion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ClaseJpaEntity getClase() {
        return clase;
    }

    public void actualizarDatos(
            Integer numeroParcial,
            BigDecimal nota,
            LocalDate fechaRegistro,
            String observacion,
            EstudianteJpaEntity estudiante,
            ClaseJpaEntity clase
    ) {
        this.numeroParcial = numeroParcial == null ? null : numeroParcial.shortValue();
        this.nota = nota;
        this.fechaRegistro = fechaRegistro;
        this.observacion = observacion;
        this.estudiante = estudiante;
        this.clase = clase;
    }

    @Override
/**
 * Implementa la operacion 'equals' del modulo calificacion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param o dato de entrada relevante para ejecutar esta operacion: 'o'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalificacionJpaEntity that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
/**
 * Implementa la operacion 'hashCode' del modulo calificacion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public int hashCode() {
        return 31;
    }
}
