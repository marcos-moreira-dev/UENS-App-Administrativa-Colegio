package com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.repository;

import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.entity.EstudianteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
/**
 * Define la responsabilidad de EstudianteJpaRepository dentro del backend UENS.
 * Contexto: modulo estudiante, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: encapsular acceso a datos y consultas sobre el esquema relacional V2 3FN.
 */
public interface EstudianteJpaRepository extends JpaRepository<EstudianteJpaEntity, Long>, JpaSpecificationExecutor<EstudianteJpaEntity> {

    boolean existsByNombresIgnoreCaseAndApellidosIgnoreCaseAndFechaNacimiento(
            String nombres,
            String apellidos,
            LocalDate fechaNacimiento
    );

    boolean existsByNombresIgnoreCaseAndApellidosIgnoreCaseAndFechaNacimientoAndIdNot(
            String nombres,
            String apellidos,
            LocalDate fechaNacimiento,
            Long id
    );

/**
 * Implementa la operacion 'countBySeccion_Id' del modulo estudiante en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param seccionId identificador de la seccion academica (grado/paralelo/anio lectivo)
     * @return salida util para continuar con la capa llamadora.
 */
    long countBySeccion_Id(Long seccionId);
}

