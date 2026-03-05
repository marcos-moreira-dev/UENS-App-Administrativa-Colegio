# ADR-004: Mappers manuales en vez de generación automatica

## Estado
Aceptado

## Contexto
El backend requiere control estricto del contrato API y reglas de transformacion entre entidad y DTO.

## Decision
Se usan mappers manuales por módulo (`*DtoMapper`) en capa `application`.

## Consecuencias
- Positivas:
  - Transformaciones explicitas y faciles de auditar.
  - Menor magia en compilacion.
  - Cambios de contrato más controlados.
- Negativas:
  - Más codigo boilerplate.

## Notas de implementacion
- No exponer entidades JPA en controllers.
- Mantener los mappers pequenos y deterministas.
- Incluir tests cuando el mapeo tenga lógica condicional.


