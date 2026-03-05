# 07_backend_v1_validaciones_reglas_negocio_y_excepciones

- **Versión:** 0.2
- **Estado:** En revisión (reconstruido por consistencia)
- **Ámbito:** Backend V1 (Spring Boot + Java 21)
- **Depende de:** `03_backend_v1_convenciones_y_estandares_codigo.md`, `04_backend_v1_modelado_aplicacion_y_modulos.md`, `05_backend_v1_diseno_api_contrato_respuestas_y_errores.md`
- **Relacionados:** `06_backend_v1_api_endpoints_y_casos_de_uso.md`, `09_backend_v1_seguridad_documentacion_y_despliegue_minimo.md`, `10_backend_v1_reporte_solicitudes_cola_simple_db_queue.md`
- **Referencias de contexto:** `04_reglas_negocio_y_supuestos.md`, `02_levantamiento_requerimientos.md`, `V2_3FN.sql`
- **Objetivo de esta revisión:** Definir una política clara y consistente para validaciones, reglas de negocio y excepciones del backend V1, con trazabilidad (`VR/RN/AUTH/API/SYS`) y manejo centralizado.

---

## 1. Propósito del documento

Este documento define **qué se valida, dónde se valida, cómo se reporta el error y cómo se relaciona con las reglas del negocio** en el backend V1.

Su propósito es evitar estos problemas típicos:

- validaciones repetidas en varias capas,
- reglas de negocio metidas en controllers o mappers,
- excepciones genéricas sin trazabilidad,
- respuestas de error inconsistentes,
- dificultad para conectar el código con los documentos funcionales.

✅ Este documento aterriza la parte “seria” del backend: **disciplina de reglas y manejo de fallos**.

---

## 2. Principios rectores (V1)

## 2.1. Separar validación técnica de regla de negocio

No todo error es igual.

### Tipos principales
- **VR (Validación de Request):** formato, campos requeridos, rango simple, tipo de dato, JSON inválido.
- **RN (Regla de Negocio):** cupos, estados, transiciones, consistencia operativa, unicidad de negocio contextual.

✅ Esta separación mejora claridad, trazabilidad y diseño del handler global.

---

## 2.2. Validar lo más cerca posible de la entrada, pero no forzar reglas complejas en DTOs

- Validaciones simples de entrada → DTO (`@Valid`)
- Reglas que requieren BD/múltiples entidades/estado → `application` y/o dominio

❌ No meter RN complejas como anotaciones improvisadas en DTOs si dependen de consultas.

---

## 2.3. Las excepciones deben expresar intención

Se evita usar `RuntimeException` genérica para todo.

Las excepciones del backend deben decir:
- qué pasó,
- con qué código (`RN-*`, `VR-*`, etc.),
- y qué status HTTP corresponde.

---

## 2.4. El contrato de error lo define `05`, no cada módulo

Este documento define **la política y taxonomía**.

La salida HTTP concreta debe respetar el contrato estándar ya definido en:
- `ApiErrorResponse`
- `GlobalExceptionHandler`

✅ Una sola forma de responder errores.

---

## 2.5. DDD-lite pragmático: invariantes con mutabilidad controlada

Cuando una entidad tiene estados o transiciones relevantes, se prefiere:
- métodos con intención (`activar()`, `marcarEnProceso()`),
- validación de invariantes,
- y excepciones explícitas.

❌ Evitar setters públicos indiscriminados para campos sensibles.

---

## 3. Clasificación oficial de errores (V1)

## 3.1. Categorías adoptadas

Se formaliza la taxonomía usada en `05`:

- `VR-*` → validación de request / entrada
- `RN-*` → reglas de negocio
- `AUTH-*` → autenticación / autorización
- `API-*` → uso incorrecto de API / recurso / contrato HTTP
- `SYS-*` → errores internos / infraestructura / inesperados

### Estado aplicado en la implementacion actual

Al corte técnico del `2026-03-04`, esta política ya se refleja en los módulos CRUD principales:

- `ValidationException` para paginacion, filtros, formatos y rangos simples.
- `BusinessRuleException` para duplicados, cupos, estados no permitidos y coherencia academica.
- `ResourceNotFoundException` para `404` consistentes del modelo.
- `InfrastructureException` para fallos técnicos del procesamiento de reportes/archivos.

La consecuencia práctica es que la capa `application` ya no deberia modelar reglas funcionales comunes con `ResponseStatusException`.

---

## 3.2. Convención de formato de código

### Formato oficial V1 (recomendado)
- `CATEGORIA-XX-SLUG`

### Ejemplos
- `VR-01-REQUEST_INVALIDO`
- `VR-03-CUERPO_JSON_INVALIDO`
- `RN-12-CUPO_SECCION_AGOTADO`
- `AUTH-01-CREDENCIALES_INVALIDAS`
- `API-04-RECURSO_NO_ENCONTRADO`
- `SYS-01-ERROR_INTERNO`

> Si deseas usar exactamente una variante como `RN-03-<SLUG>` (que tú mismo sugeriste), esta convención ya lo permite. La clave es mantener **mismo formato y catálogo consistente**.

---

## 3.3. Qué significa el `SLUG`

El `SLUG` debe expresar la causa de forma breve y estable.

✅ Buenos ejemplos
- `CUPO_SECCION_AGOTADO`
- `ESTADO_REPORTE_INVALIDO`
- `PARAMETRO_INVALIDO`

❌ Malos ejemplos
- `ERROR1`
- `NO_SIRVE`
- `EXCEPCION_X`

---

## 4. Política de ubicación de validaciones por capa

## 4.1. Resumen ejecutivo (regla rápida)

### `api` (controller + DTO)
- valida sintaxis y estructura básica de entrada
- aplica `@Valid`
- delega

### `application`
- valida reglas de negocio y orquestación
- consulta repositorios
- valida estados y consistencia entre entidades
- lanza excepciones tipadas de negocio/API sin depender de detalles HTTP

### `domain`
- protege invariantes del modelo (si se implementan métodos con intención)
- valida transiciones internas simples/clave

### `infrastructure`
- no decide RN de negocio
- puede traducir errores técnicos a excepciones más claras si corresponde

---

## 4.2. Tabla de decisión práctica

## 4.2.1. Validación de request (VR)

Ejemplos:
- campo obligatorio
- longitud de texto
- rango simple
- tipo de dato
- enum inválido
- body JSON mal formado

**Dónde va:** DTO + controller (`@Valid`) + handler global

---

## 4.2.2. Regla de negocio (RN)

Ejemplos:
- sección sin cupo
- cambio de sección no permitido por estado
- reporte no puede pasar a `COMPLETADA` desde estado inválido
- calificación fuera de política del parcial/contexto

**Dónde va:** `application` y/o métodos de dominio con intención

---

## 4.2.3. Restricción técnica/API (API)

Ejemplos:
- recurso inexistente (`404`)
- endpoint no implementado (`501` placeholder)
- path param inválido

**Dónde va:** handler global + excepciones custom API

---

## 4.2.4. Seguridad (AUTH)

Ejemplos:
- credenciales inválidas
- token expirado
- acceso denegado por rol

**Dónde va:** `auth`, `security`, handlers de Spring Security + contrato común de error

---

## 5. Validaciones de request (`VR-*`) en detalle

## 5.1. Objetivo de `VR-*`

Detectar errores de entrada **antes** de ejecutar lógica de negocio.

Esto reduce ruido y protege la capa `application` de payloads claramente inválidos.

---

## 5.2. Tipos de VR cubiertos en V1

### 5.2.1. Presencia / obligatoriedad
- `@NotNull`
- `@NotBlank`
- `@NotEmpty`

### 5.2.2. Tamaño / longitud
- `@Size`

### 5.2.3. Rango numérico
- `@Min`
- `@Max`
- `@Positive`
- `@PositiveOrZero`

### 5.2.4. Formato
- `@Pattern`
- `@Email` (si aplica)

### 5.2.5. Tipado y parseo
- JSON inválido
- enum inválido
- path/query param tipo incorrecto

---

## 5.3. Política de mensajes de validación

Los mensajes de validación deben ser:
- claros,
- concretos,
- y útiles para UI (JavaFX).

### Ejemplos
- “El campo nombres es obligatorio.”
- “La longitud máxima de observación es 250 caracteres.”
- “El número de parcial debe ser mayor o igual a 1.”

✅ Evitar mensajes demasiado técnicos del framework como salida final al cliente.

---

## 5.4. `VR-*` por anotación vs `VR-*` por parseo

No todas las VR salen de anotaciones.

### Dos fuentes comunes
1. **Bean Validation (`@Valid`)**
   - genera errores por campo
2. **Errores de parseo/tipo**
   - JSON mal formado
   - enum inválido
   - path variable inválida

Ambas deben mapearse al mismo contrato `ApiErrorResponse` con categoría `VR-*`.

---

## 5.5. Estructura de `details` para VR

Alineado con `05`, se recomienda lista de detalles por campo cuando aplique.

### Campos sugeridos por item
- `field`
- `code`
- `message`
- `rejectedValue` (opcional y con cuidado)

✅ Muy útil para que JavaFX marque controles específicos.

---

## 6. Reglas de negocio (`RN-*`) en detalle

## 6.1. Qué es una RN en este backend

Una RN es una restricción o condición del dominio que:
- depende del contexto del negocio,
- puede requerir leer estado actual,
- puede involucrar múltiples entidades,
- y no puede resolverse solo con validación estructural del request.

---

## 6.2. Dónde implementar RN en V1 (recomendación)

### Principalmente en `application`
Porque allí vive la orquestación de casos de uso.

### Opcionalmente en `domain` (muy recomendable cuando hay estados)
Para invariantes y transiciones del objeto.

✅ V1 pragmática: `application` manda, `domain` protege lo crítico.

---

## 6.3. Tipos de RN frecuentes en este dominio backend (ejemplos)

### 6.3.1. Capacidad / cupo
- No asignar estudiante a sección sin cupo.
- Código sugerido: `RN-12-CUPO_SECCION_AGOTADO`

### 6.3.2. Estado de entidades
- No operar sobre secciones inactivas/cerradas (según política V1).
- Código sugerido: `RN-13-SECCION_NO_DISPONIBLE`

### 6.3.3. Transiciones de estado
- No marcar reporte como `COMPLETADA` si no está en `EN_PROCESO`.
- Código sugerido: `RN-30-TRANSICION_ESTADO_INVALIDA`

### 6.3.4. Consistencia entre entidades
- No registrar calificación si la clase/relación requerida no existe o no está vigente (según diseño V1).
- Código sugerido: `RN-21-CONTEXTO_ACADEMICO_INVALIDO`

### 6.3.5. Unicidad de negocio (no solo técnica)
- No crear duplicado lógico aunque la BD no tenga restricción completa todavía.
- Código sugerido: `RN-18-REGISTRO_DUPLICADO`

---

## 6.4. Regla crítica: una RN debe lanzar excepción con código trazable

No basta con `throw new RuntimeException("No se puede")`.

✅ Debe lanzar una excepción semántica con:
- `errorCode` (`RN-*`)
- mensaje útil
- status (`409`, `422`, etc.)

---

## 6.5. Trazabilidad con `04_reglas_negocio_y_supuestos.md`

Cuando una regla venga del documento de negocio, se recomienda referenciarla en:
- Javadoc del caso de uso,
- nombre de código `RN-*`,
- o comentario puntual si hay deuda técnica temporal.

### Ejemplo de estilo (Javadoc)
```java
/**
 * Caso de uso: Asignar sección vigente.
 * Reglas aplicadas: RN-12, RN-13, RN-15.
 */
```

✅ Esto te permite saltar del código al documento sin perderte.

---

## 7. Invariantes y mutabilidad controlada (DDD-lite)

## 7.1. Contexto

Tú mencionaste que querías evitar setters innecesarios y tener acceso controlado. Eso encaja perfecto aquí.

✅ Esta sección formaliza esa idea para V1.

---

## 7.2. Regla general para entidades con estado sensible

Evitar setters públicos para campos que afecten reglas o transiciones.

### Preferir
- métodos con intención:
  - `activar()`
  - `inactivar()`
  - `marcarPendiente()`
  - `marcarEnProceso()`
  - `marcarCompletada(...)`
  - `marcarError(...)`

---

## 7.3. ¿Dónde se lanza la excepción de transición inválida?

### Opción recomendada V1
- la entidad (o helper de dominio) valida transición y lanza excepción semántica
- `application` coordina el flujo y persiste

Si aún no implementas suficiente dominio rico, puedes validar en `application`, pero **documentando** que es una deuda de diseño futura.

---

## 7.4. Beneficio práctico

Esto evita errores como:
- cambiar estado “a mano” desde cualquier clase,
- saltarse pasos del flujo,
- inconsistencias entre reportes/cola.

---

## 8. Política de excepciones custom (jerarquía recomendada)

## 8.1. Objetivo

Tener una jerarquía mínima pero útil para:
- simplificar `GlobalExceptionHandler`
- mapear HTTP status de forma limpia
- transportar `errorCode` y `details`

---

## 8.2. Excepción base recomendada

Se recomienda una base común (alineada con `05`):

- `ApiBaseException extends RuntimeException`

### Campos recomendados
- `errorCode`
- `HttpStatus status`
- `details` (opcional)
- `message`

✅ Esto evita repetir lógica en todas las excepciones.

---

## 8.3. Excepciones derivadas mínimas (V1)

### 8.3.1. `BusinessRuleException`
- Para RN generales (`RN-*`)
- Status típico: `409 Conflict` (o `422` según caso)

### 8.3.2. `ResourceNotFoundException`
- Recurso de dominio no existe
- Código típico: `API-04-RECURSO_NO_ENCONTRADO`
- Status: `404`

### 8.3.3. `ConflictException`
- Conflictos no necesariamente “regla pura” pero sí de estado/duplicado/consistencia
- Status: `409`

### 8.3.4. `InvalidStateTransitionException`
- Especialización útil para módulos con workflow (`reporte`)
- Código típico: `RN-30-TRANSICION_ESTADO_INVALIDA`
- Status: `409`

### 8.3.5. `UnauthorizedException`
- `401` (`AUTH-*`) cuando se maneje fuera del filtro estándar

### 8.3.6. `ForbiddenException`
- `403` (`AUTH-*`) cuando se maneje fuera del flujo automático de Spring Security

### 8.3.7. `ApiNotImplementedException` (opcional pero muy útil)
- Para placeholders controlados
- Código: `API-10-ENDPOINT_EN_CONSTRUCCION`
- Status: `501`

✅ Esta última te sirve mucho para la estrategia que propusiste.

---

## 8.4. ¿Cuándo NO crear una excepción nueva?

No crear una clase nueva por cada regla (`CupoSeccionAgotadoException`, etc.) si eso complica más de lo que ayuda en V1.

### Recomendación V1 pragmática
- usar `BusinessRuleException` + `errorCode` trazable
- crear excepciones específicas solo cuando mejoren claridad real (ej. transición de estado)

---

## 9. Mapeo de categorías a status HTTP (política oficial V1)

## 9.1. Validaciones (`VR-*`)

### Status por defecto
- `400 Bad Request`

### Casos incluidos
- `@Valid`
- parseo de JSON
- parámetro de query/path inválido
- enum inválido

---

## 9.2. Reglas de negocio (`RN-*`)

### Status recomendado por defecto
- `409 Conflict`

### Uso opcional y controlado
- `422 Unprocessable Entity` si quieres distinguir semántica de negocio compleja

✅ Para V1, `409` como “vieja confiable” es totalmente válido y consistente.

---

## 9.3. Seguridad (`AUTH-*`)

- `401 Unauthorized` → credenciales/token
- `403 Forbidden` → permisos/roles

---

## 9.4. API (`API-*`)

- `404 Not Found`
- `405 Method Not Allowed`
- `501 Not Implemented` (placeholder)

---

## 9.5. Sistema (`SYS-*`)

- `500 Internal Server Error`
- `503 Service Unavailable` (opcional si más adelante lo necesitas)

---

## 10. Integración con `GlobalExceptionHandler` (alineación con `05`)

## 10.1. Regla central

Toda excepción relevante debe terminar mapeada a:
- status HTTP correcto
- `ApiErrorResponse` consistente

No devolver strings sueltos ni mapas improvisados.

---

## 10.2. Flujo recomendado

1. `Controller` recibe request
2. `@Valid` detecta VR simples (si aplica)
3. `Application` ejecuta caso de uso
4. Lanza excepción custom si hay RN/API/AUTH
5. `GlobalExceptionHandler` construye `ApiErrorResponse`

✅ Esto mantiene controllers delgados.

---

## 10.3. Excepciones framework que deben mapearse a `VR/API`

### Validación / parseo (VR)
- `MethodArgumentNotValidException`
- `ConstraintViolationException`
- `HttpMessageNotReadableException`
- `MethodArgumentTypeMismatchException`

### API/routing (API)
- `HttpRequestMethodNotSupportedException`
- `NoHandlerFoundException` (si se habilita)

### Fallback (SYS)
- `Exception`

---

## 10.4. Política de mensaje final al cliente

### Sí
- mensaje claro
- sin internals
- útil para UI/operación

### No
- stack trace
- SQL crudo
- nombres de clases internas del backend

---

## 11. Catálogo inicial sugerido de códigos (V1)

> Este catálogo es inicial/referencial. `06` y la implementación real pueden ampliarlo, pero deben respetar formato y consistencia.

## 11.1. VR (validación de request)

- `VR-01-REQUEST_INVALIDO`
- `VR-02-PARAMETRO_INVALIDO`
- `VR-03-CUERPO_JSON_INVALIDO`
- `VR-04-FORMATO_CEDULA_INVALIDO` *(si aplica al dominio/modelo V1)*
- `VR-05-RANGO_NUMERICO_INVALIDO`
- `VR-06-VALOR_ENUM_INVALIDO`
- `VR-07-CAMPO_REQUERIDO`

---

## 11.2. RN (reglas de negocio)

- `RN-12-CUPO_SECCION_AGOTADO`
- `RN-13-SECCION_NO_DISPONIBLE`
- `RN-15-CAMBIO_SECCION_NO_PERMITIDO`
- `RN-18-REGISTRO_DUPLICADO`
- `RN-21-CONTEXTO_ACADEMICO_INVALIDO`
- `RN-30-TRANSICION_ESTADO_INVALIDA`
- `RN-31-ESTADO_REPORTE_NO_PERMITE_OPERACION`

---

## 11.3. AUTH (autenticación/autorización)

- `AUTH-01-CREDENCIALES_INVALIDAS`
- `AUTH-02-TOKEN_INVALIDO`
- `AUTH-03-TOKEN_EXPIRADO`
- `AUTH-10-ACCESO_DENEGADO`

---

## 11.4. API (uso de API/recursos)

- `API-04-RECURSO_NO_ENCONTRADO`
- `API-05-METODO_NO_PERMITIDO`
- `API-06-RUTA_NO_ENCONTRADA`
- `API-10-ENDPOINT_EN_CONSTRUCCION`

---

## 11.5. SYS (sistema)

- `SYS-01-ERROR_INTERNO`
- `SYS-02-ERROR_PERSISTENCIA` *(opcional si quieres distinguirlo)*
- `SYS-03-DEPENDENCIA_NO_DISPONIBLE` *(opcional)*

---

## 12. Política de mensajes por categoría de error

## 12.1. VR (entrada)

Mensaje orientado a corrección del request.

✅ Ejemplo
- “La solicitud contiene errores de validación.”

---

## 12.2. RN (negocio)

Mensaje orientado a explicar por qué la operación no se puede ejecutar.

✅ Ejemplo
- “No se puede asignar la sección porque no hay cupos disponibles.”

---

## 12.3. AUTH (seguridad)

Mensaje corto y seguro (sin dar demasiada información).

✅ Ejemplos
- “Credenciales inválidas.”
- “Token inválido o expirado.”
- “No tiene permisos para acceder a este recurso.”

---

## 12.4. API (uso de endpoint)

Mensaje orientado a contrato/ruta/recurso.

✅ Ejemplos
- “El recurso solicitado no existe.”
- “El endpoint está definido pero aún no se encuentra implementado.”

---

## 12.5. SYS (interno)

Mensaje genérico y seguro.

✅ Ejemplo
- “Ocurrió un error interno. Intente nuevamente más tarde.”

❌ No exponer causa técnica completa al cliente.

---

## 13. Política de `details` por tipo de error

## 13.1. `VR-*` (validaciones)

`details` normalmente debe contener lista por campo (si aplica).

✅ Alta utilidad para JavaFX.

---

## 13.2. `RN-*` (reglas de negocio)

Por defecto, `details` puede ser `null`.

Solo incluir detalles si realmente ayudan al cliente sin acoplarlo a internals.

### Ejemplo útil
- `{"seccionId": 4, "cuposDisponibles": 0}`

⚠️ No convertir `details` en dumping de estado interno.

---

## 13.3. `AUTH-*`

Normalmente `details = null` por seguridad.

---

## 13.4. `API-*` / `SYS-*`

Generalmente `details = null`, salvo un detalle muy controlado y útil.

---

## 14. Casos de uso multi-entidad y validaciones de orquestación

## 14.1. Problema común

En módulos de orquestación (ej. matrícula operativa, dashboard, reportes) se tocan varias entidades.

La duda típica es: “¿Dónde válido todo esto?”

✅ Respuesta V1: principalmente en `application`.

---

## 14.2. Patrón recomendado de validación en orquestación

Dentro del servicio de aplicación:

1. Validar precondiciones de entrada no cubiertas por `@Valid` (si aplica)
2. Cargar entidades necesarias
3. Validar existencia (`404` / `API-*`)
4. Validar estados y contexto (`RN-*`)
5. Ejecutar operación
6. Persistir y responder

Esto mantiene el flujo explícito y trazable.

---

## 14.3. Ejemplo conceptual: asignar sección vigente

Posibles validaciones/errores:
- `VR-*`: request incompleto
- `API-*`: estudiante o sección no existe
- `RN-*`: sección sin cupo
- `RN-*`: sección no disponible
- `RN-*`: cambio de sección no permitido por estado/contexto

✅ Este ejemplo explica por qué `07` debe existir antes de cerrar `06`.

---

## 15. Casos especiales: reportes y cola simple (alineación con `10`)

## 15.1. Estados del workflow de reporte (consistencia obligatoria)

Para V1 se estandariza:
- `PENDIENTE`
- `EN_PROCESO`
- `COMPLETADA`
- `ERROR`
- `CANCELADA` (si se usa)

❌ No mezclar `PROCESANDO` / `COMPLETADO` si el resto del paquete usa la convención anterior.

---

## 15.2. Errores típicos del módulo de reportes/cola

### RN (workflow)
- transición inválida
- operación no permitida según estado

### API (placeholder)
- endpoint preview/descarga aún no implementado (`501`)

### SYS
- falla de persistencia o procesamiento inesperado

---

## 15.3. Excepción útil para este módulo

`InvalidStateTransitionException` (especialización) aporta claridad real en este caso.

✅ Aquí sí vale la pena una excepción específica.

---

## 16. Casos especiales: inconsistencias o deuda técnica de BD/documento

## 16.1. Principio

Si existe una inconsistencia temporal entre modelo de negocio/documento/BD (por ejemplo, relación puente faltante o simplificada), **no se debe ocultar**.

Se debe:
- documentar la deuda,
- proteger el backend con validaciones razonables,
- y usar códigos de error consistentes si la operación no puede garantizarse.

---

## 16.2. Ejemplo de deuda conocida (referencial del paquete)

La inconsistencia `docente ↔ seccion` (si aún no está formalizada completamente en BD) puede obligar a:
- validaciones de contexto más conservadoras,
- endpoints placeholder,
- o RN temporales.

### Recomendación
Documentar con `TODO` trazable en código y referencia en `06/10` cuando afecte un endpoint.

---

## 17. Integración con comentarios/Javadocs y trazabilidad (alineado con `03`)

## 17.1. Cuándo documentar RN/VR en código

Sí conviene documentarlo en:
- casos de uso importantes (`application`)
- transiciones de estado
- validaciones no obvias
- deuda técnica temporal

---

## 17.2. Formato recomendado de Javadoc (reiterado y formalizado)

```java
/**
 * Caso de uso: Solicitar generación de reporte.
 * Descripción: Registra una solicitud en cola simple (DB queue) con estado inicial PENDIENTE.
 * Reglas aplicadas: RN-30, RN-31.
 * Validaciones relacionadas: VR-01, VR-06.
 * Errores posibles: API-10, SYS-01.
 */
```

✅ Muy útil para ti cuando vuelvas al código después de varios días.

---

## 17.3. Comentarios de deuda técnica

Formato recomendado:

```java
// TODO[V1-PENDIENTE-BD]: Validación provisional por ausencia de tabla puente docente_seccion.
// Reemplazar al aplicar migración de modelo relacional.
```

---

## 18. Política de traducción de errores técnicos (infraestructura) a errores de API

## 18.1. Problema

Errores de BD/ORM suelen salir con mensajes poco aptos para cliente.

Ejemplos:
- violación de constraint
- timeout de DB
- excepción de persistencia

---

## 18.2. Política V1

- Log interno con detalle técnico
- Cliente recibe `SYS-*` (o `RN-*` si la causa fue detectada y traducida explícitamente)

✅ No pasar mensaje crudo de Hibernate/SQL al cliente.

---

## 18.3. Traducción temprana cuando sea posible

Si detectas antes del `save(...)` una condición de negocio (p. ej. duplicado lógico), preferir lanzar `RN-*` explícito en lugar de esperar a una excepción técnica.

✅ Mejor UX, mejor trazabilidad.

---

## 19. Checklist de implementación por endpoint/caso de uso

Antes de dar por bueno un caso de uso, revisar:

### 19.1. Entrada
- [ ] ¿El DTO tiene validaciones `@Valid` razonables?
- [ ] ¿Hay mensajes de validación claros?
- [ ] ¿Se manejan parseos/enum inválidos vía handler global?

### 19.2. Negocio
- [ ] ¿Las RN están en `application`/`domain` y no en controller?
- [ ] ¿Las RN lanzan excepción con `RN-*`?
- [ ] ¿Las transiciones de estado están protegidas?

### 19.3. Excepciones
- [ ] ¿Se usan excepciones semánticas (no `RuntimeException` genérica)?
- [ ] ¿El status HTTP es coherente con la categoría?
- [ ] ¿`details` tiene forma consistente?

### 19.4. Contrato de salida
- [ ] ¿El error termina en `ApiErrorResponse` estándar?
- [ ] ¿No se exponen internals?
- [ ] ¿El mensaje es útil para JavaFX/operación?

### 19.5. Trazabilidad
- [ ] ¿Los códigos (`VR/RN/AUTH/API/SYS`) siguen el formato oficial?
- [ ] ¿Las RN importantes están referenciadas en Javadocs/comentarios?

---

## 20. Decisiones fijadas por este documento (V1)

1. ✅ Se formaliza la taxonomía `VR / RN / AUTH / API / SYS`.
2. ✅ Se fija formato de código `CATEGORIA-XX-SLUG`.
3. ✅ `VR-*` = validación de entrada (DTO, parseo, tipos, JSON).
4. ✅ `RN-*` = reglas de negocio/contexto/estado.
5. ✅ Validaciones simples en `api`/DTO; RN en `application` y/o `domain`.
6. ✅ Excepciones custom semánticas con `errorCode + status`.
7. ✅ `GlobalExceptionHandler` centraliza la salida de error (`ApiErrorResponse`).
8. ✅ Transiciones de estado deben protegerse (mutabilidad controlada, DDD-lite).
9. ✅ Placeholders usan `API-10-ENDPOINT_EN_CONSTRUCCION` + `501`.
10. ✅ No exponer detalles internos técnicos al cliente.

---

## 21. Qué deja listo para el documento 06 (endpoints)

Con este documento ya queda claro que `06` deberá definir por endpoint:

- qué `@Valid` aplica (VR)
- qué RN pueden dispararse
- qué recursos pueden dar `404` (`API-*`)
- qué placeholders llevan `501`
- qué respuestas de error se documentan en Swagger

✅ En otras palabras: `07` le da el “sistema nervioso” a `06`.

---

## 22. Cierre

Este documento convierte las validaciones y errores en una parte **diseñada**, no improvisada, del backend V1.

Eso te da varias ventajas reales para aprender y trabajar profesionalmente:

- código más claro,
- menos retrabajo,
- mejor integración con JavaFX,
- trazabilidad con tus documentos de negocio,
- y una base muy buena para tu proyecto con Andrés.

Aquí ya estás haciendo backend “de verdad”, no solo endpoints que compilan.


