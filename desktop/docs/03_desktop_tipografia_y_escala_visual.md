# 03_desktop_tipografia_y_escala_visual

- **Proyecto:** UENS Desktop (JavaFX)
- **UI:** FXML + JavaFX CSS
- **JDK:** Eclipse Temurin 21
- **Nota:** la versión exacta de JavaFX se definirá más adelante.

---

## 1) Propósito
Definir un sistema tipográfico **gratuito y libre** (apto para uso comercial y distribución) y una **jerarquía visual** consistente para:

- títulos de pantallas,
- secciones,
- texto normal,
- ayudas/metadata,
- y texto técnico (IDs, `requestId`, timestamps).

La tipografía debe:
- verse bien en desktop,
- mantener legibilidad en tablas densas,
- y ser fácil de integrar en JavaFX (cargar desde recursos y aplicar por CSS).

---

## 2) Fuentes recomendadas (libres y seguras para cualquier uso)
Estas opciones son de uso ampliamente aceptado y licencias permisivas:

### 2.1 Fuente principal (UI)
**Inter** (SIL Open Font License 1.1 – OFL)
- Muy legible en interfaces.
- Excelente para tablas y formularios.

Fallback recomendado:
- **Noto Sans** (SIL OFL) como respaldo universal.

### 2.2 Fuente técnica (monoespaciada)
**JetBrains Mono** (SIL OFL)
- Para `requestId`, IDs, rutas, timestamps y datos “técnicos”.

Fallback:
- **Noto Sans Mono** (SIL OFL).

> Regla: usar mono **solo** para “datos técnicos”; no para toda la app.

---

## 3) Organización de archivos (fonts como assets)
Ubicación real recomendada en este repo:
- `desktop/uens-desktop/src/main/resources/assets/fonts/`

Estructura sugerida:
- `assets/fonts/Inter/static/Inter_18pt-Regular.ttf`
- `assets/fonts/Inter/static/Inter_18pt-Medium.ttf`
- `assets/fonts/Inter/static/Inter_18pt-SemiBold.ttf`
- `assets/fonts/JetBrains_Mono/static/JetBrainsMono-Regular.ttf`

Licencias (obligatorio en proyecto serio):
- `assets/fonts/Inter/OFL.txt`
- `assets/fonts/JetBrains_Mono/OFL.txt`

**Convención:** no usar espacios en nombres.

---

## 4) Carga de fuentes en JavaFX (una sola vez)
La carga debe hacerse **una vez** en el arranque de la app (por ejemplo al iniciar el `AppShell` o el bootstrap de aplicación).

### 4.1 Regla de arquitectura
- El **bootstrap** y el **ThemeManager** deben garantizar:
  1) registrar fonts desde `resources`,
  2) cargar CSS base,
  3) aplicar la familia activa a la Scene,
  4) y luego navegar a login/shell.

Esto evita que al cambiar escenas/vistas se “pierda” el look.

### 4.2 Carga típica
- Usar `Font.loadFont(InputStream, size)` para registrar la familia.
- Resolver la familia real devuelta por JavaFX y reutilizarla por CSS/inline style.
- Soportar perfiles tipográficos (`INTER`, `SOURCE_SANS_3`, `IBM_PLEX_SANS`) sin tocar FXML.

---

## 5) Jerarquía tipográfica (escala visual)
La app es administrativa: prioriza densidad controlada y legibilidad.

### 5.1 Tamaños base
- **Base (Body):** 13–14px
- **Small (metadata/ayuda):** 11–12px
- **Caption (tags/footnotes):** 10–11px

### 5.2 Títulos
- **H1 (título de pantalla):** 20–22px, SemiBold
- **H2 (sección):** 16–18px, SemiBold o Medium
- **H3 (sub-sección):** 14–15px, Medium

### 5.3 Tablas
- Texto de celda: 12.5–13px
- Header de tabla: 12.5–13px, Medium/SemiBold
- Metadata de paginación: 11–12px

### 5.4 Datos técnicos (mono)
- IDs / requestId / timestamps: 11–12px

---

## 6) Pesos y estilos permitidos
Para mantener consistencia y evitar combinaciones raras:

- Regular
- Medium
- SemiBold

Evitar:
- Light/Thin (se pierden en pantallas comunes)
- Bold extremo (solo si es estrictamente necesario)

---

## 7) Aplicación por CSS (clases canónicas)
Crear clases CSS para no repetir estilos en cada FXML.

Clases recomendadas:
- `.text-h1`
- `.text-h2`
- `.text-h3`
- `.text-body`
- `.text-small`
- `.text-caption`
- `.text-mono`

Regla:
- El FXML solo usa **clases**, no setea tamaños a mano por control.

---

## 8) Reglas de legibilidad (UX)
- Líneas cortas en formularios.
- Evitar bloques largos de texto.
- En tablas: preferir truncado con tooltip antes que romper layout.
- Mantener contraste: texto muted sigue siendo legible.

---

## 9) Checklist de tipografía por pantalla
Antes de dar por terminada una vista:
- [ ] ¿El título de pantalla usa `.text-h1`?
- [ ] ¿Las secciones usan `.text-h2`?
- [ ] ¿El cuerpo usa `.text-body`?
- [ ] ¿Metadata usa `.text-small` o `.text-caption`?
- [ ] ¿IDs / requestId usan `.text-mono`?
- [ ] ¿No hay tamaños hardcodeados en controles?

---

## 10) Próximo documento
- `04_desktop_colores_iconografia_y_assets.md`

Notas:
- La navegación por escenas/vistas con patrón **Navigator** se detallará en `18_desktop_arquitectura_paquetes_y_capas_mvvm.md`, pero aquí ya queda la regla: fonts y CSS se cargan una vez en el bootstrap para que el estilo no se rompa al navegar.

