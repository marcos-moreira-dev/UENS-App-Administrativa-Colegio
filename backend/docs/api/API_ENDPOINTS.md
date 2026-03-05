# API UENS - Guía para Frontend (JavaFX)

Base URL local: `http://localhost:8080`

Formato general de respuesta OK:

```json
{
  "ok": true,
  "message": "Operación exitosa.",
  "data": {},
  "timestamp": "2026-02-27T06:24:03.431583800Z"
}
```

Formato general de error:

```json
{
  "ok": false,
  "errorCode": "AUTH-01-CREDENCIALES_INVALIDAS",
  "message": "Credenciales invalidas.",
  "path": "/api/v1/auth/login",
  "timestamp": "2026-02-27T06:19:05.968013200Z",
  "requestId": "78a5e704"
}
```

## 1) Autenticación (primero esto)

### POST `/api/v1/auth/login` (Público)
Request body:
```json
{
  "login": "admin",
  "password": "admin123"
}
```
Response body:
```json
{
  "ok": true,
  "message": "Inicio de sesión exitoso.",
  "data": {
    "accessToken": "<JWT>",
    "refreshToken": "<REFRESH_TOKEN>",
    "tokenType": "Bearer",
    "expiresInSeconds": 3600,
    "refreshExpiresInSeconds": 604800,
    "usuario": {
      "id": 1,
      "login": "admin",
      "rol": "ADMIN",
      "estado": "ACTIVO"
    }
  },
  "timestamp": "2026-02-27T06:24:03.431583800Z"
}
```

Errores relevantes adicionales:
- `AUTH-01-CREDENCIALES_INVALIDAS` (`401`)
- `AUTH-06-LOGIN_TEMPORALMENTE_BLOQUEADO` (`429`)
- `AUTH-07-RATE_LIMIT_LOGIN_EXCEDIDO` (`429`)

Notas operativas:
- el backend devuelve `details.retryAfterSeconds` cuando aplica lockout o rate limit
- el desktop debe mostrar el mensaje y permitir reintentar luego del tiempo indicado
- el desktop puede renovar la sesión usando `refreshToken` antes de que expire el `accessToken`

### POST `/api/v1/auth/refresh` (Público, usado por cliente autenticado)
Request body:
```json
{
  "refreshToken": "<REFRESH_TOKEN>"
}
```
Response body:
```json
{
  "ok": true,
  "message": "Sesión renovada correctamente.",
  "data": {
    "accessToken": "<JWT_NUEVO>",
    "refreshToken": "<REFRESH_TOKEN_ROTADO>",
    "tokenType": "Bearer",
    "expiresInSeconds": 3600,
    "refreshExpiresInSeconds": 604800,
    "usuario": {
      "id": 1,
      "login": "admin",
      "rol": "ADMIN",
      "estado": "ACTIVO"
    }
  },
  "timestamp": "2026-03-03T23:00:00Z"
}
```

Errores relevantes adicionales:
- `AUTH-08-REFRESH_TOKEN_INVALIDO` (`401`)
- `AUTH-09-REFRESH_TOKEN_EXPIRADO` (`401`)

### Uso del token en requests protegidos
Header:
```http
Authorization: Bearer <accessToken>
```

### GET `/api/v1/auth/me` (ADMIN, SECRETARIA)
Sin body.
Response body:
```json
{
  "ok": true,
  "message": "Operación exitosa.",
  "data": {
    "id": 1,
    "login": "admin",
    "rol": "ADMIN",
    "estado": "ACTIVO"
  },
  "timestamp": "2026-02-27T06:30:00Z"
}
```

### POST `/api/v1/auth/logout` (Público, best-effort)
Request body opcional:
```json
{
  "refreshToken": "<REFRESH_TOKEN_ACTUAL>"
}
```

Notas:
- si el cliente aun conserva el refresh token, el backend lo revoca
- si el cliente ya perdio el estado completo, puede hacer logout local igualmente
- no requiere `Authorization` porque el desktop puede llegar aqui con access token ya expirado

## 2) Endpoints publicos de soporte

### GET `/api/v1/system/ping` (Público)
Sin body.
Response body:
```json
{
  "ok": true,
  "message": "Ping OK.",
  "data": "ok",
  "timestamp": "2026-02-27T06:30:00Z"
}
```

### GET `/actuator/health` (Público)
Sin body.
Response de ejemplo:
```json
{
  "status": "UP"
}
```

### GET `/actuator/info` (Público)
Sin body.
Response de ejemplo:
```json
{}
```

### GET `/v3/api-docs` (Público)
Sin body.
Devuelve el OpenAPI JSON de toda la API.

### GET `/swagger-ui.html` (Público)
Sin body.
Abre la interfaz Swagger UI.

## 3) Dashboard

### GET `/api/v1/dashboard/resumen` (ADMIN, SECRETARIA)
Sin body.
Response body:
```json
{
  "ok": true,
  "message": "Resumen del dashboard obtenido correctamente.",
  "data": {
    "totalEstudiantes": 640,
    "totalDocentes": 35,
    "totalSecciones": 21,
    "totalAsignaturas": 49,
    "totalClases": 147,
    "totalCalificaciones": 8500
  },
  "timestamp": "2026-02-27T06:30:00Z"
}
```

## 4) Asignaturas

### GET `/api/v1/asignaturas` (ADMIN, SECRETARIA)
Query opcional: `page,size,sort,q,estado,grado,area`
Sin body.
Response body:
```json
{
  "ok": true,
  "message": "Listado de asignaturas obtenido correctamente.",
  "data": {
    "items": [
      { "id": 1, "nombre": "Matematica", "area": "Ciencias", "grado": 1, "estado": "ACTIVO" }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 49,
    "totalPages": 5,
    "numberOfElements": 10,
    "first": true,
    "last": false,
    "sort": "id,asc"
  },
  "timestamp": "2026-02-27T06:30:00Z"
}
```

### GET `/api/v1/asignaturas/{asignaturaId}` (ADMIN, SECRETARIA)
Sin body.
Response body:
```json
{
  "ok": true,
  "message": "Asignatura obtenida correctamente.",
  "data": {
    "id": 1,
    "nombre": "Matematica",
    "area": "Ciencias",
    "descripcion": "Numeros, operaciones, geometria y problemas",
    "grado": 1,
    "estado": "ACTIVO"
  },
  "timestamp": "2026-02-27T06:30:00Z"
}
```

### POST `/api/v1/asignaturas` (ADMIN)
Request body:
```json
{
  "nombre": "Robotica",
  "area": "Tecnologia",
  "descripcion": "Fundamentos de robotica educativa",
  "grado": 6
}
```
Response body:
```json
{
  "ok": true,
  "message": "Asignatura creada correctamente.",
  "data": {
    "id": 200,
    "nombre": "Robotica",
    "area": "Tecnologia",
    "descripcion": "Fundamentos de robotica educativa",
    "grado": 6,
    "estado": "ACTIVO"
  },
  "timestamp": "2026-02-27T06:30:00Z"
}
```

### PUT `/api/v1/asignaturas/{asignaturaId}` (ADMIN)
Request body:
```json
{
  "nombre": "Robotica Aplicada",
  "area": "Tecnologia",
  "descripcion": "Robotica con proyectos practicos",
  "grado": 6,
  "estado": "ACTIVO"
}
```
Response body igual a `AsignaturaResponseDto`.

### PATCH `/api/v1/asignaturas/{asignaturaId}/estado` (ADMIN)
Request body:
```json
{ "estado": "INACTIVO" }
```
Response body igual a `AsignaturaResponseDto`.

## 5) Secciones

### GET `/api/v1/secciones` (ADMIN, SECRETARIA)
Query opcional: `q,estado,grado,paralelo,anioLectivo,page,size,sort`
Sin body.
Response: `PageResponseDto<SeccionListItemDto>`.

### GET `/api/v1/secciones/{seccionId}` (ADMIN, SECRETARIA)
Sin body.
Response body (`SeccionResponseDto`):
```json
{
  "ok": true,
  "message": "Seccion obtenida correctamente.",
  "data": {
    "id": 1,
    "grado": 1,
    "paralelo": "A",
    "cupoMaximo": 35,
    "anioLectivo": "2026-2027",
    "estado": "ACTIVO"
  },
  "timestamp": "2026-02-27T06:30:00Z"
}
```

### POST `/api/v1/secciones` (ADMIN)
Request body:
```json
{
  "grado": 2,
  "paralelo": "D",
  "cupoMaximo": 35,
  "anioLectivo": "2026-2027"
}
```
Response: `SeccionResponseDto`.

### PUT `/api/v1/secciones/{seccionId}` (ADMIN)
Request body:
```json
{
  "grado": 2,
  "paralelo": "D",
  "cupoMaximo": 30,
  "anioLectivo": "2026-2027",
  "estado": "ACTIVO"
}
```
Response: `SeccionResponseDto`.

### PATCH `/api/v1/secciones/{seccionId}/estado` (ADMIN)
Request body:
```json
{ "estado": "INACTIVO" }
```
Response: `SeccionResponseDto`.

## 6) Docentes

### GET `/api/v1/docentes` (ADMIN, SECRETARIA)
Query opcional: `q,estado,page,size,sort`.
Sin body. Response: `PageResponseDto<DocenteListItemDto>`.

### GET `/api/v1/docentes/{docenteId}` (ADMIN, SECRETARIA)
Sin body. Response: `DocenteResponseDto`.

### POST `/api/v1/docentes` (ADMIN, SECRETARIA)
Request body:
```json
{
  "nombres": "Ana Lucia",
  "apellidos": "Paredes Mero",
  "telefono": "0991234567",
  "correoElectronico": "ana.paredes@uens.test"
}
```
Response: `DocenteResponseDto`.

### PUT `/api/v1/docentes/{docenteId}` (ADMIN, SECRETARIA)
Request body:
```json
{
  "nombres": "Ana Lucia",
  "apellidos": "Paredes Mero",
  "telefono": "0991234567",
  "correoElectronico": "ana.paredes@uens.test",
  "estado": "ACTIVO"
}
```
Response: `DocenteResponseDto`.

### PATCH `/api/v1/docentes/{docenteId}/estado` (ADMIN)
Request body:
```json
{ "estado": "INACTIVO" }
```
Response: `DocenteResponseDto`.

## 7) Representantes

### GET `/api/v1/representantes` (ADMIN, SECRETARIA)
Query opcional: `q,page,size,sort`.
Sin body. Response: `PageResponseDto<RepresentanteLegalListItemDto>`.

### GET `/api/v1/representantes/{representanteId}` (ADMIN, SECRETARIA)
Sin body. Response: `RepresentanteLegalResponseDto`.

### POST `/api/v1/representantes` (ADMIN, SECRETARIA)
Request body:
```json
{
  "nombres": "Luis Alberto",
  "apellidos": "Mendoza Vera",
  "telefono": "0997654321",
  "correoElectronico": "luis.mendoza@uens.test"
}
```
Response: `RepresentanteLegalResponseDto`.

### PUT `/api/v1/representantes/{representanteId}` (ADMIN, SECRETARIA)
Request body igual al create.
Response: `RepresentanteLegalResponseDto`.

## 8) Estudiantes

### GET `/api/v1/estudiantes` (ADMIN, SECRETARIA)
Query opcional: `q,estado,seccionId,representanteLegalId,page,size,sort`.
Sin body. Response: `PageResponseDto<EstudianteListItemDto>`.

### GET `/api/v1/estudiantes/{estudianteId}` (ADMIN, SECRETARIA)
Sin body. Response: `EstudianteResponseDto`.

### POST `/api/v1/estudiantes` (ADMIN, SECRETARIA)
Request body:
```json
{
  "nombres": "Mateo Andres",
  "apellidos": "Mendoza Vera",
  "fechaNacimiento": "2016-05-10",
  "representanteLegalId": 1,
  "seccionId": 1
}
```
Response: `EstudianteResponseDto`.

### PUT `/api/v1/estudiantes/{estudianteId}` (ADMIN, SECRETARIA)
Request body:
```json
{
  "nombres": "Mateo Andres",
  "apellidos": "Mendoza Vera",
  "fechaNacimiento": "2016-05-10",
  "representanteLegalId": 1,
  "seccionId": 1,
  "estado": "ACTIVO"
}
```
Response: `EstudianteResponseDto`.

### PATCH `/api/v1/estudiantes/{estudianteId}/estado` (ADMIN)
Request body:
```json
{ "estado": "INACTIVO" }
```
Response: `EstudianteResponseDto`.

### PUT `/api/v1/estudiantes/{estudianteId}/seccion-vigente` (ADMIN, SECRETARIA)
Request body:
```json
{ "seccionId": 2 }
```
Response: `EstudianteResponseDto`.

## 9) Clases

### GET `/api/v1/clases` (ADMIN, SECRETARIA)
Query opcional: `estado,seccionId,asignaturaId,docenteId,diaSemana,page,size,sort`.
Sin body. Response: `PageResponseDto<ClaseListItemDto>`.

### GET `/api/v1/clases/{claseId}` (ADMIN, SECRETARIA)
Sin body. Response: `ClaseResponseDto`.

### POST `/api/v1/clases` (ADMIN)
Request body:
```json
{
  "seccionId": 1,
  "asignaturaId": 1,
  "docenteId": 1,
  "diaSemana": "LUNES",
  "horaInicio": "07:00",
  "horaFin": "07:45"
}
```
Response: `ClaseResponseDto`.

### PUT `/api/v1/clases/{claseId}` (ADMIN)
Request body:
```json
{
  "seccionId": 1,
  "asignaturaId": 1,
  "docenteId": 2,
  "diaSemana": "LUNES",
  "horaInicio": "07:00",
  "horaFin": "07:45",
  "estado": "ACTIVO"
}
```
Response: `ClaseResponseDto`.

### PATCH `/api/v1/clases/{claseId}/estado` (ADMIN)
Request body:
```json
{ "estado": "INACTIVO" }
```
Response: `ClaseResponseDto`.

## 10) Calificaciones

### GET `/api/v1/calificaciones` (ADMIN, SECRETARIA)
Query opcional: `estudianteId,claseId,numeroParcial,page,size,sort`.
Sin body. Response: `PageResponseDto<CalificacionListItemDto>`.

### GET `/api/v1/calificaciones/{calificacionId}` (ADMIN, SECRETARIA)
Sin body. Response: `CalificacionResponseDto`.

### POST `/api/v1/calificaciones` (ADMIN, SECRETARIA)
Request body:
```json
{
  "numeroParcial": 1,
  "nota": 8.75,
  "fechaRegistro": "2026-06-15",
  "observacion": "Buen desempeno.",
  "estudianteId": 1,
  "claseId": 1
}
```
Response: `CalificacionResponseDto`.

### PUT `/api/v1/calificaciones/{calificacionId}` (ADMIN, SECRETARIA)
Request body igual a create.
Response: `CalificacionResponseDto`.

## 11) Reportes (cola asíncrona)

### POST `/api/v1/reportes/solicitudes` (ADMIN, SECRETARIA)
Request body:
```json
{
  "tipoReporte": "LISTADO_ESTUDIANTES_POR_SECCION",
  "formatoSalida": "XLSX",
  "seccionId": 1,
  "numeroParcial": null,
  "fechaDesde": "2026-01-01",
  "fechaHasta": "2026-12-31"
}
```
Tipos soportados en V1:
- `LISTADO_ESTUDIANTES_POR_SECCION`
- `CALIFICACIONES_POR_SECCION_Y_PARCIAL`
- `AUDITORIA_ADMIN_OPERACIONES` (reservado para endpoint ADMIN de auditoria)

Formatos de salida soportados en V1:
- `XLSX`
- `PDF`
- `DOCX`

Branding de archivo:
- Las exportaciones incluyen el logo institucional cargado desde `src/main/resources/assets/logo.png`.
- Los archivos se almacenan primero en un repositorio documental local desacoplado del worker mediante `DocumentStoragePort`.

Response (`ReporteSolicitudCreadaResponseDto`):
```json
{
  "ok": true,
  "message": "Solicitud de reporte creada correctamente.",
  "data": {
    "solicitudId": 10,
    "tipoReporte": "LISTADO_ESTUDIANTES_POR_SECCION",
    "estado": "PENDIENTE",
    "fechaSolicitud": "2026-02-27T06:30:00"
  },
  "timestamp": "2026-02-27T06:30:00Z"
}
```

### GET `/api/v1/reportes/solicitudes` (ADMIN, SECRETARIA)
Query opcional: `q,tipoReporte,estado,page,size,sort`.
Sin body. Response: `PageResponseDto<ReporteSolicitudListItemDto>`.

Regla de ownership:
- `ADMIN` ve todas las solicitudes
- `SECRETARIA` solo ve las creadas por su propio usuario autenticado

### GET `/api/v1/reportes/solicitudes/{solicitudId}` (ADMIN, SECRETARIA)
Sin body. Response: `ReporteSolicitudDetalleResponseDto`.

### GET `/api/v1/reportes/solicitudes/{solicitudId}/estado` (ADMIN, SECRETARIA)
Sin body. Response: `ReporteSolicitudResultadoResponseDto`.

### GET `/api/v1/reportes/solicitudes/{solicitudId}/resultado` (ADMIN, SECRETARIA)
Sin body. Response: `ReporteSolicitudResultadoResponseDto`.

### GET `/api/v1/reportes/solicitudes/{solicitudId}/archivo` (ADMIN, SECRETARIA)
Sin body. Respuesta binaria (`xlsx`, `pdf` o `docx`) con `Content-Disposition: attachment`, `Cache-Control: no-store` y `X-Content-Type-Options: nosniff`.

Regla de ownership:
- `ADMIN` puede descargar cualquier archivo generado
- `SECRETARIA` solo puede descargar archivos de sus propias solicitudes
- una solicitud ajena puede responder `404` para no exponer existencia de recursos de otro usuario

### POST `/api/v1/reportes/solicitudes/{solicitudId}/reintentar` (ADMIN)
Sin body.
Response: `ReporteSolicitudCreadaResponseDto`.

## 12) Auditoria operativa (solo ADMIN)

### GET `/api/v1/auditoria/eventos` (ADMIN)
Query opcional: `q,módulo,accion,resultado,actorLogin,fechaDesde,fechaHasta,page,size,sort`.
Sin body. Response: `PageResponseDto<AuditoriaEventoListItemDto>`.

Seguridad:
- acceso solo ADMIN tanto en controller como en capa application

### POST `/api/v1/auditoria/reportes/solicitudes` (ADMIN)
Request body:
```json
{
  "formatoSalida": "PDF",
  "fechaDesde": "2026-01-01",
  "fechaHasta": "2026-12-31",
  "módulo": "REPORTE",
  "accion": "WORKER_SOLICITUD_ERROR",
  "resultado": "ERROR",
  "actorLogin": "admin",
  "incluirDetalle": true
}
```

Seguridad:
- acceso solo ADMIN tanto en controller como en capa application
Genera una solicitud asíncrona del tipo `AUDITORIA_ADMIN_OPERACIONES` en la misma cola de reportes.
Response: `ReporteSolicitudCreadaResponseDto`.

## 13) Matriz rápida de permisos

- Público: `POST /api/v1/auth/login`, `POST /api/v1/auth/refresh`, `POST /api/v1/auth/logout`, `GET /api/v1/system/ping`
- ADMIN y SECRETARIA: lecturas y operaciones funcionales de gestión diaria
- Solo ADMIN: cambios de estado sensibles, creacion/edicion de configuración academica (secciones, asignaturas, clases), reintento de reportes y toda la capa de auditoria

## 14) Inventario completo de endpoints

Total endpoints implementados: `49`

- Auth: 4
- System: 1
- Dashboard: 1
- Asignaturas: 5
- Secciones: 5
- Docentes: 5
- Representantes: 4
- Estudiantes: 6
- Clases: 5
- Calificaciones: 4
- Reportes: 7
- Auditoria: 2

## 15) Recomendación para Postman

En el request de login, pestaña `Tests`:

```javascript
const json = pm.response.json();
pm.collectionVariables.set("token", json.data.accessToken);
```

Luego en Authorization (Bearer Token) de la coleccion:

```text
{{token}}
```

## 16) Catálogo técnico completo (metodo + ruta + rol)

Fuente de verdad funcional:
- Swagger UI: `GET /swagger-ui.html`
- OpenAPI JSON: `GET /v3/api-docs`

Notas:
- `ADMIN, SECRETARIA` significa que ambos roles pueden acceder.
- `PUBLICO` significa sin token.
- Total catalogado en esta seccion: 49 endpoints.

| # | Metodo | Ruta | Rol |
|---|---|---|---|
| 1 | POST | `/api/v1/auth/login` | PUBLICO |
| 2 | POST | `/api/v1/auth/refresh` | PUBLICO |
| 3 | POST | `/api/v1/auth/logout` | PUBLICO |
| 4 | GET | `/api/v1/auth/me` | ADMIN, SECRETARIA |
| 5 | GET | `/api/v1/system/ping` | PUBLICO |
| 6 | GET | `/api/v1/dashboard/resumen` | ADMIN, SECRETARIA |
| 7 | GET | `/api/v1/asignaturas` | ADMIN, SECRETARIA |
| 8 | GET | `/api/v1/asignaturas/{asignaturaId}` | ADMIN, SECRETARIA |
| 9 | POST | `/api/v1/asignaturas` | ADMIN |
| 10 | PUT | `/api/v1/asignaturas/{asignaturaId}` | ADMIN |
| 11 | PATCH | `/api/v1/asignaturas/{asignaturaId}/estado` | ADMIN |
| 12 | GET | `/api/v1/secciones` | ADMIN, SECRETARIA |
| 13 | GET | `/api/v1/secciones/{seccionId}` | ADMIN, SECRETARIA |
| 14 | POST | `/api/v1/secciones` | ADMIN |
| 15 | PUT | `/api/v1/secciones/{seccionId}` | ADMIN |
| 16 | PATCH | `/api/v1/secciones/{seccionId}/estado` | ADMIN |
| 17 | GET | `/api/v1/docentes` | ADMIN, SECRETARIA |
| 18 | GET | `/api/v1/docentes/{docenteId}` | ADMIN, SECRETARIA |
| 19 | POST | `/api/v1/docentes` | ADMIN, SECRETARIA |
| 20 | PUT | `/api/v1/docentes/{docenteId}` | ADMIN, SECRETARIA |
| 21 | PATCH | `/api/v1/docentes/{docenteId}/estado` | ADMIN |
| 22 | GET | `/api/v1/representantes` | ADMIN, SECRETARIA |
| 23 | GET | `/api/v1/representantes/{representanteId}` | ADMIN, SECRETARIA |
| 24 | POST | `/api/v1/representantes` | ADMIN, SECRETARIA |
| 25 | PUT | `/api/v1/representantes/{representanteId}` | ADMIN, SECRETARIA |
| 26 | GET | `/api/v1/estudiantes` | ADMIN, SECRETARIA |
| 27 | GET | `/api/v1/estudiantes/{estudianteId}` | ADMIN, SECRETARIA |
| 28 | POST | `/api/v1/estudiantes` | ADMIN, SECRETARIA |
| 29 | PUT | `/api/v1/estudiantes/{estudianteId}` | ADMIN, SECRETARIA |
| 30 | PATCH | `/api/v1/estudiantes/{estudianteId}/estado` | ADMIN |
| 31 | PUT | `/api/v1/estudiantes/{estudianteId}/seccion-vigente` | ADMIN, SECRETARIA |
| 32 | GET | `/api/v1/clases` | ADMIN, SECRETARIA |
| 33 | GET | `/api/v1/clases/{claseId}` | ADMIN, SECRETARIA |
| 34 | POST | `/api/v1/clases` | ADMIN |
| 35 | PUT | `/api/v1/clases/{claseId}` | ADMIN |
| 36 | PATCH | `/api/v1/clases/{claseId}/estado` | ADMIN |
| 37 | GET | `/api/v1/calificaciones` | ADMIN, SECRETARIA |
| 38 | GET | `/api/v1/calificaciones/{calificacionId}` | ADMIN, SECRETARIA |
| 39 | POST | `/api/v1/calificaciones` | ADMIN, SECRETARIA |
| 40 | PUT | `/api/v1/calificaciones/{calificacionId}` | ADMIN, SECRETARIA |
| 41 | POST | `/api/v1/reportes/solicitudes` | ADMIN, SECRETARIA |
| 42 | GET | `/api/v1/reportes/solicitudes` | ADMIN, SECRETARIA |
| 43 | GET | `/api/v1/reportes/solicitudes/{solicitudId}` | ADMIN, SECRETARIA |
| 44 | GET | `/api/v1/reportes/solicitudes/{solicitudId}/estado` | ADMIN, SECRETARIA |
| 45 | GET | `/api/v1/reportes/solicitudes/{solicitudId}/resultado` | ADMIN, SECRETARIA |
| 46 | GET | `/api/v1/reportes/solicitudes/{solicitudId}/archivo` | ADMIN, SECRETARIA |
| 47 | POST | `/api/v1/reportes/solicitudes/{solicitudId}/reintentar` | ADMIN |
| 48 | GET | `/api/v1/auditoria/eventos` | ADMIN |
| 49 | POST | `/api/v1/auditoria/reportes/solicitudes` | ADMIN |

## 17) Flujo recomendado para frontend (resumen operativo)

1. Login:
   `POST /api/v1/auth/login` con `login` y `password`.
2. Guardar token:
   usar `data.accessToken` y `data.refreshToken`.
3. Renovar sesión cuando el `accessToken` este por expirar:
   `POST /api/v1/auth/refresh` con el `refreshToken`.
4. Enviar header:
   `Authorization: Bearer <token>`.
5. Consumir módulos CRUD:
   estudiantes, docentes, secciones, clases, calificaciones.
6. Para reportes:
   crear solicitud -> consultar estado/resultado -> descargar archivo.

Flujo de reportes async:
- `POST /api/v1/reportes/solicitudes`
- polling en `GET /api/v1/reportes/solicitudes/{id}/estado`
- al completar, descargar `GET /api/v1/reportes/solicitudes/{id}/archivo`


