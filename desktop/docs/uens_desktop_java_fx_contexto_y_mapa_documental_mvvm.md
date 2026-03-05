# UENS Desktop (JavaFX) - Contexto, dominio y mapa documental (MVVM)

## 0) Contexto general del proyecto
Este repositorio contiene:

- **Backend UENS**: Spring Boot + PostgreSQL. Es la fuente de verdad del dominio operativo, validaciones, estados, roles, reportes asíncronos y auditoría.
- **Desktop UENS**: JavaFX. Es la aplicación administrativa que consume la API del backend.

Carpetas relevantes:
- `backend/` -> código + documentación del backend
- `db/` -> SQL, seeds y herramientas
- `desktop/` -> app JavaFX + documentación del frontend

Activos estaticos:
- `desktop/assets/` con iconos e ilustraciones por entidad
- `desktop/uens-desktop/src/main/resources/assets/` con fonts e imagenes runtime

Estilo visual objetivo:
- sobrio, moderno y consistente;
- CSS JavaFX para uniformidad;
- sin animacion ornamental innecesaria.

---

## 1) Contexto del dominio del negocio (UENS)
El desktop existe porque el negocio tiene problemas reales:

- registros dispersos;
- duplicidad;
- cupos;
- trazabilidad debil;
- consultas lentas.

### 1.1 Actores
- Secretaria / administrativo
- Docentes
- Representantes legales
- Estudiantes

### 1.2 Entidades base
- **Estudiante** -> asociado a 1 representante legal principal y 0..1 sección vigente
- **Representante legal** -> puede tener varios estudiantes
- **Sección** -> grado/paralelo/año lectivo + cupo máximo + estado
- **Asignatura** -> catálogo por grado
- **Clase** -> oferta concreta (sección + asignatura + horario + docente opcional + estado)
- **Calificación** -> por (estudiante + clase + parcial 1/2)
- **Usuario administrativo** -> login/rol/estado

### 1.3 Reglas clave
- edad 6-13 validada respecto a fecha de registro/matrícula;
- cupo por sección, máximo institucional 35;
- estados `ACTIVO/INACTIVO` bloquean operaciones nuevas;
- calificación por **clase**, no por asignatura directa;
- roles: `ADMIN` y `SECRETARIA`.

---

## 2) Entregable real
Diseñar e implementar el frontend desktop JavaFX con:

1. arquitectura **MVVM**;
2. estado observable para refresco reactivo de vistas;
3. documentación Markdown numerada para UX/UI, arquitectura, sesión, errores, componentes, reportes y auditoría;
4. coherencia fuerte con dominio y contratos API.

---

## 3) Fuentes de contexto oficiales

### 3.1 Dominio / negocio
- `docs/01_levantamiento_informacion_negocio.md`
- `docs/02_levantamiento_requerimientos.md`
- `docs/03_modelo_conceptual_dominio.md`
- `docs/04_reglas_negocio_y_supuestos.md`
- `docs/05_glosario_alcance_y_limites.md`

### 3.2 Backend
- `backend/docs/backend_v_1/00_backend_v_1_indice_y_mapa_documental.md`
- `backend/docs/backend_v_1/09_backend_v_1_seguridad_documentacion_y_despliegue_minimo.md`
- `backend/docs/backend_v_1/10_backend_v_1_reporte_solicitudes_cola_simple_db_queue.md`
- `backend/docs/backend_v_1/17_backend_v_1_auditoria_operativa_y_reporte_admin.md`
- `backend/docs/backend_v_1/18_backend_v_1_acid_transacciones_consistencia_backend.md`
- `backend/docs/backend_v_1/19_backend_v_1_contexto_integracion_y_diseno_frontend.md`
- `backend/docs/api/API_ENDPOINTS.md`

### 3.3 Base de datos
- `db/V2/docs/Diagramas y query de creacion/V2_3FN.sql`
- `db/V2/seeds/99_reset_demo_3FN.sql`
- `db/V2/seeds/02_seed_demo_3FN.sql`
- `db/V2/tools/03_select_recetario_postgresql_3FN.sql`

Regla práctica:
- si una decision de UI parece dudosa, primero se revisa negocio, contrato API y luego estructura de datos.

---

## 4) Principio rector del diseño
**La UI no inventa reglas.**

La API y el dominio ya definen:
- roles y permisos;
- estados;
- contratos (`ApiResponse`, `PageResponse`, errores y `requestId`);
- reportes asíncronos;
- auditoría.

El frontend agrega valor en:
- consistencia UX/UI;
- validaciones obvias;
- diseño por permisos;
- componentes reutilizables;
- manejo robusto de fallos.

---

## 5) MVVM aterrizado a JavaFX

### 5.1 Componentes y responsabilidades
- **View (FXML + Controller)** 
 Orquesta UI, bindings y eventos. No hace HTTP ni crea `*Api`.

- **ViewModel** 
 Expone estado observable con `Property`, `ObservableList` y `Bindings`.

- **Application Services** 
 Encapsulan acceso al backend y se resuelven desde `ApplicationServices`.

- **Presenter / Mapper** 
 Formatean texto o estados visuales cuando el controller ya no deberia hacerlo.

- **SessionState** 
 Mantiene token, usuario, rol y estado de autenticación.

### 5.2 Estado reactivo
- `StringProperty`
- `BooleanProperty`
- `ObjectProperty<T>`
- `ObservableList<T>`
- `Bindings`

Regla:
- si el UI debe refrescarse solo -> `Property` / `ObservableList`
- si el valor es derivado -> `Binding`

---

## 6) Etapas de diseño e implementacion

### Etapa 0 - Aterrizaje del dominio y contratos
- negocio: entender entidades, reglas y límites;
- frontend: fijar vocabulario y pantallas;
- API: asumir contratos reales.

### Etapa 1 - Shell + sesión JWT + permisos
- layout base;
- login;
- bootstrap via `auth/me`;
- menu por rol.

### Etapa 2 - Design system + assets + CSS
- tipografía;
- colores;
- iconografía;
- consistencia visual.

### Etapa 3 - Patrones reutilizables
- `TableView` estilizada con filtros y paginación;
- `DrawerCoordinator`;
- `UiFeedbackService`;
- `UiCommand` / `UiCommands`;
- `SearchableComboBoxSupport`;
- validación de formularios.

### Etapa 4 - Módulo estrella: Estudiantes
- listado;
- create/edit;
- cambio de estado;
- asignacion de sección vigente.

### Etapa 5 - Calificaciones
- filtros por estudiante/clase/parcial;
- nota validada;
- parcial como control numerico acotado.

### Etapa 6 - Reportes asíncronos
- solicitudes;
- polling;
- descarga binaria;
- reintento solo ADMIN.

### Etapa 7 - Auditoría
- tabla con filtros fuertes;
- requestId visible;
- reporte administrativo.

---

## 7) Mapa documental de `desktop/docs/`
- `00_desktop_indice_y_mapa_documental.md`
- `01_desktop_vision_alcance_y_criterios_ux_ui.md`
- `02_desktop_design_system_basico.md`
- `03_desktop_tipografia_y_escala_visual.md`
- `04_desktop_colores_iconografia_y_assets.md`
- `05_desktop_layout_shell_y_navegacion.md`
- `06_desktop_roles_y_visibilidad_de_acciones.md`
- `07_desktop_flujo_sesion_login_y_bootstrap.md`
- `08_desktop_cliente_api_contratos_y_dtos_ui.md`
- `09_desktop_manejo_errores_toasts_dialogos.md`
- `10_desktop_patron_tablas_filtros_paginacion.md`
- `11_desktop_patron_formularios_validacion.md`
- `12_desktop_patron_modales_drawers_y_detalle.md`
- `13_desktop_flujo_estudiantes_modulo_estrella.md`
- `14_desktop_flujo_calificaciones.md`
- `15_desktop_flujo_reportes_async.md`
- `16_desktop_flujo_auditoria_admin.md`
- `17_desktop_css_fx_guia_practica.md`
- `18_desktop_arquitectura_paquetes_y_capas_mvvm.md`
- `19_desktop_i18n_textos_y_mensajes.md`
- `20_desktop_checklist_calidad_y_pruebas.md`
- `23_desktop_glosario_frontend_y_arquitectura.md`
- `24_desktop_patrones_diseno_usados_y_recomendados.md`
- `25_desktop_patrones_diseno_ejemplos_antes_despues.md`
- `26_desktop_criterios_decision_arquitectonica_frontend.md`

---

## 8) Notas de coherencia importantes
- "Franja horaria" se representa como `diaSemana + horaInicio + horaFin`.
- La calificación es por **clase**, no por asignatura directa.
- Los roles cambian acciones, no solo pantallas.
- `403` = sin permisos; `401` = sesión invalida o expirada.
- `requestId` debe verse en detalle técnico.

---

## 9) Proximo paso recomendado
Mantener alineados primero:
1. `18_desktop_arquitectura_paquetes_y_capas_mvvm.md`
2. `19_desktop_i18n_textos_y_mensajes.md`
3. `20_desktop_checklist_calidad_y_pruebas.md`
4. `24/25/26` para criterios y patrones


