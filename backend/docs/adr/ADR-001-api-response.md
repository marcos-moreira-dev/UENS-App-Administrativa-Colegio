# ADR-001: Contrato estándar de respuesta API

## Estado
Aceptado

## Contexto
El backend expone muchos módulos y endpoints. Sin un contrato uniforme, cada módulo devuelve formas distintas y el frontend termina con lógica de parseo repetida.

## Decision
Se adopta un contrato único para respuestas exitosas y errores:

- Exito: `ApiResponse<T>` con campos `ok`, `message`, `data`, `meta`, `timestamp`.
- Error: `ApiErrorResponse` con campos `ok`, `errorCode`, `message`, `details`, `path`, `timestamp`, `requestId`.
- Paginación: `ResponseFactory.page(...)` que envuelve `PageResponseDto` en `data`.

## Consecuencias
- Positivas:
 - Manejo uniforme para frontend y clientes API.
 - Menor acoplamiento a cada módulo.
 - Mejor trazabilidad con `requestId`.
- Negativas:
 - Cambio en contrato impacta a todos los consumidores.

## Notas de implementación
- El armado de respuestas debe hacerse con `ResponseFactory`.
- La traducción de excepciones se centraliza en `GlobalExceptionHandler`.
- Se evita devolver entidades JPA directo en controller.

