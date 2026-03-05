# 03_backend_v1_convenciones_y_estandares_codigo

- **Versión:** 0.2
- **Estado:** En revisión (reconstruido por consistencia)
- **Ámbito:** Backend V1 (Spring Boot + Java 21)
- **Depende de:** `00_backend_v1_indice_y_mapa_documental.md`, `01_backend_v1_vision_y_alcance.md`, `02_backend_v1_arquitectura_general.md`, `04_backend_v1_modelado_aplicacion_y_modulos.md`
- **Referencias de contexto:** `04_reglas_negocio_y_supuestos.md`, `V2_3FN.sql` (para trazabilidad puntual RN/VR)
- **Objetivo de esta revisión:** Unificar convenciones de código y estilo con enfoque **Java/Spring “vieja confiable”**, alineado con **DDD-lite pragmático**, mappers manuales y contratos API consistentes.

---

## 1. Propósito del documento

Este documento define las **convenciones de código y estándares de implementación** para el backend V1.

Su objetivo es que el proyecto se mantenga:

- consistente,
- legible,
- mantenible,
- fácil de extender,
- y alineado con prácticas comunes de Java/Spring.

> Este documento **no** define el catálogo funcional de endpoints (`06`) ni el contrato completo de respuestas/errores (`05`), pero sí define **cómo deben nombrarse, estructurarse y documentarse** esas piezas en el código.

---

## 2. Principios generales de estilo para este backend V1

## 2.1. Claridad sobre “ingenio”

Se prioriza código que se lea rápido y se entienda bien antes que código demasiado creativo, compacto o “elegante” pero difícil de mantener.

**Regla práctica:** si una solución reduce 5 líneas pero aumenta mucho la confusión, no conviene.

---

## 2.2. Convención sobre preferencia personal

Cuando existan varias formas válidas de escribir algo, se elige la convención más común en Java/Spring y se aplica de forma homogénea.

✅ Consistencia > perfección local.

---

## 2.3. Código autoexplicado primero, comentarios después

Orden correcto:

1. Nombres claros
2. Métodos con responsabilidad definida
3. Estructura de clases razonable
4. Comentarios/Javadocs donde aporten contexto real

No usar comentarios para compensar nombres malos.

---

## 2.4. Explícito cuando hay reglas, simple cuando no las hay

- Si hay RN/VR/AUTH o una decisión importante, ser explícito.
- Si es código repetitivo y obvio, mantenerlo simple.

Esto evita dos extremos:
- documentación cero,
- documentación excesiva que se vuelve ruido.

---

## 2.5. Alineación con arquitectura DDD-lite (sin dogmatismo)

Este backend V1 usa una aproximación **DDD-lite pragmática**. Por tanto, las convenciones de código deben apoyar:

- separación por módulos y capas,
- mutabilidad controlada,
- métodos con intención,
- orquestación en `application`,
- DTOs y mappers manuales.

No se busca “pureza DDD” absoluta, pero sí evitar desorden típico de proyectos CRUD improvisados.

---

## 3. Convenciones base del lenguaje Java

## 3.1. Nombres por tipo

- **Clases / interfaces / enums / records:** `PascalCase`
- **Métodos / variables / atributos / parámetros:** `camelCase`
- **Constantes (`static final`):** `UPPER_SNAKE_CASE`
- **Paquetes:** minúsculas, sin guiones

### Ejemplos
- `EstudianteApplicationService`
- `registrarEstudiante(...)`
- `MAX_PAGE_SIZE`
- `com.marcos.sms.estudiante.application`

---

## 3.2. Un archivo por clase pública

Regla general:
- **1 clase pública top-level por archivo**

Se admiten excepciones (clases internas privadas/estáticas) solo cuando aporten claridad real.

---

## 3.3. Imports

- ✅ imports explícitos
- ❌ imports wildcard (`*`) como convención del proyecto

**Motivo:** mejora lectura, evita ambigüedad y cambios silenciosos.

---

## 3.4. `var` local (Java 21)

Uso permitido con criterio.

### Recomendado usar `var` cuando:
- el tipo es obvio por el lado derecho
- mejora legibilidad sin ocultar información relevante

### Evitar `var` cuando:
- oculta demasiado el tipo real
- el resultado de un método no es evidente
- dificulta leer código de negocio

✅ Regla práctica: usar `var` como ayuda, no como moda.

---

## 3.5. `final` en variables locales y parámetros

No se exige `final` en todo por estilo.

Sí se recomienda usarlo cuando:
- ayuda a expresar inmutabilidad en bloques delicados,
- evita reasignaciones accidentales,
- mejora claridad en lambdas/closures.

No convertirlo en ruido visual obligatorio.

---

## 4. Convenciones de paquetes y estructura del proyecto

## 4.1. Paquete raíz

Usar un paquete raíz estable y propio del proyecto.

Ejemplo referencial:

- `com.marcos.sms`

> El nombre exacto puede cambiar, pero debe mantenerse consistente en todo el backend.

---

## 4.2. Organización por módulos (feature-first)

Se sigue lo definido en `02` y `04`:

- organización por **módulos funcionales**,
- con **capas internas**.

### Paquetes transversales permitidos
- `config`
- `security`
- `common`

### Módulos funcionales/orquestación (ejemplos)
- `auth`
- `estudiante`
- `seccion`
- `calificacion`
- `dashboard`
- `reporte`
- `matriculaoperativa`

---

## 4.3. Capas internas por módulo (convención canónica V1)

Convención recomendada y alineada con DDD-lite:

- `api/`
- `application/`
- `domain/`
- `infrastructure/`

### Ejemplo
```text
estudiante/
├─ api/
├─ application/
├─ domain/
└─ infrastructure/
```

---

## 4.4. Compatibilidad con estilo clásico (`controller/service/repository`)

Si en partes del proyecto usas nomenclatura clásica por familiaridad (`service`, `repository`, etc.), es válido **siempre que** respetes responsabilidades y mantengas consistencia.

### Regla de consistencia
No mezclar sin criterio estas dos visiones dentro del mismo módulo:
- `api/application/domain/infrastructure`
- `controller/service/repository/entity`

✅ Puedes usar estilo clásico **como nombres de clases** (`EstudianteController`, `EstudianteRepository`) dentro de una estructura por capas moderna.

---

## 4.5. Nombres de paquetes internos (estándar)

### Recomendados
- `api`
- `api.dto.request`
- `api.dto.response`
- `application`
- `domain`
- `domain.model` (opcional)
- `domain.enums` o `domain.enumtype` (elige uno y mantén)
- `infrastructure.persistence.entity`
- `infrastructure.persistence.repository`
- `infrastructure.persistence.query` (opcional)
- `infrastructure.mapper`

### Evitar
- `utils` gigantes por módulo
- `helpers` ambiguos
- `misc`
- paquetes “temporales” permanentes

---

## 5. Convenciones de nombres de clases (Java/Spring)

## 5.1. Interfaces (sin prefijo `I`)

Para este proyecto se seguirá la convención típica de Java/Spring:

- ✅ `EstudianteService`
- ✅ `JwtTokenService`
- ❌ `IEstudianteService`
- ❌ `IJwtTokenService`

**Motivo:** mayor alineación con ecosistema Java/Spring y mejor legibilidad.

> Nota: Si en el futuro implementas un patrón de puertos/SPI más formal, se puede usar nombres como `PasswordHasher` / `TokenGenerator` sin prefijo `I` igualmente.

---

## 5.2. Implementaciones de interfaces (`Impl`)

Usar sufijo `Impl` **solo cuando exista una interfaz pública asociada**.

### Ejemplos
- `EstudianteServiceImpl`
- `JwtTokenServiceImpl`
- `ReporteSolicitudWorkerImpl` (solo si existe interfaz `ReporteSolicitudWorker`)

### Regla
- Si no hay interfaz, no agregar `Impl` “por costumbre”.

✅ Menos ruido, más intención.

---

## 5.3. Controllers

Usar sufijo `Controller`.

### Ejemplos
- `AuthController`
- `EstudianteController`
- `SeccionController`
- `DashboardController`
- `ReporteController`

### Regla
Un controller representa un módulo o subcapacidad coherente, no una mezcla arbitraria de endpoints.

---

## 5.4. Servicios de aplicación (casos de uso / orquestación)

Como el proyecto está alineado con `application`, se recomienda usar nombres explícitos que reflejen su rol.

### Convenciones válidas (elige una por módulo y sé consistente)

#### Opción A (recomendada en este paquete)
- `EstudianteApplicationService`
- `SeccionApplicationService`
- `DashboardApplicationService`
- `MatriculaOperativaApplicationService`

#### Opción B (válida si quieres más “vieja confiable”)
- `EstudianteService`
- `SeccionService`
- `DashboardService`

### Regla importante
Los servicios deben modelar **casos de uso/capacidades**, no ser simples wrappers de repository.

---

## 5.5. Servicios de consulta (opcional)

Cuando un módulo crezca y mezcle demasiadas lecturas con escrituras, se puede separar:

- `EstudianteQueryService`
- `DashboardQueryService`
- `ReporteConsultaApplicationService`

✅ Útil cuando mejora claridad; no obligatorio desde el día 1.

---

## 5.6. Repositories

Usar sufijo `Repository`.

### Ejemplos
- `EstudianteRepository`
- `SeccionRepository`
- `CalificacionRepository`
- `ReporteSolicitudRepository`

### Regla
Repository = persistencia/consulta, no lógica de negocio.

---

## 5.7. Mappers manuales

Usar sufijo `Mapper`.

### Ejemplos
- `EstudianteMapper`
- `SeccionMapper`
- `CalificacionMapper`
- `ReporteSolicitudMapper`

### Regla
El mapper transforma datos. No:
- consulta BD,
- decide estados,
- aplica RN complejas,
- decide HTTP status.

---

## 5.8. Entidades JPA

Usar nombres del dominio en singular.

### Ejemplos
- `Estudiante`
- `Seccion`
- `Docente`
- `Asignatura`
- `Clase`
- `Calificacion`
- `Usuario`
- `ReporteSolicitud`

### Regla
No usar sufijo `Entity` salvo que exista ambigüedad real que lo justifique.

---

## 5.9. Excepciones

Usar sufijo `Exception`.

### Ejemplos
- `BusinessRuleException`
- `ResourceNotFoundException`
- `ConflictException`
- `UnauthorizedException`
- `ForbiddenException`
- `InvalidStateTransitionException`

**Regla:** el nombre debe expresar el tipo de error, no el endpoint.

---

## 5.10. Clases comunes de respuesta

Ubicadas típicamente en `common.response`.

### Ejemplos
- `ApiResponse<T>`
- `ApiErrorResponse`
- `PageResponseDto<T>`
- `ResponseMeta`

---

## 6. Convenciones de nombres para DTOs

## 6.1. Regla general

Los DTOs deben nombrarse según **el contrato que representan**, no solo por la entidad base.

✅ Esto ayuda mucho cuando un caso de uso cruza varias entidades.

---

## 6.2. Request DTOs

Usar sufijos explícitos:

- `CreateRequestDto`
- `UpdateRequestDto`
- `PatchRequestDto` (si aplica)
- `FilterRequestDto` (solo si realmente usas body para filtros)

### Ejemplos
- `LoginRequestDto`
- `EstudianteCreateRequestDto`
- `EstudianteUpdateRequestDto`
- `CalificacionCreateRequestDto`
- `AsignacionSeccionVigenteRequestDto`
- `ReporteSolicitudCreateRequestDto`

---

## 6.3. Response DTOs

Usar sufijos explícitos y diferenciados cuando haga falta.

### Ejemplos
- `LoginResponseDto`
- `EstudianteResponseDto`
- `EstudianteListItemResponseDto`
- `SeccionComboItemDto`
- `DashboardResumenResponseDto`
- `ReporteSolicitudResponseDto`
- `ReporteSolicitudEstadoResponseDto`

---

## 6.4. DTOs de listado / fila / resumen / proyección

Usar nombres que describan el uso real:

- `...ListItemResponseDto`
- `...ResumenResponseDto`
- `...RowDto`
- `...ComboItemDto`
- `...MetricDto`

### Ejemplos
- `EstudianteListItemResponseDto`
- `DashboardCupoPorSeccionMetricDto`
- `ReporteCalificacionesRowDto`

---

## 6.5. DTOs paginados

No crear un DTO paginado distinto por módulo si ya existe `PageResponseDto<T>` en `common`.

### Convención recomendada
- `ApiResponse<PageResponseDto<EstudianteListItemResponseDto>>`

✅ Evita explosión innecesaria de clases.

---

## 6.6. DTOs de orquestación (multi-entidad)

Cuando el caso de uso cruza entidades, el DTO debe nombrarse por el **caso de uso**, no por una tabla.

### Ejemplos
- `RegistroEstudianteOperativoRequestDto`
- `AsignacionSeccionVigenteRequestDto`
- `AsignarDocenteClaseRequestDto`
- `DashboardResumenOperativoResponseDto`

---

## 7. Convenciones de nombres para métodos

## 7.1. Métodos de servicio/aplicación

Nombrar con verbo + intención de negocio.

### Ejemplos
- `registrarEstudiante(...)`
- `actualizarSeccion(...)`
- `asignarSeccionVigente(...)`
- `cambiarSeccionVigente(...)`
- `registrarCalificacion(...)`
- `solicitarReporte(...)`
- `consultarEstadoSolicitudReporte(...)`

### Evitar
- `process(...)`
- `handle(...)` (salvo handlers reales)
- `execute(...)` genérico en todos lados
- `doStuff(...)`

---

## 7.2. Métodos de repositorio (Spring Data / consultas)

Usar nombres explícitos y alineados con intención de consulta.

### Ejemplos
- `existsByCedula(String cedula)`
- `findByIdAndActivoTrue(Long id)`
- `findByEstado(EstadoSeccion estado)`
- `countBySeccionId(Long seccionId)`

Para consultas complejas, preferir métodos con nombre claro + `@Query`.

---

## 7.3. Métodos booleanos

Usar prefijos semánticos:
- `is...`
- `has...`
- `can...`

### Ejemplos
- `isActivo()`
- `hasCupoDisponible()`
- `canCambiarSeccion(...)`

---

## 7.4. Métodos de transición de estado (DDD-lite)

Cuando existan invariantes o estados sensibles, preferir métodos con intención en lugar de setters abiertos.

### Ejemplos
- `activar()`
- `inactivar()`
- `marcarPendiente()`
- `marcarEnProceso()`
- `marcarCompletada(...)`
- `marcarError(...)`
- `cancelar()`

✅ Esto es especialmente importante en `reporte` y otros módulos con estados.

---

## 8. Convenciones para enums y estados

## 8.1. Nombres de enum y constantes

- Nombre del enum en `PascalCase`
- Constantes en `UPPER_SNAKE_CASE`

### Ejemplo
```java
public enum EstadoReporteSolicitud {
    PENDIENTE,
    EN_PROCESO,
    COMPLETADA,
    ERROR,
    CANCELADA
}
```

---

## 8.2. Consistencia de estados (corrección transversal)

Para este backend V1 se fija la convención del módulo de reportes/cola:

- `PENDIENTE`
- `EN_PROCESO`
- `COMPLETADA`
- `ERROR`
- `CANCELADA` (opcional)

❌ Evitar mezclar con variantes como:
- `PROCESANDO`
- `COMPLETADO`

si en el resto de la documentación quedó estandarizado de otra forma.

---

## 9. Convenciones de endpoints y rutas (naming HTTP)

## 9.1. Base path

Convención V1:

- `/api/v1/...`

### Ejemplos
- `/api/v1/auth/login`
- `/api/v1/estudiantes`
- `/api/v1/secciones`
- `/api/v1/dashboard/resumen`

---

## 9.2. Rutas en plural para recursos

Convención general:
- usar nombres de recursos en plural

### Ejemplos
- `/estudiantes`
- `/docentes`
- `/asignaturas`
- `/clases`

Se admiten rutas más expresivas para orquestación, con criterio:
- `/matricula-operativa/estudiantes`
- `/estudiantes/{id}/seccion-vigente`

---

## 9.3. Nombres de path variables

Usar nombres claros y estables:
- `{id}` cuando el contexto ya define el recurso
- `{estudianteId}`, `{seccionId}` cuando hay varios IDs en la misma ruta

### Ejemplos
- `/estudiantes/{id}`
- `/secciones/{seccionId}/cupos`
- `/clases/{claseId}/docente/{docenteId}` (si se usa ese estilo)

---

## 9.4. Query params de listados (convención V1)

Convención estandarizada:
- `page`
- `size`
- `sort`
- `q` ← búsqueda simple textual

### Ejemplo
```http
GET /api/v1/estudiantes?page=0&size=20&sort=apellidos,asc&q=juan&estado=ACTIVO&seccionId=4
```

### Corrección de consistencia
En este paquete V1 se usa **`q`** como parámetro base de búsqueda simple.

❌ Evitar mezclar `q` y `search` como convenciones paralelas salvo decisión explícita futura.

---

## 9.5. Parámetros de filtro

Nombrar filtros por semántica de dominio y tipo de dato.

### Ejemplos
- `estado`
- `seccionId`
- `docenteId`
- `asignaturaId`
- `claseId`
- `numeroParcial`
- `fechaDesde`
- `fechaHasta`

Evitar nombres ambiguos:
- `type`
- `value`
- `filter1`

---

## 10. Convenciones para API response y errores (uso en código)

## 10.1. Respuestas exitosas

Usar contrato estándar definido en `05`:

- `ApiResponse<T>` para respuestas no paginadas
- `ApiResponse<PageResponseDto<T>>` para listados paginados

### Regla de estilo
No devolver directamente entidades JPA ni mapas genéricos (`Map<String, Object>`) como contrato principal del backend.

---

## 10.2. Errores

Todos los errores de API deben salir vía handler global y contrato común (`ApiErrorResponse`).

### Regla de estilo
- No construir errores JSON “a mano” en cada controller.
- No devolver strings sueltos como error (`"No encontrado"`) en endpoints de negocio.

---

## 10.3. Placeholders / en construcción

Cuando un endpoint se deja intencionalmente pendiente:
- usar convención documentada en `06/09`
- preferir respuesta controlada y consistente
- `501 Not Implemented` es válido cuando realmente aún no existe implementación

---

## 11. Convenciones de entidades JPA y mutabilidad controlada

## 11.1. Regla general de encapsulación (DDD-lite)

Evitar setters públicos innecesarios en entidades con invariantes o estados relevantes.

### Preferir
- constructores/métodos de fábrica (cuando aplique)
- métodos con intención
- setters restringidos o ausencia de setter para campos controlados

### Ejemplo conceptual
En vez de:
- `setEstado(...)`

preferir (cuando hay reglas):
- `activar()`
- `inactivar()`
- `marcarEnProceso()`

---

## 11.2. Compatibilidad con JPA

Mantener convenciones compatibles con ORM:

- constructor vacío `protected` o `package-private` (según necesidad)
- campos privados
- anotaciones JPA en entidad
- colecciones inicializadas con cuidado

✅ Encapsulación sí, pero compatible con JPA/Hibernate.

---

## 11.3. `equals` y `hashCode`

En V1, usar una estrategia simple y consistente (defínela una vez y respétala).

### Recomendación pragmática
- no improvisar `equals/hashCode` complejos en todas las entidades al inicio
- si usas Lombok (si decides usarlo), documentar claramente la estrategia
- si no usas Lombok, implementar solo cuando realmente sea necesario y con criterio de identidad

> Si el equipo no domina aún esta parte, es mejor una política conservadora que una implementación incorrecta.

---

## 12. Convenciones para mappers manuales

## 12.1. Responsabilidad del mapper

El mapper manual transforma datos entre capas/contratos:

- `RequestDto -> entidad/comando`
- `Entidad -> ResponseDto`
- `Proyección -> ResponseDto`
- actualización controlada de entidad (si se decide patrón específico)

---

## 12.2. Qué NO debe hacer un mapper

- consultar repositorios
- validar RN complejas
- decidir permisos/roles
- lanzar errores HTTP
- abrir/cerrar transacciones

✅ Si un mapper empieza a hacer eso, está invadiendo `application`.

---

## 12.3. Nombres de métodos de mapper (sugeridos)

### De entidad a response
- `toResponseDto(...)`
- `toListItemResponseDto(...)`
- `toComboItemDto(...)`

### De request a entidad/comando
- `toEntity(...)`
- `toCreateCommand(...)` (si introduces commands)

### Actualización parcial/controlada
- `applyUpdate(Entidad entidad, UpdateRequestDto dto)`

> `applyUpdate(...)` es útil, pero la RN debe vivir fuera del mapper.

---

## 12.4. Ubicación de mappers

Convención recomendada:
- mappers por módulo en `infrastructure.mapper`

Si en algún módulo un mapper está claramente ligado al contrato API y no a persistencia, puedes ubicarlo cerca de `api` o `application`, pero evita dispersión arbitraria.

✅ Regla principal: consistencia por módulo.

---

## 13. Convenciones de validación (Bean Validation + negocio)

## 13.1. Validaciones de request (`VR-*`)

Se aplican principalmente en DTOs de entrada usando Jakarta Validation (`@NotNull`, `@Size`, etc.).

### Ejemplo de criterio
- formato de texto
- longitud
- requerido
- rango simple

### Estilo
Mantener mensajes claros (aunque los códigos de error finales se centralicen).

---

## 13.2. Reglas de negocio (`RN-*`)

Se implementan principalmente en `application` y, cuando aplique, invariantes del dominio.

### Ejemplos
- cupo de sección
- unicidad de negocio compuesta
- transición de estado inválida
- operación no permitida por estado

✅ No meter RN en anotaciones del DTO si requieren contexto de BD o múltiples entidades.

---

## 13.3. Excepciones y códigos

Usar clases de excepción + códigos trazables (`RN-`, `VR-`, `AUTH-`, `API-`, `SYS-`) según se formalice en `07`.

### Regla de estilo
Si una excepción representa una regla conocida del negocio, incluir trazabilidad de forma explícita (en el código, metadata o constructor).

---

## 14. Convenciones de comentarios y Javadoc

Esta sección responde a tu preferencia explícita: **comentarios útiles cuando aporten valor**, no por rellenar.

## 14.1. Regla principal

- Si el código ya se entiende solo, no comentar obviedades.
- Si hay contexto de negocio, RN, decisión técnica o restricción no obvia, sí comentar.

---

## 14.2. Cuándo usar comentarios/Javadocs (sí recomendado)

### Sí usar en:
- métodos de caso de uso importantes (`application`)
- lógica de orquestación multi-entidad
- reglas de transición de estado no triviales
- consultas complejas (`@Query`) con intención no evidente
- decisiones temporales / deuda técnica documentada
- clases de configuración o seguridad con comportamiento delicado

---

## 14.3. Cuándo NO usar comentarios (evitar ruido)

### Evitar comentarios tipo:
- `// asigna valor`
- `// obtiene estudiante`
- `// valida si es null`

si el código ya lo dice claramente.

---

## 14.4. Formato recomendado de Javadoc para casos de uso

Este formato está alineado con lo que tú pediste (caso de uso + descripción + trazabilidad).

```java
/**
 * Caso de uso: Asignar sección vigente.
 * Descripción: Asigna o cambia la sección de un estudiante validando cupo,
 * estado de la sección y consistencia operativa.
 * Reglas aplicadas: RN-12, RN-13, RN-15.
 * Validaciones relacionadas: VR-04, VR-09.
 */
public void asignarSeccionVigente(...) {
    ...
}
```

### Regla práctica
Usar este formato en métodos importantes, no en cada getter/setter o mapper trivial.

---

## 14.5. Comentarios de deuda técnica / placeholder

Cuando una capacidad quede diferida (ej. tabla puente faltante, endpoint placeholder), documentarlo claramente.

### Ejemplo de estilo
```java
// TODO[V1-PENDIENTE-BD]: Reemplazar lógica derivada por asociación real docente_seccion
// cuando exista migración de tabla puente.
```

✅ Mejor que TODOs vagos sin contexto.

---

## 15. Convenciones de logging

## 15.1. Objetivo del logging en V1

Logging para:
- diagnóstico técnico,
- auditoría básica de flujos,
- errores y advertencias relevantes.

No usar logs para reemplazar manejo de errores.

---

## 15.2. Estilo de logs

### Recomendado
- mensajes cortos y con contexto útil
- parámetros estructurados/placeholder (`{}`)
- no concatenar strings innecesariamente

### Ejemplo conceptual
- `"Solicitud de reporte creada. solicitudId={}, usuarioId={}"`

---

## 15.3. No loggear información sensible

❌ No registrar en logs:
- contraseñas
- tokens JWT completos
- datos sensibles innecesarios
- stacktrace repetido en múltiples capas

✅ Si hay error, registrar una vez con contexto y dejar que el handler responda sanitizado.

---

## 15.4. Niveles de log (criterio V1)

- `INFO`: eventos operativos relevantes (arranque, creación de solicitud, cambios importantes)
- `WARN`: situaciones anómalas recuperables o intentos inválidos esperables
- `ERROR`: fallos reales
- `DEBUG`: detalle técnico para desarrollo (no depender de él para lógica)

---

## 16. Convenciones para seguridad y autenticación (código)

## 16.1. Separación conceptual

- `security/` = infraestructura de seguridad (filtro JWT, config, token service)
- `auth/` = casos de uso funcionales (login, usuario actual, etc.)

No mezclar lógica académica en `security`.

---

## 16.2. Nombres sugeridos en seguridad

- `SecurityConfig`
- `JwtAuthenticationFilter`
- `JwtTokenService`
- `JwtProperties`
- `CustomUserDetailsService` (si aplica)

---

## 16.3. Convenciones de endpoints públicos/protegidos

La definición exacta va en `09`, pero en código se recomienda:
- dejar explícito en `SecurityConfig` qué rutas son públicas
- no depender de “suposiciones” del controller

---

## 17. Convenciones de transacciones y capa de aplicación

## 17.1. Ubicación de `@Transactional`

Regla general:
- `@Transactional` en **capa de aplicación**, no en controller.

### Motivo
La transacción pertenece al caso de uso, no a HTTP.

---

## 17.2. Lecturas complejas

Uso permitido de:
- `@Transactional(readOnly = true)`

cuando aporte claridad o consistencia en operaciones de lectura.

---

## 17.3. No abrir transacciones en mappers/utilidades

Mappers y utilidades no deben manejar transacciones.

---

## 18. Convenciones de paginación, filtros y consultas en código

## 18.1. Parámetros base de listados

Convención V1 transversal:
- `page`
- `size`
- `sort`
- `q`

### Regla de consistencia
No usar `search` en algunos endpoints y `q` en otros como convención principal.

✅ `q` queda estandarizado para búsqueda simple textual.

---

## 18.2. Límites y defaults

Los valores exactos se formalizan en `08`, pero como convención de código:

- centralizar defaults/límites (constantes o config)
- no hardcodear números distintos por controller sin criterio

### Ejemplos de constantes (referenciales)
- `DEFAULT_PAGE_SIZE`
- `MAX_PAGE_SIZE`

---

## 18.3. Ordenamiento seguro

Definir whitelist de campos ordenables por endpoint/listado cuando aplique.

❌ No aceptar `sort` arbitrario sobre cualquier campo sin control.

---

## 19. Convenciones de estilo para controllers

## 19.1. Controllers delgados

Un controller debe:
- recibir request
- validar (`@Valid`)
- delegar a `application`
- devolver contrato estándar

No debe:
- consultar múltiples repositorios,
- aplicar RN complejas,
- construir errores manuales inconsistentes.

---

## 19.2. Métodos de controller (naming)

Usar verbos claros y alineados al endpoint.

### Ejemplos
- `crear(...)`
- `actualizar(...)`
- `obtenerPorId(...)`
- `listar(...)`
- `iniciarSesion(...)`
- `solicitarReporte(...)`
- `consultarEstadoReporte(...)`

> También es válido usar inglés si TODO el proyecto está en inglés. En este backend documental, el dominio viene más natural en español. Lo importante es ser consistente.

---

## 19.3. Swagger/OpenAPI annotations

Cuando se usen anotaciones, mantener estilo consistente en títulos/descripciones.

### Recomendación
- resumen corto de endpoint
- descripción con contexto de negocio cuando haga falta
- mencionar restricciones importantes (RN/roles) si aporta

---

## 20. Convenciones de estilo para capa `application`

## 20.1. Métodos centrados en caso de uso

Cada método debe representar una operación significativa del sistema.

✅ Bien:
- `asignarSeccionVigente(...)`
- `registrarCalificacion(...)`
- `solicitarReporte(...)`

⚠️ Señal de alerta:
- clases gigantes con 30 métodos heterogéneos
- métodos genéricos `procesar(...)` sin intención clara

---

## 20.2. Orquestación explícita

Cuando el caso de uso toca varias entidades:
- mantener el flujo explícito,
- no esconder pasos críticos en utilidades genéricas.

Esto ayuda a trazabilidad (RN/VR) y depuración.

---

## 20.3. Excepciones lanzadas con intención

En lugar de lanzar `RuntimeException` genérica, preferir excepciones del dominio/aplicación con código trazable.

✅ Mejora `07` + handler global + contrato de errores.

---

## 21. Convenciones de estilo para repositorios y consultas

## 21.1. Repositorios simples

Usar Spring Data JPA para CRUD y consultas derivadas simples.

## 21.2. Consultas complejas

Para consultas complejas:
- usar `@Query` (JPQL o nativa) con nombre claro del método
- documentar si la intención no es obvia
- devolver proyección/DTO cuando convenga (no siempre entidad completa)

---

## 21.3. Ubicación de consultas complejas

Opciones válidas (elige y mantén consistencia por módulo):
- en `Repository` con `@Query`
- en paquete `infrastructure.persistence.query` si crece el volumen/complexidad

No dispersar SQL/JPQL por `application`.

---

## 22. Convenciones de estilo para `common`

## 22.1. Qué sí puede vivir en `common`

- `ApiResponse`
- `ApiErrorResponse`
- `PageResponseDto`
- `GlobalExceptionHandler`
- excepciones base
- catálogo de códigos de error (si se define)
- utilidades realmente transversales y pequeñas

---

## 22.2. Qué no debe vivir en `common`

- lógica de negocio de estudiantes/secciones/calificaciones
- mappers de módulos concretos
- queries SQL de módulos
- “helper” multiuso sin responsabilidad clara

✅ Regla: `common` sirve al backend; no absorbe el backend.

---

## 23. Convenciones de formato del código (equipo/proyecto)

## 23.1. Indentación y llaves

- indentación consistente (4 espacios si no se automatiza con formatter)
- llaves en estilo estándar Java
- una sentencia por línea

Se recomienda configurar formatter del IDE y mantenerlo estable.

---

## 23.2. Longitud de métodos y clases (criterio práctico)

No hay número mágico rígido, pero sí señales de alerta:

### Método sospechoso si:
- mezcla validación + persistencia + mapeo + respuesta + logging + seguridad
- cuesta entender su propósito en una lectura

### Clase sospechosa si:
- concentra demasiados casos de uso heterogéneos
- termina siendo “servicio dios”

✅ Refactorizar cuando el diseño se vuelva difícil de leer, no por obsesión métrica.

---

## 23.3. Evitar magia numérica/string

Extraer constantes para:
- límites de paginación
- nombres de claims JWT (si aplica)
- prefijos de rutas repetidos (si realmente aporta)
- mensajes técnicos repetidos

No extraer constantes triviales solo por “cumplir”.

---

## 24. Convenciones para nombres de variables y parámetros

## 24.1. Nombres semánticos

Preferir nombres con intención de dominio:
- `estudianteId`
- `seccionDestinoId`
- `usuarioAutenticadoId`
- `solicitudReporte`

Evitar:
- `id1`, `id2`
- `obj`, `data`, `value`
- `x`, `tmp` (salvo contextos muy locales y obvios)

---

## 24.2. Colecciones

Usar plural cuando representa varios elementos:
- `estudiantes`
- `seccionesActivas`
- `calificaciones`

Para un elemento único, singular:
- `estudiante`
- `seccion`

---

## 24.3. Booleanos

Nombrar como estado/pregunta:
- `activo`
- `tieneCupo`
- `puedeEditar`
- `esAdmin`

---

## 25. Convenciones para documentación interna de reglas (RN/VR/AUTH/API)

## 25.1. Objetivo de la trazabilidad

La trazabilidad permite conectar:
- documento funcional (`04_reglas_negocio_y_supuestos.md`),
- implementación backend,
- excepciones y errores API,
- y luego tests/documentación.

---

## 25.2. Convención de códigos (referencial)

La definición oficial se aterriza en `07`, pero como estándar de código:

- `VR-XX` → validaciones de entrada
- `RN-XX` → reglas de negocio
- `AUTH-XX` → autenticación/autorización
- `API-XX` → errores de contrato/endpoint
- `SYS-XX` → errores técnicos internos

> Si luego decides formato `RN-03-<slug>`, mantener esa misma convención en todo el backend.

---

## 25.3. Dónde referenciar estos códigos

- Javadocs de casos de uso importantes
- excepciones/metadata de error
- documentación de endpoints (Swagger) cuando aporte
- comentarios de decisiones temporales

---

## 26. Plantillas de estilo (referenciales)

## 26.1. Plantilla de controller (esqueleto conceptual)

```java
@RestController
@RequestMapping("/api/v1/estudiantes")
public class EstudianteController {

    private final EstudianteApplicationService estudianteApplicationService;

    public EstudianteController(EstudianteApplicationService estudianteApplicationService) {
        this.estudianteApplicationService = estudianteApplicationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDto<EstudianteListItemResponseDto>>> listar(...) {
        ...
    }
}
```

### Puntos de convención visibles
- nombre claro
- inyección por constructor
- contrato estándar
- controller delgado

---

## 26.2. Plantilla de método de aplicación con trazabilidad (conceptual)

```java
/**
 * Caso de uso: Cambiar sección vigente de estudiante.
 * Descripción: Actualiza la sección del estudiante validando cupo y estado.
 * Reglas aplicadas: RN-12, RN-13, RN-15.
 */
@Transactional
public void cambiarSeccionVigente(...) {
    ...
}
```

---

## 26.3. Plantilla de mapper manual (conceptual)

```java
@Component
public class EstudianteMapper {

    public EstudianteResponseDto toResponseDto(Estudiante estudiante) {
        ...
    }

    public void applyUpdate(Estudiante estudiante, EstudianteUpdateRequestDto dto) {
        ...
    }
}
```

✅ `applyUpdate(...)` puede existir, pero sin RN complejas.

---

## 27. Antipatrones que este documento busca evitar

## 27.1. Prefijo `I` por costumbre en interfaces

❌ `IEstudianteService`

✅ `EstudianteService`

---

## 27.2. DTO único para crear/actualizar/detalle/listado

❌ un solo DTO “universal” para todo

✅ DTOs por contrato/caso de uso

---

## 27.3. Controllers con lógica de negocio

❌ controller consultando varios repositorios y validando RN

✅ controller delega a `application`

---

## 27.4. Mappers “inteligentes” que hacen de todo

❌ mapper con consultas BD, permisos y RN

✅ mapper = transformación de datos

---

## 27.5. `common` como cajón de sastre

❌ meter cualquier helper o lógica “porque se usa en dos módulos”

✅ `common` solo para piezas genuinamente transversales

---

## 27.6. Inconsistencia de query params (`search` vs `q`)

❌ mezclar dos convenciones base en listados

✅ estandarizar `q` para búsqueda simple V1

---

## 27.7. Estados con nombres inconsistentes

❌ `PROCESANDO` en un lado y `EN_PROCESO` en otro

✅ usar enum/documentación estandarizada (`PENDIENTE`, `EN_PROCESO`, `COMPLETADA`, `ERROR`, `CANCELADA`)

---

## 28. Checklist rápido de revisión de código (por archivo o PR)

Antes de dar un módulo/método por bueno, revisar:

### 28.1. Estructura
- [ ] ¿Está en el paquete/capa correcta?
- [ ] ¿El nombre de la clase refleja su responsabilidad?
- [ ] ¿Evita mezclar responsabilidades?

### 28.2. Convenciones
- [ ] ¿Respeta naming Java/Spring?
- [ ] ¿DTOs tienen nombres por contrato?
- [ ] ¿No hay prefijo `I` innecesario?

### 28.3. Arquitectura
- [ ] ¿Controller delgado?
- [ ] ¿RN en `application`/`domain` y no en mapper/controller?
- [ ] ¿No se expone entidad JPA por API?

### 28.4. Consistencia transversal
- [ ] ¿Usa `q` como búsqueda simple?
- [ ] ¿Estados/enums están alineados con docs?
- [ ] ¿Errores usan convenciones de `07`/`05`?

### 28.5. Comentarios y trazabilidad
- [ ] ¿Hay comentarios solo donde aportan?
- [ ] ¿Los casos de uso críticos tienen Javadoc útil con RN/VR?
- [ ] ¿No hay TODOs vagos sin contexto?

---

## 29. Relación con documentos siguientes

- **`05_backend_v1_diseno_api_contrato_respuestas_y_errores.md`**
 define la forma final de `ApiResponse`, `ApiErrorResponse` y mapeo de errores.

- **`06_backend_v1_api_endpoints_y_casos_de_uso.md`**
 aplicará estas convenciones de nombres a rutas, controllers y DTOs por endpoint.

- **`07_backend_v1_validaciones_reglas_negocio_y_excepciones.md`**
 formaliza la trazabilidad RN/VR/AUTH/API/SYS y la política de excepciones.

- **`08_backend_v1_paginacion_filtros_ordenamiento_y_consultas.md`**
 aterriza defaults/límites y reglas de `page/size/sort/q`.

- **`09_backend_v1_seguridad_documentacion_y_despliegue_minimo.md`**
 formaliza detalles de seguridad y configuración que aquí se mencionan como estilo.

---

## 30. Cierre

Estas convenciones están diseñadas para que el backend V1 tenga una base de código:

- profesional,
- comprensible,
- consistente con Spring/Java,
- alineada con tu objetivo de aprender bien,
- y preparada para crecer sin desorden.

La idea no es “poner reglas por poner reglas”, sino reducir fricción mental y evitar errores repetitivos mientras construyes el backend real.


