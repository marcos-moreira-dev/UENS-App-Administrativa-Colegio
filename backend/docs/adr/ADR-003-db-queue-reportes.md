# ADR-003: Cola de reportes en base de datos

## Estado
Aceptado

## Contexto
La generación de reportes puede ser costosa. Ejecutarla dentro de la request degrada tiempos de respuesta.

## Decision
Se implementa una cola simple en BD (`reporte_solicitud_queue`) con worker programado:

- La API encola solicitud con estado inicial `PENDIENTE`.
- Un scheduler reclama lotes pendientes (`claimPendientes`) y los procesa.
- Estados soportados: `PENDIENTE`, `EN_PROCESO`, `COMPLETADA`, `ERROR`.
- Reintentos limitados por `app.report.queue.max-attempts`.

## Consecuencias
- Positivas:
 - Respuesta rápida al usuario al crear solicitud.
 - Resistencia a reinicios del proceso.
- Negativas:
 - Mayor complejidad operacional.
 - Necesidad de monitorear stuck jobs.

## Notas de implementacion
- Se usa claim por lote para reducir colisiones.
- En error temporal se puede volver a `PENDIENTE`.
- En error final se marca `ERROR` y se conserva detalle.

