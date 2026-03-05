# 08_desktop_cliente_api_contratos_y_dtos_ui

- **Proyecto:** UENS Desktop (JavaFX)
- **Arquitectura:** MVVM + Navigator
- **Objetivo:** definir el **cliente API**, contratos (`ApiResponse`, `ApiErrorResponse`, `PageResponse(items)`) y el mapeo a estados de UI.

---

## 1) Propósito
Este documento define un estándar para consumir el backend UENS sin caos:

1. Un **ApiClient** central (HTTP) que maneje:
   - base URL,
   - headers,
   - parseo JSON,
   - errores,
   - y autenticación Bearer.

2. Contratos y DTOs “base”:
   - `ApiResponse<T>`
   - `ApiErrorResponse`
   - `PageResponse<T>` (colección en `items`)

3. Un mapeo consistente de respuestas a **estados de UI**:
   - `loading`, `success`, `empty`, `error`.

4. Separación clara entre:
   - **DTOs de API** (lo que viene del backend),
   - **UI Models** (lo que la vista necesita).

Fuentes de verdad:
- `backend/docs/api/API_ENDPOINTS.md`
- `backend/docs/backend_v_1/19_backend_v_1_contexto_integracion_y_diseno_frontend.md`

---

## 2) Contratos del backend (formas JSON)
### 2.1 ApiResponse<T> (éxito)
Forma estándar:
- `ok: true`
- `message: string`
- `data: T`
- `timestamp: ISO`

Regla de frontend:
- **Nunca** tratar el body como el DTO final.
- Siempre extraer `data`.

### 2.2 ApiErrorResponse (error)
Campos clave:
- `ok: false`
- `errorCode: string`
- `message: string`
- `path: string`
- `timestamp: ISO`
- `requestId: string`

Regla de frontend:
- mostrar `message`
- log técnico: `errorCode` + `requestId`

### 2.3 PageResponse<T> (paginación)
En listados paginados, `data` contiene:
- `items: T[]`
- `page, size, totalElements, totalPages, numberOfElements, first, last, sort`

Regla:
- la colección se llama **`items`** (no `content`).

---

## 3) ApiClient (diseño recomendado)
### 3.1 Responsabilidades
- Construir requests HTTP.
- Agregar headers:
  - `Content-Type: application/json`
  - `Accept: application/json`
  - `Authorization: Bearer <token>` si hay sesión.
- Parsear JSON (Jackson recomendado).
- Convertir errores HTTP en un modelo de error uniforme.

### 3.2 Componentes sugeridos
- `ApiClient`
- `ApiConfig` (baseUrl, timeouts)
- `AuthInterceptor` (aplica token)
- `ErrorMapper` (normaliza errores)

### 3.3 Manejo de timeouts
- requests de listados: timeout moderado.
- descargas de archivos: timeout mayor.

Regla UX:
- si timeout: mostrar mensaje de red y permitir reintento.

---

## 4) Modelo de resultado en el cliente (Result)
Para no depender de excepciones en toda la UI, se recomienda que el cliente entregue un tipo resultado:

- `ApiResult.Success<T>(data, message)`
- `ApiResult.Error(errorInfo)`

Donde `errorInfo` incluye:
- `httpStatus`
- `errorCode`
- `message`
- `requestId`
- `path`

Esto permite en ViewModels:
- actualizar estado observable sin try/catch gigante.

---

## 5) Mapeo a estados de UI (patrón estándar)
### 5.1 Estados canónicos por pantalla
Cada ViewModel debe manejar al menos:
- `loading: BooleanProperty`
- `error: ObjectProperty<ErrorInfo>`
- `toastMessage: StringProperty` (opcional)

Y para listados:
- `items: ObservableList<Row>`
- `pageState: ObjectProperty<PageState>`

### 5.2 Reglas de transición
- Antes de llamar API: `loading=true`, limpiar error.
- Si éxito:
  - `loading=false`
  - poblar data
  - toast opcional con `message`.
- Si error:
  - `loading=false`
  - setear `errorInfo`
  - si 401: disparar logout + navegación a Login.

### 5.3 “Empty state”
Si el listado llega con `items=[]`:
- no es error.
- mostrar estado vacío con:
  - mensaje
  - CTA (“Crear…”) si el rol lo permite.

---

## 6) Tipos de error y UX esperada
### 6.1 401
- sesión inválida/expirada
- comportamiento: limpiar sesión + Login

### 6.2 403
- sin permisos
- comportamiento: mensaje claro, sin logout

### 6.3 400 / 422
- validación
- comportamiento: mostrar errores en formulario

### 6.4 409
- conflicto de negocio
- comportamiento: mostrar `message` y mantener formulario abierto

### 6.5 500
- error técnico
- comportamiento: mensaje genérico + mostrar `requestId` en modo detalle

---

## 7) DTOs de API vs UI Models
### 7.1 DTOs (API)
- reflejan el contrato del backend.
- se usan en el módulo `api.dto`.

### 7.2 UI Models
- adaptan datos para presentación:
  - concatenar nombres
  - formatear fechas
  - traducir enums a labels

Regla:
- el formateo se hace en capa de presentación, no en ApiClient.

---

## 8) Paginación, filtros y ordenamiento (query building)
El cliente debe construir query params consistentemente:

- `page`, `size`
- `sort=campo,asc`
- `q` (búsqueda)
- filtros por módulo

Regla:
- los filtros permitidos deben alinearse con whitelists del backend.

---

## 9) Descargas binarias (archivos)
Para endpoints que devuelven archivo:
- no parsear JSON.
- guardar bytes en disco.
- usar `Content-Disposition` para nombre si está disponible.

UX:
- mostrar progreso o al menos “descargando…”
- si falla: reintentar.

---

## 10) Checklist de implementación
- [ ] ApiClient central existe.
- [ ] DTOs base (`ApiResponse`, `ApiErrorResponse`, `PageResponse`) existen.
- [ ] ApiResult (Success/Error) existe.
- [ ] ViewModels usan estados canónicos (loading/error/items/pageState).
- [ ] 401/403 se manejan consistente.
- [ ] Query builder está centralizado.

---

## 11) Próximo documento
- `09_desktop_manejo_errores_toasts_dialogos.md`

---

## Addendum 2026-03-03: responsabilidades nuevas del ApiClient

Con la sesión renovable ya implementada, el `ApiClient` asume además estas responsabilidades:

- detectar si el `accessToken` está dentro de la ventana de renovación
- llamar `POST /api/v1/auth/refresh` usando el `refreshToken`
- actualizar `SessionState` con el par de tokens rotado
- evitar que los ViewModels tengan que saber nada de refresh token

---

## Addendum 2026-03-04: cómo organizarlo para estudiar sin perderte

Una forma muy sana de estudiar este documento es separar mentalmente tres cosas:

1. contrato HTTP
2. transformación a DTO
3. transformación a estado de UI

Si mezclas esas tres responsabilidades en una sola clase, el aprendizaje se vuelve confuso y el código también. Si las separas, entiendes mucho mejor por qué existen `ApiClient`, DTOs, mappers y ViewModels.

### Regla práctica

El `ApiClient` no debería formatear etiquetas de UI ni decidir colores, badges o textos visuales. Su trabajo es hablar bien con el backend y entregar resultados uniformes para que la capa de presentación haga su parte.


