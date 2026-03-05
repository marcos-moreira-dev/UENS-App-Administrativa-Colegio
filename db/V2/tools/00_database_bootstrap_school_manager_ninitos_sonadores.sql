-- ============================================================
-- Bootstrap de base tecnica
-- Objetivo:
--   usar un nombre ASCII estable para evitar problemas de encoding
-- Base tecnica objetivo:
--   SchoolManagerNinitosSonadores
--
-- Ejecuta este archivo conectado a la base administrativa `postgres`
-- o a cualquier otra base distinta de la que quieras crear/renombrar.
-- ============================================================

-- ------------------------------------------------------------
-- Opcion A: crear base nueva desde cero
-- ------------------------------------------------------------
-- CREATE DATABASE "SchoolManagerNinitosSonadores"
--     WITH
--     OWNER = postgres
--     ENCODING = 'UTF8'
--     TEMPLATE = template0;

-- ------------------------------------------------------------
-- Opcion B: renombrar base anterior con caracteres no ASCII
-- Importante:
--   1) cerrar conexiones activas a la base vieja
--   2) ejecutar conectado a `postgres`
-- ------------------------------------------------------------
-- ALTER DATABASE "SchoolManagerNiñitosSoñadores"
--     RENAME TO "SchoolManagerNinitosSonadores";

-- ------------------------------------------------------------
-- Verificacion rapida
-- ------------------------------------------------------------
SELECT datname
FROM pg_database
WHERE datname IN (
    'SchoolManagerNiñitosSoñadores',
    'SchoolManagerNinitosSonadores'
)
ORDER BY datname;
