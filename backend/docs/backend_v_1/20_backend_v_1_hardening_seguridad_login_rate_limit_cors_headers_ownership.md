# 20_backend_v_1_hardening_seguridad_login_rate_limit_cors_headers_ownership

## 1) Objetivo
Este documento cierra el endurecimiento básico del backend UENS en cuatro frentes:

1. protección del login contra abuso
2. CORS y headers HTTP defensivos
3. ownership estricto sobre reportes y defensa adicional sobre auditoría
4. trazabilidad operativa útil para mantenimiento real

El objetivo no es convertir el sistema en una plataforma enterprise total, sino dejarlo en un punto didáctico serio, coherente y mantenible.

---

## 2) Alcance realista del endurecimiento
Se implemento una postura de seguridad razonable para un monolito Spring Boot de una sola instancia:

- JWT stateless para autenticación
- control de abuso sobre `POST /api/v1/auth/login`
- control de acceso por rol y por ownership
- headers defensivos para clientes web
- CORS declarativo y configurable
- logs a consola y archivo con `requestId`

No se implemento:

- WAF
- rate limiting distribuido con Redis
- IDS/IPS
- MFA
- rotacion automatica de secretos
- SIEM

Eso es intencional. Para el contexto del proyecto, lo implementado agrega mucho valor sin desbordar el alcance.

---

## 3) Patrones usados

### 3.1 Facade: usuario autenticado actual
Archivo: `src/main/java/com/marcosmoreiradev/uensbackend/security/user/CurrentAuthenticatedUserService.java`

Rol:
- esconder `SecurityContextHolder` de la capa application
- exponer solo lo que importa al negocio: `userId`, `login`, `role`
- permitir reglas de ownership y permisos sin acoplar servicios a Spring Security

Beneficio:
- menos duplicacion
- menos dependencias de framework en application
- más facil de testear

### 3.2 Policy Service: protección de login
Archivo: `src/main/java/com/marcosmoreiradev/uensbackend/modules/auth/application/support/LoginProtectionService.java`

Rol:
- centralizar reglas temporales de abuso
- aplicar rate limit por IP
- aplicar lockout temporal por identidad

Beneficio:
- la política no queda repartida entre controller, security config y auth service
- los tests pueden enfocarse en la política sin bootstrapping web

### 3.3 Factory: CORS
Archivo: `src/main/java/com/marcosmoreiradev/uensbackend/security/config/ApiCorsConfigurationSourceFactory.java`

Rol:
- construir una única política CORS declarativa
- dejar la configuración lista para `SecurityConfig`

Beneficio:
- `SecurityConfig` queda más limpio
- la política CORS se puede testear sin arrancar todo Spring MVC

### 3.4 Filter: headers defensivos
Archivo: `src/main/java/com/marcosmoreiradev/uensbackend/security/filter/SecurityResponseHeadersFilter.java`

Rol:
- escribir headers transversales una sola vez
- evitar repetir encabezados en controllers

Beneficio:
- comportamiento consistente
- testing simple con `MockHttpServletRequest/Response`

---

## 4) Protección de login

## 4.1 Que problema resuelve
Un backend real no debe aceptar intentos ilimitados de autenticación porque eso permite:

- fuerza bruta de credenciales
- password spraying
- abuso automatizado desde una sola IP
- saturacion del endpoint de login

Por eso se aplican dos controles distintos:

1. rate limit por IP
2. bloqueo temporal por identidad después de varios fallos

Los dos son necesarios porque atacan escenarios distintos.

---

## 4.2 Diferencia entre rate limit y lockout

### A) Rate limit por IP
Limita cuantas solicitudes de login puede emitir una misma IP en una ventana corta.

Ejemplo:
- una IP puede intentar hasta `20` logins en `300` segundos
- el intento `21` recibe error temporal

Protege contra:
- bots sencillos
- abuso explosivo
- saturacion del endpoint

### B) Lockout temporal por identidad
Bloquea temporalmente un login cuando acumula demasiados fallos dentro de una ventana.

Ejemplo:
- `admin` falla `5` veces en `900` segundos
- el login queda bloqueado `900` segundos

Protege contra:
- fuerza bruta focalizada sobre una cuenta
- intentos repetidos sobre usuarios sensibles

---

## 4.3 Implementación concreta

### Flujo actual
1. `AuthController` recibe `POST /api/v1/auth/login`.
2. `ClientIpResolver` deriva la IP remota desde `HttpServletRequest`.
3. `AuthApplicationService` normaliza el login.
4. `LoginProtectionService.assertLoginAllowed(...)` valida rate limit y lockout antes de tocar BD.
5. Si las credenciales fallan, `AuthApplicationService` registra el fallo.
6. Si el login es exitoso, limpia el historial de fallos de esa identidad.

Archivos implicados:
- `modules/auth/api/AuthController.java`
- `common/api/útil/ClientIpResolver.java`
- `modules/auth/application/AuthApplicationService.java`
- `modules/auth/application/support/LoginProtectionService.java`

---

## 4.4 Configuración
Propiedades en `application.properties`:

```properties
app.security.login-protection.enabled=true
app.security.login-protection.max-failed-attempts=5
app.security.login-protection.failure-window-seconds=900
app.security.login-protection.lock-duration-seconds=900
app.security.login-protection.max-requests-per-ip-window=20
app.security.login-protection.ip-window-seconds=300
```

Significado:

| Propiedad | Rol |
| --- | --- |
| `enabled` | activa o apaga toda la protección |
| `max-failed-attempts` | umbral de fallos por identidad |
| `failure-window-seconds` | ventana donde se cuentan esos fallos |
| `lock-duration-seconds` | tiempo de bloqueo temporal |
| `max-requests-per-ip-window` | máximo de requests de login por IP |
| `ip-window-seconds` | ventana del rate limit por IP |

---

## 4.5 Errores nuevos
Se agregaron dos códigos trazables:

| Código | HTTP | Significado |
| --- | --- | --- |
| `AUTH-06-LOGIN_TEMPORALMENTE_BLOQUEADO` | `429` | la identidad supero el umbral de fallos |
| `AUTH-07-RATE_LIMIT_LOGIN_EXCEDIDO` | `429` | la IP excedio el límite temporal de requests |

Respuesta tipo:

```json
{
  "errorCode": "AUTH-06-LOGIN_TEMPORALMENTE_BLOQUEADO",
  "message": "Demasiados intentos fallidos. Espere antes de reintentar.",
  "details": {
    "retryAfterSeconds": 120
  },
  "path": "/api/v1/auth/login",
  "requestId": "..."
}
```

Notas:
- se usa `429 Too Many Requests` porque el problema es temporal y de abuso, no de formato
- `details.retryAfterSeconds` ayuda al frontend y a soporte

---

## 4.6 Limitaciones conocidas
La implementación es **in-memory**.

Eso significa:
- funciona bien en una sola instancia
- se pierde al reiniciar la app
- no se comparte entre multiples nodos

En un backend distribuido real, el estado deberia ir a Redis o una capa equivalente.

---

## 5) CORS

## 5.1 Que es y por que importa
CORS es una política del navegador, no del desktop JavaFX.

Eso implica:
- el desktop actual no depende de CORS
- un frontend web futuro si dependeria

Por eso conviene dejarlo configurado ahora en el backend, aunque el cliente principal sea JavaFX.

---

## 5.2 Implementación concreta
Se usa una fabrica dedicada:

- `security/config/ApiCorsConfigurationSourceFactory.java`

Esa fabrica construye el `CorsConfigurationSource` que consume `SecurityConfig`.

Configuración principal:
- rutas `/api/**`
- rutas `/v3/api-docs/**`

---

## 5.3 Propiedades
```properties
app.security.cors.enabled=true
app.security.cors.allowed-origins=
app.security.cors.allowed-methods=GET,POST,PUT,PATCH,DELETE,OPTIONS
app.security.cors.allowed-headers=Authorization,Content-Type,Accept,X-Request-Id
app.security.cors.exposed-headers=X-Request-Id,Content-Disposition
app.security.cors.allow-credentials=false
app.security.cors.max-age-seconds=1800
```

Política por defecto:
- sin origenes cruzados permitidos mientras `allowed-origins` este vacio
- métodos y headers ya preparados para un cliente web futuro

Esto es importante:
- `enabled=true` no significa "todo permitido"
- si `allowed-origins` esta vacio, el navegador sigue sin tener permiso cross-origin

---

## 5.4 Decision de seguridad
No se habilitan origenes comodin por defecto.

Evitar:
- `*`
- `allow-credentials=true` junto con configuración laxa

Si en el futuro hay frontend web:
- declarar solo los origenes concretos que correspondan
- por ejemplo `http://localhost:5173` en desarrollo

---

## 6) Headers defensivos

## 6.1 Headers aplicados
Filtro:
- `security/filter/SecurityResponseHeadersFilter.java`

Headers escritos:

| Header | Valor por defecto | Rol |
| --- | --- | --- |
| `X-Content-Type-Options` | `nosniff` | evita sniffing de tipos |
| `X-Frame-Options` | `DENY` | evita framing/clickjacking en la API |
| `Referrer-Policy` | `no-referrer` | reduce fuga de información en navegación web |
| `Permissions-Policy` | política restrictiva | niega capacidades del navegador no usadas |
| `Strict-Transport-Security` | opcional | fuerza HTTPS solo cuando se habilita y la request es segura |

---

## 6.2 Configuración
```properties
app.security.headers.hsts-enabled=false
app.security.headers.hsts-max-age-seconds=31536000
app.security.headers.hsts-include-subdomains=true
app.security.headers.referrer-policy=no-referrer
app.security.headers.permissions-policy=camera=(), microphone=(), geolocation=(), payment=(), usb=()
```

Decision importante:
- HSTS queda apagado por defecto
- debe activarse cuando el backend ya este expuesto detras de HTTPS real

Eso evita publicar una política incorrecta en entornos locales HTTP.

---

## 6.3 Excepción deliberada: H2 en dev
La consola H2 usa una cadena de seguridad separada con `frameOptions.sameOrigin()`.

Eso es intencional para no romper la herramienta de desarrollo.

No afecta la API principal:
- la cadena principal sigue usando headers defensivos estrictos

---

## 7) Ownership de reportes

## 7.1 Regla funcional

### ADMIN
- puede ver todas las solicitudes
- puede consultar detalle
- puede revisar resultado
- puede descargar cualquier archivo generado
- puede reintentar solicitudes con error

### SECRETARIA
- puede crear solicitudes
- solo puede listar sus propias solicitudes
- solo puede ver detalle/estado/resultado/archivo de sus propias solicitudes
- no puede reintentar

---

## 7.2 Implementación concreta
Archivo principal:
- `modules/reporte/application/ReporteSolicitudQueryService.java`

Reglas aplicadas:
- en `listar(...)` se agrega una restriccion por `solicitadoPorUsuario` cuando el usuario no es ADMIN
- en `obtenerDetalle`, `obtenerEstado`, `obtenerResultado` y `obtenerArchivo` se valida acceso antes de exponer la solicitud

Soporte de identidad:
- `security/user/CurrentAuthenticatedUserService.java`

---

## 7.3 Comportamiento frente a acceso indebido
Si una `SECRETARIA` intenta abrir una solicitud ajena, el backend responde como si no existiera:

- `ResourceNotFoundException`
- mensaje: `Solicitud de reporte no encontrada.`

Motivo:
- no revelar que existe un recurso válido perteneciente a otra cuenta

Eso es una medida común para recursos con ownership.

---

## 7.4 Descarga de archivo y ownership
La descarga binaria no es una excepción.

Antes de tocar el archivo físico:
1. se valida ownership
2. se valida estado `COMPLETADA`
3. se valida ruta dentro del directorio permitido
4. se sanea nombre de archivo y MIME type

Así se cubren dos capas:
- autorización
- integridad de salida

---

## 8) Auditoría: acceso estricto y defensa en profundidad

## 8.1 Regla funcional
La auditoría es solo ADMIN.

Endpoints:
- `GET /api/v1/auditoria/eventos`
- `POST /api/v1/auditoria/reportes/solicitudes`

---

## 8.2 Por que no se usa ownership por usuario aqui
Porque auditoría operativa es un recurso global y sensible.

No tiene sentido funcional que:
- cada usuario vea "su pedazo" de auditoría

La política correcta es:
- acceso global solo para ADMIN

---

## 8.3 Defensa adicional
La protección no queda solo en `@PreAuthorize`.

También se refuerza en application:
- `modules/auditoria/application/AuditoriaQueryService.java`
- `modules/auditoria/application/AuditoriaReporteService.java`

Ambos servicios exigen ADMIN mediante `CurrentAuthenticatedUserService.ensureAdmin(...)`.

Esto es defensa en profundidad:
- controller protege el endpoint HTTP
- application protege el caso de uso si algun día otro adaptador lo invoca

---

## 9) Logging y trazabilidad para mantenimiento real

## 9.1 Que ya existe
El backend ya registra:
- `requestId`
- logs a consola
- logs a archivo rotativo
- auditoría operativa en BD

Configuración actual:

```properties
logging.level.root=${LOG_LEVEL_ROOT:INFO}
logging.level.com.marcosmoreiradev.uensbackend=${LOG_LEVEL_APP:INFO}
logging.pattern.level=%5p [req:%X{requestId:-na}]
logging.file.name=${LOG_FILE_PATH:logs/uens-backend.log}
```

Resultado práctico:
- un error que llega al cliente incluye `requestId`
- el mismo `requestId` aparece en logs
- y puede aparecer en `auditoria_evento` cuando aplica

---

## 9.2 Como trabajaria soporte/mantenimiento
Flujo recomendado:

1. tomar `requestId` desde la respuesta de error del cliente
2. buscarlo en `logs/uens-backend.log`
3. correlacionar con auditoría si el caso fue operativo
4. revisar `errorCode`
5. decidir si era:
 - validación
 - negocio
 - autenticación/autorización
 - sistema/infrastructura

En el caso concreto de login:
- revisar si fue `AUTH-01`, `AUTH-06` o `AUTH-07`
- distinguir rápido entre credenciales malas y protección antiabuso

---

## 10) Matriz corta de configuración

| Tema | Propiedades |
| --- | --- |
| login protection | `app.security.login-protection.*` |
| CORS | `app.security.cors.*` |
| headers | `app.security.headers.*` |
| logs | `logging.*` |

---

## 11) Pruebas automatizadas agregadas

### Login protection
- `modules/auth/application/support/LoginProtectionServiceTest`
- `modules/auth/application/AuthApplicationServiceTest`
- `modules/auth/api/AuthControllerTest`

### CORS y headers
- `security/config/ApiCorsConfigurationSourceFactoryTest`
- `security/filter/SecurityResponseHeadersFilterTest`

### Ownership y auditoría
- `modules/reporte/application/ReporteSolicitudQueryServiceTest`
- `modules/auditoria/application/AuditoriaQueryServiceTest`
- `modules/auditoria/application/AuditoriaReporteServiceTest`

---

## 12) Implicaciones para el frontend

### Login
El frontend debe contemplar estos errores:
- `AUTH-01-CREDENCIALES_INVALIDAS`
- `AUTH-06-LOGIN_TEMPORALMENTE_BLOQUEADO`
- `AUTH-07-RATE_LIMIT_LOGIN_EXCEDIDO`

Comportamiento esperado:
- no borrar el login escrito
- mostrar el `message` del backend sin inventar otra semántica
- si viene `retryAfterSeconds`, usarlo solo como ayuda visual, no como fuente única de verdad

### Reportes
Una `SECRETARIA` solo vera sus propias solicitudes.

Implicacion:
- si el desktop recibe `404` al abrir una solicitud ajena, no debe asumir "backend roto"
- puede ser una denegacion por ownership intencional

### Auditoría
La UI de auditoría debe seguir siendo solo ADMIN.

---

## 13) Limitaciones y siguientes pasos realistas

### Siguientes pasos con mejor retorno
1. mover rate limit/lockout a Redis si el backend escala a varias instancias
2. endurecer secretos y despliegue con proxy HTTPS real
3. agregar pruebas de integración de seguridad sobre `401`, `403`, `429`
4. agregar política de password más fuerte y rotacion de credenciales administrativas
5. agregar limpieza programada de archivos de reporte expirados

### Cosas que no hacen falta todavía
- analítica avanzada
- motores de riesgo complejos
- IAM corporativo

Para este dominio y este alcance, eso seria premature optimization.

---

## 14) Resumen ejecutivo
El backend ya no solo autentica y autoriza:

- ahora resiste mejor abuso sobre login
- declara una política CORS mantenible
- emite headers defensivos coherentes
- restringe reportes por ownership real
- protege auditoría con defensa adicional en application
- mantiene trazabilidad útil para soporte con logs y `requestId`

Ese conjunto ya se parece a un backend pequeno pero serio, y sigue siendo entendible para fines educativos.


