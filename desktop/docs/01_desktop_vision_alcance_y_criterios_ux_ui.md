# 01_desktop_vision_alcance_y_criterios_ux_ui

- **Proyecto:** UENS Desktop (JavaFX)
- **Arquitectura:** MVVM
- **UI:** FXML + JavaFX CSS
- **JDK:** Eclipse Temurin 21
- **Fecha de referencia:** 2026-02-27

---

## 1) Propósito
Este documento fija la **visión del frontend** y los **criterios de UX/UI** para que la aplicación administrativa:

1. Sea consistente con el **dominio del negocio** (UENS fase 1).
2. Sea consistente con los **contratos reales de la API**.
3. Mantenga un diseño sobrio, claro y mantenible (sin sobre-animación ni “UI por capricho”).
4. Permita construir pantallas rápido con FXML sin perder arquitectura MVVM.

---

## 2) Contexto del negocio (por qué existe la app)
El colegio opera con registros manuales y herramientas dispersas. Eso genera:

- **Duplicidad de registros** (estudiantes repetidos por errores de transcripción).
- **Control deficiente de cupos** por sección.
- **Consulta lenta** (información repartida en cuadernos/archivos/WhatsApp).
- **Baja trazabilidad administrativa** (no se sabe quién cambió qué y cuándo).
- **Dificultad de consulta académica** (clases por sección, calificaciones por parcial).
- **Control de acceso no centralizado**.

La app administrativa debe traducir eso en experiencia:
- búsqueda rápida,
- filtros claros,
- validaciones obvias,
- acciones restringidas por rol,
- y reportes asíncronos con historial.

---

## 3) Usuarios y roles (impacto directo en UX)
En fase 1 hay dos roles operativos:

- **SECRETARIA** (operación diaria):
  - gestiona estudiantes, representantes, docentes (datos generales), calificaciones;
  - consulta secciones/asignaturas/clases;
  - crea reportes genéricos.

- **ADMIN** (operación + configuración + supervisión):
  - todo lo anterior;
  - crea/edita catálogos académicos (asignaturas, secciones, clases);
  - cambia estados sensibles;
  - accede a auditoría;
  - reintenta reportes fallidos.

**Criterio UX clave:**
- La UI debe estar diseñada **por permisos**, no solo por páginas.
- La SECRETARIA no debe “tropezar” con botones que no puede usar.

---

## 4) Lenguaje del dominio (vocabulario obligatorio)
Para evitar confusión y saturación de contexto, el frontend usa exactamente estos términos:

- **Estudiante**: niño matriculado (edad operativa 6–13; edad derivada).
- **Representante legal**: adulto responsable del estudiante.
- **Sección**: grado + paralelo + año lectivo + cupo + estado.
- **Asignatura**: catálogo por grado (convención: `nombre + grado`).
- **Clase**: oferta concreta de una asignatura en una sección.
  - En UI, “horario” se representa como: `díaSemana + horaInicio + horaFin`.
- **Calificación**: nota por **estudiante + clase + parcial (1/2)**.
- **Usuario sistema administrativo**: cuenta para operar el sistema.
- **Estado**: `ACTIVO | INACTIVO`.

**Regla semántica:**
- En fase 1, calificaciones se registran por **clase**, no por asignatura directa.

Fuentes de verdad del vocabulario:
- `docs/05_glosario_alcance_y_limites.md`
- `docs/03_modelo_conceptual_dominio.md`

---

## 5) Alcance UX/UI de fase 1 (qué sí y qué no)
### 5.1 Incluido (fase 1)
- Login + sesión (JWT) + menú por rol.
- Dashboard con KPIs.
- CRUD operativos: estudiantes, representantes, docentes (parcial), calificaciones.
- Consulta y (según rol) CRUD: secciones, asignaturas, clases.
- Reportes asíncronos: crear → ver estado → descargar.
- Auditoría (solo ADMIN): listado + filtros + solicitud de reporte.

### 5.2 Fuera de alcance (fase 1)
- Pagos, asistencia, notificaciones.
- Matriz de permisos granular.
- Historial formal de cambios de sección.
- Reportes académicos avanzados (boletines, promedios complejos).

**Criterio:** si algo no está en el alcance, no se “simula” en UI.

---

## 6) Objetivos de UX (qué debe sentir el usuario)
1. **Rapidez percibida:** búsquedas y listados ágiles; feedback inmediato.
2. **Claridad:** formularios con etiquetas simples; errores explicables.
3. **Prevención de errores:** validaciones obvias antes de enviar.
4. **Confianza:** acciones sensibles confirmadas; trazabilidad visible (requestId en errores).
5. **Consistencia:** mismas reglas de layout, tablas y formularios en todos los módulos.
6. **Accesibilidad funcional:** navegación por teclado, foco visible, tab order coherente.

---

## 7) Criterios de UI (estilo y consistencia)
### 7.1 Estilo visual
- Sobrio y profesional (administrativo).
- Iconografía por entidad (assets locales).
- Animación mínima: solo micro-efectos (hover/focus) vía CSS.

### 7.2 Consistencia de layout
- **Shell fijo:** Sidebar + Topbar + Content.
- **Módulos** en el mismo orden de navegación.
- **Acciones** siempre en ubicaciones predecibles:
  - “Crear” arriba a la derecha o encima de la tabla.
  - “Editar/Ver” por fila.
  - “Cambiar estado” solo cuando aplique y solo ADMIN.

### 7.3 Consistencia de tablas
- Columna de acciones alineada.
- Filtros y búsqueda `q` con patrón único.
- Paginación y ordenamiento visibles.
- Estado vacío con call-to-action.

### 7.4 Consistencia de formularios
- Campos obligatorios marcados.
- Validación en vivo (suave) + validación final al enviar.
- Errores del backend se muestran cerca del campo o en banner.

---

## 8) Criterios de integración con la API (impacto UX)
La UI debe respetar los contratos:

- **Éxito:** `ApiResponse<T>` → siempre leer `data`.
- **Error:** `ApiErrorResponse` → mostrar `message`, loguear `errorCode` y `requestId`.
- **Paginación:** `PageResponseDto` con `items`.

Manejo UX por HTTP:
- `401` → sesión expirada/ inválida → volver a login (limpiar token).
- `403` → sin permisos → mensaje claro (no “error interno”).
- `409` → conflicto de negocio → mostrar `message` y mantener formulario abierto.
- `500` → error técnico → mensaje genérico + `requestId`.

---

## 9) Patrones de pantalla (plantillas FXML recomendadas)
Estas plantillas serán FXML desde el inicio:

1. **LoginView.fxml**
   - login + password + botón.

2. **AppShell.fxml**
   - sidebar (módulos) + topbar (usuario/rol) + content area.

3. **ListView.fxml (base por módulo)**
   - barra de búsqueda + filtros + tabla + paginación.

4. **FormDialog.fxml / Drawer.fxml (base)**
   - formulario create/edit con validación.

5. **AsyncJobsView.fxml (Reportes)**
   - crear solicitud + historial + estado + descarga.

6. **AuditLogView.fxml (Auditoría ADMIN)**
   - tabla con filtros fuertes + solicitud de reporte.

---

## 10) Antipatrones (cosas que NO se deben hacer)
- Diseñar sin rol y esconder botones “después”.
- Tratar reportes como descarga inmediata (son asíncronos).
- Tablas sin paginación real.
- Formularios que borran campos al fallar (409/400).
- UI que ignora `message`/`errorCode`/`requestId`.
- CSS con estilos duplicados por vista sin variables base.

---

## 11) Definición de “MVP visual” (orden recomendado)
1. Login + AppShell + sesión + menú por rol.
2. Dashboard.
3. Plantilla de tabla + plantilla de formulario.
4. Estudiantes (módulo estrella).
5. Calificaciones.
6. Reportes asíncronos.
7. Auditoría ADMIN.

---

## 12) Enlaces de contexto para Codex (cuando implemente)
- Dominio:
  - `docs/01_levantamiento_informacion_negocio.md`
  - `docs/03_modelo_conceptual_dominio.md`
  - `docs/05_glosario_alcance_y_limites.md`

- Contrato API:
  - `backend/docs/api/API_ENDPOINTS.md`

- Integración y criterios:
  - `backend/docs/backend_v_1/19_backend_v_1_contexto_integracion_y_diseno_frontend.md`

---

## 13) Resultado esperado
Al terminar este documento, todo el equipo (y Codex) debe poder responder:

- ¿Qué problemas del negocio resuelve esta UI?
- ¿Qué módulos son prioritarios y por qué?
- ¿Qué términos del dominio se usan y cuáles se evitan?
- ¿Qué patrones visuales se repiten en toda la app?
- ¿Cómo se comporta la UX ante errores y permisos?

Siguiente documento recomendado:
- `02_desktop_design_system_basico.md`


