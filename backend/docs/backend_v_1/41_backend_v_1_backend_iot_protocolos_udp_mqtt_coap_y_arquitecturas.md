# 41_backend_v_1_backend_iot_protocolos_udp_mqtt_coap_y_arquitecturas

- Versión: 1.0
- Estado: Vigente
- Ámbito: backend conectado a dispositivos IoT, protocolos, arquitecturas y patrones
- Relacionado con:
 - `27_backend_v_1_integraciones_externas_y_storage_providers.md`
 - `35_backend_v_1_mensajeria_eventos_colas_rabbitmq_kafka_cqrs_y_cdn.md`
 - `38_backend_v_1_tipos_de_apis_rest_websocket_sse_graphql_grpc_webhooks_y_roadmap.md`
 - `40_backend_v_1_concurrencia_locks_transacciones_y_condiciones_de_carrera.md`

---

## 1. Propósito

Este documento introduce una pregunta muy interesante porque te obliga a salir del backend administrativo clásico:

"¿Qué pasa cuando el backend ya no conversa solo con apps web o desktop, sino con sensores, cámaras, actuadores o gateways?"

En ese momento cambian varias cosas al mismo tiempo:

- el tipo de cliente
- la calidad de red
- el costo energético
- el protocolo
- la latencia tolerable
- la forma de modelar estado

Y también cambia la arquitectura correcta.

---

## 2. Qué entendemos por IoT en este contexto

Aquí usamos IoT en sentido amplio:

- sensores
- medidores
- actuadores
- dispositivos embebidos
- cámaras
- gateways
- controladores

No todos estos dispositivos se comportan igual.

Una cámara IP no tiene las mismas restricciones que un sensor a batería.

Por eso, un backend IoT serio empieza distinguiendo tipos de dispositivo.

---

## 3. Diferencia entre backend administrativo y backend IoT

### Backend administrativo típico

Suele optimizar para:

- formularios
- CRUD
- usuarios humanos
- consistencia transaccional
- contratos HTTP claros

### Backend IoT

Suele optimizar para:

- ingestión de telemetría
- eventos frecuentes
- dispositivos poco confiables
- conectividad intermitente
- protocolos livianos o especializados
- control de estado y firmware

Esto no significa que uno reemplace al otro.

Muchas soluciones reales tienen ambos:

- un backend operativo de negocio
- un backend o subsistema de ingestión para dispositivos

---

## 4. Preguntas que cambian la arquitectura

Antes de elegir tecnologías, debes responder:

1. ¿el dispositivo envía datos, recibe órdenes o ambas cosas?
2. ¿hay conexión continua o intermitente?
3. ¿importa más confiabilidad, latencia o consumo energético?
4. ¿el volumen es bajo, medio o masivo?
5. ¿los mensajes son críticos o toleran pérdida?
6. ¿el dispositivo puede hablar TCP o solo UDP?
7. ¿hay gateway intermedio o conexión directa a internet?

Esas preguntas importan más que el nombre del protocolo.

---

## 5. Protocolos más comunes

## 5.1 HTTP / REST

### Cuándo aparece

- dispositivos relativamente potentes
- gateways
- configuración
- administración

### Ventajas

- simple de integrar
- tooling enorme
- muy bueno para APIs de control o configuración

### Desventajas

- más pesado para dispositivos limitados
- menos ideal para telemetría continua de bajo consumo

### Uso realista

HTTP suele convivir con otros protocolos. Muchas veces se usa para:

- registro del dispositivo
- configuración
- administración
- descarga de firmware o metadata

---

## 5.2 MQTT

### Qué es

Protocolo publish/subscribe muy usado en IoT.

### Qué resuelve bien

- telemetría ligera
- dispositivos con conexión inestable
- desacople entre productor y consumidor

### Ventajas

- liviano
- modelo de tópicos muy natural
- bueno para sensores y gateways

### Riesgos

- mala gobernanza de tópicos si no diseñas bien
- seguridad insuficiente si se improvisa
- riesgo de convertir todo en eventos sin semántica clara

### Dónde encaja muy bien

- sensores
- medidores
- gateways
- telemetría ambiental

---

## 5.3 CoAP

### Qué es

Protocolo ligero orientado a dispositivos limitados, típicamente sobre UDP.

### Qué resuelve bien

- entornos restringidos
- dispositivos con muy pocos recursos
- comunicación más eficiente que HTTP en ciertos contextos

### Ventajas

- muy liviano
- bueno para redes con restricciones

### Desventajas

- menos tooling generalista que HTTP
- más complejidad para muchos equipos backend tradicionales

### Cuándo interesa

Cuando de verdad estás más cerca de IoT embebido serio que de un dispositivo IP relativamente cómodo.

---

## 5.4 UDP puro

### Qué es

UDP no garantiza:

- entrega
- orden
- no duplicación

Pero es rápido y liviano.

### Cuándo se usa

- discovery
- broadcasting local
- streaming con tolerancia a pérdida
- protocolos propios

### Riesgo principal

Si usas UDP, muchas garantías pasan de la red a la aplicación.

Eso significa que tu diseño debe decidir:

- cómo detectar pérdida
- cómo detectar duplicados
- cómo reordenar
- cuándo reintentar

UDP mal entendido produce sistemas aparentemente rápidos y operativamente muy frágiles.

---

## 5.5 TCP

TCP ofrece:

- conexión
- orden
- retransmisión
- confiabilidad mayor que UDP

A cambio de:

- más overhead
- mayor complejidad de conexión
- más sensibilidad a ciertas condiciones de red

Muchos protocolos IoT "altos" realmente corren encima de TCP:

- MQTT clásico
- HTTP
- RTSP en ciertos escenarios

---

## 5.6 WebSocket

Puede aparecer cuando:

- un gateway o cliente más potente necesita canal persistente
- hay comandos y eventos bidireccionales

No suele ser mi primera recomendación para dispositivos muy limitados, pero sí puede tener sentido entre backend y gateway o panel de monitoreo.

---

## 5.7 Protocolos de cámaras

Las cámaras no son simplemente "otro sensor".

Suelen introducir protocolos como:

- RTSP para streaming
- RTP/RTCP para transporte multimedia
- ONVIF para descubrimiento, control e interoperabilidad
- HTTP para configuración o snapshots

Aquí el backend no suele "hablar video crudo" como si fuera telemetría simple. Normalmente se apoya en:

- NVR
- gateways de video
- servicios especializados de ingestión

---

## 5.8 Protocolos industriales

En entornos industriales aparecen además:

- Modbus
- OPC UA
- CAN en ciertos dominios
- protocolos propietarios

Muchas veces el backend de negocio no habla directamente estos protocolos.

Lo habitual es que haya:

- PLC
- gateway industrial
- traductor de protocolo
- capa de ingestión

---

## 6. Arquitecturas típicas

## 6.1 Dispositivo -> backend directo

### Cuándo sirve

- pocos dispositivos
- conectividad razonable
- hardware competente

### Ventajas

- simple conceptualmente

### Desventajas

- acoplamiento mayor
- escalado menos flexible
- más superficie expuesta

---

## 6.2 Dispositivo -> gateway -> backend

Es una arquitectura muy común y muy sensata.

### El gateway puede encargarse de:

- traducir protocolos
- agrupar mensajes
- autenticar localmente
- reenviar datos
- aplicar buffering
- operar offline temporalmente

### Ventaja clave

El backend deja de depender de que cada dispositivo individual hable "idioma cloud".

---

## 6.3 Ingestión separada del core de negocio

Esta arquitectura divide:

1. capa de ingestión de dispositivos
2. capa de procesamiento de eventos
3. backend de negocio y administración

Esto suele ser sano porque:

- los perfiles de carga son distintos
- la persistencia puede ser distinta
- el ritmo de mensajes no se parece al ritmo del CRUD

---

## 6.4 Event-driven backend

Muy común en IoT porque:

- llegan eventos
- cambian estados
- se activan reglas

Ejemplo:

1. sensor publica lectura
2. backend la ingiere
3. se evalúa una regla
4. se genera alarma
5. se notifica a otra capa

Este tipo de arquitectura conversa muy bien con:

- colas
- brokers
- almacenamiento de series temporales

---

## 7. Patrones de diseño típicos en backends IoT

## 7.1 Ports and Adapters

Muy importante para desacoplar:

- protocolo
- proveedor de conectividad
- broker
- almacenamiento

Ejemplo:

- puerto para publicar comandos
- adapter MQTT
- adapter HTTP
- adapter CoAP

## 7.2 Adapter

Clásico cuando distintos dispositivos o gateways hablan protocolos distintos.

## 7.3 Strategy

Útil para elegir:

- parser por tipo de payload
- codificador por dispositivo
- política de reintento
- canal de entrega

## 7.4 Factory

Muy útil cuando debes crear manejadores por:

- modelo de dispositivo
- firmware
- familia de protocolo

## 7.5 State Machine

Este patrón es crucial en IoT.

Porque tanto backend como dispositivo suelen vivir en transiciones de estado.

Ejemplos:

- `REGISTRADO -> ACTIVO -> OFFLINE -> MANTENIMIENTO`
- `PENDIENTE_FIRMWARE -> DESCARGANDO -> APLICANDO -> CONFIRMADO`
- `ALARMA_INACTIVA -> ALARMA_ACTIVA -> ACK -> RESUELTA`

## 7.6 Observer / Publish-Subscribe

Muy natural cuando:

- muchos consumidores reaccionan a telemetría o alarmas
- el productor no debe conocer a todos los receptores

## 7.7 Circuit Breaker / Retry Policy

Importantes cuando el backend conversa con gateways o servicios intermedios.

## 7.8 Digital Twin o modelo de estado lógico

En muchos sistemas IoT se mantiene una representación lógica del dispositivo en backend:

- estado deseado
- estado reportado
- última conexión
- versión de firmware
- capacidades

Eso no siempre se llama "digital twin" formalmente, pero conceptualmente lo es.

---

## 8. Patrones y modelos típicos en los dispositivos terminales

Aquí aparece algo que conecta muy bien con lo que viste en sistemas digitales.

Los dispositivos terminales muchas veces sí se modelan con lógica y máquinas de estado.

## 8.1 Máquina de estados finitos (FSM)

Muy común en:

- sensores
- actuadores
- cámaras
- firmware embebido

Porque el dispositivo suele tener pocos estados bien definidos y transiciones claras.

Ejemplos:

- `BOOT`
- `INIT`
- `IDLE`
- `SAMPLING`
- `TRANSMITTING`
- `ERROR`
- `SLEEP`

Esto es extremadamente natural en firmware.

## 8.2 Event loop

Muchos dispositivos pequeños trabajan con un bucle principal que:

1. lee entradas
2. actualiza estado
3. decide salidas
4. duerme o espera interrupción

## 8.3 Interrupt-driven design

Muy típico cuando el hardware reacciona a:

- timer
- sensor
- botón
- cambio eléctrico

## 8.4 Tablas de transición

Sí, aquí aparecen cosas parecidas a:

- tabla de verdad
- tabla de transición
- combinaciones válidas
- condiciones "don't care"

### Importancia práctica

En sistemas embebidos, una tabla de transición o una FSM bien diseñada evita:

- estados imposibles
- ramificaciones caóticas
- errores por combinaciones no contempladas

### Relación con "don't care"

El concepto de "don't care" te ayuda a simplificar lógica cuando ciertas combinaciones:

- no ocurren
- no importan
- se consideran inválidas

Eso es muy útil en diseño digital y sigue teniendo eco en firmware y control.

Pero cuidado:

en backend de negocio no debes abusar del "don't care" como excusa para ignorar estados. En backend suele convenir modelar explícitamente lo importante.

## 8.5 Debouncing

Muy típico en entradas físicas.

Sirve para filtrar ruido o rebotes en señales.

## 8.6 Ring buffers

Muy comunes para:

- telemetría local
- logs del dispositivo
- serial
- audio o video

## 8.7 Watchdog

Mecanismo de supervivencia del dispositivo para reiniciarse si entra en bloqueo.

---

## 9. Arquitectura lógica recomendada para backend IoT

Una arquitectura bastante sana suele tener:

1. registry de dispositivos
2. autenticación y credenciales por dispositivo
3. canal de ingestión
4. parser y normalización
5. validación y enriquecimiento
6. event bus o cola
7. almacenamiento apropiado
8. capa de reglas o alarmas
9. API administrativa para humanos

### Posibles almacenes

- relacional para catálogo y administración
- time-series DB para telemetría
- object storage para archivos o video
- broker para eventos

No todo debe ir en la misma tabla relacional si el volumen o la naturaleza del dato ya no encaja.

---

## 10. Seguridad en IoT

Este punto es crítico porque el dispositivo terminal suele ser más expuesto y más débil que un navegador moderno.

Debes pensar en:

1. identidad por dispositivo
2. credenciales rotables
3. TLS cuando el contexto lo permita
4. firma o validación de mensajes
5. control de replay
6. firmware confiable
7. segmentación de red

La seguridad en IoT no se resuelve solo con "poner token".

---

## 11. Qué puede salir mal si diseñas mal un backend IoT

### Error 1

Usar HTTP pesado para dispositivos demasiado limitados.

### Error 2

Usar UDP sin diseñar recuperación, orden o duplicados.

### Error 3

Mezclar telemetría cruda con el core transaccional del negocio sin separación.

### Error 4

No modelar estado del dispositivo.

### Error 5

No distinguir estado reportado del estado deseado.

### Error 6

No pensar en reconexión, buffering u operación offline.

### Error 7

Exponer directamente los dispositivos a internet cuando un gateway sería mucho más sano.

---

## 12. Aplicado a ejemplos concretos

## 12.1 Sensores pequeños

Lo normal es priorizar:

- bajo consumo
- mensajes pequeños
- tolerancia a desconexión

Aquí suelen encajar mejor:

- MQTT
- CoAP
- gateways
- FSMs sencillas

## 12.2 Cámaras de seguridad

Aquí cambian las prioridades:

- video
- control
- streaming
- grabación

Es común ver:

- RTSP / RTP
- ONVIF
- gateways o NVR
- backend administrativo separado del pipeline multimedia

## 12.3 Gateways industriales

Suelen actuar como traductores entre:

- campo / hardware
- red local
- backend central

Aquí importan mucho:

- adapters
- colas
- buffering
- mapeo de protocolos

---

## 13. Qué deberías estudiar primero si te interesa IoT desde backend

La secuencia razonable sería:

1. redes básicas: TCP, UDP, puertos, latencia, pérdida
2. HTTP y REST
3. MQTT y pub/sub
4. WebSocket y SSE
5. conceptos de gateways
6. colas y brokers
7. series temporales
8. seguridad de dispositivos
9. firmware y FSMs a nivel conceptual

No necesitas convertirte primero en ingeniero electrónico, pero sí conviene respetar que aquí el hardware impone parte del diseño.

---

## 14. Roadmap tecnológico sugerido

### Etapa 1: fundamentos

- TCP/IP básico
- UDP
- HTTP
- JSON
- TLS

### Etapa 2: protocolos de mensajería

- MQTT
- QoS
- tópicos
- brokers

Tecnologías o entornos útiles:

- Mosquitto
- EMQX
- HiveMQ Community

### Etapa 3: arquitectura backend

- Spring Boot para APIs administrativas
- brokers o colas
- almacenamiento de telemetría
- reglas y alertas

### Etapa 4: dispositivos y edge

- gateways
- FSMs
- firmware update
- buffering offline
- watchdogs

### Etapa 5: dominios especializados

- video
- industrial
- series temporales
- digital twin

---

## 15. Qué debes llevarte de este documento

Primero:

un backend IoT no se diseña igual que un CRUD administrativo.

Segundo:

los dispositivos terminales suelen usar modelos mucho más cercanos a:

- máquinas de estado
- lógica de transición
- bucles de evento
- interrupciones

Tercero:

el backend que conversa con ellos necesita:

- adapters
- estrategias
- colas
- modelado de estado
- tratamiento explícito de conectividad imperfecta

---

## 16. Cierre

Cuando el backend conversa con hardware, la arquitectura deja de ser solo una discusión de endpoints.

Pasa a ser una discusión sobre:

- red
- protocolos
- confiabilidad
- estado
- energía
- latencia
- sincronización

Y ahí es donde conceptos de sistemas digitales, máquinas de estado y tablas de transición vuelven a tener sentido de manera muy práctica.

---

## 17. Siguiente lectura

- `38_backend_v_1_tipos_de_apis_rest_websocket_sse_graphql_grpc_webhooks_y_roadmap.md`

