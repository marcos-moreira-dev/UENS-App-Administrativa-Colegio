# 09_desktop_manejo_errores_toasts_dialogos

- **Proyecto:** UENS Desktop (JavaFX)
- **Arquitectura:** MVVM + Navigator
- **Objetivo:** definir una estrategia **consistente y profesional** de manejo de errores, usando:
  - HTTP status,
  - `errorCode`,
  - `message`,
  - `requestId`.

---

## 1) Propósito
En un sistema administrativo serio, los errores no son “un toast rojo y ya”.

Este documento define:

1. **Qué tipo de feedback** usar según el error:
   - toast,
   - banner,
   - diálogo,
   - error inline por campo.
2. **Cómo interpretar** errores por HTTP status.
3. **Cómo usar `errorCode`** para reglas UI sin inventar lógica.
4. **Cómo exponer `requestId`** de forma útil (soporte/depuración) sin saturar al usuario.
5. Un **modelo de ErrorInfo** y patrones de implementación en MVVM.

---

## 2) Fuente de verdad (backend)
Formato de error estándar (`ApiErrorResponse`):
- `ok: false`
- `errorCode`
- `message`
- `path`
- `timestamp`
- `requestId`

Fuentes de contexto:
- `backend/docs/api/API_ENDPOINTS.md`
- `backend/docs/backend_v_1/07_backend_v_1_validaciones_reglas_negocio_y_excepciones.md`
- `backend/docs/backend_v_1/05_backend_v_1_diseno_api_contrato_respuestas_y_errores.md`

---

## 3) Modelo de error en el frontend (ErrorInfo)
El frontend debe normalizar cualquier fallo (HTTP o no) a un solo modelo:

### 3.1 ErrorInfo (campos recomendados)
- `httpStatus` (int)
- `errorCode` (String, puede ser null si error de red)
- `message` (String)
- `requestId` (String, puede ser null)
- `path` (String, opcional)
- `timestamp` (String/Instant, opcional)
- `category` (enum UI: AUTH, PERMISSION, VALIDATION, CONFLICT, NETWORK, SERVER, UNKNOWN)

### 3.2 ErrorCategory (clasificación UI)
- **AUTH:** 401 (sesión)
- **PERMISSION:** 403
- **VALIDATION:** 400/422
- **CONFLICT:** 409
- **NETWORK:** timeouts/DNS/offline
- **SERVER:** 500+
- **UNKNOWN:** no clasificado

Regla:
- La UI decide el tipo de feedback por `category`.

---

## 4) Canal de feedback (toast vs banner vs diálogo)
### 4.1 Toast (notificación temporal)
Usar para:
- éxito (“Guardado correctamente”).
- info leve.
- warning leve no bloqueante.

Evitar toast para:
- validación por campo,
- errores técnicos graves,
- acciones que requieren decisión.

Duración recomendada:
- 2–4 segundos.

### 4.2 Banner (mensaje fijo en la vista)
Usar para:
- errores de carga de listados (falló fetch de tabla).
- conflictos de negocio en pantallas de flujo.
- errores recuperables con botón “Reintentar”.

Ejemplo:
- “No se pudo cargar estudiantes. Reintentar.”

### 4.3 Diálogo (modal)
Usar para:
- errores críticos de sesión (si necesitas explicar) o acciones destructivas.
- errores de servidor persistentes donde conviene mostrar detalle.
- confirmaciones.

Regla:
- si el usuario debe **tomar una decisión** → diálogo.

### 4.4 Error inline (en formulario)
Usar para:
- 400/422 y reglas de campos.

Regla:
- el campo debe mostrar:
  - borde/estado error,
  - texto corto del error.

---

## 5) Estrategia por HTTP status (tabla de decisión)
### 5.1 401 Unauthorized (AUTH)
**Significado:** token inválido/expirado.

**Acción:**
1. mostrar mensaje breve (“Sesión expirada”).
2. limpiar sesión.
3. navegar a Login.

**Feedback:**
- toast o banner breve (no diálogo largo).

### 5.2 403 Forbidden (PERMISSION)
**Significado:** sesión válida, sin permiso.

**Acción:**
- no desloguear.
- mantener vista actual.
- si intentó navegar a módulo prohibido, Navigator bloquea.

**Feedback:**
- toast o banner “No tienes permisos”.

### 5.3 400 / 422 (VALIDATION)
**Significado:** request inválido.

**Acción:**
- mapear errores a campos si existe detalle.
- si no hay detalle por campo, mostrar banner.

**Feedback:**
- inline + banner opcional.

### 5.4 409 Conflict (CONFLICT)
**Significado:** conflicto de negocio.

**Ejemplos típicos:**
- cupo excedido,
- duplicado detectado,
- docente no disponible,
- unicidad de calificación.

**Acción:**
- mantener formulario abierto.
- no borrar campos.
- mostrar el `message` del backend.

**Feedback:**
- banner en la vista o dialog suave si afecta una acción crítica.

### 5.5 404 Not Found
**Significado:** recurso no existe o fue eliminado.

**Acción:**
- refrescar listado.
- mostrar mensaje.

**Feedback:**
- toast/bannner según contexto.

### 5.6 500+ Server Error (SERVER)
**Significado:** fallo técnico.

**Acción:**
- mostrar mensaje genérico.
- ofrecer reintento.
- mostrar `requestId` en modo “detalle técnico”.

**Feedback:**
- diálogo o banner (depende del punto).

---

## 6) Uso de `errorCode` (reglas UI sin inventar lógica)
`errorCode` sirve para:

1. **Categorizar** mejor cuando el HTTP es ambiguo.
2. Definir **mensajes UI** consistentes (sin traducir mal).
3. Identificar errores recurrentes en logs.

### 6.1 Reglas prácticas
- UI muestra el `message` del backend como fuente de verdad.
- UI puede agregar contexto:
  - “Acción no permitida para tu rol.”
  - “Revisa los campos marcados.”

### 6.2 Mapeos sugeridos (ejemplos)
- `AUTH-*` → categoría AUTH
- `*_SIN_PERMISOS` → PERMISSION
- `*_VALIDACION_*` o `VR-*` → VALIDATION
- `*_CONFLICTO_*` o `RN-*` → CONFLICT
- `SYS-*` → SERVER

> Nota: el catálogo exacto de códigos vive en los docs del backend.

---

## 7) Uso de `requestId` (soporte real)
### 7.1 Qué es
`requestId` correlaciona:
- logs del backend,
- respuesta API,
- auditoría (cuando aplica).

### 7.2 Dónde se muestra
- Para usuario final: **no** mostrar siempre.
- Para soporte/depuración: mostrar en sección “Detalle técnico”.

Patrones recomendados:
- En diálogos de error: “Ver detalle” expande y muestra:
  - requestId
  - errorCode
  - path
  - timestamp

- En banners: un pequeño link “Detalle técnico”.

### 7.3 Copiar al portapapeles
Recomendado:
- botón “Copiar requestId” (útil para soporte).

---

## 8) Errores de red (NETWORK)
Incluye:
- timeout,
- DNS,
- servidor caído,
- sin internet.

**Acción:**
- mostrar “No se pudo conectar al servidor”.
- ofrecer reintentar.

**Feedback:**
- banner en listados.
- diálogo si bloquea el arranque.

Regla:
- No culpar al usuario: mensajes neutrales.

---

## 9) Dónde vive la lógica de errores (arquitectura)
### 9.1 ApiClient
- convierte HTTP+JSON a `ApiResult.Success/Error`.
- crea `ErrorInfo`.

### 9.2 ViewModel
- decide el canal de feedback:
  - setea `errorBannerMessage`,
  - setea `fieldErrors`,
  - dispara toast.

### 9.3 View (Controller)
- renderiza:
  - banner,
  - toasts,
  - diálogo,
  - errores inline.

Regla:
- el Controller no decide política; la aplica.

---

## 10) Patrones por tipo de pantalla
### 10.1 Listados (tablas)
- Error al cargar: banner + botón “Reintentar”.
- Empty state: no es error.
- 401: logout.

### 10.2 Formularios
- Validación: errores inline.
- 409: banner arriba del formulario.
- 500: diálogo con detalle técnico.

### 10.3 Reportes asíncronos
- Error de worker: badge `ERROR` + botón “Ver detalle”.
- Reintentar solo ADMIN.

### 10.4 Auditoría
- Filtros fuertes, errores presentados como banner.
- `requestId` siempre visible en vista de auditoría (es un módulo técnico).

---

## 11) Checklist de implementación
- [ ] Existe `ErrorInfo` y `ErrorCategory`.
- [ ] ApiClient normaliza errores (HTTP y red).
- [ ] Política por status implementada (401/403/409/500).
- [ ] Formularios soportan errores inline.
- [ ] Banners tienen reintento.
- [ ] `requestId` visible en detalle técnico y copiable.

---

## 12) Próximo documento
- `10_desktop_patron_tablas_filtros_paginacion.md`

---

## Addendum 2026-03-01: dialogo de cierre con sesión activa

Cuando existe sesión activa y la persona usuaria intenta salir por el botón `X` del sistema:

- no usar toast,
- no usar banner,
- usar dialogo de confirmación.

El dialogo debe explicar:

- que la sesión se cerrara de forma obligatoria,
- que se intentara notificar logout al backend,
- y que la sesión local se invalidara incluso si el backend no responde.

Motivo UX:

- evitar cierre silencioso con token activo,
- y explicar el valor operativo/auditable del cierre.



