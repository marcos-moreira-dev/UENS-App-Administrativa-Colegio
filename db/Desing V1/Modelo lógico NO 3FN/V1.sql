-- ============================================================
-- Unidad Educativa Niñitos Soñadores
-- PostgreSQL - V1 (traducción inicial desde modelo conceptual)
-- Objetivo:
--   - Crear una primera versión de tablas basada en el modelo conceptual
--   - NO busca estar normalizada todavía (sirve para ver la evolución)
--   - Incluye algunos atributos derivados del conceptual de forma intencional
--     (para luego refactorizar en la etapa de normalización/3FN)
-- ============================================================

BEGIN;

-- ============================================================
-- Limpieza (opcional)
-- ============================================================
DROP TABLE IF EXISTS calificacion CASCADE;
DROP TABLE IF EXISTS clase CASCADE;
DROP TABLE IF EXISTS asignatura CASCADE;
DROP TABLE IF EXISTS docente CASCADE;
DROP TABLE IF EXISTS estudiante CASCADE;
DROP TABLE IF EXISTS representante_legal CASCADE;
DROP TABLE IF EXISTS seccion CASCADE;
DROP TABLE IF EXISTS usuario_sistema_administrativo CASCADE;

-- ============================================================
-- 1) Usuario del sistema administrativo (separado del dominio académico)
--    Conceptual: credencial -> aquí se implementa como password_hash
-- ============================================================
CREATE TABLE usuario_sistema_administrativo (
    pk_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_login         VARCHAR(80) NOT NULL,
    password_hash        TEXT NOT NULL,
    rol                  VARCHAR(40) NOT NULL DEFAULT 'ADMINISTRATIVO',
    estado               VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT uq_usuario_sistema_login UNIQUE (nombre_login),
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
    -- Nota: en el modelo conceptual de fase 1 no se marcó estado aquí.
);

-- ============================================================
-- 3) Sección
--    Incluye atributo derivado para visualizar evolución (luego puede eliminarse)
-- ============================================================
CREATE TABLE seccion (
    pk_id                             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    grado                             SMALLINT NOT NULL,      -- 1..7 (fase 1: 6 a 13 años aprox., EGB)
    paralelo                          VARCHAR(10) NOT NULL,   -- A, B, C...
    cupo_maximo                       SMALLINT NOT NULL DEFAULT 35,
    anio_lectivo                      VARCHAR(20) NOT NULL,   -- ej. 2026-2027
    estado                            VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',

    -- Derivado en conceptual (se incluye en V0 para evolución)
    cantidad_estudiantes_registrados  INTEGER,

    CONSTRAINT ck_seccion_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT ck_seccion_cupo_maximo CHECK (cupo_maximo > 0 AND cupo_maximo <= 35),
    CONSTRAINT ck_seccion_grado CHECK (grado >= 1 AND grado <= 7),
    CONSTRAINT ck_seccion_cantidad_est_reg CHECK (
        cantidad_estudiantes_registrados IS NULL
        OR (cantidad_estudiantes_registrados >= 0 AND cantidad_estudiantes_registrados <= 35)
    ),
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
--    Decisión de esta fase: una asignatura existe por grado
--    Ej.: "Matemática" grado 1, "Matemática" grado 4, etc.
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
-- 6) Estudiante
--    Relación conceptual "Representa" -> FK a representante_legal (1 por estudiante)
--    Relación conceptual "Agrupa"      -> FK a seccion (0..1 sección vigente por estudiante)
--    Incluye atributo derivado "edad" en V0 para visualizar evolución
-- ============================================================
CREATE TABLE estudiante (
    pk_id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombres                 VARCHAR(120) NOT NULL,
    apellidos               VARCHAR(120) NOT NULL,
    fecha_nacimiento        DATE NOT NULL,

    -- Derivado en conceptual (se incluye en V0 para evolución)
    edad                    SMALLINT,

    estado                  VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',

    representante_legal_id  BIGINT NOT NULL,
    seccion_id              BIGINT NULL,

    CONSTRAINT ck_estudiante_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT ck_estudiante_edad CHECK (
        edad IS NULL OR (edad >= 0 AND edad <= 120)
    ),

    CONSTRAINT fk_estudiante_representante_legal
        FOREIGN KEY (representante_legal_id)
        REFERENCES representante_legal (pk_id),

    CONSTRAINT fk_estudiante_seccion
        FOREIGN KEY (seccion_id)
        REFERENCES seccion (pk_id)
);

-- ============================================================
-- 7) Clase
--    Relación "Oferta"         -> clase pertenece a 1 sección
--    Relación "Se dicta como"  -> clase pertenece a 1 asignatura
--    Relación "Imparte"        -> clase puede tener 0..1 docente
-- ============================================================
CREATE TABLE clase (
    pk_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    franja_horaria       VARCHAR(120) NOT NULL,
    estado               VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',

    seccion_id           BIGINT NOT NULL,
    asignatura_id        BIGINT NOT NULL,
    docente_id           BIGINT NULL,   -- opcional en fase 1 (clase puede existir sin docente asignado)

    CONSTRAINT ck_clase_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),

    CONSTRAINT fk_clase_seccion
        FOREIGN KEY (seccion_id)
        REFERENCES seccion (pk_id),

    CONSTRAINT fk_clase_asignatura
        FOREIGN KEY (asignatura_id)
        REFERENCES asignatura (pk_id),

    CONSTRAINT fk_clase_docente
        FOREIGN KEY (docente_id)
        REFERENCES docente (pk_id),

    -- Evita duplicar la misma clase por sección+asignatura+franja
    CONSTRAINT uq_clase_operativa UNIQUE (seccion_id, asignatura_id, franja_horaria)
);

-- ============================================================
-- 8) Calificación
--    Relación "Obtiene"       -> 1 estudiante, 0..M calificaciones
--    Relación "Corresponde a" -> 1 clase, 0..M calificaciones
--    Parciales de fase 1: 1 y 2
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
    -- Ajusta rango de nota si luego defines escala exacta (ej. 0..10)
    CONSTRAINT ck_calificacion_nota CHECK (nota >= 0),

    CONSTRAINT fk_calificacion_estudiante
        FOREIGN KEY (estudiante_id)
        REFERENCES estudiante (pk_id),

    CONSTRAINT fk_calificacion_clase
        FOREIGN KEY (clase_id)
        REFERENCES clase (pk_id),

    -- Una sola nota por estudiante+clase+parcial
    CONSTRAINT uq_calificacion_estudiante_clase_parcial
        UNIQUE (estudiante_id, clase_id, numero_parcial)
);

-- ============================================================
-- Índices útiles (no obligatorios, pero ayudan a visualizar y consultar)
-- ============================================================
CREATE INDEX idx_estudiante_representante_legal_id ON estudiante (representante_legal_id);
CREATE INDEX idx_estudiante_seccion_id             ON estudiante (seccion_id);

CREATE INDEX idx_clase_seccion_id                  ON clase (seccion_id);
CREATE INDEX idx_clase_asignatura_id               ON clase (asignatura_id);
CREATE INDEX idx_clase_docente_id                  ON clase (docente_id);

CREATE INDEX idx_calificacion_estudiante_id        ON calificacion (estudiante_id);
CREATE INDEX idx_calificacion_clase_id             ON calificacion (clase_id);

COMMIT;

-- ============================================================
-- Notas para la siguiente iteración (normalización / 3FN)
-- ============================================================
-- 1) "edad" (estudiante) debería eliminarse y calcularse desde fecha_nacimiento.
-- 2) "cantidad_estudiantes_registrados" (seccion) debería derivarse por consulta.
-- 3) Validar si "anio_lectivo" conviene tabla aparte en versiones futuras.
-- 4) Validar catálogo/enum para "rol" si crecen los roles.
-- 5) Ajustar rango exacto de "nota" según regla institucional.
