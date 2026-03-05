# 16_backend_v_1_base_trazabilidad_backend

## 1. Propósito

Dejar una base operativa de trazabilidad entre:

- documentos funcionales (`docs/01..05`)
- diseño técnico backend (`backend/docs/backend_v_1/00..15`)
- implementacion actual (`backend/uens-backend/src/main/java`)

Fecha de corte de esta base: `2026-03-04`.

---

## 2. Resumen rápido del estado actual

### Lo ya alineado

- Arquitectura monolito modular feature-first (`modules/*/api|application|infrastructure`) implementada.
- Contrato API común (`ApiResponse`, `ApiErrorResponse`) implementado.
- Seguridad JWT stateless y roles `ADMIN/SECRETARIA` implementados.
- Cola de reportes DB queue implementada con worker.
- `requestId` propagado por filtro y visible en logs base.
- Servicios CRUD principales migrados a excepciones tipadas en `application`.
- Paginación y sort HTTP-facing centralizados en `common/pagination`.
- Refactor alineado al SQL `V2_3FN.sql` sin cambios de esquema.

### Brechas principales de trazabilidad

- `modules/usuario` ya cubre RF-36/37/38 sin mezclar CRUD administrativo con `auth`.
- `modules/consultaacademica` ya cubre RF-21/RF-22 con endpoints explicitos de lectura agregada.
- La compatibilidad de `ResponseStatusException` sigue viva en el borde (`GlobalExceptionHandler`), pero ya no debe ser la ruta normal desde `application`.
- El handler global todavia conserva parte de mapeo por texto libre para escenarios heredados o de framework.
- Faltan tests especificos del handler global para blindar la taxonomia `VR/RN/API/SYS`.

---

## 3. Matriz base RF -> Backend

Escala de estado:

- `OK`: implementado y visible en API.
- `PARCIAL`: hay cobertura indirecta o incompleta.
- `PENDIENTE`: no hay endpoint/caso de uso implementado.

| RF | Cobertura actual | Endpoint/Módulo | Estado | Evidencia técnica |
|---|---|---|---|---|
| RF-01, RF-02, RF-03 | CRUD base estudiante | `/api/v1/estudiantes` | OK | `modules/estudiante/api/EstudianteController.java` |
| RF-04, RF-05, RF-06 | CRUD representante | `/api/v1/representantes` | OK | `modules/representante/api/RepresentanteLegalController.java` |
| RF-07 | Asociar representante a estudiante | via create/update de estudiante | PARCIAL | DTOs estudiante (sin endpoint dedicado de asociacion) |
| RF-08, RF-09, RF-10 | CRUD docente | `/api/v1/docentes` | OK | `modules/docente/api/DocenteController.java` |
| RF-11, RF-12, RF-13 | CRUD sección | `/api/v1/secciones` | OK | `modules/seccion/api/SeccionController.java` |
| RF-14, RF-15 | Asignacion vigente estudiante-sección | `PUT /api/v1/estudiantes/{id}/seccion-vigente` | OK | `modules/estudiante/api/EstudianteController.java` |
| RF-16 | Estudiantes por sección | filtro `seccionId` en estudiantes | PARCIAL | `EstudianteController#listar` |
| RF-17, RF-18, RF-19 | CRUD asignatura | `/api/v1/asignaturas` | OK | `modules/asignatura/api/AsignaturaController.java` |
| RF-20 | Asociar docentes a secciones | sin endpoint explicito | PARCIAL | hoy queda absorbido por lógica de clases |
| RF-21 | Consultar docentes por sección | `/api/v1/consultas/docentes-por-seccion` | OK | `modules/consultaacademica/api/ConsultaAcademicaController.java` |
| RF-22 | Consultar secciones por docente | `/api/v1/consultas/secciones-por-docente` | OK | `modules/consultaacademica/api/ConsultaAcademicaController.java` |
| RF-23, RF-24, RF-25 | CRUD clase | `/api/v1/clases` | OK | `modules/clase/api/ClaseController.java` |
| RF-26, RF-27, RF-28 | CRUD/listado calificación por estudiante | `/api/v1/calificaciones` | OK | `modules/calificacion/api/CalificacionController.java` |
| RF-29 | Calificaciones por clase/asignatura/parcial | clase+parcial si; asignatura no explicita | PARCIAL | filtros actuales: `estudianteId`, `claseId`, `numeroParcial` |
| RF-30 | Dashboard resumen | `/api/v1/dashboard/resumen` | OK | `modules/dashboard/api/DashboardController.java` |
| RF-31, RF-32, RF-33, RF-34 | filtros, búsqueda, sort, paginación | listados principales | OK | controllers y query services de módulos CRUD |
| RF-35 | limpiar filtros | concern de cliente; backend soporta default sin filtros | PARCIAL | aplica por contrato de query params opcionales |
| RF-36, RF-37, RF-38 | gestión usuario sistema administrativo | `/api/v1/usuarios` | OK | `modules/usuario/api/UsuarioSistemaAdministrativoController.java` |
| RF-39, RF-40 | login y control de acceso por rol/estado | `/api/v1/auth/login`, `/api/v1/auth/me` | OK | `modules/auth/api/AuthController.java` + `security/*` |

---

## 4. Matriz base VR -> Código

| VR | Estado | Observacion |
|---|---|---|
| VR-01 (rango edad estudiante) | PARCIAL | solo valida "no fecha futura" en `EstudianteRequestValidator` |
| VR-02 (límite cupo sección) | OK | controlado en `EstudianteCommandService` |
| VR-03 (límite institucional cupo) | PENDIENTE | no se evidencia regla global |
| VR-04 (estados inactivos) | PARCIAL | hay checks por módulo; falta matriz uniforme por caso de uso |
| VR-05 (duplicado estudiante advertencia) | PARCIAL | hoy se maneja como conflicto (`409`), no advertencia |
| VR-06 (parciales válidos) | OK | `@Min/@Max` y validaciones en calificación |
| VR-07 (rango nota) | OK | `CalificacionRequestValidator` |
| VR-08 (coherencia registro calificación) | PARCIAL | existe validación de contexto, falta traza formal VR/RN por regla |
| VR-09 (oferta mínima por sección) | PENDIENTE | no se evidencia validación explicita |
| VR-10 (asignacion vigente única) | OK | modelo actual con `estudiante.seccion_id` único vigente |
| VR-11 (coherencia clase-grado sección) | PARCIAL | parte de contexto académico en `ClaseCommandService` |
| VR-12 (representante principal obligatorio) | PARCIAL | cobertura en DTO/creacion, sin evidencia formal de "principal" |
| VR-13 (usuario activo para login) | OK | validado en `AuthApplicationService` |
| VR-14 (rol básico obligatorio) | PARCIAL | control de roles existe, falta módulo usuario para gobernanza completa |
| VR-15 (docente sin acceso al sistema admin) | OK | autenticación se basa en usuario administrativo, no en docente |

---

## 5. Diseño de paquetes recomendado (pragmatico, sin rehacer todo)

### Decision general

No recomiendo redisenar todo el arbol. El backend ya esta bien encaminado en `modules/*`.
Recomiendo cerrar brechas puntuales con paquetes nuevos y normalización de trazabilidad.

### Paquetes aplicados y siguientes candidatos

1. `modules/usuario`
 - motivo: cubrir RF-36/37/38 sin mezclar CRUD de usuarios con `auth`.
 - estado: implementado.
 - estructura mínima:
 - `modules/usuario/api`
 - `modules/usuario/application`
 - `modules/usuario/infrastructure/persistence`

2. `modules/consultaacademica` (o `modules/dashboard/consulta`)
 - motivo: cubrir RF-21/RF-22 sin forzar endpoints en módulos CRUD.
 - estado: implementado.
 - endpoints sugeridos:
 - `GET /api/v1/consultas/docentes-por-seccion`
 - `GET /api/v1/consultas/secciones-por-docente`

3. `modules/system/api`
 - motivo: homogeneidad estructural (hoy `PingController` no esta bajo `api`).

### Normalización transversal recomendada

1. Mantener `ResponseStatusException` solo como compatibilidad de framework/borde.
2. Seguir reduciendo el mapeo por texto libre en `GlobalExceptionHandler` hasta dejarlo residual.
3. Mantener `common/pagination` como única via para paginación/sort expuestos por API.

---

## 6. Base mínima de gobernanza de trazabilidad (para usar desde ya)

### Regla A: cada endpoint debe tener IDs funcionales

- anotar en Javadoc de método API:
 - `RF`: requerimientos funcionales impactados
 - `VR`: validaciones de entrada impactadas
 - `RN`: reglas de negocio impactadas

Ejemplo corto:

```java
// RF: RF-14, RF-15
// VR: VR-10
// RN: RN-EST-03, RN-EST-04
```

### Regla B: error siempre con código estable

- no lanzar excepciones con solo mensaje libre para reglas de negocio.
- toda regla de negocio debe terminar en `RN-*`.
- toda validación de entrada debe terminar en `VR-*`.

### Regla C: actualizar esta matriz por PR/feature

En cada cambio funcional:

1. actualizar fila RF impactada.
2. actualizar estado (`OK/PARCIAL/PENDIENTE`).
3. agregar evidencia técnica (clase/método).

---

## 7. Plan de ejecución recomendado

1. Blindar `GlobalExceptionHandler` con tests directos por familia de error.
2. Seguir reduciendo mapeo por texto libre heredado en el borde.
3. Mantener `common/pagination` como única via para listados HTTP-facing nuevos.
4. Revisar VR pendientes (VR-03, VR-09) y cerrar definicion funcional.
5. Mantener documentados `usuario`, `consultaacademica` y logging operativo en cada evolucion.

Con estos cinco pasos, la trazabilidad backend pasa de "parcial y fragil" a "operativa y mantenible".

---

## 8. Avance aplicado

### Refactor técnico consolidado

- `application` en `estudiante`, `seccion`, `clase`, `asignatura`, `docente`, `representante`, `calificacion` y `reporte` ya usa excepciones tipadas para reglas y faltantes comunes.
- `ResourceNotFoundException` concentra los `404` del modelo sin acoplar servicios a HTTP.
- `InfrastructureException` cubre fallos técnicos de archivos/reportes con mejor trazabilidad.
- `RequestIdFilter` queda temprano en el pipeline y el patron de log ya imprime `requestId`.
- `modules/usuario` concentra CRUD administrativo, hash por puerto/adaptador y persistencia desacoplada de `auth`.
- `modules/consultaacademica` encapsula lecturas multi-entidad con query repository + read models.
- El logging operativo queda disponible por consola y archivo rotativo (`logs/uens-backend.log`) con variables `LOG_LEVEL_ROOT`, `LOG_LEVEL_APP` y `LOG_FILE_PATH`.

### Criterio operativo vigente

- `VR-*`: entrada, formato, filtros, paginación.
- `RN-*`: duplicados, cupos, estados y coherencia académica.
- `API-04`: recurso inexistente.
- `SYS-*`: fallos técnicos internos o de procesamiento.

### Nota de mantenimiento

- Ante un incidente: buscar `requestId` en logs y `auditoria_evento`, clasificar por familia de error y corregir en la capa duena del problema sin romper el `ErrorCode`.


