# 18_desktop_arquitectura_paquetes_y_capas_mvvm

- **Proyecto:** UENS Desktop (JavaFX)
- **JDK:** Eclipse Temurin 21
- **UI:** FXML + JavaFX CSS
- **Arquitectura real:** MVVM + Navigator + Composition Root + Service Registry

---

## 1) Propósito
Este documento describe la arquitectura **real** del desktop, no una arquitectura aspiracional:

- cómo se organiza el código;
- dónde nace cada dependencia;
- qué puede conocer un controller;
- qué queda prohibido para mantener el sistema escalable;
- cómo se conectan sesión, navegación, feedback, tipografía y servicios de aplicación.

---

## 2) Principios duros
1. El controller FXML no hace HTTP ni construye clientes `*Api`.
2. El controller no arma servicios por su cuenta; los recibe desde `AppContext`.
3. `ApiClient` es único.
4. `Navigator` es único.
5. `SessionState` es el único estado global funcional.
6. La UI solo se actualiza en JavaFX Application Thread.
7. Todo I/O va en background.
8. La composición de dependencias vive en `app`.
9. Los flujos visuales comunes deben extraerse a utilidades reutilizables antes de repetirlos en más módulos.

---

## 3) Capas

### 3.1 Presentation
- `resources/fxml`
- `resources/css`
- `modules/*/view`
- `modules/*/viewmodel`
- `ui/*`

Responsabilidad:
- renderizar;
- bindear estado observable;
- traducir interacción del usuario a acciones UI;
- delegar lógica operativa a servicios/comandos/presenters.

### 3.2 Application
- `modules/*/application`
- `modules/*/presenter`
- `modules/*/query`
- `modules/*/mapper`

Responsabilidad:
- casos de uso;
- carga de referencias;
- polling;
- construcción de queries;
- formateo y adaptación hacia la UI cuando la complejidad ya no cabe en el controller.

### 3.3 Infrastructure
- `api.client`
- `api.contract`
- `api.modules.*`

Responsabilidad:
- HTTP;
- parseo JSON;
- descargas;
- manejo de errores remotos.

### 3.4 Shared / Common
- `common.*`
- `session.*`
- `nav.*`
- `app.*`

Responsabilidad:
- constantes;
- i18n;
- estado de sesión;
- bootstrap;
- navegación;
- composition root.

---

## 4) Composition Root real

### 4.1 Piezas actuales
- `AppBootstrap`
- `AppContext`
- `ApplicationServices`
- `ControllerFactory`

### 4.2 Regla actual
El sistema se inicializa así:

1. `AppBootstrap` crea el `AppContext`.
2. `AppContext.createDefault()` construye singletons base:
   - `ApiClient`
   - `SessionState`
   - `Navigator`
   - `UiFeedbackService`
   - `ResourceBundle`
3. `ApplicationServices.create(apiClient)` construye los servicios de aplicación por módulo.
4. `ControllerFactory` entrega el `AppContext` al controller.

Resultado:
- los controllers ya no crean `new ...Api(...)`;
- la composición se concentra en un solo sitio;
- cambiar implementaciones o añadir cross-cutting concerns es mucho más simple.

---

## 5) Service Registry
`ApplicationServices` es el registro de servicios de aplicación del desktop.

Incluye, entre otros:
- `AuthService`
- `DashboardService`
- `EstudiantesService`
- `EstudiantesReferenceDataService`
- `RepresentantesService`
- `DocentesService`
- `SeccionesService`
- `AsignaturasService`
- `ClasesService`
- `ClasesReferenceDataService`
- `CalificacionesService`
- `CalificacionesReferenceDataService`
- `ReportesService`
- `ReportesReferenceDataService`
- `AuditoriaService`

También expone fábricas ligeras para flujos con ciclo de vida corto, por ejemplo:
- `createReportePollingService()`

Regla:
- los servicios singleton viven en `ApplicationServices`;
- los objetos con estado efímero o cancelable pueden salir por método factoría.

---

## 6) Dependencias permitidas

### 6.1 Reglas reales
- `view` puede depender de:
  - `viewmodel`
  - `application`
  - `presenter`
  - `ui`
  - `common`
  - `session`
  - `nav`
- `view` no debe depender de:
  - `api.client`
  - `api.modules.*Api`
- `application` puede depender de:
  - `api.*`
  - `common`
- `api.*` no depende de JavaFX

### 6.2 Nota importante
Hoy algunos controllers todavía bindean tablas directamente a DTOs de `api.modules.*.dto`.

Eso es aceptable en esta base siempre que:
- el controller no invoque HTTP;
- el DTO no arrastre lógica de infraestructura;
- cuando la presentación crezca, se promueva a `presenter` o modelo de UI.

La regla profesional aquí es:
- **prohibido depender de `*Api` desde la vista**;
- **permitido usar DTOs de lectura de forma transitoria** mientras el módulo siga siendo manejable.

---

## 7) MVVM aterrizado al repo

### 7.1 View
- Controller FXML
- bindings
- eventos de botones
- activación de drawers/modales

### 7.2 ViewModel
- `Property`
- `ObservableList`
- textos derivados
- estados de loading, empty, next page, banner

### 7.3 Presenter / Mapper
Se usa cuando:
- hay demasiada transformación visual;
- hay que mapear estados técnicos a copy de UI;
- el controller empieza a mezclar formato y coordinación.

Actualmente ya existe en módulos como:
- `reportes`
- `auditoria`

### 7.4 Query Object
Se usa para listados con filtros/paginación y evita firmas largas de `listar(...)`.

Actualmente ya existe en módulos como:
- `estudiantes`
- `reportes`
- `auditoria`
- `clases`
- `asignaturas`
- `secciones`
- `calificaciones`

---

## 8) Navegación
Piezas base:
- `ViewId`
- `ViewRegistry`
- `Navigator`

Reglas:
- solo `Navigator` carga vistas;
- el shell no resuelve FXML a mano;
- los guards por sesión/rol viven en navegación y sesión;
- el cambio de vista puede activar efectos visuales, tooltips y artwork, pero no debe romper el desacople del módulo.

---

## 9) Concurrencia
Reglas:
- HTTP, lectura de referencias, polling y descargas van en background;
- actualizaciones visuales solo en FX thread.

Herramientas reales del repo:
- `FxExecutors`
- `Platform.runLater`
- `PauseTransition`
- servicios de polling específicos

Regla práctica:
- si un controller repite demasiado flujo asíncrono, extraer a soporte reusable antes de copiar por otro módulo.

---

## 10) UI reusable y cross-cutting
La app ya incorpora infraestructura reutilizable para no duplicar lógica transversal:

- `UiFeedbackService`
- `UiCommand` / `UiCommands`
- `DrawerCoordinator`
- `SearchableComboBoxSupport`
- `TypographyManager`
- `TooltipSupport`
- `ViewArtworkSupport`

Regla:
- si una interacción aparece en dos o más módulos y lleva estado/flujo propio, debe migrar a `ui/*`.

---

## 11) i18n real
El desktop ya tiene infraestructura `ResourceBundle` y soporta:
- `es` por defecto;
- `en` opcional por configuración.

Estado real:
- la infraestructura está activa;
- la migración completa de textos al bundle sigue siendo gradual.

Regla:
- nuevos textos globales deben priorizar bundle;
- módulos antiguos pueden convivir con texto directo mientras se migra, pero no se debe ampliar la deuda sin motivo.

---

## 12) Checklist de PR arquitectónico
- [ ] El controller no construye `*Api`.
- [ ] El controller consume servicios desde `AppContext`.
- [ ] No hay HTTP directo en la vista.
- [ ] `ApiClient` sigue siendo único.
- [ ] `Navigator` sigue siendo único.
- [ ] I/O en background; UI en FX thread.
- [ ] Si creció el formato visual, se evaluó `Presenter/Mapper`.
- [ ] Si crecieron filtros/paginación, se evaluó `Query Object`.
- [ ] La documentación sigue reflejando la implementación real.

