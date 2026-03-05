-- ============================================================
-- Unidad Educativa Ninitos Sonadores
-- PostgreSQL - V2 (Tercera Forma Normal / 3FN)
-- Base tecnica objetivo:
--   SchoolManagerNinitosSonadores
-- Ejecucion recomendada:
--   1) crear/renombrar la base con db/V2/tools/00_database_bootstrap_school_manager_ninitos_sonadores.sql
--   2) conectarse a SchoolManagerNinitosSonadores
--   3) ejecutar este archivo
-- Objetivo:
--   - Partir de la version 2FN
--   - Eliminar redundancias derivadas innecesarias
--   - Dejar una base mas limpia para backend (fase 1)
--
-- Cambios clave respecto a 2FN:
--   1) Se elimina estudiante.edad (derivable desde fecha_nacimiento)
--   2) Se elimina seccion.cantidad_estudiantes_registrados (derivable por COUNT)
--   3) Se incorpora reporte_solicitud_queue para reportes asincronos
--   4) Se incorpora auditoria_evento para trazabilidad administrativa
--
-- V2 "CHECK" (esta revision):
--   - Se agrega CHECK de rol en usuario_sistema_administrativo (ADMIN, SECRETARIA)
-- ============================================================

BEGIN;

DROP TABLE IF EXISTS auditoria_evento CASCADE;
DROP TABLE IF EXISTS reporte_solicitud_queue CASCADE;
DROP TABLE IF EXISTS calificacion CASCADE;
DROP TABLE IF EXISTS clase CASCADE;
DROP TABLE IF EXISTS asignatura CASCADE;
DROP TABLE IF EXISTS docente CASCADE;
DROP TABLE IF EXISTS estudiante CASCADE;
DROP TABLE IF EXISTS representante_legal CASCADE;
DROP TABLE IF EXISTS seccion CASCADE;
DROP TABLE IF EXISTS usuario_sistema_administrativo CASCADE;

-- ============================================================
-- 1) Usuario del sistema administrativo
-- ============================================================
CREATE TABLE usuario_sistema_administrativo (
    pk_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_login         VARCHAR(80) NOT NULL,
    password_hash        TEXT NOT NULL,
    rol                  VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    estado               VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT uq_usuario_sistema_login UNIQUE (nombre_login),
    CONSTRAINT ck_usuario_sistema_rol CHECK (rol IN ('ADMIN', 'SECRETARIA')),
    CONSTRAINT ck_usuario_sistema_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

-- ============================================================
-- 2) Representante legal
-- ============================================================
CREATE TABLE representante_legal (
    pk_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombres              VARCHAR(120) NOT NULL,
    apellidos            VARCHAR(120) NOT NULL,
    telefono             VARCHAR(30),
    correo_electronico   VARCHAR(254)
);

-- ============================================================
-- 3) Seccion (3FN)
--    Se elimina atributo derivado: cantidad_estudiantes_registrados
-- ============================================================
CREATE TABLE seccion (
    pk_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    grado                SMALLINT NOT NULL,
    paralelo             VARCHAR(10) NOT NULL,
    cupo_maximo          SMALLINT NOT NULL DEFAULT 35,
    anio_lectivo         VARCHAR(20) NOT NULL,
    estado               VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT ck_seccion_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT ck_seccion_cupo_maximo CHECK (cupo_maximo > 0 AND cupo_maximo <= 35),
    CONSTRAINT ck_seccion_grado CHECK (grado >= 1 AND grado <= 7),
    CONSTRAINT ck_seccion_anio_lectivo_formato CHECK (anio_lectivo ~ '^[0-9]{4}-[0-9]{4}$'),
    CONSTRAINT uq_seccion_unica UNIQUE (anio_lectivo, grado, paralelo)
);

-- ============================================================
-- 4) Docente
-- ============================================================
CREATE TABLE docente (
    pk_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombres              VARCHAR(120) NOT NULL,
    apellidos            VARCHAR(120) NOT NULL,
    telefono             VARCHAR(30),
    correo_electronico   VARCHAR(254),
    estado               VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT ck_docente_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

-- ============================================================
-- 5) Asignatura
--    Fase 1: una asignatura por grado
--    Ej.: Matematica (grado 1), Matematica (grado 4)
-- ============================================================
CREATE TABLE asignatura (
    pk_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre               VARCHAR(120) NOT NULL,
    area                 VARCHAR(80),
    descripcion          TEXT,
    grado                SMALLINT NOT NULL,
    estado               VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT ck_asignatura_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT ck_asignatura_grado CHECK (grado >= 1 AND grado <= 7),
    CONSTRAINT uq_asignatura_nombre_grado UNIQUE (nombre, grado)
);

-- ============================================================
-- 6) Estudiante (3FN)
--    Se elimina atributo derivado: edad
-- ============================================================
CREATE TABLE estudiante (
    pk_id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombres                 VARCHAR(120) NOT NULL,
    apellidos               VARCHAR(120) NOT NULL,
    fecha_nacimiento        DATE NOT NULL,
    estado                  VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',
    representante_legal_id  BIGINT NOT NULL,
    seccion_id              BIGINT NULL,

    CONSTRAINT ck_estudiante_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),

    CONSTRAINT fk_estudiante_representante_legal
        FOREIGN KEY (representante_legal_id) REFERENCES representante_legal (pk_id),

    CONSTRAINT fk_estudiante_seccion
        FOREIGN KEY (seccion_id) REFERENCES seccion (pk_id)
);

-- ============================================================
-- 7) Clase (horario atomico heredado de 1FN)
-- ============================================================
CREATE TABLE clase (
    pk_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    dia_semana           VARCHAR(15) NOT NULL,
    hora_inicio          TIME NOT NULL,
    hora_fin             TIME NOT NULL,
    estado               VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',
    seccion_id           BIGINT NOT NULL,
    asignatura_id        BIGINT NOT NULL,
    docente_id           BIGINT NULL, -- 0..1 en fase 1

    CONSTRAINT ck_clase_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT ck_clase_dia_semana CHECK (
        dia_semana IN ('LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO')
    ),
    CONSTRAINT ck_clase_rango_horario CHECK (hora_fin > hora_inicio),

    CONSTRAINT fk_clase_seccion
        FOREIGN KEY (seccion_id) REFERENCES seccion (pk_id),

    CONSTRAINT fk_clase_asignatura
        FOREIGN KEY (asignatura_id) REFERENCES asignatura (pk_id),

    CONSTRAINT fk_clase_docente
        FOREIGN KEY (docente_id) REFERENCES docente (pk_id),

    CONSTRAINT uq_clase_operativa
        UNIQUE (seccion_id, asignatura_id, dia_semana, hora_inicio, hora_fin)
);

-- ============================================================
-- 8) Calificacion
-- ============================================================
CREATE TABLE calificacion (
    pk_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    numero_parcial       SMALLINT NOT NULL,
    nota                 NUMERIC(5,2) NOT NULL,
    fecha_registro       DATE,
    observacion          TEXT,
    estudiante_id        BIGINT NOT NULL,
    clase_id             BIGINT NOT NULL,

    CONSTRAINT ck_calificacion_numero_parcial CHECK (numero_parcial IN (1, 2)),
    CONSTRAINT ck_calificacion_nota CHECK (nota >= 0 AND nota <= 10),

    CONSTRAINT fk_calificacion_estudiante
        FOREIGN KEY (estudiante_id) REFERENCES estudiante (pk_id),

    CONSTRAINT fk_calificacion_clase
        FOREIGN KEY (clase_id) REFERENCES clase (pk_id),

    CONSTRAINT uq_calificacion_estudiante_clase_parcial
        UNIQUE (estudiante_id, clase_id, numero_parcial)
);

-- ============================================================
-- 9) Reporte solicitud queue (backend reporte async)
-- ============================================================
CREATE TABLE reporte_solicitud_queue (
    pk_id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_reporte           VARCHAR(100) NOT NULL,
    estado                 VARCHAR(20) NOT NULL,
    parametros_json        TEXT,
    resultado_json         TEXT,
    error_detalle          TEXT,
    solicitado_por_usuario BIGINT NULL,
    intentos               INTEGER NOT NULL DEFAULT 0,
    fecha_solicitud        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ck_reporte_solicitud_queue_estado
        CHECK (estado IN ('PENDIENTE', 'EN_PROCESO', 'COMPLETADA', 'ERROR'))
);

-- ============================================================
-- 10) Auditoria operativa (backend trazabilidad)
-- ============================================================
CREATE TABLE auditoria_evento (
    pk_id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    modulo                VARCHAR(80) NOT NULL,
    accion                VARCHAR(120) NOT NULL,
    entidad               VARCHAR(120),
    entidad_id            VARCHAR(120),
    resultado             VARCHAR(20) NOT NULL,
    detalle               TEXT,
    request_id            VARCHAR(64),
    ip_origen             VARCHAR(64),
    actor_usuario_id      BIGINT NULL,
    actor_login           VARCHAR(80),
    actor_rol             VARCHAR(30),
    fecha_evento          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ck_auditoria_evento_resultado
        CHECK (resultado IN ('EXITO', 'ERROR', 'INFO', 'ADVERTENCIA')),
    CONSTRAINT fk_auditoria_evento_actor_usuario
        FOREIGN KEY (actor_usuario_id) REFERENCES usuario_sistema_administrativo (pk_id)
);

-- ============================================================
-- Indices utiles
-- ============================================================
CREATE INDEX idx_estudiante_representante_legal_id ON estudiante (representante_legal_id);
CREATE INDEX idx_estudiante_seccion_id             ON estudiante (seccion_id);

CREATE INDEX idx_clase_seccion_id                  ON clase (seccion_id);
CREATE INDEX idx_clase_asignatura_id               ON clase (asignatura_id);
CREATE INDEX idx_clase_docente_id                  ON clase (docente_id);
CREATE INDEX idx_clase_dia_hora                    ON clase (dia_semana, hora_inicio, hora_fin);

CREATE INDEX idx_calificacion_estudiante_id        ON calificacion (estudiante_id);
CREATE INDEX idx_calificacion_clase_id             ON calificacion (clase_id);
CREATE INDEX idx_reporte_queue_estado_fecha        ON reporte_solicitud_queue (estado, fecha_solicitud);
CREATE INDEX idx_reporte_queue_solicitado_por      ON reporte_solicitud_queue (solicitado_por_usuario);
CREATE INDEX idx_auditoria_evento_fecha            ON auditoria_evento (fecha_evento);
CREATE INDEX idx_auditoria_evento_modulo_accion    ON auditoria_evento (modulo, accion);
CREATE INDEX idx_auditoria_evento_resultado        ON auditoria_evento (resultado);
CREATE INDEX idx_auditoria_evento_actor_login      ON auditoria_evento (actor_login);

COMMIT;

-- ============================================================
-- Notas practicas (3FN)
-- ============================================================
-- 1) Edad del estudiante se calcula en consultas o backend:
--      age(current_date, fecha_nacimiento)
--
-- 2) Cantidad de estudiantes por seccion se obtiene por consulta:
--      SELECT seccion_id, COUNT(*) ...
--
-- 3) Esta version mantiene simplicidad de fase 1:
--      - sin catalogo de estados
--      - sin tabla de anio_lectivo
--      - sin multiples contactos por persona
--
-- 4) Reglas de negocio que pueden reforzarse luego (backend/DB):
--      - coherencia seccion.grado = asignatura.grado al crear clase
--      - evitar solapamiento horario por docente/seccion (si lo deseas)
