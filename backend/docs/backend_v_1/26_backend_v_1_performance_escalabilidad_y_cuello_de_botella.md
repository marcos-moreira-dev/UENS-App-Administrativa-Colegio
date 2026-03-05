# 26_backend_v_1_performance_escalabilidad_y_cuello_de_botella

- Version: 1.0
- Estado: Vigente
- Ámbito: rendimiento y escalabilidad con criterio
- Relacionado con:
  - `24_backend_v_1_observabilidad_logs_metricas_alertas.md`
  - `29_backend_v_1_modelo_de_datos_sql_migraciones_indices_y_consistencia.md`
  - `35_backend_v_1_mensajeria_eventos_colas_rabbitmq_kafka_cqrs_y_cdn.md`

---

## 1. Propósito

Performance no es optimizar por ansiedad.
Es medir, encontrar el cuello de botella y actuar donde realmente duele.

---

## 2. Los cuellos de botella más comunes

1. SQL sin índices
2. N+1 con ORM
3. demasiadas llamadas bloqueantes
4. pool de conexiones mal configurado
5. generación de archivos pesada
6. payloads innecesariamente grandes
7. colas o jobs atascados

---

## 3. Regla numero uno

No optimices algo que no mediste.

Primero:

1. identifica el endpoint lento
2. mide tiempo
3. revisa logs y métricas
4. mira consulta SQL o IO real

---

## 4. SQL y ORM

Las mejoras más rentables suelen estar aqui.

Debes aprender a revisar:

- índices
- cardinalidad
- joins
- `EXPLAIN`
- consultas repetidas
- N+1

---

## 5. Caching

Cache sirve, pero no es magia.

Usalo cuando:

- los datos cambian poco
- la lectura es mucho mayor que la escritura
- el costo de recomputar es alto

No lo uses para tapar mala modelacion o mala consulta.

---

## 6. Asincronia

Mover algo a asíncrono ayuda cuando:

- tarda mucho
- no necesitas respuesta inmediata
- puede reintentarse

Tu módulo de reportes ya es un ejemplo correcto.

---

## 7. Escalabilidad horizontal

Antes de pensar en muchas instancias, confirma que ya resolviste:

1. consultas lentas
2. jobs mal diseniados
3. storage mal acoplado
4. logs y métricas insuficientes

Escalar horizontalmente sin arreglar eso solo multiplica el caos.

---

## 8. Criterio practico para este proyecto

Lo siguiente más rentable para UENS seria:

1. medir tiempos de CRUD grandes
2. vigilar queries de filtros y paginacion
3. vigilar generación de reportes
4. revisar crecimiento de archivos y disco

---

## 9. Señales de que necesitas otra arquitectura

Podrias pensar en colas más serias o mensajeria cuando:

- los jobs aumentan mucho
- aparecen integraciones externas lentas
- necesitas desacoplar procesamiento pesado

Eso todavia no obliga a microservicios.

---

## 10. Siguiente lectura

- `27_backend_v_1_integraciones_externas_y_storage_providers.md`


