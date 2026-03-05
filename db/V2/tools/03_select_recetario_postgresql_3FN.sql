-- ============================================================
-- Unidad Educativa Niñitos Soñadores
-- PostgreSQL - Recetario de SELECTs (3FN)
-- Base tecnica objetivo:
--   SchoolManagerNinitosSonadores
-- Archivo sugerido:
--   tools/sql/01_select_recetario_postgresql_3FN.sql
-- o
--   queries/01_select_recetario_postgresql_3FN.sql
--
-- PROPÓSITO
--   - Tener ejemplos de SELECT para practicar y recordar sintaxis.
--   - Reutilizar consultas comunes para backend, debugging y frontend.
--   - Mostrar casos "de la vida real" sobre TU esquema 3FN.
--
-- NOTA
--   Este archivo NO modifica datos. Solo hace consultas (SELECT/CTE/EXPLAIN).
--   Puedes ejecutar bloques por separado.
-- ============================================================


/* ============================================================
   0) RECORDATORIO RÁPIDO DE SINTAXIS BÁSICA DE SELECT
   ------------------------------------------------------------
   SELECT <columnas o expresiones>
   FROM <tabla_o_subconsulta> [alias]
   [JOIN ... ON ...]
   [WHERE ...]
   [GROUP BY ...]
   [HAVING ...]
   [ORDER BY ...]
   [LIMIT ...] [OFFSET ...];

   Orden lógico (cómo se evalúa conceptualmente):
   FROM -> JOIN -> WHERE -> GROUP BY -> HAVING -> SELECT -> ORDER BY -> LIMIT/OFFSET
   ============================================================ */


-- ============================================================
-- 1) SELECT básico: traer todo (solo para exploración rápida)
-- Sintaxis:
--   SELECT * FROM tabla;
-- Nota:
--   En código productivo suele preferirse listar columnas explícitas.
-- ============================================================
SELECT * FROM seccion;


-- ============================================================
-- 2) SELECT con columnas específicas y alias
-- Sintaxis:
--   SELECT columna AS alias, ... FROM tabla;
-- "AS" es opcional, pero mejora legibilidad.
-- ============================================================
SELECT
    pk_id AS seccion_id,
    grado,
    paralelo,
    anio_lectivo,
    estado
FROM seccion;


-- ============================================================
-- 3) WHERE con comparación exacta
-- Sintaxis:
--   WHERE columna = valor
-- ============================================================
SELECT
    pk_id, grado, paralelo, anio_lectivo, estado
FROM seccion
WHERE grado = 1;


-- ============================================================
-- 4) WHERE con múltiples condiciones (AND / OR)
-- Sintaxis:
--   WHERE cond1 AND cond2
--   WHERE cond1 OR cond2
-- Usa paréntesis cuando mezcles AND/OR para evitar confusiones.
-- ============================================================
SELECT
    pk_id, grado, paralelo, anio_lectivo, estado
FROM seccion
WHERE anio_lectivo = '2026-2027'
  AND (grado = 1 OR grado = 4);


-- ============================================================
-- 5) ORDER BY (ordenamiento)
-- Sintaxis:
--   ORDER BY columna [ASC|DESC], otra_columna ...
-- ASC es por defecto.
-- ============================================================
SELECT
    pk_id, grado, paralelo, anio_lectivo
FROM seccion
ORDER BY grado ASC, paralelo ASC;


-- ============================================================
-- 6) LIMIT y OFFSET (paginación simple)
-- Sintaxis:
--   LIMIT n
--   OFFSET n
-- Usado para pruebas rápidas o paginación.
-- ============================================================
SELECT
    pk_id, nombres, apellidos, estado
FROM estudiante
ORDER BY apellidos, nombres
LIMIT 5 OFFSET 0;


-- ============================================================
-- 7) DISTINCT (evitar duplicados en resultados)
-- Sintaxis:
--   SELECT DISTINCT columna FROM tabla;
-- ============================================================
SELECT DISTINCT
    grado
FROM asignatura
ORDER BY grado;


-- ============================================================
-- 8) IN (pertenece a un conjunto)
-- Sintaxis:
--   WHERE columna IN (valor1, valor2, ...)
-- ============================================================
SELECT
    pk_id, nombre, grado, area
FROM asignatura
WHERE grado IN (1, 4)
ORDER BY grado, nombre;


-- ============================================================
-- 9) BETWEEN (rango inclusivo)
-- Sintaxis:
--   WHERE columna BETWEEN a AND b
-- Incluye ambos extremos.
-- ============================================================
SELECT
    pk_id, nota, numero_parcial, fecha_registro
FROM calificacion
WHERE nota BETWEEN 8.00 AND 10.00
ORDER BY nota DESC;


-- ============================================================
-- 10) LIKE / ILIKE (búsqueda por texto)
-- LIKE  = sensible a mayúsculas
-- ILIKE = no sensible a mayúsculas (útil en PostgreSQL)
-- Sintaxis:
--   WHERE columna LIKE 'patrón%'
--   WHERE columna ILIKE '%texto%'
-- ============================================================
SELECT
    pk_id, nombre, grado
FROM asignatura
WHERE nombre ILIKE '%mate%'
ORDER BY grado, nombre;


-- ============================================================
-- 11) IS NULL / IS NOT NULL
-- Recuerda:
--   NULL no se compara con "="
--   usa IS NULL o IS NOT NULL
-- ============================================================
SELECT
    pk_id, nombres, apellidos, telefono, correo_electronico
FROM docente
WHERE telefono IS NULL;


-- ============================================================
-- 12) CONCATENACIÓN y columnas calculadas
-- En PostgreSQL puedes concatenar con ||
-- ============================================================
SELECT
    e.pk_id,
    e.nombres || ' ' || e.apellidos AS estudiante,
    e.fecha_nacimiento
FROM estudiante e
ORDER BY estudiante;


-- ============================================================
-- 13) CAST (conversión de tipos)
-- PostgreSQL permite:
--   CAST(valor AS tipo)
--   valor::tipo          -- sintaxis corta (muy usada)
-- ============================================================
SELECT
    '2026-2027'::text AS anio_lectivo_texto,
    CAST('08:00' AS time) AS hora_ejemplo;


-- ============================================================
-- 14) CASE (if/else en SQL)
-- Sintaxis:
--   CASE
--     WHEN condición THEN valor
--     WHEN ...
--     ELSE valor
--   END
-- ============================================================
SELECT
    c.pk_id,
    c.nota,
    CASE
        WHEN c.nota >= 9 THEN 'ALTO'
        WHEN c.nota >= 7 THEN 'MEDIO'
        ELSE 'BAJO'
    END AS nivel_rendimiento
FROM calificacion c
ORDER BY c.nota DESC;


-- ============================================================
-- 15) COALESCE (reemplazar NULL)
-- Devuelve el primer valor NO NULL.
-- Muy útil para presentar datos en frontend/reportes.
-- ============================================================
SELECT
    d.pk_id,
    d.nombres || ' ' || d.apellidos AS docente,
    COALESCE(d.telefono, 'SIN TELÉFONO') AS telefono_mostrado
FROM docente d
ORDER BY docente;


-- ============================================================
-- 16) INNER JOIN (solo coincidencias)
-- Sintaxis:
--   FROM tabla1 t1
--   JOIN tabla2 t2 ON t1.fk = t2.pk
-- Caso:
--   Estudiantes con su representante y su sección (si existe sección, aquí exigimos JOIN)
-- ============================================================
SELECT
    e.pk_id AS estudiante_id,
    e.nombres || ' ' || e.apellidos AS estudiante,
    rl.nombres || ' ' || rl.apellidos AS representante,
    s.grado,
    s.paralelo,
    s.anio_lectivo
FROM estudiante e
JOIN representante_legal rl ON rl.pk_id = e.representante_legal_id
JOIN seccion s ON s.pk_id = e.seccion_id
ORDER BY s.grado, s.paralelo, estudiante;


-- ============================================================
-- 17) LEFT JOIN (trae todos los de la izquierda aunque no haya coincidencia)
-- Útil para detectar "faltantes" (ej. estudiantes sin sección).
-- ============================================================
SELECT
    e.pk_id,
    e.nombres || ' ' || e.apellidos AS estudiante,
    s.pk_id AS seccion_id,
    s.grado,
    s.paralelo
FROM estudiante e
LEFT JOIN seccion s ON s.pk_id = e.seccion_id
ORDER BY estudiante;


-- ============================================================
-- 18) JOIN múltiple: calificación con contexto completo
-- Este SELECT es muy útil para tablas del frontend / reportes.
-- ============================================================
SELECT
    e.nombres || ' ' || e.apellidos AS estudiante,
    s.anio_lectivo,
    s.grado,
    s.paralelo,
    a.nombre AS asignatura,
    cl.dia_semana,
    cl.hora_inicio,
    cl.hora_fin,
    c.numero_parcial,
    c.nota,
    c.fecha_registro
FROM calificacion c
JOIN estudiante e  ON e.pk_id = c.estudiante_id
JOIN clase cl      ON cl.pk_id = c.clase_id
JOIN seccion s     ON s.pk_id = cl.seccion_id
JOIN asignatura a  ON a.pk_id = cl.asignatura_id
ORDER BY s.grado, s.paralelo, estudiante, a.nombre, c.numero_parcial;


-- ============================================================
-- 19) Agregaciones básicas (COUNT, AVG, MIN, MAX)
-- Sintaxis:
--   SELECT COUNT(*), AVG(col), ... FROM tabla;
-- ============================================================
SELECT
    COUNT(*) AS total_calificaciones,
    AVG(nota) AS promedio_general,
    MIN(nota) AS nota_minima,
    MAX(nota) AS nota_maxima
FROM calificacion;


-- ============================================================
-- 20) GROUP BY (agrupar) + agregación
-- Regla:
--   Toda columna del SELECT que NO esté agregada debe ir en GROUP BY.
-- Caso:
--   Cantidad de estudiantes por sección.
-- ============================================================
SELECT
    s.pk_id AS seccion_id,
    s.anio_lectivo,
    s.grado,
    s.paralelo,
    COUNT(e.pk_id) AS total_estudiantes
FROM seccion s
LEFT JOIN estudiante e ON e.seccion_id = s.pk_id
GROUP BY s.pk_id, s.anio_lectivo, s.grado, s.paralelo
ORDER BY s.grado, s.paralelo;


-- ============================================================
-- 21) HAVING (filtra grupos, no filas)
-- Sintaxis:
--   ... GROUP BY ...
--   HAVING condición_sobre_agregados
-- Caso:
--   Secciones con 2 o más estudiantes
-- ============================================================
SELECT
    s.pk_id,
    s.grado,
    s.paralelo,
    COUNT(e.pk_id) AS total_estudiantes
FROM seccion s
LEFT JOIN estudiante e ON e.seccion_id = s.pk_id
GROUP BY s.pk_id, s.grado, s.paralelo
HAVING COUNT(e.pk_id) >= 2
ORDER BY s.grado, s.paralelo;


-- ============================================================
-- 22) Subconsulta en WHERE (IN / SELECT)
-- Caso:
--   Estudiantes que tienen al menos una calificación registrada.
-- ============================================================
SELECT
    e.pk_id,
    e.nombres || ' ' || e.apellidos AS estudiante
FROM estudiante e
WHERE e.pk_id IN (
    SELECT DISTINCT c.estudiante_id
    FROM calificacion c
)
ORDER BY estudiante;


-- ============================================================
-- 23) EXISTS (muy útil y eficiente para "existe/no existe")
-- Sintaxis:
--   WHERE EXISTS (SELECT 1 FROM ... WHERE ... correlacionado)
-- Caso:
--   Asignaturas que ya tienen al menos una clase planificada.
-- ============================================================
SELECT
    a.pk_id,
    a.nombre,
    a.grado
FROM asignatura a
WHERE EXISTS (
    SELECT 1
    FROM clase cl
    WHERE cl.asignatura_id = a.pk_id
)
ORDER BY a.grado, a.nombre;


-- ============================================================
-- 24) NOT EXISTS (detectar faltantes)
-- Caso:
--   Docentes sin clases asignadas.
-- ============================================================
SELECT
    d.pk_id,
    d.nombres || ' ' || d.apellidos AS docente
FROM docente d
WHERE NOT EXISTS (
    SELECT 1
    FROM clase cl
    WHERE cl.docente_id = d.pk_id
)
ORDER BY docente;


-- ============================================================
-- 25) CTE con WITH (consulta por etapas / legible)
-- Sintaxis:
--   WITH nombre_cte AS (SELECT ...)
--   SELECT ... FROM nombre_cte;
-- Caso:
--   Promedio por estudiante y luego filtrar mejores promedios.
-- ============================================================
WITH promedio_por_estudiante AS (
    SELECT
        e.pk_id AS estudiante_id,
        e.nombres || ' ' || e.apellidos AS estudiante,
        AVG(c.nota) AS promedio
    FROM estudiante e
    JOIN calificacion c ON c.estudiante_id = e.pk_id
    GROUP BY e.pk_id, e.nombres, e.apellidos
)
SELECT *
FROM promedio_por_estudiante
WHERE promedio >= 8.5
ORDER BY promedio DESC, estudiante;


-- ============================================================
-- 26) Funciones de fecha (PostgreSQL)
-- age(), EXTRACT(), CURRENT_DATE
-- Caso:
--   Edad calculada (aproximada en años) sin guardar columna "edad"
-- Nota:
--   EXTRACT(YEAR FROM age(...)) devuelve años como número.
-- ============================================================
SELECT
    e.pk_id,
    e.nombres || ' ' || e.apellidos AS estudiante,
    e.fecha_nacimiento,
    EXTRACT(YEAR FROM age(CURRENT_DATE, e.fecha_nacimiento))::int AS edad_calculada
FROM estudiante e
ORDER BY edad_calculada DESC, estudiante;


-- ============================================================
-- 27) Agregación condicional con FILTER (PostgreSQL)
-- Sintaxis:
--   COUNT(*) FILTER (WHERE condición)
-- Muy útil para reportes.
-- Caso:
--   Calificaciones por parcial en una sola fila.
-- ============================================================
SELECT
    COUNT(*) AS total_calificaciones,
    COUNT(*) FILTER (WHERE numero_parcial = 1) AS total_parcial_1,
    COUNT(*) FILTER (WHERE numero_parcial = 2) AS total_parcial_2,
    AVG(nota) FILTER (WHERE numero_parcial = 1) AS promedio_parcial_1,
    AVG(nota) FILTER (WHERE numero_parcial = 2) AS promedio_parcial_2
FROM calificacion;


-- ============================================================
-- 28) Window function: ROW_NUMBER()
-- Sintaxis:
--   ROW_NUMBER() OVER (PARTITION BY ... ORDER BY ...)
-- Caso:
--   Ranking de notas por estudiante (de mayor a menor)
-- ============================================================
SELECT
    e.nombres || ' ' || e.apellidos AS estudiante,
    c.numero_parcial,
    c.nota,
    ROW_NUMBER() OVER (
        PARTITION BY e.pk_id
        ORDER BY c.nota DESC, c.pk_id
    ) AS orden_nota_por_estudiante
FROM calificacion c
JOIN estudiante e ON e.pk_id = c.estudiante_id
ORDER BY estudiante, orden_nota_por_estudiante;


-- ============================================================
-- 29) Window function: promedio por sección sin colapsar filas
-- Ventaja:
--   Puedes mostrar cada fila y a la vez un agregado del grupo.
-- ============================================================
SELECT
    e.nombres || ' ' || e.apellidos AS estudiante,
    s.grado,
    s.paralelo,
    c.nota,
    AVG(c.nota) OVER (PARTITION BY s.pk_id) AS promedio_seccion
FROM calificacion c
JOIN estudiante e ON e.pk_id = c.estudiante_id
JOIN clase cl ON cl.pk_id = c.clase_id
JOIN seccion s ON s.pk_id = cl.seccion_id
ORDER BY s.grado, s.paralelo, estudiante, c.nota DESC;


-- ============================================================
-- 30) UNION ALL vs UNION
-- UNION      elimina duplicados (más costo)
-- UNION ALL  conserva duplicados (más rápido)
-- Caso demo:
--   Lista de personas de contacto (docentes + representantes)
-- ============================================================
SELECT
    'DOCENTE' AS tipo_persona,
    d.nombres,
    d.apellidos,
    d.correo_electronico
FROM docente d

UNION ALL

SELECT
    'REPRESENTANTE' AS tipo_persona,
    r.nombres,
    r.apellidos,
    r.correo_electronico
FROM representante_legal r

ORDER BY tipo_persona, apellidos, nombres;


-- ============================================================
-- 31) Subconsulta derivada en FROM (tabla temporal de consulta)
-- Sintaxis:
--   FROM (SELECT ...) alias
-- Caso:
--   Secciones con ocupación y porcentaje de uso del cupo.
-- ============================================================
SELECT
    x.seccion_id,
    x.anio_lectivo,
    x.grado,
    x.paralelo,
    x.cupo_maximo,
    x.total_estudiantes,
    ROUND((x.total_estudiantes::numeric / NULLIF(x.cupo_maximo, 0)) * 100, 2) AS porcentaje_ocupacion
FROM (
    SELECT
        s.pk_id AS seccion_id,
        s.anio_lectivo,
        s.grado,
        s.paralelo,
        s.cupo_maximo,
        COUNT(e.pk_id) AS total_estudiantes
    FROM seccion s
    LEFT JOIN estudiante e ON e.seccion_id = s.pk_id
    GROUP BY s.pk_id, s.anio_lectivo, s.grado, s.paralelo, s.cupo_maximo
) AS x
ORDER BY x.grado, x.paralelo;


-- ============================================================
-- 32) string_agg (PostgreSQL) para concatenar filas en una cadena
-- Sintaxis:
--   string_agg(expresión_texto, separador [ORDER BY ...])
-- Caso:
--   Asignaturas planificadas por sección (como texto)
-- ============================================================
SELECT
    s.anio_lectivo,
    s.grado,
    s.paralelo,
    string_agg(DISTINCT a.nombre, ', ' ORDER BY a.nombre) AS asignaturas_planificadas
FROM seccion s
LEFT JOIN clase cl ON cl.seccion_id = s.pk_id
LEFT JOIN asignatura a ON a.pk_id = cl.asignatura_id
GROUP BY s.pk_id, s.anio_lectivo, s.grado, s.paralelo
ORDER BY s.grado, s.paralelo;


-- ============================================================
-- 33) Consulta de validación de negocio (debugging)
-- Caso:
--   Detectar clases con incoherencia entre grado de sección y grado de asignatura
-- Debería devolver 0 filas si todo está coherente.
-- ============================================================
SELECT
    cl.pk_id AS clase_id,
    s.grado AS grado_seccion,
    a.grado AS grado_asignatura,
    s.paralelo,
    a.nombre AS asignatura
FROM clase cl
JOIN seccion s ON s.pk_id = cl.seccion_id
JOIN asignatura a ON a.pk_id = cl.asignatura_id
WHERE s.grado <> a.grado;


-- ============================================================
-- 34) Consulta de validación de negocio (debugging)
-- Caso:
--   Detectar estudiantes sin representante legal (no debería haber)
-- Debería devolver 0 filas si la FK y el NOT NULL están bien.
-- ============================================================
SELECT
    e.pk_id, e.nombres, e.apellidos
FROM estudiante e
LEFT JOIN representante_legal rl ON rl.pk_id = e.representante_legal_id
WHERE rl.pk_id IS NULL;


-- ============================================================
-- 35) EXPLAIN (plan de ejecución) - NO ejecuta la consulta
-- Sintaxis:
--   EXPLAIN SELECT ...
--   EXPLAIN ANALYZE SELECT ...   -- sí ejecuta y mide tiempos
--
-- Úsalo para aprender performance luego.
-- ============================================================
EXPLAIN
SELECT
    e.nombres || ' ' || e.apellidos AS estudiante,
    c.nota
FROM calificacion c
JOIN estudiante e ON e.pk_id = c.estudiante_id
WHERE c.numero_parcial = 1
ORDER BY c.nota DESC;


-- ============================================================
-- 36) PLANTILLA GENERAL PARA REUSAR EN BACKEND (comentada)
-- Descomenta y adapta:
-- ============================================================
-- SELECT
--     <columnas>
-- FROM <tabla_principal> t
-- JOIN <otra_tabla> o ON o.pk_id = t.<fk>
-- WHERE 1=1
--   AND (<filtro_opcional_1>)
--   AND (<filtro_opcional_2>)
-- ORDER BY <columna> ASC
-- LIMIT <tamano_pagina>
-- OFFSET <desplazamiento>;


-- ============================================================
-- 37) TIPS RÁPIDOS DE POSTGRESQL (comentarios recordatorio)
-- ------------------------------------------------------------
-- - ILIKE: búsqueda case-insensitive (útil en texto)
-- - ::tipo: cast corto (ej. valor::int)
-- - COALESCE(a,b,c): primer no-null
-- - NULLIF(a,b): devuelve NULL si a=b (evita divisiones por cero)
-- - FILTER (WHERE ...): agregación condicional elegante
-- - string_agg(): concatena varias filas en una cadena
-- - EXTRACT(...): extrae partes de fecha/hora
-- - age(): diferencia de fechas útil para edad
-- ============================================================
