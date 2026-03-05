# 36_backend_v_1_integracion_ia_modelos_locales_y_en_linea

- Version: 1.0
- Estado: Vigente
- Ámbito: integración de IA en producto, backend y operaciones
- Relacionado con:
  - `24_backend_v_1_observabilidad_logs_metricas_alertas.md`
  - `27_backend_v_1_integraciones_externas_y_storage_providers.md`
  - `31_backend_v_1_patrones_diseno_usados_con_criterio.md`
  - `35_backend_v_1_mensajeria_eventos_colas_rabbitmq_kafka_cqrs_y_cdn.md`

---

## 1. Propósito

Este documento te da una guía realista para integrar IA en un sistema sin convertir el backend en una caja negra difícil de operar.

La idea central es esta:

- la IA no reemplaza diseño
- la IA no reemplaza validación
- la IA es otra integración técnica
- debe entrar por contratos claros y medibles

---

## 2. La pregunta correcta antes de usar IA

No empieces por:

- "qué modelo está de moda"
- "qué proveedor es más famoso"

Empieza por:

- "qué problema resuelvo"
- "qué parte del flujo mejora"
- "qué riesgo nuevo introduzco"
- "cómo voy a medir si sirvió"

Ejemplos de problemas válidos:

1. resumir auditorias largas
2. clasificar tickets o incidencias
3. extraer campos de documentos
4. responder preguntas sobre manuales internos
5. sugerir texto inicial para observaciones o reportes

Ejemplos pobres:

1. poner un chat solo por marketing
2. dejar que el modelo tome decisiones críticas sin revisión
3. reemplazar validaciones del backend con una respuesta del modelo

---

## 3. Tres formas de consumo

### 3.1 Modelos en línea

Son modelos accesibles por API remota.

Ventajas:

- arranque rápido
- buena calidad general
- sin administrar GPUs propias
- escalado más simple al inicio

Desventajas:

- coste por uso
- dependencia de internet
- dependencia del proveedor
- políticas de datos externas

Buenos casos:

- prototipos
- copilots internos
- clasificación y resumen
- extracción estructurada

### 3.2 Modelos locales

Son modelos que corres en tu propio equipo o servidor.

Ventajas:

- mayor control técnico
- mejor posición para datos sensibles
- laboratorio offline
- posibilidad de costos más predecibles si ya tienes hardware

Desventajas:

- administración propia
- consumo fuerte de RAM o VRAM
- calidad variable según modelo
- operación más compleja

Buenos casos:

- laboratorios internos
- datos sensibles
- entornos sin internet
- pruebas controladas de arquitectura

### 3.3 Esquema híbrido

Combina ambos.

Ejemplo:

- modelo online para alta calidad general
- modelo local para pruebas, respaldo o datos internos

Es la opción más corporativa cuando quieres desacople real.

---

## 4. Casos de uso que sí tienen sentido

### Generación

- borradores de texto
- resúmenes
- respuestas guiadas

### Clasificación

- categorizar incidencias
- priorizar tickets
- detectar tema principal

### Extracción estructurada

- convertir texto o documento en JSON validable
- detectar entidades
- mapear formularios a campos internos

### Recuperación asistida

- preguntas sobre manuales
- ayuda interna sobre políticas
- buscador semántico

### Automatización supervisada

- sugerencias para operador humano
- prellenado de datos con confirmación posterior

No uses IA como reemplazo de:

- reglas de negocio
- autorización
- validaciones críticas
- decisiones financieras finales

---

## 5. Arquitectura recomendada

## 5.1 Regla principal

Tu `application` no debe depender del SDK crudo del proveedor.

Debe depender de un puerto propio.

### Ejemplo de puerto correcto

- `AiTextGenerationPort`
- `AiClassificationPort`
- `AiEmbeddingPort`

### Ejemplo de puerto incorrecto

- `OpenAiSdkWrapper`
- `ProveedorXChatCompletionDirectService`

Los nombres deben expresar capacidad de negocio, no tecnología puntual.

## 5.2 Capas típicas

1. controlador o scheduler
2. servicio de aplicación
3. puerto de IA
4. adapter online o local
5. políticas de fallback y observabilidad

## 5.3 Patrones que si suelen servir

### Port and Adapter

Te permite cambiar proveedor sin romper `application`.

### Strategy

Sirve para seleccionar proveedor por configuración, por feature o por entorno.

### Facade

Sirve para ocultar complejidad de prompts, embeddings, vector store y parsing.

### Template Method o prompt templates

Sirve para estandarizar instrucciones y versionar prompts.

### Queue o procesamiento asíncrono

Útil para tareas pesadas:

- generación larga
- indexacion documental
- enriquecimiento batch

No metas patrones por decoracion.

---

## 6. Componentes que suelen aparecer

### 6.1 Prompt catalog

Repositorio controlado de prompts o plantillas.

Sirve para:

- versionar instrucciones
- mantener consistencia
- comparar resultados entre cambios

### 6.2 Output validator

Valida que la salida tenga la forma esperada.

Ejemplos:

- JSON válido
- longitud maxima
- campos requeridos
- categorias permitidas

### 6.3 Usage tracker

Registra:

- feature
- usuario
- proveedor
- tokens o consumo
- latencia
- resultado

### 6.4 Safety layer

Aplica:

- sanitizacion
- redaccion de PII
- reglas de contenido
- bloqueo de solicitudes peligrosas

### 6.5 Fallback policy

Define que pasa si el modelo falla:

- reintentar
- usar otro proveedor
- devolver respuesta degradada
- pasar a revisión humana

---

## 7. Modelos locales: que debes entender

No necesitas casarte con una herramienta unica.

Lo que debes entender es el tipo de stack.

### 7.1 Runtime local

Ejemplos de familia:

- motores locales sencillos para desarrollo
- servidores HTTP de inferencia
- runtimes de GPU más avanzados

Lo importante es evaluar:

1. requisitos de hardware
2. facilidad de despliegue
3. API o protocolo disponible
4. soporte para embeddings
5. facilidad para automatizar desde backend

### 7.2 Coste real del modelo local

No es gratis solo por no pagar API.

Debes contar:

- RAM
- VRAM
- CPU o GPU
- tiempo de operación
- energia
- mantenimiento del host

### 7.3 Donde si encaja un modelo local

- pruebas de arquitectura
- laboratorio de prompts
- asistentes internos no críticos
- indexacion y embeddings locales

### 7.4 Donde no es buena idea al inicio

- cargas pesadas con pocos recursos
- features de cara al cliente sin pruebas
- casos donde necesitas SLA fuerte sin equipo de operación

---

## 8. Modelos en linea: que debes mirar

No elijas solo por hype.

Mira al menos:

1. precio por uso
2. limites de tasa
3. latencia
4. contexto maximo
5. calidad de salida estructurada
6. políticas de retención de datos
7. regiones y cumplimiento
8. capacidad de moderacion o safety
9. estabilidad del contrato API

Buenas preguntas antes de elegir:

- puedo exigir JSON o salida estructurada
- tiene mecanismos de tool use o function calling
- puedo medir costo por feature
- puedo cambiar de proveedor sin reescribir media app

---

## 9. Prompting serio

Prompting no es magia.

Es diseño de interfaz textual con restricciones.

Buenas prácticas:

1. define rol y tarea
2. da contexto mínimo suficiente
3. pide formato claro
4. limita creatividad cuando importa precision
5. separa instrucciones del input del usuario
6. valida el resultado en backend

Si quieres datos estructurados:

1. pide JSON
2. define schema o formato esperado
3. parsea
4. valida
5. maneja fallos sin romper la operación

---

## 10. Seguridad y gobierno

La IA mete riesgos nuevos.

Debes pensar en:

### 10.1 Datos sensibles

- no enviar PII sin necesidad
- redactar campos sensibles cuando sea posible
- separar datos por tenant o usuario

### 10.2 Prompt injection

Ocurre cuando el input intenta manipular instrucciones o extraer datos.

Medidas:

- aislar system prompt
- no permitir que el usuario sobreescriba reglas internas
- limitar herramientas expuestas
- validar contexto recuperado

### 10.3 Tool use con control

Si algun día la IA llama herramientas o acciones:

- define allowlist
- limita parametros
- registra ejecución
- exige confirmación humana en acciones sensibles

### 10.4 Auditoria

Registra:

- quien uso la feature
- cuando
- con que proveedor
- que decision tomo el sistema
- que fallback se activo

---

## 11. RAG: cuando aparece y que implica

RAG significa que el modelo responde usando documentos tuyos como contexto recuperado.

Suele implicar:

1. ingesta documental
2. limpieza
3. fragmentacion
4. embeddings
5. almacenamiento vectorial
6. recuperacion
7. generación con contexto

### 11.1 Cuando si usar RAG

- preguntas sobre manuales
- reglamentos
- documentos internos
- base de conocimiento viva

### 11.2 Cuando no usarlo todavia

- si solo quieres resumir texto enviado por el usuario
- si el problema se resuelve con busqueda tradicional
- si no tienes documentos minimamente limpios

### 11.3 Componentes que suelen entrar

- `AiEmbeddingPort`
- `VectorStorePort`
- pipeline de indexacion
- política de chunking
- metadata y filtros

En local, una opción didáctica suele ser `pgvector` o un vector store sencillo.

---

## 12. Observabilidad y evaluacion

Si integras IA y no mides, no sabes si realmente mejora algo.

Debes medir:

1. latencia
2. costo
3. tasa de error
4. calidad de salida
5. caidas por parsing
6. uso por feature

### 12.1 Evals prácticas

No necesitas empezar con una plataforma gigante.

Puedes arrancar con:

- dataset pequeno de casos esperados
- prompts versionados
- comparación antes/después
- revisión humana de una muestra

### 12.2 Lo que debes guardar

- version de prompt
- version de modelo
- proveedor
- temperatura o parametros relevantes
- salida resumida o hash si no puedes guardar texto completo

---

## 13. Operación y costos

La IA tambien exige presupuesto y mantenimiento.

Puntos operativos:

1. timeouts estrictos
2. reintentos controlados
3. circuit breaker o bloqueo temporal si proveedor falla
4. limites por usuario o por feature
5. cache si el caso lo permite
6. jobs asíncronos para tareas pesadas

### 13.1 Patrones de resiliencia útiles

- timeout corto en request interactivo
- cola para procesos largos
- fallback a respuesta simplificada
- degradacion elegante si IA no responde

---

## 14. Casos realistas para UENS

Lo más sensato para este proyecto seria:

1. resumen de auditoría o bitácoras
2. asistente sobre documentación funcional o técnica
3. ayuda contextual para operadores
4. clasificación de incidencias administrativas

Lo menos sensato hoy:

1. dejar que IA edite notas academicas automaticamente
2. tomar decisiones disciplinarias o administrativas solo con IA
3. reemplazar reglas de negocio con prompts

---

## 15. Ruta de adopcion recomendada

### Etapa 1

- abstraer proveedor con puertos
- probar un caso simple de resumen o clasificación
- medir costo, latencia y utilidad

### Etapa 2

- agregar trazabilidad y métricas
- versionar prompts
- poner validación de salida

### Etapa 3

- introducir proveedor alterno o modelo local
- evaluar fallback
- considerar RAG si ya hay documentación útil

### Etapa 4

- endurecer seguridad
- controlar cuotas
- automatizar evaluaciones

---

## 16. Lo que debes saber como backend engineer

Si algún día quieres trabajar IA desde backend, deberías poder:

1. abstraer proveedores
2. tratar IA como integración externa
3. validar y auditar salidas
4. medir costo y latencia
5. proteger datos
6. decidir cuando no usar IA

Ese criterio importa más que saber nombrar modelos.

---

## 17. Cierre

Integrar IA bien no significa meter un chat.

Significa:

- elegir un caso de uso válido
- desacoplar proveedor
- medir resultado
- proteger datos
- mantener control arquitectonico

La mejor integración con IA es la que mejora el producto sin romper la disciplina del backend.

---

## 18. Siguiente lectura

- `37_backend_v_1_landing_page_ux_ui_y_pagos_en_linea.md`

---

## 19. Cómo estudiar IA sin humo

Si quieres estudiar este tema de forma útil, no empieces memorizando nombres de modelos. Empieza por responder estas preguntas:

1. qué caso de uso concreto quieres mejorar
2. qué dato entrarías al modelo
3. qué riesgo introduces al hacerlo
4. cómo validarías la salida
5. cómo apagarías la feature si empieza a fallar

Esa forma de pensar es mucho más valiosa que saber repetir un benchmark de moda.

---

## 20. Checklist mínimo antes de integrar IA en un backend real

Antes de exponer una feature de IA a usuarios reales, deberías tener al menos:

1. un puerto claro y desacoplado del proveedor
2. límites de tiempo y costo por request
3. validación de salida
4. trazabilidad de uso
5. política de datos sensibles
6. fallback o degradación elegante
7. una decisión explícita sobre si la salida es automática o supervisada

Si no puedes responder esas siete cosas, todavía no estás integrando IA con criterio: solo estás conectando una API llamativa.




