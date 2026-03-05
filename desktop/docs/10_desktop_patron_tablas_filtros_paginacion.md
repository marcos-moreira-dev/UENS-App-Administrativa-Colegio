# 10_desktop_patron_tablas_filtros_paginacion

- **Proyecto:** UENS Desktop (JavaFX)
- **UI:** JavaFX TableView + FXML + CSS
- **Arquitectura:** MVVM + Navigator
- **Objetivo:** definir un **patrón de tabla reusable** para listados administrativos (CRUDs), con:
  - búsqueda (`q`),
  - filtros,
  - ordenamiento,
  - paginación,
  - estados (loading/empty/error),
  - y acciones por rol.

---

## 1) Por qué este patrón es crítico (negocio → UI)
En UENS fase 1, casi toda la operación administrativa ocurre como:

- consultar listados,
- filtrar rápido,
- abrir detalle,
- editar/crear,
- y volver al listado.

Si cada módulo implementa su tabla “a su manera”, el sistema se vuelve:
- inconsistente (UX),
- difícil de mantener,
- y lento de desarrollar.

Por eso se define un patrón único para:
- Estudiantes,
- Representantes,
- Docentes,
- Secciones,
- Asignaturas,
- Clases,
- Calificaciones,
- Reportes (listado de solicitudes),
- Auditoría (listado de eventos).

---

## 2) Fuente de verdad (API)
La mayoría de listados del backend usan:
- `GET /...` con query params como: `page,size,sort,q,...filtros...`
- respuesta: `ApiResponse<PageResponseDto<...>>`

**Reglas contractuales:**
- `data.items` es la colección.
- `data.page`, `data.size`, `data.totalElements`, etc.

Fuentes:
- `backend/docs/api/API_ENDPOINTS.md`
- `backend/docs/backend_v_1/08_backend_v_1_paginacion_filtros_ordenamiento_y_consultas.md`

---

## 3) Patrón visual (layout de la pantalla de listado)
Estructura canónica:

1. **Header**
   - Título (H1)
   - Descripción corta opcional
   - CTA principal (ej. “Crear…”) si el rol lo permite

2. **FilterBar** (barra de filtros)
   - Search `q` (texto)
   - Filtros rápidos (ComboBox) según módulo
   - Botón “Limpiar”
   - Botón “Refrescar” (opcional)

3. **TableView**
   - Columnas
   - Columna “Acciones” al final

4. **Footer / PaginationBar**
   - page/size
   - total
   - navegación (prev/next)

5. **Estados**
   - Loading overlay
   - Empty state
   - Error banner + reintento

---

## 4) Estado y responsabilidades (MVVM)
### 4.1 ViewModel (estado mínimo)
Cada pantalla de listado debe tener:

- `BooleanProperty loading`
- `ObjectProperty<ErrorInfo> error`
- `StringProperty bannerMessage` (opcional)

- `ObservableList<RowVm> items`

- `ObjectProperty<QueryState> queryState`
  - `String q`
  - `Map<String, Object> filters`
  - `SortState sort`
  - `PaginationState pagination`

- `ObjectProperty<PageMeta> pageMeta`
  - `totalElements, totalPages, numberOfElements, first, last, sortString`

- Acciones:
  - `loadPage()`
  - `applyFilters()`
  - `clearFilters()`
  - `changePage(page)`
  - `changeSize(size)`
  - `changeSort(column, direction)`

### 4.2 View (FXML Controller)
- bindea controles ↔ propiedades del ViewModel
- no construye URLs
- no parsea JSON

### 4.3 Api layer
- `ApiClient` + `...Api` por módulo
- entrega `ApiResult.Success/Error`

---

## 5) QueryState (modelo estándar de query)
Para no duplicar lógica, se define un estado de consulta estándar:

### 5.1 Campos
- `q` (String) – búsqueda libre
- `filters` (map) – filtros por módulo
- `page` (int)
- `size` (int)
- `sort` (SortState)

### 5.2 Reglas
- Al cambiar filtros o q: resetear `page=0`.
- `size` por defecto: 10 o 20 (decidir una y mantener).
- `sort` por defecto: `id,asc` o el campo más útil del módulo.

### 5.3 Query Object cuando los filtros crecen
Si un listado ya combina:
- paginacion
- busqueda libre
- tres o más filtros

entonces el estado de consulta no debe viajar como una firma larga en `service.listar(...)`.

Regla recomendada:
- encapsularlo en un `Query Object` por módulo

Ejemplos reales:
- `EstudiantesListQuery`
- `ReportesListQuery`
- `AuditoriaEventosQuery`

### 5.4 Serialización a query params
- `q=<texto>`
- `page=<n>`
- `size=<n>`
- `sort=campo,asc`
- filtros por módulo: `estado=ACTIVO`, etc.

Regla:
- Solo mandar filtros permitidos por backend (whitelist).

---

## 6) Filtros por módulo (guía práctica)
El FilterBar es reutilizable, pero cada módulo define su set.

Ejemplos:

### 6.1 Estudiantes
- `q` (nombres/apellidos)
- `estado`
- `seccionId`
- `representanteLegalId`

### 6.2 Docentes
- `q`
- `estado`

### 6.3 Secciones
- `q`
- `estado`
- `grado`
- `paralelo`
- `anioLectivo`

### 6.4 Clases
- `estado`
- `seccionId`
- `asignaturaId`
- `docenteId`
- `diaSemana`

### 6.5 Calificaciones
- `estudianteId`
- `claseId`
- `numeroParcial`

**Regla UX:**
- no meter 12 filtros al inicio: usar “filtros básicos” + “avanzados” (colapsable).

---

## 7) Ordenamiento (Sort)
### 7.1 Contrato
Backend espera `sort=campo,direccion`.

### 7.2 UX
- Indicar visualmente columna ordenada.
- Permitir alternar `asc/desc`.

### 7.3 Whitelists
El backend aplica whitelists; la UI debe:
- mostrar solo columnas ordenables permitidas
- o mapear columnas UI a campos backend.

---

## 8) Paginación
### 8.1 Modelo
- `page` 0-indexed (coherente con Spring Pageable)
- `size`

### 8.2 Controles
- Botón `Anterior` y `Siguiente`
- Selector de tamaño (10/20/50)
- Texto: “Mostrando X–Y de total”

### 8.3 Reglas
- Si cambia `size`, resetear `page=0`.
- Si `last=true`, deshabilitar `Siguiente`.

---

## 9) Estados de pantalla (loading / empty / error)
### 9.1 Loading
- Overlay sobre TableView o skeleton simple.
- Deshabilitar acciones sensibles.

### 9.2 Empty
No es error.

Contenido recomendado:
- mensaje: “No se encontraron resultados.”
- sugerencia: “Ajusta filtros o crea un nuevo registro.”
- CTA “Crear” si rol permite.

### 9.3 Error
- Banner con:
  - `message`
  - botón “Reintentar”
  - link “Detalle técnico” (muestra `errorCode` + `requestId`)

Regla:
- En listados, el error se muestra como banner, no como diálogo (salvo error crítico global).

---

## 10) Acciones por fila (Row Actions)
### 10.1 Acciones estándar
- “Ver” (detalle)
- “Editar”
- “Cambiar estado” (solo si aplica y solo ADMIN)

### 10.2 Reglas UX
- Agrupar acciones en un menú contextual si hay muchas.
- Para Win7 feel: botones pequeños con icono + tooltip.

### 10.3 Guardias
- Ocultar acciones no permitidas por rol.
- Aun así, manejar 403 si ocurre.

---

## 11) Selección y “relaciones indirectas” (dominio cruzado)
Las tablas no deben ser “islas”. En UENS:

- Estudiante muestra resumen de sección/representante.
- Calificación puede mostrar resumen de clase (asignatura + sección + docente + horario).

Regla:
- En columnas, mostrar un resumen corto.
- Para detalle completo, usar Drawer/Detail.

---

## 12) Plantilla FXML (ListViewTemplate)
Estructura sugerida:

- Root: `VBox`
  - Header (HBox)
  - FilterBar (HBox/VBox)
  - TableHost (StackPane)
    - TableView
    - LoadingOverlay
  - PaginationBar (HBox)
  - Banner (si error)

Regla:
- el template se reutiliza por módulo.

---

## 13) Checklist (cuando implementes una tabla)
- [ ] Respeta layout: header + filters + table + pagination.
- [ ] Usa QueryState estándar.
- [ ] Reset page al cambiar filtros.
- [ ] Empty ≠ error.
- [ ] Error banner incluye requestId en detalle técnico.
- [ ] Acciones por rol.
- [ ] Columnas ordenables alineadas a whitelist backend.

---

## 14) Próximo documento
- `11_desktop_patron_formularios_validacion.md`


