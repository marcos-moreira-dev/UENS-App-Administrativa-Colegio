# 16_desktop_flujo_auditoria_admin

- **Proyecto:** UENS Desktop (JavaFX)
- **Módulo:** Auditoría operativa (solo ADMIN)
- **Arquitectura:** MVVM + Navigator
- **UI:** Pantalla dedicada (tabla + filtros fuertes) + Drawer para detalle
- **Objetivo:** aterrizar auditoría en términos de **negocio + UI + API**.

---

## 1) Propósito del módulo (negocio → evidencia)
### 1.1 Negocio
La auditoría existe para:
- **trazabilidad** de operaciones críticas,
- **evidencia** para revisión administrativa,
- reconstruir historia ante incidentes,
- y justificar por qué un reporte se generó/falló/reintentó.

No es “vigilancia personal”: es control de proceso.

### 1.2 Técnico
El backend registra eventos en una tabla (`auditoria_evento`) con campos clave:
- módulo, acción, resultado,
- entidad y entidadId,
- actor (login/rol),
- requestId,
- IP,
- fecha.

El reporte de auditoría reutiliza la misma infraestructura asíncrona de reportes.

Fuentes:
- `backend/docs/backend_v_1/17_backend_v_1_auditoria_operativa_y_reporte_admin.md`
- `backend/docs/backend_v_1/10_backend_v_1_reporte_solicitudes_cola_simple_db_queue.md`

---

## 2) Permisos por rol
- **Solo `ADMIN`**:
 - listar eventos
 - filtrar/buscar
 - solicitar reporte de auditoría (async)

- `SECRETARIA`:
 - sin acceso (ni menú ni rutas)

Reglas UI:
- módulo oculto a SECRETARIA.
- Navigator bloquea acceso directo.

---

## 3) API del módulo (endpoints)
Fuente: `backend/docs/api/API_ENDPOINTS.md`

### 3.1 Listado de eventos
- `GET /api/v1/auditoria/eventos` (ADMIN)
- filtros: `q,módulo,accion,resultado,actorLogin,fechaDesde,fechaHasta,page,size,sort`
- El backend también refuerza esta restriccion en la capa application, no solo en la UI.

### 3.2 Solicitar reporte de auditoría (async)
- `POST /api/v1/auditoria/reportes/solicitudes` (ADMIN)
- El backend también refuerza esta restriccion en la capa application.

Esto crea una solicitud de reporte en la cola de reportes.

---

## 4) Pantalla y UX recomendada
### 4.1 AuditoriaView (pantalla dedicada)
Diseño recomendado:

A) **Header**
- Título “Auditoría operativa”
- Descripción breve: “Trazabilidad de acciones del sistema”
- CTA: “Solicitar reporte de auditoría”

B) **FilterBar (filtros fuertes)**
- `q` (búsqueda general)
- `módulo` (ComboBox)
- `accion` (ComboBox o TextField)
- `resultado` (ComboBox)
- `actorLogin` (TextField)
- rango de fechas (`fechaDesde`, `fechaHasta`)
- botones: “Aplicar”, “Limpiar”

C) **Tabla (listado de eventos)**
- TableView paginada
- Columna acciones: “Ver detalle”

D) **Drawer de detalle**
- Muestra evento completo, incluyendo `requestId` y `path` si está disponible.

Regla de densidad:
- Auditoría es un módulo técnico: se permite mayor densidad (más columnas), pero sin sacrificar legibilidad.

---

## 5) Columnas sugeridas para la tabla
Mínimo útil en tabla:
- Fecha (fechaEvento)
- Módulo
- Acción
- Resultado (badge)
- Entidad
- EntidadId
- ActorLogin
- ActorRol
- RequestId (mono, copiable)

Opcional:
- IP origen

Reglas:
- No mostrar “detalle largo” en tabla; usar drawer.
- RequestId en tabla sí es valioso (soporte).

---

## 6) Filtros (cómo se usan en la práctica)
### 6.1 Búsquedas típicas
1. “¿Por qué falló un reporte?”
 - módulo=REPORTE
 - resultado=ERROR
 - rango de fechas

2. “¿Quién cambió el estado de una entidad?”
 - acción relevante (según catálogo)
 - actorLogin
 - entidad + entidadId

3. “Errores recurrentes”
 - resultado=ERROR
 - q con palabra clave

### 6.2 Reglas UX
- Filtros por defecto simples, los demás visibles pero ordenados.
- Botón “Limpiar” restablece page=0.
- Validación UI: fechaDesde <= fechaHasta.

---

## 7) Solicitud de reporte de auditoría (async)
### 7.1 Negocio
El reporte sirve para:
- entregar evidencia,
- archivar eventos,
- revisión interna.

### 7.2 UX
En la vista de auditoría:
- botón “Solicitar reporte” abre modal.

Modal incluye:
- formatoSalida (XLSX/PDF/DOCX)
- fechaDesde/fechaHasta
- filtros opcionales: módulo, acción, resultado, actorLogin
- incluirDetalle (boolean)

Al confirmar:
- se crea solicitud
- se muestra toast “Solicitud creada”
- redirigir o enlazar a módulo Reportes (historial) para seguimiento

### 7.3 API
- `POST /api/v1/auditoria/reportes/solicitudes`

Errores típicos:
- 403 (si rol incorrecto)
- 400/422 (fechas inválidas)
- 500 (requestId)

---

## 8) Detalle del evento (Drawer)
Contenido recomendado:
- Módulo / Acción / Resultado
- Entidad + entidadId
- Actor (login/rol)
- RequestId (copiable)
- IP origen
- Fecha
- Detalle (texto corto)

Regla:
- No mostrar información sensible (contraseñas, hashes completos). El backend ya intenta evitarlo.

---

## 9) Manejo de errores (obligatorio)
Basado en `09_desktop_manejo_errores_toasts_dialogos.md`.

- Error al cargar tabla: banner + reintentar + detalle técnico.
- 401: logout.
- 403: no debería ocurrir si ya está en módulo ADMIN, pero igual mostrar mensaje.
- 500: diálogo o banner con requestId.

---

## 10) MVVM recomendado
### 10.1 ViewModels
- `AuditoriaListViewModel` (tabla + filtros)
- `AuditoriaDetailViewModel` (drawer)
- `AuditoriaReporteFormViewModel` (modal solicitar reporte)

### 10.2 Servicios API
- `AuditoriaApi`
- `ReportesApi` (si se quiere navegar/consultar solicitud creada)

---

## 11) Checklist de implementación
- [ ] Módulo visible solo para ADMIN.
- [ ] Tabla paginada con filtros fuertes.
- [ ] RequestId visible y copiable.
- [ ] Drawer de detalle.
- [ ] Modal para solicitar reporte.
- [ ] Enlace o navegación hacia Reportes para seguimiento.
- [ ] Manejo correcto de 401/403/500.

---

## 12) Próximo documento
- `17_desktop_css_fx_guia_practica.md`

---

## Addendum 2026-03-01: cierre de app y trazabilidad

El desktop ahora distingue entre:

- logout normal desde la UI,
- y cierre forzado mediante el botón `X` de la ventana.

Si hay sesión activa y se usa `X`:

- se muestra un diálogo de confirmación,
- se informa que la sesión será invalidada de forma obligatoria,
- y se intenta `POST /api/v1/auth/logout` en modo best-effort.

Esto no reemplaza la auditoría de negocio, pero agrega trazabilidad valiosa alrededor del acceso y cierre de sesión cuando el backend soporte ese evento.

---

## Addendum 2026-03-04: cómo usar auditoría para aprender soporte real

Este módulo es especialmente bueno para estudiar mantenimiento porque te obliga a pensar como alguien que investiga incidentes.

Si mañana un usuario dice "el sistema falló", la pantalla de auditoría te debería ayudar a reconstruir:

1. quién hizo la operación
2. en qué módulo ocurrió
3. con qué `requestId`
4. qué resultado dejó
5. si luego se generó un reporte asociado

Cuando entiendes auditoría así, ya no la ves como una tabla más. La ves como un puente entre backend, soporte, seguridad y operación diaria.



