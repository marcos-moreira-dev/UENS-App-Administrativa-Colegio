-- ============================================================
-- 02_seed_demo_3FN.sql (DEV ONLY)
-- UENS - PostgreSQL V2 (3FN)
-- Base tecnica objetivo: SchoolManagerNinitosSonadores
-- Ejecutar conectado a esa base, luego de aplicar V2_3FN.sql
-- Seed masivo para pruebas funcionales y de carga local
-- ============================================================

BEGIN;

TRUNCATE TABLE
  auditoria_evento,
  reporte_solicitud_queue,
  calificacion,
  clase,
  estudiante,
  asignatura,
  docente,
  seccion,
  representante_legal,
  usuario_sistema_administrativo
RESTART IDENTITY CASCADE;

-- ============================================================
-- 1) Usuarios del sistema (BCrypt real para pruebas locales)
-- ============================================================
INSERT INTO usuario_sistema_administrativo (nombre_login, password_hash, rol, estado)
VALUES
  -- password: admin123
  ('admin',      '$2a$10$Fa1LKnNzZR7ryNrB6cubgucO53r2Xx4WHL9VXvvTC3bp8cZAoTuXK', 'ADMIN',      'ACTIVO'),
  -- password: secretaria123
  ('secretaria', '$2a$10$V/95bjJxIxdQADL7gdFLuOlhkbzd345SzV74i.EoAaADl7Y1xBy0i', 'SECRETARIA', 'ACTIVO');

-- ============================================================
-- 2) Representantes (250)
-- ============================================================
WITH lists AS (
  SELECT
    ARRAY['Diana','Paola','Karla','Andrea','Valeria','Camila','Sofia','Gabriela','Mariana','Daniela','Monica','Rocio','Ximena','Patricia','Lorena']::text[] AS fn_f,
    ARRAY['Juan','Carlos','Luis','Diego','Jorge','Andres','Ricardo','Miguel','Santiago','Jose','Pedro','Kevin','Bryan','Cristian','Fernando']::text[] AS fn_m,
    ARRAY['Gomez','Mero','Zambrano','Vera','Mendoza','Paredes','Castro','Garcia','Rodriguez','Torres','Lopez','Cedeno','Macias','Alvarez','Chavez','Ortega','Perez','Sanchez','Bravo','Loor']::text[] AS ln
)
INSERT INTO representante_legal (nombres, apellidos, telefono, correo_electronico)
SELECT
  concat_ws(' ',
    CASE WHEN random() < 0.55
      THEN lists.fn_f[(1 + floor(random()*array_length(lists.fn_f,1)))::int]
      ELSE lists.fn_m[(1 + floor(random()*array_length(lists.fn_m,1)))::int]
    END,
    CASE WHEN random() < 0.55
      THEN lists.fn_f[(1 + floor(random()*array_length(lists.fn_f,1)))::int]
      ELSE lists.fn_m[(1 + floor(random()*array_length(lists.fn_m,1)))::int]
    END
  ) AS nombres,
  concat_ws(' ',
    lists.ln[(1 + floor(random()*array_length(lists.ln,1)))::int],
    lists.ln[(1 + floor(random()*array_length(lists.ln,1)))::int]
  ) AS apellidos,
  CASE WHEN random() < 0.05 THEN NULL
       ELSE '09' || lpad((floor(random()*100000000)::int)::text, 8, '0')
  END AS telefono,
  CASE WHEN random() < 0.08 THEN NULL
       ELSE format('rep%04s@uens.test', g.i)
  END AS correo_electronico
FROM generate_series(1,250) AS g(i), lists;

-- ============================================================
-- 3) Secciones (2026-2027) grados 1..7 paralelos A/B/C
-- ============================================================
INSERT INTO seccion (grado, paralelo, cupo_maximo, anio_lectivo, estado)
SELECT g, p, 35, '2026-2027', 'ACTIVO'
FROM generate_series(1,7) g
CROSS JOIN (VALUES ('A'),('B'),('C')) v(p);

-- ============================================================
-- 4) Docentes (35)
-- ============================================================
WITH lists AS (
  SELECT
    ARRAY['Maria','Diana','Paola','Andrea','Valeria','Camila','Sofia','Gabriela','Mariana','Daniela','Rocio','Ximena','Patricia','Lorena','Veronica']::text[] AS fn_f,
    ARRAY['Juan','Carlos','Luis','Diego','Jorge','Andres','Ricardo','Miguel','Santiago','Jose','Pedro','Kevin','Bryan','Cristian','Fernando']::text[] AS fn_m,
    ARRAY['Gomez','Mero','Zambrano','Vera','Mendoza','Paredes','Castro','Garcia','Rodriguez','Torres','Lopez','Cedeno','Macias','Alvarez','Chavez','Ortega','Perez','Sanchez','Bravo','Loor']::text[] AS ln
)
INSERT INTO docente (nombres, apellidos, telefono, correo_electronico, estado)
SELECT
  concat_ws(' ',
    CASE WHEN random() < 0.55
      THEN lists.fn_f[(1 + floor(random()*array_length(lists.fn_f,1)))::int]
      ELSE lists.fn_m[(1 + floor(random()*array_length(lists.fn_m,1)))::int]
    END,
    CASE WHEN random() < 0.55
      THEN lists.fn_f[(1 + floor(random()*array_length(lists.fn_f,1)))::int]
      ELSE lists.fn_m[(1 + floor(random()*array_length(lists.fn_m,1)))::int]
    END
  ) AS nombres,
  concat_ws(' ',
    lists.ln[(1 + floor(random()*array_length(lists.ln,1)))::int],
    lists.ln[(1 + floor(random()*array_length(lists.ln,1)))::int]
  ) AS apellidos,
  CASE WHEN random() < 0.05 THEN NULL
       ELSE '09' || lpad((floor(random()*100000000)::int)::text, 8, '0')
  END AS telefono,
  format('doc%03s@uens.test', g.i) AS correo_electronico,
  CASE WHEN random() < 0.10 THEN 'INACTIVO' ELSE 'ACTIVO' END AS estado
FROM generate_series(1,35) AS g(i), lists;

-- ============================================================
-- 5) Asignaturas (7 por grado)
-- ============================================================
WITH sub AS (
  SELECT * FROM (VALUES
    ('Matematica',          'Ciencias',   'Numeros, operaciones, geometria y problemas'),
    ('Lengua y Literatura', 'Lengua',     'Lectura, escritura y comprension'),
    ('Ciencias Naturales',  'Ciencias',   'Seres vivos, materia, energia y ambiente'),
    ('Estudios Sociales',   'Sociales',   'Historia, geografia y ciudadania'),
    ('Ingles',              'Idiomas',    'Vocabulario, listening y speaking'),
    ('Educacion Fisica',    'Deportes',   'Coordinacion, resistencia y juego limpio'),
    ('Computacion',         'Tecnologia', 'Ofimatica, logica y herramientas digitales')
  ) AS t(nombre, area, descripcion)
)
INSERT INTO asignatura (nombre, area, descripcion, grado, estado)
SELECT sub.nombre, sub.area, sub.descripcion, g.grado, 'ACTIVO'
FROM generate_series(1,7) AS g(grado)
CROSS JOIN sub;

-- ============================================================
-- 6) Clases (7 por seccion) horario fijo + docente random
-- ============================================================
WITH slots AS (
  SELECT * FROM (VALUES
    (1, 'LUNES'::varchar(15),     '07:00'::time, '07:45'::time, 'Matematica'),
    (2, 'MARTES'::varchar(15),    '07:00'::time, '07:45'::time, 'Lengua y Literatura'),
    (3, 'MIERCOLES'::varchar(15), '07:00'::time, '07:45'::time, 'Ciencias Naturales'),
    (4, 'JUEVES'::varchar(15),    '07:00'::time, '07:45'::time, 'Estudios Sociales'),
    (5, 'VIERNES'::varchar(15),   '07:00'::time, '07:45'::time, 'Ingles'),
    (6, 'SABADO'::varchar(15),    '07:00'::time, '07:45'::time, 'Educacion Fisica'),
    (7, 'LUNES'::varchar(15),     '07:50'::time, '08:35'::time, 'Computacion')
  ) AS t(rn, dia_semana, hora_inicio, hora_fin, asig_nombre)
)
INSERT INTO clase (dia_semana, hora_inicio, hora_fin, estado, seccion_id, asignatura_id, docente_id)
SELECT
  sl.dia_semana,
  sl.hora_inicio,
  sl.hora_fin,
  'ACTIVO',
  s.pk_id AS seccion_id,
  a.pk_id AS asignatura_id,
  CASE WHEN random() < 0.15 THEN NULL
       ELSE (SELECT d.pk_id FROM docente d WHERE d.estado='ACTIVO' ORDER BY random() LIMIT 1)
  END AS docente_id
FROM seccion s
JOIN slots sl ON true
JOIN asignatura a ON a.grado = s.grado AND a.nombre = sl.asig_nombre
WHERE s.estado='ACTIVO';

-- ============================================================
-- 7) Estudiantes (25..35 por seccion), representante random
-- ============================================================
WITH lists AS (
  SELECT
    ARRAY['Diana','Paola','Karla','Andrea','Valeria','Camila','Sofia','Gabriela','Mariana','Daniela','Monica','Rocio','Ximena','Patricia','Lorena','Maria','Alejandra','Nicole','Melanie','Emily']::text[] AS fn_f,
    ARRAY['Juan','Carlos','Luis','Diego','Jorge','Andres','Ricardo','Miguel','Santiago','Jose','Pedro','Kevin','Bryan','Cristian','Fernando','Mateo','Sebastian','Dylan','Gael','Ian']::text[] AS fn_m,
    ARRAY['Gomez','Mero','Zambrano','Vera','Mendoza','Paredes','Castro','Garcia','Rodriguez','Torres','Lopez','Cedeno','Macias','Alvarez','Chavez','Ortega','Perez','Sanchez','Bravo','Loor']::text[] AS ln
),
sec AS (
  SELECT pk_id, grado FROM seccion WHERE estado='ACTIVO'
)
INSERT INTO estudiante (nombres, apellidos, fecha_nacimiento, estado, representante_legal_id, seccion_id)
SELECT
  concat_ws(' ',
    CASE WHEN random() < 0.50
      THEN lists.fn_f[(1 + floor(random()*array_length(lists.fn_f,1)))::int]
      ELSE lists.fn_m[(1 + floor(random()*array_length(lists.fn_m,1)))::int]
    END,
    CASE WHEN random() < 0.50
      THEN lists.fn_f[(1 + floor(random()*array_length(lists.fn_f,1)))::int]
      ELSE lists.fn_m[(1 + floor(random()*array_length(lists.fn_m,1)))::int]
    END
  ) AS nombres,
  concat_ws(' ',
    lists.ln[(1 + floor(random()*array_length(lists.ln,1)))::int],
    lists.ln[(1 + floor(random()*array_length(lists.ln,1)))::int]
  ) AS apellidos,
  (
    current_date
    - make_interval(years => (sec.grado + 5 + floor(random()*2))::int)
    - make_interval(days  => floor(random()*365)::int)
  )::date AS fecha_nacimiento,
  CASE WHEN random() < 0.03 THEN 'INACTIVO' ELSE 'ACTIVO' END AS estado,
  (SELECT r.pk_id FROM representante_legal r ORDER BY random() LIMIT 1) AS representante_legal_id,
  sec.pk_id AS seccion_id
FROM sec
CROSS JOIN LATERAL generate_series(1, (25 + floor(random()*11))::int) gs(n)
CROSS JOIN lists;

-- ============================================================
-- 8) Calificaciones (parcial 1 siempre; parcial 2 en 95%)
-- ============================================================

-- Parcial 1
INSERT INTO calificacion (numero_parcial, nota, fecha_registro, observacion, estudiante_id, clase_id)
SELECT
  1,
  ROUND((
    CASE
      WHEN random() < 0.08 THEN (random()*4.0)
      ELSE (5.5 + random()*4.5)
    END
  )::numeric, 2) AS nota,
  (current_date - (floor(random()*25))::int),
  CASE
    WHEN random() < 0.10 THEN 'Necesita refuerzo'
    WHEN random() < 0.35 THEN 'Buen trabajo'
    ELSE 'Excelente'
  END,
  e.pk_id,
  c.pk_id
FROM estudiante e
JOIN clase c ON c.seccion_id = e.seccion_id;

-- Parcial 2 (95%)
INSERT INTO calificacion (numero_parcial, nota, fecha_registro, observacion, estudiante_id, clase_id)
SELECT
  2,
  ROUND((
    CASE
      WHEN random() < 0.06 THEN (random()*4.5)
      ELSE (6.0 + random()*4.0)
    END
  )::numeric, 2) AS nota,
  (current_date - (floor(random()*10))::int),
  CASE
    WHEN random() < 0.08 THEN 'Debe practicar en casa'
    WHEN random() < 0.30 THEN 'Progreso notable'
    ELSE 'Muy bien'
  END,
  e.pk_id,
  c.pk_id
FROM estudiante e
JOIN clase c ON c.seccion_id = e.seccion_id
WHERE random() > 0.05;

COMMIT;

-- ============================================================
-- Quick checks (opcional)
-- ============================================================
-- SELECT s.grado, s.paralelo, COUNT(*) AS estudiantes
-- FROM estudiante e JOIN seccion s ON s.pk_id = e.seccion_id
-- GROUP BY 1,2 ORDER BY 1,2;
--
-- SELECT COUNT(DISTINCT representante_legal_id) AS reps_usados FROM estudiante;
