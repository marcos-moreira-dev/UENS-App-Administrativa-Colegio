# 17_backend_v_1_auditoria_operativa_y_reporte_admin

- Versión: 1.0
- Estado: Base implementada en backend y SQL
- Ámbito: Backend V1 (módulos `auditoria` + `reporte`)
- Fecha de corte: 2026-02-27

---

## 1. Propósito

Definir una base profesional de auditoría operativa para el backend UENS, con estos objetivos:

1. Trazabilidad de operaciones críticas en tiempo real.
2. Evidencia técnica para revisión administrativa y académica.
3. Reporteria desacoplada (cola async existente) con acceso exclusivo ADMIN.

---

## 2. Narrativa pedagogica (la "excusa" funcional)

El sistema se usa en un contexto educativo donde:

1. Se forman estudiantes en buenas prácticas digitales.
2. El personal administrativo debe rendir cuentas sobre cambios y procesos.
3. Es necesario demostrar por que un reporte se género, fallo o fue reintentado.

Por eso se implementa una auditoría operativa simple pero realista:

- No es vigilancia personal.
- Es control de proceso y calidad de operación.
- Permite reconstruir la historia de eventos ante dudas, incidencias o evaluaciones internas.

---

## 3. Principios de diseño

1. Bajo acoplamiento: auditoría se registra en su propia tabla (`auditoria_evento`).
2. Tolerancia a falla: si no se puede auditar, no se cae la operación funcional.
3. Evidencia útil: actor, requestId, IP, módulo, acción, resultado y detalle.
4. Seguridad por rol: solo ADMIN consulta eventos y solicita reporte de auditoría.
5. Reuso de infraestructura: el reporte de auditoría usa la misma DB queue de reportes.

---

## 4. Criterios de auditoría (que se registra y como)

## 4.1 Campos mínimos del evento

Cada evento guarda:

- `módulo`: origen funcional (`REPORTE`, `AUDITORIA`, etc.).
- `accion`: acción concreta (`SOLICITUD_CREADA`, `WORKER_SOLICITUD_ERROR`, etc.).
- `entidad` y `entidad_id`: objeto afectado.
- `resultado`: `EXITO | ERROR | INFO | ADVERTENCIA`.
- `detalle`: contexto corto de negocio/técnico.
- `request_id`: correlación entre logs, API y auditoría.
- `ip_origen`: IP de origen (considerando `X-Forwarded-For`).
- `actor_usuario_id`, `actor_login`, `actor_rol`: actor autenticado cuando existe.
- `fecha_evento`: timestamp de persistencia.

## 4.2 Política de resultados

- `EXITO`: operación ejecutada correctamente.
- `ERROR`: operación fallida o exception funcional.
- `INFO`: evento informativo no crítico.
- `ADVERTENCIA`: condicion anomala no bloqueante.

## 4.3 Política de detalle

1. Incluir contexto útil para soporte.
2. Evitar datos sensibles (credenciales, secretos, hash completos).
3. Mantener mensajes cortos y estables para búsqueda/filtros.

---

## 5. Paquete implementado

Se agrega el módulo `auditoria` con capas consistentes al monolito modular:

- `modules/auditoria/api/AuditoriaController`
- `modules/auditoria/api/dto/AuditoriaEventoListItemDto`
- `modules/auditoria/api/dto/CrearAuditoriaReporteRequestDto`
- `modules/auditoria/application/AuditoriaEventService`
- `modules/auditoria/application/AuditoriaQueryService`
- `modules/auditoria/application/AuditoriaReporteService`
- `modules/auditoria/application/mapper/AuditoriaDtoMapper`
- `modules/auditoria/infrastructure/persistence/entity/AuditoriaEventoJpaEntity`
- `modules/auditoria/infrastructure/persistence/repository/AuditoriaEventoJpaRepository`

Integraciones en `reporte`:

- `ReporteSolicitudCommandService`: creacion de solicitud de auditoría (solo ADMIN) + trazas.
- `ReporteSolicitudWorkerService`: traza de exito/error por procesamiento.
- `AuditoriaAdminOperacionesProcessor`: payload del reporte de auditoría.

---

## 6. SQL y modelo fisico

## 6.1 DDL principal

Archivo actualizado:

- `db/V2/docs/Diagramas y query de creación/V2_3FN.sql`

Se incorpora tabla:

- `auditoria_evento`

Y sus índices:

- `idx_auditoria_evento_fecha`
- `idx_auditoria_evento_modulo_accion`
- `idx_auditoria_evento_resultado`
- `idx_auditoria_evento_actor_login`

## 6.2 Reset y seed

Se actualizan scripts para reinicio limpio:

- `db/V2/seeds/99_reset_demo_3FN.sql` (incluye `auditoria_evento` y `reporte_solicitud_queue`)
- `db/V2/seeds/02_seed_demo_3FN.sql` (incluye `auditoria_evento` en `TRUNCATE`)

Resultado: al resetear o reseedear, no quedan residuos de auditoría/reportes anteriores.

---

## 7. Endpoints de auditoría

Base path: `/api/v1/auditoria`

## 7.1 Listado de eventos

- Método: `GET /eventos`
- Rol: `ADMIN`
- Respuesta: `ApiResponse<PageResponseDto<AuditoriaEventoListItemDto>>`
- Filtros: `q, módulo, accion, resultado, actorLogin, fechaDesde, fechaHasta, page, size, sort`

Uso principal:

1. Revisar incidentes.
2. Seguir flujo de reportes.
3. Ver trazabilidad de acciones administrativas.

## 7.2 Solicitud de reporte de auditoría

- Método: `POST /reportes/solicitudes`
- Rol: `ADMIN`
- Request: `CrearAuditoriaReporteRequestDto`
- Response: `ApiResponse<ReporteSolicitudCreadaResponseDto>`
- Tipo de reporte en cola: `AUDITORIA_ADMIN_OPERACIONES`

Validaciones clave:

1. `formatoSalida`: `XLSX|PDF|DOCX`.
2. `resultado` filtrado a catálogo permitido.
3. `fechaDesde <= fechaHasta`.
4. Solo ADMIN puede crear esta solicitud.

---

## 8. Flujo de procesamiento del reporte de auditoría

1. ADMIN solicita reporte por endpoint de auditoría.
2. Se encola solicitud en `reporte_solicitud_queue`.
3. Worker toma la tarea.
4. `AuditoriaAdminOperacionesProcessor` consulta `auditoria_evento` con filtros.
5. Se arma payload con resumen (`totalEventos`, `totalExitos`, `totalErrores`) + `items`.
6. Exportador genera archivo (`xlsx/pdf/docx`) con branding actual.
7. Resultado se guarda y solicitud queda `COMPLETADA` o `ERROR`.

Nota de branding:

- Se mantiene el logo institucional de reportes (`src/main/resources/assets/logo.png`) porque el flujo de export usa la misma infraestructura existente.

---

## 9. Seguridad y autorización

Controles en dos capas:

1. API: `@PreAuthorize("hasRole('ADMIN')")` en endpoints de auditoría.
2. Aplicación: validación adicional en `ReporteSolicitudCommandService#crearSolicitudAuditoria`.

Esto evita bypass accidental aunque cambie configuración de endpoints.

---

## 10. Criterios de desacoplamiento (por que no rompe reportes actuales)

1. Se reutiliza DB queue existente, sin cambiar contrato de endpoints de reportes normales.
2. Se agrega nuevo `processor` por tipo, sin modificar processors previos.
3. El módulo auditoría no depende de frontend ni de formato visual especifico.
4. El reporte de auditoría es un consumidor adicional del pipeline ya estable.

---

## 11. DTOs nuevos

## 11.1 `AuditoriaEventoListItemDto`

DTO de salida para listado paginado de eventos:

- `eventoId`
- `módulo`
- `accion`
- `entidad`
- `entidadId`
- `resultado`
- `actorLogin`
- `actorRol`
- `requestId`
- `ipOrigen`
- `fechaEvento`

## 11.2 `CrearAuditoriaReporteRequestDto`

DTO de entrada para solicitud de reporte:

- `formatoSalida`
- `fechaDesde`
- `fechaHasta`
- `módulo`
- `accion`
- `resultado`
- `actorLogin`
- `incluirDetalle`

---

## 12. Riesgos controlados y decisiones tomadas

1. Riesgo: auditoría no debe bloquear negocio.
 Decision: `AuditoriaEventService` persiste en modo tolerante (captura exception y loguea warn).
2. Riesgo: nulls en filtros del payload de reporte.
 Decision: usar `LinkedHashMap` mutable (evita `Map.of` con null).
3. Riesgo: duplicar privilegios por error.
 Decision: restriccion ADMIN en controlador y en servicio.

---

## 13. Casos de prueba manual recomendados

1. Login como `SECRETARIA` y probar `GET /api/v1/auditoria/eventos` -> `403`.
2. Login como `ADMIN` y probar listado sin filtros -> `200` paginado.
3. Solicitar reporte de auditoría con rango válido -> `201` + estado `PENDIENTE`.
4. Consultar estado de la solicitud en módulo reportes -> transicion a `COMPLETADA`.
5. Descargar archivo y validar que incluye logo institucional.
6. Forzar error de processor y confirmar evento `WORKER_SOLICITUD_ERROR` en auditoría.

---

## 14. Proximos pasos recomendados

1. Exportar reporte de auditoría con plantilla dedicada por formato (tabla + resumen + metadata).
2. Agregar política de retención/archivado de `auditoria_evento` por antiguedad.
3. Incorporar catálogo de acciones auditables para estandarizar semántica.
4. Integrar dashboard de auditoría (KPIs de errores, volumen por módulo, top acciones).


