package com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.repository;

import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.entity.SeccionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Define la responsabilidad de SeccionJpaRepository dentro del backend UENS.
 * Contexto: modulo seccion, capa infrastructure, arquitectura monolito modular Spring Boot.
 * Alcance: encapsular acceso a datos y consultas sobre el esquema relacional V2 3FN.
 */
public interface SeccionJpaRepository extends JpaRepository<SeccionJpaEntity, Long>, JpaSpecificationExecutor<SeccionJpaEntity> {

/**
 * Implementa la operacion 'existsByAnioLectivoAndGradoAndParaleloIgnoreCase' del modulo seccion en la capa infrastructure.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param anioLectivo dato de entrada relevante para ejecutar esta operacion: 'anioLectivo'
     * @param grado grado academico de Educacion General Basica para filtrar/validar datos
     * @param paralelo dato de entrada relevante para ejecutar esta operacion: 'paralelo'
     * @return salida util para continuar con la capa llamadora.
 */
    boolean existsByAnioLectivoAndGradoAndParaleloIgnoreCase(String anioLectivo, Integer grado, String paralelo);

    boolean existsByAnioLectivoAndGradoAndParaleloIgnoreCaseAndIdNot(
            String anioLectivo,
            Integer grado,
            String paralelo,
            Long id
    );
}

