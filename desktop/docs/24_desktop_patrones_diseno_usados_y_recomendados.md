# 24_desktop_patrones_diseno_usados_y_recomendados

- **Proyecto:** UENS Desktop (JavaFX)
- **Arquitectura base:** MVVM + Navigator
- **Objetivo:** explicar que patrones ya están implementados, por qué se eligieron y bajo qué criterio conviene seguir usándolos.

---

## 1) Propósito
Este documento no busca adornar la arquitectura.

Su función es:

1. decir que patrones existen de verdad en el código;
2. justificar por que se adoptaron;
3. dejar criterios claros para decidir si un patron nuevo merece entrar o no;
4. servir como material docente para revisiones técnicas del frontend desktop.

---

## 2) Criterios de decision arquitectónica

Un patron solo debe entrar si cumple al menos dos de estos criterios:

### Criterio A: reduce repeticion real
Si la repeticion aparece en varios controllers, servicios o flujos, el patron puede ahorrar código y errores.

### Criterio B: mejora legibilidad
Si el código queda más facil de leer para estudiantes, revisores y mantenedores, el patron aporta valor docente y técnico.

### Criterio C: baja acoplamiento
Si separa mejor UI, sesión, backend o capa visual, el patron vale la pena.

### Criterio D: prepara crecimiento natural
Si el sistema probablemente crecera por la misma línea, el patron ayuda a no rehacer la arquitectura luego.

### Criterio E: no rompe simplicidad operativa
Si para entenderlo se necesita demasiada infraestructura extra, no conviene en esta etapa.

**Regla final:** si un patron agrega más lenguaje que solución, no entra.

---

## 3) Patrones ya implementados

### 3.1 MVVM
**Estado:** implementado y base del proyecto.

**Donde se ve:**
- `modules/*/view`
- `modules/*/viewmodel`
- `modules/*/application`

**Por que se usa:**
- separa UI y estado;
- facilita bindings de JavaFX;
- evita que la vista haga HTTP;
- deja el proyecto más ensenable.

**Criterio que cumple:** A, B, C, D.

### 3.2 Observer / Data Binding
**Estado:** implementado por JavaFX.

**Piezas reales:**
- `Property`
- `Binding`
- `ObservableList`

**Por que se usa:**
- la UI reacciona a cambios de estado sin código imperativo repetitivo;
- reduce sincronizacion manual.

**Criterio que cumple:** A, B, C.

### 3.3 Composition Root
**Estado:** implementado.

**Piezas reales:**
- `AppBootstrap`
- `AppContext`
- `ControllerFactory`

**Por que se usa:**
- centraliza el armado del sistema;
- evita creacion dispersa de dependencias;
- hace visible el wiring real del frontend.

**Criterio que cumple:** B, C, D.

### 3.4 Factory
**Estado:** implementado.

**Pieza real:** `ControllerFactory`.

**Por que se usa:**
- FXMLLoader recibe controllers ya cableados;
- evita construcción manual repetida.

**Criterio que cumple:** A, B, C.

### 3.5 Navigator / Front Controller de vistas
**Estado:** implementado.

**Piezas reales:**
- `Navigator`
- `ViewRegistry`
- `ViewId`

**Por que se usa:**
- toda navegación pasa por un punto común;
- se pueden aplicar guards de sesión y rol;
- evita que cada controller cargue FXML por su cuenta.

**Criterio que cumple:** A, B, C, D.

### 3.6 State
**Estado:** implementado.

**Pieza real:** `SessionState`.

**Por que se usa:**
- la sesión deja de ser una suma de variables sueltas;
- la UI puede reaccionar a login/logout de forma consistente.

**Criterio que cumple:** B, C, D.

### 3.7 Facade / Application Service
**Estado:** implementado.

**Piezas reales:** `EstudiantesService`, `ReportesService`, `AuditoriaService`, etc.

**Por que se usa:**
- la vista no hace HTTP;
- se centraliza cada caso de uso;
- se desacopla UI de infraestructura.

**Criterio que cumple:** A, B, C.

### 3.8 Query Object
**Estado:** implementado y expandido.

**Piezas reales:**
- `EstudiantesListQuery`
- `ReportesListQuery`
- `AuditoriaEventosQuery`
- `ClasesListQuery`
- `AsignaturasListQuery`
- `SeccionesListQuery`
- `CalificacionesListQuery`

**Por que se usa:**
- los listados con filtros dejaron de depender de firmas largas;
- hace visible el contrato de consulta;
- favorece evolucion futura de filtros.

**Criterio que cumple:** A, B, C, D.

### 3.9 Presenter
**Estado:** implementado en módulos con más carga visual.

**Piezas reales:**
- `ReportesPresenter`
- `AuditoriaPresenter`

**Por que se usa:**
- saca formato visible del controller;
- deja la lógica de presentacion en una pieza semántica;
- reduce ruido de badges, fechas y textos visibles.

**Criterio que cumple:** A, B, C.

### 3.10 Strategy ligera
**Estado:** implementado de forma acotada.

**Ejemplo real:** perfiles tipograficos en `TypographyManager`.

**Por que se usa:**
- cambiar fuente o perfil visual no requiere reescribir toda la UI.

**Criterio que cumple:** B, C, D.

### 3.11 Command
**Estado:** implementado.

**Piezas reales:**
- `UiCommand`
- `UiCommands`

**Módulos donde ya aporta valor:**
- `ReportesController`
- `AuditoriaController`
- `EstudiantesController`
- `ClasesController`

**Por que se usa:**
- encapsula acciones UI importantes;
- reduce lógica de handlers;
- hace más explicito el flujo.

**Criterio que cumple:** A, B, C.

### 3.12 Drawer Coordinator
**Estado:** implementado.

**Pieza real:** `DrawerCoordinator`.

**Por que se usa:**
- centraliza apertura/cierre de drawers;
- evita repetir `visible` y `managed`;
- mejora coherencia visual entre módulos.

**Criterio que cumple:** A, B, C.

### 3.13 UiFeedbackService
**Estado:** implementado.

**Pieza real:** `UiFeedbackService`.

**Por que se usa:**
- uniforma dialogs, confirmaciones y mensajes;
- evita `Alert` manual disperso;
- mejora consistencia de UX.

**Criterio que cumple:** A, B, C.

---

## 4) Patrones recomendados para seguir expandiendo

### 4.1 Presenter / Mapper en CRUD secundarios
**Recomendación:** seguir sacando formato visual de controllers de `Docentes`, `Secciones`, `Asignaturas` y similares.

**Por que todavia vale la pena:** esos controllers aún concentran demasiado texto visible y decisiones de presentacion.

### 4.2 Command en acciones de tabla más complejas
**Recomendación:** extender `Command` a flujos como reintentos, cambios de estado, refrescos fuertes y acciones compuestas.

### 4.3 Query Object en cualquier listado nuevo
**Regla práctica:** si un listado ya tiene búsqueda, paginación y tres filtros, no debe nacer con firma larga.

---

## 5) Patrones que no conviene meter todavia

### 5.1 Event Bus global
**No conviene** porque vuelve opaco el flujo y dificulta el aprendizaje del sistema.

### 5.2 CQRS formal
**No conviene** porque seria demasiada infraestructura para el tamano actual del frontend.

### 5.3 DI pesada
**No conviene** porque `AppBootstrap + AppContext + ControllerFactory` ya resuelven bien el wiring de esta etapa.

### 5.4 Repository adicional sobre la capa API
**No conviene** porque duplicaria responsabilidades entre `*Api` y `*Service`.

---

## 6) Criterios concretos para saber si un patron se acepta

### Un patron SI se acepta si:
- reduce ruido técnico visible;
- deja el código más didáctico;
- baja duplicacion;
- desacopla UI de backend o de detalles visuales;
- tiene un punto claro de reaprovechamiento.

### Un patron NO se acepta si:
- solo cambia nombres, pero no responsabilidades;
- mete más clases que valor real;
- es difícil de explicar sin teoria innecesaria;
- no se reaprovecha en al menos dos zonas del sistema.

---

## 7) Diagnostico objetivo actual

Hoy el frontend desktop ya tiene una base sana para fines productivos y educativos:

- MVVM real, no simulado;
- navegación centralizada;
- composition root clara;
- servicios de aplicación coherentes;
- polling y concurrencia encapsulados;
- consultas de listados mejor modeladas;
- drawers y feedback desacoplados;
- presentacion más limpia en módulos complejos.

Lo que aún queda como deuda natural:

- aplicar `Presenter` a más CRUD secundarios;
- seguir achicando controllers grandes;
- crecer en pruebas de controllers cuando el proyecto lo necesite.

---

## 8) Referencias reales del repo
- `app/AppBootstrap.java`
- `app/AppContext.java`
- `app/ControllerFactory.java`
- `nav/Navigator.java`
- `session/SessionState.java`
- `ui/fx/FxExecutors.java`
- `ui/command/UiCommand.java`
- `ui/command/UiCommands.java`
- `ui/drawer/DrawerCoordinator.java`
- `ui/feedback/UiFeedbackService.java`
- `modules/reportes/presenter/ReportesPresenter.java`
- `modules/auditoria/presenter/AuditoriaPresenter.java`
- `modules/estudiantes/application/EstudiantesListQuery.java`
- `modules/reportes/application/ReportesListQuery.java`
- `modules/auditoria/application/AuditoriaEventosQuery.java`

---

## 9) Relacion con otros documentos
- `18_desktop_arquitectura_paquetes_y_capas_mvvm.md`
- `23_desktop_glosario_frontend_y_arquitectura.md`
- `25_desktop_patrones_diseno_ejemplos_antes_despues.md`
- `26_desktop_criterios_decision_arquitectonica_frontend.md`


