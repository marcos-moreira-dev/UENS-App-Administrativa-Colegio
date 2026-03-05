# 00_backend_v_1_indice_y_mapa_documental

* **Versión:** 0.4
* **Estado:** En revisión (alineación final del paquete)
* **Ámbito:** Backend V1 (Spring Boot + Java 21) del sistema escolar **UENS**
* **Enfoque:** arquitectura, diseño técnico, contrato API y criterios de implementación
* **Fuera de alcance en este paquete:** UI/UX JavaFX, diseño visual, branding/diseño gráfico
* **Convención canónica de nombres (archivos en repo):** `backend_v_1` (se respeta tal cual existe hoy)
* **Nota de normalización futura:** si más adelante deseas renombrar a `backend_v1` (sin guion bajo), hazlo como refactor controlado. Mientras tanto, este paquete asume los nombres actuales.

---

## 1. Propósito de este documento

Este archivo es el **punto de entrada oficial** del paquete de documentación del backend V1.

Su función es:

1. Explicar cómo está organizada la documentación del backend.
2. Definir el alcance real (qué cubre y qué no cubre).
3. Servir como mapa de lectura para implementación.
4. Mantener coherencia transversal entre arquitectura, endpoints, validaciones, seguridad y despliegue.

> Este documento no diseña endpoints ni código. Su trabajo es **ordenar, conectar y asegurar consistencia** del paquete.

---

## 2. Contexto y relación con documentos previos

Este paquete **no reemplaza** la documentación de negocio ni el diseño de datos. Los toma como base.

### 2.1 Documentos de referencia (fuente funcional y de dominio)

* `01_levantamiento_informacion_negocio.md`
* `02_levantamiento_requerimientos.md`
* `03_modelo_conceptual_dominio.md`
* `04_reglas_negocio_y_supuestos.md`
* `05_glosario_alcance_y_limites.md`
* `V2_3FN.sql` (esquema físico oficial del dominio académico para fase 1)

### 2.2 Regla importante de consistencia

Si una decisión del backend contradice:

* una regla de negocio,
* un requerimiento,
* o una restricción del SQL,

entonces se debe **ajustar el backend** o **documentar explícitamente** el cambio de alcance.

Regla operativa: **sin parches silenciosos**.

---

## 3. Alcance del paquete Backend V1

### 3.1 Qué sí cubre este paquete

* arquitectura general del backend
* convenciones y estándares de código
* modelado por módulos (CRUD + orquestación)
* contrato API (respuestas, errores, códigos)
* catálogo de endpoints y casos de uso
* política de validaciones, reglas de negocio y excepciones
* paginación, filtros, ordenamiento y consultas
* seguridad JWT + Swagger/OpenAPI + despliegue mínimo (Docker)
* reportes asíncronos con **cola simple en BD (DB queue)**

### 3.2 Qué no cubre (o queda diferido)

* UI/UX detallada en JavaFX
* pruebas automatizadas extensas
* CI/CD completo
* observabilidad enterprise
* infraestructura avanzada (Kubernetes, brokers externos, etc.)

---

## 4. Decisiones transversales cerradas (V1)

Estas decisiones gobiernan **todos** los documentos del paquete:

1. **Monolito modular** (feature-first) con capas internas por módulo.
2. **Backend agnóstico del frontend**, pero orientado a **casos de uso** reales.
3. **Contrato API uniforme**:

 * éxito: `ApiResponse<T>`
 * error: `ApiErrorResponse`
 * listados: `ApiResponse<PageResponseDto<T>>`
4. **Paginación/consulta uniforme:** `page`, `size`, `sort`, `q` (con whitelist).
5. **Errores trazables** por categoría: `VR / RN / AUTH / API / SYS`.
6. **Seguridad V1:** JWT stateless + BCrypt + roles.
7. **Configuración por `.properties`** (no YAML) + `MessageSource`.
8. **Reportes asíncronos (DB queue):**

 * `POST /reportes/solicitudes` crea una solicitud (**recomendado:** `201 Created`)
 * procesamiento interno (worker) y polling a estado/resultado

---

## 5. Índice maestro del paquete (archivos actuales)

> Nota: el repositorio actual **no incluye** un `01_backend_v_1_vision_y_alcance.md`. Se deja como **pendiente recomendado** porque cierra alcance y criterios de éxito.

### 5.1 Documentos base

* **00** `00_backend_v_1_indice_y_mapa_documental.md` — mapa y consistencia
* **01 (pendiente recomendado)** `01_backend_v_1_vision_y_alcance.md` — visión, alcance y criterios de éxito
* **02** `02_backend_v_1_arquitectura_general.md` — arquitectura y decisiones de alto nivel
* **03** `03_backend_v_1_convenciones_y_estandares_codigo.md` — naming, estilo, estándares
* **04** `04_backend_v_1_modelado_aplicacion_y_modulos.md` — mapa de módulos y responsabilidades

### 5.2 Contrato, API y reglas

* **05** `05_backend_v_1_diseno_api_contrato_respuestas_y_errores.md` — contrato API uniforme
* **06** `06_backend_v_1_api_endpoints_y_casos_de_uso.md` — catálogo de endpoints y casos de uso
* **07** `07_backend_v_1_validaciones_reglas_negocio_y_excepciones.md` — VR/RN/excepciones
* **08** `08_backend_v_1_paginacion_filtros_ordenamiento_y_consultas.md` — gramática de consultas

### 5.3 Operación

* **09** `09_backend_v_1_seguridad_documentacion_y_despliegue_minimo.md` — JWT, Swagger, Docker y operación mínima
* **10** `10_backend_v_1_reporte_solicitudes_cola_simple_db_queue.md` — DB queue, worker, estados y polling

### 5.4 Implementación (detalle)

* **13** `13_backend_v_1_dtos_mappers_matriz_permisos_filtros_y_mapeo_jpa.md` — DTOs, mappers, matriz de permisos, whitelists, mapeo JPA↔SQL
* **14** `14_backend_v_1_arbol_archivos_completo_sugerido_implementacion.md` — árbol sugerido para arrancar implementación
* **15** `15_backend_v_1_reportes_generacion_archivos_excel_pdf_word.md` - generación de reportes (xlsx/pdf/docx) sobre cola asíncrona
* **16** `16_backend_v_1_base_trazabilidad_backend.md` - matriz base RF/VR, brechas actuales y plan pragmático de trazabilidad
* **17** `17_backend_v_1_auditoria_operativa_y_reporte_admin.md` - criterios de auditoría operativa, narrativa pedagógica y reporte de auditoría solo ADMIN
* **18** `18_backend_v_1_acid_transacciones_consistencia_backend.md` - ACID aplicado al backend, transacciones, compensaciones y justificaciones
* **19** `19_backend_v_1_contexto_integracion_y_diseno_frontend.md` - contexto operativo del backend para iniciar diseño frontend, con roles, flujos, restricciones y criterios de integración
* **20** `20_backend_v_1_hardening_seguridad_login_rate_limit_cors_headers_ownership.md` - endurecimiento pragmatico de seguridad: login, CORS, headers, ownership y trazabilidad operativa
* **21** `21_backend_v_1_sesion_renovable_y_repositorio_documental_local.md` - sesión renovable, repositorio documental desacoplado y patrones aplicados

### 5.5 Crecimiento profesional y operación avanzada

* **22** `22_backend_v_1_roadmap_full_backend.md` - mapa de estudio para pasar de backend funcional a backend fuerte
* **23** `23_backend_v_1_mantenimiento_operacion_incidentes.md` - trabajo real de mantenimiento, soporte, incidentes y hotfix
* **24** `24_backend_v_1_observabilidad_logs_metricas_alertas.md` - logs, métricas, dashboards, tracing y alertas
* **25** `25_backend_v_1_seguridad_practica_backend.md` - seguridad aplicada al backend y a su operación diaria
* **26** `26_backend_v_1_performance_escalabilidad_y_cuello_de_botella.md` - performance, tuning, concurrencia y límites
* **27** `27_backend_v_1_integraciones_externas_y_storage_providers.md` - integraciones con terceros, timeouts, retries y storage providers
* **28** `28_backend_v_1_despliegue_realista_dev_stage_prod.md` - despliegue por ambientes y operación mínima seria
* **29** `29_backend_v_1_modelo_de_datos_sql_migraciones_indices_y_consistencia.md` - gobierno de esquema, índices y migraciones
* **30** `30_backend_v_1_testing_estrategia_profesional.md` - estrategia de pruebas para backend real
* **31** `31_backend_v_1_patrones_diseno_usados_con_criterio.md` - cuando usar patrones y cuando evitarlos
* **32** `32_backend_v_1_freelance_backend_y_trabajo_autonomo.md` - enfoque autónomo, alcance, soporte y cobro de mantenimiento
* **33** `33_backend_v_1_habilidades_complementarias_y_analisis_de_datos.md` - habilidades de soporte, SQL analitico, BI y criterio de datos
* **34** `34_backend_v_1_siguiente_paso_devops_iaas_paas_vps.md` - siguiente paso realista hacia DevOps e infraestructura
* **35** `35_backend_v_1_mensajeria_eventos_colas_rabbitmq_kafka_cqrs_y_cdn.md` - colas, eventos, RabbitMQ, Kafka, CQRS y CDN con criterio
* **36** `36_backend_v_1_integracion_ia_modelos_locales_y_en_linea.md` - IA aplicada a producto y backend con modelos locales y en línea
* **37** `37_backend_v_1_landing_page_ux_ui_y_pagos_en_linea.md` - landing page, hero, conversion, UX/UI y gestión técnica de pagos
* **38** `38_backend_v_1_tipos_de_apis_rest_websocket_sse_graphql_grpc_webhooks_y_roadmap.md` - tipos de API, comparación, cuándo usar cada estilo y roadmap de aprendizaje
* **39** `39_backend_v_1_algoritmos_y_estructuras_de_datos_riesgos_backend.md` - errores típicos de algoritmos y estructuras de datos que rompen un backend real
* **40** `40_backend_v_1_concurrencia_locks_transacciones_y_condiciones_de_carrera.md` - concurrencia, locks, transacciones e idempotencia aplicadas a backend real
* **41** `41_backend_v_1_backend_iot_protocolos_udp_mqtt_coap_y_arquitecturas.md` - backend para IoT, protocolos, patrones, gateways y máquinas de estado

### 5.6 Documento histórico (no guía de implementación)

* **11 (histórico)** `11_backend_v_1_arbol_archivos_proyecto_hipotetico.md`

 * Útil como referencia de contexto, pero **no** es el árbol final de implementación (ese es `14`).

---

## 6. Orden de lectura recomendado

### 6.1 Para implementación desde cero (ruta completa)

1. `00`
2. `01` (si lo creas)
3. `02`
4. `03`
5. `04`
6. `05`
7. `06`
8. `07`
9. `08`
10. `09`
11. `10`
12. `13`
13. `14`
14. `15`
15. `16`
16. `17`
17. `18`
18. `19`
19. `20`
20. `21`
21. `22`
22. `23`
23. `24`
24. `25`
25. `26`
26. `27`
27. `28`
28. `29`
29. `30`
30. `31`
31. `32`
32. `33`
33. `34`
34. `35`
35. `36`
36. `37`
37. `38`
38. `39`
39. `40`
40. `41`

### 6.2 Para arrancar a programar sin perder consistencia

1. `02`
2. `04`
3. `05`
4. `06`
5. `07`
6. `08`
7. `09`
8. `10`
9. `13`
10. `14`
11. `15`
12. `16`
13. `17`
14. `18`
15. `19`
16. `20`
17. `21`
18. `03` (como referencia durante el desarrollo)

### 6.3 Para crecer de backend funcional a backend fuerte

1. `22`
2. `23`
3. `24`
4. `25`
5. `30`
6. `28`
7. `29`
8. `26`
9. `27`
10. `31`
11. `32`
12. `33`
13. `34`
14. `35`
15. `36`
16. `37`
17. `38`
18. `39`
19. `40`
20. `41`

---

## 7. Mapa de dependencias entre documentos (resumen)

* `02` (arquitectura) condiciona `04`, `09`, `10`, `13`, `14`.
* `04` (módulos) condiciona `06` y parte de `13`.
* `05` (contrato API) condiciona `06`, `07`, `08`, `09`, `10`, `13`.
* `06` (endpoints) condiciona `08`, `09`, `10`, `13`.
* `07` (VR/RN/excepciones) condiciona `05`, `06`, `09`, `10`, `13`.
* `08` (consultas) se aplica a los listados definidos en `06` y al historial de `10`.
* `09` (seguridad/Swagger/Docker) se aplica sobre los endpoints y el contrato ya definido.
* `10` (reportes DB queue) usa reglas de `05–09`.
* `13` aterriza detalle de DTOs, permisos y whitelists (cierre operativo antes de programar).
* `14` materializa el árbol de archivos final para implementación.
* `16` consolida trazabilidad RF/VR↔código y prioriza brechas para ejecución incremental.

* `17` cierra auditoría operativa y reporte administrativo desacoplado sobre DB queue.
* `18` formaliza ACID, límites transaccionales y decisiones de consistencia del backend.
* `19` traduce el estado real del backend a implicaciones de UX, navegación, permisos y arquitectura frontend.
* `20` endurece seguridad operativa con controles concretos de abuso, headers web y ownership.
* `21` consolida la narrativa de refresh token y repositorio documental desacoplado.
* `22` sintetiza la hoja de ruta de aprendizaje y madurez backend.
* `23` y `24` aterrizan el trabajo de mantenimiento y observabilidad.
* `25` y `26` empujan el backend hacia seguridad y performance realistas.
* `27` y `35` explican integraciones, storage, colas y arquitectura distribuida sin sobredisenar.
* `28` y `34` cubren despliegue, DevOps e infraestructura.
* `29` fija disciplina de datos y cambios de esquema.
* `30` deja la estrategia de pruebas.
* `31` da criterio de patrones.
* `32` y `33` cierran la perspectiva de trabajo autónomo y habilidades complementarias.
* `36` abre la perspectiva de integración con IA desde producto y arquitectura.
* `37` cubre producto web, landing y pagos desde la programación.
* `38` organiza el mapa de estilos de API y los conecta con una ruta de aprendizaje técnica.
* `39` baja teoría de algoritmos y estructuras de datos a fallos concretos del backend.
* `40` profundiza en tiempo, concurrencia y coordinación sobre recursos compartidos.
* `41` abre el panorama de backends conectados a hardware, protocolos IoT y modelado por estado.

---

## 8. Checklist de cierre del paquete (antes de programar)

Marca como “cerrado” cuando se cumpla:

* [ ] Los documentos **no referencian archivos que no existan** (o están marcados explícitamente como “pendiente”).
* [ ] `06` (endpoints) coincide con:

 * `13` (DTOs y permisos)
 * `08` (whitelists de filtros/sort)
* [ ] `05` (contrato) coincide con:

 * `07` (códigos y excepciones)
 * `09` (401/403 JSON)
 * `10` (async/polling)
* [ ] Se decidió y quedó uniforme:

 * campos exactos de `PageResponseDto` (**elegir 1:** `items` o `content`)
 * formato exacto de códigos de error (y catálogo mínimo estable)
 * catálogo mínimo de roles (por lo menos `ADMIN` y `SECRETARIA`) y su política base
* [ ] `09` describe `.properties` (no YAML) y coincide con `14`.

---

## 9. Próximo paso recomendado

En orden de impacto (corrige lo estructural primero):

1. `02` arquitectura
2. `04` módulos
3. `06` endpoints
4. `07` reglas/errores
5. `08` consultas
6. `09` seguridad/Swagger/Docker
7. `10` reportes DB queue
8. `05` contrato (revisión final transversal)
9. `13` (cierre de DTOs/permisos/whitelists)
10. `14` (árbol final, solo ajuste si algo cambió)

---

## 10. Cierre

Este índice deja organizado el paquete **Backend V1** para que puedas implementar sin improvisar:

* qué documentos existen,
* qué decide cada uno,
* cómo se relacionan,
* y qué debe quedar uniforme antes de escribir código.



