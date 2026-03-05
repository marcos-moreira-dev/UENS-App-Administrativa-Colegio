package com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Objects;

@Entity
@Table(
        name = "seccion",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_seccion_unica",
                        columnNames = {"anio_lectivo", "grado", "paralelo"}
                )
        }
)
/**
 * Define la responsabilidad de SeccionJpaEntity dentro del backend UENS.
 * Contexto: modulo seccion, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: representar el estado persistente alineado a tablas y restricciones del SQL oficial.
 */
public class SeccionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id", nullable = false)
    private Long id;

    @Column(name = "grado", nullable = false)
    private Short grado;

    @Column(name = "paralelo", nullable = false, length = 10)
    private String paralelo;

    @Column(name = "cupo_maximo", nullable = false)
    private Short cupoMaximo;

    @Column(name = "anio_lectivo", nullable = false, length = 20)
    private String anioLectivo;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;
/**
 * Construye la instancia de SeccionJpaEntity para operar en el modulo seccion.
 * Contexto: capa infrastructure con dependencias inyectadas segun la arquitectura modular UENS.
 */

    protected SeccionJpaEntity() {
    }

/**
 * Construye la instancia de SeccionJpaEntity para operar en el modulo seccion.
 * Contexto: capa infrastructure con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param grado grado academico de Educacion General Basica para filtrar/validar datos
     * @param paralelo dato de entrada relevante para ejecutar esta operacion: 'paralelo'
     * @param cupoMaximo cupo maximo permitido para la seccion en pruebas o datos de soporte
     * @param anioLectivo dato de entrada relevante para ejecutar esta operacion: 'anioLectivo'
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
 */
    private SeccionJpaEntity(Integer grado, String paralelo, Integer cupoMaximo, String anioLectivo, String estado) {
        this.grado = grado == null ? null : grado.shortValue();
        this.paralelo = paralelo;
        this.cupoMaximo = cupoMaximo == null ? null : cupoMaximo.shortValue();
        this.anioLectivo = anioLectivo;
        this.estado = estado;
    }

/**
 * Implementa la operacion 'crear' del modulo seccion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param grado grado academico de Educacion General Basica para filtrar/validar datos
     * @param paralelo dato de entrada relevante para ejecutar esta operacion: 'paralelo'
     * @param cupoMaximo cupo maximo permitido para la seccion en pruebas o datos de soporte
     * @param anioLectivo dato de entrada relevante para ejecutar esta operacion: 'anioLectivo'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static SeccionJpaEntity crear(Integer grado, String paralelo, Integer cupoMaximo, String anioLectivo) {
        return new SeccionJpaEntity(grado, paralelo, cupoMaximo, anioLectivo, "ACTIVO");
    }

/**
 * Implementa la operacion 'getId' del modulo seccion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Long getId() {
        return id;
    }

/**
 * Implementa la operacion 'getGrado' del modulo seccion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Integer getGrado() {
        return grado == null ? null : grado.intValue();
    }

/**
 * Implementa la operacion 'getParalelo' del modulo seccion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getParalelo() {
        return paralelo;
    }

/**
 * Implementa la operacion 'getCupoMaximo' del modulo seccion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Integer getCupoMaximo() {
        return cupoMaximo == null ? null : cupoMaximo.intValue();
    }

/**
 * Implementa la operacion 'getAnioLectivo' del modulo seccion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getAnioLectivo() {
        return anioLectivo;
    }

/**
 * Implementa la operacion 'getEstado' del modulo seccion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getEstado() {
        return estado;
    }

/**
 * Implementa la operacion 'actualizarDatos' del modulo seccion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param grado grado academico de Educacion General Basica para filtrar/validar datos
     * @param paralelo dato de entrada relevante para ejecutar esta operacion: 'paralelo'
     * @param cupoMaximo cupo maximo permitido para la seccion en pruebas o datos de soporte
     * @param anioLectivo dato de entrada relevante para ejecutar esta operacion: 'anioLectivo'
 */
    public void actualizarDatos(Integer grado, String paralelo, Integer cupoMaximo, String anioLectivo) {
        this.grado = grado == null ? null : grado.shortValue();
        this.paralelo = paralelo;
        this.cupoMaximo = cupoMaximo == null ? null : cupoMaximo.shortValue();
        this.anioLectivo = anioLectivo;
    }

/**
 * Implementa la operacion 'activar' del modulo seccion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void activar() {
        this.estado = "ACTIVO";
    }

/**
 * Implementa la operacion 'inactivar' del modulo seccion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public void inactivar() {
        this.estado = "INACTIVO";
    }

    @Override
/**
 * Implementa la operacion 'equals' del modulo seccion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param o dato de entrada relevante para ejecutar esta operacion: 'o'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeccionJpaEntity that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
/**
 * Implementa la operacion 'hashCode' del modulo seccion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public int hashCode() {
        return 31;
    }
}
