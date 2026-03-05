# ADR-002: Autenticación JWT stateless

## Estado
Aceptado

## Contexto
La API necesita autenticación para endpoints administrativos sin mantener sesión en servidor.

## Decision
Se usa autenticación JWT stateless:

- Emision de token en `POST /api/v1/auth/login`.
- Validación por request en `JwtAuthenticationFilter`.
- Sin sesión HTTP: `SessionCreationPolicy.STATELESS`.
- Firma HS256 con secreto en propiedades (`app.security.jwt.secret`).
- Claims mínimos: `sub`, `rol`, `iat`, `exp`, opcional `iss`.

## Consecuencias
- Positivas:
 - Escalabilidad horizontal sin afinidad de sesión.
 - Modelo simple para clientes web y moviles.
- Negativas:
 - Revocacion inmediata no nativa (se resuelve con expiraciones cortas o blacklist futura).

## Notas de implementación
- Roles soportados definidos en `SecurityRoles`.
- Errores de token mapeados a códigos `AUTH-*`.
- Endpoints publicos definidos en `SecurityConfig`.

