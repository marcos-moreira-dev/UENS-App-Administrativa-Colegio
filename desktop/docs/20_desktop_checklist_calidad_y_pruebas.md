# 20_desktop_checklist_calidad_y_pruebas

- **Proyecto:** UENS Desktop (JavaFX)
- **Objetivo:** checklist honesto de calidad funcional, arquitectónica y documental.

---

## 1) Propósito
Este checklist sirve para:

1. validar que la app sea confiable para operación administrativa;
2. detectar incoherencias entre arquitectura, UX y documentación;
3. dejar claro que significa "listo para iterar" en esta base.

---

## 2) Checklist de arquitectura

### 2.1 Reglas duras
- [x] Ningun controller hace HTTP directo.
- [x] Ningun controller construye `*Api`.
- [x] Ningun controller construye servicios por su cuenta.
- [x] Los servicios se resuelven desde `AppContext` / `ApplicationServices`.
- [x] Existe un `ApiClient` único.
- [x] Existe un `Navigator` único.
- [x] Fonts y CSS se cargan una vez.
- [x] Todo I/O va en background.
- [x] La UI solo se actualiza en FX thread.

### 2.2 Dependencias
- [x] `view` no depende de `api.client` ni de `api.modules.*Api`.
- [x] `api` no conoce JavaFX.
- [x] `application` no usa `Node`, `Control` o `Scene`.
- [x] Reportes y Auditoría ya usan `Presenter/Mapper` donde habia mezcla de formato visual.
- [x] Los listados complejos ya usan `Query Object`.

Pendiente estructural:
- [ ] seguir extrayendo presenter/mapper a módulos CRUD secundarios si crecen más.

---

## 3) Checklist de sesión y seguridad
- [x] Login guarda token y usuario.
- [x] `auth/me` resuelve sesión real.
- [x] Logout limpia token y usuario.
- [x] Cierre por `X` intenta logout remoto y limpia sesión local.
- [x] `401` devuelve al login.
- [x] `403` no destruye sesión, solo bloquea la operación.
- [x] El menu y la navegación respetan rol.

---

## 4) Checklist de contratos API
- [x] Se usa `ApiResponse<T>.data`.
- [x] Los listados leen `PageResponse.items`.
- [x] Los filtros envian solo parámetros útiles.
- [x] El cliente interpreta `requestId`, `errorCode` y `message`.
- [x] Descargas y reportes binarios siguen el contrato real del backend.

---

## 5) Checklist de UX global
- [x] Jerarquía tipografica consistente.
- [x] Un solo primary button por zona principal.
- [x] Estados loading / empty / error diferenciados.
- [x] Drawers y modales con patron consistente.
- [x] Tooltips explican utilidad operativa real.
- [x] Los formularios usan controles adecuados al dominio (`ComboBox`, `Spinner`, `DatePicker`, controles buscables).

---

## 6) Checklist de textos e i18n
- [x] La base soporta `es` y `en`.
- [x] El idioma por defecto es `es`.
- [x] Existe infraestructura i18n operativa.
- [ ] Todo texto global ya vive en bundle.
- [ ] Toda la UI visible ya fue normalizada con acentos correctos.
- [x] La documentación refleja que la migración i18n sigue parcial.

---

## 7) Checklist funcional por módulos

### 7.1 Dashboard
- [x] Resume la operación con KPIs reales.
- [x] No depende de datos mock.

### 7.2 Estudiantes
- [x] Listado, create, edit y cambio de estado funcionan.
- [x] Representante y sección se seleccionan con controles buscables.

### 7.3 Representantes / Docentes / Secciones / Asignaturas / Clases
- [x] CRUD consistente.
- [x] Estados y permisos por rol coherentes.
- [x] Controles del formulario reflejan el dominio.

### 7.4 Calificaciones
- [x] Filtros por estudiante, clase y parcial.
- [x] Nota validada.
- [x] Parcial se maneja como control numérico acotado.

### 7.5 Reportes
- [x] Solicitud asíncrona.
- [x] Polling controlado.
- [x] Descarga solo cuando el backend lo permite.
- [x] Ruta o evidencia de descarga visible.

### 7.6 Auditoría
- [x] Filtros fuertes.
- [x] requestId visible.
- [x] Reporte administrativo de auditoría integrado.

---

## 8) Pruebas automaticas minimas serias
- [x] `ApiClient`
- [x] parseo de contratos
- [x] polling
- [x] ViewModels críticos
- [x] utilidades transversales con lógica relevante

Pendiente todavía:
- [ ] controllers con flujos integrados;
- [ ] pruebas de interacción UI más cercanas a runtime.

---

## 9) Criterio de "listo para iterar"
El desktop puede considerarse listo para seguir creciendo cuando:

- la arquitectura documentada coincide con el código real;
- los controllers no conocen la capa HTTP;
- la sesión y permisos funcionan;
- los módulos nucleares operan con UX consistente;
- los textos visibles están bien escritos;
- los docs no prometen componentes o reglas que ya no existen.

Estado actual:
- el frente arquitectónico principal ya esta alineado;
- la deuda visible más clara que queda es terminar de normalizar copy e i18n.


