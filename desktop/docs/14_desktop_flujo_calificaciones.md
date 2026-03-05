# 14_desktop_flujo_calificaciones

- **Proyecto:** UENS Desktop (JavaFX)
- **Módulo:** Calificaciones
- **Arquitectura:** MVVM + Navigator
- **UI:** Pantalla de listado filtrable + Modal/Drawer para create/edit
- **Objetivo:** aterrizar calificaciones en términos de **negocio + UI + API**.

---

## 1) Rol del módulo en el negocio (por qué existe)
### 1.1 Negocio
En fase 1, la calificación es una operación académica básica:
- Se registra una nota para un **estudiante** en una **clase** y un **parcial**.
- Solo existen **2 parciales** (1 y 2).
- La referencia principal es **Clase**, no asignatura abstracta.

Esto responde a problemas reales del negocio:
- consulta lenta de notas,
- registros dispersos,
- dificultad para ver “qué nota tiene cada estudiante por parcial”.

### 1.2 UI
Este módulo obliga a resolver:
- filtros fuertes por estudiante/clase/parcial,
- inputs numéricos confiables,
- evitar duplicados (unicidad por estudiante+clase+parcial),
- y consistencia de mensajes (409/validación).

---

## 2) Reglas del dominio relevantes (resumen)
Fuentes:
- `docs/02_levantamiento_requerimientos.md`
- `docs/04_reglas_negocio_y_supuestos.md`
- `docs/05_glosario_alcance_y_limites.md`

Reglas clave:
1. Calificación se registra por **estudiante + clase + parcial**.
2. Parciales permitidos: **1** y **2**.
3. Nota debe cumplir escala institucional (rango exacto puede definirse en implementación; UI valida un rango razonable).
4. Entidades inactivas no deben participar en operaciones nuevas.
5. Unicidad operativa: no duplicar calificación para el mismo estudiante+clase+parcial.

Implicación UI:
- Nunca pedir “asignatura” como referencia principal; se deriva desde la clase.

---

## 3) Permisos por rol
Fuente:
- `backend/docs/api/API_ENDPOINTS.md`

- `ADMIN` y `SECRETARIA`:
  - listar
  - ver detalle
  - crear
  - editar

No hay “cambiar estado” para calificación en V1.

---

## 4) Pantallas y patrón de navegación
### 4.1 Pantalla principal
**CalificacionesView** (listado filtrable)
- Tabla + filtros (estudiante/clase/parcial) + paginación
- CTA: “Registrar calificación”

### 4.2 Modal o Drawer
**CalificacionFormDialog** (recomendado modal)
- Campos relativamente pocos
- Mantiene contexto del listado

**CalificacionDetailDrawer** (opcional)
- Mostrar detalle de una calificación

Regla:
- Como la operación es puntual, modal suele ser suficiente.

---

## 5) API del módulo (endpoints)
Fuente: `backend/docs/api/API_ENDPOINTS.md`

### 5.1 Listado
- `GET /api/v1/calificaciones`
- Query opcional: `estudianteId,claseId,numeroParcial,page,size,sort`

### 5.2 Detalle
- `GET /api/v1/calificaciones/{calificacionId}`

### 5.3 Crear
- `POST /api/v1/calificaciones`

### 5.4 Editar
- `PUT /api/v1/calificaciones/{calificacionId}`

Dependencias para selección:
- `GET /api/v1/estudiantes` (para elegir estudiante)
- `GET /api/v1/clases` (para elegir clase)

Dependencias para “resumen” de clase:
- `GET /api/v1/clases/{claseId}` (si hace falta mostrar detalle)

---

## 6) UX del listado (tabla)
Basado en `10_desktop_patron_tablas_filtros_paginacion.md`.

### 6.1 Columnas sugeridas
- ID
- Estudiante (resumen)
- Clase (resumen)
- Parcial
- Nota
- Fecha registro (si existe)
- Acciones (Ver/Editar)

### 6.2 Resumen de estudiante
- `apellidos, nombres` (o `nombres apellidos`, definir convención)
- sección vigente (opcional en tooltip)

### 6.3 Resumen de clase (importante)
Mostrar como texto corto:
- `Asignatura` + `Sección` + `díaSemana horaInicio–horaFin`
Ejemplo:
- “Matemática – 5to A (LUN 07:00–07:45)”

Nota:
- La asignatura se deriva desde clase.

---

## 7) Filtros (muy importantes)
Calificaciones sin filtros se vuelve ruidoso.

### 7.1 Filtros básicos
- estudianteId
- claseId
- numeroParcial

### 7.2 UX de filtros
- Mostrar filtros como selectores con búsqueda:
  - Estudiante → buscar por nombre/apellido
  - Clase → filtrar por sección/asignatura si el volumen es alto

Recomendación:
- En el listado de calificaciones, tener un modo “seleccionar estudiante” primero.
  - Muchas operaciones reales empiezan por “quiero ver las notas de este estudiante”.

---

## 8) UX del formulario (create/edit)
Basado en `11_desktop_patron_formularios_validacion.md`.

### 8.1 Campos
- `estudianteId` (requerido)
- `claseId` (requerido)
- `numeroParcial` (requerido, 1/2)
- `nota` (requerido)
- `fechaRegistro` (opcional)
- `observacion` (opcional)

### 8.2 Validaciones UI (obvias)
- parcial ∈ {1,2}
- nota: rango UI recomendado (por defecto 0..10)
- si hay fechaRegistro: fecha válida

Regla:
- La escala exacta es fuente de verdad del backend; UI solo previene valores ridículos.

### 8.3 Input numérico (nota)
Recomendación UX:
- control numérico con:
  - restringir caracteres,
  - permitir decimales,
  - formateo consistente.

Regla:
- Mostrar ejemplo o hint (“Ej: 8.75”).

---

## 9) Prevención de duplicados (unicidad)
La combinación (estudiante, clase, parcial) es única.

### 9.1 UX proactiva (opcional)
Si ya hay una calificación existente para esa combinación:
- mostrar advertencia en UI (si se detecta con una consulta previa) o
- confiar en backend y manejar 409.

### 9.2 Manejo 409 (obligatorio)
- mostrar banner “Ya existe calificación para este estudiante en esta clase y parcial”.
- mantener datos.
- sugerir: “edita la existente”.

---

## 10) Manejo de errores por escenario
Basado en `09_desktop_manejo_errores_toasts_dialogos.md`.

### 10.1 En listado
- error al cargar: banner + reintentar.

### 10.2 En formulario
- 400/422: errores inline.
- 409: banner conflicto (duplicado/cupo/estado).
- 401: logout.
- 403: mensaje sin cerrar.
- 500: diálogo con requestId.

---

## 11) Recomendación MVVM específica
### 11.1 ViewModels sugeridos
- `CalificacionesListViewModel`
- `CalificacionFormViewModel`
- `CalificacionDetailViewModel` (si se hace drawer de detalle)

### 11.2 Servicios API
- `CalificacionesApi`
- `EstudiantesApi` (selector)
- `ClasesApi` (selector)

---

## 12) Checklist de implementación
- [ ] Listado filtrable (estudiante/clase/parcial).
- [ ] Columna de clase con resumen derivado (asignatura+sección+horario).
- [ ] Modal de create/edit que conserva input al fallar.
- [ ] Manejo 409 duplicado sin borrar campos.
- [ ] Validación parcial 1/2.
- [ ] UX numérica para nota.

---

## 13) Próximo documento
- `15_desktop_flujo_reportes_async.md`

