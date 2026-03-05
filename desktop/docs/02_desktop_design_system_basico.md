# 02_desktop_design_system_basico

- **Proyecto:** UENS Desktop (JavaFX)
- **Objetivo:** definir un **sistema visual consistente** (design system) para una app administrativa seria.
- **UI:** FXML + JavaFX CSS
- **JDK:** Eclipse Temurin 21

---

## 1) Propósito
Este documento define el **mínimo profesional** de un design system para que:

1. La UI se vea **uniforme** en todos los módulos.
2. Se reduzca fricción al implementar (menos “decisiones por pantalla”).
3. Se mantenga escalable a futuro (más pantallas, más módulos, más equipo).
4. El frontend sea coherente con el **dominio UENS** y con los contratos del backend.

No es “solo colores”. Incluye:
- tipografía y jerarquía,
- espaciado y densidad,
- layout y grid,
- componentes base (botones, inputs, tablas, badges, dialogs),
- estados (hover/focus/disabled/loading/error),
- iconografía y reglas de assets,
- accesibilidad operativa (focus, teclado),
- y convenciones de copy (textos).

---

## 2) Principios de diseño (para una app administrativa)
1. **Claridad sobre decoración:** lo primero es que se entienda.
2. **Consistencia sobre creatividad:** misma interacción en todos los módulos.
3. **Densidad controlada:** mostrar información sin saturar.
4. **Feedback inmediato:** toda acción tiene respuesta (loading/success/error).
5. **Diseño por permisos:** la UI cambia por rol (`ADMIN`, `SECRETARIA`).
6. **Accesibilidad funcional:** foco visible, tab order, atajos básicos.

---

## 3) Tokens del sistema (variables base)
> Estos tokens se implementan en CSS como variables “con nombre”. En JavaFX no hay CSS variables nativas como en web, pero se logra con:
> - clases CSS bien definidas,
> - archivos CSS base,
> - y colores/fuentes centralizados.

### 3.1 Colores (semánticos)
- **Surface / fondo:** base de la app (shell y paneles)
- **Text primary:** texto principal
- **Text muted:** texto secundario
- **Border:** bordes suaves
- **Brand:** color institucional (botones primarios, links)
- **Success / Warning / Danger / Info:** estados

Regla:
- Los colores se usan por **intención**, no por gusto.

### 3.2 Espaciado (escala)
Escala recomendada (en px):
- `4, 8, 12, 16, 20, 24, 32`

Uso:
- padding de contenedor típico: `16`
- gap en formularios: `12`
- gap entre secciones: `24`

### 3.3 Radios (bordes redondeados)
- Controles: `6`
- Cards/Paneles: `10`
- Diálogos: `12`

### 3.4 Sombra (jerarquía)
- Panel: sombra suave (casi imperceptible)
- Diálogo: sombra un poco más marcada

Regla:
- sombras discretas; la jerarquía la da el layout.

---

## 4) Tipografía (jerarquía en desktop)
> La fuente exacta se definirá en el doc `03_...`, aquí definimos jerarquía.

### 4.1 Escala tipográfica
- **H1 (título de pantalla):** grande, peso medio
- **H2 (sección):** medio, peso medio
- **Body (texto normal):** estándar
- **Small (ayuda/metadata):** más pequeño y “muted”

### 4.2 Reglas
- Un solo estilo para títulos de pantalla.
- Metadatos (ids, requestId, timestamps) en small/mono opcional.
- Evitar párrafos largos: UI administrativa = frases cortas.

---

## 5) Layout base (shell) y grid
### 5.1 Shell
- Sidebar fija (módulos)
- Topbar (usuario/rol, acciones globales)
- Content area (pantalla actual)

### 5.2 Grid práctico
- Usar **contenedores** con ancho máximo (no estirar todo siempre).
- Columnas recomendadas:
  - listas/tablas: 1 columna principal + panel lateral opcional
  - formularios: 1 columna (simple) o 2 columnas (cuando hay muchos campos)

### 5.3 Breakpoints (desktop)
No se trabaja como responsive web, pero sí:
- la ventana puede redimensionarse.
- mínimo: evitar que se rompa la UI con scrolls razonables.

Regla:
- La tabla crece, el header se mantiene.

---

## 6) Componentes base (biblioteca interna)
> No depende de librerías externas: son patrones de JavaFX + CSS.

### 6.1 Botones
Tipos:
- **Primary:** acción principal (Crear, Guardar)
- **Secondary:** acción alternativa (Cancelar)
- **Tertiary/Link:** acciones suaves (Ver detalles)
- **Danger:** acciones destructivas (Inactivar)

Estados:
- normal
- hover
- pressed
- disabled
- loading (con spinner pequeño)

Reglas:
- Máximo 1 botón primary por zona.
- Destructivo siempre con confirmación.

### 6.2 Campos de texto
Tipos:
- TextField (texto)
- PasswordField
- TextArea (observaciones)

Estados:
- normal
- focus (borde/halo)
- error (borde + mensaje)
- disabled

Reglas:
- Errores se muestran cerca del campo.
- No “gritar” con rojo en toda la pantalla.

### 6.3 Selectores
- ComboBox (catálogos: estado, rol, parcial)
- DatePicker (fechas)

Reglas:
- Catálogos deben estar controlados por enums del backend.

### 6.4 Tablas (DataTable)
Partes:
- toolbar: búsqueda `q` + filtros
- TableView
- footer: paginación + contador

Estados:
- loading (skeleton simple o overlay)
- empty (mensaje + CTA)
- error (banner + retry)

Reglas:
- Acciones por fila en columna final.
- Ordenamiento visible.

### 6.5 Badges / chips
Para:
- estado `ACTIVO/INACTIVO`
- estados de reportes (`PENDIENTE`, `EN_PROCESO`, ...)
- resultado auditoría (`EXITO`, `ERROR`, ...)

Regla:
- Color semántico + texto corto.

### 6.6 Cards
Para:
- dashboard KPIs
- resumen de solicitud de reporte

Regla:
- cards homogéneas, padding 16.

### 6.7 Banners / alerts
Tipos:
- info
- success
- warning
- danger

Uso:
- errores de backend
- confirmaciones

Regla:
- mostrar `requestId` en modo detalle (expandible).

### 6.8 Dialogs
- confirmación (acciones destructivas)
- errores técnicos (500)
- “sin permisos” (403) si no es toast

Regla:
- confirmar antes de inactivar.

### 6.9 Toasts
Para:
- éxito corto
- info rápido
- warning leve

Regla:
- no usar toast para errores que requieren acción.

### 6.10 Progress / loading
- Spinner pequeño en botones
- Overlay de carga en tablas
- Progreso para reportes (estado asíncrono)

Regla:
- Nada de spinner infinito sin contexto.

---

## 7) Estados globales de interacción (UX)
### 7.1 Loading
- Bloquear botones de submit.
- Mantener formularios con datos (no limpiar).

### 7.2 Error
- Mostrar `message` del backend.
- Si 409: tratar como conflicto de negocio.
- Si 500: mensaje genérico + `requestId`.

### 7.3 Disabled
- No ocultar todo: deshabilitar con tooltip si ayuda.

### 7.4 Focus y teclado (mínimo serio)
- Foco visible.
- Tab order coherente.
- Enter para submit en login/formularios (si aplica).

---

## 8) Iconografía y assets
### 8.1 Uso de iconos por entidad
Assets actuales en `desktop/assets/`:
- Estudiante, Docente, Sección, Asignatura, Clase, Calificación, Representante, Usuario, logo.

Reglas:
- Un icono por módulo en sidebar.
- Tamaño consistente (ej. 20–24px).
- No mezclar estilos diferentes en la misma pantalla.

### 8.2 Naming
- Evitar espacios en nombres de archivo en assets finales.
  - ejemplo recomendado: `representante_legal.png` en vez de `Representante legal.png`

---

## 9) Copywriting (textos) – guía rápida
- Botones: verbo + objeto ("Crear estudiante", "Guardar", "Descargar")
- Errores: lenguaje humano + detalle técnico opcional
- Confirmaciones: “¿Seguro que deseas…?” + consecuencia

Regla:
- Usar vocabulario del dominio (doc 01).

---

## 10) Plantillas FXML (cómo se ve el sistema)
Plantillas base (para reutilizar):

1. `AppShell.fxml` – layout general
2. `ListViewTemplate.fxml` – tabla + filtros + paginación
3. `FormDialogTemplate.fxml` – create/edit
4. `DetailDrawerTemplate.fxml` – detalle rápido
5. `AsyncJobsTemplate.fxml` – reportes

Regla:
- No copiar/pegar FXML descontrolado: usar plantillas y especializar.

---

## 11) Checklist de consistencia (antes de crear una nueva pantalla)
- [ ] ¿La pantalla respeta el rol? (botones/acciones)
- [ ] ¿Usa el mismo patrón de tabla o formulario?
- [ ] ¿Usa el mismo estilo de títulos y espaciado?
- [ ] ¿Maneja loading/error/empty?
- [ ] ¿Muestra mensajes del backend correctamente?
- [ ] ¿No inventa reglas fuera del dominio?

---

## 12) Próximo documento
- `03_desktop_tipografia_y_escala_visual.md`

Notas:
- La versión exacta de JavaFX y librerías se definirá más adelante, pero el design system debe ser estable desde ya.


