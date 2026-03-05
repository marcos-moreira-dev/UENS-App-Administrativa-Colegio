# 09_backend_v1_seguridad_documentacion_y_despliegue_minimo

> Complemento vigente: para el hardening operativo ya implementado en codigo revisar tambien `20_backend_v_1_hardening_seguridad_login_rate_limit_cors_headers_ownership.md`.

- **Versión:** 0.2
- **Estado:** En revisión (reconstruido y alineado con `05–08`)
- **Ámbito:** Backend V1 (Spring Boot + Java 21 + ORM + DTOs + mappers manuales)
- **Depende de:**
  - `00_backend_v1_indice_y_mapa_documental.md`
  - `01_backend_v1_vision_y_alcance.md`
  - `02_backend_v1_arquitectura_general.md`
  - `03_backend_v1_convenciones_y_estandares_codigo.md`
  - `04_backend_v1_modelado_aplicacion_y_modulos.md`
  - `05_backend_v1_diseno_api_contrato_respuestas_y_errores.md`
  - `06_backend_v1_api_endpoints_y_casos_de_uso.md`
  - `07_backend_v1_validaciones_reglas_negocio_y_excepciones.md`
  - `08_backend_v1_paginacion_filtros_ordenamiento_y_consultas.md`
- **Referencias de contexto funcional/base de datos:**
  - `01_levantamiento_informacion_negocio.md`
  - `02_levantamiento_requerimientos.md`
  - `03_modelo_conceptual_dominio.md`
  - `04_reglas_negocio_y_supuestos.md`
  - `05_glosario_alcance_y_limites.md`
  - `V2_3FN.sql`
- **Objetivo de esta revisión:** Definir una base V1 profesional y pragmática para seguridad, documentación de API y despliegue mínimo, manteniendo compatibilidad con JavaFX (cliente) sin acoplar el backend a la UI.

---

## 1. Propósito del documento

Este documento formaliza cómo se manejará en el Backend V1:

- **seguridad** (autenticación, autorización y respuestas de seguridad consistentes),
- **documentación de API** (OpenAPI/Swagger útil de verdad, no decorativa),
- **despliegue mínimo profesional** (perfiles, variables de entorno, Docker mínimo),
- y **operación básica** (healthcheck, logging mínimo, checklist de no meter la pata).

La meta es una solución:

- **realista** para práctica profesional,
- **simple de implementar** para una V1,
- **escalable conceptualmente** sin sobreingeniería,
- y alineada con los contratos/API definidos en `05`, `06`, `07` y `08`.

✅ Este documento responde a la pregunta: *“¿Cómo hago que mi backend sea usable en trabajo real, seguro y desplegable, sin complicarme de más?”*

---

## 2. Alcance V1 y decisiones principales

## 2.1. Qué sí entra en esta V1

### Seguridad (núcleo)
- login backend (`POST /api/v1/auth/login`)
- autenticación **JWT stateless**
- autorización por roles (base)
- endpoints públicos/protegidos
- manejo correcto de `401` y `403` con `ApiErrorResponse`

### Documentación
- OpenAPI 3 + Swagger UI
- documentación de auth, errores y listados (según `08`)
- esquema Bearer JWT en Swagger

### Despliegue mínimo
- variables de entorno
- perfiles `dev` y `prod`
- `Dockerfile` mínimo
- `docker-compose.yml` mínimo (`backend + postgres`)
- healthcheck básico (idealmente con Actuator)

### Operación mínima
- logging básico útil
- Swagger habilitado en `dev`
- checklist de despliegue y validación

---

## 2.2. Qué NO entra todavía (se difiere)

Para evitar frenar V1:

- refresh tokens robustos
- revocación distribuida de tokens
- RBAC/ABAC avanzado
- auditoría exhaustiva
- rate limiting avanzado
- multi-tenant
- CI/CD formal
- observabilidad completa (metrics + tracing centralizado)
- vault/secret manager profesional
- SSO / OAuth2 / LDAP

✅ Se dejan como evolución natural, no como requisito de arranque.

---

## 2.3. Decisiones fijadas en este documento (resumen rápido)

1. ✅ Autenticación V1 con **JWT stateless**.
2. ✅ Hash de contraseñas con **BCrypt**.
3. ✅ Autorización por roles con Spring Security (rutas + `@PreAuthorize` cuando convenga).
4. ✅ `401/403` siempre en **JSON** bajo el contrato de `05` (sin HTML por defecto de Spring).
5. ✅ Configuración sensible por **variables de entorno**.
6. ✅ Perfiles `dev` / `prod`.
7. ✅ OpenAPI/Swagger con **Bearer JWT**.
8. ✅ Docker mínimo: **backend + postgres** con Compose.
9. ✅ Swagger en `dev`; en `prod` deshabilitado o restringido.
10. ✅ Reportes pueden quedar como placeholder/backend parcial sin romper arquitectura.

---

## 3. Principios rectores de seguridad V1

## 3.1. Seguridad por capas (mínimo profesional)

La seguridad no se reduce a “poner login”. En V1 se cubren estas capas:

1. **Autenticación** → quién eres.
2. **Autorización** → qué puedes hacer.
3. **Validación** → qué datos puedes enviar.
4. **Errores seguros** → qué información se devuelve (sin filtrar internals).
5. **Configuración segura** → secretos fuera del código.
6. **Operación mínima segura** → perfiles/logs/docs bien configurados.

---

## 3.2. Backend agnóstico de UI, pero NO agnóstico del caso de uso

El backend debe ser consumible por JavaFX, web o móvil, pero:

- **sí** impone reglas de acceso,
- **sí** impone validaciones,
- **sí** rechaza operaciones no permitidas,
- y **sí** modela el comportamiento según casos de uso (`06`) y reglas (`07`).

✅ Ocultar botones en UI no sustituye seguridad backend.

---

## 3.3. Principio de mínimo privilegio

Cada rol recibe solo lo necesario.

Ejemplo conceptual (ajustar a tu dominio real):
- `ADMIN`
- `SECRETARIA`
- `DOCENTE` (si aplica en evolución)

Aunque V1 sea práctica, modelarlo así desde el inicio te entrena mejor para trabajo real.

---

## 3.4. Coherencia con DDD-lite / encapsulación

Aunque este documento es técnico-operativo, se alinea con tu intención de mayor control del dominio:

- evitar setters innecesarios en entidades/agregados,
- centralizar cambios en métodos de dominio o servicios de aplicación,
- y **no saltarse** reglas solo porque el request llegó autenticado.

✅ Seguridad valida acceso.
✅ Dominio valida invariantes.
✅ Aplicación orquesta.

---

## 4. Modelo de autenticación V1 (JWT stateless)

## 4.1. ¿Por qué JWT en esta V1?

Porque encaja muy bien con una API REST desacoplada del frontend y te permite practicar lo más común en entorno profesional actual.

### Ventajas para tu caso
- cliente JavaFX, web o móvil pueden consumir igual
- backend stateless (más simple de escalar conceptualmente)
- arquitectura limpia para practicar Spring Security
- despliegue sencillo (sin sesión server-side obligatoria)

### Qué se evita por ahora
- complejidad de refresh tokens y rotación avanzada
- manejo de sesiones distribuidas

✅ Decisión V1: **JWT simple, corto y bien implementado**.

---

## 4.2. Flujo general de autenticación (V1)

1. Cliente envía credenciales a `POST /api/v1/auth/login`
2. Backend valida usuario + contraseña + estado de usuario
3. Backend genera JWT firmado
4. Cliente guarda token de forma controlada
5. Cliente envía `Authorization: Bearer <token>`
6. Backend valida token en endpoints protegidos

---

## 4.3. Endpoint de login (contrato de alto nivel)

### Endpoint
- `POST /api/v1/auth/login`

### Request DTO (conceptual)
- `login`
- `password`

### Response DTO (conceptual)
Bajo `ApiResponse<T>` (`05`), devolver por ejemplo:
- `accessToken`
- `tokenType` = `Bearer`
- `expiresInSeconds`
- `usuario` (resumen sin datos sensibles)
  - `id`
  - `login`
  - `rol`
  - `estado`

✅ No devolver password, hashes, ni campos internos.

---

## 4.4. Claims mínimos recomendados en el JWT

Mantener el token pequeño y útil.

### Claims sugeridos V1
- `sub` → identificador principal (idealmente `userId` o login, pero consistente)
- `uid` → id técnico (si `sub` no es id)
- `rol` → rol principal
- `iat` → fecha de emisión
- `exp` → fecha de expiración
- `iss` → issuer (opcional, recomendado)

### No incluir
- datos personales innecesarios
- listas extensas de permisos
- hashes/secretos

---

## 4.5. Expiración del token (V1)

### Recomendación V1 práctica
- `2 horas` para entorno administrativo de práctica

Es un punto medio razonable entre:
- comodidad (no pedir login cada 5 minutos)
- seguridad (no dejar sesiones casi infinitas)

> Si luego necesitas sesiones más largas en JavaFX, puedes ajustar expiración o introducir refresh tokens en V2.

---

## 4.6. Refresh token (decisión V1)

### Decisión
✅ **No implementar refresh token en V1**.

### Motivo
- reduces complejidad
- avanzas más rápido en el backend funcional
- aprendes bien el núcleo (login + JWT + roles + handlers)

---

## 4.7. Almacenamiento del token en JavaFX (nota de integración)

Este documento no diseña JavaFX, pero sí deja recomendaciones para el cliente:

### Recomendación V1 pragmática
- guardar token en memoria durante sesión, o
- en almacenamiento local controlado si la app requiere persistencia de sesión

### Evitar
- logs con token completo
- exponer token en mensajes de error
- hardcodear token para “probar rápido” y olvidarlo

✅ Esta nota es importante porque backend + cliente forman una cadena de seguridad.

---

## 5. Contraseñas y autenticación de usuario (política V1)

## 5.1. Hash de contraseñas

### Regla obligatoria
❌ Nunca almacenar contraseñas en texto plano.

### Decisión V1
✅ Usar `BCryptPasswordEncoder` (Spring Security).

Es la vieja confiable y suficiente para V1.

---

## 5.2. Validaciones mínimas en login

Durante login, validar al menos:
- credenciales correctas
- usuario activo/habilitado (si el modelo lo contempla)
- opcional: rol permitido para usar el sistema (según negocio)

### Errores esperados (alineados a `05`/`07`)
- `AUTH-01-CREDENCIALES_INVALIDAS` → `401`
- `AUTH-05-USUARIO_INACTIVO` → `401` o `403` según política (recomendado `401` con mensaje controlado)

✅ Para el cliente, el mensaje puede ser genérico por seguridad.

---

## 5.3. Política de mensajes de error en login

Evitar revelar demasiado detalle a un atacante.

### Recomendación V1
Respuesta legible pero controlada:
- “Credenciales inválidas”
- “No autorizado”

### En logs internos (backend)
Puedes registrar más contexto técnico **sin** exponer password/tokens.

---

## 6. Autorización V1 (roles + casos de uso)

## 6.1. Estrategia general

Usar Spring Security combinando:

- **reglas por ruta** (rápidas, globales)
- **restricciones por caso de uso** (`@PreAuthorize`) donde convenga

### Recomendación V1
- reglas base por ruta
- `@PreAuthorize` para endpoints sensibles o específicos

✅ Profesional y sin complicarse demasiado.

---

## 6.2. Mapeo inicial de acceso por tipo de endpoint (propuesta V1)

> Ajustar nombres exactos según enums y módulos reales del proyecto.

### Públicos (sin token)
- `POST /api/v1/auth/login`
- `GET /actuator/health` (si se expone)
- Swagger/OpenAPI **solo en dev** (o restringido)

### Protegidos (requieren token)
- módulos CRUD principales (`estudiantes`, `secciones`, `calificaciones`, etc.)
- dashboard
- endpoints de reportes/solicitudes

### Restringidos a `ADMIN` (recomendado)
- gestión de usuarios del sistema
- cambios de rol/permisos
- endpoints de administración operativa sensible

---

## 6.3. Autorización por caso de uso (no solo por módulo)

No todos los endpoints de un módulo tienen que compartir el mismo permiso.

Ejemplo conceptual:
- `GET /api/v1/estudiantes` → `ADMIN`, `SECRETARIA`
- `POST /api/v1/estudiantes` → `ADMIN`, `SECRETARIA`
- `DELETE /api/v1/usuarios-sistema/{id}` → solo `ADMIN`

✅ Esto está alineado con tu idea: el backend responde a casos de uso, no a pantallas.

---

## 6.4. `401` vs `403` (regla fija)

- **401 Unauthorized** → no autenticado / token inválido / expirado
- **403 Forbidden** → autenticado pero sin permisos suficientes

Esto debe quedar consistente en código, Swagger y pruebas manuales.

---

## 7. Arquitectura técnica de seguridad en Spring Boot (V1)

## 7.1. Componentes recomendados (responsabilidades)

> Los nombres exactos pueden variar; lo importante es la separación de responsabilidades.

### Configuración
- `SecurityConfig`

### JWT
- `JwtTokenService` (generar + validar token)
- `JwtAuthenticationFilter` (leer header, validar token, poblar contexto)

### Usuario autenticable
- `CustomUserDetailsService` (o equivalente)

### Caso de uso de login
- `AuthController`
- `AuthApplicationService` / `AuthenticationService`

### Handlers JSON de seguridad
- `RestAuthenticationEntryPoint` (401)
- `RestAccessDeniedHandler` (403)

✅ Esto evita mezclar todo en un solo archivo gigante.

---

## 7.2. Configuración de `HttpSecurity` (lineamientos V1)

### Reglas base recomendadas
- API **stateless**
- `CSRF` deshabilitado para API JWT
- `sessionCreationPolicy(STATELESS)`
- rutas públicas explícitas
- resto autenticado por defecto
- filtro JWT antes del filtro adecuado del pipeline

### CORS
- JavaFX no lo necesita como navegador
- pero conviene dejar política explícita si luego habrá frontend web

---

## 7.3. Dónde viven las reglas de negocio de auth

La parte técnica (JWT/Spring Security) vive en infraestructura.

La parte funcional (ej. “usuario inactivo no puede entrar”) debe estar en:
- aplicación / dominio (según tu arquitectura `04`)

✅ DDD-lite práctico: Spring no se come toda la lógica de negocio.

---

## 8. Respuestas de seguridad y manejo de errores (alineado con `05` y `07`)

## 8.1. Regla obligatoria

Los errores de seguridad **también** deben salir con el contrato estándar.

✅ `ApiErrorResponse` para `401/403`
❌ HTML por defecto de Spring Security

---

## 8.2. Códigos sugeridos (`AUTH-*`)

> Ajustar nomenclatura exacta a tu catálogo final.

### `401`
- `AUTH-01-CREDENCIALES_INVALIDAS`
- `AUTH-02-TOKEN_INVALIDO`
- `AUTH-03-TOKEN_EXPIRADO`
- `AUTH-05-USUARIO_INACTIVO` (según política)

### `403`
- `AUTH-04-SIN_PERMISOS`

---

## 8.3. Ejemplos de situaciones típicas

### Token ausente en endpoint protegido
- Resultado: `401`
- Código: `AUTH-02-TOKEN_INVALIDO` (o uno más específico como token ausente, si lo deseas)

### Token expirado
- Resultado: `401`
- Código: `AUTH-03-TOKEN_EXPIRADO`

### Token válido pero rol insuficiente
- Resultado: `403`
- Código: `AUTH-04-SIN_PERMISOS`

---

## 8.4. Integración con `05` (contrato de errores)

La respuesta debería conservar estructura compatible con tu contrato estándar, por ejemplo:

- `ok = false`
- `error.code`
- `error.message`
- `error.details` (si aplica)
- `meta` / timestamp / path (según diseño final de `05`)

✅ Esto simplifica muchísimo JavaFX porque todos los errores se procesan igual.

---

## 9. Variables de entorno y configuración segura

## 9.1. Principio general

✅ Todo lo sensible debe salir del código y entrar por configuración externa.

### Nunca hardcodear en repo
- credenciales de BD
- secretos JWT
- claves de terceros
- configuración sensible de producción

---

## 9.2. Variables de entorno mínimas recomendadas (V1)

## 9.2.1. App / perfil
- `SPRING_PROFILES_ACTIVE` (ej. `dev`, `prod`)
- `SERVER_PORT`

## 9.2.2. Base de datos PostgreSQL
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`

## 9.2.3. JWT
- `JWT_SECRET`
- `JWT_EXPIRATION_SECONDS`
- `JWT_ISSUER` (opcional, recomendado)

## 9.2.4. Swagger / docs (opcional)
- `SWAGGER_ENABLED` (si decides controlarlo por env)

## 9.2.5. Logs (opcional útil)
- `LOG_LEVEL_ROOT`
- `LOG_LEVEL_APP`

---

## 9.3. Política de `JWT_SECRET`

### Reglas mínimas
- largo suficiente
- aleatorio
- distinto por ambiente
- no reutilizar claves de ejemplo
- no subir al repo

✅ En V1 basta con generar una cadena fuerte y pasarla por variable de entorno.

---

## 9.4. Estructura recomendada de configuración (`application*.properties`)

### `application.properties` (base)
- configuración común no sensible
- placeholders `${...}`

### `application-dev.properties`
- defaults de desarrollo
- Swagger ON
- logs más verbosos
- opcional SQL logging

### `application-prod.properties`
- configuración productiva base
- Swagger OFF (o restringido)
- logs más sobrios
- **sin secretos hardcodeados**

---

## 9.5. Ejemplo de placeholders (orientativo)

```properties
server.port=${SERVER_PORT:8080}

spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:app_db}
spring.datasource.username=${DB_USER:app_user}
spring.datasource.password=${DB_PASSWORD}

app.security.jwt.secret=${JWT_SECRET}
app.security.jwt.expiration-seconds=${JWT_EXPIRATION_SECONDS:7200}
app.security.jwt.issuer=${JWT_ISSUER:backend-v1}
```

### Nota importante
- `DB_PASSWORD` y `JWT_SECRET` **sin default real** en ambientes serios.

---

## 9.6. Archivo `.env.example` (muy recomendado)

Subir un `.env.example` con valores ficticios ayuda muchísimo.

### Ejemplo orientativo
```env
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080

DB_HOST=localhost
DB_PORT=5432
DB_NAME=colegio_v1
DB_USER=postgres
DB_PASSWORD=CAMBIAR_ESTE_VALOR

JWT_SECRET=CAMBIAR_POR_UN_SECRETO_LARGO_Y_ALEATORIO
JWT_EXPIRATION_SECONDS=7200
JWT_ISSUER=colegio-backend-v1

SWAGGER_ENABLED=true
LOG_LEVEL_ROOT=INFO
LOG_LEVEL_APP=DEBUG
```

✅ Profesional, simple y muy útil cuando cambies de máquina o chat.

---

## 10. Documentación de API con OpenAPI / Swagger

## 10.1. Objetivo real de Swagger en este proyecto

Swagger aquí no es solo para “verse bonito”. Te sirve para:

- probar endpoints rápido
- verificar contratos mientras implementas
- validar errores (`400/401/403/404/409/500/501`)
- probar paginación/filtros (`08`)
- depurar backend antes de conectar JavaFX
- dejar evidencia profesional del trabajo

✅ Es tu banco de pruebas inicial.

---

## 10.2. Herramienta recomendada V1

✅ `springdoc-openapi` (OpenAPI 3 + Swagger UI)

Motivos:
- estándar actual
- muy usado en Spring
- integración rápida para V1

---

## 10.3. Qué documentar sí o sí (global)

### Info general
- nombre del sistema/API
- versión `v1`
- descripción corta del backend
- servidor local/dev

### Seguridad
- `SecurityScheme` tipo **HTTP Bearer**
- formato JWT

### Convenciones
- contrato `ApiResponse<T>`
- contrato `ApiErrorResponse`
- convención de paginación (`page`, `size`, `sort`, `q`) según `08`

---

## 10.4. Qué documentar sí o sí (por endpoint)

Para cada endpoint relevante (`06`):
- propósito (summary/description)
- path params
- query params
- request body
- responses (`200/201/204/400/401/403/404/409/500/501` según aplique)
- ejemplos útiles (cuando aporten)

✅ No hace falta escribir novelas, pero sí claridad.

---

## 10.5. Documentar seguridad en OpenAPI

### Recomendación V1
- definir `SecurityScheme` Bearer JWT global
- aplicar `SecurityRequirement` global o por controller
- dejar `POST /api/v1/auth/login` sin security requirement

### Resultado esperado
En Swagger UI podrás:
- hacer login
- copiar token
- usar botón **Authorize**
- probar endpoints protegidos

---

## 10.6. Swagger y listados (`08`)

Endpoints de listado deben reflejar claramente:
- `page` (base 0)
- `size` (default/max)
- `sort` (`campo,direccion`, repetible)
- `q` (si aplica)
- filtros específicos (`estado`, `fechaDesde`, `fechaHasta`, etc.)

✅ Esto evita que después tú mismo olvides la convención al conectar JavaFX.

---

## 10.7. Swagger en `dev` vs `prod`

### Recomendación V1
- **dev:** habilitado
- **prod:** deshabilitado o restringido

Si lo dejas en `prod` (temporalmente):
- proteger acceso
- no exponer información innecesaria
- considerar IP allowlist/reverse proxy

---

## 11. Documentación complementaria (además de Swagger)

Swagger no reemplaza toda la documentación técnica.

## 11.1. README del backend (mínimo profesional)

Debe incluir:
- propósito del proyecto
- stack técnico (Java 21 + Spring Boot + PostgreSQL + etc.)
- requisitos previos
- cómo correr en local
- variables de entorno requeridas
- cómo correr con Docker / Compose
- URL de Swagger (dev)
- notas de seguridad básicas

---

## 11.2. `.env.example` / guía de variables

Ya recomendado en `9.6`, pero se recalca porque evita muchos errores de arranque.

✅ No subir secretos reales.

---

## 11.3. Colección de pruebas manuales (opcional útil)

Si quieres sumar profesionalismo después:
- Postman
- Bruno
- Insomnia

No es obligatorio si Swagger está bien, pero puede ayudarte cuando crezcan endpoints.

---

## 12. Despliegue mínimo V1 (visión general)

## 12.1. Qué significa “mínimo profesional” en este proyecto

No significa infraestructura enterprise.

Significa que el backend pueda correr:
- de forma reproducible,
- con configuración por ambiente,
- con secrets fuera del código,
- y con una ruta clara de arranque local/servidor.

---

## 12.2. Topologías mínimas razonables

### Opción A — Local de desarrollo
- backend Spring Boot local
- PostgreSQL local (instalado o Docker)

### Opción B — Docker Compose local (recomendada para practicar)
- backend container
- postgres container

### Opción C — Servidor simple (futuro)
- backend (jar o container)
- postgres local/gestionado
- reverse proxy (Nginx/Caddy)
- HTTPS

✅ Recomendación V1: **Opción B (Docker Compose local)**.

---

## 13. Docker mínimo (Backend + PostgreSQL)

## 13.1. Objetivo real de Docker en esta V1

Docker te sirve para:
- reproducibilidad
- despliegue mínimo serio
- practicar variables de entorno
- reducir “en mi máquina sí funciona”

✅ No se usa “por moda”, se usa por operatividad.

---

## 13.2. Decisión de runtime Java

Tú pediste Java 21 Temurin.

✅ Mantener **Eclipse Temurin 21** como base del contenedor runtime.

---

## 13.3. Estrategias válidas para Dockerfile (V1)

### Opción 1 — Compilar fuera de Docker y ejecutar jar (más simple)
- haces `mvn package` local
- Docker solo copia el `.jar` y ejecuta

### Opción 2 — Multi-stage build (más profesional)
- stage de build (Maven/Gradle)
- stage runtime (Temurin 21 JRE/JDK)

✅ Recomendación V1:
- empieza con Opción 1 si quieres velocidad,
- migra a Opción 2 cuando ya tengas backend estable.

---

## 13.4. Lineamientos mínimos del contenedor backend

- exponer puerto `8080` (o configurable)
- leer configuración por env vars
- usar `SPRING_PROFILES_ACTIVE`
- logs a stdout/stderr
- no hardcodear secrets

---

## 13.5. Dockerfile mínimo orientativo (simple)

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

✅ Suficiente para una V1 de práctica.

---

## 13.6. `docker-compose.yml` mínimo orientativo (backend + db)

```yaml
services:
  db:
    image: postgres:16
    container_name: colegio_db
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: .
    container_name: colegio_backend
    depends_on:
      - db
    environment:
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-dev}
      SERVER_PORT: 8080
      DB_HOST: db
      DB_PORT: 5432
      DB_NAME: ${DB_NAME}
      DB_USER: ${DB_USER}
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION_SECONDS: ${JWT_EXPIRATION_SECONDS:-7200}
      JWT_ISSUER: ${JWT_ISSUER:-colegio-backend-v1}
      SWAGGER_ENABLED: ${SWAGGER_ENABLED:-true}
    ports:
      - "8080:8080"

volumes:
  postgres_data:
```

### Nota importante
`depends_on` ayuda al orden de arranque, pero **no garantiza** que PostgreSQL ya esté listo para conexiones.

✅ V1 pragmática: permitir reintento/reinicio y seguir avanzando.

---

## 13.7. Healthcheck y readiness (mínimo)

Si agregas Actuator, puedes mejorar el arranque/diagnóstico.

### Recomendado V1
- `GET /actuator/health`

### En Compose (futuro inmediato)
Puedes agregar `healthcheck` cuando te sientas cómodo, pero no es obligatorio en la primera pasada.

---

## 14. Perfiles y configuración por ambiente

## 14.1. Perfiles mínimos

- `dev`
- `prod`

### Opcional (cuando empieces tests)
- `test`

---

## 14.2. Comportamiento sugerido por perfil

### `dev`
- Swagger ON
- logs más detallados
- SQL logging opcional (con cuidado)
- errores más diagnósticos en logs

### `prod`
- Swagger OFF (o restringido)
- logs sobrios
- debug OFF
- sin detalles internos en respuestas
- secrets solo por env vars

---

## 14.3. `ddl-auto` / estrategia de esquema (alineado a tu BD ya diseñada)

Como ya hiciste un trabajo fuerte de base de datos, hay que ser cuidadoso.

### Recomendación V1
- evitar `ddl-auto=create` fuera de pruebas rápidas
- preferir `validate` o `none` (según tu estrategia)
- usar SQL versionado/controlado como fuente de verdad operativa

✅ Esto protege tu modelo y evita sorpresas del ORM.

---

## 15. Seguridad operativa mínima (checklist de no meter la pata)

## 15.1. Checklist base

- [ ] No subir `.env` con secretos reales al repositorio
- [ ] No hardcodear `JWT_SECRET`
- [ ] No hardcodear `DB_PASSWORD`
- [ ] No dejar endpoints admin sin protección
- [ ] No dejar respuestas HTML de error de Spring Security
- [ ] No loggear contraseñas ni tokens completos
- [ ] No ejecutar producción con perfil `dev`
- [ ] No exponer Swagger en prod sin control (si decides dejarlo)
- [ ] No mostrar stack trace en respuestas HTTP

---

## 15.2. HTTPS (nota realista)

### Local
No es obligatorio para práctica inicial.

### Despliegue real
✅ Recomendado usar HTTPS mediante reverse proxy (Nginx o Caddy) delante del backend.

El backend puede escuchar HTTP interno; el proxy termina TLS.

---

## 15.3. CORS (si luego aparece frontend web)

JavaFX no sufre CORS como navegador, pero por diseño profesional conviene dejar estrategia clara.

### Recomendación V1
- configurar orígenes por ambiente
- permitir headers necesarios (`Authorization`, `Content-Type`)
- evitar `*` indiscriminado en producción si luego usas credenciales web

---

## 16. Logging mínimo y observabilidad básica (V1)

## 16.1. Qué sí hacer desde V1

- log de arranque (perfil, puerto, versión)
- log de errores internos
- log de autenticación fallida (sin revelar de más)
- log de eventos importantes (ej. login exitoso, si decides agregarlo)

---

## 16.2. Qué NO hacer

## 16.2. Configuración práctica recomendada

Para este proyecto didáctico, lo más útil es dejar ambos canales activos:
- consola para desarrollo y pruebas locales
- archivo rotativo para soporte operativo

Base sugerida:
- `requestId` en cada linea via MDC
- salida a consola para ver el flujo en tiempo real
- archivo `logs/uens-backend.log`
- rotacion por tamano y fecha para no crecer sin control

Variables útiles para no tocar codigo:
- `LOG_LEVEL_ROOT`
- `LOG_LEVEL_APP`
- `LOG_FILE_PATH`

Con esto, cuando aparezca un incidente real, el flujo de soporte es simple:
1. pedir `requestId` al cliente o leerlo del `ApiErrorResponse`
2. buscarlo en consola o en `logs/uens-backend.log`
3. correlacionarlo con `auditoria_evento` si aplica
4. clasificar si fue `VR`, `RN`, `AUTH`, `API` o `SYS`

---

## 16.3. Que NO hacer

- loggear password
- loggear token completo
- loggear datos sensibles por costumbre
- dejar logs ultra verbosos en `prod`

---

## 16.3. Actuator (opcional recomendado)

Spring Boot Actuator aporta mucho con poco esfuerzo.

### Recomendado V1
- `health`
- opcional `info`

### Precaución
No exponer demasiados endpoints de Actuator en producción sin control.

---

## 17. Estructura de paquetes sugerida (seguridad + ops) alineada con tu arquitectura

> Ajusta nombres según `04`, pero esta guía evita mezclar responsabilidades.

### Distribución pragmática sugerida
- `api.auth` → `AuthController`
- `application.auth` → caso de uso / servicio de login
- `infrastructure.security` → JWT, filtros, handlers, config de Spring Security
- `config` → OpenAPI, configuración transversal
- `infrastructure.persistence` → repositorios/ORM

### Nota DDD-lite
- seguridad técnica (JWT, filtros, handlers) → infraestructura
- reglas del caso de uso de autenticación → aplicación/dominio

✅ Buena separación sin ponerse académico extremo.

---

## 18. Convenciones de documentación/comentarios en código (seguridad)

Como quieres trazabilidad y comentarios útiles, aquí sí aporta documentar **decisiones**, no obviedades.

## 18.1. Ejemplo de comentario útil (estilo recomendado)

```java
/**
 * Caso de uso: Autenticar usuario del sistema.
 * Reglas aplicadas: AUTH-01, AUTH-05.
 * Descripción: Valida credenciales y estado del usuario; genera access token JWT.
 */
```

✅ Bien para servicios de aplicación / puntos críticos.
❌ No comentar cosas obvias del framework línea por línea.

---

## 18.2. Convenciones de nombres (alineadas a Spring “vieja confiable”)

- `...Controller`
- `...Service` / `...ApplicationService`
- `...Repository`
- `...RequestDto` / `...ResponseDto`
- `...Mapper` (manual)
- `...Exception`
- `SecurityConfig`
- `JwtTokenService`
- `JwtAuthenticationFilter`

### Sobre interfaces (`IAlgo`)
En Java/Spring moderno es común **no** usar prefijo `I`.

✅ Recomendación V1: seguir convención estándar Spring/Java (sin `I`), salvo que el equipo del proyecto defina otra cosa.

---

## 19. Reportes, placeholders y documentación `501` (alineado con tu decisión actual)

Tú ya decidiste que la generación final de reportes se puede trabajar más del lado JavaFX (al menos en esta etapa), pero eso **no rompe** el backend.

## 19.1. Estrategia V1 válida

Puedes dejar:
- endpoint placeholder `501`
- endpoint de metadatos/listado de solicitudes de reporte
- endpoint de estado de solicitud (cola simple, ver `10`)

---

## 19.2. Cómo documentar placeholders en Swagger

Si un endpoint aún no está implementado:
- marcarlo explícitamente como **En construcción**
- documentar respuesta `501`
- usar código estandarizado (ej. `API-99-ENDPOINT_EN_CONSTRUCCION` o el que hayas fijado en `05/06`)

✅ Profesional y honesto. Mejor que “dejarlo roto”.

---

## 20. Ruta sugerida de implementación (orden de trabajo)

Para no cansarte mentalmente y avanzar con feedback rápido:

1. Configurar OpenAPI/Swagger
2. Configurar `SecurityConfig` base (rutas públicas/protegidas)
3. Implementar `POST /api/v1/auth/login`
4. Implementar `BCryptPasswordEncoder`
5. Implementar `JwtTokenService`
6. Implementar `JwtAuthenticationFilter`
7. Implementar handlers JSON de `401/403`
8. Probar auth en Swagger
9. Proteger 1–2 endpoints reales (ej. `GET /estudiantes`)
10. Documentar paginación/filtros según `08`
11. Agregar `Dockerfile`
12. Agregar `docker-compose.yml`
13. Validar arranque `backend + db`
14. Revisar perfil `prod` (Swagger OFF/restringido)

✅ Este orden te da resultados visibles desde temprano.

---

## 21. Riesgos comunes en V1 y cómo evitarlos

## 21.1. JWT con demasiada información

Error común: meter demasiados claims.

✅ Solución: token pequeño con claims esenciales (`sub`, `rol`, `exp`, etc.).

---

## 21.2. Respuestas HTML de Spring Security

Muy común si no configuras handlers custom.

✅ Solución: `AuthenticationEntryPoint` + `AccessDeniedHandler` que devuelvan `ApiErrorResponse`.

---

## 21.3. Seguridad dispersa en controllers con `if` improvisados

✅ Solución: usar Spring Security + anotaciones + servicios. Mantener controllers delgados.

---

## 21.4. Secretos en repo “por mientras”

✅ Solución: desde V1 usar env vars + `.env.example`. Hábitat profesional desde el inicio.

---

## 21.5. Swagger desactualizado respecto al código

✅ Solución: documentar junto con implementación, no al final del proyecto.

---

## 21.6. Confiar solo en UI para permisos

✅ Solución: el backend debe rechazar requests no autorizados aunque la UI muestre/oculte botones.

---

## 22. Checklist de implementación derivado (V1)

## 22.1. Seguridad
- [ ] Agregar `spring-boot-starter-security`
- [ ] Implementar `POST /api/v1/auth/login`
- [ ] Configurar `PasswordEncoder` (`BCrypt`)
- [ ] Implementar servicio JWT (generar/validar)
- [ ] Implementar filtro JWT
- [ ] Configurar rutas públicas/protegidas
- [ ] Configurar `AuthenticationEntryPoint` JSON (`401`)
- [ ] Configurar `AccessDeniedHandler` JSON (`403`)
- [ ] Definir permisos por rol en endpoints críticos

## 22.2. Documentación
- [ ] Agregar `springdoc-openapi`
- [ ] Configurar `SecurityScheme` Bearer JWT
- [ ] Documentar `auth/login`
- [ ] Documentar respuestas de error (`400/401/403/404/409/500/501`)
- [ ] Documentar query params de listados según `08`

## 22.3. Configuración / despliegue
- [ ] Definir variables de entorno mínimas
- [ ] Crear `application.properties`, `application-dev.properties`, `application-prod.properties`
- [ ] Crear `.env.example` (sin secretos reales)
- [ ] Crear `Dockerfile`
- [ ] Crear `docker-compose.yml` (backend + postgres)
- [ ] Validar arranque local con Compose
- [ ] Verificar perfil `prod` con Swagger OFF/restringido

## 22.4. Operación mínima
- [ ] Habilitar `health` (Actuator opcional recomendado)
- [ ] Revisar logs sin datos sensibles
- [ ] Probar `401/403` manualmente en Swagger/Postman

---

## 23. Relación con otros documentos del backend V1

## 23.1. Relación con `05` (contrato API)

Este documento depende de `05` para que:
- `401/403` usen `ApiErrorResponse`
- auth y errores mantengan consistencia transversal

## 23.2. Relación con `06` (endpoints/casos de uso)

Aquí se define **cómo proteger** y **cómo documentar** lo que `06` enumera.

## 23.3. Relación con `07` (validaciones/reglas/excepciones)

Aquí se aterrizan los códigos `AUTH-*` y su integración con la capa de excepciones.

## 23.4. Relación con `08` (paginación/filtros)

Swagger y seguridad deben respetar la gramática de consultas definida en `08`.

## 23.5. Relación con `10` (cola simple para reportes)

La cola simple DB queue puede requerir:
- endpoints protegidos
- permisos por rol
- documentación de estados
- placeholders `501`/operativos en Swagger

✅ `09` deja la base transversal para eso.

---

## 24. Cierre del documento

## Addendum 2026-03-03: refresh token y storage documental

Las secciones antiguas que marcaban "sin refresh token en V1" quedan superadas por el estado actual del codigo.

Estado vigente:
- `accessToken` JWT corto
- `refreshToken` opaco con store desacoplado
- rotacion de refresh token en `POST /api/v1/auth/refresh`
- revocacion best-effort en `POST /api/v1/auth/logout`

Propiedades activas:
- `app.security.jwt.expiration-seconds`
- `app.security.refresh-token.enabled`
- `app.security.refresh-token.expiration-seconds`
- `app.security.refresh-token.token-bytes-length`

Tambien queda formalizado un repositorio documental desacoplado para reportes:
- `DocumentStoragePort`
- `LocalFilesystemDocumentStorageAdapter`

Documento complementario:
- `21_backend_v_1_sesion_renovable_y_repositorio_documental_local.md`

Con este documento queda definida una base V1 **muy profesional y operativa** para que practiques backend como se hace en proyectos reales:

- seguridad realista (JWT + roles + errores consistentes),
- documentación útil (Swagger/OpenAPI),
- despliegue mínimo reproducible (env vars + Docker),
- y una ruta clara de implementación sin sobreingeniería.

Esto encaja perfecto con tu enfoque actual:
- backend agnóstico del frontend,
- dominio/control de reglas en backend,
- y JavaFX consumiendo una API limpia y bien documentada.

✅ Con `09` ya tienes cerrado el bloque transversal de operación del backend V1.


