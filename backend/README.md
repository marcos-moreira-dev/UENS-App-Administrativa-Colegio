# Backend UENS - Guía general

Este directorio contiene:

- `uens-backend/`: aplicación Spring Boot 4 (API principal).
- `docs/`: documentación funcional, API y despliegue.

## Por dónde empezar (junior-mid)

1. Leer `uens-backend/README.md` para setup completo local.
2. Levantar DB y API en local.
3. Probar login y endpoints con Postman.
4. Revisar `docs/api/API_ENDPOINTS.md` como contrato operativo.

## Mapa rápido de documentación

- Guía técnica principal:
 `uens-backend/README.md`
- Endpoints y ejemplos:
 `docs/api/API_ENDPOINTS.md`
- Variables de entorno:
 `docs/despliegue/variables_entorno.md`
- Docker local:
 `docs/despliegue/docker_local.md`
- Checklist release:
 `docs/despliegue/checklist_release_v1.md`

## Resumen de arquitectura

- Estilo: monolito modular.
- Enfoque: DDD táctico por módulos (`api`, `application`, `infrastructure`).
- Seguridad: JWT + autorización por roles (`ADMIN`, `SECRETARIA`).
- Persistencia: PostgreSQL + JPA/Hibernate.
- Reportes: procesamiento asíncrono con cola y exportación de archivos.
