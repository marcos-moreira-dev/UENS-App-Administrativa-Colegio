# 10_backend_v1_reporte_solicitudes_cola_simple_db_queue

- **Versión:** 0.2
- **Estado:** En revisión (reconstruido y alineado con `05`, `06`, `08`, `09`)
- **Ámbito:** Backend V1 (Spring Boot + Java 21 + ORM + DTOs + mappers manuales)
- **Propósito específico:** Diseñar una **cola simple de solicitudes de reporte** persistida en PostgreSQL (DB queue) para desacoplar la solicitud HTTP del procesamiento del reporte.
- **Depende de:**
  - `00_backend_v1_indice_y_mapa_documental.md`
  - `01_backend_v1_vision_y_alcance.md`
  - `02_backend_v1_arquitectura_general.md`
  - `03_backend_v1_convenciones_y_estandares_codigo.md`
  - `04_backend_v1_modelado_aplicacion_y_modulos.md`
  - `05_backend_v1_diseno_api_contrato_respuestas_y_errores.md`
  - `06_backend_v1_api_endpoints_y_casos_de_uso.md`
  - `07_backend_v1_validaciones_reglas_negocio_y_excepciones.md` *(si existe catálogo final de códigos; no bloqueante)*
  - `08_backend_v1_paginacion_filtros_ordenamiento_y_consultas.md`
  - `09_backend_v1_seguridad_documentacion_y_despliegue_minimo.md`
- **Referencias de negocio / datos:**
  - `01_levantamiento_informacion_negocio.md`
  - `02_levantamiento_requerimientos.md`
  - `03_modelo_conceptual_dominio.md`
  - `04_reglas_negocio_y_supuestos.md`
  - `05_glosario_alcance_y_limites.md`
  - `V2_3FN.sql`

---

## 1. Propósito del documento

Este documento define la estrategia V1 para incorporar procesamiento asíncrono de reportes usando **la propia base de datos PostgreSQL** como cola persistente.

La meta no es construir una plataforma enterprise de mensajería, sino practicar un patrón muy útil y realista:

- el cliente **solicita** un reporte,
- el backend **encola** la solicitud,
- un worker interno **procesa** la solicitud después,
- el cliente consulta **estado** y obtiene el **resultado preparado**.

Esto te permite practicar desde ya:

- modelado de estados,
- polling,
- reintentos,
- control de concurrencia,
- idempotencia pragmática,
- y diseño de APIs desacopladas del frontend.

✅ El objetivo es aprendizaje profesional + utilidad real, sin sobreingeniería.

---

## 2. Problema que resuelve

## 2.1. Problema práctico

Algunos reportes pueden tardar más que un CRUD normal:

- si el controller genera todo en la misma petición HTTP, la UI se congela,
- aumenta el riesgo de timeout,
- y se mezcla una operación de comando (`solicitar`) con una tarea pesada (`procesar`).

## 2.2. Solución V1

Separar el flujo en dos pasos:

1. **Solicitar reporte** (rápido) → crea solicitud en cola (`PENDIENTE`).
2. **Procesar reporte** (asíncrono) → worker interno toma la tarea y genera resultado.

Luego el cliente consulta estado por polling.

✅ Esto mejora UX y simplifica la arquitectura.

---

## 3. ¿Por qué DB queue en V1 y no RabbitMQ/Kafka?

Porque en V1 quieres:

- practicar arquitectura asíncrona,
- evitar infraestructura adicional,
- reutilizar PostgreSQL que ya tienes,
- mantener operación simple (menos puntos de falla),
- aprender transacciones y concurrencia de forma directa.

### Decisión V1
✅ Usar **cola simple en BD (DB queue)**.

### Qué se deja para futuro
- RabbitMQ / Kafka / Redis Streams
- workers separados por servicio
- scheduler distribuido real

---

## 4. Casos de uso cubiertos por este módulo (V1)

## 4.1. CU-REP-01 — Solicitar preparación de reporte

Un usuario autenticado solicita un reporte (ej. listado, resumen, calificaciones).

El backend:
- valida request,
- crea registro en cola,
- devuelve `requestId`, estado inicial y metadatos.

## 4.2. CU-REP-02 — Consultar estado de solicitud

El cliente consulta periódicamente el estado:
- `PENDIENTE`
- `EN_PROCESO`
- `COMPLETADA`
- `ERROR`
- `CANCELADA` *(opcional futuro)*

## 4.3. CU-REP-03 — Obtener resultado preparado

Si la solicitud ya está `COMPLETADA`, el cliente obtiene:
- payload estructurado,
- metadata del archivo generado,
- resumen de ejecución.

## 4.4. CU-REP-04 — Listar solicitudes (administración / historial)

Permite tabla paginada de solicitudes con filtros, ordenamiento y búsqueda (`08`).

## 4.5. CU-REP-05 — Reintento de solicitudes fallidas (opcional V1.1)

Puede ser:
- automático (worker) con `maxAttempts`, o
- manual (endpoint admin) como mejora.

---

## 5. Estrategia funcional V1 alineada con el estado real del backend

La implementación V1 deja al backend como responsable de **generar el archivo final del reporte** y a JavaFX como cliente que solicita, monitorea y descarga el resultado.

## 5.1. Estrategia V1 recomendada

El backend usa la cola para **preparar datos de reporte, construir un modelo documental y materializar el archivo final**.

### Backend (asíncrono)
- valida parámetros
- calcula dataset / resumen
- normaliza datos
- guarda `resultadoPayload` JSON
- marca `COMPLETADA`

### JavaFX (cliente)
- consulta estado
- obtiene metadata y resultado
- descarga el archivo generado por backend
- puede conservar una copia local para trabajo offline

✅ Esta división te permite practicar backend profesional sin acoplar el contrato a una pantalla concreta.

## 5.2. Estrategia futura (no V1 obligatoria)

Como evolución futura se puede mover la salida a almacenamiento externo o enlaces firmados, pero no hace falta para V1.

---

## 6. Principios de diseño de la cola (V1)

## 6.1. Persistente y auditable

Cada solicitud vive como una fila con:
- tipo,
- estado,
- parámetros,
- timestamps,
- intentos,
- errores,
- resultado (si aplica).

✅ Fácil de inspeccionar con SQL/pgAdmin.

## 6.2. Desacoplamiento solicitud/procesamiento

El endpoint HTTP **no procesa** la tarea pesada.

Solo:
- valida,
- encola,
- responde.

El worker procesa después.

## 6.3. Estado explícito

Nada de inferir estado por timestamps “raros”.

✅ El estado se modela con enum y transiciones válidas.

## 6.4. Idempotencia pragmática

Debe minimizar duplicados accidentales (doble clic / reintento de red), sin diseñar un sistema complejo desde día 1.

## 6.5. Concurrencia segura

Si el worker corre en más de un hilo/instancia, dos workers **no** deben procesar la misma solicitud al mismo tiempo.

✅ Esto se resuelve con “claim” transaccional (ver sección 14).

---

## 7. Modelo conceptual de la cola de reportes

## 7.1. Entidad principal conceptual

### `ReporteSolicitudQueue`

Representa una solicitud encolada para preparación de reporte.

### Campos conceptuales recomendados (V1)
- `id` (PK)
- `tipoReporte` (enum)
- `estado` (enum)
- `payloadParametros` (JSON/JSONB)
- `requestedByUserId`
- `requestedByUsername` *(opcional útil para auditoría ligera)*
- `requestedAt`
- `scheduledAt` *(útil para reintentos/backoff)*
- `startedAt`
- `finishedAt`
- `attemptCount`
- `maxAttempts`
- `lastErrorCode`
- `lastErrorMessage`
- `resultadoPayload` (JSON/JSONB, opcional según estrategia A)
- `resultadoResumen` (texto corto opcional)
- `idempotencyKey` *(opcional recomendado)*
- `correlationId` *(opcional útil para logs)*
- `version` *(optimistic locking, opcional)*
- `createdAt` / `updatedAt` *(si manejas auditoría base)*

---

## 7.2. Tipos de reporte (enum) — catálogo V1

Usar enum explícito, por ejemplo:

- `LISTADO_ESTUDIANTES_POR_SECCION`
- `RESUMEN_SECCION`
- `CALIFICACIONES_POR_SECCION_Y_PARCIAL`
- `CALIFICACIONES_POR_ESTUDIANTE`

### Decisión práctica V1
✅ Implementar 1–2 tipos reales primero y dejar los demás como roadmap o `501`.

---

## 7.3. Estados de la solicitud (enum)

Estados mínimos recomendados V1:

- `PENDIENTE` → creada y esperando procesamiento
- `EN_PROCESO` → worker la tomó
- `COMPLETADA` → resultado listo
- `ERROR` → falló y agotó reintentos o error no reintentable
- `CANCELADA` *(opcional futuro)*

### Opcionales (si quieres granularidad extra)
- `REINTENTANDO`
- `EXPIRADA`

✅ Para V1, el set mínimo es suficiente.

---

## 8. Máquina de estados (transiciones válidas)

## 8.1. Transiciones principales válidas

- `PENDIENTE -> EN_PROCESO`
- `EN_PROCESO -> COMPLETADA`
- `EN_PROCESO -> ERROR`
- `EN_PROCESO -> PENDIENTE` *(si habrá reintento con backoff; reprogramación)*
- `PENDIENTE -> CANCELADA` *(solo si implementas cancelación futura)*

## 8.2. Transiciones inválidas (ejemplos)

- `COMPLETADA -> EN_PROCESO`
- `ERROR -> EN_PROCESO` *(sin reencolar explícitamente)*
- `COMPLETADA -> PENDIENTE`

✅ Las transiciones deben ser explícitas en métodos de dominio/servicio, no por setters libres.

## 8.3. Regla de timestamps por estado

- Al crear `PENDIENTE`: setear `requestedAt`
- Al pasar a `EN_PROCESO`: setear `startedAt`
- Al pasar a `COMPLETADA`/`ERROR`: setear `finishedAt`
- En reintento: actualizar `scheduledAt` y `attemptCount`

---

## 9. Diseño de endpoints del módulo reportes/solicitudes (V1)

> Alineado con `06`, `08` y `09`.

## 9.1. `POST /api/v1/reportes/solicitudes`

### Propósito
Crear una solicitud de reporte en cola.

### Request DTO conceptual
- `tipoReporte`
- `parametros` (objeto/JSON según tipo)
- `idempotencyKey` *(opcional recomendado)*

### Response (201) — `ApiResponse<ReporteSolicitudCreadaResponseDto>`
Campos sugeridos:
- `id`
- `estado` (`PENDIENTE`)
- `tipoReporte`
- `requestedAt`
- `pollingHintSeconds` *(opcional útil para UI)*
- `links` o referencias de endpoints de consulta *(opcional)*

### Errores comunes
- `400` `VR-*` (parametros inválidos)
- `401` `AUTH-*`
- `403` `AUTH-*` sin permisos
- `409` `RN-*` o `API-*` (duplicado/idempotencia, si aplica)
- `501` `API-*` si tipo de reporte aún no implementado

---

## 9.2. `GET /api/v1/reportes/solicitudes/{id}`

### Propósito
Consultar estado + metadatos de una solicitud específica.

### Response — `ApiResponse<ReporteSolicitudDetalleResponseDto>`
Campos sugeridos:
- `id`
- `tipoReporte`
- `estado`
- `requestedAt`
- `startedAt`
- `finishedAt`
- `attemptCount`
- `maxAttempts`
- `lastErrorCode`
- `lastErrorMessagePublica` *(mensaje controlado)*
- `requestedByUserId` *(si aplica y según permisos)*
- `resultadoDisponible` (bool)

### Errores comunes
- `404` si no existe
- `403` si la política de ownership/restricción aplica

---

## 9.3. `GET /api/v1/reportes/solicitudes/{id}/resultado`

### Propósito
Obtener el resultado preparado cuando la solicitud está `COMPLETADA`.

### Respuesta (200)
`ApiResponse<ReporteSolicitudResultadoDto>`

Campos sugeridos:
- `requestId`
- `tipoReporte`
- `generatedAt`
- `payload` (JSON estructurado según tipo)
- `archivo` (nombre, formato, mimeType, rutaRelativa, tamanoBytes, generadoEn)
- `summary` (opcional)

### Casos especiales recomendados
- Si aún no está lista: `409` o `425` *(prácticamente mejor `409` en V1 por simplicidad)*
  - código: `RN-REP-RESULTADO_NO_LISTO`

✅ Recomendación V1: usar `409` con mensaje claro y consistente.

---

## 9.4. `GET /api/v1/reportes/solicitudes`

### Propósito
Listado paginado de solicitudes (historial/administración).

Alineado con `08`:
- `page`, `size`, `sort`, `q`
- filtros (`estado`, `tipoReporte`, `fechaDesde`, `fechaHasta`, etc.)

### Response
`ApiResponse<PageResponseDto<ReporteSolicitudListItemDto>>`

---

## 9.5. Endpoints opcionales V1.1 (no obligatorios)

- `POST /api/v1/reportes/solicitudes/{id}/reintentar`
- `POST /api/v1/reportes/solicitudes/{id}/cancelar`

Si no se implementan, se pueden dejar documentados como roadmap o `501`.

---

## 10. Seguridad del módulo (alineado con `09`)

## 10.1. Autenticación

✅ Todos los endpoints de solicitudes de reporte deben requerir JWT, excepto que explícitamente definas otra cosa (no recomendado).

## 10.2. Autorización por rol (propuesta V1)

Ejemplo razonable:
- `POST /reportes/solicitudes` → `ADMIN`, `SECRETARIA`
- `GET /reportes/solicitudes/{id}` → `ADMIN`, `SECRETARIA` (+ ownership opcional)
- `GET /reportes/solicitudes` → `ADMIN`, `SECRETARIA` (listado administrativo)

Ajustar a roles reales del sistema.

## 10.3. Regla de ownership (opcional V1)

Puedes aplicar una de estas políticas:

### Política A (simple V1)
Cualquier rol autorizado puede ver todas las solicitudes.

### Política B (más estricta)
Un usuario solo ve sus solicitudes, salvo `ADMIN`.

✅ Recomendación V1: **Política A** si quieres avanzar más rápido. La B queda como mejora.

---

## 11. Contratos de request/response y DTOs (nivel conceptual)

## 11.1. DTOs sugeridos

### Request
- `CrearReporteSolicitudRequestDto`
  - `tipoReporte`
  - `parametros`
  - `idempotencyKey` *(opcional)*

### Response
- `ReporteSolicitudCreadaResponseDto`
- `ReporteSolicitudDetalleResponseDto`
- `ReporteSolicitudResultadoDto`
- `ReporteSolicitudListItemDto`

## 11.2. Mappers manuales (tu preferencia)

✅ Mantener mappers manuales para entender bien el flujo.

### Recomendación
- mapper de entidad -> DTO detalle
- mapper de entidad -> DTO listado
- mapper de payload interno -> DTO resultado

Evitar que el controller acceda directo a entidades JPA.

---

## 12. Idempotencia y prevención de duplicados (V1 pragmática)

## 12.1. Problema real

Si el usuario hace doble clic o la red reintenta el `POST`, puedes crear dos solicitudes iguales.

## 12.2. Estrategias posibles

### A) Sin idempotencia (muy simple, menos robusto)
- Aceptar duplicados
- Malo para práctica seria

### B) Idempotency key explícita (recomendada)
- El cliente envía `idempotencyKey`
- El backend evita duplicar solicitud equivalente reciente

### C) Deducción por fingerprint de payload (más compleja)
- Hash de parámetros + usuario + tipo
- Útil, pero más trabajo

## 12.3. Recomendación V1

✅ Implementar **idempotency key opcional**.

Si llega una key repetida para el mismo usuario/tipo dentro de una ventana razonable:
- devolver solicitud existente (200/201 según política)
- o `409` con referencia a solicitud existente

### Política simple sugerida
- devolver `200` con la solicitud ya creada (más amigable para cliente)

---

## 13. Diseño del worker/poller interno (Spring Boot)

## 13.1. Objetivo

Un componente interno del backend revisa periódicamente la tabla y procesa tareas pendientes.

## 13.2. Tecnología V1 recomendada

✅ `@Scheduled` + servicio de aplicación + repositorio custom.

No necesitas colas externas ni un microservicio aparte para empezar.

## 13.3. Componentes sugeridos

- `ReporteSolicitudWorkerScheduler` (trigger periódico)
- `ReporteSolicitudQueueProcessor` (orquesta ciclo de procesamiento)
- `ReporteSolicitudClaimRepository` / repo custom (claim seguro)
- `ReporteDataPreparationService` (lógica por tipo de reporte)
- `ReporteSolicitudStateService` (transiciones de estado)

### Nota
Puedes combinar algunos componentes en V1 para no fragmentar demasiado, pero mantén separadas estas responsabilidades conceptuales.

---

## 13.4. Flujo general del worker (alto nivel)

1. Scheduler se ejecuta cada `N` segundos.
2. Intenta **claim** de una o varias solicitudes elegibles.
3. Marca solicitud como `EN_PROCESO` (transaccionalmente).
4. Ejecuta preparación de datos.
5. Guarda `resultadoPayload`.
6. Marca `COMPLETADA`.
7. Si falla:
   - incrementa intento,
   - decide reintento o `ERROR`.

✅ El scheduler dispara; el processor decide.

---

## 14. Concurrencia y claim seguro (muy importante)

## 14.1. Problema

Si hay dos workers/hilos y ambos leen la misma fila `PENDIENTE`, podrían procesarla dos veces.

## 14.2. Objetivo

Garantizar que una solicitud sea “tomada” por un solo worker a la vez.

## 14.3. Estrategias posibles en DB queue

- optimistic locking puro (`version`)  
- actualización atómica con condición por estado  
- `SELECT ... FOR UPDATE SKIP LOCKED` (PostgreSQL)  
- marca de claim con `workerId` + timestamps (más complejo)

## 14.4. Recomendación V1 concreta (PostgreSQL)

✅ Usar **claim transaccional con `FOR UPDATE SKIP LOCKED`** o una variante equivalente en repo custom.

### Idea conceptual
- seleccionar una fila elegible (`PENDIENTE`, `scheduledAt <= now()`)
- bloquearla
- cambiar estado a `EN_PROCESO`
- confirmar transacción

Esto evita doble toma y escala razonablemente para V1.

## 14.5. Elegibilidad de solicitud para claim

Una solicitud es elegible si:
- `estado = PENDIENTE`
- `scheduledAt` es `null` o `<= ahora`
- no excedió `maxAttempts` *(si manejas esa regla al reencolar)*

---

## 15. Política de reintentos (V1)

## 15.1. ¿Por qué reintentos?

Algunos fallos son temporales (ej. timeout de consulta, error transitorio de DB, dependencia momentánea).

## 15.2. Reglas simples V1

- `attemptCount` inicia en `0`
- `maxAttempts` por defecto: `3` *(ajustable)*
- cada fallo incrementa `attemptCount`
- si aún hay intentos: reencolar `PENDIENTE` con `scheduledAt` futuro
- si se agotaron: `ERROR`

## 15.3. Backoff simple recomendado

✅ Backoff incremental simple:
- intento 1: +10 s
- intento 2: +30 s
- intento 3: +60 s

No hace falta exponencial sofisticado en V1.

## 15.4. Qué errores reintentar (guía)

### Reintentables (ejemplos)
- errores transitorios de infraestructura
- timeouts momentáneos
- lock contention temporal

### No reintentables (ejemplos)
- parámetros inválidos de solicitud
- tipo de reporte no soportado
- violación de regla de negocio permanente

✅ Los errores de request no deberían llegar al worker si el endpoint valida bien.

---

## 16. Persistencia del resultado (estrategia A — JSON preparado)

## 16.1. Qué guardar

Para V1, guardar:
- `resultadoPayload` (JSON/JSONB)
- metadata del archivo generado
- estado, error e intentos para trazabilidad operativa
- metadatos de generación (timestamps, conteos)

## 16.2. JSONB en PostgreSQL

✅ Recomendado usar `JSONB` si tu mapeo/ORM lo permite cómodamente.

Ventajas:
- flexible por tipo de reporte
- útil para prototipado
- fácil de inspeccionar en pgAdmin

## 16.3. Tamaño de resultado (advertencia práctica)

No conviene guardar payloads gigantes indefinidamente.

### Regla pragmática V1
- reportes moderados: OK en JSONB
- si crece demasiado, pasar a archivo/tabla separada en evolución

## 16.4. Retención / limpieza (opcional V1.1)

Puedes definir política futura de limpieza:
- mantener `COMPLETADA/ERROR` por X días
- job de limpieza nocturno

No es obligatorio en primera implementación.

---

## 17. Diseño de la lógica de preparación de reportes

## 17.1. Evitar `if/else` gigante en controller

El controller no debe decidir cómo preparar cada reporte.

✅ Esa lógica debe estar en capa de aplicación/servicio de procesamiento.

## 17.2. Estrategia de processors por tipo (sin exagerar)

Puedes usar una interfaz simple, por ejemplo conceptual:
- `ReporteDataProcessor`

Implementaciones:
- `ListadoEstudiantesPorSeccionProcessor`
- `ResumenSeccionProcessor`

### Recomendación V1
No crear jerarquías enormes. 1 interfaz + 2–4 implementaciones está perfecto.

## 17.3. Registro/selección de processor

Un selector/factory simple puede resolver el processor según `tipoReporte`.

✅ Flexible sin sobreingeniería.

---

## 18. Validaciones, reglas de negocio y excepciones (alineado con `05/07`)

## 18.1. Validaciones de request (`VR-*`)

Ejemplos:
- `tipoReporte` nulo o inválido
- parámetros obligatorios faltantes
- formatos inválidos (IDs, fechas, rangos)
- combinación de parámetros incompatible

## 18.2. Reglas de negocio (`RN-*`) sugeridas para este módulo

Ejemplos:
- `RN-REP-TIPO_NO_HABILITADO_V1`
- `RN-REP-RESULTADO_NO_LISTO`
- `RN-REP-SOLICITUD_NO_CANCELABLE`
- `RN-REP-REINTENTO_NO_PERMITIDO`

## 18.3. Errores técnicos / API (`SYS-*`, `API-*`, `AUTH-*`)

- `AUTH-*` → autenticación/autorización (`09`)
- `API-04-RECURSO_NO_ENCONTRADO`
- `API-99-ENDPOINT_EN_CONSTRUCCION` *(si dejas algo placeholder)*
- `SYS-*` para fallos internos del worker/infraestructura (sin exponer detalle crudo al cliente)

✅ Todos bajo `ApiErrorResponse` (`05`).

---

## 19. Paginación, filtros y ordenamiento del listado de solicitudes (alineado con `08`)

## 19.1. Endpoint listado

`GET /api/v1/reportes/solicitudes`

## 19.2. Query params comunes

- `page`
- `size`
- `sort`
- `q` *(opcional si aporta valor)*

## 19.3. Filtros específicos sugeridos

- `estado`
- `tipoReporte`
- `requestedByUserId` *(si aplica)*
- `fechaDesde`
- `fechaHasta`

## 19.4. Campos ordenables (whitelist sugerida)

- `id`
- `estado`
- `tipoReporte`
- `requestedAt`
- `startedAt`
- `finishedAt`
- `attemptCount`

## 19.5. Orden por defecto recomendado

- `requestedAt,desc`

✅ Muy útil para ver primero lo más reciente.

---

## 20. Integración con JavaFX (online/offline) — enfoque práctico

## 20.1. Flujo online recomendado (V1)

1. Usuario presiona “Generar reporte”
2. JavaFX llama `POST /reportes/solicitudes`
3. JavaFX recibe `requestId`
4. JavaFX muestra estado “Procesando…”
5. JavaFX hace polling a `GET /reportes/solicitudes/{id}` cada N segundos
6. Si `COMPLETADA`, llama `GET /resultado`
7. JavaFX descarga el archivo y lo presenta al usuario

✅ UX profesional sin bloquear la UI.

## 20.2. Flujo offline (interpretación razonable para tu caso)

Como mencionaste online/offline, una interpretación práctica V1 es:

- **online:** backend prepara dataset con datos actuales
- **offline (cliente):** JavaFX puede conservar el archivo ya descargado para uso local posterior

### Nota importante
La cola DB queue es una capacidad **backend online**. No sustituye una cola offline local del cliente.

✅ Son problemas distintos.

---

## 21. Diseño de base de datos (alternativas de modelado V1)

## 21.1. Opción A — Tabla única (recomendada V1)

Una sola tabla para solicitud + estado + resultado + errores.

### Ventajas
- más simple
- menos joins
- ideal para V1

### Desventaja
- la fila puede crecer si el `resultadoPayload` aumenta mucho

✅ Recomendación V1: **Opción A**.

## 21.2. Opción B — Dos tablas (`solicitud` + `resultado`)

Útil si:
- los resultados crecen mucho,
- quieres separación fuerte de lifecycle.

Se deja para evolución.

## 21.3. Tipos de columnas recomendadas (PostgreSQL)

- enums (o varchar controlado) para estados/tipos
- `jsonb` para payloads
- timestamps (`timestamp` / `timestamptz` según convención global)
- índices en campos de consulta frecuente

---

## 22. Índices recomendados (muy importante para que escale “bien” en V1)

## 22.1. Índices operativos de la cola

Candidatos claros:
- `(estado, scheduled_at)` → claim de pendientes
- `requested_at` → listados recientes
- `tipo_reporte` → filtros
- `requested_by_user_id` → historial por usuario (si aplica)
- `idempotency_key` *(si la implementas, con condición/alcance adecuado)*

## 22.2. Índices para filtros de administración

Según uso real del endpoint de listado:
- `estado`
- `tipo_reporte`
- `requested_at`

✅ Índices deben responder a consultas reales (`08`), no ponerse por costumbre.

---

## 23. Compatibilidad con DDD-lite / encapsulación (tu observación importante)

Tú mencionaste querer evitar setters innecesarios. Ese enfoque encaja muy bien aquí.

## 23.1. Recomendación de diseño de entidad/agregado

En vez de setters libres para todo, usar métodos con intención:

- `marcarEnProceso(...)`
- `marcarCompletada(...)`
- `marcarError(...)`
- `reencolarParaReintento(...)`
- `cancelar(...)` *(si aplica)*

✅ Así controlas transiciones válidas y timestamps.

## 23.2. Compatibilidad con JPA

Sí, se puede hacer con JPA sin pelearte demasiado:
- constructor protegido/privado + factory
- getters públicos
- setters restringidos/protegidos (solo si JPA lo exige)
- lógica de transición encapsulada en métodos de dominio

✅ Muy buena práctica para aprender diseño serio sin exagerar.

---

## 24. Política de errores HTTP de este módulo

## 24.1. `POST /reportes/solicitudes`

- `201` creado correctamente
- `400` validación (`VR-*`)
- `401` no autenticado (`AUTH-*`)
- `403` sin permisos (`AUTH-*`)
- `409` conflicto (idempotencia/duplicado, si eliges esa política)
- `501` tipo de reporte aún no implementado (`API-*` / `RN-*`)
- `500` error interno (`SYS-*`)

## 24.2. `GET /reportes/solicitudes/{id}`

- `200` ok
- `401` / `403`
- `404` no encontrado

## 24.3. `GET /reportes/solicitudes/{id}/resultado`

- `200` resultado disponible
- `401` / `403`
- `404` solicitud no encontrada
- `409` resultado no listo (`RN-REP-RESULTADO_NO_LISTO`)

## 24.4. `GET /reportes/solicitudes`

- `200` listado paginado
- `400` query params inválidos (`VR-*`, ver `08`)
- `401` / `403`

---

## 25. Swagger/OpenAPI para este módulo (alineado con `09`)

## 25.1. Qué documentar sí o sí

- `POST /reportes/solicitudes` (request + respuestas + ejemplos)
- `GET /reportes/solicitudes/{id}`
- `GET /reportes/solicitudes/{id}/resultado`
- `GET /reportes/solicitudes` con paginación/filtros (`08`)
- esquema de seguridad Bearer JWT
- códigos de error relevantes (`400/401/403/404/409/500/501`)

## 25.2. Aclaraciones útiles en descripciones

Documentar explícitamente que:
- la solicitud **encola** trabajo asíncrono
- el `POST` no garantiza resultado inmediato
- el cliente debe consultar estado/resultados

✅ Esto evita malos entendidos con UI/QA.

---

## 26. Observabilidad mínima del módulo (V1)

## 26.1. Logs útiles (sin datos sensibles)

Registrar eventos como:
- solicitud creada (`requestId`, `tipoReporte`, usuario)
- solicitud claimada por worker
- inicio/fin de procesamiento
- error/reintento (código, intento)
- completada (duración, conteo de filas si aplica)

### No loggear
- payloads sensibles completos
- tokens JWT
- datos personales innecesarios

## 26.2. Métricas manuales (opcionales pero útiles)

Aunque no uses Prometheus aún, puedes calcular/registrar:
- pendientes actuales
- en proceso actuales
- errores últimas 24h
- tiempo promedio de procesamiento (simple)

✅ Sirve para aprender operación de backend real.

---

## 27. Implementación incremental recomendada (ruta de trabajo)

## Fase 1 — Modelo + endpoints básicos (rápido)

1. Crear entidad/tabla de solicitudes de reporte
2. Crear enum `tipoReporte` y `estado`
3. Implementar `POST /reportes/solicitudes`
4. Implementar `GET /reportes/solicitudes/{id}`
5. Implementar `GET /reportes/solicitudes` paginado
6. Dejar `GET /resultado` como placeholder (`501`) si aún no está listo

✅ Ya prácticas API + estados + persistencia.

## Fase 2 — Worker mínimo funcional

1. Agregar `@Scheduled`
2. Implementar claim seguro de una solicitud
3. Procesar 1 tipo de reporte real
4. Guardar `resultadoPayload`
5. Implementar `GET /resultado`

✅ Ya tienes flujo asíncrono completo.

## Fase 3 — Robustez básica

1. Reintentos simples (`maxAttempts`)
2. Backoff con `scheduledAt`
3. Mejores logs
4. Manejo de errores más fino (`RN/SYS`)

## Fase 4 — Mejoras opcionales

1. Idempotency key
2. Cancelación manual
3. Reintento manual
4. TTL/limpieza
5. Más tipos de reporte

---

## 28. Riesgos comunes y cómo evitarlos

## 28.1. Procesar reporte dentro del controller

❌ Convierte el endpoint en tarea pesada y frágil.

✅ El controller solo encola.

## 28.2. No controlar concurrencia en el claim

❌ Puede duplicar procesamiento.

✅ Claim transaccional con `SKIP LOCKED` (o equivalente).

## 28.3. Mezclar errores técnicos crudos con respuesta pública

❌ Mala seguridad/UX.

✅ Guardar detalle técnico interno y devolver mensaje público controlado.

## 28.4. Crear una arquitectura de processors exagerada desde el inicio

❌ Te desgasta y frena.

✅ 1 interfaz + pocas implementaciones reales.

## 28.5. Confundir “cola backend” con “offline del cliente”

❌ Son problemas distintos.

✅ La cola aquí resuelve procesamiento asíncrono del backend.

---

## 29. Decisiones fijadas por este documento (V1)

1. ✅ Se adopta **DB queue en PostgreSQL** para solicitudes de reportes.
2. ✅ El `POST /reportes/solicitudes` solo **encola**, no procesa el reporte completo.
3. ✅ El backend V1 prepara **datos del reporte** y genera el archivo final en `PDF`, `DOCX` o `XLSX`.
4. ✅ Estados mínimos: `PENDIENTE`, `EN_PROCESO`, `COMPLETADA`, `ERROR` (`CANCELADA` opcional futuro).
5. ✅ Se implementará worker interno con `@Scheduled` en V1.
6. ✅ Se requiere claim seguro para evitar doble procesamiento.
7. ✅ Se adopta listado paginado de solicitudes alineado con `08`.
8. ✅ Se mantiene contrato estándar `ApiResponse` / `ApiErrorResponse` (`05`).
9. ✅ Seguridad JWT/roles según `09`.
10. ✅ Se favorece encapsulación (métodos de transición) sobre setters innecesarios.

---

## 30. Checklist de implementación derivado (V1)

## 30.1. Modelo y persistencia
- [ ] Definir enum `TipoReporte`
- [ ] Definir enum `EstadoReporteSolicitud`
- [ ] Crear entidad JPA de solicitud de reporte
- [ ] Definir columnas para payload parámetros/resultado (JSON/JSONB)
- [ ] Agregar índices básicos (`estado + scheduledAt`, `requestedAt`, etc.)

## 30.2. API
- [ ] `POST /api/v1/reportes/solicitudes`
- [ ] `GET /api/v1/reportes/solicitudes/{id}`
- [ ] `GET /api/v1/reportes/solicitudes`
- [ ] `GET /api/v1/reportes/solicitudes/{id}/resultado` (o placeholder `501` temporal)
- [ ] DTOs + mappers manuales
- [ ] Errores `VR/RN/API/AUTH/SYS` bajo contrato estándar

## 30.3. Seguridad
- [ ] Proteger endpoints con JWT
- [ ] Definir roles autorizados
- [ ] Evaluar ownership (opcional V1)

## 30.4. Worker
- [ ] Scheduler `@Scheduled`
- [ ] Claim seguro de solicitudes elegibles
- [ ] Processor de al menos 1 tipo de reporte
- [ ] Transiciones de estado controladas
- [ ] Manejo de errores y `attemptCount`

## 30.5. Integración / documentación
- [ ] Swagger documentado con flujo asíncrono
- [ ] Ejemplos de requests/responses
- [ ] JavaFX: polling simple sobre `GET /{id}` y descarga posterior del archivo

---

## 31. Relación con otros documentos del backend V1

## 31.1. Relación con `05`

Este módulo hereda el contrato estándar de respuestas y errores.

## 31.2. Relación con `06`

Aquí se aterriza el módulo de reportes/solicitudes como caso de uso de orquestación (no solo CRUD).

## 31.3. Relación con `08`

El listado de solicitudes debe respetar paginación, filtros y ordenamiento definidos allí.

## 31.4. Relación con `09`

Autenticación JWT, autorización por rol, Swagger y despliegue mínimo aplican directamente.

---

## 32. Cierre del documento

Con este diseño ya tienes una **práctica de backend bastante profesional** sin salirte de una V1 razonable:

- API desacoplada del frontend,
- procesamiento asíncrono real,
- estados y polling,
- reintentos básicos,
- control de concurrencia,
- y generación de archivos de reporte compatible con un cliente JavaFX online/offline.

✅ Este módulo es excelente para practicar backend “de verdad” porque combina CRUD, orquestación, diseño de contratos, operación y arquitectura.

