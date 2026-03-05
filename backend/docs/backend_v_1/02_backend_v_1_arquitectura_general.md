# 02_backend_v1_arquitectura_general

- **Versión:** 0.2
- **Estado:** En revisión (reconstruido por consistencia)
- **Ámbito:** Backend V1 (Spring Boot + Java 21)
- **Depende de:** `00_backend_v1_indice_y_mapa_documental.md`, `01_backend_v1_vision_y_alcance.md`
- **Referencias de contexto:** `02_levantamiento_requerimientos.md`, `03_modelo_conceptual_dominio.md`, `04_reglas_negocio_y_supuestos.md`, `V2_3FN.sql`
- **Objetivo de esta revisión:** Consolidar arquitectura V1 con enfoque **profesional, común y sin sobreingeniería**, incorporando **DDD-lite + encapsulación pragmática**.

---

## 1. Propósito del documento

Este documento define la **arquitectura general del backend V1**:

- cómo se organiza el sistema,
- cómo se distribuyen responsabilidades,
- cómo circulan los datos,
- y qué decisiones técnicas son obligatorias para mantener consistencia.

Su propósito es servir como base para implementar una API REST con Spring Boot de forma ordenada, entendible y mantenible.

> Este documento **no** detalla el catálogo completo de endpoints ni los DTOs finales por módulo (eso se aterriza en `05`, `06`, `08` y `11` si se formaliza).

---

## 2. Resumen de la decisión arquitectónica

## 2.1. Decisión principal

El backend V1 se implementará como un **monolito modular** con arquitectura por capas y módulos funcionales, usando una aproximación **DDD-lite pragmática**.

### ¿Qué significa aquí “DDD-lite pragmática”?
No significa implementar DDD completo con toda su terminología/patrones desde el día 1.
Significa:

- organizar el código alrededor de **casos de uso y dominio** (no solo controllers),
- encapsular transiciones/reglas importantes (evitar setters innecesarios),
- separar contrato API de persistencia,
- mantener compatibilidad natural con Spring + JPA.

✅ Resultado esperado: arquitectura profesional y común, con buen control del modelo, sin volverse académica ni rígida.

---

## 2.2. Stack base de referencia (V1)

- **Java 21 (Eclipse Temurin)**
- **Spring Boot**
- **Spring Web** (REST)
- **Spring Data JPA + Hibernate** (ORM)
- **PostgreSQL**
- **Spring Security + JWT**
- **Bean Validation (Jakarta Validation)**
- **Swagger/OpenAPI (`springdoc-openapi`)**
- **Docker mínimo** (backend + postgres con Compose)

---

## 2.3. Qué se evita conscientemente en V1

Para no caer en sobreingeniería, se evita:

- microservicios,
- arquitectura distribuida,
- event-driven con broker externo real (RabbitMQ/Kafka),
- hexagonal completa con puertos/adaptadores para absolutamente todo,
- abstracciones genéricas prematuras “por si acaso”.

> V1 busca una base sólida y empleable, no una demostración de patrones avanzados.

---

## 3. Objetivos técnicos de la arquitectura V1

La arquitectura debe permitir:

1. **Implementar rápido sin desorden**.
2. **Separar responsabilidades** (HTTP, aplicación, dominio, persistencia, seguridad).
3. **Mantener trazabilidad** con requerimientos, RN y VR.
4. **Exponer una API consistente** para cualquier cliente (JavaFX hoy, otro mañana).
5. **Facilitar evolución incremental** sin reescribir todo.
6. **Practicar diseño backend profesional** (transacciones, errores, seguridad, asincronía simple).

---

## 4. Principios arquitectónicos transversales

## 4.1. Backend agnóstico de UI, no agnóstico del negocio

El backend:

- **no conoce** JavaFX/FXML, layouts, componentes visuales ni UX,
- **sí conoce** casos de uso, reglas de negocio, validaciones, estados y flujos del sistema.

### Implicación práctica
La UI se adapta al backend, y el backend se diseña para casos de uso del negocio, no para una pantalla específica.

---

## 4.2. Separación de responsabilidades por capa y por intención

Se combinan dos criterios de organización:

- **por módulo funcional** (feature-based packaging), y
- **por capa interna** (API / aplicación / dominio / infraestructura).

Esto evita dos extremos malos:

- proyecto gigante solo por capas globales (`controller/`, `service/`, etc.),
- proyecto por módulos sin estructura interna clara.

---

## 4.3. DDD-lite con encapsulación pragmática

Decisión transversal:

- evitar setters públicos innecesarios en entidades/objetos importantes,
- preferir métodos con intención cuando hay invariantes o transiciones,
- mantener constructor/protección compatible con JPA,
- no forzar “pureza DDD” si complica V1 sin valor real.

### Ejemplo conceptual (no código final)
En lugar de exponer cambios arbitrarios:
- `setEstado(...)`

preferir cuando aplica:
- `marcarActivo()`
- `cerrarPeriodo()`
- `marcarEnProceso()`
- `marcarCompletada(...)`

✅ Esto mejora control del modelo y legibilidad de casos de uso.

---

## 4.4. Desacople pragmático de librerías (no fantasioso)

Tú pediste algo muy importante: desacoplar la aplicación de librerías secundarias (Jackson, detalles del repositorio, etc.) sin intentar desacoplarla de Spring Boot por completo.

### Sí se busca (V1)
- no meter `ObjectMapper`/`JsonNode` en servicios de negocio (salvo casos puntuales de infraestructura como JSONB de reportes),
- no exponer entidades JPA como contrato API,
- no mezclar detalles HTTP dentro de la lógica de aplicación,
- no dispersar decisiones de seguridad por todos los módulos,
- encapsular acceso a datos en repositorios.

### No se busca (V1)
- reemplazar JPA por otro ORM “sin tocar nada”,
- abstraer cada librería detrás de interfaces artificiales sin necesidad,
- construir una arquitectura “agnóstica a Spring” total.

✅ Meta realista: **código propio ordenado**, no “framework-independencia absoluta”.

---

## 4.5. Convención antes que creatividad accidental

Se priorizan convenciones de Spring/Java conocidas porque:

- reducen fricción,
- mejoran mantenibilidad,
- facilitan onboarding,
- y son la base más útil para tu primer trabajo formal.

---

## 5. Estilo arquitectónico seleccionado

## 5.1. Monolito modular

La aplicación corre como **una sola unidad desplegable** (un backend Spring Boot), pero organizada en módulos funcionales.

### Ventajas para V1
- simple de desarrollar y desplegar,
- menor complejidad operativa,
- fácil depuración,
- suficiente para el alcance actual,
- permite prácticas profesionales reales (seguridad, DTOs, colas simples, etc.).

---

## 5.2. Arquitectura de capas dentro de módulos

Cada módulo se organiza con capas internas coherentes, separando:

- entrada HTTP/API,
- casos de uso (aplicación),
- reglas/objetos de dominio,
- persistencia/infraestructura,
- mapeo y DTOs.

✅ Esto habilita módulos CRUD y módulos de orquestación sin romper consistencia.

---

## 5.3. DDD-lite compatible con JPA

Se adopta un diseño de dominio **controlado**, pero sin inventar una capa de dominio pura independiente de JPA en toda la aplicación desde el inicio.

### Decisión práctica
- Las entidades persistentes JPA pueden contener reglas/invariantes simples y transiciones con intención.
- La orquestación de casos de uso y reglas compuestas vive en la capa de aplicación/servicio.
- Los repositorios encapsulan persistencia.

---

## 6. Vista de alto nivel del flujo de una petición

## 6.1. Flujo principal (REST)

```text
Cliente (JavaFX / otro)
        |
        v
API Controller (HTTP, params, @Valid, response)
        |
        v
Application Service / Use Case
        |
        +--> Domain logic (invariantes / transiciones)
        +--> Repository(s)
        +--> Mapper(s)
        |
        v
ApiResponse<T>
```

---

## 6.2. Flujo de error (alto nivel)

```text
Controller / Application Service / Repository / Security
        |
   (lanza excepción)
        |
        v
GlobalExceptionHandler
        |
        v
ApiErrorResponse + HTTP status coherente
```

### Regla
No devolver errores HTML por defecto de Spring para APIs del negocio.

---

## 7. Organización del código: módulos funcionales + capas internas

## 7.1. Decisión de organización

Se usará **feature-based packaging** (paquetes por módulo funcional), con subpaquetes internos por responsabilidad.

Esto es la base para:
- claridad,
- mantenibilidad,
- crecimiento gradual,
- y soporte natural para casos de uso multi-entidad.

---

## 7.2. Paquetes transversales (globales)

Fuera de los módulos funcionales se admiten paquetes transversales para infraestructura y piezas compartidas.

### Paquetes transversales esperados (V1)
- `config/` (configs de framework/documentación si aplica)
- `security/` (infraestructura de seguridad JWT)
- `common/` (respuestas, errores, excepciones, utilidades muy controladas)

---

## 7.3. Estructura base propuesta (referencial)

> El nombre de paquete raíz es ilustrativo. Se ajusta al proyecto real.

```text
com.marcos.sms
├─ config/
│  ├─ OpenApiConfig.java
│  ├─ JacksonConfig.java                  (opcional)
│  └─ CorsConfig.java                     (si aplica)
│
├─ security/
│  ├─ config/
│  │  └─ SecurityConfig.java
│  ├─ jwt/
│  │  ├─ JwtTokenService.java
│  │  └─ JwtProperties.java
│  ├─ filter/
│  │  └─ JwtAuthenticationFilter.java
│  └─ service/
│     └─ CustomUserDetailsService.java    (si aplica)
│
├─ common/
│  ├─ response/
│  │  ├─ ApiResponse.java
│  │  ├─ ApiErrorResponse.java
│  │  ├─ PageResponseDto.java
│  │  └─ ResponseMeta.java                (opcional)
│  ├─ exception/
│  │  ├─ GlobalExceptionHandler.java
│  │  ├─ BusinessRuleException.java
│  │  ├─ ValidationException.java         (si se define custom)
│  │  ├─ ResourceNotFoundException.java
│  │  └─ AccessDeniedBusinessException.java (opcional)
│  ├─ error/
│  │  └─ ErrorCodeCatalog.java            (opcional recomendado)
│  ├─ pagination/
│  │  └─ ... soporte común de paginación/filtros (si aplica)
│  └─ útil/
│     └─ ... utilidades mínimas y justificadas
│
├─ auth/
│  ├─ api/
│  │  ├─ AuthController.java
│  │  └─ dto/
│  │     ├─ request/
│  │     └─ response/
│  ├─ application/
│  │  └─ AuthApplicationService.java
│  ├─ domain/
│  │  └─ ... reglas/objetos auth (si aplica)
│  └─ infrastructure/
│     ├─ persistence/
│     │  └─ ... repositorios/adaptación JPA
│     └─ mapper/
│
├─ estudiante/
│  ├─ api/
│  ├─ application/
│  ├─ domain/
│  └─ infrastructure/
│     ├─ persistence/
│     └─ mapper/
│
├─ seccion/
├─ docente/
├─ asignatura/
├─ clase/
├─ calificacion/
├─ dashboard/
└─ reporte/
   ├─ api/
   ├─ application/
   ├─ domain/
   └─ infrastructure/
      ├─ persistence/
      ├─ mapper/
      └─ worker/                         (@Scheduled para DB queue, si aplica)
```

---

## 7.4. Nota de compatibilidad con el estilo “controller/service/repository” clásico

Si en la implementación inicial quieres usar nombres más clásicos por familiaridad (`controller`, `service`, `repository`, `dto`, `mapper`, `entity`), **es válido**, siempre que se respeten las responsabilidades.

### Regla de fondo
La arquitectura no depende del nombre exacto del paquete, sino de:
- **qué responsabilidad vive dónde**, y
- **qué dependencias están permitidas**.

> En este documento se propone `api/application/domain/infrastructure` porque deja más claro el DDD-lite sin obligarte a una arquitectura compleja.

---

## 8. Clasificación de módulos en la V1

La arquitectura reconoce que **no todo es CRUD**. Se definen dos clases de módulos.

## 8.1. Módulos CRUD (centrados en entidad principal)

Responsabilidad principal: gestionar una entidad o agregado relativamente directo.

### Ejemplos probables
- `docente`
- `asignatura`
- `representantelegal`
- `seccion`

### Características
- CRUD bastante directo,
- validaciones de entrada + algunas RN,
- uno o pocos repositorios principales,
- DTOs relativamente estables.

---

## 8.2. Módulos de orquestación / caso de uso compuesto

Responsabilidad principal: resolver procesos que cruzan varias entidades/tablas/reglas.

### Ejemplos probables
- `auth`
- `estudiante` (por validaciones de representante, sección, cupo)
- `calificacion` (clase + estudiante + parcial + reglas)
- `dashboard` (consultas agregadas)
- `reporte` (solicitudes async, datasets, estados)

### Características
- usan múltiples repositorios,
- concentran orquestación y validaciones compuestas,
- exponen endpoints orientados a casos de uso.

✅ Esto es normal en backend real y debe quedar explícitamente permitido.

---

## 9. Capas y responsabilidades (detalle operativo)

> Esta sección es la base para evitar mezclar lógica en controllers o dispersar reglas.

## 9.1. Capa API (`api/`)

### Rol
Exponer endpoints REST y traducir HTTP ↔ aplicación.

### Responsabilidades permitidas
- rutas (`/api/v1/...`)
- recibir body/path/query/header
- validación de request (`@Valid`)
- delegar a capa de aplicación
- mapear respuesta HTTP con contrato estándar
- anotaciones Swagger/OpenAPI

### Responsabilidades no permitidas
- reglas de negocio complejas (RN)
- consultas directas a repositorio
- transacciones de negocio explícitas aquí
- manipular entidades JPA como contrato API

✅ Controllers delgados y previsibles.

---

## 9.2. Capa de aplicación (`application/`)

### Rol
Implementar **casos de uso** y orquestación.

### Responsabilidades permitidas
- coordinar flujo del caso de uso
- invocar repositorios
- aplicar reglas de negocio compuestas (RN)
- invocar validaciones contextuales (más allá de `@Valid`)
- manejar transacciones (`@Transactional`)
- disparar procesos internos (ej. encolar solicitud de reporte)
- coordinar mappers

### Responsabilidades no permitidas
- detalles HTTP puros
- lógica visual/UI
- serialización JSON de contrato en servicios de negocio (salvo infraestructura puntual)

✅ Aquí vive el “valor” del backend.

---

## 9.3. Capa de dominio (`domain/`) en enfoque DDD-lite

### Rol
Representar conceptos del negocio que requieren control, invariantes y lenguaje del dominio.

### Qué puede contener (según módulo)
- entidades/objetos con comportamiento e invariantes,
- value objects simples (si aportan claridad),
- enums del dominio,
- reglas locales del agregado,
- métodos con intención para transiciones de estado.

### Qué NO debe hacer (V1)
- depender de HTTP,
- conocer DTOs de API,
- acceder directamente a repositorios,
- convertirse en una capa abstracta gigantesca sin uso real.

### Nota pragmática
En módulos simples CRUD, el `domain/` puede ser mínimo. En módulos como `reporte` o `calificacion`, suele aportar mucho más.

---

## 9.4. Capa de infraestructura (`infrastructure/`)

### Rol
Implementar detalles técnicos de persistencia, adaptaciones y mecanismos de soporte.

### Subáreas típicas en V1
- `persistence/` (repositorios JPA, entidades persistentes, queries)
- `mapper/` (mappers manuales)
- `worker/` (scheduler de cola DB en reportes)

### Regla importante
La infraestructura soporta a la aplicación; no define los casos de uso.

---

## 9.5. DTOs (contrato API)

Los DTOs pertenecen al contrato con el cliente y se ubican del lado API (o módulo, según organización concreta), no en dominio.

### Principio clave
- **DTOs != entidades JPA**
- **DTOs != reglas de negocio**

Los DTOs representan datos del contrato, nada más.

---

## 9.6. Mappers manuales

Decisión V1: **mappers manuales** por claridad y aprendizaje.

### Responsabilidades permitidas
- `RequestDto -> comando/objeto de aplicación`
- `Entidad/Proyección -> ResponseDto`
- actualización controlada de entidad cuando corresponda

### No permitido
- consultar repositorios
- aplicar RN complejas
- decidir códigos HTTP

✅ Esta decisión está alineada con tu objetivo de entender el backend “de verdad”.

---

## 10. Modelo de datos vs arquitectura del backend

## 10.1. Qué hereda el backend de la BD (3FN)

La base de datos define:
- estructura relacional,
- claves,
- relaciones,
- restricciones estructurales.

El backend lo traduce a:
- entidades persistentes JPA,
- repositorios,
- servicios de aplicación,
- DTOs y casos de uso.

---

## 10.2. Qué NO se copia literalmente de la BD a la API

No todo lo que existe en BD se expone igual por API.

### Ejemplos
- joins complejos salen como DTOs compuestos,
- dashboards/reportes salen como proyecciones/agregados,
- algunos catálogos se exponen simplificados.

### Regla operativa
- **BD modela datos**
- **Backend modela operaciones/casos de uso sobre esos datos**

---

## 11. Soporte para módulos con múltiples entidades (punto crítico)

Esta sección responde a una duda central del proyecto: **qué pasa cuando un módulo trabaja con varias entidades del dominio**.

## 11.1. Principio arquitectónico

Eso es normal y esperado.

Un módulo funcional puede:
- tener una entidad principal,
- pero requerir varios repositorios y reglas cruzadas para resolver un caso de uso.

✅ No rompe la arquitectura. De hecho, la justifica.

---

## 11.2. Ejemplo conceptual: módulo `estudiante`

Un caso de uso como “registrar estudiante” puede requerir:
- validar representante legal,
- validar sección,
- validar cupo,
- validar estado activo del período (si aplica),
- persistir estudiante.

Eso involucra múltiples entidades/tablas y múltiples repositorios.

La **orquestación** vive en `application/` del módulo `estudiante`.

---

## 11.3. Ejemplo conceptual: módulo `dashboard`

`dashboard` puede leer datos agregados de:
- estudiantes,
- secciones,
- clases,
- calificaciones.

No necesita ser “CRUD de una entidad”.
Es un módulo de **capacidad de lectura agregada**.

---

## 11.4. Regla práctica derivada

- Repositorios encapsulan acceso a entidades/consultas.
- Application services orquestan múltiples repositorios.
- Controllers exponen endpoints por módulo funcional.

---

## 12. Transacciones (criterio V1)

## 12.1. Regla general

Las transacciones se controlan en la capa de aplicación, no en controllers.

## 12.2. Uso recomendado

- `@Transactional` en operaciones de escritura y casos compuestos.
- `@Transactional(readOnly = true)` en lecturas complejas cuando aporte claridad/optimización.

## 12.3. Motivo

La transacción pertenece al **caso de uso**, no al endpoint HTTP.

---

## 13. Validaciones y reglas de negocio: ubicación arquitectónica

## 13.1. Validaciones de entrada (VR)

Principalmente en DTOs/request con Bean Validation:
- obligatorios,
- rangos simples,
- tamaños,
- formatos básicos.

## 13.2. Reglas de negocio (RN)

Principalmente en capa de aplicación (y algunas invariantes en dominio):
- cupos,
- estados válidos,
- duplicidades de negocio,
- consistencia entre entidades,
- transiciones permitidas.

## 13.3. Errores y excepciones

Manejo unificado en `common.exception.GlobalExceptionHandler`, devolviendo `ApiErrorResponse` con:
- `status`,
- `errorCode`,
- `message`,
- `details` (cuando aplique).

> El detalle exacto se formaliza en `05` y `07`.

---

## 14. Seguridad en la arquitectura (mecanismo vs caso de uso)

## 14.1. Paquete transversal `security/`

Contiene la infraestructura de seguridad:
- `SecurityConfig`,
- filtro JWT,
- utilidades/servicio de tokens,
- integración con autenticación de usuarios.

## 14.2. Módulo `auth/`

Contiene casos de uso funcionales de autenticación/autorización de aplicación:
- login,
- datos del usuario autenticado,
- endpoints de sesión/token (según alcance V1).

✅ Separar `auth` (caso de uso) de `security` (infraestructura) da mucha claridad.

---

## 15. Componentes transversales (`common/`) y límites

`common/` existe para compartir piezas realmente transversales, no para mezclar cualquier cosa.

## 15.1. Permitido en `common/`

- respuestas API (`ApiResponse`, `ApiErrorResponse`, `PageResponseDto`)
- excepciones y handler global
- catálogo de códigos de error (si se formaliza)
- utilidades pequeñas y justificadas
- componentes de paginación/filtro reutilizables (si son verdaderamente comunes)

## 15.2. No permitido en `common/` (como regla)

- lógica de negocio de módulos específicos
- helpers ambiguos tipo “Utils” gigantes
- mappers de dominio/módulos concretos
- reglas funcionales solo porque “se usan en dos lados”

---

## 16. Reportes y cola simple en BD dentro de esta arquitectura

## 16.1. Decisión V1 (alineada con el proyecto)

- El backend puede gestionar **solicitudes asíncronas** de reportes (DB queue).
- El backend puede preparar **datasets/resultado JSON**.
- La **presentación/exportación final** del reporte se deja a JavaFX en V1.

✅ Esto te permite practicar backend asíncrono sin meter infraestructura extra.

---

## 16.2. Ubicación arquitectónica del módulo `reporte`

- `api/`: endpoints de solicitud/estado/resultado
- `application/`: orquestación del flujo async
- `domain/`: estados/transiciones de solicitud (si aplica como agregado)
- `infrastructure/persistence/`: tabla de solicitudes, repositorios, consultas
- `infrastructure/worker/`: `@Scheduled` para polling/claim/proceso

---

## 16.3. Nota de arquitectura sobre asincronía

Aunque se use polling con `@Scheduled`, sigue siendo coherente con la V1 porque:
- mantiene simple el despliegue,
- introduce conceptos reales de cola/estados/reintentos,
- y no obliga a RabbitMQ/Kafka todavía.

---

## 17. Escalabilidad y evolución (sin sobreingeniería)

## 17.1. Qué deja preparado la V1

La arquitectura V1 deja base para evolucionar:

- módulos funcionales claros,
- contratos API estables (DTOs/errores),
- seguridad centralizada,
- paginación y filtros estandarizados,
- reportes async con DB queue,
- despliegue reproducible con Docker mínimo,
- env vars/perfiles.

---

## 17.2. Qué se pospone conscientemente

- microservicios,
- colas externas reales,
- cache distribuido,
- observabilidad avanzada,
- alta disponibilidad,
- despliegues complejos.

> Escalar bien comienza con una base limpia, no con tecnología extra prematura.

---

## 18. Reglas arquitectónicas obligatorias para la V1

Estas reglas se consideran obligatorias (salvo justificación documentada):

### 18.1. Contrato y API
- No exponer entidades JPA directamente en controllers.
- Controllers delegan a capa de aplicación.
- Respuestas API usan contrato estándar (`ApiResponse` / `ApiErrorResponse`).

### 18.2. Lógica y negocio
- RN viven en capa de aplicación (e invariantes puntuales en dominio).
- No poner lógica de negocio relevante en controllers o mappers.
- Módulos de orquestación multi-entidad están permitidos y son esperados.

### 18.3. Persistencia
- Acceso a BD vía repositorios.
- Consultas agregadas/reportes pueden usar JPQL o nativas cuando sea razonable.
- No dispersar SQL/lógica de consulta en capa de aplicación.

### 18.4. Encapsulación (DDD-lite)
- Evitar setters públicos innecesarios en entidades/objetos con invariantes.
- Preferir métodos con intención cuando haya transiciones de estado.
- Mantener compatibilidad con JPA sin sacrificar control del modelo.

### 18.5. Seguridad y transversalidad
- Seguridad centralizada en `security/`.
- `auth/` separado como módulo funcional.
- Errores centralizados en handler global.

---

## 19. Riesgos arquitectónicos comunes (y cómo evitarlos)

## 19.1. “Controller gordo”

**Riesgo:** meter validaciones de negocio, consultas y decisiones en el controller.

**Cómo evitarlo:** controller delgado, delegando a application service.

---

## 19.2. “Service” como cajón de sastre

**Riesgo:** una clase gigante con muchos casos de uso mezclados.

**Cómo evitarlo:** separar servicios por caso de uso/capacidad del módulo cuando crezca.

---

## 19.3. Entidades anémicas con setters para todo

**Riesgo:** cualquier parte del sistema cambia estado sin control.

**Cómo evitarlo:** DDD-lite con encapsulación y métodos con intención.

---

## 19.4. `common/` convertido en basurero técnico

**Riesgo:** helpers y lógica sin ubicación clara terminan en `common/`.

**Cómo evitarlo:** `common/` solo para piezas genuinamente transversales.

---

## 19.5. Acoplar backend a pantallas específicas

**Riesgo:** diseñar endpoints pensando en una sola vista UI.

**Cómo evitarlo:** diseñar por casos de uso y contratos API estables.

---

## 20. Relación con documentos siguientes (continuidad)

Este documento condiciona directamente:

- **`03_backend_v1_convenciones_y_estandares_codigo.md`**
  - naming, comentarios, estructura fina de clases/paquetes.

- **`04_backend_v1_modelado_aplicacion_y_modulos.md`**
  - aterriza mapa de módulos, responsabilidades y casos compuestos.

- **`05_backend_v1_diseno_api_contrato_respuestas_y_errores.md`**
  - formaliza contrato API que esta arquitectura usará en todos los módulos.

- **`06_backend_v1_api_endpoints_y_casos_de_uso.md`**
  - define el catálogo real de endpoints con base en esta arquitectura.

- **`07_backend_v1_validaciones_reglas_negocio_y_excepciones.md`**
  - aterriza dónde viven VR/RN y cómo se convierten en errores API.

- **`10_backend_v1_reporte_solicitudes_cola_simple_db_queue.md`**
  - detalla la asincronía simple compatible con esta arquitectura.

---

## 21. Decisiones tomadas (resumen ejecutivo)

- ✅ Backend V1 = **monolito modular** con Spring Boot.
- ✅ Organización por **módulos funcionales** + **capas internas**.
- ✅ Enfoque **DDD-lite pragmático** (encapsulación útil, sin dogmatismo).
- ✅ Backend agnóstico de UI, pero orientado a casos de uso del negocio.
- ✅ Capa de aplicación como núcleo de orquestación y reglas de negocio.
- ✅ DTOs + mappers manuales; entidades JPA no se exponen por API.
- ✅ Soporte explícito para módulos multi-entidad/orquestación.
- ✅ Seguridad transversal en `security/`, separada de `auth/`.
- ✅ DB queue para reportes async compatible con la arquitectura V1.
- ✅ Desacople pragmático de librerías secundarias, sin “agnosticismo total” artificial.

---

## 22. Cierre del documento

Esta arquitectura general fija una base técnica suficientemente profesional para construir el backend V1 con orden y criterio:

- clara para implementar,
- flexible para crecer,
- alineada con tu objetivo de aprender bien,
- y sin sobreingeniería.

El siguiente paso recomendado en la auditoría/corrección es:

- **`04_backend_v1_modelado_aplicacion_y_modulos.md`**

porque ahí se aterriza esta arquitectura en módulos concretos y es donde más se nota la coherencia (o incoherencia) del diseño real.


