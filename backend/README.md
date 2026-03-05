# Backend UENS - Guia general

Este directorio contiene:

- `uens-backend/`: aplicacion Spring Boot 4 (API principal).
- `docs/`: documentacion funcional, API y despliegue.

## Por donde empezar (junior-mid)

1. Leer `uens-backend/README.md` para setup completo local.
2. Levantar DB y API en local.
3. Probar login y endpoints con Postman.
4. Revisar `docs/api/API_ENDPOINTS.md` como contrato operativo.

## Mapa rapido de documentacion

- Guia tecnica principal:
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
- Enfoque: DDD tactico por modulos (`api`, `application`, `infrastructure`).
- Seguridad: JWT + autorizacion por roles (`ADMIN`, `SECRETARIA`).
- Persistencia: PostgreSQL + JPA/Hibernate.
- Reportes: procesamiento asincrono con cola y exportacion de archivos.
