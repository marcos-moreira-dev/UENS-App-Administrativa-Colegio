package com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.entity;

import com.marcosmoreiradev.uensbackend.modules.representante.infrastructure.persistence.entity.RepresentanteLegalJpaEntity;
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

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "estudiante")
/**
 * Define la responsabilidad de EstudianteJpaEntity dentro del backend UENS.
 * Contexto: modulo estudiante, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: representar el estado persistente alineado a tablas y restricciones del SQL oficial.
 */
public class EstudianteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id", nullable = false)
    private Long id;

    @Column(name = "nombres", nullable = false, length = 120)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 120)
    private String apellidos;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "representante_legal_id", nullable = false)
    private RepresentanteLegalJpaEntity representanteLegal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seccion_id")
    private SeccionJpaEntity seccion;
/**
 * Construye la instancia de EstudianteJpaEntity para operar en el modulo estudiante.
 * Contexto: capa infrastructure con dependencias inyectadas segun la arquitectura modular UENS.
 */

    protected EstudianteJpaEntity() {
    }

    private EstudianteJpaEntity(
            String nombres,
            String apellidos,
            LocalDate fechaNacimiento,
            RepresentanteLegalJpaEntity representanteLegal,
            SeccionJpaEntity seccion,
            String estado
    ) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.representanteLegal = representanteLegal;
        this.seccion = seccion;
        this.estado = estado;
    }
/**
 * Implementa la operacion 'crear' del modulo estudiante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param nombres dato de entrada relevante para ejecutar esta operacion: 'nombres'
     * @param apellidos dato de entrada relevante para ejecutar esta operacion: 'apellidos'
     * @param fechaNacimiento dato de entrada relevante para ejecutar esta operacion: 'fechaNacimiento'
     * @param representanteLegal dato de entrada relevante para ejecutar esta operacion: 'representanteLegal'
     * @param seccion dato de entrada relevante para ejecutar esta operacion: 'seccion'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */

    public static EstudianteJpaEntity crear(
            String nombres,
            String apellidos,
            LocalDate fechaNacimiento,
            RepresentanteLegalJpaEntity representanteLegal,
            SeccionJpaEntity seccion
    ) {
        return new EstudianteJpaEntity(nombres, apellidos, fechaNacimiento, representanteLegal, seccion, "ACTIVO");
    }

/**
 * Implementa la operacion 'getId' del modulo estudiante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Long getId() {
        return id;
    }

/**
 * Implementa la operacion 'getNombres' del modulo estudiante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getNombres() {
        return nombres;
    }

/**
 * Implementa la operacion 'getApellidos' del modulo estudiante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getApellidos() {
        return apellidos;
    }

/**
 * Implementa la operacion 'getFechaNacimiento' del modulo estudiante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

/**
 * Implementa la operacion 'getEstado' del modulo estudiante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getEstado() {
        return estado;
    }

/**
 * Implementa la operacion 'getRepresentanteLegal' del modulo estudiante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public RepresentanteLegalJpaEntity getRepresentanteLegal() {
        return representanteLegal;
    }

/**
 * Implementa la operacion 'getSeccion' del modulo estudiante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public SeccionJpaEntity getSeccion() {
        return seccion;
    }

    public void actualizarDatos(
            String nombres,
            String apellidos,
            LocalDate fechaNacimiento,
            RepresentanteLegalJpaEntity representanteLegal,
            SeccionJpaEntity seccion
    ) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.representanteLegal = representanteLegal;
        this.seccion = seccion;
    }

/**
 * Implementa la operacion 'asignarSeccion' del modulo estudiante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param seccion dato de entrada relevante para ejecutar esta operacion: 'seccion'
 */
    public void asignarSeccion(SeccionJpaEntity seccion) {
        this.seccion = seccion;
    }

/**
 * Implementa la operacion 'activar' del modulo estudiante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void activar() {
        this.estado = "ACTIVO";
    }

/**
 * Implementa la operacion 'inactivar' del modulo estudiante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void inactivar() {
        this.estado = "INACTIVO";
    }

    @Override
/**
 * Implementa la operacion 'equals' del modulo estudiante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param o dato de entrada relevante para ejecutar esta operacion: 'o'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EstudianteJpaEntity that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
/**
 * Implementa la operacion 'hashCode' del modulo estudiante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public int hashCode() {
        return 31;
    }
}

