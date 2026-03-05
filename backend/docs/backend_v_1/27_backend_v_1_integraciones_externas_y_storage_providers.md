# 27_backend_v_1_integraciones_externas_y_storage_providers

- Version: 1.0
- Estado: Vigente
- Ámbito: integraciones con terceros y storage realista
- Relacionado con:
  - `21_backend_v_1_sesion_renovable_y_repositorio_documental_local.md`
  - `26_backend_v_1_performance_escalabilidad_y_cuello_de_botella.md`
  - `35_backend_v_1_mensajeria_eventos_colas_rabbitmq_kafka_cqrs_y_cdn.md`

---

## 1. Propósito

Todo backend serio termina integrando algo externo:

- correo
- object storage
- pagos
- webhooks
- APIs de terceros

Este documento te da criterio para hacerlo sin acoplar ni fragilizar el sistema.

---

## 2. Reglas base de integración

1. timeout explicito
2. retries controlados
3. idempotencia cuando aplique
4. logs con requestId y contexto
5. contrato de error claro
6. no mezclar infraestructura externa con dominio central

---

## 3. Patron correcto

La base correcta suele ser:

- puerto
- adapter
- configuración por proveedor

Eso ya lo estas aplicando con:

- `DocumentStoragePort`
- `LocalFilesystemDocumentStorageAdapter`

---

## 4. Storage providers más comunes

### Para aprender local

- filesystem local
- MinIO

### Para mercado

- Amazon S3
- Google Cloud Storage
- Azure Blob Storage

Google Drive no suele ser la opción backend correcta para este tipo de caso.

---

## 5. Timeouts y retries

No debes dejar esto "a ver si responde".

Cada integración deberia pensar:

1. cuanto espero
2. cuantas veces reintento
3. cuando corto
4. que hago si falla

---

## 6. Webhooks y procesos externos

Si algun día integras sistemas que te llaman desde afuera:

- valida firma si existe
- guarda evento recibido
- procesa de forma idempotente
- separa recepcion de procesamiento si el flujo crece

---

## 7. Aplicado a UENS

Tus siguientes integraciones realistas podrian ser:

1. storage tipo MinIO/S3 para reportes
2. correo para notificaciones simples
3. webhook o API externa solo si el proyecto evoluciona

No necesitas más que eso por ahora.

---

## 8. Siguiente lectura

- `28_backend_v_1_despliegue_realista_dev_stage_prod.md`


