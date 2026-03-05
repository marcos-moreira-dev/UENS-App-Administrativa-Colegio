# 23_backend_v_1_mantenimiento_operacion_incidentes

- Versión: 1.0
- Estado: Vigente
- Ámbito: trabajo real de mantenimiento mensual o continuo
- Relacionado con:
 - `16_backend_v_1_base_trazabilidad_backend.md`
 - `24_backend_v_1_observabilidad_logs_metricas_alertas.md`
 - `30_backend_v_1_testing_estrategia_profesional.md`

---

## 1. Propósito

Este documento explica cómo se trabaja cuando ya no estas "construyendo un proyecto", sino manteniendo un sistema real que alguien usa y por el que te pagan.

---

## 2. Que hace realmente una persona de mantenimiento backend

No pasa el día escribiendo features nuevas.

Pasa mucho tiempo en:

1. diagnosticar incidentes
2. responder dudas de operación
3. revisar logs y requestId
4. entender datos inconsistentes
5. corregir bugs sin romper lo que ya sirve
6. reducir deuda técnica
7. agregar pruebas que faltaban

---

## 3. Trabajo recurrente mensual

### 3.1 Soporte

- tickets de usuarios
- errores en produccion
- permisos mal configurados
- problemas de datos

### 3.2 Salud técnica

- revisar logs
- revisar errores repetidos
- revisar jobs o colas
- revisar almacenamiento de archivos

### 3.3 Evolucion controlada

- cambios pequenos
- hardening
- mejoras de performance
- refactors acotados

---

## 4. Tipos de incidente

### S1 crítico

- login caido
- sistema no arranca
- BD inaccesible
- corrupcion de datos

### S2 alto

- módulo importante inutilizable
- reportes no descargan
- errores 500 frecuentes

### S3 medio

- bug con workaround
- comportamiento incorrecto pero no bloqueante

### S4 bajo

- detalle visual
- mejora de mensaje
- problema raro de baja frecuencia

---

## 5. Flujo correcto de manejo de incidente

1. identificar impacto y severidad
2. capturar evidencia:
 - requestId
 - hora
 - endpoint
 - rol
 - datos mínimos del caso
3. reproducir
4. clasificar la causa
5. corregir
6. agregar prueba
7. documentar que paso

---

## 6. Como diagnosticar bien

Nunca empieces "tocando código a ciegas".

Primero revisa:

1. `requestId`
2. logs
3. auditoría
4. datos reales en BD
5. si el error es nuevo o recurrente

Preguntas útiles:

- fallo por dato raro
- fallo por permiso
- fallo por código
- fallo por infraestructura
- fallo por contrato frontend/backend

---

## 7. Clasificación mental de errores

### Validación

- request malo
- enum invalido
- campos faltantes

### Regla de negocio

- estado no permitido
- duplicado funcional
- conflicto académico

### Seguridad

- token invalido
- sin permisos
- ownership violado

### Infraestructura

- BD
- filesystem
- red
- timeout
- proveedor externo

Esta clasificación reduce mucho el ruido mental.

---

## 8. Hotfix vs arreglo normal

### Hotfix

Usarlo cuando:

- el sistema esta bloqueado
- hay impacto alto
- necesitas salir rápido

Reglas:

- cambio pequeno
- riesgo bajo
- prueba puntual
- despliegue rápido

### Arreglo normal

Usarlo cuando:

- puedes reproducir bien
- el incidente no exige urgencia extrema
- conviene corregir de forma más limpia

---

## 9. Lo que nunca deberias hacer

- tocar produccion sin evidencia
- corregir solo en frontend un bug que es de backend
- borrar datos para "arreglar rápido" sin respaldo
- hacer refactor grande dentro de un hotfix
- cerrar incidente sin prueba o sin entender la causa

---

## 10. Runbooks mínimos que deberias tener

1. login falla
2. reporte no genera
3. archivo no descarga
4. token expira y cliente queda fuera
5. BD lenta
6. rollback simple

Un runbook no es burocracia.
Es memoria externa del sistema.

---

## 11. Mantenimiento y deuda técnica

El mantenimiento sano siempre separa:

- deuda que duele ya
- deuda que puede esperar
- deuda que es solo gusto personal

Si no aprendes a distinguir eso, te vas a desgastar.

---

## 12. Aplicado a UENS

Tu backend ya tiene una base buena para mantenimiento porque tiene:

- requestId
- auditoría
- errores tipados
- módulos claros
- pruebas

Lo siguiente natural es fortalecer:

1. runbooks
2. observabilidad
3. backups
4. despliegue por ambientes

---

## 13. Siguiente lectura

- `24_backend_v_1_observabilidad_logs_metricas_alertas.md`


