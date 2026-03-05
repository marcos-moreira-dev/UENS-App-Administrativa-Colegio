# 00_desktop_indice_y_mapa_documental

- **Proyecto:** UENS Desktop (JavaFX)
- **Arquitectura objetivo:** MVVM
- **UI:** FXML + JavaFX CSS
- **Fuente de verdad:** backend UENS + BD PostgreSQL

---

## 1) Propósito
Este archivo es el índice y mapa documental del desktop. Sirve para:
- ubicar el contexto oficial
- saber que documentos gobiernan cada etapa
- distinguir entre arbol objetivo y estado real del repo

---

## 2) Estructura de carpetas actual y objetivo

- `desktop/`
  - `assets/` -> iconos PNG de trabajo y `logo.png`
  - `docs/` -> documentación del desktop
  - `uens-desktop/`
    - `pom.xml`
    - `src/main/java`
    - `src/main/resources`
    - `src/test/java`
    - `tools/scripts/dev`

Convencion recomendada para FXML:
- `fxml/shell/AppShell.fxml`
- `fxml/auth/LoginView.fxml`
- `fxml/dashboard/DashboardView.fxml`

---

## 3) Fuentes de contexto del repo

### 3.1 Negocio
- `docs/01_levantamiento_informacion_negocio.md`
- `docs/02_levantamiento_requerimientos.md`
- `docs/03_modelo_conceptual_dominio.md`
- `docs/04_reglas_negocio_y_supuestos.md`
- `docs/05_glosario_alcance_y_limites.md`

### 3.2 Contrato frontend-backend
- `backend/docs/api/API_ENDPOINTS.md`
- `backend/docs/backend_v_1/19_backend_v_1_contexto_integracion_y_diseno_frontend.md`

### 3.3 Seguridad, reportes y auditoria
- `backend/docs/backend_v_1/09_backend_v_1_seguridad_documentacion_y_despliegue_minimo.md`
- `backend/docs/backend_v_1/10_backend_v_1_reporte_solicitudes_cola_simple_db_queue.md`
- `backend/docs/backend_v_1/17_backend_v_1_auditoria_operativa_y_reporte_admin.md`
- `backend/docs/backend_v_1/18_backend_v_1_acid_transacciones_consistencia_backend.md`

### 3.4 Base de datos
- `db/V2/docs/Diagramas y query de creacion/V2_3FN.sql`
- `db/V2/seeds/99_reset_demo_3FN.sql`
- `db/V2/seeds/02_seed_demo_3FN.sql`
- `db/V2/tools/03_select_recetario_postgresql_3FN.sql`

Regla práctica:
- duda de negocio -> `docs/01..05`
- duda de endpoint -> `backend/docs/api/API_ENDPOINTS.md`
- duda de relaciones -> `db/.../V2_3FN.sql`

---

## 4) Índice de documentos desktop
Todos viven en `desktop/docs/`.

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
- `21_desktop_roadmap_impl.md`
- `22_desktop_arbol_archivos_completo_sugerido.md`
- `23_desktop_glosario_frontend_y_arquitectura.md`
- `24_desktop_patrones_diseno_usados_y_recomendados.md`
- `25_desktop_patrones_diseno_ejemplos_antes_despues.md`
- `26_desktop_criterios_decision_arquitectonica_frontend.md`

---

## 5) Etapas de implementacion

### Etapa 0
- dominio
- contratos
- errores

Docs:
- `01`
- `08`
- `09`

### Etapa 1
- shell
- sesión
- login
- navigator

Docs:
- `05`
- `06`
- `07`
- `18`
- `21`

### Etapa 2
- design system
- CSS
- assets

Docs:
- `02`
- `03`
- `04`
- `17`

### Etapa 3
- componentes reutilizables

Docs:
- `10`
- `11`
- `12`

### Etapa 4
- estudiantes

### Etapa 5
- calificaciones

### Etapa 6
- reportes asíncronos

### Etapa 7
- auditoria

---

## 6) Estado real del repo
- Ya existe `desktop/uens-desktop/` como módulo Maven real.
- Ya existe `pom.xml` agregador en la raiz.
- Ya existe Maven Wrapper en la raiz.
- Ya existe un scaffold inicial validado con Maven.

Desde ahora, esta documentación debe leerse distinguiendo dos cosas:
- **objetivo final**
- **estado real ya materializado**


