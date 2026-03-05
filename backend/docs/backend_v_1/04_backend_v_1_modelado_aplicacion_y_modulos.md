# 04_backend_v1_modelado_aplicacion_y_modulos

- **Versión:** 0.2
- **Estado:** En revisión (reconstruido por consistencia)
- **Ámbito:** Backend V1 (Spring Boot + Java 21 + JPA/PostgreSQL)
- **Depende de:** `00_backend_v1_indice_y_mapa_documental.md`, `01_backend_v1_vision_y_alcance.md`, `02_backend_v1_arquitectura_general.md`, `03_backend_v1_convenciones_y_estandares_codigo.md`
- **Referencias de contexto:** `01_levantamiento_informacion_negocio.md`, `02_levantamiento_requerimientos.md`, `03_modelo_conceptual_dominio.md`, `04_reglas_negocio_y_supuestos.md`, `05_glosario_alcance_y_limites.md`, `V2_3FN.sql`
- **Objetivo de esta revisión:** Aterrizar la arquitectura general en un **mapa de módulos implementable**, con **módulos CRUD + módulos de orquestación**, enfoque **DDD-lite pragmático**, y reglas claras para V1 sin sobreingeniería.

---

## 1. Propósito del documento

Este documento define **cómo se modela la aplicación backend V1** en términos de:

- módulos funcionales,
- módulos de orquestación (casos de uso multi-entidad),
- responsabilidades por capa,
- límites entre módulos,
- y una forma de implementación repetible y profesional.

La meta es que el backend no sea una simple copia de la base de datos por tablas, sino una **aplicación organizada por capacidades del sistema**.

> Este documento no formaliza aún el contrato exacto de respuestas/errores (`05`) ni el catálogo final de endpoints (`06`), pero sí define la **estructura base** donde esos documentos vivirán.

---

## 2. Objetivo de modelado para backend V1

## 2.1. Qué queremos lograr

Diseñar un backend que:

- sea **claro de leer y mantener**,
- esté **alineado al dominio** y a casos de uso reales,
- permita CRUDs + consultas operativas + autenticación básica,
- soporte paginación, filtros y ordenamiento,
- sea reutilizable desde JavaFX (y otro cliente futuro),
- y deje una base sólida para crecer sin reescribir todo.

---

## 2.2. Qué NO queremos en V1

Para evitar sobreingeniería, en V1 no se introduce de entrada:

- microservicios,
- mensajería real con broker externo,
- hexagonal ultra formal con interfaces para todo,
- CQRS completo,
- permisos granulares complejos,
- arquitectura distribuida,
- ni generación pesada de reportes dentro del backend (presentación/exportación final se deja a JavaFX).

✅ Decisión V1: **monolito modular limpio**, con capas claras, ORM, DTOs y mappers manuales.

---

## 3. Principios de modelado de la aplicación

## 3.1. Backend agnóstico de UI, orientado a casos de uso

El backend:

- **sí** debe ser agnóstico de la interfaz (JavaFX, web, móvil),
- **no** es agnóstico de los casos de uso del negocio.

### Traducción práctica
- No diseñamos endpoints por “botones” de una pantalla.
- Sí diseñamos endpoints por operaciones del sistema (registrar estudiante, asignar sección, registrar calificación, consultar dashboard, etc.).

---

## 3.2. Módulos por capacidad, no solo por tabla

No todo se resuelve con “un módulo por entidad”.

En V1 convivirán:

### A) Módulos CRUD (centrados en entidad/capacidad principal)
Ejemplos:
- `representante`
- `docente`
- `asignatura`
- `seccion`
- `usuario`

### B) Módulos de orquestación (casos de uso multi-entidad)
Ejemplos:
- `matriculaoperativa`
- `asignacionestudianteseccion`
- `planificacionacademica`
- `dashboard`
- `reporte`

✅ Esto responde directamente a tu duda: **sí es normal** que haya módulos que trabajen con varias entidades del dominio.

---

## 3.3. ORM sí, pero con control

Se usará **JPA/Hibernate** porque para V1 es la mejor relación beneficio/tiempo:

- acelera CRUDs,
- facilita paginación y filtros,
- reduce SQL repetitivo,
- y es estándar profesional.

Pero bajo estas reglas:

- **DTOs explícitos** (no exponer entidades JPA por API)
- **mappers manuales** (decisión consciente de aprendizaje)
- **consultas complejas** solo donde tenga sentido (JPQL / nativas / proyecciones)
- **RN** en capa de aplicación, no en controllers ni mappers

---

## 3.4. DDD-lite pragmático y mutabilidad controlada

Este documento se alinea con `02`:

- evitar setters públicos innecesarios,
- encapsular transiciones importantes con métodos con intención,
- mantener compatibilidad con JPA,
- sin forzar DDD “puro” en toda la V1.

### Ejemplo conceptual
En vez de permitir cambios arbitrarios de estado desde cualquier capa, preferir métodos con intención cuando haya invariantes:
- `activar()`
- `inactivar()`
- `marcarEnProceso()`
- `marcarCompletada()`

✅ Esto aplica especialmente a estados sensibles (`seccion`, `reporte`, etc.).

---

## 4. Modelo general de módulos del backend V1

### Patrones pragmaticos que si aportan en este backend

Para esta V1 no hace falta perseguir "todos los patrones", pero si conviene usar algunos de forma intencional porque mejoran desacople y mantenimiento.

### A. Application Service como fachada de caso de uso

Los controllers no deben contener reglas de negocio ni armar transacciones manuales.

Responsabilidad esperada:
- recibir el request ya validado
- coordinar repositorios, mappers y validaciones RN
- devolver DTO o lanzar excepciones tipadas

Ejemplos naturales:
- `EstudianteCommandService`
- `UsuarioCommandService`
- `ConsultaAcademicaQueryService`

### B. Puerto + adaptador cuando la dependencia es técnica

Cuando una regla de aplicación necesita algo técnico que podria cambiar, conviene ocultarlo detras de un puerto pequeno.

Ejemplo claro para esta V1:
- `UsuarioPasswordService` en `application/port`
- implementacion `BcryptUsuarioPasswordService` en infraestructura

Beneficio:
- `auth` y `usuario` no quedan acoplados a `PasswordEncoder`
- permite cambiar hash, testing o integración futura sin contaminar la capa de aplicación

### C. Query repository + read model para lecturas agregadas

Las consultas multi-entidad no deben forzarse dentro de un CRUD que no les pertenece.

En esos casos aporta usar:
- un `QueryRepository` especializado
- un `read model` pequeno para la fila proyectada
- un mapper hacia DTO API

Esto encaja especialmente bien en:
- `consultaacademica`
- `dashboard`
- reportes de solo lectura

### D. Specification para filtros paginados

Cuando un listado admite filtros opcionales, conviene encapsular el criterio en `Specification` en vez de multiplicar métodos de repositorio.

Beneficio:
- escala mejor al crecer filtros
- evita firmas explosivas tipo `findByAAndBAndC...`
- mantiene el query service legible

### E. Factory method / named constructor en entidades persistentes

Para entidades con estados o invariantes básicos, es preferible ofrecer métodos con intencion como:
- `crear(...)`
- `activar()`
- `inactivar()`
- `actualizarCredencial(...)`

Esto reduce setters abiertos y deja más claro el rol de la entidad dentro del modelo.

### F. Strategy solo cuando haya variantes reales

No hace falta meter Strategy en todos lados. Si un flujo tiene varias implementaciones segun tipo o formato, ahi si tiene sentido.

Encaja bien en:
- generadores/procesadores de reportes por tipo
- exportadores por formato (`pdf`, `xlsx`, `csv`)
- reglas variables por canal externo, si algun día aparecen

Regla práctica:
- si hoy solo existe una variante real, no fuerces Strategy todavia
- si ya hay dos o más variantes estables, Strategy empieza a pagar sola

---

## 4.1. Vista global (3 grupos)

Se modelan 3 grupos de módulos:

1. **Transversales (shared/common + config + security)**
2. **Funcionales de dominio (CRUD + consultas por capacidad principal)**
3. **Orquestación / lectura agregada (casos de uso multi-entidad)**

---

## 4.2. Módulos transversales (infraestructura compartida)

Estos módulos no representan un “negocio” específico, sino soporte común del backend.

### 4.2.1. `config`
Responsabilidad:
- configuración Spring (OpenAPI, CORS si aplica, properties, etc.)
- ajustes técnicos de framework

No debe contener:
- reglas de negocio
- lógica de módulos académicos

---

### 4.2.2. `security`
Responsabilidad:
- infraestructura de seguridad (JWT, filtros, configuración)
- integración con autenticación de Spring Security

Subáreas típicas:
- `security/config`
- `security/jwt`
- `security/filter`
- `security/service`

No debe contener:
- lógica académica
- lógica de reportes

---

### 4.2.3. `common`
Responsabilidad:
- contrato de respuestas comunes (`ApiResponse`, `ApiErrorResponse`, `PageResponseDto`)
- excepciones comunes + `GlobalExceptionHandler`
- paginación/filtros compartidos (si realmente son comunes)
- piezas transversales pequeñas y justificadas

Regla:
`common` **no** debe convertirse en cajón de sastre.

---

## 4.3. Módulos funcionales de dominio (CRUD + consultas específicas)

> Nota: se organizan por **capacidad funcional**, no por “copiar tablas” literalmente.

A continuación se propone el mapa V1 base.

---

### 4.3.1. `representante`
**Capacidad principal:** gestión de representantes legales.

**Casos de uso V1 (referenciales):**
- registrar representante
- actualizar representante
- obtener detalle
- listar/buscar representantes
- combos de selección (si aplica)

**Responsabilidades backend:**
- validaciones de request (VR)
- consultas con paginación/filtros/búsqueda
- respuesta DTO limpia para UI

**No debería hacer:**
- registrar estudiante completo en la misma operación (eso va en orquestación)

---

### 4.3.2. `estudiante`
**Capacidad principal:** gestión de estudiantes.

**Casos de uso V1 (referenciales):**
- registrar estudiante
- actualizar estudiante
- obtener detalle
- listar/buscar estudiantes
- consultar por sección (lectura)

**Responsabilidades backend:**
- validación de estructura/formatos (DTO request)
- reglas de negocio de estudiante (edad/rango, estados, duplicados de negocio, etc. según RN/VR)
- filtros por estado/sección/grado/año

**Nota importante:**
Aunque la tabla tenga FK a `representante` y `seccion`, las validaciones cruzadas más fuertes (cupo, cambio de sección, etc.) pueden resolverse en módulos de orquestación.

---

### 4.3.3. `seccion`
**Capacidad principal:** gestión de secciones.

**Casos de uso V1:**
- crear/actualizar sección
- obtener detalle
- listar/buscar secciones
- combos de secciones activas (si aplica)

**Responsabilidades backend:**
- validar atributos propios
- exponer estado operativo
- consultas con derivados de lectura cuando aplique (`cantidadEstudiantes`, `cuposDisponibles`) como DTO de listado/proyección

**Mutabilidad controlada (DDD-lite):**
Si el estado de sección tiene reglas, preferir métodos con intención (`activar`, `inactivar`) en vez de setters abiertos.

---

### 4.3.4. `docente`
**Capacidad principal:** gestión de docentes.

**Casos de uso V1:**
- crear/actualizar docente
- obtener detalle
- listar/buscar docentes
- combos para asignación a clase

**Responsabilidades backend:**
- CRUD y consultas típicas
- soporte a otros módulos (planificación académica)

---

### 4.3.5. `asignatura`
**Capacidad principal:** catálogo académico de asignaturas.

**Casos de uso V1:**
- crear/actualizar asignatura
- obtener detalle
- listar/buscar asignaturas
- combos por grado/estado (si aplica)

**Responsabilidades backend:**
- CRUD de catálogo
- soporte a validación de coherencia con `seccion`/`clase`

---

### 4.3.6. `clase`
**Capacidad principal:** gestión de clases (sección + asignatura + horario + docente opcional, según modelo).

**Casos de uso V1:**
- crear/actualizar clase
- obtener detalle
- listar/buscar clases
- consultar por sección/docente/asignatura/día

**Responsabilidades backend:**
- validar coherencia operativa (horario, entidades activas, grado sección vs asignatura si aplica)
- exponer lectura con joins razonables

**Comentario técnico:**
Este módulo ya es una excelente práctica de backend real porque toca varias entidades aunque su foco funcional sea “clase”.

---

### 4.3.7. `calificacion`
**Capacidad principal:** registro y consulta de calificaciones.

**Casos de uso V1:**
- registrar calificación
- actualizar calificación
- listar/consultar por estudiante
- listar/consultar por clase/asignatura/parcial

**Responsabilidades backend:**
- validar parcial permitido y rango de nota (según reglas institucionales definidas en backend)
- controlar unicidad lógica (ej. combinación estudiante–clase–parcial)
- exponer consultas filtradas y paginadas cuando aplique

---

### 4.3.8. `usuario`
**Capacidad principal:** gestión de usuarios del sistema administrativo.

**Casos de uso V1:**
- crear/actualizar usuario
- obtener detalle
- listar usuarios
- activar/inactivar usuario
- cambio de contraseña/hash (según alcance)

**Responsabilidades backend:**
- estado operativo para login
- gestión de rol básico (V1)
- nunca exponer credenciales sensibles

---

### 4.3.9. `auth` (módulo funcional)
**Capacidad principal:** autenticación/identidad de aplicación.

**Casos de uso V1:**
- login
- obtener usuario autenticado (`/me`, recomendable)
- (opcional) refresh token si se decide en V1

**Importante:**
- `auth` = casos de uso de autenticación
- `security` = infraestructura transversal de seguridad

✅ Separarlos evita mezclar mecanismo con funcionalidad.

---

## 4.4. Módulos de orquestación y consulta agregada (multi-entidad)

Aquí vive buena parte del backend “real”, donde se cruzan varias entidades y reglas.

---

### 4.4.1. `matriculaoperativa` (o `registroestudianteoperativo`)
**Qué resuelve:**
Flujo de registro operativo de estudiante que involucra más de una entidad (ej. estudiante + representante + validaciones relacionadas, y potencialmente sección).

**Por qué existe:**
Porque el flujo real no es solo “insertar estudiante”; hay secuencia y validaciones de negocio.

**Operaciones típicas:**
- `registrarEstudianteConRepresentante(...)`
- `registrarEstudianteConRepresentanteYSeccion(...)` (si se habilita en V1)

**Repositorios/módulos involucrados:**
- `representante`
- `estudiante`
- `seccion` (si aplica)

**Transacción:**
Sí, debe ser **transaccional** (`@Transactional`).

---

### 4.4.2. `asignacionestudianteseccion`
**Qué resuelve:**
Asignar/cambiar sección vigente de un estudiante con validación de cupo, estados y coherencia.

**Por qué no meterlo todo en `estudiante`:**
Porque es un caso de uso compuesto con reglas cruzadas y transacción propia.

**Operaciones típicas:**
- `asignarSeccionVigente(...)`
- `cambiarSeccionVigente(...)`

**Repositorios/módulos involucrados:**
- `estudiante`
- `seccion`

**Punto crítico de backend:**
La validación de cupo **no se confía al frontend**; se valida en backend dentro del caso de uso.

---

### 4.4.3. `planificacionacademica`
**Qué resuelve:**
Casos de uso que involucran `clase`, `seccion`, `asignatura` y `docente`.

**Operaciones típicas:**
- crear clase con coherencia de grado
- asignar/quitar docente a clase
- validaciones de estados operativos

**Repositorios/módulos involucrados:**
- `clase`
- `seccion`
- `asignatura`
- `docente`

✅ Este módulo evita ensuciar `clase` con demasiada orquestación si crece la lógica.

---

### 4.4.4. `dashboard`
**Qué resuelve:**
Consultas agregadas de lectura para panel administrativo.

**Características:**
- no tiene por qué corresponder a una sola entidad
- puede usar joins, agregaciones y proyecciones
- devuelve DTOs de lectura (read models)

**Ejemplos de consultas:**
- cupos por sección
- clases por docente/sección
- estudiantes por sección
- indicadores simples de operación

**Regla:**
Este módulo es de **lectura/orquestación**, no un CRUD.

---

### 4.4.5. `reporte` (placeholder + soporte backend limitado V1)
**Decisión V1 (alineada al proyecto):**
- La presentación/exportación final del reporte se hará en **JavaFX**.
- El backend puede manejar:
 - solicitudes de reporte,
 - estado de solicitud,
 - dataset/resultado JSON (si se implementa),
 - endpoints placeholder para practicar arquitectura.

✅ Así prácticas backend profesional sin trabarte con la capa de rendering/exportación.

---

## 5. Inconsistencia importante detectada entre requerimientos y SQL (docente–sección)

## 5.1. Hallazgo

En requerimientos/reglas aparece una relación operativa **Docente ↔ Sección** (M:N) como capacidad funcional relevante.

Sin embargo, en `V2_3FN.sql`:
- existe `docente`
- existe `seccion`
- existe `clase` con `docente_id` opcional
- **pero no existe tabla puente `docente_seccion`**

---

## 5.2. Impacto en backend

Sin `docente_seccion`, no se puede implementar correctamente una asociación general docente–sección como capacidad independiente.

Solo podrías derivar algo aproximado desde `clase` (docentes con clases en una sección), que **no equivale** a la asociación operativa general.

---

## 5.3. Decisión V1 recomendada

### Opción recomendada (mejor)
Crear ajuste/migración de BD para tabla puente `docente_seccion`.

### Opción temporal (si no quieres tocar BD todavía)
- marcar esos endpoints como **placeholder** o **pendientes por ajuste de modelo lógico**,
- implementar solo relaciones vía `clase`,
- documentar deuda técnica explícitamente.

✅ Detectarlo ahora es señal de buen diseño, no un problema grave.

---

## 6. Estructura de paquetes por módulo (alineada con `02`)

## 6.1. Regla general

Se adopta **feature-first** (por módulo funcional) con **capas internas**.

### Paquetes internos estándar por módulo (referencia)
- `api/` (controllers + DTOs)
- `application/` (casos de uso / servicios de aplicación)
- `domain/` (reglas/invariantes/VO/enums si aportan)
- `infrastructure/` (persistencia JPA, queries, mappers técnicos, workers)

> Nota: en V1, algunos módulos CRUD simples pueden tener `domain/` mínimo. Eso es normal.

---

## 6.2. Estructura referencial (visión global)

```text
com.marcos.sms
├─ config/
├─ security/
├─ common/
├─ auth/
├─ representante/
├─ estudiante/
├─ seccion/
├─ docente/
├─ asignatura/
├─ clase/
├─ calificacion/
├─ usuario/
├─ matriculaoperativa/
├─ asignacionestudianteseccion/
├─ planificacionacademica/
├─ dashboard/
└─ reporte/
```

> Puedes usar un paquete intermedio `modules/` si te gusta (`com.marcos.sms.modules.estudiante...`), pero lo importante es mantener consistencia.

---

## 6.3. Estructura interna de un módulo funcional (plantilla V1)

```text
<módulo>/
├─ api/
│  ├─ <Módulo>Controller.java
│  └─ dto/
│     ├─ request/
│     └─ response/
├─ application/
│  ├─ <Módulo>ApplicationService.java
│  └─ <Módulo>QueryService.java          (opcional)
├─ domain/
│  ├─ model/                             (opcional)
│  ├─ enum/                              (opcional)
│  └─ exception/                         (opcional)
└─ infrastructure/
   ├─ persistence/
   │  ├─ entity/
   │  ├─ repository/
   │  └─ query/                          (opcional)
   └─ mapper/
```

✅ Esto deja clara la separación API / aplicación / dominio / infraestructura sin complicarte de más.

---

## 6.4. Estructura interna de módulo de orquestación (plantilla V1)

```text
<módulo-orquestacion>/
├─ api/
│  ├─ <Módulo>Controller.java
│  └─ dto/
├─ application/
│  └─ <Módulo>ApplicationService.java
├─ domain/
│  └─ ... (opcional, si hay estados/reglas propias)
└─ infrastructure/
   └─ mapper/                            (si aplica)
```

### Regla importante
Un módulo de orquestación **puede no tener entidad propia** ni repositorio propio.
Su trabajo es coordinar repositorios existentes.

---

## 7. Reglas de dependencia entre capas y módulos

## 7.1. Dirección de dependencias (interna por módulo)

Dirección recomendada:

- `api -> application`
- `application -> domain` + repositorios / servicios de infraestructura necesarios
- `infrastructure -> JPA/Spring Data/PostgreSQL`

### Regla operativa
Controllers no llaman repositorios directamente.

---

## 7.2. Dependencias entre módulos (criterio V1)

Se permiten dependencias **de aplicación a repositorios/servicios de otros módulos** cuando el caso de uso lo exige, pero con criterio.

### Recomendación práctica
En V1, en lugar de crear interfaces artificiales entre todos los módulos, puedes:
- usar repositorios necesarios directamente desde la capa `application` del módulo de orquestación,
- mantener esa orquestación localizada,
- y documentar la dependencia.

✅ Es pragmático, claro y suficiente para V1.

---

## 7.3. Qué NO hacer (acoplamiento indeseado)

- `api` llamando `repository`
- `mapper` consultando BD
- `domain` con dependencias HTTP/Jackson
- `common` absorbiendo lógica de negocio de módulos
- endpoints diseñados por pantalla específica

---

## 8. Modelo de responsabilidades por capa (operativo)

## 8.1. `api` (controllers + DTOs)

**Responsable de:**
- recibir requests HTTP
- validar request DTOs (`@Valid`)
- leer path/query params
- delegar a `application`
- devolver `ApiResponse<T>` / `ApiErrorResponse`
- documentar endpoints (Swagger/OpenAPI)

**No responsable de:**
- RN complejas
- acceso directo a varios repositorios
- decisiones transaccionales

---

## 8.2. `application` (casos de uso / orquestación)

**Responsable de:**
- RN y flujo del caso de uso
- transacciones (`@Transactional`)
- orquestar múltiples repositorios/módulos
- coordinar mappers
- lanzar excepciones de negocio con códigos RN/VR cuando aplique

**Aquí encaja tu idea de comentarios/Javadoc con trazabilidad**, por ejemplo:

```java
/**
 * Caso de uso: Asignar sección vigente.
 * Descripción: Asigna o cambia la sección de un estudiante validando cupo y estado.
 * RN aplicadas: RN-12, RN-13, RN-15.
 */
```

✅ Muy buena práctica si se usa con moderación y en métodos realmente importantes.

---

## 8.3. `domain` (DDD-lite)

**Responsable de:**
- invariantes locales
- métodos con intención (mutabilidad controlada)
- enums y objetos con semántica de negocio
- excepciones de dominio (si aporta)

**No responsable de:**
- HTTP
- DTOs API
- consultas a BD

---

## 8.4. `infrastructure` (persistencia y detalles técnicos)

**Responsable de:**
- entidades JPA (si se ubican aquí)
- `JpaRepository`
- consultas complejas (JPQL/nativa/proyecciones)
- mappers de persistencia/DTO según organización elegida
- workers/schedulers (ej. cola simple de reportes)

**No responsable de:**
- RN de negocio dispersas fuera del caso de uso

---

## 9. Diseño de módulos por tipo de endpoint

## 9.1. CRUD estándar (vieja confiable)

Aplica muy bien a módulos como:
- `representante`
- `docente`
- `asignatura`
- `seccion`
- `usuario`

Patrón típico:
- `POST /api/v1/<recurso>`
- `PUT /api/v1/<recurso>/{id}`
- `GET /api/v1/<recurso>/{id}`
- `GET /api/v1/<recurso>`
- `PATCH`/`DELETE` según decisión del producto

---

## 9.2. Endpoints de orquestación (válidos y recomendables)

Para casos compuestos se permiten endpoints más expresivos, por ejemplo:

- `POST /api/v1/matricula-operativa/estudiantes`
- `PUT /api/v1/estudiantes/{id}/seccion-vigente`
- `PUT /api/v1/clases/{id}/docente`
- `GET /api/v1/dashboard/cupos-por-seccion`

✅ Esto no “rompe REST” si el recurso/acción está bien nombrado y el contrato es claro.

---

## 9.3. Endpoints placeholder (permitidos en V1)

Se permiten endpoints placeholder para no bloquear el avance de arquitectura cuando una pieza aún no está lista:

Casos típicos:
- reportes backend con generación real de archivos (`PDF`, `DOCX`, `XLSX`)
- capacidades dependientes de una corrección de BD (`docente_seccion`)
- endpoint futuro de landing/API de presentación

**Opciones de respuesta temporal:**
- `501 Not Implemented` (más semántico)
- o respuesta controlada de “en construcción” con contrato estándar (si decides simplificar)

---

## 10. DTOs por módulo y por caso de uso

## 10.1. Regla general

Cada módulo funcional debería tener, como mínimo, DTOs separados para:
- crear (`CreateRequestDto`)
- actualizar (`UpdateRequestDto`)
- detalle (`ResponseDto`)
- listado (`ListItemResponseDto`) cuando difiere del detalle

✅ No usar un DTO universal para todo.

---

## 10.2. DTOs de orquestación (multi-entidad)

Cuando el caso de uso cruza entidades, el DTO pertenece al **caso de uso**, no a una tabla.

Ejemplos:
- `RegistroEstudianteOperativoRequestDto`
- `AsignacionSeccionVigenteRequestDto`
- `AsignarDocenteClaseRequestDto`
- `DashboardCuposPorSeccionItemDto`

Esto formaliza tu idea de backend orientado a capacidades, no solo a entidades.

---

## 10.3. Paginación: qué se página y qué no

No todos los endpoints llevan paginación.

### Normalmente sí se paginan
- listados de estudiantes
- representantes
- docentes
- asignaturas
- usuarios
- clases
- calificaciones (si volumen lo amerita)

### Normalmente no se paginan
- login
- detalle por ID
- combos catálogos pequeños
- métricas pequeñas del dashboard

### Contrato recomendado (alineado con `05`)
- `ApiResponse<T>` para respuestas simples
- `ApiResponse<PageResponseDto<T>>` para listados paginados

---

## 11. Filtros, búsqueda, ordenamiento y query params (cómo entra en el modelado)

Sí, los `?param=...` forman parte del diseño backend y deben modelarse desde el inicio.

## 11.1. Query params comunes de listados (convención V1)

Ejemplo:

```http
GET /api/v1/estudiantes?page=0&size=20&sort=apellidos,asc&estado=ACTIVO&q=juan&seccionId=4
```

Parámetros comunes:
- `page`
- `size`
- `sort`
- `q` (búsqueda simple)

> Nota de consistencia: se usará **`q`** como convención base de búsqueda simple en V1 (no mezclar con `search` salvo decisión global futura).

---

## 11.2. Filtros por dominio (según módulo)

Ejemplos típicos:
- `estado`
- `grado`
- `paralelo`
- `anioLectivo`
- `seccionId`
- `docenteId`
- `asignaturaId`
- `claseId`
- `numeroParcial`

---

## 11.3. Regla de seguridad/robustez en consultas

No permitir ordenamiento por cualquier campo arbitrario sin control.

Cada módulo/listado debe definir:
- campos ordenables permitidos,
- filtros soportados,
- combinación mínima razonable.

✅ Esto mejora estabilidad y evita errores/abuso accidental.

---

## 12. Trazabilidad RN/VR dentro de módulos

Tu idea de trazabilidad con `RN-XX` / `VR-XX` se adopta como práctica recomendada.

## 12.1. Dónde usar RN/VR

- Javadocs de casos de uso importantes (`application`)
- excepciones de negocio / `errorCode`
- documentación Swagger/OpenAPI (descripción de endpoint)
- documentación técnica (`07`)
- tests (más adelante)

---

## 12.2. Regla de uso

Usarlo con criterio:
- **sí** en casos de uso importantes y reglas críticas,
- **no** llenar todos los métodos con comentarios redundantes.

✅ Trazabilidad útil, no ruido.

---

## 13. Modelo repetible por tipo de módulo (plantillas)

## 13.1. Plantilla de módulo funcional CRUD (V1)

```text
<módulo>/
├─ api/
│  ├─ <Módulo>Controller.java
│  └─ dto/
│     ├─ request/
│     └─ response/
├─ application/
│  └─ <Módulo>ApplicationService.java
├─ domain/
│  └─ ... (mínimo si es CRUD simple)
└─ infrastructure/
   ├─ persistence/
   │  ├─ entity/
   │  └─ repository/
   └─ mapper/
```

---

## 13.2. Plantilla de módulo de orquestación (V1)

```text
<módulo-orquestacion>/
├─ api/
│  ├─ <Módulo>Controller.java
│  └─ dto/
├─ application/
│  └─ <Módulo>ApplicationService.java
├─ domain/
│  └─ ... (solo si aporta estados/reglas propias)
└─ infrastructure/
   └─ mapper/ (si aplica)
```

Regla:
Este módulo puede depender de varios repositorios de otros módulos y **no necesita** entidad propia.

---

## 13.3. Plantilla de módulo `reporte` con cola simple DB queue (si se implementa en V1)

```text
reporte/
├─ api/
│  ├─ ReporteController.java
│  └─ dto/
├─ application/
│  ├─ ReporteSolicitudApplicationService.java
│  └─ ReporteConsultaApplicationService.java
├─ domain/
│  ├─ enum/
│  │  └─ EstadoReporteSolicitud.java
│  └─ ... (si modelas transiciones)
└─ infrastructure/
   ├─ persistence/
   │  ├─ entity/
│   │  └─ repository/
   ├─ mapper/
   └─ worker/
      └─ ReporteSolicitudWorker.java     (@Scheduled)
```

✅ Compatible con la arquitectura V1 y con tu práctica de asincronía simple.

---

## 14. Propuesta de módulos V1 final (lista concreta)

## 14.1. Transversales
- `config`
- `security`
- `common`

## 14.2. Funcionales de dominio
- `auth`
- `usuario`
- `representante`
- `estudiante`
- `seccion`
- `docente`
- `asignatura`
- `clase`
- `calificacion`

## 14.3. Orquestación / consulta / soporte
- `matriculaoperativa`
- `asignacionestudianteseccion`
- `planificacionacademica`
- `dashboard`
- `reporte` (placeholder + soporte backend limitado / DB queue opcional)

---

## 14.4. Módulo pendiente por ajuste de BD (si `V2_3FN.sql` sigue igual)

- `docenteseccion` (asociación general M:N)

Estado recomendado:
- `PENDIENTE_BD` / placeholder documentado

---

## 15. Orden recomendado de implementación (pensado para avanzar sin saturarte)

## 15.1. Arranque técnico
1. Proyecto Spring Boot base (Java 21 Temurin)
2. Variables de entorno + perfiles
3. PostgreSQL + JPA
4. Swagger/OpenAPI
5. `ApiResponse` + manejo global de excepciones
6. Seguridad mínima (`auth` + JWT)

---

## 15.2. CRUDs simples primero (victorias rápidas)
7. `representante`
8. `docente`
9. `asignatura`
10. `seccion`
11. `usuario`

---

## 15.3. Módulos con más lógica
12. `estudiante`
13. `clase`
14. `calificacion`

---

## 15.4. Orquestación y lectura agregada
15. `matriculaoperativa`
16. `asignacionestudianteseccion`
17. `planificacionacademica`
18. `dashboard`
19. `reporte` placeholder / DB queue simple

✅ Este orden te da progreso visible, práctica real y menos carga mental.

---

## 16. Decisiones de modelado fijadas por este documento (V1)

1. ✅ Backend V1 = **monolito modular** con Spring Boot.
2. ✅ Organización por **módulos funcionales + módulos de orquestación**.
3. ✅ ORM/JPA con **DTOs explícitos** y **mappers manuales**.
4. ✅ Backend agnóstico de UI, orientado a casos de uso del dominio.
5. ✅ DDD-lite pragmático con **mutabilidad controlada** (evitar setters innecesarios).
6. ✅ Paginación/filtros/ordenamiento en listados principales.
7. ✅ Convención de búsqueda simple con query param **`q`**.
8. ✅ Trazabilidad `RN-XX` / `VR-XX` en lugares clave.
9. ✅ Reportes V1: JavaFX para presentación/exportación; backend con soporte/placeholder.
10. ✅ Relación docente–sección general queda pendiente si no se ajusta BD.

---

## 17. Riesgos de modelado comunes y cómo evitarlos

## 17.1. “Un módulo por tabla” a la fuerza

**Riesgo:** meter lógica multi-entidad dispersa o duplicada.

**Cómo evitarlo:** crear módulos de orquestación cuando el caso de uso lo pida.

---

## 17.2. `controller` con lógica de negocio

**Riesgo:** controllers gordos, difíciles de probar y mantener.

**Cómo evitarlo:** controllers delgados; RN y transacciones en `application`.

---

## 17.3. `common` como basurero

**Riesgo:** helpers ambiguos y lógica mezclada.

**Cómo evitarlo:** `common` solo para piezas genuinamente transversales.

---

## 17.4. Acoplar endpoints a pantallas JavaFX

**Riesgo:** API frágil y poco reutilizable.

**Cómo evitarlo:** diseñar por capacidades/casos de uso, no por widgets.

---

## 17.5. Ignorar deudas de modelo lógico (SQL)

**Riesgo:** implementar “parches” incoherentes para relaciones no modeladas.

**Cómo evitarlo:** documentar y dejar placeholders cuando falte una tabla puente real.

---

## 18. Relación con documentos siguientes

- **`05_backend_v1_diseno_api_contrato_respuestas_y_errores.md`**
 formaliza `ApiResponse`, `ApiErrorResponse`, errores y contratos mencionados aquí.

- **`06_backend_v1_api_endpoints_y_casos_de_uso.md`**
 aterriza el catálogo de endpoints por módulo y por orquestación.

- **`07_backend_v1_validaciones_reglas_negocio_y_excepciones.md`**
 aterriza VR/RN y su ubicación real en `application`/`domain`/excepciones.

- **`08_backend_v1_paginacion_filtros_ordenamiento_y_consultas.md`**
 formaliza `page/size/sort/q`, filtros y criterios de consulta.

- **`10_backend_v1_reporte_solicitudes_cola_simple_db_queue.md`**
 detalla el módulo `reporte` cuando se implemente cola simple en BD.

---

## 19. Cierre

Con este modelado, el backend V1 queda diseñado como una aplicación **coherente, profesional y practicable**:

- suficientemente formal para aprender bien,
- suficientemente simple para avanzar rápido,
- y suficientemente clara para sostener una app cliente en JavaFX sin improvisar por pantallas.

Lo más importante: aquí ya estás modelando **computación backend real** (módulos, límites, casos de uso, transacciones, contratos), no solo dibujando CRUDs.


