# 07_desktop_flujo_sesion_login_y_bootstrap

- **Proyecto:** UENS Desktop (JavaFX)
- **Arquitectura:** MVVM + Navigator
- **Objetivo:** definir el flujo de sesión: login → bootstrap → uso normal → expiración.
- **Backend:** JWT Bearer + `GET /api/v1/auth/me`

---

## 1) Propósito
Definir un flujo de sesión **serio** para una app administrativa desktop:

1. Login con JWT.
2. Bootstrap de sesión (validar token, cargar usuario/rol).
3. Menú dinámico por rol.
4. Manejo consistente de `401` (expirado) y `403` (sin permisos).
5. Reglas de persistencia del token (desktop).

Este flujo es base para toda la arquitectura (Navigator, guards, API client).

---

## 2) Contratos de la API (fuente de verdad)
Endpoints:
- `POST /api/v1/auth/login` (público)
- `GET /api/v1/auth/me` (protegido)

Header:
- `Authorization: Bearer <accessToken>`

Respuestas:
- En éxito: `ApiResponse<T>` (leer `data`).
- En error: `ApiErrorResponse` (`errorCode`, `message`, `requestId`).

Errores de login que la UI debe reconocer:
- `AUTH-01-CREDENCIALES_INVALIDAS`
- `AUTH-06-LOGIN_TEMPORALMENTE_BLOQUEADO`
- `AUTH-07-RATE_LIMIT_LOGIN_EXCEDIDO`

Fuente:
- `backend/docs/api/API_ENDPOINTS.md`

---

## 3) Modelo mental (negocio → frontend)
- Solo usuarios **ACTIVO** pueden operar.
- El rol (`ADMIN` o `SECRETARIA`) determina:
  - módulos visibles,
  - acciones disponibles.

Por eso el frontend debe:
- resolver rol al iniciar,
- construir el menú con guardias,
- y reaccionar correctamente ante expiración.

Adicionalmente, el formulario de login debe:
- conservar el `login` escrito cuando el backend responde error
- mostrar el `message` del backend sin inventar otra semántica
- usar `requestId` para soporte si el error persiste

---

## 4) Componentes involucrados
### 4.1 SessionState (estado global)
Debe almacenar:
- `token` (String)
- `usuario` (id, login, rol, estado)
- `isAuthenticated` (derivado)

Debe exponer:
- `login()`
- `logout()`
- `bootstrapSession()`
- `hasRole(ADMIN)` / `hasAnyRole(...)`

Implementación sugerida:
- propiedades observables (JavaFX `Property`) para que la UI reaccione.

### 4.2 ApiClient (HTTP)
Responsable de:
- agregar header `Authorization` cuando hay token,
- parsear `ApiResponse<T>`,
- normalizar errores (401/403/409/500).

### 4.3 AuthApi
Métodos típicos:
- `login(login, password)`
- `me()`

### 4.4 Navigator
- decide qué vista mostrar según estado de sesión.

---

## 5) Flujo 1: Arranque (bootstrap)
### 5.1 Objetivo
Al iniciar la app:
- cargar fonts y CSS,
- intentar restaurar sesión si existe token,
- resolver usuario/rol,
- navegar a Dashboard o Login.

### 5.2 Secuencia recomendada
1. **BootstrapApp**
   - carga fonts (`03_...`)
   - carga CSS base (`17_...`)
   - inicializa `SessionState` y `Navigator`

2. **Restore token (opcional)**
   - leer token de storage (si se decidió persistir)

3. **Validate token**
   - llamar `GET /api/v1/auth/me`

4. Resultado:
   - **200**: sesión válida → construir menú por rol → navegar `DASHBOARD`
   - **401**: token inválido/expirado → limpiar → navegar `LOGIN`

Regla:
- El bootstrap nunca deja la app en estado ambiguo.

---

## 6) Flujo 2: Login
### 6.1 Pantalla Login (FXML)
Campos:
- login
- password
- botón “Iniciar sesión”
- estado loading
- error visible (si credenciales inválidas)

### 6.2 Secuencia
1. usuario envía credenciales.
2. UI entra en loading.
3. `POST /api/v1/auth/login`.
4. Si éxito:
   - guardar token
   - llamar `auth/me` (recomendado) para confirmar rol/estado
   - construir menú por rol
   - navegar `DASHBOARD`
5. Si error:
   - mostrar `message`
   - mantener campos (no limpiar todo)

Nota:
- Aunque el login devuelva `usuario`, `auth/me` sigue siendo útil como “fuente de verdad” y para bootstraps futuros.

---

## 7) Flujo 3: Uso normal (requests protegidos)
Regla:
- Todo request protegido debe incluir `Authorization`.

Recomendación:
- ApiClient central agrega el header automáticamente.

---

## 8) Manejo estándar de errores de sesión
### 8.1 401 (Unauthorized)
Interpretación:
- Token inválido o expirado.

Comportamiento:
1. mostrar mensaje corto (“Sesión expirada”).
2. limpiar token/usuario.
3. navegar a `LOGIN`.

Regla:
- la renovacion silenciosa ya existe y queda encapsulada en `ApiClient`; ver addendum 2026-03-03.

### 8.2 403 (Forbidden)
Interpretación:
- Sesión válida, pero sin permisos.

Comportamiento:
- mostrar mensaje “No tienes permisos”.
- no desloguear.
- no navegar automáticamente (salvo navegación a vista prohibida: quedarse en la anterior).

---

## 9) Persistencia del token (desktop)
Por defecto (recomendado):
- **token solo en memoria** durante la ejecución.

Opcional (“Recordarme”):
- persistir token en disco con:
  - expiración,
  - y al menos ofuscación/cifrado básico.

Regla:
- nunca guardar password.

---

## 10) UX de sesión (detalles importantes)
- Mostrar usuario/rol en Topbar.
- Botón “Cerrar sesión” siempre visible.
- En logout:
  - limpiar sesión
  - volver a Login

- Si falla `auth/me` por 500:
  - mostrar mensaje técnico
  - permitir reintentar

---

## 11) Checklist de implementación
- [ ] `SessionState` existe y es observable.
- [ ] `ApiClient` agrega Bearer token automáticamente.
- [ ] `bootstrapSession()` llama `auth/me`.
- [ ] Menú se construye por rol.
- [ ] `401` → logout + login.
- [ ] `403` → mensaje sin logout.
- [ ] Logout limpia todo.

---

## 12) Próximo documento
- `08_desktop_cliente_api_contratos_y_dtos_ui.md`

---

## Addendum 2026-03-01: cierre forzado y logout best-effort

Para mantener consistencia con el runtime actual:

- Contratos de auth usados por el desktop:
  - `POST /api/v1/auth/login`
  - `GET /api/v1/auth/me`
  - `POST /api/v1/auth/logout` como endpoint opcional o best-effort si el backend lo implementa

- Flujo de cierre no normal:
  - si la persona usuaria usa el botón `X` del sistema con sesión activa,
  - la app muestra un diálogo de confirmación,
  - si confirma, intenta `auth/logout`,
  - y aunque el backend falle, limpia token y usuario antes de salir.

- Regla dura:
  - el cierre por ventana no debe dejar la sesión viva en memoria.

- Atajo operativo:
  - `F11` alterna pantalla completa en el mismo `Stage`.

---

## Addendum 2026-03-03: auto refresh del token

El flujo de sesión ya evolucionó respecto a la narrativa inicial.

Contratos usados por el desktop:
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /api/v1/auth/me`

Estado real de `SessionState`:
- `accessToken`
- `refreshToken`
- `accessTokenExpiresAt`
- `refreshTokenExpiresAt`
- `usuario`

Regla operativa del `ApiClient`:
- antes de un request autenticado, si el `accessToken` está cerca de expirar y el `refreshToken` sigue usable, intenta `auth/refresh`
- si la renovación funciona, actualiza la sesión en memoria
- si falla y ya no queda token válido, ejecuta logout local

---

## Addendum 2026-03-04: cómo estudiar este flujo

Si quieres entender bien la sesión en una app desktop, no estudies solo la pantalla de login. Estudia el ciclo completo:

1. cómo entra la identidad
2. cómo se conserva en memoria
3. cómo se renueva
4. cómo se invalida
5. cómo reacciona la navegación

Cuando dominas ese ciclo, ya no ves el login como una ventana aislada. Lo ves como una pieza de estado global, seguridad y UX.




