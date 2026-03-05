package com.marcosmoreiradev.uens_backend.support;

import com.marcosmoreiradev.uensbackend.modules.estudiante.infrastructure.persistence.entity.EstudianteJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.reporte.infrastructure.persistence.entity.ReporteSolicitudQueueJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.representante.infrastructure.persistence.entity.RepresentanteLegalJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.seccion.infrastructure.persistence.entity.SeccionJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.entity.UsuarioSistemaAdministrativoJpaEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

public final class TestDataFactory {
/**
 * Construye la instancia de TestDataFactory para operar en el modulo core.
 * Contexto: capa core con dependencias inyectadas segun la arquitectura modular UENS.
 */

    private TestDataFactory() {
    }

/**
 * Implementa la operacion 'usuarioAdmin' del modulo core en la capa core.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static UsuarioSistemaAdministrativoJpaEntity usuarioAdmin(Long id) {
        UsuarioSistemaAdministrativoJpaEntity usuario =
                UsuarioSistemaAdministrativoJpaEntity.crear("admin", "$2a$10$hash", "ADMIN", "ACTIVO");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }

/**
 * Implementa la operacion 'representante' del modulo core en la capa core.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static RepresentanteLegalJpaEntity representante(Long id) {
        RepresentanteLegalJpaEntity entity =
                new RepresentanteLegalJpaEntity("Ana", "Lopez", "0999999999", "ana@example.com");
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

/**
 * Implementa la operacion 'seccionActiva' del modulo core en la capa core.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param cupoMaximo cupo maximo permitido para la seccion en pruebas o datos de soporte
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static SeccionJpaEntity seccionActiva(Long id, int cupoMaximo) {
        SeccionJpaEntity entity = SeccionJpaEntity.crear(3, "A", cupoMaximo, "2025-2026");
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

/**
 * Implementa la operacion 'estudiante' del modulo core en la capa core.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param representanteId identificador del representante legal asociado al estudiante
     * @param seccionId identificador de la seccion academica (grado/paralelo/anio lectivo)
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static EstudianteJpaEntity estudiante(Long id, Long representanteId, Long seccionId) {
        RepresentanteLegalJpaEntity representante = representante(representanteId);
        SeccionJpaEntity seccion = seccionActiva(seccionId, 30);
        EstudianteJpaEntity entity = EstudianteJpaEntity.crear(
                "Juan",
                "Perez",
                LocalDate.of(2015, 1, 10),
                representante,
                seccion
        );
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

/**
 * Implementa la operacion 'reporteSolicitud' del modulo core en la capa core.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param id identificador tecnico del recurso dentro del modulo actual
     * @param tipo tipo de entidad o reporte usado para construir escenario esperado
     * @param estado estado operativo del recurso (ACTIVO/INACTIVO) para filtrar o actualizar
     * @param intentos numero de intentos acumulados en procesamiento de solicitud asincrona
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static ReporteSolicitudQueueJpaEntity reporteSolicitud(Long id, String tipo, String estado, int intentos) {
        ReporteSolicitudQueueJpaEntity entity = ReporteSolicitudQueueJpaEntity.crear(tipo, "{\"k\":\"v\"}", 1L);
        ReflectionTestUtils.setField(entity, "id", id);
        ReflectionTestUtils.setField(entity, "estado", estado);
        ReflectionTestUtils.setField(entity, "intentos", intentos);
        return entity;
    }
}
