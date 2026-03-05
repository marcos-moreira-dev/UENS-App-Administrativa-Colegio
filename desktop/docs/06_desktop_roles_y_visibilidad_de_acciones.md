# 06_desktop_roles_y_visibilidad_de_acciones

- **Proyecto:** UENS Desktop (JavaFX)
- **Arquitectura:** MVVM + Navigator
- **Objetivo:** definir guardias de UI (role guards) y visibilidad/habilitación de acciones por rol.

---

## 1) Propósito
Este documento define cómo el frontend aplica permisos de manera **consistente**:

1. **Qué ve** cada rol (menú y módulos).
2. **Qué acciones** aparecen en cada pantalla.
3. Cómo se manejan los errores de autorización (`401/403`) sin romper UX.
4. Qué patrones usar para implementar guardias en JavaFX (sin duplicar lógica).

**Regla central:**
- El backend es la fuente de verdad, pero el frontend debe **prevenir fricción** ocultando o deshabilitando acciones que el usuario no puede ejecutar.

---

## 2) Roles operativos (fase 1)
Roles soportados por backend y UI:

- `ADMIN`
- `SECRETARIA`

Notas:
- En la documentación de negocio se hablaba de “rol de uso básico”; en V1 se materializa en estos dos roles.
- La UI siempre debe resolver el rol con `GET /api/v1/auth/me`.

---

## 3) Dos capas de seguridad (UI + API)
### 3.1 Capa UI (experiencia)
- Evita mostrar acciones imposibles.
- Reduce errores 403.

### 3.2 Capa API (real)
- El backend siempre valida permisos.
- Si hay bug en UI, igual no se puede “bypassear”.

**Criterio:**
- UI **no reemplaza** al backend; solo mejora UX.

---

## 4) Guardias de UI (patrón recomendado)
### 4.1 SessionState como fuente local
`SessionState` expone:
- `usuario` (login, rol, estado)
- `isAuthenticated`
- métodos: `hasRole(ADMIN)`, `hasAnyRole(...)`

### 4.2 RoleGuard (componente/patrón)
Un RoleGuard debe permitir:
- ocultar un `Node` si no hay permiso
- o deshabilitarlo con tooltip

Reglas de uso:
- **Ocultar** cuando la acción es irrelevante para el rol.
- **Deshabilitar** cuando conviene enseñar que existe, pero no aplica (uso raro en fase 1).

### 4.3 Navigator como guardia principal de rutas
El Navigator valida antes de navegar:
- si el rol no cumple → no navega, muestra “sin permisos”.

Esto evita:
- acceso directo a vistas por evento accidental.

---

## 5) Estrategia de visibilidad vs habilitación
### 5.1 Reglas simples
- **Menú:** ocultar módulos no permitidos.
- **Acciones sensibles (estado, auditoría):** ocultar para SECRETARIA.
- **Acciones de consulta:** visibles para ambos.

### 5.2 Ejemplos
- Botón “Cambiar estado” (INACTIVO/ACTIVO): **solo ADMIN** → oculto a SECRETARIA.
- Botón “Crear sección”: **solo ADMIN** → oculto a SECRETARIA.
- Campo “Rol” en usuario admin (si se implementa UI): editable solo ADMIN.

---

## 6) Matriz de permisos UI por módulo (fase 1)
> Basado en la API real (`backend/docs/api/API_ENDPOINTS.md`).

### 6.1 Dashboard
- `ADMIN`, `SECRETARIA`: ver KPIs.

UI:
- visible para ambos.

### 6.2 Estudiantes
- `ADMIN`, `SECRETARIA`: listar, ver, crear, editar, asignar sección vigente.
- `ADMIN`: cambiar estado.

UI:
- botón “Inactivar/Activar” solo ADMIN.
- CTA “Asignar sección” visible para ambos.

### 6.3 Representantes
- `ADMIN`, `SECRETARIA`: listar, ver, crear, editar.

UI:
- CRUD completo para ambos.

### 6.4 Docentes
- `ADMIN`, `SECRETARIA`: listar, ver, crear, editar.
- `ADMIN`: cambiar estado.

UI:
- estado solo ADMIN.

### 6.5 Secciones
- `ADMIN`, `SECRETARIA`: listar, ver detalle.
- `ADMIN`: crear, editar, cambiar estado.

UI:
- SECRETARIA: módulo principalmente de consulta.
- ADMIN: CRUD completo.

### 6.6 Asignaturas
- `ADMIN`, `SECRETARIA`: listar, ver detalle.
- `ADMIN`: crear, editar, cambiar estado.

UI:
- patrón igual a secciones.

### 6.7 Clases
- `ADMIN`, `SECRETARIA`: listar, ver detalle.
- `ADMIN`: crear, editar, cambiar estado.

UI:
- SECRETARIA: consulta.
- ADMIN: CRUD.

### 6.8 Calificaciones
- `ADMIN`, `SECRETARIA`: listar, ver, crear, editar.

UI:
- CRUD para ambos.
- filtros fuertes (estudiante/clase/parcial).

### 6.9 Reportes
- `ADMIN`, `SECRETARIA`: crear solicitud, listar, ver estado, descargar.
- `ADMIN`: reintentar solicitud fallida.

UI:
- botón “Reintentar” solo ADMIN.

### 6.10 Auditoría
- `ADMIN`: ver eventos + solicitar reporte de auditoría.
- `SECRETARIA`: sin acceso.

UI:
- módulo oculto a SECRETARIA.

---

## 7) Reglas de UX ante 401/403
### 7.1 401 (no autenticado / token inválido)
Comportamiento estándar:
1. mostrar mensaje corto (“Sesión expirada”).
2. limpiar sesión.
3. navegar a Login.

### 7.2 403 (sin permisos)
Comportamiento estándar:
- mostrar banner/toast “No tienes permisos para esta acción”.
- no cambiar de pantalla.
- si fue navegación: quedarse en la vista anterior.

---

## 8) Acciones sensibles (siempre con confirmación)
Estas acciones deben requerir confirmación (dialog):
- Cambiar estado a INACTIVO (entidades académicas).
- Reintentar reportes fallidos.

Regla:
- Confirmación debe explicar consecuencia (“No se podrá usar para nuevas asignaciones/operaciones”).

---

## 9) Diseño por rol (detalle práctico)
### 9.1 SECRETARIA
- Prioridad: operación diaria rápida.
- UI debe ser directa, menos “configuración”.
- No mostrar módulos de supervisión.

### 9.2 ADMIN
- Prioridad: configuración académica y supervisión.
- UI debe exponer controles adicionales (estado, auditoría, reintentos).

---

## 10) Checklist de implementación
- [ ] Menú construido por rol.
- [ ] Navigator bloquea vistas no permitidas.
- [ ] Acciones sensibles ocultas a SECRETARIA.
- [ ] 401 limpia sesión y vuelve a login.
- [ ] 403 muestra mensaje claro.
- [ ] Confirmación en acciones destructivas.

---

## 11) Próximo documento
- `07_desktop_flujo_sesion_login_y_bootstrap.md`

