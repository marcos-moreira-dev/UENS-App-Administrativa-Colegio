# =========================
# STAGING (NO-SRC) GENERATOR
# =========================

# 1) Carpeta staging (cámbiala)
$ROOT = "C:\...Sistema UE Niñitos Soñadores\backend\Nueva carpeta"

# 2) Dónde quieres scripts (en tu repo real vi tools\scripts)
$SCRIPTS_DIR = "tools\scripts"

# 3) Dónde guardas la doc del backend
$DOCS_DIR = "docs\backend_v_1"

function New-File {
  param(
    [string]$relativePath,
    [string]$content = $null
  )

  $fullPath = Join-Path $ROOT $relativePath
  $dir = Split-Path $fullPath

  New-Item -ItemType Directory -Path $dir -Force | Out-Null

  if ($null -eq $content) {
    New-Item -ItemType File -Path $fullPath -Force | Out-Null
  } else {
    Set-Content -Path $fullPath -Value $content -Encoding UTF8
  }
}

# -------------------------
# Root files (no-src)
# -------------------------
New-Item -ItemType Directory -Path $ROOT -Force | Out-Null

New-File ".editorconfig"
New-File ".gitattributes"
New-File ".gitignore"
New-File "README.md" "# UENS Backend (V1)`r`n`r`n> Placeholder. Copia aquí tu README real.`r`n"
New-File ".env.example" @"
# =========================
# Variables mínimas (V1)
# =========================

# Perfil / app
SPRING_PROFILES_ACTIVE=
SERVER_PORT=

# PostgreSQL
DB_HOST=
DB_PORT=
DB_NAME=SchoolManagerNinitosSonadores
DB_USER=
DB_PASSWORD=

# JWT
JWT_SECRET=
JWT_EXPIRATION_SECONDS=
JWT_ISSUER=

# Swagger (opcional)
SWAGGER_ENABLED=

# Logs (opcional)
LOG_LEVEL_ROOT=
LOG_LEVEL_APP=
"@

# Docker (si vas a usarlos)
New-File "docker-compose.yml"
New-File "Dockerfile"

# Licencias (opcionales, pero recomendadas si distribuyes)
New-File "LICENSE"
New-File "NOTICE"
New-File "THIRD-PARTY-NOTICES.md"

# -------------------------
# Docs (tu set de documentos)
# -------------------------
$docFiles = @(
  "00_backend_v_1_indice_y_mapa_documental.md",
  "01_backend_v_1_vision_y_alcance.md",
  "02_backend_v_1_arquitectura_general.md",
  "03_backend_v_1_convenciones_y_estandares_codigo.md",
  "04_backend_v_1_modelado_aplicacion_y_modulos.md",
  "05_backend_v_1_diseno_api_contrato_respuestas_y_errores.md",
  "06_backend_v_1_api_endpoints_y_casos_de_uso.md",
  "07_backend_v_1_validaciones_reglas_negocio_y_excepciones.md",
  "08_backend_v_1_paginacion_filtros_ordenamiento_y_consultas.md",
  "09_backend_v_1_seguridad_documentacion_y_despliegue_minimo.md",
  "10_backend_v_1_reporte_solicitudes_cola_simple_db_queue.md",
  "11_backend_v_1_arbol_archivos_proyecto_hipotetico.md",
  "13_backend_v_1_dtos_mappers_matriz_permisos_filtros_y_mapeo_jpa.md",
  "14_backend_v_1_arbol_archivos_completo_sugerido_implementacion.md"
)

foreach ($f in $docFiles) {
  New-File (Join-Path $DOCS_DIR $f)
}

# Extras de docs (opcionales, según el árbol recomendado)
New-File "docs\adr\ADR-001-api-response.md"
New-File "docs\adr\ADR-002-jwt-stateless.md"
New-File "docs\adr\ADR-003-db-queue-reportes.md"
New-File "docs\adr\ADR-004-mappers-manuales.md"

New-File "docs\despliegue\variables_entorno.md"
New-File "docs\despliegue\docker_local.md"
New-File "docs\despliegue\checklist_release_v1.md"

New-File "docs\ejemplos\requests_http.md"
New-File "docs\ejemplos\ejemplos_errores_api.md"
New-File "docs\ejemplos\ejemplos_paginacion_filtros.md"

# -------------------------
# Scripts (vacíos; tú pegas luego)
# -------------------------
New-File (Join-Path $SCRIPTS_DIR "dev\run-local.ps1")
New-File (Join-Path $SCRIPTS_DIR "dev\run-local.sh")
New-File (Join-Path $SCRIPTS_DIR "dev\up-db.sh")
New-File (Join-Path $SCRIPTS_DIR "dev\down-db.sh")

New-File (Join-Path $SCRIPTS_DIR "db\reset-dev-db.sh")
New-File (Join-Path $SCRIPTS_DIR "db\seed-dev-data.sh")
New-File (Join-Path $SCRIPTS_DIR "db\backup-dev-db.sh")

New-File (Join-Path $SCRIPTS_DIR "quality\format-check.sh")
New-File (Join-Path $SCRIPTS_DIR "quality\lint-check.sh")

New-File (Join-Path $SCRIPTS_DIR "ci\build.sh")
New-File (Join-Path $SCRIPTS_DIR "ci\smoke-test.sh")

# -------------------------
# DB folder (opcional, si quieres SQL fuera de Flyway)
# -------------------------
New-File "db\README.md" "# DB`r`n`r`n> Placeholder para scripts SQL (V2_3FN.sql, seeds, etc.)`r`n"
New-File "db\V2_3FN.sql"
New-File "db\seed_dev.sql"

Write-Host "Listo. No-src scaffolding creado en: $ROOT"
# Opcional: ver árbol
# cmd /c "tree `"$ROOT`" /f"
