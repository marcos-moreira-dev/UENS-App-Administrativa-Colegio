# 35_backend_v_1_mensajeria_eventos_colas_rabbitmq_kafka_cqrs_y_cdn

- Version: 1.0
- Estado: Vigente
- Ámbito: colas, eventos y componentes distribuidos con criterio
- Relacionado con:
  - `10_backend_v_1_reporte_solicitudes_cola_simple_db_queue.md`
  - `26_backend_v_1_performance_escalabilidad_y_cuello_de_botella.md`
  - `34_backend_v_1_siguiente_paso_devops_iaas_paas_vps.md`

---

## 1. Propósito

Este documento te orienta sobre RabbitMQ, Kafka, CQRS, CDN y otras piezas que suelen sonar "muy corporativas".

La pregunta correcta no es "debo usarlas ya".
La pregunta correcta es "que problema resuelven y cuando tienen sentido".

---

## 2. RabbitMQ

RabbitMQ sirve muy bien para:

- colas de trabajo
- reintentos
- tareas asincronas
- fanout simple
- desacoplar productor y consumidor

Es ideal cuando quieres mover trabajos fuera del request principal.

Para tu proyecto seria más natural que Kafka si algun día creces los workers o notificaciones.

---

## 3. Kafka

Kafka es más fuerte en:

- streaming de eventos
- gran volumen
- relectura de eventos
- integración entre muchos consumidores

No es la mejor primera cola para un sistema como UENS.

Kafka empieza a tener sentido cuando:

- hay muchos servicios
- mucho throughput
- analytics o eventos a gran escala

---

## 4. Redis como punto intermedio

Redis puede servir para:

- cache
- rate limiting
- sesiones o refresh token store
- colas ligeras

Antes de Kafka, muchas veces Redis resuelve mejor y más simple.

---

## 5. CQRS

CQRS separa lecturas y escrituras.

Puede ser útil cuando:

- hay mucha diferencia entre modelo de escritura y modelo de lectura
- los reportes y consultas se vuelven muy distintas del dominio transaccional

No es obligatorio para que un backend sea serio.

En UENS, hoy no lo veo como el siguiente paso.

---

## 6. Event-driven architecture

Arquitectura por eventos sirve cuando:

- muchos procesos reaccionan a cambios
- quieres desacoplar acciones secundarias
- hay integraciones externas

Ejemplo:

- "reporte generado" dispara notificacion
- "usuario bloqueado" dispara auditoria de seguridad

---

## 7. CDN

CDN no es una cola ni una pieza de backend transaccional.

Sirve para:

- distribuir contenido estatico
- mejorar latencia de descarga
- descargar al backend de trafico estatico

Para UENS no es prioridad salvo que sirvas muchos archivos publicos o descargas masivas.

---

## 8. Orden correcto de aprendizaje

1. Docker y despliegue
2. observabilidad
3. backups y operación
4. Redis
5. RabbitMQ
6. luego Kafka si de verdad el sistema lo necesita

---

## 9. Recomendación concreta para tu proyecto

### Hoy

- mantener DB queue para reportes
- fortalecer operación
- eventualmente mover refresh token store a Redis
- eventualmente mover reportes a MinIO/S3

### Después

- si la asincronia crece, RabbitMQ

### Mucho después

- Kafka solo si el volumen y la arquitectura lo justifican

---

## 10. Cierre

Saber que existen RabbitMQ, Kafka, CQRS y CDN si importa.
Pero más importante es saber cuando no usarlos.

Ese criterio es mucho más valioso que poder nombrarlos en una lista de tecnologias.


