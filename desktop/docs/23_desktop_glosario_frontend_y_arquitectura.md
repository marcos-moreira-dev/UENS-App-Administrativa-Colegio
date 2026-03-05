# 23_desktop_glosario_frontend_y_arquitectura

- **Proyecto:** UENS Desktop (JavaFX)
- **Arquitectura base:** MVVM + Navigator + JavaFX CSS
- **Objetivo:** dar un vocabulario común, práctico y útil para entender el frontend desktop sin caer en jerga vacia.

---

## 1) Propósito
Este glosario existe para:

1. traducir terminos técnicos a lenguaje entendible;
2. conectar cada termino con ejemplos reales del repo;
3. ayudar a estudiantes, revisores y docentes a hablar del sistema con precision;
4. evitar que se usen palabras de arquitectura sin entender su función.

---

## 2) Terminos base de arquitectura

### MVVM
**Significado:** `Model - View - ViewModel`.

**Idea simple:** la vista muestra; el ViewModel expone estado observable; la capa application ejecuta casos de uso.

**En este proyecto se ve en:**
- `modules/*/view`
- `modules/*/viewmodel`
- `modules/*/application`

**Para que sirve:** separar la UI del acceso HTTP y del estado de negocio.

### View
**Significado:** capa visible con la que el usuario interactua.

**En este proyecto:** FXML + controller JavaFX.

**Responsabilidad:** dibujar controles, disparar eventos, hacer bind con el ViewModel y delegar al resto.

### ViewModel
**Significado:** estado observable consumido por la vista.

**En este proyecto:** usa `Property`, `Binding` y `ObservableList`.

**Responsabilidad:** exponer loading, items, filtros, estado de formularios y validaciones.

### Model
**Significado:** representacion de datos del dominio o de UI.

**Ejemplos:** DTOs, rows de tabla, datos de formularios, metadatos de paginación.

### Application Service
**Significado:** capa intermedia entre UI y backend.

**En este proyecto:** servicios por módulo como `EstudiantesService`, `ReportesService` o `AuditoriaService`.

**Responsabilidad:** ejecutar casos de uso sin depender de JavaFX.

### Composition Root
**Significado:** punto central donde se crean y conectan dependencias.

**En este proyecto:** `AppBootstrap`, `AppContext` y `ControllerFactory`.

**Para que sirve:** evitar que cada controller construya sus propias dependencias.

### Dependency Wiring
**Significado:** proceso de enlazar objetos para que colaboren.

**En este proyecto:** `ControllerFactory` entrega controllers ya listos para trabajar.

### Navigator
**Significado:** componente central de navegación.

**En este proyecto:** decide que vista abrir, donde mostrarla y bajo que reglas de sesión o rol.

### Front Controller
**Significado:** pieza central que concentra decisiones de entrada a una parte del sistema.

**En este proyecto:** el `Navigator` y el `AppShell` cumplen este rol para vistas y shell.

### Guard / Navigation Guard
**Significado:** regla que bloquea el acceso a vistas o acciones no permitidas.

**Ejemplo:** auditoría o ciertas acciones solo para `ADMIN`.

### SessionState
**Significado:** estado global mínimo de sesión.

**En este proyecto:** guarda token, usuario, rol y flags de autenticación.

### Facade
**Significado:** objeto que ofrece una interfaz simple sobre varias piezas internas.

**Ejemplo real:** `UiFeedbackService` resume confirmaciones, info y errores; `*Service` resume interacciones con `*Api`.

### Strategy
**Significado:** patron para intercambiar comportamiento sin reescribir al consumidor.

**Ejemplo real:** perfiles tipograficos en `TypographyManager`.

### Query Object
**Significado:** objeto que encapsula filtros, búsqueda y paginación.

**Ejemplos reales:**
- `EstudiantesListQuery`
- `ReportesListQuery`
- `AuditoriaEventosQuery`
- `ClasesListQuery`

**Ventaja:** evita firmas largas tipo `listar(page, size, q, estado, seccion, ...)`.

### Presenter
**Significado:** componente que transforma datos crudos en texto y estados listos para la UI.

**Ejemplos reales:**
- `ReportesPresenter`
- `AuditoriaPresenter`

**Ventaja:** el controller deja de formatear fechas, badges y resumentes visuales.

### Mapper
**Significado:** componente que convierte un tipo de dato en otro.

**Diferencia con Presenter:** el mapper suele transformar estructura; el presenter traduce a algo visible para el usuario.

### Command
**Significado:** encapsular una acción en un objeto ejecutable.

**Ejemplos reales:**
- `UiCommand`
- `UiCommands`

**Ventaja:** nombrar acciones UI importantes y reducir ruido dentro del handler del controller.

### Drawer Coordinator
**Significado:** coordinador visual para abrir, cerrar y alternar contenido en drawers.

**Ejemplo real:** `DrawerCoordinator`.

**Ventaja:** evitar repetir `setVisible` y `setManaged` en cada controller.

### UiFeedbackService
**Significado:** fachada para feedback visual.

**Responsabilidad:** confirmaciones, alerts, información y mensajes consistentes.

---

## 3) Terminos de JavaFX y frontend desktop

### FXML
**Significado:** XML declarativo para construir vistas JavaFX.

**Ejemplos:** `AppShell.fxml`, `LoginView.fxml`, `ReportesView.fxml`.

### JavaFX CSS
**Significado:** sistema de estilos de JavaFX.

**Archivos claves:**
- `app-base.css`
- `shell.css`
- `table.css`
- `dialogs.css`

### Property
**Significado:** valor observable de JavaFX.

**Ejemplos:** `StringProperty`, `BooleanProperty`, `ObjectProperty<T>`.

### Binding
**Significado:** valor derivado que cambia automaticamente al cambiar otra propiedad.

### ObservableList
**Significado:** lista observable usada por tablas y combos.

### Controller
**Significado:** clase que conecta FXML con la lógica de interacción.

**Regla en este proyecto:** no hace HTTP directo ni construye URLs.

### JavaFX Application Thread
**Significado:** hilo principal de UI.

**Regla:** cualquier cambio visible en nodos JavaFX debe ocurrir aqui.

### Background I/O
**Significado:** trabajo de red o disco fuera del hilo de UI.

### FxExecutors
**Significado:** helper de concurrencia del proyecto para mandar trabajo a background y volver a UI.

### Polling
**Significado:** consultar periodicamente el estado de un proceso.

### Scheduled Executor
**Significado:** ejecutor capaz de correr tareas cada cierto intervalo.

### Listener / Event Handler
**Significado:** código que reacciona a eventos de teclado, raton, ventana o propiedades.

### Modal
**Significado:** ventana o capa que exige atención antes de volver al contenido base.

### Drawer
**Significado:** panel lateral o centrado para detalle o formulario.

### Backdrop
**Significado:** capa semitransparente o con blur que separa el drawer del resto de la UI.

### Message Box / Dialog
**Significado:** ventana de confirmación, información o advertencia.

### Tooltip
**Significado:** ayuda breve asociada a un control.

### Toast
**Significado:** mensaje temporal no bloqueante.

### Banner
**Significado:** mensaje persistente dentro de una pantalla.

### Empty State
**Significado:** estado visual cuando no hay resultados para mostrar.

### Full Screen
**Significado:** modo pantalla completa.

---

## 4) Terminos de tablas, filtros y formularios

### Filter Bar
**Significado:** bloque de filtros encima de una tabla.

### Data Table
**Significado:** tabla operativa con acciones por fila.

### Row Action
**Significado:** acción por fila como `Ver`, `Editar`, `Descargar`, `Reintentar`.

### Pagination
**Significado:** navegación por páginas de resultados.

### Query State
**Significado:** estado completo de una búsqueda: texto, filtros, página, size y sort.

### Sort
**Significado:** criterio de ordenamiento de una tabla.

### Inline Validation
**Significado:** validación mostrada cerca del campo que falla.

### Form Validation
**Significado:** reglas que impiden enviar formularios invalidos.

### Primary Action
**Significado:** acción principal de un bloque visual.

---

## 5) Terminos de frontend-backend

### ApiClient
**Significado:** cliente HTTP centralizado del frontend.

### DTO
**Significado:** `Data Transfer Object`.

### ApiResponse
**Significado:** envoltorio de exito del backend.

### ApiErrorResponse
**Significado:** envoltorio de error del backend.

### PageResponse
**Significado:** contrato de paginación.

### requestId
**Significado:** identificador único de una operación o error.

### errorCode
**Significado:** código semántico de error.

### Binary Download
**Significado:** descarga de archivos binarios como PDF, XLSX o DOCX.

---

## 6) Terminos de dominio visibles en la UI

### Módulo operativo
**Significado:** area funcional del sistema.

### Trazabilidad
**Significado:** capacidad de saber que paso, quien lo hizo y cuando.

### Reporte asíncrono
**Significado:** reporte que se solicita primero y se descarga después.

### Auditoría operativa
**Significado:** registro de acciones administrativas del sistema.

### Actor
**Significado:** usuario o proceso que ejecuto una acción.

### Resultado
**Significado:** desenlace de una acción auditada: `EXITO`, `ERROR`, `INFO`, etc.

### Reintento
**Significado:** nuevo intento de ejecutar una solicitud fallida.

---

## 7) Terminos raros que vale la pena memorizar

### Overengineering
**Significado:** meter complejidad sin necesidad real.

### Acoplamiento
**Significado:** grado en que una pieza depende de otra.

### Cohesion
**Significado:** que tan enfocada esta una pieza en una sola responsabilidad.

### Boilerplate
**Significado:** código repetitivo con poco valor semántico.

### Wiring
**Significado:** armado de dependencias.

### Lifecycle
**Significado:** ciclo de vida de una vista, ventana o servicio.

### Reactive UI
**Significado:** UI que responde al cambio de estado observable sin refresco manual agresivo.

---

## 8) Regla pedagogica final
Si un termino aparece en código, FXML, CSS o documentación y un estudiante no puede explicarlo en una frase corta:

- falta glosario,
- falta contexto,
- o el diseño ya se volvio innecesariamente complejo.

Este documento debe crecer junto con la arquitectura real del proyecto.


