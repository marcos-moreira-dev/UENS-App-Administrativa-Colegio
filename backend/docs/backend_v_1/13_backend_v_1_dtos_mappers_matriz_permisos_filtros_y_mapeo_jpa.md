# 13_backend_v_1_dtos_mappers_matriz_permisos_filtros_y_mapeo_jpa

## 1. Propósito del documento

Definir el **diseño de detalle de implementación** del backend V1 para las piezas que faltan cerrar antes de programar con consistencia:

1. Catálogo de **DTOs** por endpoint/caso de uso
2. Lineamientos de **mappers** (manuales)
3. **Matriz endpoint → rol/permisos**
4. **Whitelists** de filtros/ordenamiento por endpoint de listado
5. **Mapeo JPA ↔ PostgreSQL (V2_3FN oficial)**
6. Diseño de **payloads** de reportes (JSON) por tipo
7. Convención mínima de claves `messages.properties`

> **Fuera de alcance (diferido):** migraciones SQL/Flyway/Liquibase. La BD oficial (`V2_3FN.sql`) sigue siendo la fuente de verdad en esta fase.

---

## 2. Decisiones fijas que este documento respeta

### 2.1 Diseño oficial que NO se rompe
- BD oficial PostgreSQL 3FN (`V2_3FN.sql`) con 8 tablas del dominio principal.
- `clase` como núcleo operativo de planificación.
- `calificacion` referida a `estudiante + clase + parcial`.
- `usuario_sistema_administrativo` aislado del dominio académico.
- Contrato API estándar (`ApiResponse<T>`, `ApiErrorResponse`, `PageResponseDto<T>`).
- Paginación/filtros/ordenamiento con `page`, `size`, `sort`, `q`.
- Seguridad JWT stateless + roles + `401/403` en JSON.
- Reportes V1 con DB queue en PostgreSQL y polling.

### 2.2 Aclaración importante sobre “matrícula”
- **No existe tabla `matricula`** en la BD oficial actual.
- La asignación vigente se modela con `estudiante.seccion_id` + reglas de negocio/backend.
- Por tanto, el caso de uso de “matriculación/asignación de sección” se implementa como **orquestación**, no como nueva entidad persistente (en esta fase).

---

## 3. DTOs: criterio de diseño (regla general)

### 3.1 Qué es un DTO en este proyecto (y cuál es su rol)
Un DTO (**Data Transfer Object**) es una estructura para mover datos entre capas/API con un propósito específico. En este backend V1:

- **No tiene por qué contener todos los atributos** de una entidad.
- **Puede mezclar datos de varias entidades** si el caso de uso lo requiere (ej. dashboard, reportes, listados enriquecidos).
- **Puede representar vistas especializadas** (listado, detalle, resumen, fila, estado, metadata).
- **No reemplaza al dominio** ni a las entidades JPA.

### 3.2 Reglas prácticas
- **DTO por caso de uso / endpoint**, no “DTO genérico para todo”.
- Separar **RequestDto** y **ResponseDto**.
- Para listados, preferir **ListItemDto** o **RowDto** (más livianos que detalle completo).
- Para operaciones compuestas/orquestación, usar DTOs específicos (no reciclar create/update de otra entidad si semánticamente no encaja).
- Los DTOs NO deben exponer detalles internos de JPA ni campos sensibles.

### 3.3 Tipos de DTOs permitidos en V1
- **Request DTOs**: entrada HTTP
- **Response DTOs**: salida HTTP de detalle/acción
- **ListItem DTOs**: filas de listados paginados
- **Patch DTOs**: cambios parciales (ej. estado)
- **Query DTOs internos** (opcional): encapsular filtros de aplicación
- **Payload DTOs de reporte**: JSON de resultados asíncronos por tipo de reporte

---

## 4. Catálogo mínimo de DTOs (V1)

> Este catálogo es el mínimo recomendado para mantener consistencia y avanzar. Se puede ampliar sin romper contrato si se hace con control.

### 4.1 Transversales (`common/api` o similar)
- `ApiResponse<T>`
- `ApiErrorResponse`
- `ErrorDetailDto`
- `PageResponseDto<T>`

### 4.2 Auth (`auth`)
#### Requests
- `LoginRequestDto`
  - `login`
  - `password`

#### Responses
- `LoginResponseDto`
  - `accessToken`
  - `tokenType`
  - `expiresInSeconds`
  - `usuario` (resumen)
- `AuthUsuarioResumenDto`
  - `id`
  - `login`
  - `rol`
  - `estado`
- (Opcional) `AuthMeResponseDto`

### 4.3 Estudiantes (`estudiante`)
#### Requests
- `EstudianteCreateRequestDto`
  - datos del estudiante
  - `representanteLegalId` (obligatorio)
  - `seccionId` (opcional si el flujo lo permite)
- `EstudianteUpdateRequestDto`
- `EstudiantePatchEstadoRequestDto`
  - `estado`
- `AsignarSeccionVigenteRequestDto` (**orquestación**)
  - `seccionId`
  - (opcional) `motivo` / `observacion` si quieres trazabilidad simple

#### Responses
- `EstudianteResponseDto` (detalle)
- `EstudianteListItemDto` (listado)
- `EstudianteResumenDto` (reutilizable en respuestas anidadas, opcional)

### 4.4 Representantes (`representante`)
#### Requests
- `RepresentanteLegalCreateRequestDto`
- `RepresentanteLegalUpdateRequestDto`

#### Responses
- `RepresentanteLegalResponseDto`
- `RepresentanteLegalListItemDto`

### 4.5 Secciones (`seccion`)
#### Requests
- `SeccionCreateRequestDto`
- `SeccionUpdateRequestDto`
- `SeccionPatchEstadoRequestDto`

#### Responses
- `SeccionResponseDto`
- `SeccionListItemDto`
- `SeccionEstudianteListItemDto` (para `/secciones/{id}/estudiantes`, si se expone como subrecurso)

### 4.6 Docentes (`docente`)
#### Requests
- `DocenteCreateRequestDto`
- `DocenteUpdateRequestDto`
- `DocentePatchEstadoRequestDto`

#### Responses
- `DocenteResponseDto`
- `DocenteListItemDto`

### 4.7 Asignaturas (`asignatura`)
#### Requests
- `AsignaturaCreateRequestDto`
- `AsignaturaUpdateRequestDto`
- `AsignaturaPatchEstadoRequestDto`

#### Responses
- `AsignaturaResponseDto`
- `AsignaturaListItemDto`

### 4.8 Clases (`clase`)
#### Requests
- `ClaseCreateRequestDto`
  - `seccionId`
  - `asignaturaId`
  - `docenteId` (nullable)
  - `diaSemana`
  - `horaInicio`
  - `horaFin`
  - `estado` (opcional, o derivado por defecto)
- `ClaseUpdateRequestDto`
- `ClasePatchEstadoRequestDto`
- `ClaseAsignarDocenteRequestDto` (si prefieres endpoint específico)

#### Responses
- `ClaseResponseDto`
- `ClaseListItemDto`

### 4.9 Calificaciones (`calificacion`)
#### Requests
- `CalificacionCreateRequestDto`
  - `estudianteId`
  - `claseId`
  - `numeroParcial`
  - `nota`
  - `fechaRegistro` (opcional)
  - `observacion` (opcional)
- `CalificacionUpdateRequestDto`

#### Responses
- `CalificacionResponseDto`
- `CalificacionListItemDto`

### 4.10 Dashboard / Consultas (`dashboard`, `consulta`)
#### Responses (orientados a caso de uso, no a tabla)
- `DashboardResumenResponseDto`
- (Opcional) `DashboardAlertaDto`, `DashboardTotalesDto`, etc.

### 4.11 Reportes / cola DB queue (`reporte`)
#### Requests
- `CrearReporteSolicitudRequestDto`
  - `tipoReporte`
  - `parametros` (estructura JSON/objeto)
  - `idempotencyKey` (opcional)

#### Responses
- `ReporteSolicitudCreadaResponseDto`
- `ReporteSolicitudDetalleResponseDto`
- `ReporteSolicitudResultadoResponseDto`
- `ReporteSolicitudListItemDto`

#### Payload DTOs de reporte (por tipo)
- `ListadoEstudiantesPorSeccionPayloadDto`
- `ResumenSeccionPayloadDto`
- `CalificacionesPorSeccionParcialPayloadDto`
- `CalificacionesPorEstudiantePayloadDto`

> Nota: estos payloads representan el JSON que el backend prepara y JavaFX consume para renderizar/exportar.

### 4.12 Auditoria operativa (`auditoria`)
#### Requests
- `CrearAuditoriaReporteRequestDto`
  - `formatoSalida` (`XLSX|PDF|DOCX`)
  - `fechaDesde` (opcional)
  - `fechaHasta` (opcional)
  - `módulo` (opcional)
  - `accion` (opcional)
  - `resultado` (opcional)
  - `actorLogin` (opcional)
  - `incluirDetalle` (opcional, default `true`)

#### Responses
- `AuditoriaEventoListItemDto`
- `ReporteSolicitudCreadaResponseDto` (reuso del módulo `reporte`)

#### Payload DTOs de reporte (por tipo)
- `AuditoriaAdminOperacionesPayloadDto` (representado hoy como payload map estructurado)

---

## 5. Mappers manuales: reglas mínimas (V1)

### 5.1 Rol del mapper
Los mappers manuales convierten entre:
- Request DTO → comandos/objetos de aplicación (o estructuras de entrada)
- Entidad/JPA → Response DTO / ListItem DTO
- Resultado interno de reporte → Payload DTO

### 5.2 Qué NO hace un mapper
- No aplica reglas de negocio (`RN-*`)
- No valida invariantes complejas
- No consulta repositorios (salvo casos excepcionales y explícitos, no recomendado)
- No decide permisos

### 5.3 Qué sí puede hacer
- Normalizaciones triviales de salida (ej. null-safe de strings, formateo simple si está definido)
- Armar DTOs compuestos con datos ya resueltos por application/query service

### 5.4 Reglas de estilo
- Un mapper por módulo/caso de uso cuando el volumen crezca
- Métodos con nombres explícitos:
  - `toResponseDto(...)`
  - `toListItemDto(...)`
  - `toEntity(...)` (solo si aplica y no rompe la encapsulación)
- Si el mapper empieza a tener lógica de negocio, esa lógica se mueve a `application` o `domain`

---

## 6. Matriz endpoint → rol/permisos (V1)

> Esta matriz es la versión inicial recomendada. Ajustar nombres de rol cuando cierres catálogo final (`ADMIN`, `SECRETARIA`, etc.).

### 6.1 Convención de roles (propuesta base)
- `ADMIN`
- `SECRETARIA`

> Si tu tabla actual usa `ADMINISTRATIVO` como default, se puede mapear temporalmente a uno de estos criterios, pero conviene definir el catálogo final antes de implementar permisos finos.

### 6.2 Matriz inicial (resumen)

#### Auth
- `POST /api/v1/auth/login` → **Público**
- `GET /api/v1/auth/me` → `ADMIN`, `SECRETARIA` (autenticado)
- `POST /api/v1/auth/logout` (si existe) → `ADMIN`, `SECRETARIA`

#### Estudiantes
- `GET /api/v1/estudiantes` → `ADMIN`, `SECRETARIA`
- `GET /api/v1/estudiantes/{id}` → `ADMIN`, `SECRETARIA`
- `POST /api/v1/estudiantes` → `ADMIN`, `SECRETARIA`
- `PUT /api/v1/estudiantes/{id}` → `ADMIN`, `SECRETARIA`
- `PATCH /api/v1/estudiantes/{id}/estado` → `ADMIN` (o `ADMIN`, `SECRETARIA` según política)
- `DELETE /api/v1/estudiantes/{id}` → `ADMIN` (si V1 usa borrado lógico/restricción)
- `PUT /api/v1/estudiantes/{id}/seccion-vigente` → `ADMIN`, `SECRETARIA`

#### Secciones
- `GET /api/v1/secciones` → `ADMIN`, `SECRETARIA`
- `GET /api/v1/secciones/{id}` → `ADMIN`, `SECRETARIA`
- `POST /api/v1/secciones` → `ADMIN`
- `PUT /api/v1/secciones/{id}` → `ADMIN`
- `PATCH /api/v1/secciones/{id}/estado` → `ADMIN`
- `GET /api/v1/secciones/{id}/estudiantes` → `ADMIN`, `SECRETARIA`

#### Docentes / Asignaturas / Clases / Calificaciones
- Lectura → `ADMIN`, `SECRETARIA`
- Escritura (create/update/delete/patch estado) → `ADMIN` (o compartido con `SECRETARIA` si tu operación real lo requiere)

#### Dashboard / Consultas
- `GET /api/v1/dashboard/resumen` → `ADMIN`, `SECRETARIA`
- consultas operativas → `ADMIN`, `SECRETARIA`

#### Reportes (solicitudes)
- `POST /api/v1/reportes/solicitudes` → `ADMIN`, `SECRETARIA`
- `GET /api/v1/reportes/solicitudes` → `ADMIN`, `SECRETARIA`
- `GET /api/v1/reportes/solicitudes/{id}` → `ADMIN`, `SECRETARIA`
- `GET /api/v1/reportes/solicitudes/{id}/estado` → `ADMIN`, `SECRETARIA`
- `GET /api/v1/reportes/solicitudes/{id}/resultado` → `ADMIN`, `SECRETARIA`
- `POST /api/v1/reportes/solicitudes/{id}/reintentar` (si se implementa) → `ADMIN`

#### Auditoria
- `GET /api/v1/auditoria/eventos` → `ADMIN`
- `POST /api/v1/auditoria/reportes/solicitudes` → `ADMIN`

### 6.3 Regla `401/403`
- `401`: no autenticado / token inválido / expirado
- `403`: autenticado, pero sin permisos

---

## 7. Whitelists de filtros/ordenamiento por endpoint (V1)

> Regla global: **nunca** aceptar `sort` o filtros arbitrarios sin whitelist.

### 7.1 Convenciones globales
- `page` (base 0)
- `size` (default y max definidos por endpoint)
- `sort` (repetible, formato `campo,direccion`)
- `q` (búsqueda textual simple, semántica definida por endpoint)

### 7.2 Estudiantes — `GET /api/v1/estudiantes`
#### Filtros permitidos
- `estado`
- `seccionId`
- `representanteLegalId` (opcional)
- `q` (nombres/apellidos)

#### Sort permitido (whitelist)
- `id`
- `nombres`
- `apellidos`
- `fechaNacimiento`
- `estado`

#### Sort por defecto
- `apellidos,asc`
- `nombres,asc`

### 7.3 Secciones — `GET /api/v1/secciones`
#### Filtros permitidos
- `estado`
- `grado`
- `paralelo`
- `anioLectivo`
- `q` (si aporta; ej. búsqueda por paralelo o año)

#### Sort permitido
- `id`
- `anioLectivo`
- `grado`
- `paralelo`
- `estado`

#### Sort por defecto
- `anioLectivo,desc`
- `grado,asc`
- `paralelo,asc`

### 7.4 Docentes — `GET /api/v1/docentes`
#### Filtros permitidos
- `estado`
- `q` (nombres/apellidos/correo, según implementación)

#### Sort permitido
- `id`
- `nombres`
- `apellidos`
- `estado`

#### Sort por defecto
- `apellidos,asc`
- `nombres,asc`

### 7.5 Asignaturas — `GET /api/v1/asignaturas`
#### Filtros permitidos
- `estado`
- `grado`
- `area`
- `q` (nombre/descripcion opcional)

#### Sort permitido
- `id`
- `nombre`
- `grado`
- `area`
- `estado`

#### Sort por defecto
- `grado,asc`
- `nombre,asc`

### 7.6 Clases — `GET /api/v1/clases`
#### Filtros permitidos
- `estado`
- `seccionId`
- `asignaturaId`
- `docenteId`
- `diaSemana`
- `q` (opcional, si implementas búsqueda textual sobre datos relacionados)

#### Sort permitido
- `id`
- `diaSemana`
- `horaInicio`
- `horaFin`
- `estado`

#### Sort por defecto
- `diaSemana,asc`
- `horaInicio,asc`

### 7.7 Calificaciones — `GET /api/v1/calificaciones`
#### Filtros permitidos
- `estudianteId`
- `claseId`
- `numeroParcial`
- `fechaRegistroDesde` (opcional)
- `fechaRegistroHasta` (opcional)
- `q` (normalmente no aplica; usar solo si tiene semántica clara)

#### Sort permitido
- `id`
- `numeroParcial`
- `nota`
- `fechaRegistro`

#### Sort por defecto
- `id,desc`

### 7.8 Reportes — `GET /api/v1/reportes/solicitudes`
#### Filtros permitidos
- `estado`
- `tipoReporte`
- `requestedByUserId` (si se expone)
- `fechaDesde`
- `fechaHasta`
- `q` (opcional; solo si tiene semántica clara)

#### Sort permitido
- `id`
- `estado`
- `tipoReporte`
- `requestedAt`
- `startedAt`
- `finishedAt`

#### Sort por defecto
- `requestedAt,desc`

### 7.9 Auditoria — `GET /api/v1/auditoria/eventos`
#### Filtros permitidos
- `q`
- `módulo`
- `accion`
- `resultado`
- `actorLogin`
- `fechaDesde`
- `fechaHasta`

#### Sort permitido
- `id`
- `módulo`
- `accion`
- `resultado`
- `actorLogin`
- `fechaEvento`

#### Sort por defecto
- `fechaEvento,desc`

### 7.10 Reglas de validación de filtros/sort (ligadas a `07`)
- Campo de `sort` fuera de whitelist → `VR-*` (`400`)
- Dirección inválida → `VR-*` (`400`)
- `size` fuera de rango → `VR-*` (`400`)
- Filtros incompatibles → `VR-*` o `RN-*` según semántica

---

## 8. Catálogo mínimo de errores por módulo (sí se recomienda cerrar)

> Aunque se puede ampliar luego, conviene definir **un catálogo base** desde ya para no improvisar códigos.

### 8.1 Por qué sí conviene hacerlo ahora
- Evita códigos duplicados o inconsistentes
- Facilita `GlobalExceptionHandler`
- Facilita JavaFX (manejo de errores por código)
- Mejora trazabilidad entre docs (05/07/09/10) y código

### 8.2 Estructura recomendada de código
`CATEGORIA-MODULO-NN-SLUG`

Ejemplos:
- `VR-EST-01-CAMPO_OBLIGATORIO`
- `RN-SEC-03-CUPO_AGOTADO`
- `AUTH-01-CREDENCIALES_INVALIDAS`
- `API-04-RECURSO_NO_ENCONTRADO`
- `SYS-REP-02-FALLO_PROCESAMIENTO`

> Si prefieres mantener `AUTH-01-...`, `API-04-...` sin módulo intermedio por ahora, también es válido. Lo importante es la consistencia.

### 8.3 Catálogo base sugerido (resumen)
#### AUTH
- `AUTH-01-CREDENCIALES_INVALIDAS`
- `AUTH-02-TOKEN_INVALIDO`
- `AUTH-03-TOKEN_EXPIRADO`
- `AUTH-04-SIN_PERMISOS`
- `AUTH-05-USUARIO_INACTIVO`

#### API
- `API-01-REQUEST_MALFORMADO`
- `API-02-METODO_NO_PERMITIDO`
- `API-03-TIPO_CONTENIDO_NO_SOPORTADO`
- `API-04-RECURSO_NO_ENCONTRADO`
- `API-99-ENDPOINT_EN_CONSTRUCCION`

#### VR / RN (por módulo)
- Estudiante (`EST`)
- Sección (`SEC`)
- Clase (`CLA`)
- Calificación (`CAL`)
- Reportes (`REP`)

#### SYS
- `SYS-01-ERROR_INTERNO`
- `SYS-REP-01-ERROR_WORKER`
- `SYS-REP-02-FALLO_PROCESAMIENTO`

---

## 9. Mapeo JPA ↔ PostgreSQL oficial (`V2_3FN.sql`)

> Objetivo: evitar desalineaciones silenciosas entre entidades JPA y la BD oficial “escrita en piedra”.

### 9.1 Reglas generales de mapeo
- Respetar nombres de tablas/columnas oficiales (usar `@Table`, `@Column` explícitos si hace falta)
- Respetar nullability (`nullable = false` donde corresponda)
- Respetar unicidad e índices (aunque los índices vivan en SQL, documentarlos y usarlos en consultas)
- Usar `Long` para PKs (`BIGINT`)
- Usar `LocalDate`, `LocalTime`
- Estados como `String` + validación (V1), o enum con converter si ya lo controlas bien
- Evitar generación automática de schema que contradiga el SQL oficial

### 9.2 Fuente de verdad
La fuente de verdad del esquema físico en esta fase es:
- `V2_3FN.sql`

JPA debe **adaptarse** al SQL, no al revés.

### 9.3 Mapa resumido (tablas del núcleo)
#### `usuario_sistema_administrativo`
- PK `pk_id` → `Long id`
- `nombre_login`
- `password_hash`
- `rol`
- `estado`

#### `representante_legal`
- PK `pk_id`
- `nombres`, `apellidos`, `telefono`, `correo_electronico`

#### `seccion`
- PK `pk_id`
- `grado`, `paralelo`, `cupo_maximo`, `anio_lectivo`, `estado`
- unique `(anio_lectivo, grado, paralelo)`

#### `docente`
- PK `pk_id`
- `nombres`, `apellidos`, `telefono`, `correo_electronico`, `estado`

#### `asignatura`
- PK `pk_id`
- `nombre`, `area`, `descripcion`, `grado`, `estado`
- unique `(nombre, grado)`

#### `estudiante`
- PK `pk_id`
- `nombres`, `apellidos`, `fecha_nacimiento`, `estado`
- FK `representante_legal_id` (obligatoria)
- FK `seccion_id` (nullable, asignación vigente)

#### `clase`
- PK `pk_id`
- `dia_semana`, `hora_inicio`, `hora_fin`, `estado`
- FK `seccion_id`, `asignatura_id`, `docente_id` (nullable)
- unique `(seccion_id, asignatura_id, dia_semana, hora_inicio, hora_fin)`

#### `calificacion`
- PK `pk_id`
- `numero_parcial`, `nota`, `fecha_registro`, `observacion`
- FK `estudiante_id`, `clase_id`
- unique `(estudiante_id, clase_id, numero_parcial)`

### 9.4 Módulo de reportes (DB queue)
La tabla de cola de reportes es **complementaria** al núcleo oficial 3FN y se diseña aparte (doc 10). No reemplaza ni contradice el diseño académico-base.

### 9.5 Checklist de mapeo JPA (antes de programar en masa)
- [ ] Nombres de tablas correctos
- [ ] Nombres de columnas correctos
- [ ] Nullability correcta
- [ ] FK opcional/obligatoria correcta
- [ ] Unique constraints relevantes documentadas
- [ ] Tipos `LocalDate` / `LocalTime` correctos
- [ ] No existe entidad `Matricula` persistente (salvo rediseño formal)

---

## 10. ¿Qué es “payload” de reporte? (aclaración puntual)

**Payload** = el **contenido útil** del resultado de un reporte.

Ejemplo (conceptual):
- Un reporte de “Listado de estudiantes por sección” puede devolver:
  - metadatos (`seccion`, `anioLectivo`, `generadoEn`)
  - filas (`estudiantes[]`)
  - resumen (`totalEstudiantes`)

Todo eso es el **payload del reporte** (el JSON que el backend prepara y el cliente JavaFX usa para renderizar/exportar).

### 10.1 Por qué conviene diseñarlo
- Evita romper el contrato con JavaFX después
- Permite escribir processors y mappers con forma clara
- Hace Swagger / pruebas más consistentes

### 10.2 Estructura base sugerida (común)
Cada `...PayloadDto` puede tener, cuando aplique:
- `metadata` (info contextual)
- `items` (filas)
- `summary` (totales/resúmenes)

> No todos los reportes tienen que usar exactamente la misma forma, pero conviene mantener una línea parecida.

### 10.3 Ejemplo conceptual (sin casarlo todavía al código)
`ListadoEstudiantesPorSeccionPayloadDto`
- `seccion` (resumen)
- `anioLectivo`
- `generatedAt`
- `items[]` (filas de estudiantes)
- `summary.totalEstudiantes`

---

## 11. Claves mínimas para `messages.properties` (V1)

> Sí conviene definir una base. No hace falta diseñar 300 claves ahora.

### 11.1 Objetivo
Centralizar mensajes públicos reutilizables (errores/validaciones/negocio) para:
- consistencia
- mantenimiento
- futura i18n

### 11.2 Convención sugerida
- `error.auth.*`
- `error.api.*`
- `error.vr.<módulo>.*`
- `error.rn.<módulo>.*`
- `error.sys.*`
- `validation.*` (si separas mensajes de Bean Validation)

### 11.3 Claves base sugeridas (ejemplo mínimo)
#### Auth
- `error.auth.invalid_credentials`
- `error.auth.token_invalid`
- `error.auth.token_expired`
- `error.auth.forbidden`
- `error.auth.user_inactive`

#### API
- `error.api.not_found`
- `error.api.method_not_allowed`
- `error.api.unsupported_media_type`
- `error.api.endpoint_not_implemented`

#### Validación/paginación
- `validation.page.invalid`
- `validation.size.invalid`
- `validation.sort.field_not_allowed`
- `validation.sort.direction_invalid`

#### Negocio (ejemplos)
- `error.rn.seccion.cupo_agotado`
- `error.rn.clase.horario_invalido`
- `error.rn.calificacion.duplicada_parcial`
- `error.rn.reporte.resultado_no_listo`

### 11.4 Regla de uso
- El `errorCode` (ej. `RN-SEC-03-CUPO_AGOTADO`) es el identificador técnico/funcional.
- `messages.properties` aporta el **mensaje público**.
- No mezclar stack traces ni detalles internos en mensajes públicos.

---

## 12. Qué se difiere explícitamente (para no desviarse)

### 12.1 Migraciones SQL
Se difieren para una fase posterior. En esta etapa:
- La BD oficial `V2_3FN.sql` es suficiente como referencia física.
- Si luego se incorpora Flyway/Liquibase, se diseña como documento separado (ej. doc 14 o anexo técnico).

### 12.2 Rediseño de matrícula histórica
Se difiere. Requeriría:
- nuevo diseño de BD
- nuevas reglas de negocio
- cambios de endpoints y contratos
- nueva versión oficial del esquema

---

## 13. Orden recomendado de cierre (antes de implementar mucho código)

1. **Cerrar matriz endpoint → rol** (versión V1 operativa)
2. **Cerrar whitelists** por endpoint de listado
3. **Cerrar catálogo mínimo de DTOs** (nombres + campos)
4. **Cerrar catálogo base de errores** por módulo (`VR/RN/AUTH/API/SYS`)
5. **Cerrar mapeo JPA ↔ BD oficial** (checklist y nombres exactos)
6. **Cerrar payloads de reportes** de 1–2 tipos iniciales
7. **Crear base de `messages.properties`** (claves mínimas)

> Con eso ya se puede entrar a implementación con mucha menos improvisación.

---

## 14. Resultado esperado después de este documento

Al cerrar este documento, el backend V1 queda listo para empezar implementación con diseño obediente a:
- dominio de negocio
- BD oficial PostgreSQL (3FN)
- arquitectura backend V1
- contrato API estándar
- seguridad/documentación/despliegue mínimo
- módulo de reportes con DB queue

Sin forzar patrones “porque sí” y sin inventar entidades no soportadas por el diseño actual.

