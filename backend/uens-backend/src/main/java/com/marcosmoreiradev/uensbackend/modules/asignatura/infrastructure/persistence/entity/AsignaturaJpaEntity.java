package com.marcosmoreiradev.uensbackend.modules.asignatura.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(
        name = "asignatura",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_asignatura_nombre_grado",
                        columnNames = {"nombre", "grado"}
                )
        }
)

/**
 * Define la responsabilidad de AsignaturaJpaEntity dentro del backend UENS.
 * Contexto: modulo asignatura, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: representar el estado persistente alineado a tablas y restricciones del SQL oficial.
 */
public class AsignaturaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id", nullable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "area", nullable = true, length = 80)
    private String area;

    @Column(name = "descripcion", nullable = true, length = 500)
    private String descripcion;

    @Column(name = "grado", nullable = false)
    private Short grado;

    /**
     * Estado operativo (ej. ACTIVO / INACTIVO).
     * En V1 se persiste como String por alineaciÃ³n directa con SQL oficial.
     */
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
/**
 * Construye la instancia de AsignaturaJpaEntity para operar en el modulo asignatura.
 * Contexto: capa infrastructure con dependencias inyectadas segun la arquitectura modular UENS.
 */

    protected AsignaturaJpaEntity() {
        // JPA
    }

/**
 * Construye la instancia de AsignaturaJpaEntity para operar en el modulo asignatura.
 * Contexto: capa infrastructure con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param nombre dato de entrada relevante para ejecutar esta operacion: 'nombre'
     * @param area area academica de la asignatura (por ejemplo Lenguaje o Ciencias)
     * @param descripcion dato de entrada relevante para ejecutar esta operacion: 'descripcion'
     * @param grado grado academico de Educacion General Basica para filtrar/validar datos
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
 */
    private AsignaturaJpaEntity(String nombre, String area, String descripcion, Integer grado, String estado) {
        this.nombre = nombre;
        this.area = area;
        this.descripcion = descripcion;
        this.grado = grado == null ? null : grado.shortValue();
        this.estado = estado;
    }

/**
 * Implementa la operacion 'crear' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param nombre dato de entrada relevante para ejecutar esta operacion: 'nombre'
     * @param area area academica de la asignatura (por ejemplo Lenguaje o Ciencias)
     * @param descripcion dato de entrada relevante para ejecutar esta operacion: 'descripcion'
     * @param grado grado academico de Educacion General Basica para filtrar/validar datos
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static AsignaturaJpaEntity crear(String nombre, String area, String descripcion, Integer grado) {
        // Estado por defecto V1
        return new AsignaturaJpaEntity(nombre, area, descripcion, grado, "ACTIVO");
    }

    // -------------------------
    // Getters
    // -------------------------

/**
 * Implementa la operacion 'getId' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Long getId() {
        return id;
    }

/**
 * Implementa la operacion 'getNombre' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getNombre() {
        return nombre;
    }

/**
 * Implementa la operacion 'getArea' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getArea() {
        return area;
    }

/**
 * Implementa la operacion 'getDescripcion' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getDescripcion() {
        return descripcion;
    }

/**
 * Implementa la operacion 'getGrado' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Integer getGrado() {
        return grado == null ? null : grado.intValue();
    }

/**
 * Implementa la operacion 'getEstado' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getEstado() {
        return estado;
    }

    // -------------------------
    // Mutabilidad controlada
    // -------------------------

/**
 * Implementa la operacion 'actualizarDatos' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param nombre dato de entrada relevante para ejecutar esta operacion: 'nombre'
     * @param area area academica de la asignatura (por ejemplo Lenguaje o Ciencias)
     * @param descripcion dato de entrada relevante para ejecutar esta operacion: 'descripcion'
     * @param grado grado academico de Educacion General Basica para filtrar/validar datos
 */
    public void actualizarDatos(String nombre, String area, String descripcion, Integer grado) {
        this.nombre = nombre;
        this.area = area;
        this.descripcion = descripcion;
        this.grado = grado == null ? null : grado.shortValue();
    }

/**
 * Implementa la operacion 'activar' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void activar() {
        this.estado = "ACTIVO";
    }

/**
 * Implementa la operacion 'inactivar' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void inactivar() {
        this.estado = "INACTIVO";
    }

    // -------------------------
    // equals / hashCode (pragmÃ¡tico V1)
    // -------------------------

    @Override
/**
 * Implementa la operacion 'equals' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param o dato de entrada relevante para ejecutar esta operacion: 'o'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AsignaturaJpaEntity that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
/**
 * Implementa la operacion 'hashCode' del modulo asignatura en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public int hashCode() {
        return 31;
    }
}
