# 21_backend_v_1_sesion_renovable_y_repositorio_documental_local

- Version: 1.0
- Estado: Vigente
- Ámbito: backend V1 y su integración con desktop
- Relacionado con:
  - `09_backend_v_1_seguridad_documentacion_y_despliegue_minimo.md`
  - `10_backend_v_1_reporte_solicitudes_cola_simple_db_queue.md`
  - `15_backend_v_1_reportes_generacion_archivos_excel_pdf_word.md`
  - `19_backend_v_1_contexto_integracion_y_diseno_frontend.md`
  - `20_backend_v_1_hardening_seguridad_login_rate_limit_cors_headers_ownership.md`

---

## 1. Propósito

Este documento explica dos evoluciones importantes del sistema:

1. Sesión renovable con `refreshToken` para evitar cierres abruptos en desktop.
2. Repositorio documental desacoplado para reportes, comenzando con una simulacion local en filesystem.

Tambien deja clara la lectura arquitectonica correcta:
- esto sigue siendo un monolito modular
- no es un microservicio
- pero ya usa patrones comunes del mundo corporativo

---

## 2. Que cambio realmente

### 2.1 Autenticación

El backend ya no entrega solo un JWT de acceso.

Ahora el contrato de autenticación devuelve:
- `accessToken`
- `refreshToken`
- `tokenType`
- `expiresInSeconds`
- `refreshExpiresInSeconds`
- `usuario`

Endpoints relacionados:
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /api/v1/auth/me`

### 2.2 Reportes

La generación y descarga de archivos ya no depende de que la capa application manipule rutas fisicas acopladas.

Ahora existe un puerto:
- `DocumentStoragePort`

Y un adapter local:
- `LocalFilesystemDocumentStorageAdapter`

Eso permite que la capa application piense en "guardar/cargar/eliminar documentos" sin conocer si detras hay:
- filesystem local
- MinIO
- Amazon S3
- Azure Blob Storage
- Google Cloud Storage

---

## 3. Esto no es un microservicio

Aunque el sistema ya tiene:
- JWT
- cola de reportes
- auditoria
- módulos separados
- puertos y adapters
- contratos de error y trazabilidad

...sigue siendo un monolito modular porque:

1. se despliega como una sola aplicacion Spring Boot
2. comparte un solo runtime principal
3. comparte una misma base de datos operacional
4. los módulos se separan por paquetes y capas, no por despliegues independientes

Conclusion:
- la complejidad ya se siente "corporativa"
- pero la topologia sigue siendo monolitica
- eso es bueno para un proyecto didáctico serio

---

## 4. Patrones de diseño que ya se estan usando

### 4.1 Inyeccion de dependencias

Si, esto es justamente inyeccion de dependencias.

Spring resuelve interfaces y clases concretas en tiempo de arranque, por ejemplo:
- `RefreshTokenStore` -> `InMemoryRefreshTokenStore`
- `DocumentStoragePort` -> `LocalFilesystemDocumentStorageAdapter`

Beneficio:
- la capa de negocio no crea dependencias concretas con `new`
- las implementaciones se pueden sustituir sin reescribir el caso de uso

### 4.2 Ports and Adapters

Este es el patron principal usado en las dos piezas nuevas.

Puerto:
- define lo que la aplicacion necesita

Adapter:
- resuelve como se hace en una tecnologia concreta

Ejemplos:
- `RefreshTokenStore`
- `DocumentStoragePort`

### 4.3 Adapter

Las implementaciones concretas traducen el contrato abstracto al proveedor real.

Ejemplos:
- `InMemoryRefreshTokenStore`
- `LocalFilesystemDocumentStorageAdapter`

### 4.4 Facade

`CurrentAuthenticatedUserService` actua como una fachada pequena sobre el contexto de seguridad.

### 4.5 Strategy

En reportes ya existia una linea de `Strategy` con selector por tipo de reporte y por formato de salida.

---

## 5. Sesión renovable: como funciona

1. El usuario inicia sesión con `login`.
2. El backend devuelve `accessToken` y `refreshToken`.
3. El desktop guarda ambos en `SessionState`, junto con sus expiraciones absolutas.
4. Antes de un request autenticado, `ApiClient` verifica si el `accessToken` esta por vencer.
5. Si esta cerca de expirar y el `refreshToken` sigue vigente, llama `POST /api/v1/auth/refresh`.
6. El backend rota el refresh token y emite un nuevo JWT.
7. El desktop actualiza su estado y continua sin interrumpir a la persona usuaria.

Distincion importante:
- `accessToken`: JWT corto y stateless
- `refreshToken`: token opaco, revocable y rotativo

---

## 6. Store actual de refresh token

El store actual es:
- `InMemoryRefreshTokenStore`

Esto es suficiente para:
- desarrollo local
- una sola instancia del backend
- un proyecto didáctico o demo seria

Limites reales:
- si el proceso reinicia, se pierden refresh tokens
- si algun día levantas multiples instancias, cada una tendria su memoria separada

Ruta natural de evolucion:
- Redis
- tabla dedicada en PostgreSQL

---

## 7. Repositorio documental local

### 7.1 Problema que resuelve

Antes, la capa application sabia demasiado de rutas y archivos fisicos.

Eso es debil porque:
- acopla negocio a filesystem
- dificulta migracion a otro proveedor
- mezcla reglas de negocio con preocupaciones de almacenamiento

### 7.2 Solucion aplicada

Se introdujo este puerto:
- `DocumentStoragePort`

Con operaciones de alto nivel:
- `store(...)`
- `load(...)`
- `delete(...)`

La aplicacion ya no piensa en "carpeta exacta y ruta completa".
Piensa en "documento almacenado" y "clave documental".

### 7.3 Implementacion local actual

Adapter activo:
- `LocalFilesystemDocumentStorageAdapter`

Comportamiento:
- usa `app.report.output.dir` como raiz
- crea subespacios logicos, por ejemplo `reportes/`
- sanitiza nombres
- impide escapar del directorio base
- devuelve un `documentKey` relativo y seguro

---

## 8. Por que esta simulacion local si es valida

Para un proyecto didáctico, filesystem local desacoplado es una decision muy buena porque:

1. se entiende facil
2. se prueba facil
3. no depende de nube ni credenciales de terceros
4. deja lista la abstraccion para cambiar de proveedor después

Si quieres una simulacion más cercana a mercado sin salirte del desarrollo local, la evolucion natural es MinIO en Docker.

---

## 9. Como migraria esto a un proveedor real

Sin cambiar la capa application, podrias agregar:
- `MinioDocumentStorageAdapter`
- `S3DocumentStorageAdapter`
- `AzureBlobDocumentStorageAdapter`
- `GcsDocumentStorageAdapter`

Cada adapter implementaria `DocumentStoragePort`.

La capa application seguiria igual:
- `ReporteFileGenerationService`
- `ReporteSolicitudQueryService`

---

## 10. Seguridad y operación alrededor de documentos

La arquitectura nueva ya deja estas buenas prácticas:

1. Ownership de reportes:
   - `ADMIN` puede ver todo
   - `SECRETARIA` solo sus propias solicitudes y archivos

2. Descarga binaria con headers correctos:
   - `Content-Disposition`
   - `Cache-Control: no-store`
   - `X-Content-Type-Options: nosniff`

3. Saneamiento de nombre de archivo y validación de MIME.

4. Bloqueo de rutas fuera del directorio base.

---

## 11. Que faltaria para un backend aun más robusto

Sin salirte de expectativas realistas, los siguientes pasos con mejor retorno serian:

1. mover `RefreshTokenStore` a Redis o BD si algun día hay más de una instancia
2. agregar retención y limpieza automatica de archivos de reporte
3. persistir metadata documental más rica si el negocio lo exigiera
4. agregar pruebas HTTP de integración para `401`, `403` y `refresh`

---

## 12. Resumen ejecutivo

Lo implementado ahora ya se parece a una base corporativa razonable:

- autenticación con sesión renovable
- puerto y adapter para refresh token store
- puerto y adapter para repositorio documental
- ownership y seguridad en descargas
- cliente desktop con auto refresh encapsulado

Traduccion práctica:
- mejor experiencia de sesión
- mejor desacople de infraestructura
- base realista para crecer sin romper lo ya construido


