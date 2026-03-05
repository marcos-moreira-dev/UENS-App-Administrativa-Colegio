# UENS Backend

README global del backend para arrancar rapido y entender como esta organizado.

## En una frase
Flujo recomendado para desarrollo diario:
- Docker para PostgreSQL.
- Maven local para la API Spring Boot.

"Todo Docker" queda como opcion para demo, pruebas de integracion o validar la imagen.

## Requisitos
- Java 21
- Docker Desktop (para PostgreSQL y opcion full Docker)
- PowerShell (Windows) o Bash (Linux/macOS)

## Inicio rapido (recomendado)

### Windows (PowerShell)
```powershell
cd backend/uens-backend
Copy-Item .env.example .env
.\tools\scripts\dev\up-db.ps1
.\tools\scripts\dev\run-local.ps1
```

### Linux/macOS (Bash)
```bash
cd backend/uens-backend
cp .env.example .env
./tools/scripts/dev/up-db.sh
./tools/scripts/dev/run-local.sh
```

Que hace este flujo:
- Levanta PostgreSQL con Docker.
- Carga variables desde `.env`.
- Arranca Spring Boot con perfil `dev`.

## Como correr la API (opciones)

### Opcion A: DB en Docker + API local (Maven) [recomendada]
Ventajas:
- Mejor debugging (breakpoints, logs, hot reload/devtools).
- Build y reinicio mas rapido que reconstruir imagen Docker.
- Menos friccion para desarrollar endpoints.

Comandos utiles:
```powershell
cd backend/uens-backend
.\tools\scripts\dev\up-db.ps1
.\tools\scripts\dev\run-local.ps1
```

Para bajar la DB:
```powershell
.\tools\scripts\dev\down-db.ps1
```

### Opcion B: Todo Docker (DB + API)
Usa el perfil `full` del `docker-compose.yml`.

```powershell
cd backend/uens-backend
docker compose --profile full up --build
```

Para detener:
```powershell
docker compose down
```

Cuando conviene:
- Demo local.
- Verificar que la imagen Docker de la API compila y arranca.
- Simular entorno mas cercano a despliegue.

## Como usar Docker en este backend

### Servicios definidos en `docker-compose.yml`
- `db`: PostgreSQL de desarrollo.
- `api` (perfil `full`): backend Spring Boot corriendo en contenedor.

### Puertos importantes
- Postgres host -> `localhost:5433` (mapeo `5433:5432`).
- API host (full Docker) -> `localhost:8080` por defecto (`APP_PORT`).

### Regla para no confundirse con puertos
- API local (Maven) se conecta a `localhost:5433`.
- API en contenedor se conecta a `db:5432` (red interna Docker).

### Comandos Docker utiles
```powershell
# Ver estado de servicios
docker compose ps

# Ver logs de Postgres
docker compose logs -f db

# Levantar solo DB
docker compose up -d db

# Levantar DB + API (perfil full)
docker compose --profile full up --build

# Detener todo
docker compose down
```

## Configuracion: que archivo manda que cosa

### Archivos de entorno
- `.env`
  - Configuracion local real (NO versionar secretos).
  - Docker Compose lo usa para interpolar variables.
  - Los scripts `run-local.*` lo cargan para Spring Boot.
- `.env.example`
  - Plantilla para crear tu `.env`.

### Configuracion Spring (`src/main/resources`)
- `application.properties`
  - Configuracion comun minima de la app.
- `application-default.properties`
  - Perfil `default`: arranque minimo sin DB (desactiva DataSource/JPA).
- `application-dev.properties`
  - Perfil `dev`: habilita PostgreSQL/JPA.
  - Lee `DB_URL` o (`DB_HOST`, `DB_PORT`, `DB_NAME`) + credenciales.

### Precedencia practica de DB en `dev`
1. `DB_URL` (si existe)
2. `DB_HOST` + `DB_PORT` + `DB_NAME`
3. Fallbacks definidos en properties

## Infraestructura del backend (como entenderlo)

### Vista de ejecucion (runtime)
Flujo recomendado (desarrollo):
- Cliente (Postman / frontend / desktop)
- -> API Spring Boot local (`mvn spring-boot:run`)
- -> PostgreSQL en Docker (`db`)

Flujo full Docker:
- Cliente
- -> contenedor `api`
- -> contenedor `db`

### Estructura del codigo (`src/main/java/com/marcosmoreiradev/uensbackend`)
- `boot/`
  - Arranque y utilidades de inicializacion para dev (por ejemplo seed/init local).
- `common/`
  - Componentes compartidos: respuestas API, errores, validacion, paginacion, utilidades.
- `config/`
  - Configuracion de Spring (Jackson, OpenAPI, scheduling, properties, etc.).
- `security/`
  - Seguridad/JWT: filtros, servicios de token, `SecurityConfig`, handlers.
- `modules/`
  - Funcionalidades del dominio (asignatura, auth, estudiante, seccion, etc.).

### Patron dentro de `modules/*`
La mayoria de modulos siguen una separacion por capas:
- `api/`
  - Controllers y DTOs de request/response.
- `application/`
  - Casos de uso/servicios, validadores, mappers.
- `infrastructure/`
  - Persistencia JPA (entities y repositories) y adaptadores concretos.

Esto ayuda a mantener:
- Endpoint claro (`api`).
- Logica de negocio en `application`.
- Detalles tecnicos (DB/JPA) en `infrastructure`.

### Scripts del backend (`tools/scripts`)
- `dev/` (utiles hoy)
  - `up-db.*`: levanta Postgres.
  - `down-db.*`: baja servicios Docker Compose.
  - `run-local.*`: carga `.env` y corre backend local con perfil `dev`.
- `db/`, `ci/`, `quality/`
  - Varios scripts aun estan como placeholder y se iran completando.

## Variables importantes de `.env`
- `APP_PORT`: puerto host de la API en full Docker.
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`: conexion a PostgreSQL.
- `DB_URL`: opcional, si quieres una URL JDBC unica.
- `JWT_SECRET`, `JWT_ISSUER`, `JWT_EXPIRATION_SECONDS`: configuracion JWT de dev.

## PowerShell vs CMD (error comun)
En PowerShell NO uses:
```cmd
set DB_URL=...
```

Usa:
```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5433/SchoolManagerNinitosSonadores"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "postgres"
.\mvnw.cmd -DskipTests spring-boot:run -Dspring-boot.run.profiles=dev
```

## Troubleshooting rapido
- `Connection refused` hacia Postgres:
  - Verifica `docker compose ps`.
  - Si la API corre local, revisa `DB_PORT=5433` en `.env`.
- `Falta .env`:
  - Ejecuta `Copy-Item .env.example .env`.
- Error de credenciales:
  - Revisa `DB_USER`/`DB_PASSWORD` en `.env` y lo que usa el contenedor `db`.
- Error de esquema (Hibernate `ddl-auto=validate`):
  - La BD debe tener el esquema esperado antes de arrancar `dev`.

## Archivos clave (resumen)
- `backend/uens-backend/docker-compose.yml`
- `backend/uens-backend/Dockerfile`
- `backend/uens-backend/.env.example`
- `backend/uens-backend/src/main/resources/application.properties`
- `backend/uens-backend/src/main/resources/application-default.properties`
- `backend/uens-backend/src/main/resources/application-dev.properties`
- `backend/uens-backend/tools/scripts/dev/run-local.ps1`
- `backend/uens-backend/tools/scripts/dev/up-db.ps1`

## Arquitectura backend (DDD + monolito modular)

Este backend aplica una separacion por modulos de dominio y por capas:

- `modules/<modulo>/api`
  expone endpoints REST, valida contrato HTTP y delega.
- `modules/<modulo>/application`
  contiene casos de uso, reglas de negocio, validadores y mapeos.
- `modules/<modulo>/infrastructure`
  implementa persistencia JPA y adaptadores tecnicos.

Regla practica:
- `api` no contiene logica de negocio compleja.
- `application` no depende de detalles HTTP.
- `infrastructure` no define reglas funcionales del dominio.

## Flujo de request (mental model para junior-mid)

1. El cliente llama endpoint en un `Controller`.
2. Seguridad valida JWT y rol.
3. Controller transforma request a DTO de aplicacion.
4. `application service` ejecuta reglas y orquesta repositorios.
5. `infrastructure repository` lee/escribe en PostgreSQL.
6. `mapper` arma DTO de respuesta.
7. Se retorna `ApiResponse` estandar.

## Modulo de reportes async (cola + archivos)

Flujo:

1. `POST /api/v1/reportes/solicitudes` crea registro en cola (`PENDIENTE`).
2. Worker scheduler toma solicitudes (`EN_PROCESO`).
3. Processor genera payload segun tipo de reporte.
4. Exporter escribe archivo (`XLSX`, `PDF`, `DOCX`).
5. Se persiste `resultado_json` + metadata de archivo.
6. Cliente consulta estado/resultado y descarga archivo final.

Branding de reportes:
- Los exportadores `XLSX`, `PDF` y `DOCX` incorporan el logo institucional desde `src/main/resources/assets/logo.png`.

Beneficios:
- evita bloquear requests largos.
- permite reintentos.
- facilita auditar errores por solicitud.

## Buenas practicas implementadas en el proyecto

- Contrato de respuesta uniforme (`ok`, `message`, `data`, `timestamp`).
- Validacion de entrada en DTOs (`jakarta.validation`).
- Paginacion y orden controlados (listas).
- Seguridad basada en roles (`ADMIN`, `SECRETARIA`) con JWT.
- Mapeo explicito DTO <-> entidad.
- Errores con codigos de negocio y HTTP status coherentes.
- Reportes desacoplados por estrategia:
  - `ReporteDataProcessor` por tipo de reporte.
  - `ReporteFileExporter` por formato de salida.
- Configuracion externa por `.env` y properties por perfil.

## Como extender el backend sin romper arquitectura

Para crear un modulo nuevo:

1. Crear paquete `modules/<nuevo_modulo>`.
2. Definir DTOs y controller en `api`.
3. Implementar casos de uso en `application`.
4. Crear entity/repository en `infrastructure`.
5. Agregar validaciones, mappers y pruebas.
6. Documentar endpoint en `backend/docs/api/API_ENDPOINTS.md`.

Para agregar un nuevo reporte:

1. Definir nuevo `tipoReporte`.
2. Implementar `ReporteDataProcessor`.
3. Registrar reglas de validacion en request validator.
4. Si aplica, extender exportadores/formato.
5. Actualizar docs API y postman collection.

## Comandos base para desarrollo diario

```powershell
# 1) Ir al backend
cd backend/uens-backend

# 2) Levantar base de datos local en Docker
.\tools\scripts\dev\up-db.ps1

# 3) Ejecutar API local
.\tools\scripts\dev\run-local.ps1

# 4) Ejecutar pruebas (cuando toque)
mvn clean test
```

## Documentacion relacionada

- API funcional: `backend/docs/api/API_ENDPOINTS.md`
- Coleccion Postman: `backend/docs/api/UENS.postman_collection.json`
- Variables de entorno: `backend/docs/despliegue/variables_entorno.md`
- Docker local: `backend/docs/despliegue/docker_local.md`
- Checklist release: `backend/docs/despliegue/checklist_release_v1.md`
