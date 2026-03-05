# 13_desktop_flujo_estudiantes_modulo_estrella

- **Proyecto:** UENS Desktop (JavaFX)
- **Módulo:** Estudiantes (módulo estrella)
- **Arquitectura:** MVVM + Navigator
- **UI:** Pantalla de listado + Drawer/Modal para detalle/edición
- **Objetivo:** aterrizar el flujo completo de Estudiantes en términos de **negocio + UI + API**.

---

## 1) Por qué Estudiantes es el módulo estrella
### 1.1 Negocio
El estudiante es el centro operativo del dominio académico en fase 1:
- debe tener representante legal principal,
- puede estar asignado a una sección vigente,
- genera calificaciones por clase/parcial,
- y participa en control de cupo.

### 1.2 UI
Este módulo obliga a resolver:
- tablas con filtros reales,
- formularios con relaciones (representante y sección),
- permisos por rol (cambiar estado solo ADMIN),
- validaciones (fechas, estados),
- y manejo de conflictos de negocio (409).

### 1.3 API
Consume y cruza módulos:
- Estudiantes
- Representantes
- Secciones

Por eso es ideal para validar la arquitectura antes de replicar en otros CRUDs.

---

## 2) Reglas de negocio relevantes (resumen)
Fuentes:
- `docs/02_levantamiento_requerimientos.md`
- `docs/04_reglas_negocio_y_supuestos.md`
- `docs/05_glosario_alcance_y_limites.md`

Reglas clave:
1. Estudiante debe asociarse a **1 representante legal principal** (fase 1).
2. Rango operativo de edad 6–13 validado respecto a fecha de registro/matrícula (edad derivada).
3. Asignación vigente única estudiante–sección.
4. Cupo máximo por sección (≤35) y no exceder cupo en asignación.
5. Estados `ACTIVO/INACTIVO`: INACTIVO bloquea operaciones nuevas.

Implicación UI:
- el formulario debe facilitar seleccionar representante y sección sin usar IDs.

---

## 3) Permisos por rol (UI)
Fuentes:
- `backend/docs/api/API_ENDPOINTS.md`
- `06_desktop_roles_y_visibilidad_de_acciones.md`

- `ADMIN` y `SECRETARIA`:
  - listar
  - ver detalle
  - crear
  - editar
  - asignar sección vigente

- Solo `ADMIN`:
  - cambiar estado (`PATCH /estado`)

Regla UI:
- botón de “Activar/Inactivar” oculto a SECRETARIA.

---

## 4) Pantallas y patrón de navegación
### 4.1 Pantalla principal
**EstudiantesView** (listado)
- Tabla + filtros + paginación
- Acciones por fila: Ver / Editar / (ADMIN) Cambiar estado
- CTA principal: Crear estudiante

### 4.2 Drawer (recomendado)
**EstudianteDrawer**
- Modo detalle (solo lectura)
- Modo edición (form)

### 4.3 Modal (opcional)
**AsignarSeccionVigenteDialog**
- acción puntual, pocas opciones

Regla:
- Mantener la tabla visible mientras se edita (drawer), para flujo administrativo rápido.

---

## 5) API del módulo (endpoints)
Fuente: `backend/docs/api/API_ENDPOINTS.md`

### 5.1 Listado
- `GET /api/v1/estudiantes`
- Query opcional: `q,estado,seccionId,representanteLegalId,page,size,sort`

### 5.2 Detalle
- `GET /api/v1/estudiantes/{estudianteId}`

### 5.3 Crear
- `POST /api/v1/estudiantes`

### 5.4 Editar
- `PUT /api/v1/estudiantes/{estudianteId}`

### 5.5 Cambiar estado (ADMIN)
- `PATCH /api/v1/estudiantes/{estudianteId}/estado`

### 5.6 Asignar sección vigente
- `PUT /api/v1/estudiantes/{estudianteId}/seccion-vigente`

Dependencias:
- `GET /api/v1/representantes` (para elegir representante)
- `GET /api/v1/secciones` (para elegir sección)

---

## 6) UX del listado (tabla)
Basado en `10_desktop_patron_tablas_filtros_paginacion.md`.

### 6.1 Columnas sugeridas
- ID
- Nombres
- Apellidos
- Fecha nacimiento
- Estado
- Representante (resumen)
- Sección vigente (resumen)
- Acciones

### 6.2 Resúmenes (relaciones indirectas)
- Representante: `nombres apellidos` (+ teléfono opcional en tooltip)
- Sección: `grado + paralelo + año lectivo`

Regla:
- No mostrar IDs de representante/sección como dato principal.

### 6.3 Filtros
- `q` (nombres/apellidos)
- `estado` (ACTIVO/INACTIVO)
- `seccionId`
- `representanteLegalId`

UX recomendada:
- filtros básicos visibles: `q`, `estado`.
- filtros avanzados colapsables: sección, representante.

---

## 7) UX del formulario (crear/editar)
Basado en `11_desktop_patron_formularios_validacion.md`.

### 7.1 Campos
- `nombres` (requerido)
- `apellidos` (requerido)
- `fechaNacimiento` (requerido)
- `representanteLegalId` (requerido)
- `seccionId` (opcional en create si se quiere permitir registrar sin asignación inmediata)

**Decisión recomendada:**
- Permitir crear estudiante con sección opcional, pero facilitar “Asignar sección” como acción inmediata.

Motivo:
- en operación real, a veces primero registras y luego asignas.
- igual respetas “asignación vigente única” cuando se asigna.

### 7.2 Validaciones UI (obvias)
- nombres/apellidos no vacíos.
- fechaNacimiento en pasado.

Nota:
- la validación exacta 6–13 es del backend (y de reglas), no duplicar matemática compleja en UI.

### 7.3 Selección de representante
Patrón recomendado:
- campo “Representante” con:
  - botón “Buscar/Seleccionar” → abre modal de búsqueda (tabla de representantes)
  - muestra resumen del seleccionado

Alternativa:
- ComboBox con búsqueda si el volumen es pequeño.

### 7.4 Selección de sección
Patrón recomendado:
- selector de sección con resumen `grado/paralelo/año`.
- mostrar cupo máximo y (si se puede) cupos disponibles.

---

## 8) Acción “Asignar sección vigente” (CTA importante)
### 8.1 Negocio
- cambiar sección no mantiene historial en fase 1.
- debe respetar cupo.

### 8.2 UI
CTA recomendado:
- en detalle del estudiante: botón “Asignar sección vigente”
- también como acción por fila (opcional)

Flujo:
1. abrir modal sencillo
2. seleccionar sección
3. confirmar
4. refrescar detalle y tabla

### 8.3 API
- `PUT /api/v1/estudiantes/{id}/seccion-vigente` con `{ seccionId }`

Errores típicos:
- `409` si cupo excedido / regla de negocio.

---

## 9) Cambiar estado (solo ADMIN)
### 9.1 UX
- botón “Inactivar” o “Activar” según estado.
- confirm dialog obligatorio.

Texto de confirmación recomendado:
- “Al inactivar, el estudiante no podrá usarse en nuevas asignaciones/operaciones.”

### 9.2 API
- `PATCH /api/v1/estudiantes/{id}/estado` con `{ estado }`

---

## 10) Manejo de errores (por escenario)
Basado en `09_desktop_manejo_errores_toasts_dialogos.md`.

### 10.1 En listado
- error al cargar: banner + reintentar + detalle técnico.

### 10.2 En formulario
- 400/422: errores inline.
- 409: banner de conflicto, mantener datos.
- 401: logout.
- 403: mensaje y mantener drawer.
- 500: diálogo con requestId.

---

## 11) Recomendación MVVM específica
### 11.1 ViewModels sugeridos
- `EstudiantesListViewModel`
- `EstudianteDetailViewModel`
- `EstudianteFormViewModel`
- `AsignarSeccionVigenteViewModel` (si se hace modal dedicado)

### 11.2 Servicios API
- `EstudiantesApi`
- `RepresentantesApi`
- `SeccionesApi`

---

## 12) Checklist de “listo para replicar a otros módulos”
Cuando Estudiantes esté bien implementado, debe cumplir:

- [ ] Tabla reusable con filtros/paginación/sort.
- [ ] Drawer con create/edit y detalle.
- [ ] Selector de representante y sección sin IDs.
- [ ] Manejo correcto 409 (no borrar campos).
- [ ] Acciones por rol (estado solo ADMIN).
- [ ] Confirmaciones para acciones sensibles.
- [ ] Refrescos coherentes (listado y detalle).

Si esto se logra, el patrón se replica casi igual a:
- Representantes, Docentes, Secciones, Asignaturas, Clases.

---

## 13) Próximo documento
- `14_desktop_flujo_calificaciones.md`

