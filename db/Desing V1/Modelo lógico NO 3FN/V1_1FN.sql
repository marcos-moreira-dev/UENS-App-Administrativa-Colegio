-- ============================================================
-- Unidad Educativa Niñitos Soñadores
-- PostgreSQL - V1 (Primera Forma Normal / 1FN)
-- Objetivo:
--   - Mantener la V0 como base conceptual-operativa
--   - Ajustar estructura para 1FN (atomicidad más estricta)
--   - AÚN no llegar a 2FN/3FN (se conservan atributos derivados)
-- ============================================================

BEGIN;

DROP TABLE IF EXISTS calificacion CASCADE;
DROP TABLE IF EXISTS clase CASCADE;
DROP TABLE IF EXISTS asignatura CASCADE;
DROP TABLE IF EXISTS docente CASCADE;
DROP TABLE IF EXISTS estudiante CASCADE;
DROP TABLE IF EXISTS representante_legal CASCADE;
DROP TABLE IF EXISTS seccion CASCADE;
DROP TABLE IF EXISTS usuario_sistema_administrativo CASCADE;

CREATE TABLE usuario_sistema_administrativo (
    pk_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_login         VARCHAR(80) NOT NULL,
    password_hash        TEXT NOT NULL,
    rol                  VARCHAR(40) NOT NULL DEFAULT 'ADMINISTRATIVO',
    estado               VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT uq_usuario_sistema_login UNIQUE (nombre_login),
    CONSTRAINT ck_usuario_sistema_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE representante_legal (
    pk_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombres              VARCHAR(120) NOT NULL,
    apellidos            VARCHAR(120) NOT NULL,
    telefono             VARCHAR(30),
    correo_electronico   VARCHAR(254)
);

CREATE TABLE seccion (
    pk_id                             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    grado                             SMALLINT NOT NULL,
    paralelo                          VARCHAR(10) NOT NULL,
    cupo_maximo                       SMALLINT NOT NULL DEFAULT 35,
    anio_lectivo                      VARCHAR(20) NOT NULL,
    estado                            VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',
    cantidad_estudiantes_registrados  INTEGER,
    CONSTRAINT ck_seccion_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT ck_seccion_cupo_maximo CHECK (cupo_maximo > 0 AND cupo_maximo <= 35),
    CONSTRAINT ck_seccion_grado CHECK (grado >= 1 AND grado <= 7),
    CONSTRAINT ck_seccion_anio_lectivo_formato CHECK (anio_lectivo ~ '^[0-9]{4}-[0-9]{4}$'),
    CONSTRAINT ck_seccion_cantidad_est_reg CHECK (
        cantidad_estudiantes_registrados IS NULL
        OR (cantidad_estudiantes_registrados >= 0 AND cantidad_estudiantes_registrados <= 35)
    ),
    CONSTRAINT uq_seccion_unica UNIQUE (anio_lectivo, grado, paralelo)
);

CREATE TABLE docente (
    pk_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombres              VARCHAR(120) NOT NULL,
    apellidos            VARCHAR(120) NOT NULL,
    telefono             VARCHAR(30),
    correo_electronico   VARCHAR(254),
    estado               VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT ck_docente_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

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

CREATE TABLE estudiante (
    pk_id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombres                 VARCHAR(120) NOT NULL,
    apellidos               VARCHAR(120) NOT NULL,
    fecha_nacimiento        DATE NOT NULL,
    edad                    SMALLINT,
    estado                  VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',
    representante_legal_id  BIGINT NOT NULL,
    seccion_id              BIGINT NULL,
    CONSTRAINT ck_estudiante_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT ck_estudiante_edad CHECK (edad IS NULL OR (edad >= 0 AND edad <= 120)),
    CONSTRAINT fk_estudiante_representante_legal
        FOREIGN KEY (representante_legal_id) REFERENCES representante_legal (pk_id),
    CONSTRAINT fk_estudiante_seccion
        FOREIGN KEY (seccion_id) REFERENCES seccion (pk_id)
);

-- 1FN (criterio estricto): franja_horaria -> dia_semana + hora_inicio + hora_fin
CREATE TABLE clase (
    pk_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    dia_semana           VARCHAR(15) NOT NULL,
    hora_inicio          TIME NOT NULL,
    hora_fin             TIME NOT NULL,
    estado               VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',
    seccion_id           BIGINT NOT NULL,
    asignatura_id        BIGINT NOT NULL,
    docente_id           BIGINT NULL,
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
    CONSTRAINT uq_clase_operativa UNIQUE (seccion_id, asignatura_id, dia_semana, hora_inicio, hora_fin)
);

CREATE TABLE calificacion (
    pk_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    numero_parcial       SMALLINT NOT NULL,
    nota                 NUMERIC(5,2) NOT NULL,
    fecha_registro       DATE,
    observacion          TEXT,
    estudiante_id        BIGINT NOT NULL,
    clase_id             BIGINT NOT NULL,
    CONSTRAINT ck_calificacion_numero_parcial CHECK (numero_parcial IN (1, 2)),
    CONSTRAINT ck_calificacion_nota CHECK (nota >= 0),
    CONSTRAINT fk_calificacion_estudiante
        FOREIGN KEY (estudiante_id) REFERENCES estudiante (pk_id),
    CONSTRAINT fk_calificacion_clase
        FOREIGN KEY (clase_id) REFERENCES clase (pk_id),
    CONSTRAINT uq_calificacion_estudiante_clase_parcial
        UNIQUE (estudiante_id, clase_id, numero_parcial)
);

CREATE INDEX idx_estudiante_representante_legal_id ON estudiante (representante_legal_id);
CREATE INDEX idx_estudiante_seccion_id             ON estudiante (seccion_id);

CREATE INDEX idx_clase_seccion_id                  ON clase (seccion_id);
CREATE INDEX idx_clase_asignatura_id               ON clase (asignatura_id);
CREATE INDEX idx_clase_docente_id                  ON clase (docente_id);
CREATE INDEX idx_clase_dia_hora                    ON clase (dia_semana, hora_inicio, hora_fin);

CREATE INDEX idx_calificacion_estudiante_id        ON calificacion (estudiante_id);
CREATE INDEX idx_calificacion_clase_id             ON calificacion (clase_id);

COMMIT;
