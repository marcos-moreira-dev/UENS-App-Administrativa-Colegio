package com.marcosmoreiradev.uensbackend.modules.representante.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "representante_legal")
/**
 * Define la responsabilidad de RepresentanteLegalJpaEntity dentro del backend UENS.
 * Contexto: modulo representante, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: representar el estado persistente alineado a tablas y restricciones del SQL oficial.
 */
public class RepresentanteLegalJpaEntity {

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
/**
 * Construye la instancia de RepresentanteLegalJpaEntity para operar en el modulo representante.
 * Contexto: capa infrastructure con dependencias inyectadas segun la arquitectura modular UENS.
 */

    protected RepresentanteLegalJpaEntity() {
    }

/**
 * Construye la instancia de RepresentanteLegalJpaEntity para operar en el modulo representante.
 * Contexto: capa infrastructure con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param nombres dato de entrada relevante para ejecutar esta operacion: 'nombres'
     * @param apellidos dato de entrada relevante para ejecutar esta operacion: 'apellidos'
     * @param telefono dato de entrada relevante para ejecutar esta operacion: 'telefono'
     * @param correoElectronico dato de entrada relevante para ejecutar esta operacion: 'correoElectronico'
 */
    public RepresentanteLegalJpaEntity(String nombres, String apellidos, String telefono, String correoElectronico) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
    }

/**
 * Implementa la operacion 'getId' del modulo representante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Long getId() {
        return id;
    }

/**
 * Implementa la operacion 'getNombres' del modulo representante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getNombres() {
        return nombres;
    }

/**
 * Implementa la operacion 'getApellidos' del modulo representante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getApellidos() {
        return apellidos;
    }

/**
 * Implementa la operacion 'getTelefono' del modulo representante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getTelefono() {
        return telefono;
    }

/**
 * Implementa la operacion 'getCorreoElectronico' del modulo representante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getCorreoElectronico() {
        return correoElectronico;
    }

/**
 * Implementa la operacion 'actualizarDatos' del modulo representante en la capa infrastructure.
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
}

