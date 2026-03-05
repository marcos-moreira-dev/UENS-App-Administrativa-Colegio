# 25_desktop_patrones_diseno_ejemplos_antes_despues

- **Proyecto:** UENS Desktop (JavaFX)
- **Objetivo:** mostrar con ejemplos cercanos al repo por que la arquitectura actual es mejor que la version previa con más lógica dispersa.

---

## 1) Propósito
Este documento no define teoria aislada.

Su meta es comparar:

1. problema inicial;
2. forma anterior de codificarlo;
3. forma actual recomendada;
4. beneficio practico para UENS Desktop.

---

## 2) Caso 1: Query Object

### Problema
Los listados administrativos crecian con demasiados parametros.

### Antes
```java
service.listar(page, size, q, estado, seccionId, docenteId, fechaDesde, fechaHasta);
```

### Después
```java
ClasesListQuery query = new ClasesListQuery(
        page,
        size,
        q,
        estado,
        seccionId,
        asignaturaId,
        docenteId,
        diaSemana);

service.listar(query);
```

### Beneficio
- menos errores de orden;
- firma más clara;
- mejor crecimiento de filtros;
- mejor material docente.

### Donde verlo
- `modules/clases/application/ClasesListQuery.java`
- `modules/reportes/application/ReportesListQuery.java`
- `modules/auditoria/application/AuditoriaEventosQuery.java`

---

## 3) Caso 2: Presenter

### Problema
El controller formateaba fechas, estados y textos visibles mezclados con eventos de UI.

### Antes
```java
private String formatEstado(String value) {
    if (value == null) {
        return "-";
    }
    return switch (value) {
        case "COMPLETADA" -> "Completada";
        case "ERROR" -> "Error";
        default -> value;
    };
}
```

### Después
```java
var presentation = presenter.presentDetail(detail);
detailViewModel.estadoProperty().set(presentation.estado());
```

### Beneficio
- controller más enfocado en flujo;
- formato visible reusable;
- más facil de entender para estudiantes.

### Donde verlo
- `modules/reportes/presenter/ReportesPresenter.java`
- `modules/auditoria/presenter/AuditoriaPresenter.java`

---

## 4) Caso 3: helper de concurrencia

### Problema
Muchos controllers repetian la misma coreografia de background + vuelta a UI.

### Antes
```java
FxExecutors.io().submit(() -> {
    var result = service.operación();
    FxThreading.runOnUiThread(() -> applyResult(result));
});
```

### Después
```java
FxExecutors.submitIo(
        () -> service.operación(),
        this::applyResult);
```

### Beneficio
- menos boilerplate;
- intencion más clara;
- menos errores de hilo.

### Donde verlo
- `ui/fx/FxExecutors.java`
- controllers de `Reportes`, `Auditoria`, `Estudiantes`, `Clases`, `Calificaciones`

---

## 5) Caso 4: Command

### Problema
Los handlers FXML crecian demasiado y cada accion quedaba mezclada con validación, loading y respuesta visual.

### Antes
```java
private void onGuardar() {
    loadingProperty.set(true);
    service.guardar(...);
}
```

### Después
```java
submitFormCommand = UiCommands.io(this::submitForm, this::applySaveResult);
submitFormCommand.execute();
```

### Beneficio
- acciones nombradas y reutilizables;
- menos codigo operativo en el handler;
- mejor lectura del flujo.

### Donde verlo
- `ui/command/UiCommand.java`
- `ui/command/UiCommands.java`
- `modules/reportes/view/ReportesController.java`
- `modules/clases/view/ClasesController.java`

---

## 6) Caso 5: Drawer Coordinator

### Problema
Cada controller con drawer repetia demasiados cambios de `visible` y `managed`.

### Antes
```java
drawerPane.setVisible(true);
drawerPane.setManaged(true);
detailContent.setVisible(false);
detailContent.setManaged(false);
formContent.setVisible(true);
formContent.setManaged(true);
```

### Después
```java
drawerCoordinator.showOnly(formContent);
```

### Beneficio
- menos ruido visual;
- menos riesgo de inconsistencias;
- apertura y cierre más uniformes.

### Donde verlo
- `ui/drawer/DrawerCoordinator.java`
- `modules/reportes/view/ReportesController.java`
- `modules/auditoria/view/AuditoriaController.java`

---

## 7) Caso 6: UiFeedbackService

### Problema
Las confirmaciones y mensajes estaban repartidos entre `Alert` manual, banners locales y confirm dialogs armados en cada controller.

### Antes
```java
Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
confirm.setTitle("Confirmar cambio de estado");
confirm.setHeaderText("Cambiar estado");
confirm.setContentText("Se cambiara el estado...");
if (confirm.showAndWait().filter(ButtonType.OK::equals).isEmpty()) {
    return;
}
```

### Después
```java
boolean confirmed = appContext.feedback().confirm(
        currentWindow(),
        "Confirmar cambio de estado",
        "Cambiar estado del docente",
        "Se cambiara el estado a INACTIVO.",
        "Confirmar",
        "Cancelar");
```

### Beneficio
- dialogs consistentes;
- menos codigo visual repetido;
- mejor mantenimiento de UX.

### Donde verlo
- `ui/feedback/UiFeedbackService.java`
- `app/AppWindowSupport.java`
- `modules/docentes/view/DocentesController.java`
- `modules/secciones/view/SeccionesController.java`

---

## 8) Caso 7: tipografia desacoplada

### Problema
La UI podia terminar atada a nombres de fuente hardcodeados o a familias no cargadas de verdad desde `resources`.

### Antes
```css
.root {
    -fx-font-family: "Segoe UI";
}
```

### Después
Se usa `TypographyManager` para cargar las fuentes reales del proyecto y `ThemeManager` aplica el perfil tipografico activo en runtime.

### Beneficio
- consistencia visual;
- posibilidad de cambiar perfil sin reescribir toda la UI;
- mejor control pedagogico de tipografia y theming.

### Donde verlo
- `ui/theme/TypographyManager.java`
- `ui/theme/TypographyProfile.java`
- `ui/theme/ThemeManager.java`

---

## 9) Criterio docente para elegir el patron correcto

### Usa Query Object si:
- el listado tiene busqueda;
- hay paginacion;
- hay tres o más filtros.

### Usa Presenter si:
- el controller formatea demasiado;
- hay badges, fechas o textos visibles complejos;
- el mismo formato aparece en varias vistas.

### Usa Command si:
- la accion tiene varios pasos;
- el handler FXML ya no cabe limpiamente;
- la misma accion necesita estructura repetible.

### Usa Drawer Coordinator si:
- la vista tiene más de un modo interno;
- se repite `visible/managed`;
- hay formularios y detalle compartiendo espacio.

### Usa UiFeedbackService si:
- hay confirmaciones;
- hay alerts informativos;
- hay mensajes operativos que deben verse iguales en varios módulos.

---

## 10) Lo que no se hizo a proposito
No se metio:

- Event Bus global
- CQRS formal
- DI pesada
- Repository extra sobre la capa API

**Motivo:** hoy agregarian más complejidad que valor.

---

## 11) Relacion con otros documentos
- `23_desktop_glosario_frontend_y_arquitectura.md`
- `24_desktop_patrones_diseno_usados_y_recomendados.md`
- `26_desktop_criterios_decision_arquitectonica_frontend.md`


