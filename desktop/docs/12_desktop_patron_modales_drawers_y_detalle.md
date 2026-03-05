# 12_desktop_patron_modales_drawers_y_detalle

- **Proyecto:** UENS Desktop (JavaFX)
- **UI:** FXML + JavaFX CSS
- **Arquitectura:** MVVM + Navigator
- **Objetivo:** definir cuándo usar **modal**, **drawer** (panel lateral) o **pantalla** para detalle/edición.

---

## 1) Propósito (por qué importa)
En UENS, la operación diaria consiste en:
- navegar listados,
- abrir detalle,
- editar,
- confirmar,
- y volver al listado.

Si cada módulo usa un patrón distinto (a veces nueva escena, a veces ventana suelta, a veces diálogo), se pierde:
- consistencia,
- velocidad,
- y control de errores.

Este documento define patrones reutilizables para:
- detalle,
- create/edit,
- confirmaciones,
- y vistas técnicas.

---

## 2) Opciones de presentación
### 2.1 Modal (Dialog)
Ventana/diálogo que bloquea interacción con el fondo.

Uso ideal:
- confirmaciones,
- formularios cortos,
- acciones atómicas.

Ventajas:
- enfoque total.
- UX clara para “terminar o cancelar”.

Riesgos:
- se vuelve molesto si todo es modal.

### 2.2 Drawer (panel lateral)
Panel que se abre en el lado derecho (o izquierdo) sobre la vista principal.

Uso ideal:
- detalle rápido,
- edición sin perder contexto del listado,
- comparar información (lista a la izquierda, detalle a la derecha).

Ventajas:
- mantiene contexto.
- muy útil en admin.

Riesgos:
- si el formulario es largo, el scroll se vuelve pesado.

### 2.3 Pantalla (navegación en Content Area)
Vista completa en el content host (Shell).

Uso ideal:
- flujos largos o complejos,
- reportes asíncronos,
- auditoría,
- pantallas que requieren mucho espacio.

Ventajas:
- espacio suficiente.
- mejor para filtros complejos.

Riesgos:
- perder contexto si se usa para todo.

---

## 3) Regla principal (decisión rápida)
### 3.1 “Tamaño y complejidad”
- **Si es pequeño y atómico:** Modal.
- **Si es detalle/edición rápida mientras veo tabla:** Drawer.
- **Si es un flujo completo con estado (polling, filtros fuertes):** Pantalla.

### 3.2 “¿Necesito ver la tabla al mismo tiempo?”
- Sí → Drawer.
- No → Modal o Pantalla.

---

## 4) Patrón recomendado por tipo de caso
### 4.1 CRUD típico (la mayoría)
- Listado: pantalla principal.
- Detalle: drawer.
- Edit: drawer o modal (según longitud).
- Create: modal si es corto, drawer si es mediano.

### 4.2 Estudiantes (módulo estrella)
- Listado: pantalla.
- Detalle: drawer.
- Edit: drawer (porque es más rico).
- Asignar sección vigente: modal pequeño o sección dentro del drawer.

### 4.3 Calificaciones
- Listado filtrable: pantalla.
- Crear/Editar calificación: modal (si campos son pocos) o drawer.

### 4.4 Secciones/Asignaturas/Clases (ADMIN)
- Listado: pantalla.
- Create/Edit: modal o drawer (según densidad).

### 4.5 Reportes asíncronos
- Pantalla dedicada (historial + estado + descarga).
- Detalle de solicitud: drawer o diálogo “detalle”.

### 4.6 Auditoría
- Pantalla dedicada (tabla + filtros fuertes).
- Detalle de evento: drawer.

---

## 5) Plantillas FXML reutilizables
Ubicación sugerida:
- `fxml/templates/`

### 5.1 `ConfirmDialogTemplate.fxml`
- título
- mensaje
- botón cancel
- botón confirmar (danger)

### 5.2 `FormDialogTemplate.fxml`
- header + body (GridPane) + footer

### 5.3 `DrawerTemplate.fxml`
- overlay oscuro opcional
- panel lateral con:
  - header (título + cerrar)
  - content (scroll)
  - footer (acciones)

### 5.4 `DetailPanelTemplate.fxml`
- solo lectura (labels), pensado para “Ver”

Regla:
- Los módulos especializan plantillas, no reinventan estructura.

---

## 6) Comportamiento de cierre (UX)
### 6.1 Cierre seguro
Si hay cambios sin guardar:
- confirmar “Descartar cambios”.

### 6.2 Cierre en loading
- si `loading=true` durante submit, bloquear cerrar para evitar estados corruptos.

### 6.3 Escape/Enter
- `ESC` cierra modal/drawer si no hay cambios.
- `ENTER` en formularios dispara submit cuando es seguro.

---

## 7) Manejo de errores dentro de modal/drawer
Se aplican reglas del doc 09/11:

- 400/422 → errores inline.
- 409 → banner dentro del modal/drawer.
- 403 → mensaje y mantener abierto.
- 500 → diálogo de error con “detalle técnico” + requestId.

Regla:
- Nunca cerrar el modal/drawer automáticamente en error.

---

## 8) Layout y densidad (Win7 moderno)
- Drawer con borde y sombra suave.
- Modal con padding generoso.
- Headers consistentes (mismo tamaño de título).

Regla:
- no abusar de sombras.

---

## 9) Acciones recomendadas en el footer
### 9.1 Formulario
- Cancelar (secondary)
- Guardar (primary)

### 9.2 Detalle
- Editar (si rol permite)
- Cerrar

### 9.3 Acciones destructivas
- Siempre confirm dialog.

---

## 10) Checklist de implementación
- [ ] Hay plantillas FXML (confirm/form/drawer).
- [ ] CRUD usa patrón estable (list → drawer/modal).
- [ ] Cierre confirma si hay cambios.
- [ ] Loading bloquea cierre.
- [ ] Errores no cierran el panel.
- [ ] Acciones por rol.

---

## 11) Próximo documento
- `13_desktop_flujo_estudiantes_modulo_estrella.md`

