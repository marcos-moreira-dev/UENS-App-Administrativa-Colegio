# 17_desktop_css_fx_guia_practica

- **Proyecto:** UENS Desktop (JavaFX)
- **Objetivo:** usar JavaFX CSS para un look profesional tipo **Windows 7 moderno**, sin sobre-animación ni cientos de líneas por pantalla.
- **Principio:** pocas reglas, bien pensadas, reutilizadas en todo el sistema.

---

## 1) Propósito
Este documento define una guía práctica para:

1. **Estructurar CSS** en archivos reutilizables.
2. Definir un look & feel sobrio con **profundidad ligera** (degradados suaves, bordes y sombras discretas).
3. Estilizar componentes clave:
 - Shell (sidebar/topbar/content)
 - botones
 - inputs
 - tablas
 - badges
 - dialogs/drawers
4. Establecer estados consistentes:
 - hover
 - focus
 - disabled
 - error
 - loading

Sin caer en:
- animaciones complejas,
- CSS duplicado por pantalla,
- o “efectos por capricho”.

---

## 2) Estructura recomendada de CSS
Ubicación:
- `desktop/src/main/resources/css/`

Archivos sugeridos:
1. `app-base.css` → tipografía, colores base, estilos globales.
2. `shell.css` → sidebar/topbar/content.
3. `controls.css` → botones, inputs, combos.
4. `table.css` → TableView + toolbar + paginación.
5. `dialogs.css` → dialogs + drawer.
6. `badges.css` → badges por estado.

Regla:
- Ninguna pantalla debe traer su propio CSS salvo casos excepcionales.

---

## 3) Carga de CSS (una vez)
**Regla:** cargar CSS al inicio (bootstrap) para que el estilo no se rompa al navegar.

- El Navigator trabaja dentro de una misma Scene (recomendación de `05_...`).
- Esto permite que los estilos sean globales.

---

## 4) Convenciones de clases CSS (canónicas)
Estas clases deben existir y usarse desde FXML:

Shell:
- `.app-shell`
- `.sidebar`
- `.sidebar-item`
- `.sidebar-item-active`
- `.topbar`
- `.content`

Botones:
- `.btn-primary`
- `.btn-secondary`
- `.btn-danger`
- `.btn-link`

Inputs:
- `.field`
- `.field-error`

Tablas:
- `.table`
- `.table-toolbar`
- `.pagination-bar`

Badges:
- `.badge`
- `.badge-success`
- `.badge-warning`
- `.badge-danger`
- `.badge-info`
- `.badge-muted`

Texto:
- `.text-h1`, `.text-h2`, `.text-body`, `.text-small`, `.text-mono`

Regla:
- La vista usa clases; el CSS define el estilo.

---

## 5) “Win7 moderno”: cómo lograrlo sin exagerar
### 5.1 Degradados suaves
- Úsalos en:
 - botones,
 - topbar,
 - headers de paneles.

No usarlos en:
- tablas completas,
- fondos grandes con muchos colores.

### 5.2 Bordes y relieve
- Borde sutil + fondo ligeramente distinto para “separación”.
- Sombra mínima para cards/drawers.

### 5.3 Transparencia prudente
- Evitar “glass” fuerte.
- Un overlay semitransparente solo para drawer/modal.

---

## 6) Estados visuales (reglas)
### 6.1 Hover
- cambio sutil (no parpadeo).
- no cambiar todo el layout.

### 6.2 Focus
- borde/halo con color Brand.
- foco siempre visible (accesibilidad).

### 6.3 Disabled
- opacidad suave + color muted.
- no ocultar.

### 6.4 Error
- borde rojo moderado.
- mensaje de error en texto pequeño.

### 6.5 Loading
- overlay sobre tabla o spinner pequeño en botón.

---

## 7) Botones (guía)
### 7.1 Primary
- degradado suave.
- texto blanco o de alto contraste.
- hover: un poco más claro.
- pressed: un poco más oscuro.

### 7.2 Secondary
- gris suave con borde.
- hover: borde un poco más marcado.

### 7.3 Danger
- rojo moderado.
- confirmación obligatoria en acciones destructivas.

### 7.4 Link
- sin fondo.
- subrayado en hover.

Regla:
- máximo 1 primary por zona.

---

## 8) Inputs (TextField/ComboBox/DatePicker)
- fondo claro.
- borde suave.
- focus con halo.
- error con borde rojo moderado.

Regla:
- no usar sombras pesadas en inputs.

---

## 9) Tablas (TableView)
### 9.1 Estilo recomendado
- header con fondo ligeramente más oscuro.
- filas alternadas muy sutiles (opcional).
- selección visible.

### 9.2 Toolbar
- búsqueda `q` con ícono.
- filtros con ComboBox.
- botón limpiar.

### 9.3 Paginación
- barra inferior sobria.
- texto small.

---

## 10) Badges
Usar badges para:
- estado entidades (ACTIVO/INACTIVO)
- reportes (PENDIENTE/EN_PROCESO/COMPLETADA/ERROR)
- auditoría (EXITO/ERROR/INFO/ADVERTENCIA)

Regla:
- badge siempre con texto, no solo color.

---

## 11) Dialogs y Drawers
### 11.1 Modal
- fondo panel + sombra.
- overlay oscuro suave.

### 11.2 Drawer
- panel lateral con borde y sombra suave.
- overlay para indicar “modo detalle”.

Regla:
- no animar con transiciones largas; si hay animación, que sea mínima (aparecer/deslizar en 120–180ms como máximo) y opcional.

---

## 12) Qué NO hacer (para evitar exceso)
- No crear un CSS por pantalla.
- No usar 10 colores “por moda”.
- No animar todo (tablas, overlays, etc.).
- No usar sombras fuertes tipo “neón”.
- No usar tipografías distintas en cada módulo.

---

## 13) Checklist de CSS
Antes de aceptar un cambio de CSS:
- [ ] ¿Se reutiliza una clase canónica?
- [ ] ¿No rompe contraste/legibilidad?
- [ ] ¿No agrega complejidad innecesaria?
- [ ] ¿Respeta look Win7 moderno?
- [ ] ¿No depende de hacks por pantalla?

---

## 14) Próximo documento
- `18_desktop_arquitectura_paquetes_y_capas_mvvm.md`

