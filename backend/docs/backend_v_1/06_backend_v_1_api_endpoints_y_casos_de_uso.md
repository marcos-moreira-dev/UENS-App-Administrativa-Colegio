# 06_backend_v1_api_endpoints_y_casos_de_uso

- **Versión:** 0.2
- **Estado:** En revisión (reconstruido por consistencia)
- **Ámbito:** Backend V1 (Spring Boot + Java 21 + ORM)
- **Depende de:** `04_backend_v1_modelado_aplicacion_y_modulos.md`, `05_backend_v1_diseno_api_contrato_respuestas_y_errores.md`, `07_backend_v1_validaciones_reglas_negocio_y_excepciones.md`, `08_backend_v1_paginacion_filtros_ordenamiento_y_consultas.md`
- **Relacionados:** `09_backend_v1_seguridad_documentacion_y_despliegue_minimo.md`, `10_backend_v1_reporte_solicitudes_cola_simple_db_queue.md`
- **Objetivo de esta revisión:** Definir el mapa operativo de endpoints REST V1 y su relación con casos de uso de aplicación, con alcance realista, trazabilidad y placeholders controlados.

---

## 1. Propósito del documento

Este documento define **qué endpoints tendrá el backend V1**, **qué caso de uso ejecuta cada uno**, **qué DTOs de entrada/salida usa**, y **qué errores puede producir**.

Su propósito es que el backend no se diseñe “a medida de una pantalla específica”, sino como una API:

- coherente con el dominio,
- reusable por JavaFX (y futuro frontend web/móvil),
- compatible con el contrato estándar (`ApiResponse`, `ApiErrorResponse`),
- y sin sobreingeniería.

✅ Aquí se aterriza la frase clave que tú mismo dijiste: **el backend es agnóstico de la UI, pero no de los casos de uso**.

---

## 2. Principios de diseño de endpoints (V1)

## 2.1. Diseño orientado a casos de uso, no solo a tablas

Aunque habrá endpoints CRUD por módulo, el backend V1 también tendrá endpoints de **orquestación** cuando el flujo involucre varias entidades.

### Tipos de endpoints permitidos en V1
- **CRUD de entidad/módulo** (catálogos y mantenimiento)
- **Consultas operativas** (listas, detalles, filtros)
- **Acciones de negocio** (asignar sección, cambiar estado, etc.)
- **Cola simple de reportes** (solicitudes y estado)
- **Placeholders controlados** (`501`) para funcionalidades planificadas

✅ Esto evita el extremo de “todo CRUD” y también evita sobrecomplicar con CQRS completo.

---

## 2.2. Contrato uniforme obligatorio

Todos los endpoints deben responder usando lo definido en `05`:

- éxito → `ApiResponse<T>`
- error → `ApiErrorResponse`
- listados paginados → `ApiResponse<PageResponseDto<T>>`

---

## 2.3. Convenciones REST V1

### Base path global
- `/api/v1`

### Convenciones clave
- nombres de recursos en plural cuando representen colección (`/estudiantes`, `/secciones`)
- acciones explícitas solo cuando el caso de uso no es CRUD puro
- query param de búsqueda simple = **`q`** (no `search`)
- paginación/filtros documentados aparte en `08`

---

## 2.4. Endpoints definidos pero no implementados (placeholders)

Se permite dejar endpoints planificados con `501 Not Implemented` cuando:
- el flujo está decidido arquitectónicamente,
- pero la lógica aún no se implementa,
- o depende de una deuda técnica/ajuste de BD.

✅ Esto es válido en V1 si se documenta claramente y responde con `ApiErrorResponse` (`API-10-ENDPOINT_EN_CONSTRUCCION`).

---

## 3. Estructura de módulos y agrupación de endpoints (V1)

Este documento asume la arquitectura de `04`:

- módulos CRUD (por entidad o agregado funcional)
- módulos de orquestación (casos multi-entidad)
- módulo de auth
- módulo de reportes/cola simple
- endpoint técnico/placeholder opcional para landing/futuro consumo externo

### Agrupación funcional recomendada
1. **Auth** (`/auth`)
2. **Estudiantes** (`/estudiantes`)
3. **Secciones** (`/secciones`)
4. **Calificaciones** (`/calificaciones`) *(si entra en V1 operativa)*
5. **Consultas operativas / dashboard** (`/dashboard`, `/consultas`) *(placeholders o lectura mínima)*
6. **Reportes (cola simple DB queue)** (`/reportes`)
7. **Catálogos auxiliares** (solo si realmente se necesitan en V1)

> La selección exacta depende del alcance V1 final, pero este mapa deja lista la arquitectura sin inflarla.

---

## 4. Estrategia general de diseño de casos de uso

## 4.1. Tipos de casos de uso en `application`

### A. Lectura (query de aplicación)
- listados
- detalles
- filtros
- consultas para soporte a UI

### B. Comando / acción
- crear
- actualizar
- asignar
- cambiar estado
- solicitar reporte

### C. Orquestación
- flujo multi-entidad con validaciones RN y transacciones

✅ Cada endpoint debe mapearse explícitamente a uno de estos tipos.

---

## 4.2. Naming sugerido para servicios de aplicación (alineado con `03` y `04`)

### Opción práctica V1 (recomendada)
- `EstudianteApplicationService`
- `SeccionApplicationService`
- `CalificacionApplicationService`
- `ReporteSolicitudApplicationService`
- `AuthApplicationService`

### Si separas lectura/escritura (sin full CQRS)
- `EstudianteQueryService`
- `EstudianteCommandService`

✅ Ambas son válidas. Para V1, una sola clase por módulo suele ser suficiente si no se vuelve gigantesca.

---

## 5. Catálogo de endpoints V1 (vista de alto nivel)

> Este catálogo define el **mapa oficial V1**. `09` (Swagger/OpenAPI) deberá documentar al menos los endpoints implementados + placeholders relevantes.

## 5.1. Auth
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/logout` *(opcional según estrategia JWT; puede ser placeholder/no-op documentado)*
- `GET /api/v1/auth/me` *(opcional pero útil para JavaFX)*

## 5.2. Estudiantes
- `GET /api/v1/estudiantes`
- `GET /api/v1/estudiantes/{estudianteId}`
- `POST /api/v1/estudiantes`
- `PUT /api/v1/estudiantes/{estudianteId}`
- `PATCH /api/v1/estudiantes/{estudianteId}/estado` *(opcional, si manejas estado explícito)*
- `DELETE /api/v1/estudiantes/{estudianteId}` *(preferible eliminación lógica si aplica)
- `PUT /api/v1/estudiantes/{estudianteId}/seccion-vigente` *(acción/orquestación)

## 5.3. Secciones
- `GET /api/v1/secciones`
- `GET /api/v1/secciones/{seccionId}`
- `POST /api/v1/secciones`
- `PUT /api/v1/secciones/{seccionId}`
- `PATCH /api/v1/secciones/{seccionId}/estado` *(opcional)*
- `GET /api/v1/secciones/{seccionId}/estudiantes` *(consulta útil para UI)

## 5.4. Calificaciones (si el V1 las incluye)
- `GET /api/v1/calificaciones`
- `GET /api/v1/calificaciones/{calificacionId}` *(opcional si el flujo es más por filtros que por id)
- `POST /api/v1/calificaciones`
- `PUT /api/v1/calificaciones/{calificacionId}`
- `DELETE /api/v1/calificaciones/{calificacionId}` *(si aplica; si no, anulación lógica)

## 5.5. Dashboard / consultas operativas (mínimo o placeholder)
- `GET /api/v1/dashboard/resumen` *(puede iniciar como placeholder 501)*
- `GET /api/v1/consultas/alertas` *(opcional/placeholder)
- `GET /api/v1/consultas/docentes-por-seccion` *(consulta agregada operativa)*
- `GET /api/v1/consultas/secciones-por-docente` *(consulta agregada operativa)*

## 5.6. Usuarios del sistema administrativo
- `GET /api/v1/usuarios`
- `GET /api/v1/usuarios/{usuarioId}`
- `POST /api/v1/usuarios`
- `PUT /api/v1/usuarios/{usuarioId}`
- `PATCH /api/v1/usuarios/{usuarioId}/estado`

## 5.7. Reportes (cola simple DB queue)
- `POST /api/v1/reportes/solicitudes`
- `GET /api/v1/reportes/solicitudes`
- `GET /api/v1/reportes/solicitudes/{solicitudId}`
- `GET /api/v1/reportes/solicitudes/{solicitudId}/estado`
- `POST /api/v1/reportes/solicitudes/{solicitudId}/reintentar` *(opcional V1)
- `GET /api/v1/reportes/solicitudes/{solicitudId}/resultado` *(placeholder 501 si el archivo/report output aún no existe)
- `GET /api/v1/reportes/preview` *(placeholder 501 si solo se practicará arquitectura)

---

## 6. Diseño de endpoints de Auth (`/auth`)

> El detalle de seguridad/JWT se aterriza en `09`, pero aquí se define el contrato funcional y sus casos de uso.

## 6.1. `POST /api/v1/auth/login`

### Caso de uso
**Iniciar sesión** y obtener token para consumir endpoints protegidos.

### Servicio de aplicación (sugerido)
- `AuthApplicationService.login(...)`

### Request DTO (ejemplo conceptual)
- `LoginRequestDto`
 - `username`
 - `password`

### Response DTO (ejemplo conceptual)
- `LoginResponseDto`
 - `token`
 - `tokenType` (`Bearer`)
 - `expiresInSeconds` (opcional)
 - `usuario` (resumen)

### Respuesta exitosa
- `200 OK`
- `ApiResponse<LoginResponseDto>`

### Errores comunes
- `VR-01-REQUEST_INVALIDO` (`400`)
- `AUTH-01-CREDENCIALES_INVALIDAS` (`401`)
- `SYS-01-ERROR_INTERNO` (`500`)

---

## 6.2. `GET /api/v1/auth/me` (opcional pero recomendado)

### Caso de uso
Obtener datos mínimos del usuario autenticado (útil para inicializar JavaFX, menú/rol).

### Servicio de aplicación
- `AuthApplicationService.getCurrentUser(...)`

### Respuesta
- `200 OK`
- `ApiResponse<AuthMeResponseDto>`

### Errores
- `AUTH-02-TOKEN_INVALIDO` (`401`)
- `AUTH-03-TOKEN_EXPIRADO` (`401`)
- `AUTH-10-ACCESO_DENEGADO` (`403`, si aplica)

---

## 6.3. `POST /api/v1/auth/logout` (opcional)

### Nota de diseño V1
Si usas JWT stateless puro, `logout` backend puede ser:
- no implementado (cliente descarta token), o
- endpoint simbólico/log de sesión, o
- placeholder.

### Recomendación V1 simple
- Si no hay blacklist de tokens, documentar que logout es **client-side**.
- Si deseas practicar el endpoint, puedes responder `200 OK` con mensaje informativo.

✅ No sobreingenierizar blacklist en V1 si no es necesario.

---

## 7. Diseño de endpoints de Estudiantes (`/estudiantes`)

Este módulo es central para práctica CRUD + orquestación.

## 7.1. `GET /api/v1/estudiantes`

### Caso de uso
Listar estudiantes con paginación, filtros simples y búsqueda `q`.

### Servicio de aplicación
- `EstudianteApplicationService.listar(...)`

### Query params esperados (base; detalle en `08`)
- `page`
- `size`
- `sort`
- `q` (búsqueda simple)
- filtros opcionales (ej. `estado`, `seccionId`, etc. según V1 real)

### Respuesta exitosa
- `200 OK`
- `ApiResponse<PageResponseDto<EstudianteListItemResponseDto>>`

### Errores comunes
- `VR-02-PARAMETRO_INVALIDO` (`400`) para `page`, `size`, `sort` inválidos
- `VR-06-VALOR_ENUM_INVALIDO` (`400`) si `estado` es enum y llega mal
- `SYS-01-ERROR_INTERNO` (`500`)

---

## 7.2. `GET /api/v1/estudiantes/{estudianteId}`

### Caso de uso
Obtener detalle de un estudiante.

### Servicio de aplicación
- `EstudianteApplicationService.obtenerPorId(estudianteId)`

### Respuesta exitosa
- `200 OK`
- `ApiResponse<EstudianteDetailResponseDto>`

### Errores comunes
- `VR-02-PARAMETRO_INVALIDO` (`400`) si ID inválido
- `API-04-RECURSO_NO_ENCONTRADO` (`404`) si no existe

---

## 7.3. `POST /api/v1/estudiantes`

### Caso de uso
Crear estudiante.

### Servicio de aplicación
- `EstudianteApplicationService.crear(...)`

### Request DTO (ejemplo)
- `EstudianteCreateRequestDto`
 - campos personales básicos
 - datos necesarios para V1
 - **sin meter objetos de dominio completos**

### Respuesta exitosa
- `201 Created`
- `ApiResponse<EstudianteDetailResponseDto>` o `ApiResponse<EstudianteCreatedResponseDto>`

### Errores comunes
- `VR-*` (`400`) por campos inválidos
- `RN-18-REGISTRO_DUPLICADO` (`409`) si hay duplicado lógico
- `SYS-*` (`500`) por persistencia inesperada

---

## 7.4. `PUT /api/v1/estudiantes/{estudianteId}`

### Caso de uso
Actualizar datos editables del estudiante.

### Servicio de aplicación
- `EstudianteApplicationService.actualizar(...)`

### Recomendación de diseño V1
Definir claramente qué campos son editables para no sobreexponer mutaciones.

✅ Coherente con mutabilidad controlada (DDD-lite).

### Respuesta
- `200 OK`
- `ApiResponse<EstudianteDetailResponseDto>` o `ApiResponse<Void>`

### Errores
- `VR-*` (`400`)
- `API-04-RECURSO_NO_ENCONTRADO` (`404`)
- `RN-*` (`409`) si alguna regla contextual lo impide

---

## 7.5. `PATCH /api/v1/estudiantes/{estudianteId}/estado` (opcional pero útil)

### Caso de uso
Cambiar estado del estudiante de forma explícita (activar/inactivar, etc.).

### Servicio de aplicación
- `EstudianteApplicationService.cambiarEstado(...)`

### Request DTO sugerido
- `EstudianteCambiarEstadoRequestDto`
 - `estadoObjetivo`
 - `motivo` (opcional)

### Respuesta
- `200 OK`
- `ApiResponse<Void>` o `ApiResponse<EstudianteEstadoResponseDto>`

### Errores
- `API-04-RECURSO_NO_ENCONTRADO` (`404`)
- `RN-30-TRANSICION_ESTADO_INVALIDA` (`409`) si aplica por diseño de dominio

---

## 7.6. `DELETE /api/v1/estudiantes/{estudianteId}`

### Decisión de diseño V1
Antes de implementarlo, definir si es:
- eliminación física,
- eliminación lógica (recomendada en contextos administrativos),
- o “inactivación” vía endpoint de estado.

### Recomendación V1
Preferir **inactivación** si hay trazabilidad/histórico que preservar.

Si se mantiene `DELETE`:
- `200 OK` + `ApiResponse<Void>` (uniformidad)
- documentar claramente si es físico o lógico

### Errores
- `API-04-RECURSO_NO_ENCONTRADO` (`404`)
- `RN-*` (`409`) si no puede eliminarse por dependencias/estado

---

## 7.7. `PUT /api/v1/estudiantes/{estudianteId}/seccion-vigente` (endpoint de orquestación)

### Caso de uso
Asignar o cambiar la sección vigente de un estudiante.

### Por qué es importante
Este endpoint representa muy bien un caso multi-entidad (estudiante + sección + reglas de cupo/estado).

✅ Excelente para practicar backend real (no solo CRUD).

### Servicio de aplicación
- `EstudianteSeccionApplicationService.asignarSeccionVigente(...)`
 o
- `EstudianteApplicationService.asignarSeccionVigente(...)`

### Request DTO sugerido
- `AsignarSeccionVigenteRequestDto`
 - `seccionId`
 - `motivo` (opcional)
 - `fechaVigencia` (opcional según alcance V1)

### Respuesta
- `200 OK`
- `ApiResponse<EstudianteSeccionVigenteResponseDto>` o `ApiResponse<Void>`

### Errores esperados (muy importantes)
- `API-04-RECURSO_NO_ENCONTRADO` (`404`) si estudiante/sección no existen
- `RN-12-CUPO_SECCION_AGOTADO` (`409`)
- `RN-13-SECCION_NO_DISPONIBLE` (`409`)
- `RN-15-CAMBIO_SECCION_NO_PERMITIDO` (`409`)
- `VR-*` (`400`) por request inválido

### Comentario de trazabilidad recomendado
Documentar en Javadoc las RN aplicadas (`RN-12`, `RN-13`, `RN-15`).

---

## 8. Diseño de endpoints de Secciones (`/secciones`)

Este módulo soporta CRUD y consultas de composición con estudiantes.

## 8.1. `GET /api/v1/secciones`

### Caso de uso
Listar secciones con paginación y filtros.

### Servicio de aplicación
- `SeccionApplicationService.listar(...)`

### Query params (detalle en `08`)
- `page`, `size`, `sort`, `q`
- filtros opcionales (`estado`, grado/paralelo, período, etc. según modelo final)

### Respuesta
- `200 OK`
- `ApiResponse<PageResponseDto<SeccionListItemResponseDto>>`

### Errores
- `VR-02-PARAMETRO_INVALIDO` (`400`)
- `VR-06-VALOR_ENUM_INVALIDO` (`400`)

---

## 8.2. `GET /api/v1/secciones/{seccionId}`

### Caso de uso
Obtener detalle de sección.

### Respuesta
- `200 OK`
- `ApiResponse<SeccionDetailResponseDto>`

### Errores
- `API-04-RECURSO_NO_ENCONTRADO` (`404`)

---

## 8.3. `POST /api/v1/secciones`

### Caso de uso
Crear sección.

### Servicio
- `SeccionApplicationService.crear(...)`

### Respuesta
- `201 Created`
- `ApiResponse<SeccionDetailResponseDto>`

### Errores
- `VR-*` (`400`)
- `RN-18-REGISTRO_DUPLICADO` (`409`) si aplica a código/nombre/unicidad lógica

---

## 8.4. `PUT /api/v1/secciones/{seccionId}`

### Caso de uso
Actualizar datos editables de la sección.

### Respuesta
- `200 OK`
- `ApiResponse<SeccionDetailResponseDto>` o `ApiResponse<Void>`

### Errores
- `API-04-RECURSO_NO_ENCONTRADO` (`404`)
- `RN-*` (`409`) si estado/contexto no lo permite

---

## 8.5. `PATCH /api/v1/secciones/{seccionId}/estado` (opcional)

### Caso de uso
Cambiar estado operativo de la sección (activar/cerrar/inactivar, según diseño V1).

### Servicio
- `SeccionApplicationService.cambiarEstado(...)`

### Respuesta
- `200 OK`
- `ApiResponse<Void>`

### Errores comunes
- `API-04-RECURSO_NO_ENCONTRADO` (`404`)
- `RN-30-TRANSICION_ESTADO_INVALIDA` (`409`)
- `RN-13-SECCION_NO_DISPONIBLE` puede aparecer en endpoints consumidores, no necesariamente aquí

---

## 8.6. `GET /api/v1/secciones/{seccionId}/estudiantes`

### Caso de uso
Listar estudiantes pertenecientes a una sección (útil para tablas UI y navegación JavaFX).

### Servicio
- `SeccionConsultaApplicationService.listarEstudiantesDeSeccion(...)`
 o dentro de `SeccionApplicationService`

### Query params
- `page`, `size`, `sort`, `q` (búsqueda en estudiantes de la sección)

### Respuesta
- `200 OK`
- `ApiResponse<PageResponseDto<EstudianteListItemResponseDto>>`

### Errores
- `API-04-RECURSO_NO_ENCONTRADO` (`404`) si sección no existe

✅ Este endpoint evita que el frontend tenga que filtrar localmente toda la base.

---

## 9. Diseño de endpoints de Calificaciones (`/calificaciones`) (si aplica al V1)

> Si el alcance V1 real decide postergar calificaciones, estos endpoints pueden quedar como placeholder parcial. Pero el diseño ya queda listo.

## 9.1. `GET /api/v1/calificaciones`

### Caso de uso
Consultar calificaciones con filtros (por estudiante, sección, materia, parcial, etc.).

### Servicio
- `CalificacionApplicationService.listar(...)`

### Query params (propuestos; `08` definirá reglas)
- `page`, `size`, `sort`, `q` *(si tiene sentido)*
- `estudianteId`
- `seccionId`
- `materiaId`
- `parcial`
- `periodoId` (si aplica)

### Respuesta
- `200 OK`
- `ApiResponse<PageResponseDto<CalificacionListItemResponseDto>>`

### Errores
- `VR-*` (`400`) por parámetros inválidos
- `API-*`/`RN-*` según filtros inconsistentes si decides validarlos estrictamente

---

## 9.2. `POST /api/v1/calificaciones`

### Caso de uso
Registrar calificación.

### Servicio
- `CalificacionApplicationService.registrar(...)`

### Request DTO sugerido
- `CalificacionCreateRequestDto`
 - `estudianteId`
 - `materiaId` / `claseId` (según modelo final)
 - `numeroParcial`
 - `valor`
 - `observacion` (opcional)

### Respuesta
- `201 Created`
- `ApiResponse<CalificacionDetailResponseDto>`

### Errores comunes
- `VR-*` (`400`) por rango/formato
- `API-04-RECURSO_NO_ENCONTRADO` (`404`) si contexto base no existe
- `RN-21-CONTEXTO_ACADEMICO_INVALIDO` (`409`)
- `RN-18-REGISTRO_DUPLICADO` (`409`) si duplicado lógico

---

## 9.3. `PUT /api/v1/calificaciones/{calificacionId}`

### Caso de uso
Actualizar calificación (si la política V1 lo permite).

### Nota de negocio importante
Aquí conviene definir si la calificación se puede editar libremente o bajo restricciones (estado/período/cierre).

### Respuesta
- `200 OK`
- `ApiResponse<CalificacionDetailResponseDto>` o `ApiResponse<Void>`

### Errores
- `API-04-RECURSO_NO_ENCONTRADO` (`404`)
- `RN-*` (`409`) por reglas de edición/cierre

---

## 9.4. `DELETE /api/v1/calificaciones/{calificacionId}` (opcional)

### Recomendación V1
Confirmar si es eliminación real, anulación, o no se permite y se reemplaza por actualización controlada.

Si se implementa:
- `200 OK` + `ApiResponse<Void>`
- documentar reglas de negocio asociadas

---

## 10. Endpoints de consultas operativas / dashboard (mínimo útil + placeholders)

Este bloque es clave porque tú mencionaste querer algo “parecido” a una app JavaFX con dashboard, pero sin diseñar todavía toda la UI.

✅ Solución: definir endpoints de soporte al dashboard con alcance mínimo y placeholders controlados.

## 10.1. `GET /api/v1/dashboard/resumen` (mínimo o placeholder)

### Caso de uso
Obtener métricas/resumen para pantalla inicial.

### Opción A (implementación mínima V1)
Devolver conteos simples:
- total estudiantes activos
- total secciones activas
- solicitudes de reporte pendientes
- etc.

### Opción B (placeholder)
- `501 Not Implemented`
- `ApiErrorResponse` con `API-10-ENDPOINT_EN_CONSTRUCCION`

### Servicio (si implementas)
- `DashboardApplicationService.obtenerResumen()`

✅ Muy buena práctica dejarlo definido desde ahora aunque se implemente después.

---

## 10.2. `GET /api/v1/consultas/alertas` (opcional/placeholder)

### Caso de uso
Consultar alertas operativas simples (ej. secciones sin cupo, reportes con error, etc.)

### Estado recomendado V1
- Placeholder (`501`) si no entra en alcance inmediato.

---

## 10.3. `GET /api/v1/consultas/docentes-por-seccion`

### Caso de uso
Consultar que docentes tienen clases activas asociadas a una sección concreta.

### Servicio de aplicación
- `ConsultaAcademicaQueryService.obtenerDocentesPorSeccion(seccionId)`

### Query params
- `seccionId` obligatorio

### Respuesta
- `200 OK`
- `ApiResponse<List<DocentePorSeccionItemDto>>`

### Errores comunes
- `VR-02-PARAMETRO_INVALIDO` (`400`) si `seccionId` es invalido
- `API-04-RECURSO_NO_ENCONTRADO` (`404`) si la sección no existe
- `401/403` si el usuario no esta autenticado o no tiene rol permitido

## 10.4. `GET /api/v1/consultas/secciones-por-docente`

### Caso de uso
Consultar que secciones tienen clases activas asignadas a un docente concreto.

### Servicio de aplicación
- `ConsultaAcademicaQueryService.obtenerSeccionesPorDocente(docenteId)`

### Query params
- `docenteId` obligatorio

### Respuesta
- `200 OK`
- `ApiResponse<List<SeccionPorDocenteItemDto>>`

### Errores comunes
- `VR-02-PARAMETRO_INVALIDO` (`400`) si `docenteId` es invalido
- `API-04-RECURSO_NO_ENCONTRADO` (`404`) si el docente no existe
- `401/403` si el usuario no esta autenticado o no tiene rol permitido

## 10.5. Gestión administrativa de usuarios (`/usuarios`)

### Caso de uso
Cubrir la gobernanza operativa de cuentas administrativas sin mezclar CRUD de usuarios con `auth`.

### Endpoints base
- `GET /api/v1/usuarios`
- `GET /api/v1/usuarios/{usuarioId}`
- `POST /api/v1/usuarios`
- `PUT /api/v1/usuarios/{usuarioId}`
- `PATCH /api/v1/usuarios/{usuarioId}/estado`

### Servicios de aplicación
- `UsuarioQueryService`
- `UsuarioCommandService`

### Notas de diseÃ±o
- protegido solo para rol `ADMIN`
- el hash de password debe resolverse via puerto/adaptador técnico
- nunca se expone `passwordHash` por API
- los listados deben soportar `page`, `size`, `sort`, `q`, `estado` y `rol`

---

## 11. Diseño de endpoints de reportes y cola simple DB queue (`/reportes`)

> Este bloque se alinea directamente con `10_backend_v1_reporte_solicitudes_cola_simple_db_queue.md`.

## 11.1. Enfoque funcional V1

El backend **sí** maneja la **solicitud, estado y materialización final** del reporte (cola simple en DB + archivos `PDF`, `DOCX`, `XLSX`).

✅ JavaFX consume el flujo asíncrono y descarga el resultado sin asumir la responsabilidad de renderizar el archivo final.

---

## 11.2. `POST /api/v1/reportes/solicitudes`

### Caso de uso
Registrar una solicitud de generación de reporte en cola simple (DB queue).

### Servicio de aplicación
- `ReporteSolicitudApplicationService.solicitarReporte(...)`

### Request DTO sugerido
- `ReporteSolicitudCreateRequestDto`
 - `tipoReporte`
 - `parametros` (objeto o estructura tipada según alcance)
 - `modoEntrega` (opcional; si aplica)

### Respuesta exitosa
- `201 Created`
- `ApiResponse<ReporteSolicitudResponseDto>`

### `data` mínima sugerida
- `solicitudId`
- `tipoReporte`
- `estado` (`PENDIENTE`)
- `fechaSolicitud`

### Errores comunes
- `VR-01-REQUEST_INVALIDO` (`400`)
- `VR-06-VALOR_ENUM_INVALIDO` (`400`) para `tipoReporte`
- `RN-31-ESTADO_REPORTE_NO_PERMITE_OPERACION` (`409`) si hay política de deduplicación/estado
- `SYS-01-ERROR_INTERNO` (`500`)

---

## 11.3. `GET /api/v1/reportes/solicitudes`

### Caso de uso
Listar solicitudes de reporte con filtros y paginación.

### Servicio
- `ReporteSolicitudApplicationService.listar(...)`

### Query params sugeridos
- `page`, `size`, `sort`, `q`
- `estado`
- `tipoReporte`
- `fechaDesde`, `fechaHasta` (si entra en V1)

### Respuesta
- `200 OK`
- `ApiResponse<PageResponseDto<ReporteSolicitudListItemResponseDto>>`

### Estados estandarizados (obligatorios)
- `PENDIENTE`
- `EN_PROCESO`
- `COMPLETADA`
- `ERROR`
- `CANCELADA` (si se usa)

❌ No mezclar sinónimos (`PROCESANDO`, `COMPLETADO`) si el resto de documentos usa los anteriores.

---

## 11.4. `GET /api/v1/reportes/solicitudes/{solicitudId}`

### Caso de uso
Consultar detalle de una solicitud de reporte.

### Respuesta
- `200 OK`
- `ApiResponse<ReporteSolicitudDetailResponseDto>`

### Errores
- `API-04-RECURSO_NO_ENCONTRADO` (`404`)

---

## 11.5. `GET /api/v1/reportes/solicitudes/{solicitudId}/estado`

### Caso de uso
Consultar solo el estado de procesamiento de una solicitud (útil para polling desde JavaFX).

### Servicio
- `ReporteSolicitudApplicationService.obtenerEstado(solicitudId)`

### Respuesta
- `200 OK`
- `ApiResponse<ReporteSolicitudEstadoResponseDto>`

### Ventaja arquitectónica
Permite a JavaFX refrescar solo estado sin descargar detalle completo.

✅ Muy útil para práctica de UI reactiva/polling simple.

---

## 11.6. `POST /api/v1/reportes/solicitudes/{solicitudId}/reintentar` (opcional)

### Caso de uso
Reintentar una solicitud que quedó en `ERROR`.

### Servicio
- `ReporteSolicitudApplicationService.reintentar(...)`

### Reglas probables
- solo si estado actual = `ERROR`
- transición controlada a `PENDIENTE` o `EN_PROCESO`

### Errores comunes
- `API-04-RECURSO_NO_ENCONTRADO` (`404`)
- `RN-30-TRANSICION_ESTADO_INVALIDA` (`409`)
- `RN-31-ESTADO_REPORTE_NO_PERMITE_OPERACION` (`409`)

---

## 11.7. `GET /api/v1/reportes/solicitudes/{solicitudId}/resultado` (placeholder probable)

### Caso de uso
Obtener resultado generado (archivo, JSON de salida, metadata de descarga, etc.)

### Estado recomendado V1
Si aún no implementas generación real/almacenamiento de resultado:
- `501 Not Implemented`
- `ApiErrorResponse`
- `API-10-ENDPOINT_EN_CONSTRUCCION`

✅ Arquitectura lista, implementación diferida.

---

## 11.8. `GET /api/v1/reportes/preview` (placeholder para futura integración UI)

### Caso de uso
Previsualización de reporte (si más adelante decides backend-assisted preview).

### Estado recomendado V1
- Placeholder `501`

---

## 12. Endpoints placeholder explícitos de V1 (lista oficial)

Para evitar ambigüedades, se puede declarar explícitamente qué endpoints están planificados pero no implementados.

## 12.1. Placeholders recomendados (según alcance real)

- `GET /api/v1/dashboard/resumen` *(si no lo implementas aún)*
- `GET /api/v1/consultas/alertas`
- `GET /api/v1/reportes/preview`
- `GET /api/v1/reportes/solicitudes/{solicitudId}/resultado`

## 12.2. Contrato obligatorio del placeholder

- status: `501 Not Implemented`
- body: `ApiErrorResponse`
- código: `API-10-ENDPOINT_EN_CONSTRUCCION`

✅ Ya definido en `05` y reforzado en `07`.

---

## 13. Diseño de DTOs por endpoint (reglas de oro para V1)

## 13.1. No exponer entidades JPA en controllers

Cada endpoint debe usar DTOs de request/response.

❌ No devolver entidad persistente directamente.

✅ Alineado con `03`, `04` y `05`.

---

## 13.2. Tipos de DTO recomendados por módulo

### Request DTOs
- `Create...RequestDto`
- `Update...RequestDto`
- `CambiarEstado...RequestDto`
- `...FiltroRequestDto` *(solo si el filtro se vuelve complejo; si no, query params directos)*

### Response DTOs
- `...ListItemResponseDto`
- `...DetailResponseDto`
- `...CreatedResponseDto` *(opcional)
- `...EstadoResponseDto`

✅ Naming explícito = código más navegable.

---

## 13.3. Mappers manuales (tu preferencia)

Se mantiene decisión de mappers manuales para aprender/controlar.

### Regla práctica
- mapper convierte datos (entity ↔ DTO)
- **no** ejecuta reglas de negocio
- **no** consulta repositorios
- **no** decide errores

✅ Caso de uso orquesta; mapper transforma.

---

## 14. Matriz endpoint → caso de uso → módulo (resumen operativo)

> Esta sección sirve como “mapa rápido” cuando empieces a implementar controllers.

## 14.1. Auth
- `POST /auth/login` → `AuthApplicationService.login` → módulo `auth`
- `GET /auth/me` → `AuthApplicationService.getCurrentUser` → módulo `auth`

## 14.2. Estudiantes
- `GET /estudiantes` → `listar` → módulo `estudiante`
- `GET /estudiantes/{id}` → `obtenerPorId` → módulo `estudiante`
- `POST /estudiantes` → `crear` → módulo `estudiante`
- `PUT /estudiantes/{id}` → `actualizar` → módulo `estudiante`
- `PATCH /estudiantes/{id}/estado` → `cambiarEstado` → módulo `estudiante`
- `PUT /estudiantes/{id}/seccion-vigente` → `asignarSeccionVigente` → módulo de orquestación `estudiante-seccion`

## 14.3. Secciones
- `GET /secciones` → `listar` → módulo `seccion`
- `GET /secciones/{id}` → `obtenerPorId` → módulo `seccion`
- `POST /secciones` → `crear` → módulo `seccion`
- `PUT /secciones/{id}` → `actualizar` → módulo `seccion`
- `PATCH /secciones/{id}/estado` → `cambiarEstado` → módulo `seccion`
- `GET /secciones/{id}/estudiantes` → `listarEstudiantesDeSeccion` → consulta/orquestación

## 14.4. Calificaciones
- `GET /calificaciones` → `listar` → módulo `calificacion`
- `POST /calificaciones` → `registrar` → módulo `calificacion`
- `PUT /calificaciones/{id}` → `actualizar` → módulo `calificacion`

## 14.5. Consultas operativas
- `GET /consultas/docentes-por-seccion` → `obtenerDocentesPorSeccion` → módulo `consultaacademica`
- `GET /consultas/secciones-por-docente` → `obtenerSeccionesPorDocente` → módulo `consultaacademica`

## 14.6. Usuarios del sistema
- `GET /usuarios` → `listar` → módulo `usuario`
- `GET /usuarios/{id}` → `obtenerPorId` → módulo `usuario`
- `POST /usuarios` → `crear` → módulo `usuario`
- `PUT /usuarios/{id}` → `actualizar` → módulo `usuario`
- `PATCH /usuarios/{id}/estado` → `cambiarEstado` → módulo `usuario`

## 14.7. Reportes
- `POST /reportes/solicitudes` → `solicitarReporte` → módulo `reporte-solicitud`
- `GET /reportes/solicitudes` → `listar` → módulo `reporte-solicitud`
- `GET /reportes/solicitudes/{id}` → `obtenerDetalle` → módulo `reporte-solicitud`
- `GET /reportes/solicitudes/{id}/estado` → `obtenerEstado` → módulo `reporte-solicitud`
- `POST /reportes/solicitudes/{id}/reintentar` → `reintentar` → módulo `reporte-solicitud`

---

## 15. Política de errores por endpoint (cómo documentarlos sin exagerar)

No hace falta listar 20 errores por endpoint en V1. Recomendación práctica:

## 15.1. Documentar siempre
- `400` (`VR-*`) si recibe entrada
- `404` (`API-04`) si usa `{id}` de recurso
- `409` (`RN-*`) si hay RN relevantes
- `401/403` si es protegido
- `500` (`SYS-01`) como fallback

## 15.2. Documentar adicionalmente cuando aplique
- `501` (`API-10`) en placeholders

✅ Esto deja Swagger claro sin hacerlo inmanejable.

---

## 16. Política de seguridad por endpoint (vista funcional; detalle en `09`)

## 16.1. Clasificación básica

### Públicos (si decides)
- `POST /api/v1/auth/login`
- `GET /api/v1/actuator/health` *(si habilitas health, fuera de este documento funcional)*

### Protegidos (recomendado V1)
- prácticamente todos los módulos de negocio (`estudiantes`, `secciones`, `calificaciones`, `reportes`, `dashboard`)

---

## 16.2. Respuestas de seguridad obligatorias

Endpoints protegidos deben poder responder con contrato estándar:
- `401` + `AUTH-*`
- `403` + `AUTH-*`

✅ Esto debe quedar consistente con `09` (entry point + access denied handler).

---

## 17. Paginación, filtros y ordenamiento por endpoint (interfaz de uso)

> El documento técnico detallado es `08`, pero aquí se deja el criterio por endpoint.

## 17.1. Endpoints paginados (V1)

### Deben soportar paginación
- `GET /estudiantes`
- `GET /secciones`
- `GET /secciones/{seccionId}/estudiantes`
- `GET /calificaciones` *(si implementado)*
- `GET /usuarios`
- `GET /reportes/solicitudes`

### Pueden no requerir paginación en V1
- catálogos pequeños (si existieran) y si su tamaño es realmente bajo

---

## 17.2. Convención mínima de query params comunes

- `page` (base 0)
- `size`
- `sort` (`campo,direccion`)
- `q` (búsqueda simple)

✅ `08` definirá límites, whitelist y parseo robusto.

---

## 18. Diseño para JavaFX sin acoplarse a JavaFX (principio importante)

Tú lo planteaste muy bien: el backend no se diseña “pegado” a la pantalla, pero sí puede ser **amigable** con la UI.

## 18.1. Qué sí hacer (backend-friendly para JavaFX)

- endpoints de detalle y listados paginados
- endpoint de polling de estado (`/reportes/.../estado`)
- respuestas con `errorCode` + `details` para validar campos
- endpoints de orquestación claros (ej. asignar sección)

## 18.2. Qué no hacer

- crear endpoint por widget/pantalla si no hay caso de uso real
- devolver payloads “mezclados” solo para evitar llamadas adicionales sin justificación
- meter lógica de UX/UI en el backend

✅ Backend agnóstico de UI, pero útil para UI.

---

## 19. Secuencia de implementación recomendada (práctica V1)

Para no cansarte mentalmente, y maximizar avance visible:

## 19.1. Fase 1 (base técnica)
1. `auth/login` (mínimo)
2. contrato API (`05`) + handler global (`07`) en código
3. endpoint `GET /estudiantes` paginado básico
4. endpoint `GET /estudiantes/{id}`

## 19.2. Fase 2 (CRUD útil)
5. `POST /estudiantes`
6. `PUT /estudiantes/{id}`
7. `GET /secciones`
8. `GET /secciones/{id}`

## 19.3. Fase 3 (orquestación real)
9. `PUT /estudiantes/{id}/seccion-vigente`
10. `GET /secciones/{id}/estudiantes`

## 19.4. Fase 4 (cola simple reportes)
11. `POST /reportes/solicitudes`
12. `GET /reportes/solicitudes`
13. `GET /reportes/solicitudes/{id}/estado`
14. placeholders de preview/resultado (`501`)

✅ Esta secuencia te da progreso tangible sin bloquearte por lo más complejo.

---

## 20. Swagger/OpenAPI: qué debe salir de este documento

`09` implementará la parte técnica, pero este documento ya define el contenido mínimo:

## 20.1. Por endpoint documentar
- summary
- descripción corta del caso de uso
- request DTO (si aplica)
- response DTO exitoso (envuelto en `ApiResponse`)
- errores principales (`VR/RN/API/AUTH/SYS`)
- ejemplos de query params (`q`, `page`, `size`, `sort`)

---

## 20.2. Tags sugeridos para Swagger
- `Auth`
- `Estudiantes`
- `Secciones`
- `Calificaciones`
- `Reportes`
- `Dashboard` (si existe)

✅ Esto mejora la navegación y te sirve cuando conectes JavaFX.

---

## 21. Riesgos de diseño comunes y mitigación

## 21.1. Hacer solo CRUD por tabla

**Riesgo:** backend queda pobre para casos reales.

**Mitigación:** incluir endpoints de orquestación (ej. asignar sección, reportes).

---

## 21.2. Hacer demasiados endpoints “por si acaso”

**Riesgo:** sobreingeniería y mantenimiento pesado.

**Mitigación:** placeholders `501` solo para los pocos que ya están en roadmap real.

---

## 21.3. Mezclar rutas inconsistentes

**Riesgo:** API difícil de memorizar y documentar.

**Mitigación:** convención REST estable + revisión cruzada con `08` y `09`.

---

## 21.4. Acoplar endpoints a una pantalla específica de JavaFX

**Riesgo:** API rígida y poco reusable.

**Mitigación:** diseñar por caso de uso de dominio + consultas operativas generales.

---

## 21.5. Omitir endpoints de estado en reportes

**Riesgo:** JavaFX termina haciendo polling pesado del detalle completo.

**Mitigación:** mantener `/reportes/solicitudes/{id}/estado` como endpoint dedicado.

---

## 22. Checklist de diseño por endpoint (antes de implementar)

Para cada endpoint en código revisar:

### 22.1. Contrato
- [ ] ¿Responde con `ApiResponse`/`ApiErrorResponse`?
- [ ] ¿Usa DTOs y no entidades JPA?

### 22.2. Caso de uso
- [ ] ¿Está mapeado a un método de `application` claramente nombrado?
- [ ] ¿La lógica de negocio está fuera del controller?

### 22.3. Validaciones y errores
- [ ] ¿Tiene `@Valid` donde corresponde?
- [ ] ¿Están previstos `VR-*`, `API-*`, `RN-*` principales?
- [ ] ¿Se documentó `501` si es placeholder?

### 22.4. Consultas
- [ ] ¿Usa `q` y no `search`?
- [ ] ¿Si es listado, soporta paginación/ordenamiento según `08`?

### 22.5. Seguridad
- [ ] ¿Está clasificado como público o protegido?
- [ ] ¿Contempla `401/403` con contrato estándar?

---

## 23. Decisiones fijadas por este documento (V1)

1. ✅ La API V1 se diseña por **casos de uso + módulos**, no solo por tablas.
2. ✅ Se adopta base path **`/api/v1`**.
3. ✅ Se mantiene convención `q` para búsqueda simple.
4. ✅ Los endpoints de listados clave serán paginados (`ApiResponse<PageResponseDto<T>>`).
5. ✅ Se incluye al menos un endpoint de orquestación real: `PUT /estudiantes/{id}/seccion-vigente`.
6. ✅ Se incluye módulo de reportes con **cola simple DB queue** (`/reportes/solicitudes`).
7. ✅ Se permiten placeholders `501` con `API-10-ENDPOINT_EN_CONSTRUCCION`.
8. ✅ Los endpoints se diseñan backend-friendly para JavaFX **sin acoplarse a la UI**.
9. ✅ Swagger/OpenAPI (`09`) deberá reflejar este catálogo y sus contratos.
10. ✅ El detalle de paginación/filtros/ordenamiento se centraliza en `08`, pero este documento fija qué endpoints los usan.

---

## 24. Relación con documentos siguientes

- **`08_backend_v1_paginacion_filtros_ordenamiento_y_consultas.md`**
 aterriza `page/size/sort/q`, filtros por endpoint y whitelist de ordenamiento.

- **`09_backend_v1_seguridad_documentacion_y_despliegue_minimo.md`**
 define protección de endpoints, JWT, Swagger/OpenAPI y despliegue mínimo con Docker/env vars.

- **`10_backend_v1_reporte_solicitudes_cola_simple_db_queue.md`**
 profundiza el flujo del módulo de reportes (estados, transiciones, persistencia y procesamiento simple).

---

## 25. Cierre

## Addendum 2026-03-03: auth real implementado

Este addendum actualiza la narrativa original del módulo `auth` para reflejar el estado real del backend.

Endpoints vigentes:
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /api/v1/auth/me`

Contrato vigente de `login` y `refresh`:
- `accessToken`
- `refreshToken`
- `tokenType`
- `expiresInSeconds`
- `refreshExpiresInSeconds`
- `usuario`

Decision de diseño:
- `refresh` y `logout` quedaron publicos porque el cliente desktop puede necesitar renovar o cerrar sesión cuando el `accessToken` ya expiro
- la revocacion real se hace sobre el `refreshToken`, no sobre el JWT de acceso

Caso de uso real:
- `AuthApplicationService.login(...)`
- `AuthApplicationService.refresh(...)`
- `AuthApplicationService.logout(...)`
- `AuthApplicationService.me(...)`

Este documento deja el backend V1 con un **mapa de rutas y casos de uso profesional**, suficientemente claro para que empieces a implementar controllers y servicios sin improvisar.

En la práctica, esto te da algo muy valioso para tu primera experiencia formal:
- sabes qué endpoints existen,
- por qué existen,
- qué errores deben manejar,
- cuáles son placeholders,
- y cómo conectarlos después desde JavaFX sin rehacer la arquitectura.

✅ Con `05 + 06 + 07` ya tienes una base muy buena para empezar código real de backend con Spring Boot.

