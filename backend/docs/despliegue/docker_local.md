# Docker local

## Objetivo
Levantar un entorno local reproducible para backend v1.

## Requisitos
- Docker Desktop reciente
- Docker Compose v2

## Flujo recomendado (DB Docker + API local)
```bash
cd backend/uens-backend
cp .env.example .env
./tools/scripts/dev/up-db.sh
./tools/scripts/dev/run-local.sh
```

## Flujo full Docker (DB + API)
```bash
cd backend/uens-backend
docker compose --profile full up --build
```

## Verificacion rápida
- API: `http://localhost:8080/api/v1/system/ping`
- Swagger: `http://localhost:8080/swagger-ui/index.html`
- DB host local: `localhost:5433`
- Reportes generados: revisar `APP_REPORT_OUTPUT_DIR` (por defecto `reportes-output` dentro del contenedor/proyecto)

## Comandos útiles
```bash
docker compose ps
docker compose logs -f db
docker compose down
```

## Problemas comunes
- Error de conexion DB desde API local:
  - revisar `DB_PORT=5433` en `.env`.
- Puerto ocupado:
  - cambiar `APP_PORT` en `.env`.
- Error al generar archivo de reporte:
  - verificar permisos de escritura en `APP_REPORT_OUTPUT_DIR`.

