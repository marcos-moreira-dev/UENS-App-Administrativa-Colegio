# 19_backend_v_1_contexto_integracion_y_diseno_frontend

- Versión: 1.0
- Estado: Base de contexto para iniciar frontend
- Ámbito: Integración frontend <-> backend V1
- Fecha de corte: 2026-02-27

---

## 1. Propósito

Este documento aterriza el contexto que el frontend necesita antes de empezar diseño visual, arquitectura de pantallas y consumo de API.

Su objetivo no es repetir toda la documentación técnica del backend, sino responder estas preguntas:

1. Que módulos existen realmente y como se navegan.
2. Que puede hacer cada rol.
3. Que contratos de respuesta ya son estables.
4. Que flujos conviene diseñar primero.
5. Que restricciones técnicas debes respetar para no diseñar una UI incompatible.
6. Que puntos del backend ya se consideran suficientemente confiables para avanzar.

---

## 2. Alcance funcional útil para frontend

El backend V1 ya expone una base funcional consistente para estas areas:

1. autenticación
2. dashboard
3. representantes legales
4. docentes
5. secciones
6. asignaturas
7. estudiantes
8. clases
9. calificaciones
10. reportes asíncronos
11. auditoría operativa

Eso significa que el frontend ya puede plantearse como un sistema administrativo escolar completo, no solo como CRUDs sueltos.

---

## 3. Estado de confianza del backend

Se realizo validación operativa sobre una instancia limpia del backend y el resultado práctico fue:

1. endpoints base funcionando
2. autenticación por JWT funcionando
3. permisos `ADMIN` y `SECRETARIA` funcionando en los flujos probados
4. CRUDs principales funcionando
5. reportes genericos funcionando
6. reporte de auditoría funcionando
7. descarga de archivos funcionando

Resultado de verificacion manual automatizada:

- 46 de 47 endpoints reales cubiertos de forma operativa
- el endpoint no cubierto fue `POST /api/v1/reportes/solicitudes/{solicitudId}/reintentar`
- razón: requiere una solicitud preexistente en estado `ERROR`

Conclusion de frontend:

- ya puedes avanzar con confianza en el diseño del frontend para el flujo normal
- no debes asumir aún una UX central basada en `reintentar reporte fallido` hasta validarlo en un escenario forzado

---

## 4. Convenciones de contrato que el frontend debe asumir como estables

## 4.1 Respuesta de exito

El backend responde con un envoltorio uniforme:

```json
{
  "ok": true,
  "message": "Operación exitosa.",
  "data": {},
  "timestamp": "2026-02-27T23:00:00Z"
}
```

Implicacion de frontend:

1. no consumas directamente el cuerpo como si fuera el DTO final
2. extrae siempre `data`
3. usa `message` como feedback rápido o fallback

## 4.2 Respuesta de error

```json
{
  "ok": false,
  "errorCode": "AUTH-04-SIN_PERMISOS",
  "message": "Solo ADMIN puede solicitar reportes de auditoria.",
  "path": "/api/v1/auditoria/reportes/solicitudes",
  "timestamp": "2026-02-27T23:08:13Z",
  "requestId": "318c22e2"
}
```

Implicacion de frontend:

1. muestra `message` al usuario
2. conserva `errorCode` para logging técnico y reglas UI
3. no ocultes `requestId` en consola de desarrollo; sirve para soporte

## 4.3 Paginación

Los listados paginados usan `PageResponseDto` con esta forma:

```json
{
  "items": [],
  "page": 0,
  "size": 10,
  "totalElements": 49,
  "totalPages": 5,
  "numberOfElements": 10,
  "first": true,
  "last": false,
  "sort": "id,asc"
}
```

Decision importante:

- en este backend la colección paginada canonica es `items`
- no disenes tablas esperando `content`

## 4.4 Autorización

El backend usa JWT Bearer.

Header:

```http
Authorization: Bearer <token>
```

Implicacion de frontend:

1. guarda el token en una capa central de sesión
2. intercepta `401` para logout o renovacion de flujo de login
3. intercepta `403` para mostrar "sin permisos" y no "error interno"

---

## 5. Roles y su impacto en navegación

En V1 hay dos roles funcionales:

1. `ADMIN`
2. `SECRETARIA`

No son roles decorativos; cambian lo que la UI puede mostrar y ejecutar.

## 5.1 `ADMIN`

Puede:

1. ver todo lo que ve `SECRETARIA`
2. cambiar estados sensibles
3. crear y actualizar catalogos académicos restringidos
4. acceder a auditoría
5. solicitar reporte de auditoría
6. reintentar reportes fallidos

## 5.2 `SECRETARIA`

Puede:

1. autenticarse
2. ver dashboard
3. administrar representantes
4. administrar docentes en parte
5. administrar estudiantes
6. administrar calificaciones
7. consultar secciones, clases, asignaturas
8. crear reportes genericos

No puede:

1. entrar a auditoría
2. solicitar reportes de auditoría
3. ejecutar endpoints marcados solo para `ADMIN`

---

## 6. Matriz operativa frontend por módulo

## 6.1 Publicos

- `POST /api/v1/auth/login`
- `GET /api/v1/system/ping`
- Swagger/OpenAPI
- Actuator básico

Frontend:

1. login screen
2. chequeo de salud opcional

## 6.2 Auth

- `GET /api/v1/auth/me`

Frontend:

1. bootstrap de sesión
2. resolver rol
3. decidir layout y menu

## 6.3 Dashboard

- `GET /api/v1/dashboard/resumen`

Frontend:

1. landing luego de login
2. tarjetas KPI
3. acceso rápido a módulos

## 6.4 Representantes

Permisos:

- `ADMIN`, `SECRETARIA`: listar, ver detalle, crear, actualizar

Frontend:

1. módulo CRUD completo sin restricciones especiales por rol
2. tabla + formulario modal o vista lateral funciona bien

## 6.5 Docentes

Permisos:

- `ADMIN`, `SECRETARIA`: listar, ver detalle, crear, actualizar
- `ADMIN`: cambiar estado

Frontend:

1. `SECRETARIA` debe poder editar datos generales
2. botón de cambiar estado solo visible para `ADMIN`

## 6.6 Secciones

Permisos:

- `ADMIN`, `SECRETARIA`: listar, ver detalle
- `ADMIN`: crear, actualizar, cambiar estado

Frontend:

1. `SECRETARIA` usa este módulo principalmente como consulta
2. `ADMIN` necesita CRUD completo

## 6.7 Asignaturas

Permisos:

- `ADMIN`, `SECRETARIA`: listar, ver detalle
- `ADMIN`: crear, actualizar, cambiar estado

Frontend:

1. mismo patron que secciones
2. catálogo académico administrado solo por `ADMIN`

## 6.8 Estudiantes

Permisos:

- `ADMIN`, `SECRETARIA`: listar, ver detalle, crear, actualizar, asignar sección vigente
- `ADMIN`: cambiar estado

Frontend:

1. es uno de los módulos principales
2. requiere formulario más rico
3. la acción de asignar sección merece CTA propio

## 6.9 Clases

Permisos:

- `ADMIN`, `SECRETARIA`: listar, ver detalle
- `ADMIN`: crear, actualizar, cambiar estado

Frontend:

1. `SECRETARIA` consulta horario
2. `ADMIN` define la oferta de clases

## 6.10 Calificaciones

Permisos:

- `ADMIN`, `SECRETARIA`: listar, ver detalle, crear, actualizar

Frontend:

1. módulo transaccional importante
2. requiere filtro por estudiante, clase y parcial
3. no hay patch de estado; es CRUD simple de nota

## 6.11 Reportes

Permisos:

- `ADMIN`, `SECRETARIA`: crear reporte generico, listar solicitudes, ver detalle, ver estado, ver resultado, descargar archivo
- `ADMIN`: reintentar solicitud fallida

Frontend:

1. area de reportes propia
2. polling de estado
3. historial de solicitudes
4. descargas

## 6.12 Auditoría

Permisos:

- `ADMIN`: listar eventos, crear solicitud de reporte de auditoría
- `SECRETARIA`: sin acceso

Frontend:

1. módulo exclusivo de `ADMIN`
2. no muestres ni enlaces ni botones a `SECRETARIA`

---

## 7. Flujos principales que conviene diseñar primero

Si quieres avanzar rápido sin diseñar de más, este es el orden correcto.

## 7.1 Flujo de sesión

Pantallas:

1. login
2. splash o bootstrap de sesión
3. dashboard inicial

Debe resolver:

1. estado autenticado/no autenticado
2. lectura de `auth/me`
3. construcción dinamica del menu por rol

## 7.2 Flujo de estudiantes

Pantallas:

1. listado de estudiantes
2. detalle o edición
3. crear estudiante
4. asignar sección vigente

Por que primero:

1. conecta representantes y secciones
2. te obliga a resolver formularios, tablas y permisos
3. es el centro del dominio académico

## 7.3 Flujo de calificaciones

Pantallas:

1. listado filtrable
2. crear/editar calificación

Por que pronto:

1. toca un flujo operativo real
2. te obliga a manejar validaciones de números y parciales

## 7.4 Flujo de reportes

Pantallas:

1. crear solicitud
2. ver estado
3. ver historial
4. descargar archivo

Por que antes de cerrar frontend:

1. define experiencia asíncrona
2. cambia la arquitectura del manejo de loading y background states

## 7.5 Flujo de auditoría

Pantallas:

1. listado de eventos
2. filtros
3. solicitud de reporte

Solo para `ADMIN`.

---

## 8. Implicaciones directas para arquitectura frontend

## 8.1 Necesitas una capa de API centralizada

No conviene consumir endpoints dispersos desde componentes.

Recomendación:

1. `authApi`
2. `dashboardApi`
3. `estudiantesApi`
4. `docentesApi`
5. `seccionesApi`
6. `asignaturasApi`
7. `clasesApi`
8. `calificacionesApi`
9. `reportesApi`
10. `auditoriaApi`

## 8.2 Necesitas una capa de sesión

Debe guardar:

1. token
2. usuario actual
3. rol
4. estado de sesión

Y exponer:

1. `login`
2. `logout`
3. `bootstrapSession`
4. `hasRole('ADMIN')`
5. `hasAnyRole(...)`

## 8.3 Necesitas manejo transversal de errores

Mínimo:

1. `401` -> volver a login o limpiar sesión
2. `403` -> toast/mensaje de permiso
3. `400/422` -> errores de formulario
4. `409` -> conflicto de negocio
5. `500` -> mensaje generico con `requestId`

Ejemplo real observado:

- crear clase puede devolver `409` si el docente no esta disponible

Eso significa que el frontend no debe traducir todo error a "fallo técnico".

## 8.4 Necesitas patron de tablas con filtros y paginación

Porque casi todos los módulos lo usan.

Tabla base reusable recomendada:

1. `search q`
2. filtros avanzados
3. sort
4. page/size
5. estado de carga
6. vacio
7. error

## 8.5 Necesitas patron de formularios reutilizable

Casi todos los módulos tienen:

1. crear
2. editar
3. errores por validación
4. submit loading
5. exito

---

## 9. Cosas que debes tener en cuenta antes de diseñar visualmente

## 9.1 El sistema no es solo CRUD

Tiene:

1. roles
2. estados
3. reportes asíncronos
4. auditoría
5. descargas binarias

Si disenas solo tablas + formularios, el frontend va a quedar corto.

## 9.2 Debes diseñar por permisos, no solo por páginas

Ejemplo:

1. `SECRETARIA` puede editar un docente
2. `SECRETARIA` no puede cambiar su estado

Eso implica:

1. acciones visibles por rol
2. botones condicionales
3. mensajes de capacidad limitada

## 9.3 Debes diseñar estados intermedios

Especialmente en reportes:

1. `PENDIENTE`
2. `EN_PROCESO`
3. `COMPLETADA`
4. `ERROR`

No alcanza con un botón `Generar`.

Necesitas:

1. historial
2. polling o refresco manual
3. badges de estado
4. botón descargar solo cuando aplique

## 9.4 Debes diseñar el frontend para mensajes reales del backend

El backend ya entrega mensajes funcionales.

Usa eso a favor:

1. feedback corto al usuario
2. trazabilidad por `requestId`
3. manejo diferenciado por `errorCode`

## 9.5 Debes asumir integración con fechas y horas

Hay varios tipos:

1. `LocalDate`
2. `LocalTime`
3. timestamps ISO

Recomendación:

1. normaliza parseo
2. no mezcles formatos locales en la capa HTTP
3. convierte en la capa de presentación

---

## 10. Módulos y vistas recomendadas

## 10.1 Layout general

Recomendación de navegación:

1. `Dashboard`
2. `Estudiantes`
3. `Representantes`
4. `Docentes`
5. `Secciones`
6. `Asignaturas`
7. `Clases`
8. `Calificaciones`
9. `Reportes`
10. `Auditoria` solo ADMIN

## 10.2 Vistas por módulo

### Dashboard

1. KPIs
2. atajos
3. estado rápido del sistema

### Estudiantes

1. tabla principal
2. drawer/modal de detalle
3. formulario crear/editar
4. acción asignar sección

### Representantes

1. tabla
2. formulario simple

### Docentes

1. tabla
2. formulario simple
3. toggle de estado solo ADMIN

### Secciones

1. tabla con filtros por grado/anio/paralelo
2. formulario CRUD solo ADMIN

### Asignaturas

1. tabla por grado/area
2. formulario CRUD solo ADMIN

### Clases

1. tabla tipo horario o lista
2. filtros por sección/asignatura/docente/día
3. CRUD solo ADMIN

### Calificaciones

1. tabla
2. filtros por estudiante/clase/parcial
3. formulario de nota

### Reportes

1. generador de reporte
2. historial de solicitudes
3. estado
4. descarga
5. reintentar solo ADMIN

### Auditoría

1. tabla de eventos
2. filtros por módulo/acción/resultado/actor/fecha
3. generación de reporte de auditoría

---

## 11. Flujos de reporte que el frontend debe contemplar

## 11.1 Reportes genericos

Tipos soportados:

1. `LISTADO_ESTUDIANTES_POR_SECCION`
2. `CALIFICACIONES_POR_SECCION_Y_PARCIAL`

Formatos:

1. `XLSX`
2. `PDF`
3. `DOCX`

Flujo:

1. usuario llena formulario
2. backend crea solicitud
3. frontend muestra `solicitudId` y estado inicial
4. frontend consulta estado
5. cuando esta `COMPLETADA`, habilita descarga

## 11.2 Reporte de auditoría

Acceso:

1. solo `ADMIN`

Flujo:

1. filtrar criterios
2. solicitar reporte
3. esperar resultado
4. descargar archivo

## 11.3 Que UX no debes hacer

No disenes:

1. spinner eterno sin historial
2. descarga inmediata sin polling
3. pantalla de reportes sin estados visibles

---

## 12. Validaciones de frontend recomendadas

Aunque el backend valida, el frontend debe validar lo obvio para reducir fricción.

## 12.1 Generales

1. campos obligatorios
2. maximos de longitud
3. email con forma valida
4. estados permitidos

## 12.2 Fechas

1. `fechaNacimiento` en pasado
2. `fechaDesde <= fechaHasta`

## 12.3 Numericos

1. `grado` entre 1 y 7
2. `cupoMaximo` entre 1 y 35
3. `numeroParcial` entre 1 y 2
4. `nota` entre 0 y 10

## 12.4 Catalogos controlados

1. `estado`: `ACTIVO | INACTIVO`
2. `formatoSalida`: `XLSX | PDF | DOCX`
3. `diaSemana`: `LUNES | MARTES | MIERCOLES | JUEVES | VIERNES | SABADO`

---

## 13. Riesgos de diseño frontend si ignoras el contexto backend

## 13.1 Diseñar pantallas sin rol

Conduce a:

1. botones que luego hay que esconder a última hora
2. UX inconsistente entre `ADMIN` y `SECRETARIA`

## 13.2 Diseñar reportes como descarga inmediata

Conduce a:

1. UX rota
2. errores de expectativa
3. acoplamiento incorrecto con backend

## 13.3 Diseñar tablas sin paginación real

Conduce a:

1. problemas de performance
2. mala reutilizacion de componentes

## 13.4 Diseñar formularios sin conflicto de negocio

Conduce a:

1. manejo pobre de `409`
2. mensajes técnicos mal presentados

---

## 14. Recomendación de estrategia para iniciar frontend

Orden recomendado:

1. layout base + login + sesión
2. dashboard
3. shell de tablas reutilizable
4. módulo estudiantes
5. módulo representantes
6. módulo secciones
7. módulo asignaturas
8. módulo clases
9. módulo calificaciones
10. módulo reportes
11. módulo auditoría

Justificacion:

1. resuelves primero sesión y permisos
2. luego resuelves componentes reutilizables
3. luego resuelves el dominio central
4. dejas auditoría al final porque depende de un backend ya estable

---

## 15. Recomendación de componentes reutilizables

Conviene definir desde el inicio:

1. `AppShell`
2. `RoleGuard`
3. `ProtectedRoute` o equivalente si usas router
4. `DataTable`
5. `FilterBar`
6. `PagedToolbar`
7. `FormDialog` o `EntityDrawer`
8. `StatusBadge`
9. `AsyncJobStatusCard`
10. `ErrorBanner`
11. `ConfirmActionDialog`

---

## 16. Recomendación de estado frontend mínimo

## 16.1 Estado global

1. sesión
2. usuario actual
3. rol
4. token
5. notificaciones UI

## 16.2 Estado por vista

1. filtros
2. paginación
3. ordenamiento
4. loading
5. error
6. seleccion actual
7. modal/drawer abierto o cerrado

## 16.3 Estado de reportes

1. solicitudes cargadas
2. solicitud seleccionada
3. estado actual
4. último refresh
5. archivo disponible o no

---

## 17. Integraciones especiales que no debes olvidar

## 17.1 Descarga de archivos

Los endpoints de archivo devuelven binario.

Necesitas en frontend:

1. descargar por blob o descarga nativa
2. conservar nombre de archivo si el navegador lo permite

## 17.2 Branding de reportes

El backend ya incorpora branding/logo en la infraestructura de reportes.

Implicacion:

1. no necesitas construir el PDF en frontend
2. el frontend solo dispara solicitud y descarga

## 17.3 Auditoría

La auditoría no es solo `otra tabla`.

Es módulo de trazabilidad y supervision.

Diseño recomendado:

1. tono sobrio
2. filtros fuertes
3. buena legibilidad de metadata
4. acciones minimizadas

---

## 18. Checklist antes de abrir Figma o empezar componentes

Marca esto como cerrado:

- [ ] Ya definiste layout por rol
- [ ] Ya definiste estrategia de sesión JWT
- [ ] Ya definiste wrapper de `ApiResponse<T>`
- [ ] Ya definiste wrapper de errores
- [ ] Ya definiste tabla paginada reutilizable
- [ ] Ya definiste formulario reutilizable
- [ ] Ya definiste patron de polling para reportes
- [ ] Ya definiste como ocultar acciones por rol
- [ ] Ya definiste como mostrar `403`, `409` y `500`
- [ ] Ya decidiste cuales módulos van primero en MVP visual

---

## 19. Conclusiones prácticas

Para iniciar frontend, estas son las decisiones que debes asumir como base:

1. el backend ya no se disena como hipotesis; ya responde con contratos y permisos reales
2. la UI debe ser sensible a rol desde el primer día
3. la experiencia de reportes debe ser asíncrona
4. la paginación y filtros deben ser un patron común
5. auditoría debe existir como módulo de administración avanzada, no como detalle secundario
6. la integración debe centrarse en `data`, `errorCode`, `message` y `requestId`

Si disenas respetando este contexto, el frontend va a quedar alineado con el backend actual y no vas a tener que rehacer arquitectura por choques de contrato.

---

## Addendum 2026-03-03: sesión renovable y descargas

Actualizacion importante para frontend:

1. La sesión ya no es solo `accessToken + usuario`.
 El desktop mantiene:
 - `accessToken`
 - `refreshToken`
 - expiración absoluta de ambos
 - usuario autenticado

2. `ApiClient` intenta renovar el `accessToken` automaticamente antes de que expire.

3. Si `refresh` falla y el `accessToken` ya no sirve, el cliente ejecuta logout local y vuelve al flujo de autenticación.

4. Las descargas de reportes salen como binario HTTP, pero el backend ya no depende de rutas fisicas expuestas al frontend.
 Internamente usa un repositorio documental desacoplado.


