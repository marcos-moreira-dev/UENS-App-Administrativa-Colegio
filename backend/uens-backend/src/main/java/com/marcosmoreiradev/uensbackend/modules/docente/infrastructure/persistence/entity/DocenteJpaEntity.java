package com.marcosmoreiradev.uensbackend.modules.docente.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "docente")
/**
 * Define la responsabilidad de DocenteJpaEntity dentro del backend UENS.
 * Contexto: modulo docente, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: representar el estado persistente alineado a tablas y restricciones del SQL oficial.
 */
public class DocenteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id", nullable = false)
    private Long id;

    @Column(name = "nombres", nullable = false, length = 120)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 120)
    private String apellidos;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "correo_electronico", length = 254)
    private String correoElectronico;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;
/**
 * Construye la instancia de DocenteJpaEntity para operar en el modulo docente.
 * Contexto: capa infrastructure con dependencias inyectadas segun la arquitectura modular UENS.
 */

    protected DocenteJpaEntity() {
    }

/**
 * Construye la instancia de DocenteJpaEntity para operar en el modulo docente.
 * Contexto: capa infrastructure con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param nombres dato de entrada relevante para ejecutar esta operacion: 'nombres'
     * @param apellidos dato de entrada relevante para ejecutar esta operacion: 'apellidos'
     * @param telefono dato de entrada relevante para ejecutar esta operacion: 'telefono'
     * @param correoElectronico dato de entrada relevante para ejecutar esta operacion: 'correoElectronico'
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
 */
    private DocenteJpaEntity(String nombres, String apellidos, String telefono, String correoElectronico, String estado) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.estado = estado;
    }

/**
 * Implementa la operacion 'crear' del modulo docente en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param nombres dato de entrada relevante para ejecutar esta operacion: 'nombres'
     * @param apellidos dato de entrada relevante para ejecutar esta operacion: 'apellidos'
     * @param telefono dato de entrada relevante para ejecutar esta operacion: 'telefono'
     * @param correoElectronico dato de entrada relevante para ejecutar esta operacion: 'correoElectronico'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static DocenteJpaEntity crear(String nombres, String apellidos, String telefono, String correoElectronico) {
        return new DocenteJpaEntity(nombres, apellidos, telefono, correoElectronico, "ACTIVO");
    }

/**
 * Implementa la operacion 'getId' del modulo docente en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Long getId() {
        return id;
    }

/**
 * Implementa la operacion 'getNombres' del modulo docente en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getNombres() {
        return nombres;
    }

/**
 * Implementa la operacion 'getApellidos' del modulo docente en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getApellidos() {
        return apellidos;
    }

/**
 * Implementa la operacion 'getTelefono' del modulo docente en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getTelefono() {
        return telefono;
    }

/**
 * Implementa la operacion 'getCorreoElectronico' del modulo docente en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getCorreoElectronico() {
        return correoElectronico;
    }

/**
 * Implementa la operacion 'getEstado' del modulo docente en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getEstado() {
        return estado;
    }

/**
 * Implementa la operacion 'actualizarDatos' del modulo docente en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param nombres dato de entrada relevante para ejecutar esta operacion: 'nombres'
     * @param apellidos dato de entrada relevante para ejecutar esta operacion: 'apellidos'
     * @param telefono dato de entrada relevante para ejecutar esta operacion: 'telefono'
     * @param correoElectronico dato de entrada relevante para ejecutar esta operacion: 'correoElectronico'
 */
    public void actualizarDatos(String nombres, String apellidos, String telefono, String correoElectronico) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
    }

/**
 * Implementa la operacion 'activar' del modulo docente en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void activar() {
        this.estado = "ACTIVO";
    }

/**
 * Implementa la operacion 'inactivar' del modulo docente en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void inactivar() {
        this.estado = "INACTIVO";
    }

    @Override
/**
 * Implementa la operacion 'equals' del modulo docente en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param o dato de entrada relevante para ejecutar esta operacion: 'o'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocenteJpaEntity that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
/**
 * Implementa la operacion 'hashCode' del modulo docente en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public int hashCode() {
        return 31;
    }
}

