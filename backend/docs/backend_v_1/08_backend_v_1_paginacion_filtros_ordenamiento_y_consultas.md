# 08_backend_v1_paginacion_filtros_ordenamiento_y_consultas

- **Versión:** 0.2
- **Estado:** En revisión (reconstruido por consistencia)
- **Ámbito:** Backend V1 (Spring Boot + Java 21 + ORM)
- **Depende de:** `05_backend_v1_diseno_api_contrato_respuestas_y_errores.md`, `06_backend_v1_api_endpoints_y_casos_de_uso.md`, `07_backend_v1_validaciones_reglas_negocio_y_excepciones.md`
- **Relacionados:** `04_backend_v1_modelado_aplicacion_y_modulos.md`, `09_backend_v1_seguridad_documentacion_y_despliegue_minimo.md`
- **Objetivo de esta revisión:** Estandarizar cómo se manejan paginación, filtros, ordenamiento y consultas en endpoints GET/listados del backend V1 para tener un contrato predecible, seguro y usable desde JavaFX.

---

## 1. Propósito del documento

Este documento define **las reglas oficiales de consulta** del backend V1 para endpoints de lectura/listados:

- paginación (`page`, `size`)
- ordenamiento (`sort`)
- búsqueda simple (`q`)
- filtros por campo (IDs, enums, fechas, estados, etc.)
- validación de query params (`VR-*`)
- respuesta paginada estándar (`ApiResponse<PageResponseDto<T>>`)

Su objetivo es evitar:

- cada endpoint con query params distintos “porque sí”,
- filtros inconsistentes entre módulos,
- ordenamiento inseguro por campos arbitrarios,
- errores ambiguos cuando llegan parámetros inválidos,
- y acoplamiento innecesario del frontend a detalles internos.

✅ Este documento es la “gramática” de consultas del backend V1.

---

## 2. Principios rectores (V1)

## 2.1. Convenciones globales antes que creatividad por endpoint

Todos los endpoints de listado deben intentar usar la misma base:

- `page`
- `size`
- `sort`
- `q`

Luego cada módulo agrega sus filtros específicos.

✅ Esto reduce fricción mental y simplifica JavaFX.

---

## 2.2. Paginación en backend, no en frontend

Los listados potencialmente crecientes deben paginarse en backend.

❌ No descargar toda la tabla para filtrar/paginar en JavaFX.

✅ Mejor rendimiento, menor memoria, mejor escalabilidad.

---

## 2.3. Ordenamiento con whitelist explícita

Nunca permitir ordenar por cualquier campo recibido del cliente sin validación.

✅ Usar lista blanca por endpoint (campos soportados).

Esto evita:
- errores por nombres inválidos,
- inconsistencias con joins/campos no expuestos,
- comportamiento impredecible.

---

## 2.4. `q` es búsqueda simple global (no reemplaza filtros estructurados)

`q` sirve para búsquedas rápidas tipo texto.

Los filtros por campo siguen siendo necesarios para:
- estado
- fechas
- IDs
- enums
- rangos

✅ `q` complementa, no sustituye.

---

## 2.5. Validar query params también es parte de `VR-*`

Los errores en `page`, `size`, `sort`, enums y fechas deben producir `VR-*` consistentes (`400`), alineados con `07`.

---

## 3. Alcance de este documento

## 3.1. Aplica a

Principalmente a endpoints `GET` de consulta/listado, por ejemplo:
- `GET /api/v1/estudiantes`
- `GET /api/v1/secciones`
- `GET /api/v1/secciones/{id}/estudiantes`
- `GET /api/v1/calificaciones`
- `GET /api/v1/reportes/solicitudes`

## 3.2. No aplica directamente a

- endpoints de comando (`POST`, `PUT`, `PATCH`, `DELETE`)
- reglas de negocio (`RN-*`) salvo que la consulta implique consistencia contextual
- seguridad/JWT (eso se detalla en `09`)

---

## 4. Contrato de respuesta paginada (obligatorio)

## 4.1. Respuesta estándar de éxito paginado

Los listados paginados responden con:

- `ApiResponse<PageResponseDto<T>>`

Esto mantiene consistencia con `05`.

---

## 4.2. Estructura sugerida de `PageResponseDto<T>`

> El nombre exacto y campos finales se consolidan en `05`/código, pero esta es la forma recomendada V1.

### Campos sugeridos
- `items` → lista de elementos (`List<T>`) (equivalente a `Page.getContent()`)
- `page` → página actual (base 0)
- `size` → tamaño de página solicitado/aplicado
- `totalElements` → total de registros
- `totalPages` → total de páginas
- `numberOfElements` → elementos de la página actual
- `first` → boolean
- `last` → boolean
- `sort` → resumen de orden aplicado (opcional pero útil)

✅ Muy amigable para JavaFX (tablas + paginadores).

---

## 4.3. Política de respuesta de listados no paginados

Solo se permite respuesta no paginada cuando:
- el catálogo es realmente pequeño,
- estable,
- y su tamaño está controlado.

En ese caso:
- `ApiResponse<List<T>>`

⚠️ No usar esta excepción para entidades operativas principales (`estudiantes`, `calificaciones`, `reportes`).

---

## 5. Convención oficial de query params comunes

## 5.1. `page`

### Significado
Número de página solicitado.

### Convención V1
- **base 0** (compatibilidad natural con Spring `Pageable`)

### Ejemplos
- `page=0` → primera página
- `page=1` → segunda página

### Validación
- entero
- `>= 0`

### Error si inválido
- `VR-02-PARAMETRO_INVALIDO` (`400`)

---

## 5.2. `size`

### Significado
Cantidad de elementos por página.

### Política V1 recomendada
- `defaultSize = 20`
- `maxSize = 100` *(ajustable; valor razonable para V1)*

### Validación
- entero
- `>= 1`
- `<= maxSize`

### Error si inválido
- `VR-05-RANGO_NUMERICO_INVALIDO` o `VR-02-PARAMETRO_INVALIDO` (`400`)

✅ Recomendación práctica: normalizar y documentar un máximo fijo.

---

## 5.3. `sort`

### Significado
Ordenamiento de resultados.

### Formato oficial V1
- `sort=campo,direccion`
- donde `direccion ∈ {asc,desc}`

### Ejemplos
- `sort=nombres,asc`
- `sort=fechaCreacion,desc`

### Múltiples criterios (permitido)
Puede repetirse el query param:
- `?sort=estado,asc&sort=fechaSolicitud,desc`

✅ Spring lo soporta naturalmente si se parsea con cuidado.

### Validación obligatoria
- `campo` debe estar en whitelist del endpoint
- `direccion` válida (`asc|desc`)

### Errores
- `VR-02-PARAMETRO_INVALIDO` (`400`) si formato o dirección inválidos
- `VR-02-PARAMETRO_INVALIDO` (`400`) si campo no permitido

---

## 5.4. `q`

### Significado
Búsqueda simple libre de texto para listados.

### Convención oficial V1
- query param único: **`q`**
- búsqueda opcional
- si viene vacío/blanco, se ignora (o se normaliza a `null`)

### Ejemplos
- `?q=juan`
- `?q=3ro b`
- `?q=perez`

### Alcance típico de `q`
Depende del endpoint (se define en su whitelist de campos de búsqueda), por ejemplo:
- estudiantes → nombres, apellidos, identificación, código
- secciones → nombre/código/grado/paralelo
- reportes → tipo, estado (si tiene sentido textual), referencia visible

✅ `q` debe ser útil, pero acotado.

---

## 5.5. Query params específicos de filtro

Cada endpoint puede agregar filtros estructurados, por ejemplo:
- `estado`
- `seccionId`
- `estudianteId`
- `tipoReporte`
- `fechaDesde`
- `fechaHasta`
- `parcial`

Estos filtros **no reemplazan** `page/size/sort/q`; se combinan con ellos.

---

## 6. Reglas de combinación de filtros (semántica)

## 6.1. Semántica por defecto: AND

Cuando llegan múltiples filtros, la combinación por defecto es:

- **AND** entre filtros estructurados

Ejemplo:
- `estado=ACTIVO` **y** `seccionId=4` **y** `q=juan`

✅ Esta semántica es la más intuitiva y fácil de documentar en V1.

---

## 6.2. `q` como filtro adicional

`q` se aplica como filtro de texto adicional, no como reemplazo de filtros estructurados.

Ejemplo:
- `GET /estudiantes?estado=ACTIVO&q=perez`

Interpretación:
- estudiantes activos
- que además coincidan con la búsqueda textual

---

## 6.3. OR explícito (no recomendado en V1 general)

Evitar diseñar OR complejos por query params en V1 (`or`, `group`, etc.) salvo necesidad real.

✅ Mantener consultas predecibles y simples.

---

## 7. Política de normalización de query params

## 7.1. Strings

- `trim()` al inicio/fin
- si queda vacío → `null` (cuando aplique)

### Aplica especialmente a
- `q`
- filtros de texto

---

## 7.2. IDs numéricos

- validar tipo numérico
- validar `> 0` (si corresponde)

---

## 7.3. Enums

- parseo controlado
- si valor inválido → `VR-06-VALOR_ENUM_INVALIDO` (`400`)

✅ Mejor que dejar fallar con mensaje crudo del framework.

---

## 7.4. Fechas y rangos

- validar formato esperado (`YYYY-MM-DD` si se usa `LocalDate`)
- validar consistencia (`fechaDesde <= fechaHasta`)

Errores:
- `VR-02-PARAMETRO_INVALIDO` (formato)
- `VR-05-RANGO_NUMERICO_INVALIDO` o `VR-02` (si rango inválido; en V1 puede unificarse como `VR-02`)

---

## 8. Estrategia de implementación en Spring Boot (sin sobreingeniería)

## 8.1. Opción recomendada V1: `Pageable` + filtros explícitos

Para listados, usar:
- `Pageable` (Spring Data) para `page/size/sort`
- query params explícitos para filtros (`estado`, `seccionId`, etc.)
- servicio de aplicación que orquesta y valida

✅ Es la “vieja confiable” y profesional.

---

## 8.2. Dónde validar `page/size/sort`

### Recomendación V1
Validar en capa `api` o en un helper/assembler de consulta antes de llegar al repositorio.

Ejemplo conceptual:
- controller recibe params
- normaliza/valida
- construye `Pageable` seguro
- delega a `application`

✅ Evita pasar `sort` sin filtrar a capa de persistencia.

---

## 8.3. Query objects (opcional pero útil)

Cuando un endpoint tiene muchos filtros, conviene agruparlos en un DTO de consulta interno (no necesariamente request body), por ejemplo:

- `EstudianteSearchCriteria`
- `ReporteSolicitudSearchCriteria`

### Beneficios
- firma de método más limpia
- validación centralizada
- más fácil de testear luego

✅ Muy recomendable si empiezan a crecer filtros en `calificaciones` y `reportes`.

---

## 8.4. No acoplar controller a JPQL/SQL

El controller no debe construir consultas SQL/JPQL.

✅ Controller parsea → application decide → repository ejecuta.

---

## 9. Política de ordenamiento (whitelist por endpoint)

## 9.1. Principio general

Cada endpoint de listado define una **whitelist de campos ordenables**.

### Ejemplo conceptual
`GET /estudiantes` soporta:
- `id`
- `apellidos`
- `nombres`
- `estado`
- `fechaCreacion`

No soporta:
- campos internos no expuestos
- propiedades anidadas arbitrarias
- nombres de columna de BD directos (si no coinciden con contrato)

✅ El cliente ordena por **campos documentados**, no por detalles de BD.

---

## 9.2. Mapeo `sort` API → campo de dominio/persistencia

Puede haber diferencias entre:
- nombre del campo en API
- propiedad de entidad JPA
- columna real en BD

### Recomendación V1
Mantener una tabla/mapa explícito de campos soportados por endpoint.

Ejemplo conceptual:
- `fechaCreacion` → `createdAt`
- `nombreCompleto` → *(no permitir si requiere lógica compleja, salvo implementación explícita)*

✅ Esto desacopla la API de la base de datos.

---

## 9.3. Orden por defecto (si no llega `sort`)

Cada endpoint debe definir su orden por defecto.

### Recomendación general
- listados operativos: más recientes primero si es flujo temporal
- catálogos/personas: orden alfabético natural

Ejemplos:
- `/estudiantes` → `apellidos ASC, nombres ASC`
- `/secciones` → `nombre ASC`
- `/reportes/solicitudes` → `fechaSolicitud DESC`

✅ Documentar el default en Swagger (`09`).

---

## 10. Política de búsqueda simple `q`

## 10.1. Objetivo

`q` está pensado para la experiencia del usuario (JavaFX) con una caja de búsqueda simple.

Debe permitir búsquedas comunes sin exponer sintaxis avanzada.

---

## 10.2. Reglas V1 de `q`

- `q` es texto libre simple
- sin operadores avanzados (`AND`, `OR`, comillas, etc.) en V1
- aplicar `trim()`
- si `q` está vacío → ignorar
- longitud máxima recomendada (ej. 100 caracteres)

### Error por exceso de longitud (si decides validarlo)
- `VR-02-PARAMETRO_INVALIDO` (`400`)

✅ Esto protege rendimiento y evita inputs absurdos.

---

## 10.3. Campos alcanzados por `q` (por endpoint)

Debe documentarse por endpoint qué campos cubre `q`.

### Ejemplo (estudiantes)
`q` puede buscar en:
- nombres
- apellidos
- identificación/código (si aplica)

### Ejemplo (secciones)
`q` puede buscar en:
- nombre/código
- grado/paralelo (si representados como texto)

### Ejemplo (reportes/solicitudes)
`q` puede buscar en:
- tipo de reporte (texto visible)
- referencia/código de solicitud
- usuario solicitante (si aplica)

✅ Evitar que `q` haga joins complejos innecesarios en V1 sin justificación.

---

## 11. Política de filtros por tipo de dato

## 11.1. Filtros por ID (`Long`, `Integer`)

Ejemplos:
- `estudianteId`
- `seccionId`
- `materiaId`

### Reglas
- numéricos
- positivos
- semántica exacta (`=`)

Errores inválidos → `VR-02-PARAMETRO_INVALIDO` (`400`)

---

## 11.2. Filtros por enum

Ejemplos:
- `estado`
- `tipoReporte`

### Reglas
- valor dentro del catálogo permitido
- idealmente documentado en Swagger como enum

Errores inválidos → `VR-06-VALOR_ENUM_INVALIDO` (`400`)

---

## 11.3. Filtros booleanos (si aplican)

Ejemplos:
- `activo=true`
- `conError=false`

### Reglas
- solo valores válidos (`true`/`false`)
- evitar múltiples variantes ambiguas (`1/0`, `si/no`) en V1

✅ Simplifica parseo y documentación.

---

## 11.4. Filtros por fecha/rango

Ejemplos:
- `fechaDesde`
- `fechaHasta`

### Reglas V1
- formato ISO simple (`YYYY-MM-DD`) si se usa `LocalDate`
- rango inclusivo documentado (recomendado) o claramente definido
- validar `fechaDesde <= fechaHasta`

### Errores comunes
- formato inválido → `VR-02-PARAMETRO_INVALIDO`
- rango inconsistente → `VR-02-PARAMETRO_INVALIDO`

---

## 11.5. Filtros numéricos de rango (si aplican)

Ejemplos:
- `valorMin`
- `valorMax`
- `parcial`

### Reglas
- validar límites
- validar consistencia `min <= max`
- documentar si el rango es inclusivo

---

## 12. Catálogo de endpoints paginados y reglas específicas (V1)

> Esta sección aterriza `06` con criterios concretos de consulta.

## 12.1. `GET /api/v1/estudiantes`

### Soporta paginación
✅ Sí

### Query params comunes
- `page`
- `size`
- `sort`
- `q`

### Filtros específicos sugeridos (V1)
- `estado` (enum)
- `seccionId` (ID, si el modelo/consulta lo soporta)

### Ordenamiento permitido (whitelist sugerida)
- `id`
- `apellidos`
- `nombres`
- `estado`
- `fechaCreacion`

### Orden por defecto sugerido
- `apellidos,asc`
- `nombres,asc`

### Campos cubiertos por `q` (sugerido)
- nombres
- apellidos
- identificación/código visible

---

## 12.2. `GET /api/v1/secciones`

### Soporta paginación
✅ Sí

### Query params comunes
- `page`, `size`, `sort`, `q`

### Filtros específicos sugeridos
- `estado`
- `grado` *(si aplica al modelo expuesto)*
- `paralelo` *(si aplica)*
- `periodoId` *(si existe en V1)*

### Ordenamiento permitido (whitelist sugerida)
- `id`
- `nombre`
- `estado`
- `capacidad`
- `fechaCreacion`

### Orden por defecto sugerido
- `nombre,asc`

### Campos cubiertos por `q`
- nombre/código de sección
- grado/paralelo textual (si aplica)

---

## 12.3. `GET /api/v1/secciones/{seccionId}/estudiantes`

### Soporta paginación
✅ Sí

### Query params comunes
- `page`, `size`, `sort`, `q`

### Filtros específicos sugeridos
- `estado` (del estudiante) *(opcional)*

### Ordenamiento permitido (whitelist sugerida)
- `id`
- `apellidos`
- `nombres`
- `estado`

### Orden por defecto sugerido
- `apellidos,asc`
- `nombres,asc`

### Notas de diseño
- `seccionId` es path param obligatorio
- si `seccionId` no existe → `API-04-RECURSO_NO_ENCONTRADO` (`404`)

---

## 12.4. `GET /api/v1/calificaciones` (si entra en V1)

### Soporta paginación
✅ Sí

### Query params comunes
- `page`, `size`, `sort`, `q` *(opcional; solo si aporta valor)*

### Filtros específicos sugeridos
- `estudianteId`
- `seccionId`
- `materiaId`
- `parcial`
- `periodoId` (si aplica)

### Ordenamiento permitido (whitelist sugerida)
- `id`
- `parcial`
- `valor`
- `fechaRegistro`

### Orden por defecto sugerido
- `fechaRegistro,desc`

### Nota
Si `q` no aporta utilidad real aquí, puede omitirse del endpoint para evitar complejidad innecesaria.

✅ V1 pragmática: no forzar `q` en todos lados.

---

## 12.5. `GET /api/v1/reportes/solicitudes`

### Soporta paginación
✅ Sí

### Query params comunes
- `page`, `size`, `sort`, `q`

### Filtros específicos sugeridos
- `estado` (enum)
- `tipoReporte` (enum)
- `fechaDesde`
- `fechaHasta`

### Ordenamiento permitido (whitelist sugerida)
- `id`
- `estado`
- `tipoReporte`
- `fechaSolicitud`
- `fechaActualizacion`

### Orden por defecto sugerido
- `fechaSolicitud,desc`

### Campos cubiertos por `q` (sugerido)
- referencia/identificador visible
- tipo de reporte (texto)
- usuario solicitante (si se expone)

✅ Este endpoint es clave para tabla + polling en JavaFX.

---

## 13. Política de validación y errores en consultas (alineada con `07`)

## 13.1. Errores `VR-*` típicos en query params

### `VR-02-PARAMETRO_INVALIDO`
Casos típicos:
- `page=abc`
- `sort=nombre,up`
- `sort=campoNoPermitido,asc`
- `fechaDesde=2026/01/01`

### `VR-05-RANGO_NUMERICO_INVALIDO`
Casos típicos:
- `size=0`
- `size=1000` (si supera máximo)
- `page=-1`

### `VR-06-VALOR_ENUM_INVALIDO`
Casos típicos:
- `estado=ACTIVADOOO`
- `tipoReporte=XYZ`

---

## 13.2. Estructura sugerida de `details` para errores de query param

Alineado con `05`/`07`, cuando sea posible incluir detalle por parámetro:

- `field`: nombre del query param
- `code`: código interno corto (opcional)
- `message`: mensaje legible
- `rejectedValue`: valor recibido (cuidando no exponer datos sensibles)

### Ejemplo conceptual
- `field: "sort"`
- `message: "El campo de ordenamiento 'foo' no está permitido."`

✅ Esto ayuda muchísimo a la UI y a depurar integración.

---

## 13.3. ¿Cuándo puede aparecer `RN-*` en un GET?

Aunque la mayoría de consultas fallan por `VR/API`, puede haber casos donde una consulta implique regla contextual.

Ejemplo (si lo decides en V1):
- combinación de filtros incompatible por política de negocio
- consulta de datos no disponibles por estado de proceso

### Recomendación V1
Evitar RN en GET salvo necesidad real; preferir consultas tolerantes y bien documentadas.

✅ Menos fricción para la UI.

---

## 14. Diseño de capa de aplicación para consultas

## 14.1. Controllers delgados

El controller debe:
- recibir query params
- validar/normalizar básicos
- construir criterio + pageable seguro
- delegar a `application`

No debe:
- escribir lógica de filtros complejos
- construir SQL/JPQL
- decidir reglas de negocio profundas

---

## 14.2. Servicios de aplicación orientados a consultas

Ejemplos de firmas conceptuales:

- `Page<EstudianteListItemDto> listar(EstudianteSearchCriteria criteria, Pageable pageable)`
- `Page<ReporteSolicitudListItemDto> listar(ReporteSolicitudSearchCriteria criteria, Pageable pageable)`

✅ Firmas claras y fáciles de mantener.

---

## 14.3. Criterios de búsqueda (`SearchCriteria`) recomendados

Cuando hay varios filtros, usar objetos de criterio evita firmas largas y repetitivas.

### Ejemplos
- `EstudianteSearchCriteria`
  - `q`
  - `estado`
  - `seccionId`

- `ReporteSolicitudSearchCriteria`
  - `q`
  - `estado`
  - `tipoReporte`
  - `fechaDesde`
  - `fechaHasta`

✅ Esto también ayuda a testear después sin tocar controller.

---

## 15. Estrategias de persistencia para consultas (V1)

## 15.1. Opción V1 recomendada: Spring Data + consultas dinámicas simples

Sin sobreingeniería, puedes implementar consultas con:
- Spring Data JPA
- `Pageable`
- `Specification` (si te conviene)
- repositorios custom puntuales para consultas complejas

✅ Lo importante es mantener el contrato estable, no casarte con una técnica prematuramente.

---

## 15.2. Cuándo usar `Specification` (opcional)

Útil cuando hay:
- múltiples filtros opcionales,
- combinaciones crecientes,
- necesidad de evitar muchos métodos `findBy...`.

### No obligatorio si
- el endpoint tiene 1–2 filtros simples.

✅ V1 pragmática: usarlo donde aporte, no por moda.

---

## 15.3. Proyecciones/DTOs de lectura

Para listados pesados, puede ser mejor devolver DTO/proyección directamente desde consulta (cuando aplique), en lugar de cargar entidades completas con relaciones innecesarias.

✅ Mejora rendimiento y reduce acoplamiento.

---

## 15.4. Evitar N+1 en listados

Si un listado muestra campos de relaciones, cuidar no disparar consultas por cada fila.

### Medidas prácticas V1
- diseñar DTOs de listado livianos
- usar joins/proyecciones controladas
- revisar consultas de endpoints de tablas principales

✅ Importante especialmente en `secciones/{id}/estudiantes` y reportes.

---

## 16. Política de performance y límites (V1)

## 16.1. Tamaño máximo de página

Definir un máximo global (ej. `100`) protege al backend de consultas excesivas.

✅ Si el cliente pide más, responder error `VR-*` o capear (recomendado: error explícito para aprender mejor).

---

## 16.2. Búsqueda `q` razonable

Evitar búsquedas de texto sobre demasiados campos/joins en V1.

✅ Priorizar utilidad real de UI.

---

## 16.3. Índices y base de datos (coordinación futura)

La calidad de consultas depende también de índices. Este documento no rediseña BD, pero sí deja claro qué campos de consulta/orden son candidatos naturales a índice en DB.

Ejemplos típicos:
- estados
- fechas de creación/solicitud
- claves de relación frecuentes
- identificadores de búsqueda

✅ Esto sirve luego para tuning sin romper API.

---

## 17. Integración con JavaFX (sin acoplar la API)

## 17.1. Qué espera JavaFX de los listados

Para tablas/pantallas de gestión, JavaFX suele necesitar:
- lista actual (`items`)
- total de elementos/páginas
- página actual
- orden aplicado
- mensajes de error por parámetro

✅ `PageResponseDto<T>` + `ApiErrorResponse.details` cubren esto muy bien.

---

## 17.2. Recomendación de UX técnica para JavaFX

- mantener estado local de filtros (`q`, `estado`, etc.)
- hacer llamadas paginadas al backend
- reiniciar a `page=0` cuando cambian filtros
- reusar endpoint `/estado` para polling puntual en reportes

✅ Esto reduce confusión y llamadas innecesarias.

---

## 18. Ejemplos de URLs válidas (V1)

## 18.1. Estudiantes

```text
GET /api/v1/estudiantes?page=0&size=20&sort=apellidos,asc&sort=nombres,asc&q=perez&estado=ACTIVO
```

---

## 18.2. Secciones

```text
GET /api/v1/secciones?page=0&size=10&sort=nombre,asc&estado=ACTIVA
```

---

## 18.3. Estudiantes de una sección

```text
GET /api/v1/secciones/4/estudiantes?page=0&size=20&sort=apellidos,asc&q=juan
```

---

## 18.4. Reportes/solicitudes

```text
GET /api/v1/reportes/solicitudes?page=0&size=20&sort=fechaSolicitud,desc&estado=PENDIENTE&tipoReporte=LISTADO_GENERAL&fechaDesde=2026-01-01&fechaHasta=2026-01-31
```

---

## 19. Ejemplos de errores de consulta (alineados con `05` y `07`)

## 19.1. `size` inválido

Situación:
- `GET /api/v1/estudiantes?size=0`

Resultado esperado:
- `400 Bad Request`
- `ApiErrorResponse`
- código: `VR-05-RANGO_NUMERICO_INVALIDO`

---

## 19.2. `sort` no permitido

Situación:
- `GET /api/v1/secciones?sort=passwordHash,asc`

Resultado esperado:
- `400 Bad Request`
- código: `VR-02-PARAMETRO_INVALIDO`

✅ La whitelist protege incluso si el cliente intenta campos absurdos.

---

## 19.3. Enum inválido

Situación:
- `GET /api/v1/reportes/solicitudes?estado=SUPERPENDIENTE`

Resultado esperado:
- `400 Bad Request`
- código: `VR-06-VALOR_ENUM_INVALIDO`

---

## 19.4. Recurso inexistente en consulta compuesta

Situación:
- `GET /api/v1/secciones/9999/estudiantes`

Resultado esperado:
- `404 Not Found`
- código: `API-04-RECURSO_NO_ENCONTRADO`

---

## 20. Swagger/OpenAPI: qué documentar de consultas (insumo para `09`)

## 20.1. Por endpoint de listado documentar

- `page` (base 0)
- `size` (default y máximo)
- `sort` (formato y campos permitidos)
- `q` (si el endpoint lo soporta)
- filtros específicos (tipo, enum, formato)
- respuesta paginada (`ApiResponse<PageResponseDto<...>>`)
- errores `400/404/401/403/500` y `501` si aplica

✅ Esto hace que Swagger sea realmente útil, no solo decorativo.

---

## 20.2. Ejemplos en Swagger

Muy recomendable incluir ejemplos reales de query params en endpoints clave:
- `/estudiantes`
- `/secciones`
- `/reportes/solicitudes`

✅ Te ayudará mucho cuando conectes JavaFX y pruebes con Postman/Swagger UI.

---

## 21. Checklist de diseño/implementación de consultas

Antes de dar por terminado un endpoint de listado:

### 21.1. Contrato
- [ ] ¿Responde con `ApiResponse<PageResponseDto<T>>` si corresponde?
- [ ] ¿El DTO de listado es liviano y útil para tabla?

### 21.2. Paginación
- [ ] ¿Usa `page` base 0?
- [ ] ¿Tiene `defaultSize` y `maxSize`?
- [ ] ¿Valida rangos y tipos?

### 21.3. Ordenamiento
- [ ] ¿Existe whitelist de campos?
- [ ] ¿Tiene orden por defecto documentado?
- [ ] ¿Valida `asc|desc`?

### 21.4. Filtros
- [ ] ¿`q` está definido (si aplica) y documentado?
- [ ] ¿Los filtros por enum/fecha/ID se validan?
- [ ] ¿La semántica de combinación es AND?

### 21.5. Errores
- [ ] ¿Devuelve `VR-*` consistentes en query params inválidos?
- [ ] ¿`404` aplica cuando hay path param de recurso padre?

### 21.6. Performance
- [ ] ¿`size` máximo está limitado?
- [ ] ¿No hay N+1 evidente en el listado?

---

## 22. Decisiones fijadas por este documento (V1)

1. ✅ Se adopta convención global de consulta: `page`, `size`, `sort`, `q`.
2. ✅ `page` será **base 0**.
3. ✅ `size` tendrá `default` y `max` globales documentados.
4. ✅ `sort` usará formato `campo,direccion` y permitirá múltiples criterios.
5. ✅ Todo ordenamiento usará whitelist por endpoint (sin campos arbitrarios).
6. ✅ `q` se mantiene como búsqueda simple libre y opcional.
7. ✅ Los filtros estructurados se combinan con semántica **AND**.
8. ✅ Errores de query params se modelan como `VR-*` (`400`) bajo contrato estándar.
9. ✅ Listados principales responderán con `ApiResponse<PageResponseDto<T>>`.
10. ✅ El diseño de consultas será usable para JavaFX sin acoplar la API a pantallas específicas.

---

## 23. Relación con el siguiente documento (`09`)

Este documento deja listo para `09_backend_v1_seguridad_documentacion_y_despliegue_minimo.md`:

- cómo documentar parámetros en Swagger/OpenAPI,
- qué ejemplos de consulta publicar,
- qué errores `400/401/403/404/500` deben aparecer en endpoints de listado,
- y cómo mantener consistencia entre contrato y seguridad.

---

## 24. Cierre

Con este documento, la parte de “los signos de interrogación y parámetros raros” de REST queda formalizada de forma profesional y sencilla.

Eso te permite avanzar con seguridad en Spring Boot porque ya sabes:
- qué parámetros aceptar,
- cómo validarlos,
- cómo responder errores,
- cómo paginar/ordenar sin improvisar,
- y cómo diseñar consultas que luego JavaFX pueda consumir cómodamente.

✅ Aquí ya estás cubriendo una parte muy importante del backend real que muchos omiten al inicio.


