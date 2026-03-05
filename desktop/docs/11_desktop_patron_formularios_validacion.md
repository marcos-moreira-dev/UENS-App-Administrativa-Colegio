# 11_desktop_patron_formularios_validacion

- **Proyecto:** UENS Desktop (JavaFX)
- **UI:** FXML + JavaFX CSS
- **Arquitectura:** MVVM
- **Objetivo:** definir un patrón reusable para formularios (create/edit) con validación y manejo de errores coherente con el backend.

---

## 1) Propósito (negocio → UI)
En UENS fase 1, los formularios representan operaciones administrativas críticas:

- Registrar estudiantes evitando duplicados.
- Mantener consistencia de cupos y estados.
- Registrar calificaciones por clase y parcial.
- Configurar catálogos académicos (ADMIN).

Por eso el formulario debe:
- prevenir errores obvios,
- dar feedback rápido,
- y respetar la fuente de verdad (backend).

---

## 2) Principios de formularios (serios)
1. **No perder datos del usuario:** si falla, el formulario conserva el input.
2. **Validación en 2 capas:**
   - UI (obvia, inmediata),
   - backend (regla real, definitiva).
3. **Errores cerca de donde importan:** field errors junto al campo.
4. **Feedback claro:** mensajes cortos, concretos.
5. **Acciones seguras:** confirmación cuando haya impacto (ej. inactivar).
6. **Diseño por rol:** campos/acciones visibles según permisos.

---

## 3) Plantilla FXML (FormDialogTemplate)
### 3.1 Estructura sugerida
- Root: `BorderPane` o `VBox`
  - Header:
    - título (Crear/Editar)
    - subtítulo (opcional)
  - Body:
    - GridPane (campos)
    - Banner de error (opcional)
  - Footer:
    - Botón Cancelar
    - Botón Guardar (primary)

### 3.2 Layout de campos
- Formularios simples: 1 columna.
- Formularios largos: 2 columnas (pero sin saturar).

Regla:
- Mantener espaciado y tamaños del design system (`02_...`).

---

## 4) Estado del formulario (MVVM)
### 4.1 ViewModel (estado mínimo)
- `BooleanProperty loading`
- `ObjectProperty<ErrorInfo> error` (errores globales)
- `StringProperty bannerMessage` (para 409 o errores no mapeables)
- `MapProperty<String, String> fieldErrors` (campo → mensaje)

- `BooleanProperty canSubmit` (Binding derivado)

- `FormMode mode`:
  - CREATE
  - EDIT

### 4.2 Modelo de formulario (FormState)
Se recomienda un `FormState` con propiedades JavaFX por campo:
- `StringProperty nombres`
- `StringProperty apellidos`
- etc.

Regla:
- La vista se bindea a estas propiedades.

---

## 5) Validación en UI (capa 1)
### 5.1 Objetivo
Reducir fricción y prevenir requests inválidos.

### 5.2 Tipos de validación UI
1. **Requeridos:** no vacío.
2. **Formato:** email, teléfono.
3. **Rangos básicos:** grado 1..7, cupo 1..35, parcial 1..2.
4. **Fechas:** fechaNacimiento en el pasado; fechaDesde <= fechaHasta.

Reglas:
- UI valida **lo obvio**.
- La UI no intenta replicar reglas complejas de negocio si dependen de DB.

### 5.3 Momento de validación
- “Suave” mientras escribe (no molestar en cada tecla).
- “Fuerte” al intentar guardar.

Recomendación práctica:
- validar al perder foco + al submit.

---

## 6) Validación backend (capa 2) y mapeo de errores
El backend puede devolver:
- 400/422 (validación),
- 409 (conflicto de negocio),
- 403 (sin permisos),
- 500 (error técnico).

### 6.1 400/422
**Objetivo UI:** mostrar errores por campo.

Casos:
- Si backend envía detalle por campo (ideal): mapear a `fieldErrors`.
- Si no: usar `bannerMessage`.

### 6.2 409
**Objetivo UI:** conflicto de negocio.

Ejemplos:
- cupo excedido.
- duplicado detectado.
- docente no disponible.
- unicidad calificación (estudiante+clase+parcial).

Regla:
- Mostrar banner con `message`.
- No limpiar campos.

### 6.3 401
- sesión inválida/expirada.
- Comportamiento: logout + login.

### 6.4 403
- mostrar mensaje “No tienes permisos”.
- no cerrar formulario.

### 6.5 500
- diálogo/bloque de error con “detalle técnico” (requestId).
- permitir reintentar.

---

## 7) Botones y estados
### 7.1 Guardar
- Deshabilitado si `loading=true`.
- Deshabilitado si `canSubmit=false`.
- Muestra spinner pequeño en loading.

### 7.2 Cancelar
- Si `loading=true`, permitir cancelar solo si no rompe coherencia.
- En operaciones largas: bloquear cancel.

Regla UX:
- “Guardar” siempre a la derecha.

---

## 8) Create vs Edit (diferencias)
### 8.1 Create
- Campos vacíos.
- Al éxito:
  - cerrar dialog/drawer,
  - refrescar listado,
  - toast “Creado correctamente”.

### 8.2 Edit
- Pre-cargar datos.
- Al éxito:
  - cerrar,
  - refrescar,
  - toast “Actualizado correctamente”.

Regla:
- En edit, el ID no se edita (si se muestra, mostrarlo como metadata).

---

## 9) Formularios por entidad (consideraciones)
> Este bloque guía UX sin duplicar docs de cada módulo.

### 9.1 Estudiante
Campos típicos:
- nombres, apellidos
- fechaNacimiento
- representanteLegalId
- seccionId (opcional en create si se decide)

Validaciones UI:
- nombres/apellidos no vacíos
- fechaNacimiento en pasado

UX:
- selector de representante (buscar + seleccionar)
- selector de sección (mostrar grado/paralelo/año)

### 9.2 Representante legal
Campos:
- nombres, apellidos
- teléfono, correo

Validaciones:
- email forma válida
- teléfono formato básico

### 9.3 Docente
Campos:
- nombres, apellidos
- teléfono/correo opcionales

### 9.4 Sección (ADMIN)
Campos:
- grado (1..7)
- paralelo
- anioLectivo
- cupoMaximo (<=35)

Validaciones:
- cupo <= 35

### 9.5 Asignatura (ADMIN)
Campos:
- nombre
- area
- grado
- descripción

### 9.6 Clase (ADMIN)
Campos:
- seccionId, asignaturaId
- docenteId (opcional)
- diaSemana, horaInicio, horaFin

Validaciones:
- horaInicio < horaFin

### 9.7 Calificación
Campos:
- estudianteId
- claseId
- numeroParcial (1/2)
- nota
- fechaRegistro (opcional)
- observación (opcional)

Validación UI:
- nota en rango UI recomendado (por defecto 0..10) pero **fuente de verdad es backend**.

---

## 10) Patrones UX de selección (relaciones indirectas)
Cuando un formulario referencia otras entidades:

- Preferir selector con búsqueda (`q`) si hay muchos registros.
- Mostrar “resumen” del seleccionado:
  - Representante: nombres + teléfono
  - Sección: grado/paralelo/año
  - Clase: asignatura + sección + horario + docente

Regla:
- No obligar al usuario a memorizar IDs.

---

## 11) Confirmaciones y acciones destructivas
Acciones destructivas no deben ser “submit normal”:
- Cambiar estado a INACTIVO.

Regla:
- Confirm dialog con consecuencia.

---

## 12) Checklist de formulario (antes de cerrar)
- [ ] Campos bindeados a propiedades.
- [ ] `canSubmit` bloquea submit cuando falta data.
- [ ] Validación UI obvia implementada.
- [ ] Errores backend se muestran (inline o banner).
- [ ] 409 mantiene datos y muestra mensaje.
- [ ] 500 muestra requestId en detalle técnico.
- [ ] Éxito refresca listado.

---

## 13) Próximo documento
- `12_desktop_patron_modales_drawers_y_detalle.md`

