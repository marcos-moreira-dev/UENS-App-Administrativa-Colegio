# 04_desktop_colores_iconografia_y_assets

- **Proyecto:** UENS Desktop (JavaFX)
- **UI:** FXML + JavaFX CSS
- **Objetivo:** definir paleta, iconografía, reglas de assets y naming.
- **Look & feel:** administrativo sobrio con inspiración **Windows 7** (ligeros degradados, biseles suaves, nada “flat” extremo).

---

## 1) Propósito
Este documento define:

1. **Paleta de color** y su uso semántico (no por gusto).
2. **Look & feel** “tipo Win7” (profesional, con profundidad ligera).
3. Reglas de **iconografía** (tamaño, estilo, consistencia).
4. Reglas de **assets** (ubicación, naming, licencias, versiones).
5. Convenciones para **CSS** (clases y estados) que se aplican en toda la app.

---

## 2) Principios visuales (Win7 moderno, prudente)
1. **Profundidad ligera:** usar degradados suaves y sombras discretas para jerarquía.
2. **Bordes suaves:** contornos sutiles para separar paneles.
3. **No “flat” extremo:** botones y cards pueden tener un leve relieve.
4. **No “glass” exagerado:** evitar transparencias pesadas (cansan y complican).
5. **Semántica primero:** colores para estado (ok/warn/error/info), no para decorar.

---

## 3) Paleta de color (semántica)
> Los valores exactos se ajustarán cuando se defina el CSS final, pero el **mapa semántico** es estable.

### 3.1 Neutros (base)
- **Surface (app):** fondo general del shell.
- **Panel (cards):** paneles sobre el fondo.
- **Border:** bordes suaves.
- **Text primary / muted:** texto.

### 3.2 Brand (institucional)
- **Brand primary:** botones principales, links y foco.
- **Brand dark/light:** variantes para hover/pressed.

### 3.3 Estados
- **Success:** confirmación de operación.
- **Warning:** advertencia (no bloqueante).
- **Danger:** error/acción destructiva.
- **Info:** notificación neutral.

Reglas:
- El estado se comunica con **badge + color + texto**, no solo con color.
- Evitar saturación: usar tonos moderados.

---

## 4) Estilo Win7: degradados y relieves (dónde sí aplican)
### 4.1 Botones
- **Primary:** degradado vertical suave (top ligeramente más claro).
- **Secondary:** degradado aún más sutil o gris suave.
- **Pressed:** invertir o oscurecer ligeramente.

### 4.2 Topbar / Sidebar
- Sidebar: fondo ligeramente más oscuro que el contenido.
- Topbar: separación con borde inferior y sombra mínima.

### 4.3 Cards y paneles
- Panel con fondo claro + borde suave.
- Sombra casi imperceptible.

### 4.4 Inputs
- Bordes suaves.
- Focus con halo discreto (brand).
- Error con borde rojo moderado + mensaje.

---

## 5) Iconografía (reglas)
### 5.1 Fuentes de iconos
En este proyecto se prioriza iconografía **local (PNG)** por módulo.

Carpeta actual:
- `desktop/assets/`

Íconos por entidad (referencia actual):
- Asignatura, Calificación, Clase, Docente, Estudiante, Representante legal, Sección, Usuario sistema administrativo, logo.

### 5.2 Tamaños estándar
- Sidebar icon: **20–24px**
- Botón con icon: **16–18px**
- Header de módulo: **24–32px** (si se usa)

### 5.3 Consistencia de estilo
- Misma familia visual en toda la app.
- No mezclar “flat” con “3D” duro.
- Preferir sombras suaves y bordes claros (Win7).

### 5.4 Reglas de uso
- Un icono por módulo en menú.
- Iconos en botones solo cuando aporten claridad.
- Evitar iconos redundantes en tablas (mejor acciones claras).

---

## 6) Naming de assets (regla: sin espacios, sin tildes)
Los nombres actuales tienen espacios y tildes. Para un proyecto serio, se recomienda migrar a:

- `asignatura.png`
- `calificacion.png`
- `clase.png`
- `docente.png`
- `estudiante.png`
- `representante_legal.png`
- `seccion.png`
- `usuario_sistema_administrativo.png`
- `logo.png`

Reglas:
- lowercase
- snake_case
- sin espacios
- sin caracteres especiales

**Nota:** si por ahora no quieres renombrar, se puede soportar, pero a futuro conviene estandarizar.

---

## 7) Estructura recomendada de recursos (a mediano plazo)
Para separar “assets de diseño” de “recursos de runtime”, se recomienda:

- `desktop/src/main/resources/images/`
 - `icons/` (png)
 - `brand/` (logo, variantes)
 - `illustrations/` (si se agregan)

Y mantener `desktop/assets/` como carpeta de trabajo/entrada.

Regla práctica:
- Lo que usa la app en runtime debe vivir en `src/main/resources/...`.

---

## 8) Licencias y origen de assets
### 8.1 Iconos actuales
- Si fueron creados por ti o generados por IA: documentar origen.
- Si vienen de terceros: asegurar licencia compatible.

### 8.2 Regla de proyecto
- Todo asset debe tener:
 - origen
 - licencia
 - fecha

Lugar recomendado:
- `desktop/assets/ASSETS_SOURCES.md`

---

## 9) CSS: clases canónicas (para no duplicar estilos)
Definir clases globales (no por pantalla):

- `.app-shell`
- `.sidebar`
- `.topbar`
- `.content`
- `.card`
- `.panel`

Botones:
- `.btn-primary`
- `.btn-secondary`
- `.btn-danger`
- `.btn-link`

Badges:
- `.badge-success`
- `.badge-warning`
- `.badge-danger`
- `.badge-info`
- `.badge-muted`

Inputs:
- `.field`
- `.field-error`

Tablas:
- `.table`
- `.table-toolbar`

Regla:
- Cada nueva vista debe reutilizar estas clases.

---

## 10) Estados visuales (UX)
Se definen estados visuales consistentes:

- Hover: cambio sutil (no parpadeo).
- Focus: halo brand moderado.
- Disabled: desaturación y opacidad suave.
- Loading: overlay o spinner pequeño.
- Error: borde + texto, sin pantallas rojas.

---

## 11) Checklist de assets y estilo
Antes de agregar un nuevo icono/imagen:
- [ ] ¿Respeta el estilo Win7 (sombra/borde suave)?
- [ ] ¿Está en PNG y se ve bien a 20–24px?
- [ ] ¿Nombre en snake_case sin espacios?
- [ ] ¿Origen/licencia documentados?
- [ ] ¿Se guardó en `resources/images/...` si es runtime?

---

## 12) Próximo documento
- `05_desktop_layout_shell_y_navegacion.md`

Notas:
- El estilo “Win7 moderno” se implementará con degradados suaves y sombras discretas en `17_desktop_css_fx_guia_practica.md`.

