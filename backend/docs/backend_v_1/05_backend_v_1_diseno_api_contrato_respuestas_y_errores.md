# 05_backend_v1_diseno_api_contrato_respuestas_y_errores

- **Versión:** 0.2
- **Estado:** En revisión (reconstruido por consistencia)
- **Ámbito:** Backend V1 (Spring Boot + Java 21)
- **Depende de:** `02_backend_v1_arquitectura_general.md`, `03_backend_v1_convenciones_y_estandares_codigo.md`, `04_backend_v1_modelado_aplicacion_y_modulos.md`
- **Relacionados:** `06_backend_v1_api_endpoints_y_casos_de_uso.md`, `07_backend_v1_validaciones_reglas_negocio_y_excepciones.md`, `08_backend_v1_paginacion_filtros_ordenamiento_y_consultas.md`, `09_backend_v1_seguridad_documentacion_y_despliegue_minimo.md`
- **Objetivo de esta revisión:** Definir un contrato de API consistente para V1 (éxitos, errores, paginación y placeholders) con trazabilidad técnica/negocio y manejo centralizado de excepciones.

---

## 1. Propósito del documento

Este documento define **cómo responde la API** del backend V1, tanto en éxito como en error.

Su propósito es evitar que cada controller responda “a su manera” y asegurar:

- consistencia entre módulos,
- facilidad de consumo desde JavaFX,
- trazabilidad de reglas/errores,
- manejo centralizado de excepciones,
- y una base profesional para documentación Swagger/OpenAPI.

> La idea es que el frontend (JavaFX o futuro cliente web/móvil) pueda confiar en una estructura de respuesta **predecible**.

---

## 2. Principios de diseño del contrato API (V1)

## 2.0. Estado de implementacion actualizado

Al corte técnico del `2026-03-04`, el backend mantiene este contrato sin cambios de esquema sobre `V2_3FN.sql`, pero con una mejora interna importante:

- la capa `application` usa excepciones tipadas en la mayoria de módulos CRUD y reportes,
- `ResponseStatusException` queda como compatibilidad de framework/borde,
- `requestId` viaja tanto en `ApiErrorResponse` como en el patron base de logs,
- y la paginacion HTTP-facing se resuelve desde `common/pagination`.

## 2.1. Contrato estable y uniforme

Todos los endpoints deben intentar responder con una estructura homogénea, para reducir lógica condicional innecesaria en clientes.

### Regla V1
- Éxitos: `ApiResponse<T>`
- Errores: `ApiErrorResponse`
- Listados paginados: `ApiResponse<PageResponseDto<T>>`

---

## 2.2. El backend expresa semántica HTTP + semántica de negocio

El contrato no se limita al status HTTP.

También debe transmitir:
- código de error (`RN-*`, `VR-*`, `AUTH-*`, etc.)
- mensaje entendible
- detalles útiles para depuración/control del cliente (sin filtrar internals sensibles)

✅ HTTP status + `errorCode` = combinación robusta.

---

## 2.3. No exponer entidades JPA ni errores internos crudos

El contrato de API no debe exponer:
- entidades JPA directamente,
- stack traces,
- nombres internos de tablas,
- SQL crudo,
- detalles de infraestructura que no aportan al cliente.

---

## 2.4. Consistencia > perfección en V1

Se prioriza un contrato claro y repetible sobre un diseño excesivamente complejo (RFCs avanzados, hipertexto, etc.).

✅ V1 profesional, práctica y mantenible.

---

## 3. Contrato de respuesta exitosa (`ApiResponse<T>`)

## 3.1. Estructura general propuesta

Se adopta una respuesta estándar con envoltorio.

### Estructura conceptual
- `ok`: indica éxito lógico de la operación
- `message`: mensaje breve y estable (opcional pero recomendado en muchas operaciones)
- `data`: payload principal (tipado)
- `meta`: metadata opcional (paginación, trazas ligeras, etc.)
- `timestamp`: marca temporal de respuesta

---

## 3.2. Clase referencial (conceptual)

```java
public class ApiResponse<T> {
    private boolean ok;
    private String message;
    private T data;
    private Object meta;      // o un tipo más específico si prefieres
    private Instant timestamp;
}
```

> En implementación real, `meta` puede ser `ResponseMeta` o `null`. Si prefieres mayor tipado, puedes usar una clase dedicada.

---

## 3.3. Reglas de uso por campo

### `ok`
- En respuestas exitosas debe ser `true`.
- Debe estar presente (no opcional) para que el cliente tenga chequeo rápido.

### `message`
- Recomendado en operaciones de escritura (`POST`, `PUT`, `PATCH`, acciones)
- Opcional en lecturas masivas si el payload ya es claro
- Debe ser breve y estable (no textos novelados)

### `data`
- Payload principal tipado
- Puede ser objeto, lista, `PageResponseDto<T>`, o incluso `null` en algunos casos controlados

### `meta`
- Opcional
- Se usa para metadata complementaria no perteneciente al dominio (ej. paginación si no va embebida, requestId, etc.)

### `timestamp`
- Recomendado para trazabilidad y depuración básica

---

## 3.4. Ejemplo de respuesta exitosa simple (detalle)

```json
{
  "ok": true,
  "message": "Estudiante obtenido correctamente.",
  "data": {
    "id": 15,
    "nombres": "Juan",
    "apellidos": "Pérez",
    "estado": "ACTIVO",
    "seccionVigenteId": 4
  },
  "meta": null,
  "timestamp": "2026-02-24T20:15:11Z"
}
```

---

## 3.5. Ejemplo de respuesta exitosa de acción (sin payload relevante)

```json
{
  "ok": true,
  "message": "Sección asignada correctamente.",
  "data": null,
  "meta": null,
  "timestamp": "2026-02-24T20:18:04Z"
}
```

✅ Útil para acciones de orquestación donde lo importante es confirmar ejecución.

---

## 4. Contrato de errores (`ApiErrorResponse`)

## 4.1. Objetivo

Definir una estructura única para errores que sirva para:

- validaciones de request (`VR-*`)
- reglas de negocio (`RN-*`)
- auth/seguridad (`AUTH-*`)
- errores de API (`API-*`)
- errores del sistema (`SYS-*`)

---

## 4.2. Estructura general propuesta

### Campos recomendados
- `ok` = `false`
- `errorCode` = código estable y trazable
- `message` = mensaje principal legible
- `details` = lista/detalle adicional (opcional)
- `path` = ruta HTTP solicitada
- `timestamp` = marca temporal
- `requestId` = opcional (muy recomendable si se implementa filtro/interceptor)

---

## 4.3. Clase referencial (conceptual)

```java
public class ApiErrorResponse {
    private boolean ok;
    private String errorCode;
    private String message;
    private Object details;
    private String path;
    private Instant timestamp;
    private String requestId; // opcional
}
```

---

## 4.4. Reglas de diseño para mensajes de error

### `message`
Debe ser:
- útil para el cliente/operador
- entendible
- no sensible

### Evitar
- stack trace
- mensaje crudo de SQL
- clases internas (`NullPointerException`, etc.) como mensaje final al cliente

---

## 4.5. Ejemplo de error de negocio (`RN-*`)

```json
{
  "ok": false,
  "errorCode": "RN-12-CUPO_SECCION_AGOTADO",
  "message": "No se puede asignar la sección porque no hay cupos disponibles.",
  "details": null,
  "path": "/api/v1/estudiantes/15/seccion-vigente",
  "timestamp": "2026-02-24T20:22:31Z",
  "requestId": "7f9a3c2b"
}
```

---

## 4.6. Ejemplo de error de validación (`VR-*`) con detalle por campo

```json
{
  "ok": false,
  "errorCode": "VR-01-REQUEST_INVALIDO",
  "message": "La solicitud contiene errores de validación.",
  "details": [
    {
      "field": "nombres",
      "code": "NotBlank",
      "message": "El campo nombres es obligatorio."
    },
    {
      "field": "numeroParcial",
      "code": "Min",
      "message": "El número de parcial debe ser mayor o igual a 1."
    }
  ],
  "path": "/api/v1/calificaciones",
  "timestamp": "2026-02-24T20:25:10Z",
  "requestId": "6c0aa190"
}
```

✅ Esta forma le sirve mucho al cliente JavaFX para marcar campos.

---

## 5. Taxonomía de códigos de error (V1)

## 5.1. Categorías base (alineadas con `03` y `07`)

Se adopta la siguiente clasificación de alto nivel:

- `VR-*` → validaciones de request / entrada
- `RN-*` → reglas de negocio
- `AUTH-*` → autenticación / autorización
- `API-*` → uso incorrecto de la API / contrato / recursos HTTP
- `SYS-*` → errores internos del sistema / infraestructura

---

## 5.2. Convención de formato (recomendada)

Formato recomendado (flexible pero consistente):

- `CATEGORIA-NN-SLUG`

### Ejemplos
- `VR-01-REQUEST_INVALIDO`
- `VR-04-FORMATO_CEDULA_INVALIDO`
- `RN-12-CUPO_SECCION_AGOTADO`
- `AUTH-01-CREDENCIALES_INVALIDAS`
- `API-04-RECURSO_NO_ENCONTRADO`
- `SYS-01-ERROR_INTERNO`

> Si luego prefieres `RN-03-<slug>` exactamente, mantén esa misma forma en todo el proyecto. Lo importante es **uniformidad**.

---

## 5.3. Relación entre código y status HTTP

El `errorCode` no reemplaza el status HTTP; lo complementa.

Ejemplo:
- `409 Conflict` + `RN-12-CUPO_SECCION_AGOTADO`

Esto permite al cliente decidir por:
- semántica HTTP,
- y/o semántica de negocio específica.

---

## 6. Mapeo recomendado de errores a HTTP status

## 6.1. Validaciones de request (`VR-*`)

### Status recomendado
- **400 Bad Request** (general)

> Si usas `@Valid`, muchas veces cae naturalmente en `MethodArgumentNotValidException` → se mapea a 400.

---

## 6.2. Reglas de negocio (`RN-*`)

No todas las RN son el mismo status. Recomendación V1:

- **409 Conflict** → conflicto de estado / cupo / unicidad lógica
- **422 Unprocessable Entity** (opcional) → operación válida sintácticamente pero inválida semánticamente
- **400 Bad Request** → cuando la violación es de uso incorrecto y no amerita distinguir más

✅ Para V1, usar `409` en muchos conflictos de negocio suele ser una buena “vieja confiable”.

---

## 6.3. Auth / seguridad (`AUTH-*`)

- **401 Unauthorized** → credenciales inválidas o ausencia de autenticación
- **403 Forbidden** → autenticado pero sin permiso

---

## 6.4. API / recurso (`API-*`)

- **404 Not Found** → recurso inexistente
- **405 Method Not Allowed** → método HTTP no permitido (si se captura/customiza)
- **415 Unsupported Media Type** (si aplica)
- **406 Not Acceptable** (raro en V1, normalmente no necesario customizar)

---

## 6.5. Sistema (`SYS-*`)

- **500 Internal Server Error** → error inesperado
- **503 Service Unavailable** (opcional) → si hay dependencia caída o mantenimiento

### Regla de seguridad
Nunca exponer detalles internos del fallo en `message` del cliente.

---

## 7. Contrato de paginación (`PageResponseDto<T>`)

## 7.1. Objetivo

Estandarizar la forma en que se devuelven listados paginados para que JavaFX (y futuros clientes) siempre sepa cómo leer:
- items,
- número de página,
- tamaño,
- total,
- etc.

---

## 7.2. Estructura propuesta

### Clase referencial (conceptual)

```java
public class PageResponseDto<T> {
    private List<T> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private String sort; // opcional/resumen
}
```

> También puedes modelar `sort` como objeto/lista si quieres más detalle. En V1, string resumen es suficiente.

---

## 7.3. Uso dentro de `ApiResponse`

Convención V1:

- `ApiResponse<PageResponseDto<EstudianteListItemResponseDto>>`

Esto mantiene un contrato homogéneo en toda la API.

---

## 7.4. Ejemplo de listado paginado

```json
{
  "ok": true,
  "message": "Listado de estudiantes obtenido correctamente.",
  "data": {
    "items": [
      {
        "id": 15,
        "nombres": "Juan",
        "apellidos": "Pérez",
        "estado": "ACTIVO",
        "seccionVigente": "7mo A"
      },
      {
        "id": 16,
        "nombres": "Ana",
        "apellidos": "Gómez",
        "estado": "ACTIVO",
        "seccionVigente": "7mo B"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 52,
    "totalPages": 3,
    "first": true,
    "last": false,
    "sort": "apellidos,asc"
  },
  "meta": null,
  "timestamp": "2026-02-24T20:31:44Z"
}
```

---

## 8. Política de éxito por tipo de operación HTTP (V1)

## 8.1. GET (lectura)

### Status recomendados
- `200 OK`

### Contrato
- `ApiResponse<T>` o `ApiResponse<PageResponseDto<T>>`

---

## 8.2. POST (creación o acción)

### Casos típicos
- creación de recurso → `201 Created`
- acción operativa (p. ej., solicitar reporte, login) → `200 OK` o `201 Created` según semántica

### Regla práctica V1
- Si **crea registro**: preferir `201 Created`
- Si **ejecuta acción** sin crear recurso principal claro: `200 OK`

> El uso de header `Location` es una buena práctica cuando crea recurso identificable, pero en V1 puede implementarse de forma gradual.

---

## 8.3. PUT / PATCH (actualización)

### Status recomendado
- `200 OK`

### Contrato
- `ApiResponse<T>` (si devuelve recurso actualizado)
- `ApiResponse<Void/null>` (si solo confirma)

---

## 8.4. DELETE (eliminación lógica o física)

### Dos opciones válidas en V1
1. `200 OK` + `ApiResponse` (más consistente con envoltorio)
2. `204 No Content` (sin body)

### Recomendación para este paquete V1
**Preferir `200 OK` + `ApiResponse`** para mantener uniformidad del contrato.

✅ Facilita manejo uniforme en JavaFX.

---

## 9. Placeholders y endpoints “en construcción”

Tú lo planteaste y es una excelente estrategia V1 para avanzar arquitectura sin bloquearte.

## 9.1. Cuándo aplicar placeholder

Se permite en V1 cuando:
- un endpoint está planificado pero aún no implementado,
- depende de ajuste de BD (ej. `docente_seccion`),
- o se quiere practicar la arquitectura antes del comportamiento final.

Casos típicos:
- `reporte` backend parcial
- endpoint de landing/API futura
- consultas complejas pospuestas

---

## 9.2. Status recomendado para placeholder

### Opción preferida (semántica fuerte)
- **501 Not Implemented**

### Opción alternativa (si quieres simplificar consumo temporal)
- `200 OK` con `ok=true` y mensaje “en construcción” (menos recomendable para producción)

✅ Recomendación V1 profesional: **501 + `ApiErrorResponse`** con código `API-*` o `SYS-*` específico de placeholder.

---

## 9.3. Ejemplo de placeholder con contrato consistente

```json
{
  "ok": false,
  "errorCode": "API-10-ENDPOINT_EN_CONSTRUCCION",
  "message": "El endpoint está definido en la arquitectura pero aún no se encuentra implementado.",
  "details": null,
  "path": "/api/v1/reportes/preview",
  "timestamp": "2026-02-24T20:36:50Z",
  "requestId": "e193ab45"
}
```

✅ Esto mantiene consistencia y comunica estado real.

---

## 10. Manejo global de excepciones (`GlobalExceptionHandler`)

## 10.1. Objetivo

Centralizar el mapeo de excepciones a `ApiErrorResponse` para evitar:
- duplicación de código en controllers,
- respuestas inconsistentes,
- errores mal formateados.

---

## 10.2. Ubicación recomendada

Paquete sugerido:
- `common.exception` o `common.api.error`

Clase típica:
- `GlobalExceptionHandler`

Con `@RestControllerAdvice`

---

## 10.3. Responsabilidades del handler global

- capturar excepciones de validación (`@Valid`)
- capturar excepciones de negocio (`RN-*`)
- capturar not found / conflict / auth
- capturar errores inesperados (`Exception`)
- construir `ApiErrorResponse`
- asignar status HTTP correcto
- incluir `path`, `timestamp`, y si existe `requestId`

---

## 10.4. Regla de diseño

Los controllers **no** deben construir manualmente errores JSON repetidos.

✅ Controller delgado + handler global = consistencia.

---

## 10.5. Excepciones que conviene mapear explícitamente (V1)

### Validación / request
- `MethodArgumentNotValidException`
- `ConstraintViolationException`
- `HttpMessageNotReadableException`
- `MethodArgumentTypeMismatchException`

### API / routing
- `NoHandlerFoundException` (si se configura)
- `HttpRequestMethodNotSupportedException`

### Negocio / aplicación (custom)
- `BusinessRuleException`
- `ResourceNotFoundException`
- `ConflictException`
- `InvalidStateTransitionException`

### Seguridad (según integración)
- `BadCredentialsException` (si se maneja en capa auth)
- `AccessDeniedException`
- `AuthenticationException`

### Fallback
- `Exception`

---

## 11. Modelo recomendado de excepciones custom (alineado con `07`)

## 11.1. Objetivo

Tener excepciones con semántica de aplicación/negocio, no solo `RuntimeException` genérica.

---

## 11.2. Excepción base recomendada (conceptual)

Puedes tener una base como:

```java
public abstract class ApiBaseException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus status;
    private final Object details;
}
```

y derivadas como:
- `BusinessRuleException`
- `ResourceNotFoundException`
- `ConflictException`
- `UnauthorizedException`
- `ForbiddenException`

✅ Esto simplifica muchísimo el `GlobalExceptionHandler`.

---

## 11.3. Trazabilidad RN/VR en excepciones

Cuando aplique, las excepciones de negocio deben transportar `errorCode` trazable (`RN-*`).

Ejemplo conceptual:
- `RN-12-CUPO_SECCION_AGOTADO`
- `RN-15-CAMBIO_SECCION_NO_PERMITIDO`

Esto conecta directamente con `07` y con tus docs de reglas.

---

## 12. Errores de validación (`@Valid`) y estructura `details`

## 12.1. Objetivo de `details`

Permitir que el cliente UI (JavaFX) identifique:
- qué campo falló,
- qué regla falló,
- qué mensaje mostrar.

---

## 12.2. Estructura sugerida de item de error de validación

```java
public class ValidationErrorDetail {
    private String field;
    private String code;
    private String message;
    private Object rejectedValue; // opcional, con cuidado
}
```

### Recomendación sobre `rejectedValue`
- útil para depuración
- **pero** omitirlo cuando pueda exponer datos sensibles o generar ruido excesivo

---

## 12.3. Regla de mensajes

Los mensajes deben ser útiles para el consumidor de la API y no depender totalmente del mensaje por defecto del framework.

✅ Si luego internacionalizas (`MessageSource`), este diseño sigue siendo compatible.

---

## 13. Errores de autenticación y autorización (contrato V1)

## 13.1. `401 Unauthorized`

Casos típicos:
- credenciales inválidas en login
- token ausente (en endpoint protegido)
- token inválido/expirado

### Código sugerido
- `AUTH-01-CREDENCIALES_INVALIDAS`
- `AUTH-02-TOKEN_INVALIDO`
- `AUTH-03-TOKEN_EXPIRADO`

---

## 13.2. `403 Forbidden`

Caso típico:
- usuario autenticado pero sin rol/permisos suficientes

### Código sugerido
- `AUTH-10-ACCESO_DENEGADO`

---

## 13.3. Consistencia entre filtros de seguridad y handler global

Dependiendo de Spring Security, algunos errores ocurren antes de llegar al controller.

✅ Aun así, se recomienda configurar entry points/handlers de seguridad para que respondan con el **mismo contrato `ApiErrorResponse`**.

> Esto se aterriza más en `09`.

---

## 14. Errores de API y recursos inexistentes

## 14.1. `404` por recurso inexistente de dominio

Ejemplo:
- estudiante con `id` inexistente

### Recomendación
Usar excepción custom (`ResourceNotFoundException`) con código `API-*` o `RN-*` según tu criterio documental.

### Criterio práctico sugerido
- `API-*` si el problema es “recurso no existe”
- `RN-*` si es una regla de negocio contextual más específica

---

## 14.2. `404` por ruta inexistente

Si decides capturarlo y customizarlo, responder con `ApiErrorResponse` consistente.

Código sugerido:
- `API-04-RUTA_NO_ENCONTRADA`

---

## 14.3. Parámetros inválidos y parseos

Ejemplos:
- path variable numérica inválida
- enum inválido
- JSON mal formado

Códigos sugeridos:
- `VR-02-PARAMETRO_INVALIDO`
- `VR-03-CUERPO_JSON_INVALIDO`

---

## 15. Metadatos transversales (`timestamp`, `path`, `requestId`)

## 15.1. `timestamp`

Recomendado en éxito y error.

Ventajas:
- trazabilidad
- depuración
- correlación visual con logs

---

## 15.2. `path`

Obligatorio en `ApiErrorResponse`.

Ventaja:
- facilita diagnóstico del cliente y del backend sin revisar logs inmediatamente.

---

## 15.3. `requestId` (muy recomendable)

Aunque es V1, es una mejora barata y profesional.

### Recomendación
Generar o propagar un `requestId` por filtro/interceptor y:
- incluirlo en logs,
- incluirlo en `ApiErrorResponse`,
- opcionalmente también en `ApiResponse.meta`.

✅ Muy útil cuando empieces a depurar flows reales con JavaFX.

---

## 16. Contrato para endpoints de login/auth (caso especial pero consistente)

## 16.1. Login exitoso

Debe seguir `ApiResponse<T>`.

### Ejemplo conceptual de `data`
- token
- tipo de token
- expiración (opcional)
- usuario resumido (opcional/recomendado)

```json
{
  "ok": true,
  "message": "Inicio de sesión exitoso.",
  "data": {
    "token": "<jwt>",
    "tokenType": "Bearer",
    "expiresInSeconds": 3600,
    "usuario": {
      "id": 3,
      "username": "admin",
      "rol": "ADMIN"
    }
  },
  "meta": null,
  "timestamp": "2026-02-24T20:48:00Z"
}
```

---

## 16.2. Login fallido

Debe responder con `ApiErrorResponse` + `401`.

✅ No devolver `200` con “login fallido” en `ok=false` si corresponde semánticamente a auth fallida.

---

## 17. Contrato para endpoints de reportes y cola simple (alineación con `10`)

## 17.1. Solicitud de reporte creada

Si el backend crea una solicitud en cola (DB queue), el endpoint puede responder:
- `201 Created`
- `ApiResponse<ReporteSolicitudResponseDto>`

### `data` mínima recomendable
- `solicitudId`
- `estado` (ej. `PENDIENTE`)
- `tipoReporte`
- `fechaSolicitud`

---

## 17.2. Consulta de estado de reporte

- `200 OK`
- `ApiResponse<ReporteSolicitudEstadoResponseDto>`

Estados estandarizados (consistencia con `03`/`10`):
- `PENDIENTE`
- `EN_PROCESO`
- `COMPLETADA`
- `ERROR`
- `CANCELADA` (si se usa)

---

## 17.3. Placeholder de generación/preview no implementado

Si se define endpoint de preview/descarga y aún no está listo:
- `501 Not Implemented`
- `ApiErrorResponse`
- `API-10-ENDPOINT_EN_CONSTRUCCION` (o código equivalente definido)

✅ Esto mantiene arquitectura lista sin mentir al cliente.

---

## 18. Contrato y Swagger/OpenAPI (alineación documental)

## 18.1. Importancia

Swagger/OpenAPI debe documentar el contrato real que se implementa, no una versión idealizada.

---

## 18.2. Qué documentar en cada endpoint (mínimo)

- resumen
- descripción corta
- respuestas principales (`200/201/400/401/403/404/409/500` según aplique)
- schema de `ApiResponse<...>` y `ApiErrorResponse`
- ejemplo de error cuando el endpoint tiene RN relevante

✅ Esto será muy útil para ti al conectar JavaFX.

---

## 18.3. Consistencia de ejemplos

Los ejemplos Swagger deben usar:
- `q` como query param de búsqueda simple (no `search`)
- estados de reporte estandarizados (`EN_PROCESO`, `COMPLETADA`)
- códigos `RN/VR/AUTH/API/SYS` coherentes

---

## 19. Reglas prácticas de implementación (para no romper el contrato)

## 19.1. Controllers no crean errores manuales repetidos

Si se necesita un error de negocio, lanzar excepción custom y dejar que el handler global forme el `ApiErrorResponse`.

---

## 19.2. No devolver `Map<String, Object>` ad hoc

Evitar respuestas improvisadas por endpoint.

✅ Siempre usar DTO + `ApiResponse`/`ApiErrorResponse`.

---

## 19.3. No mezclar contratos en un mismo módulo

Ejemplo de mala práctica:
- un endpoint devuelve envoltorio
- otro endpoint del mismo módulo devuelve entidad cruda
- otro devuelve string simple

✅ Mantener contrato uniforme desde el inicio.

---

## 19.4. Mensajes estables y razonables

Los mensajes del contrato:
- deben ser útiles,
- pero no convertirse en única fuente de lógica del frontend.

La lógica del cliente debe apoyarse más en:
- status HTTP,
- `ok`,
- `errorCode`,
- estructura de `data/details`.

---

## 20. Propuesta de clases base (catálogo mínimo V1)

## 20.1. En `common.response`
- `ApiResponse<T>`
- `PageResponseDto<T>`
- `ResponseMeta` (opcional)

## 20.2. En `common.exception`
- `ApiErrorResponse`
- `GlobalExceptionHandler`
- `ApiBaseException` (opcional pero recomendado)
- `BusinessRuleException`
- `ResourceNotFoundException`
- `ConflictException`
- `UnauthorizedException`
- `ForbiddenException`

## 20.3. En `common.exception.detail` (opcional)
- `ValidationErrorDetail`

✅ Este catálogo es suficiente para una V1 seria sin sobreingeniería.

---

## 21. Decisiones fijadas por este documento (V1)

1. ✅ Contrato de éxito estándar con `ApiResponse<T>`.
2. ✅ Contrato de error estándar con `ApiErrorResponse`.
3. ✅ Listados paginados con `ApiResponse<PageResponseDto<T>>`.
4. ✅ Manejo de excepciones centralizado con `GlobalExceptionHandler`.
5. ✅ Códigos de error trazables por categoría (`VR`, `RN`, `AUTH`, `API`, `SYS`).
6. ✅ Endpoints placeholder permitidos con `501` + `ApiErrorResponse`.
7. ✅ `timestamp` obligatorio recomendado en éxito y error.
8. ✅ `path` obligatorio en error.
9. ✅ `requestId` recomendado (si se implementa filtro/interceptor).
10. ✅ No exponer entidades JPA ni errores internos crudos.

---

## 22. Riesgos comunes y cómo evitarlos

## 22.1. Contratos improvisados por controller

**Riesgo:** frontend más complejo y backend inconsistente.

**Mitigación:** usar siempre `ApiResponse`/`ApiErrorResponse`.

---

## 22.2. ErrorCode sin disciplina

**Riesgo:** códigos duplicados, ambiguos o sin trazabilidad.

**Mitigación:** formalizar catálogo en `07` y reutilizarlo.

---

## 22.3. Exponer detalles internos en errores 500

**Riesgo:** fuga de información y mala UX.

**Mitigación:** sanitizar mensajes al cliente + logs internos con contexto.

---

## 22.4. `details` inconsistente

**Riesgo:** cliente no sabe cómo interpretar errores.

**Mitigación:** estructura estable por tipo de error (validación vs negocio vs sistema).

---

## 22.5. Mezclar `200 OK` para errores reales

**Riesgo:** semántica HTTP rota y clientes confundidos.

**Mitigación:** usar HTTP status correcto + contrato consistente.

---

## 23. Relación con documentos siguientes

- **`06_backend_v1_api_endpoints_y_casos_de_uso.md`**
  aplicará este contrato a cada endpoint y definirá respuestas por caso de uso.

- **`07_backend_v1_validaciones_reglas_negocio_y_excepciones.md`**
  formalizará catálogo y trazabilidad de errores (`VR/RN/AUTH/API/SYS`) y excepciones custom.

- **`08_backend_v1_paginacion_filtros_ordenamiento_y_consultas.md`**
  aterrizará `PageResponseDto` en listados reales con `page/size/sort/q`.

- **`09_backend_v1_seguridad_documentacion_y_despliegue_minimo.md`**
  alineará respuestas de seguridad (`401/403`) y Swagger/OpenAPI con este contrato.

- **`10_backend_v1_reporte_solicitudes_cola_simple_db_queue.md`**
  reutilizará este contrato para solicitudes de reporte, estados y placeholders.

---

## 24. Cierre

Este documento deja resuelta una de las piezas más importantes del backend V1: **el idioma formal de la API**.

Con este contrato:
- el backend responde de forma profesional,
- JavaFX consume con menos fricción,
- las reglas/errores se vuelven trazables,
- y el crecimiento del proyecto se vuelve mucho más ordenado.

En términos prácticos: esto te ahorra una tonelada de retrabajo cuando empieces a conectar pantallas reales.

