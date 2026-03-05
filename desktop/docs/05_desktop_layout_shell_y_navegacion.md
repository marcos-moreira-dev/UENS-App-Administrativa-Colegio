# 05_desktop_layout_shell_y_navegacion

- **Proyecto:** UENS Desktop (JavaFX)
- **UI:** FXML + JavaFX CSS
- **Arquitectura:** MVVM + patrón **Navigator**
- **Objetivo:** definir el **shell** (layout base) y la **navegación** (menú por rol).

---

## 1) Propósito
Este documento define cómo se organiza la app en runtime:

1. **Shell** (AppShell): estructura visual persistente.
2. **Navegación**: cómo cambiamos vistas/escenas sin perder consistencia.
3. **Menú por rol**: qué módulos ve `ADMIN` vs `SECRETARIA`.
4. **Plantillas FXML** iniciales para arrancar rápido y mantener MVVM.

---

## 2) Concepto de Shell (AppShell)
El Shell es la “carcasa” estable de la app administrativa:

- **Sidebar**: módulos (navegación primaria)
- **Topbar**: sesión (usuario/rol), acciones globales
- **Content Area**: pantalla activa (cambia por navegación)

**Regla:** el Shell no se destruye al navegar; lo que cambia es el contenido.

---

## 3) FXML base (plantillas)
Ubicación recomendada:
- `desktop/src/main/resources/fxml/`

### 3.1 AppShell.fxml
Ruta sugerida:
- `fxml/shell/AppShell.fxml`

Estructura:
- `BorderPane` como raíz
  - `left`: Sidebar
  - `top`: Topbar
  - `center`: ContentHost (contenedor donde se inyecta la vista actual)

### 3.2 LoginView.fxml
Ruta sugerida:
- `fxml/auth/LoginView.fxml`

### 3.3 Plantillas de pantallas
- `fxml/templates/ListViewTemplate.fxml`
- `fxml/templates/FormDialogTemplate.fxml`

**Regla:** primero crear plantillas y luego especializar por módulo.

---

## 4) Patrón Navigator (navegación con buena arquitectura)
Objetivo: cambiar pantallas sin mezclar lógica de navegación dentro de cada controlador.

### 4.1 Responsabilidades
- **Navigator**:
  - conoce rutas de vistas (FXML) y crea instancias
  - controla el “host” donde se renderiza la vista (center del Shell)
  - aplica guardias por rol
  - centraliza navegación (no se navega desde cualquier lado)

- **ViewController (FXML Controller)**:
  - no debe decidir navegación por su cuenta
  - solicita navegación mediante Navigator (o un servicio de navegación)

### 4.2 Navegación por “ViewId”
Se recomienda un enum o catálogo:

- `ViewId.LOGIN`
- `ViewId.DASHBOARD`
- `ViewId.ESTUDIANTES`
- `ViewId.REPRESENTANTES`
- `ViewId.DOCENTES`
- `ViewId.SECCIONES`
- `ViewId.ASIGNATURAS`
- `ViewId.CLASES`
- `ViewId.CALIFICACIONES`
- `ViewId.REPORTES`
- `ViewId.AUDITORIA`

Y un registro:
- `ViewRegistry` → `ViewId -> (FXML path, required roles)`

### 4.3 Política de escenas
Dos opciones válidas (elegir una y mantenerla):

**Opción A (recomendada): 1 Stage + 1 Scene**
- el Shell vive en la misma Scene
- el Navigator reemplaza el contenido del `ContentHost`

**Opción B: múltiples Scenes**
- más compleja para estilos y estado

**Decisión:** usar **Opción A** para mantener consistencia de CSS/fonts y evitar duplicación.

---

## 5) Flujo de navegación (sesión)
### 5.1 Arranque
1. Bootstrap carga fonts + CSS base.
2. Navigator abre `LOGIN`.

### 5.2 Login exitoso
1. Guardar sesión (token + usuario + rol).
2. Construir menú por rol.
3. Navegar a `DASHBOARD`.

### 5.3 Sesión expirada
- `401` en cualquier request:
  1. limpiar sesión
  2. navegar a `LOGIN`

### 5.4 Sin permisos
- `403`:
  - mostrar mensaje “sin permisos” y mantener en la vista actual.

---

## 6) Menú por rol (navegación primaria)
### 6.1 Orden recomendado de módulos
1. Dashboard
2. Estudiantes
3. Representantes
4. Docentes
5. Secciones
6. Asignaturas
7. Clases
8. Calificaciones
9. Reportes
10. Auditoría (solo ADMIN)

### 6.2 Visibilidad por rol

**SECRETARIA** ve:
- Dashboard
- Estudiantes
- Representantes
- Docentes
- Secciones (consulta)
- Asignaturas (consulta)
- Clases (consulta)
- Calificaciones
- Reportes

**ADMIN** ve todo lo anterior +
- Auditoría

**Regla:**
- si el rol no puede acceder, el menú no lo muestra.
- adicionalmente, Navigator aplica guardia por seguridad.

---

## 7) Topbar (acciones globales)
Elementos recomendados:
- Nombre del sistema (UENS)
- Usuario actual (login)
- Rol actual
- Botón “Cerrar sesión”
- Indicador de estado (opcional): ping/health

Reglas:
- no saturar la topbar con acciones.
- acciones de cada módulo viven dentro del módulo.

---

## 8) Content Area (host) y navegación interna
### 8.1 ContentHost
- Contenedor único donde el Navigator inserta vistas.
- Puede ser un `StackPane` (para overlays de loading global si se necesita).

### 8.2 Navegación secundaria
Dentro de un módulo:
- preferir patrón de **vista principal + modal/drawer**
- evitar abrir “sub-escenas” innecesarias

Ejemplo Estudiantes:
- Listado (tabla) en la vista principal
- Detalle/Edición en drawer o dialog

---

## 9) Breadcrumbs (opcional, prudente)
En apps administrativas desktop, breadcrumbs pueden ayudar, pero no son obligatorios.

Recomendación:
- empezar sin breadcrumbs
- si luego hay rutas profundas, agregar breadcrumb simple en header de la vista

---

## 10) Integración con assets (iconos en menú)
- Sidebar muestra icono + texto por módulo.
- Iconos actuales en `desktop/assets/` (migrables a `resources/images/icons/`).

Reglas:
- tamaño 20–24px
- consistencia Win7 moderna

---

## 11) Checklist de implementación del Shell
- [ ] Existe `AppShell.fxml` con `BorderPane`.
- [ ] Existe `ContentHost` (center) donde se reemplazan vistas.
- [ ] Existe `Navigator` central.
- [ ] Menú se construye por rol desde `SessionState`.
- [ ] `401` redirige a login (limpia sesión).
- [ ] `403` muestra mensaje sin cambiar pantalla.
- [ ] CSS y fonts se cargan una sola vez.

---

## 12) Próximo documento
- `06_desktop_roles_y_visibilidad_de_acciones.md`

---

## Addendum 2026-03-01: runtime del Stage

Se agrega una regla de runtime para el `Stage` principal:

- Si existe sesión activa y la persona usuaria intenta salir con el botón `X` del sistema:
  - la app muestra un dialogo de confirmación,
  - informa que la sesión sera cerrada de forma obligatoria,
  - intenta `POST /api/v1/auth/logout` en modo best-effort,
  - y luego invalida la sesión local antes del cierre de la app.

- Motivo:
  - evitar tokens activos al cerrar por ventana,
  - y mejorar trazabilidad para auditoria.

- Acceso rápido de ventana:
  - `F11` alterna pantalla completa
  - el hint visible debe indicar: `Presiona F11 o Esc para salir de pantalla completa`

Notas:
- La estructura exacta de paquetes y clases del Navigator se detalla en `18_desktop_arquitectura_paquetes_y_capas_mvvm.md`.



