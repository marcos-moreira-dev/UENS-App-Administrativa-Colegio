# Diccionario de Datos

## Unidad Educativa Niñitos Soñadores (UENS)
### Versión V2 (3FN)

---

## 1. Información general

- **Proyecto:** Sistema administrativo escolar (UENS)
- **Versión de base de datos:** `V2_3FN`
- **Motor de base de datos:** PostgreSQL
- **Esquema:** `public`
- **Objetivo del documento:** describir la estructura física de datos de la versión V2 en Tercera Forma Normal (3FN), incluyendo tablas, campos, claves, relaciones y restricciones relevantes.

### 1.1 Alcance
Este documento cubre:
- Definición de tablas del dominio académico y del sistema administrativo.
- Diccionario técnico de campos.
- Relaciones principales entre entidades.
- Restricciones e índices relevantes.
- Notas de diseño y campos derivables no almacenados.

### 1.2 Fuentes de referencia
- Script DDL de la base de datos: `V2_3FN.sql`
- Diagrama relacional/ERD de la versión V2 (3FN)

---

## 2. Convenciones generales de modelado

- **PK estándar:** `pk_id BIGINT GENERATED ALWAYS AS IDENTITY`
- **Estados operativos usados en varias tablas:** `ACTIVO` / `INACTIVO`
- **Claves foráneas:** relaciones explícitas con restricciones `FOREIGN KEY`
- **Reglas de integridad:** reforzadas con `CHECK`, `UNIQUE` e índices
- **Normalización aplicada:** 3FN (atributos derivables removidos del almacenamiento físico)

---

## 3. Diccionario de datos por tabla

---

## 3.1 Tabla `usuario_sistema_administrativo`

**Descripción:** usuarios con acceso al sistema administrativo. Esta tabla se mantiene separada del dominio académico (estudiantes, docentes, etc.).

| Campo | Tipo | Nulo | Default | Clave / Restricción | Descripción |
|---|---|---:|---|---|---|
| `pk_id` | `BIGINT` (IDENTITY) | No | — | PK | Identificador único del usuario del sistema. |
| `nombre_login` | `VARCHAR(80)` | No | — | `UNIQUE` | Nombre de inicio de sesión. |
| `password_hash` | `TEXT` | No | — | — | Hash de contraseña (no texto plano). |
| `rol` | `VARCHAR(20)` | No | `'ADMIN'` | `CHECK` (`ADMIN`,`SECRETARIA`) | Rol del usuario del sistema. |
| `estado` | `VARCHAR(10)` | No | `'ACTIVO'` | `CHECK` (`ACTIVO`,`INACTIVO`) | Estado del usuario. |

**Notas:**
- Tabla aislada del dominio académico en esta versión.
- Preparada para autenticación/autorización del backend.

---

## 3.2 Tabla `representante_legal`

**Descripción:** personas responsables legales de estudiantes (padre, madre, tutor u otro representante).

| Campo | Tipo | Nulo | Default | Clave / Restricción | Descripción |
|---|---|---:|---|---|---|
| `pk_id` | `BIGINT` (IDENTITY) | No | — | PK | Identificador único del representante legal. |
| `nombres` | `VARCHAR(120)` | No | — | — | Nombres del representante. |
| `apellidos` | `VARCHAR(120)` | No | — | — | Apellidos del representante. |
| `telefono` | `VARCHAR(30)` | Sí | — | — | Teléfono de contacto. |
| `correo_electronico` | `VARCHAR(254)` | Sí | — | — | Correo electrónico de contacto. |

---

## 3.3 Tabla `seccion`

**Descripción:** sección académica identificada por grado + paralelo + año lectivo.

| Campo | Tipo | Nulo | Default | Clave / Restricción | Descripción |
|---|---|---:|---|---|---|
| `pk_id` | `BIGINT` (IDENTITY) | No | — | PK | Identificador único de la sección. |
| `grado` | `SMALLINT` | No | — | `CHECK` (1 a 7) | Grado académico de la sección. |
| `paralelo` | `VARCHAR(10)` | No | — | Parte de `UNIQUE` | Paralelo de la sección (ej. A, B). |
| `cupo_maximo` | `SMALLINT` | No | `35` | `CHECK` (>0 y <=35) | Cupo máximo permitido. |
| `anio_lectivo` | `VARCHAR(20)` | No | — | `CHECK` formato `YYYY-YYYY`; parte de `UNIQUE` | Año lectivo. |
| `estado` | `VARCHAR(10)` | No | `'ACTIVO'` | `CHECK` (`ACTIVO`,`INACTIVO`) | Estado de la sección. |

**Restricciones clave (lógicas/esperadas):**
- Unicidad de sección por combinación: `anio_lectivo + grado + paralelo`

**Notas de diseño:**
- **No** se almacena `cantidad_estudiantes_registrados`; se deriva por consulta (`COUNT`).

---

## 3.4 Tabla `docente`

**Descripción:** docentes registrados en el sistema.

| Campo | Tipo | Nulo | Default | Clave / Restricción | Descripción |
|---|---|---:|---|---|---|
| `pk_id` | `BIGINT` (IDENTITY) | No | — | PK | Identificador único del docente. |
| `nombres` | `VARCHAR(120)` | No | — | — | Nombres del docente. |
| `apellidos` | `VARCHAR(120)` | No | — | — | Apellidos del docente. |
| `telefono` | `VARCHAR(30)` | Sí | — | — | Teléfono de contacto. |
| `correo_electronico` | `VARCHAR(254)` | Sí | — | — | Correo electrónico. |
| `estado` | `VARCHAR(10)` | No | `'ACTIVO'` | `CHECK` (`ACTIVO`,`INACTIVO`) | Estado del docente. |

---

## 3.5 Tabla `asignatura`

**Descripción:** materias/asignaturas ofrecidas por grado.

| Campo | Tipo | Nulo | Default | Clave / Restricción | Descripción |
|---|---|---:|---|---|---|
| `pk_id` | `BIGINT` (IDENTITY) | No | — | PK | Identificador único de la asignatura. |
| `nombre` | `VARCHAR(120)` | No | — | Parte de `UNIQUE` | Nombre de la asignatura. |
| `area` | `VARCHAR(80)` | Sí | — | — | Área académica (ej. Ciencias, Lenguaje). |
| `descripcion` | `TEXT` | Sí | — | — | Descripción de la asignatura. |
| `grado` | `SMALLINT` | No | — | `CHECK` (1 a 7); parte de `UNIQUE` | Grado al que corresponde la asignatura. |
| `estado` | `VARCHAR(10)` | No | `'ACTIVO'` | `CHECK` (`ACTIVO`,`INACTIVO`) | Estado de la asignatura. |

**Restricción clave (lógica/esperada):**
- Unicidad por `nombre + grado`

---

## 3.6 Tabla `estudiante`

**Descripción:** estudiantes del sistema con relación obligatoria a representante legal y relación opcional a sección.

| Campo | Tipo | Nulo | Default | Clave / Restricción | Descripción |
|---|---|---:|---|---|---|
| `pk_id` | `BIGINT` (IDENTITY) | No | — | PK | Identificador único del estudiante. |
| `nombres` | `VARCHAR(120)` | No | — | — | Nombres del estudiante. |
| `apellidos` | `VARCHAR(120)` | No | — | — | Apellidos del estudiante. |
| `fecha_nacimiento` | `DATE` | No | — | — | Fecha de nacimiento. |
| `estado` | `VARCHAR(10)` | No | `'ACTIVO'` | `CHECK` (`ACTIVO`,`INACTIVO`) | Estado del estudiante. |
| `representante_legal_id` | `BIGINT` | No | — | FK → `representante_legal(pk_id)` | Representante legal asociado. |
| `seccion_id` | `BIGINT` | Sí | — | FK → `seccion(pk_id)` | Sección asignada (nullable). |

**Notas de diseño:**
- **No** se almacena `edad`; se calcula a partir de `fecha_nacimiento`.
- `seccion_id` nullable permite preregistro o estudiante aún no asignado.

---

## 3.7 Tabla `clase`

**Descripción:** bloque horario (unidad operativa) que relaciona sección, asignatura, docente y franja horaria.

| Campo | Tipo | Nulo | Default | Clave / Restricción | Descripción |
|---|---|---:|---|---|---|
| `pk_id` | `BIGINT` (IDENTITY) | No | — | PK | Identificador único de la clase/bloque horario. |
| `dia_semana` | `VARCHAR(15)` | No | — | `CHECK` valores permitidos | Día de la semana. |
| `hora_inicio` | `TIME` | No | — | — | Hora de inicio de la clase. |
| `hora_fin` | `TIME` | No | — | `CHECK` (`hora_fin > hora_inicio`) | Hora de fin de la clase. |
| `estado` | `VARCHAR(10)` | No | `'ACTIVO'` | `CHECK` (`ACTIVO`,`INACTIVO`) | Estado de la clase. |
| `seccion_id` | `BIGINT` | No | — | FK → `seccion(pk_id)` | Sección a la que pertenece el bloque. |
| `asignatura_id` | `BIGINT` | No | — | FK → `asignatura(pk_id)` | Asignatura del bloque. |
| `docente_id` | `BIGINT` | Sí | — | FK → `docente(pk_id)` | Docente asignado (nullable en fase 1). |

**Valores permitidos recomendados para `dia_semana`:**
- `LUNES`, `MARTES`, `MIERCOLES`, `JUEVES`, `VIERNES`, `SABADO`

**Restricción clave (lógica/esperada):**
- Unicidad operativa por combinación: `seccion_id + asignatura_id + dia_semana + hora_inicio + hora_fin`

**Notas:**
- En esta versión no necesariamente se fuerza por DB la coherencia entre `asignatura.grado` y `seccion.grado` (puede resolverse en backend/reglas). 
- La validación de solapamientos horarios puede quedar para backend o versión posterior.

---

## 3.8 Tabla `calificacion`

**Descripción:** registro de notas por estudiante, clase y parcial.

| Campo | Tipo | Nulo | Default | Clave / Restricción | Descripción |
|---|---|---:|---|---|---|
| `pk_id` | `BIGINT` (IDENTITY) | No | — | PK | Identificador único de la calificación. |
| `numero_parcial` | `SMALLINT` | No | — | `CHECK` (fase 1: 1 o 2) | Número de parcial. |
| `nota` | `NUMERIC(5,2)` | No | — | `CHECK` (`nota >= 0`) | Nota registrada. |
| `fecha_registro` | `DATE` | Sí | — | — | Fecha de registro. |
| `observacion` | `TEXT` | Sí | — | — | Observación adicional. |
| `estudiante_id` | `BIGINT` | No | — | FK → `estudiante(pk_id)` | Estudiante evaluado. |
| `clase_id` | `BIGINT` | No | — | FK → `clase(pk_id)` | Clase asociada a la calificación. |

**Restricción clave (lógica/esperada):**
- Unicidad por `estudiante_id + clase_id + numero_parcial`

---

## 3.9 Tabla `reporte_solicitud_queue`

**Descripción:** cola persistente de solicitudes de reportes asíncronos del backend.

| Campo | Tipo | Nulo | Default | Clave / Restricción | Descripción |
|---|---|---:|---|---|---|
| `pk_id` | `BIGINT` (IDENTITY) | No | — | PK | Identificador único de la solicitud de reporte. |
| `tipo_reporte` | `VARCHAR(100)` | No | — | — | Tipo funcional de reporte solicitado. |
| `estado` | `VARCHAR(20)` | No | — | `CHECK` (`PENDIENTE`,`EN_PROCESO`,`COMPLETADA`,`ERROR`) | Estado de procesamiento del worker. |
| `parametros_json` | `TEXT` | Sí | — | — | JSON de parámetros/filtros de entrada. |
| `resultado_json` | `TEXT` | Sí | — | — | JSON con resultado y metadata de archivo generado. |
| `error_detalle` | `TEXT` | Sí | — | — | último error técnico/funcional de procesamiento. |
| `solicitado_por_usuario` | `BIGINT` | Sí | — | — | Usuario administrativo que género la solicitud. |
| `intentos` | `INTEGER` | No | `0` | — | Número de intentos del worker. |
| `fecha_solicitud` | `TIMESTAMP` | No | `CURRENT_TIMESTAMP` | — | Fecha de creacion de la solicitud. |
| `fecha_actualizacion` | `TIMESTAMP` | No | `CURRENT_TIMESTAMP` | — | última fecha de cambio de estado. |

---

## 3.10 Tabla `auditoria_evento`

**Descripción:** bitácora operativa de trazabilidad para acciones administrativas y flujo de reportes.

| Campo | Tipo | Nulo | Default | Clave / Restricción | Descripción |
|---|---|---:|---|---|---|
| `pk_id` | `BIGINT` (IDENTITY) | No | — | PK | Identificador único del evento. |
| `modulo` | `VARCHAR(80)` | No | — | — | Módulo funcional que emite el evento (`REPORTE`,`AUDITORIA`, etc.). |
| `accion` | `VARCHAR(120)` | No | — | — | Acción ejecutada (`SOLICITUD_CREADA`, `WORKER_SOLICITUD_ERROR`, etc.). |
| `entidad` | `VARCHAR(120)` | Sí | — | — | Entidad de negocio afectada. |
| `entidad_id` | `VARCHAR(120)` | Sí | — | — | Identificador de la entidad afectada. |
| `resultado` | `VARCHAR(20)` | No | — | `CHECK` (`EXITO`,`ERROR`,`INFO`,`ADVERTENCIA`) | Resultado del evento. |
| `detalle` | `TEXT` | Sí | — | — | Detalle técnico/funcional controlado para trazabilidad. |
| `request_id` | `VARCHAR(64)` | Sí | — | — | Correlation id de la peticion HTTP original. |
| `ip_origen` | `VARCHAR(64)` | Sí | — | — | IP origen capturada desde request/proxy. |
| `actor_usuario_id` | `BIGINT` | Sí | — | FK → `usuario_sistema_administrativo(pk_id)` | Usuario autenticado asociado al evento. |
| `actor_login` | `VARCHAR(80)` | Sí | — | — | Login del actor al momento del evento. |
| `actor_rol` | `VARCHAR(30)` | Sí | — | — | Rol del actor (`ADMIN`, `SECRETARIA`). |
| `fecha_evento` | `TIMESTAMP` | No | `CURRENT_TIMESTAMP` | — | Fecha exacta del evento auditado. |

---

## 4. Relaciones principales (cardinalidad lógica)

- **representante_legal (1) — (N) estudiante**
 - Un representante legal puede estar asociado a varios estudiantes.
 - Un estudiante debe tener un representante legal.

- **sección (1) — (N) estudiante**
 - Una sección puede tener múltiples estudiantes.
 - Un estudiante puede no tener sección asignada temporalmente (`seccion_id` nullable).

- **sección (1) — (N) clase**
 - Una sección tiene múltiples bloques de clase.

- **asignatura (1) — (N) clase**
 - Una asignatura puede impartirse en múltiples bloques/clases.

- **docente (1) — (N) clase**
 - Un docente puede impartir múltiples clases.
 - Una clase puede no tener docente asignado en fase 1.

- **estudiante (1) — (N) calificación**
 - Un estudiante puede tener múltiples calificaciones.

- **clase (1) — (N) calificación**
 - Una clase puede generar múltiples calificaciones.

- **usuario_sistema_administrativo (1) — (N) reporte_solicitud_queue**
 - Un usuario puede solicitar multiples reportes asíncronos.
 - Una solicitud puede no tener usuario asociado si se encola por proceso técnico.

- **usuario_sistema_administrativo (1) — (N) auditoria_evento**
 - Un usuario autenticado puede generar multiples eventos de auditoría.
 - Un evento puede existir sin actor cuando ocurre fuera de contexto HTTP.

---

## 5. Índices relevantes (operativos)

**Índices típicos/recomendados observados para rendimiento:**

### 5.1 `estudiante`
- Índice por `representante_legal_id`
- Índice por `seccion_id`

### 5.2 `clase`
- Índice por `seccion_id`
- Índice por `asignatura_id`
- Índice por `docente_id`
- Índice compuesto por (`dia_semana`, `hora_inicio`, `hora_fin`)

### 5.3 `calificacion`
- Índice por `estudiante_id`
- Índice por `clase_id`

### 5.4 `reporte_solicitud_queue`
- Índice compuesto por (`estado`, `fecha_solicitud`)
- Índice por `solicitado_por_usuario`

### 5.5 `auditoria_evento`
- Índice por `fecha_evento`
- Índice compuesto por (`modulo`, `accion`)
- Índice por `resultado`
- Índice por `actor_login`

> Nota: los nombres exactos de índices y restricciones deben tomarse del DDL oficial (`V2_3FN.sql`) cuando se genere la versión final de entrega.

---

## 6. Campos derivables (no almacenados en 3FN)

### 6.1 Edad del estudiante
- **No se almacena** en la tabla `estudiante`.
- Se deriva desde `fecha_nacimiento`.
- Ventaja: evita inconsistencias por paso del tiempo.

### 6.2 Cantidad de estudiantes por sección
- **No se almacena** en la tabla `seccion`.
- Se obtiene por agregación (`COUNT`) sobre `estudiante.seccion_id`.
- Ventaja: evita duplicidad de información y desincronización.

---

## 7. Reglas de negocio / validaciones pendientes para capas superiores

Estas reglas pueden implementarse en backend (servicios/validadores) o reforzarse en futuras versiones de BD:

1. **Coherencia de grado entre `seccion` y `asignatura` al crear `clase`.**
2. **Evitar solapamientos horarios** por:
 - docente
 - sección
3. **Validar rangos de nota** según política institucional (si se define máximo, ej. 10.00 o 100.00).
4. **Catálogos de dominio** (estados, días de semana, etc.) en fases futuras si se desea mayor normalización.
5. **Reglas de negocio de matrícula y promoción** fuera del alcance de esta versión física.

---

## 8. Recomendación de paquete de entrega profesional (para esta versión)

### 8.1 Entregables sugeridos
- **PDF oficial** del diccionario de datos (este documento)
- **ERD/DER** en PNG o PDF
- **Script SQL DDL** (`V2_3FN.sql`)
- **Hoja de cálculo editable** (opcional pero muy recomendable) con columnas estándar por campo

### 8.2 Nombre sugerido del archivo final
`Diccionario_de_Datos_UENS_V2_3FN.pdf`

---

## 9. Anexo — Estructura estándar para hoja de cálculo del diccionario (opcional)

Columnas recomendadas:

- `tabla`
- `campo`
- `tipo_dato`
- `longitud_precision`
- `permite_nulo`
- `es_pk`
- `es_fk`
- `referencia_fk`
- `default`
- `restriccion_check`
- `restriccion_unique`
- `descripcion`
- `ejemplo_valor`
- `observaciones`

---

## 10. Observación de control de calidad

Este documento está “pasado a limpio” para uso técnico/entrega. Antes de la versión final oficial, se recomienda una **verificación cruzada contra el DDL** para asegurar que:
- nombres de constraints,
- defaults,
- checks,
- e índices
coincidan exactamente con el script `V2_3FN.sql`.
