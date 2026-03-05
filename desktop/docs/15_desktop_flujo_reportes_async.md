# 15_desktop_flujo_reportes_async

- **Proyecto:** UENS Desktop (JavaFX)
- **Módulo:** Reportes (cola asíncrona)
- **Arquitectura:** MVVM + Navigator
- **UI:** Pantalla dedicada (flujo asíncrono + historial + descarga)
- **Objetivo:** aterrizar reportes en términos de **negocio + UI + API**.

---

## 1) Por qué los reportes son asíncronos (negocio → técnico)
### 1.1 Negocio
En un entorno administrativo real:
- algunos reportes tardan,
- requieren filtros,
- deben quedar registrados (historial),
- y pueden fallar/reintentarse.

Además, el sistema necesita reportes para:
- apoyo administrativo,
- auditorías internas,
- evidencia de operación.

### 1.2 Técnico
El backend implementa reportes como:
- **solicitud** → se encola
- **worker** → procesa en background
- **resultado** → queda persistido

Esto evita:
- bloquear la UI,
- timeouts largos,
- experiencia rota.

Fuentes:
- `backend/docs/backend_v_1/10_backend_v_1_reporte_solicitudes_cola_simple_db_queue.md`
- `backend/docs/backend_v_1/19_backend_v_1_contexto_integracion_y_diseno_frontend.md`

---

## 2) Tipos de reportes soportados (V1)
Fuente: `backend/docs/api/API_ENDPOINTS.md`

Reportes genéricos (para ADMIN y SECRETARIA):
- `LISTADO_ESTUDIANTES_POR_SECCION`
- `CALIFICACIONES_POR_SECCION_Y_PARCIAL`

Reporte especial:
- `AUDITORIA_ADMIN_OPERACIONES` (solo ADMIN, se solicita desde módulo auditoría)

Formatos de salida:
- `XLSX`, `PDF`, `DOCX`

---

## 3) Permisos por rol
- `ADMIN`, `SECRETARIA`:
  - crear solicitud de reporte genérico
  - listar solicitudes
  - ver detalle
  - ver estado/resultado
  - descargar archivo

- Solo `ADMIN`:
  - reintentar solicitud fallida

Regla UI:
- Botón “Reintentar” oculto a SECRETARIA.

---

## 4) Estados del reporte (UX obligatorio)
Estados típicos de la cola:
- `PENDIENTE`
- `EN_PROCESO`
- `COMPLETADA`
- `ERROR`

Regla:
- La UI debe mostrar estados con badge (color + texto).
- No existe “descarga inmediata”.

---

## 5) Pantallas y navegación
### 5.1 Pantalla principal (recomendada)
**ReportesView** (pantalla dedicada)
Dividida en 2 zonas:

A) **Crear solicitud** (formulario)
- tipoReporte
- formatoSalida
- filtros específicos

B) **Historial de solicitudes** (tabla)
- estado
- tipo
- fechaSolicitud
- acciones: ver detalle / refrescar estado / descargar / reintentar (ADMIN)

### 5.2 Drawer de detalle (opcional)
**ReporteSolicitudDrawer**
- muestra:
  - metadata
  - filtros usados
  - estado actual
  - resultado (si existe)

---

## 6) API del módulo (endpoints)
Fuente: `backend/docs/api/API_ENDPOINTS.md`

### 6.1 Crear solicitud
- `POST /api/v1/reportes/solicitudes`

### 6.2 Listado
- `GET /api/v1/reportes/solicitudes`
- filtros: `q,tipoReporte,estado,page,size,sort`
- `ADMIN` ve todas las solicitudes
- `SECRETARIA` solo ve las creadas por su usuario autenticado

### 6.3 Detalle
- `GET /api/v1/reportes/solicitudes/{solicitudId}`

### 6.4 Estado
- `GET /api/v1/reportes/solicitudes/{solicitudId}/estado`

### 6.5 Resultado
- `GET /api/v1/reportes/solicitudes/{solicitudId}/resultado`

### 6.6 Descargar archivo
- `GET /api/v1/reportes/solicitudes/{solicitudId}/archivo`
- Respuesta binaria con `Content-Disposition`.
- El desktop conserva el nombre sugerido por backend, lo sanea y guarda la descarga en `~/Downloads/UENS`.
- Si una `SECRETARIA` intenta abrir una solicitud ajena, el backend puede responder `404` por ownership.

### 6.7 Reintentar (solo ADMIN)
- `POST /api/v1/reportes/solicitudes/{solicitudId}/reintentar`

---

## 7) UX: formulario “Crear reporte”
### 7.1 Campos comunes
- `tipoReporte` (ComboBox)
- `formatoSalida` (ComboBox)
- `fechaDesde`, `fechaHasta` (DatePicker)

### 7.2 Campos por tipo

#### A) LISTADO_ESTUDIANTES_POR_SECCION
- `seccionId` (requerido)

#### B) CALIFICACIONES_POR_SECCION_Y_PARCIAL
- `seccionId` (requerido)
- `numeroParcial` (requerido)

### 7.3 Validaciones UI
- fechaDesde <= fechaHasta
- parcial 1/2
- sección requerida

Regla:
- backend es la fuente de verdad; UI solo previene errores básicos.

### 7.4 UX de selección de sección
- selector con resumen: `grado + paralelo + año`
- evitar IDs.

---

## 8) UX: historial de solicitudes (tabla)
Basado en `10_desktop_patron_tablas_filtros_paginacion.md`.

### 8.1 Columnas sugeridas
- ID
- Tipo reporte
- Formato
- Estado (badge)
- Fecha solicitud
- Fecha fin/proceso (si existe)
- Acciones

### 8.2 Acciones por fila
- Ver detalle
- Refrescar estado
- Descargar (solo si COMPLETADA)
- Reintentar (solo ADMIN y solo si ERROR)

### 8.3 Filtros
- `q`
- `tipoReporte`
- `estado`

---

## 9) Polling (patrón de seguimiento)
### 9.1 Por qué polling
El backend procesa en background.

### 9.2 Estrategia recomendada
- Cuando el usuario crea una solicitud:
  - mostrar de inmediato la fila en historial
  - iniciar seguimiento automático del estado por un tiempo

- Intervalos sugeridos:
  - cada 2–4 segundos mientras esté `PENDIENTE/EN_PROCESO`
  - detener al llegar a `COMPLETADA/ERROR`

- Controles UX:
  - indicador “actualizando…”
  - botón “Refrescar ahora”

Regla:
- no hacer polling infinito si el usuario sale de la pantalla.

---

## 10) Descarga de archivos (binario)
### 10.1 Reglas
- Solo habilitar descarga si estado `COMPLETADA`.
- Al descargar:
  - mostrar loading
  - guardar bytes a disco
  - usar `Content-Disposition` para nombre si existe

### 10.2 Naming de archivo (fallback)
Si no hay nombre:
- `<tipoReporte>_<solicitudId>_<timestamp>.<ext>`

---

## 11) Manejo de errores (casos importantes)
Basado en `09_desktop_manejo_errores_toasts_dialogos.md`.

### 11.1 Crear solicitud
- 400/422: errores de formulario
- 409: conflicto de negocio (filtros inválidos, etc.)
- 403: sin permisos
- 500: mostrar requestId

### 11.2 Worker en ERROR
- Mostrar badge `ERROR`.
- Permitir ver detalle.
- Reintentar solo ADMIN.

### 11.3 Descarga falla
- Mostrar mensaje
- permitir reintentar

---

## 12) MVVM recomendado
### 12.1 ViewModels
- `ReportesViewModel` (pantalla)
- `ReporteSolicitudListViewModel` (tabla)
- `CrearReporteSolicitudFormViewModel` (form)

### 12.2 Servicios API
- `ReportesApi`
- `SeccionesApi` (selector)

---

## 13) Checklist de implementación
- [ ] Formulario crea solicitudes por tipo.
- [ ] Historial paginado con estados.
- [ ] Polling controlado (se detiene).
- [ ] Descarga binaria con nombre correcto.
- [ ] Reintentar solo ADMIN y solo ERROR.
- [ ] Errores muestran requestId en detalle técnico.

---

## 14) Próximo documento
- `16_desktop_flujo_auditoria_admin.md`

---

## 15) Cómo se estudia bien este módulo

Reportes es un muy buen módulo para aprender arquitectura porque te obliga a pensar en varias capas al mismo tiempo:

1. formulario y validación
2. request HTTP
3. procesamiento asíncrono
4. polling
5. descarga binaria
6. ownership y permisos

Si entiendes ese flujo completo, ya estás trabajando mucho más cerca de un sistema real que de un CRUD simple.

### Pregunta guía

Cuando leas este documento, pregúntate siempre:

"¿Qué pasa si el reporte tarda, falla, se reintenta o pertenece a otro usuario?"

Esa pregunta te obliga a pensar como mantenedor, no solo como implementador de pantalla.

