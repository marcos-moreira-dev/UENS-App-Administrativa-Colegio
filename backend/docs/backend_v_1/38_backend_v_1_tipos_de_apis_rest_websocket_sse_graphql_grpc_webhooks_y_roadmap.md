# 38_backend_v_1_tipos_de_apis_rest_websocket_sse_graphql_grpc_webhooks_y_roadmap

- Versión: 1.0
- Estado: Vigente
- Ámbito: tipologías de API, criterios de uso y ruta de aprendizaje
- Relacionado con:
 - `05_backend_v_1_diseno_api_contrato_respuestas_y_errores.md`
 - `06_backend_v_1_api_endpoints_y_casos_de_uso.md`
 - `27_backend_v_1_integraciones_externas_y_storage_providers.md`
 - `35_backend_v_1_mensajeria_eventos_colas_rabbitmq_kafka_cqrs_y_cdn.md`

---

## 1. Propósito

Este documento responde una duda que aparece tarde o temprano en cualquier formación backend seria:

"Además de REST, ¿qué otros tipos de API existen, cuándo tienen sentido y en qué orden debería aprenderlos?"

La respuesta corta es esta:

- no todas las APIs sirven para el mismo problema
- cada estilo de comunicación tiene costos mentales y operativos
- aprenderlas todas a la vez no te hace mejor ingeniero
- aprender el criterio para elegirlas sí

---

## 2. Antes de hablar de tecnologías: qué cambia entre una API y otra

Cuando comparas REST, WebSocket, SSE, GraphQL o gRPC, en el fondo estás comparando varias decisiones a la vez:

1. quién inicia la comunicación
2. si la comunicación es de una sola respuesta o persistente
3. si el canal es texto o binario
4. si el cliente pide exactamente recursos o formula consultas más libres
5. si la comunicación es síncrona, asíncrona o por eventos
6. qué tan fácil es observar, depurar y versionar el contrato

Por eso no conviene estudiar estas tecnologías como una colección de nombres. Conviene estudiarlas como respuestas distintas a problemas distintos.

---

## 3. REST

### 3.1 Qué es

REST es el estilo más común en aplicaciones de negocio.

Normalmente se expresa con:

- HTTP
- rutas por recurso
- métodos como `GET`, `POST`, `PUT`, `PATCH`, `DELETE`
- respuestas JSON

### 3.2 Qué resuelve bien

REST funciona muy bien para:

1. CRUD administrativo
2. listados paginados
3. formularios
4. seguridad basada en JWT o sesión
5. integraciones relativamente simples

### 3.3 Ventajas

- fácil de entender
- fácil de probar con Postman o curl
- buena compatibilidad con proxies, logs y herramientas web
- buen encaje con contratos estables y recursos de negocio

### 3.4 Limitaciones

- no es ideal para push en tiempo real
- puede producir overfetching o underfetching si el diseño es pobre
- no resuelve por sí solo eventos asíncronos

### 3.5 Aplicado a UENS

REST es el canal correcto y principal para este sistema.

UENS es un backend administrativo con:

- usuarios
- módulos operativos
- listados
- filtros
- formularios
- reportes

Eso encaja de forma natural en REST.

---

## 4. Polling y long polling

Aunque a veces no se enseñen como "tipo de API" en sentido estricto, sí son patrones de comunicación muy importantes.

### 4.1 Polling

El cliente pregunta periódicamente:

- "¿ya terminó?"
- "¿cuál es el estado?"

Es exactamente lo que UENS hace con reportes asíncronos.

### 4.2 Long polling

El cliente hace un request que el servidor retiene hasta tener novedad o hasta que expire un timeout.

Es un punto intermedio entre REST clásico y comunicación más reactiva.

### 4.3 Cuándo sirve

- procesamiento asíncrono
- tareas largas
- estados que no justifican un canal persistente

### 4.4 Riesgos

- exceso de requests
- mala experiencia si se elige un intervalo absurdo
- carga innecesaria si muchos clientes consultan a la vez

---

## 5. Webhooks

### 5.1 Qué son

Un webhook no es una API de consumo interactivo para la UI.

Es una notificación HTTP entre sistemas.

Ejemplo:

- un proveedor de pagos le avisa a tu backend que un pago fue confirmado
- un proveedor de correo avisa rebotes
- un servicio externo avisa que terminó un procesamiento

### 5.2 Qué resuelven bien

- integración entre sistemas
- eventos asíncronos
- evitar polling innecesario hacia terceros

### 5.3 Requisitos serios

Si implementas webhooks de verdad, debes dominar:

1. validación de firma
2. idempotencia
3. reintentos
4. trazabilidad del evento recibido
5. reconciliación posterior

### 5.4 Cuándo aprenderlos

Temprano.

No porque sean complejos, sino porque aparecen mucho antes que GraphQL o gRPC en proyectos reales.

---

## 6. SSE (Server-Sent Events)

### 6.1 Qué es

SSE permite que el servidor empuje eventos al cliente por HTTP, normalmente en un solo sentido:

- servidor -> cliente

### 6.2 Cuándo tiene sentido

Es útil cuando necesitas:

- notificaciones ligeras
- barra de progreso
- actualización de estado en vivo
- feed de eventos sencillo

### 6.3 Ventajas

- más simple que WebSocket
- muy bueno si solo necesitas push del servidor al cliente
- sigue siendo bastante amistoso con HTTP

### 6.4 Desventajas

- no es bidireccional real
- no todos los proxies o entornos viejos lo manejan igual de bien
- exige pensar en reconexión y timeouts

### 6.5 Aplicado a UENS

Si algún día quisieras mejorar el seguimiento de reportes sin saltar a WebSocket, SSE sería una evolución mucho más razonable que GraphQL o gRPC.

---

## 7. WebSocket

### 7.1 Qué es

WebSocket abre un canal persistente y bidireccional:

- cliente -> servidor
- servidor -> cliente

### 7.2 Qué resuelve bien

- chats
- colaboración en vivo
- notificaciones frecuentes
- dashboards en tiempo real
- presencia o estados activos

### 7.3 Ventajas

- baja latencia
- comunicación continua
- muy bueno para eventos en tiempo real

### 7.4 Costos

- mayor complejidad operativa
- más estado por conexión
- más cuidado con escalabilidad, autenticación y reconexión
- más complejidad al pasar por proxies o balanceadores

### 7.5 Cuándo no usarlo

No lo uses solo porque "suena moderno".

Si el problema se resuelve con:

- REST
- polling razonable
- SSE

entonces WebSocket puede ser un sobrecosto innecesario.

### 7.6 Aplicado a UENS

Hoy no es prioridad.

Podría tener sentido en el futuro para:

- notificaciones administrativas en vivo
- monitoreo de workers
- alertas operativas

Pero no es el siguiente paso natural.

---

## 8. GraphQL

### 8.1 Qué es

GraphQL permite que el cliente formule consultas más flexibles sobre un esquema:

- pide exactamente los campos que necesita
- puede navegar relaciones en una sola consulta

### 8.2 Qué resuelve bien

- frontends complejos con muchas pantallas y combinaciones de datos
- clientes múltiples con necesidades muy distintas
- agregación de datos sobre muchos recursos

### 8.3 Ventajas

- flexibilidad de consulta
- puede reducir overfetching
- contrato fuertemente tipado a nivel de schema

### 8.4 Riesgos

- consultas demasiado pesadas si no las limitas
- complejidad de autorización por campo o relación
- más dificultad para cachear y observar que un REST simple
- mayor riesgo si el equipo todavía no domina bien modelado de consultas

### 8.5 Cuándo sí aprenderlo

Cuando ya dominas REST y quieres entender otra forma seria de exposición de datos.

### 8.6 Aplicado a UENS

No lo pondría como prioridad.

UENS es un monolito modular administrativo. Su problema hoy no es flexibilidad extrema de consultas del cliente. Su problema útil es operar mejor lo que ya tiene.

---

## 9. gRPC

### 9.1 Qué es

gRPC es un sistema RPC moderno que suele usar:

- HTTP/2
- Protocol Buffers
- contratos fuertemente tipados
- payload binario

### 9.2 Qué resuelve bien

- comunicación interna entre servicios
- baja latencia
- contratos muy estrictos
- streaming eficiente

### 9.3 Ventajas

- alto rendimiento
- contratos claros
- generación de clientes y servidores
- muy bueno para comunicación backend-backend

### 9.4 Desventajas

- menos natural para navegador que REST
- debugging menos amistoso al inicio
- exige aprender Protobuf y tooling adicional

### 9.5 Aplicado a UENS

No es prioridad mientras el sistema siga siendo un monolito modular.

Si algún día hubiera varios servicios internos separados y una necesidad clara de comunicación eficiente entre ellos, entonces sí entraría en conversación.

---

## 10. RPC clásico y APIs internas

Aunque hoy se hable mucho de REST o GraphQL, conceptualmente muchas APIs internas siguen siendo RPC:

- "generaReporte"
- "reintentaSolicitud"
- "renuevaToken"

Esto importa por una razón pedagógica:

no toda API tiene que fingir ser CRUD puro.

Algunas operaciones son claramente acciones o comandos, y es mejor reconocerlo que disfrazarlo mal.

---

## 11. Comparación conceptual rápida

### REST

- mejor para CRUD y negocio administrativo
- simple de operar
- excelente primera API a dominar

### Polling / long polling

- útil para tareas largas y estados
- simple conceptualmente
- puede volverse ruidoso si se usa mal

### Webhooks

- muy importantes para integraciones reales
- obligan a pensar en idempotencia y seguridad

### SSE

- muy bueno para push unidireccional
- menos complejo que WebSocket

### WebSocket

- ideal para tiempo real bidireccional
- más caro de operar

### GraphQL

- útil cuando el cliente necesita mucha flexibilidad de consulta
- más complejo que REST para gobernar

### gRPC

- muy fuerte para backend-backend
- poco prioritario en un monolito administrativo

---

## 12. Qué debería aprender primero un backend engineer

La secuencia sensata, en la mayoría de contextos, es esta:

1. HTTP serio
2. REST bien diseñado
3. autenticación y autorización sobre REST
4. webhooks
5. polling y tareas asíncronas
6. SSE o WebSocket
7. GraphQL
8. gRPC

Esa secuencia no es un dogma, pero sí evita aprender herramientas sofisticadas antes de dominar el canal que más usarás.

---

## 13. Roadmap de estudio recomendado

### Etapa 1: fundamentos obligatorios

Debes dominar:

- HTTP
- métodos y códigos de estado
- headers
- idempotencia
- JSON
- OpenAPI / Swagger

Tecnologías útiles:

- Spring Web / Spring MVC
- Jackson
- Postman o Insomnia
- curl
- Swagger UI / springdoc-openapi

### Etapa 2: REST profesional

Debes dominar:

- diseño de recursos
- paginación
- filtros
- errores consistentes
- seguridad JWT
- versionado y compatibilidad

Tecnologías útiles:

- Spring Boot
- Spring Security
- Bean Validation
- tests de integración HTTP

### Etapa 3: asincronía e integración

Debes dominar:

- webhooks
- polling
- reintentos
- idempotencia
- firmas y validación de eventos

Tecnologías útiles:

- Spring MVC o WebFlux
- ngrok o similar para pruebas locales
- colas simples, Redis o brokers según el caso

### Etapa 4: tiempo real

Debes dominar:

- SSE
- WebSocket
- reconexión
- heartbeats
- control de sesiones activas

Tecnologías útiles:

- Spring WebSocket
- STOMP si quieres una capa de mensajería simple
- WebFlux para SSE

### Etapa 5: APIs más especializadas

Debes explorar:

- GraphQL
- gRPC
- Protobuf
- streaming

Tecnologías útiles:

- Spring for GraphQL
- graphql-java
- gRPC Java
- protoc / Protocol Buffers

---

## 14. Recomendación concreta para tu proyecto

Para UENS, mi recomendación es muy clara:

### Hoy

- REST como canal principal
- polling para reportes
- webhooks si algún día integras pagos o proveedores externos

### Después

- SSE si quieres estados o notificaciones en vivo con menor complejidad

### Mucho después

- WebSocket si de verdad aparece un caso fuerte de tiempo real
- GraphQL o gRPC solo si la arquitectura y el producto lo justifican

---

## 15. Preguntas de examen mental

Si quieres estudiar este tema con mentalidad universitaria, intenta responder sin mirar el documento:

1. ¿Qué diferencia estructural hay entre pedir datos y recibir eventos?
2. ¿Por qué WebSocket no reemplaza REST?
3. ¿Por qué un webhook exige idempotencia?
4. ¿Qué ganas y qué pierdes al pasar de JSON a Protobuf?
5. ¿Por qué GraphQL puede complicar seguridad y performance?
6. ¿Qué estilo encaja mejor con un sistema administrativo como UENS y por qué?

Si puedes responder esas seis preguntas con ejemplos, ya tienes criterio, no solo definiciones.

---

## 16. Cierre

Aprender tipos de API no es coleccionar protocolos.

Es aprender a relacionar:

- forma de comunicación
- necesidad del producto
- costo operativo
- observabilidad
- seguridad

Ese criterio vale mucho más que decir "sé REST, GraphQL, WebSocket y gRPC" sin saber cuándo usar cada uno.

---

## 17. Siguiente lectura

- `39_backend_v_1_algoritmos_y_estructuras_de_datos_riesgos_backend.md`

