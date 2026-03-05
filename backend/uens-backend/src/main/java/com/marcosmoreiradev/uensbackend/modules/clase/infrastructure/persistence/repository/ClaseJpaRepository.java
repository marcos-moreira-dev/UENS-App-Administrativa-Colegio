package com.marcosmoreiradev.uensbackend.modules.clase.infrastructure.persistence.repository;

import com.marcosmoreiradev.uensbackend.modules.clase.infrastructure.persistence.entity.ClaseJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;

@Repository
/**
 * Define la responsabilidad de ClaseJpaRepository dentro del backend UENS.
 * Contexto: modulo clase, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: encapsular acceso a datos y consultas sobre el esquema relacional V2 3FN.
 */
public interface ClaseJpaRepository extends JpaRepository<ClaseJpaEntity, Long>, JpaSpecificationExecutor<ClaseJpaEntity> {

    boolean existsBySeccion_IdAndAsignatura_IdAndDiaSemanaAndHoraInicioAndHoraFin(
            Long seccionId,
            Long asignaturaId,
            String diaSemana,
            LocalTime horaInicio,
            LocalTime horaFin
    );

    boolean existsBySeccion_IdAndAsignatura_IdAndDiaSemanaAndHoraInicioAndHoraFinAndIdNot(
            Long seccionId,
            Long asignaturaId,
            String diaSemana,
            LocalTime horaInicio,
            LocalTime horaFin,
            Long id
    );

    boolean existsByDocente_IdAndDiaSemanaAndHoraInicioLessThanAndHoraFinGreaterThan(
            Long docenteId,
            String diaSemana,
            LocalTime horaFin,
            LocalTime horaInicio
    );

    boolean existsByDocente_IdAndDiaSemanaAndHoraInicioLessThanAndHoraFinGreaterThanAndIdNot(
            Long docenteId,
            String diaSemana,
            LocalTime horaFin,
            LocalTime horaInicio,
            Long id
    );

    boolean existsBySeccion_IdAndDiaSemanaAndHoraInicioLessThanAndHoraFinGreaterThan(
            Long seccionId,
            String diaSemana,
            LocalTime horaFin,
            LocalTime horaInicio
    );

    boolean existsBySeccion_IdAndDiaSemanaAndHoraInicioLessThanAndHoraFinGreaterThanAndIdNot(
            Long seccionId,
            String diaSemana,
            LocalTime horaFin,
            LocalTime horaInicio,
            Long id
    );
}

