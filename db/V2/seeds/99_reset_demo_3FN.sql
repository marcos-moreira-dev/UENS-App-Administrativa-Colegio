-- ============================================================
-- Unidad Educativa Niñitos Soñadores
-- RESET DEMO (3FN - V2) - Limpieza de datos de prueba / "mocks"
-- Archivo sugerido: 99_reset_demo_3FN.sql
-- Base tecnica objetivo: SchoolManagerNinitosSonadores
-- Ejecutar conectado a esa base
-- ============================================================

BEGIN;

TRUNCATE TABLE
    auditoria_evento,
    reporte_solicitud_queue,
    calificacion,
    clase,
    estudiante,
    docente,
    asignatura,
    representante_legal,
    seccion,
    usuario_sistema_administrativo
RESTART IDENTITY CASCADE;

COMMIT;
