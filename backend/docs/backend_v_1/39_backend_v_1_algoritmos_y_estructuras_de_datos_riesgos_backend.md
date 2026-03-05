# 39_backend_v_1_algoritmos_y_estructuras_de_datos_riesgos_backend

- Versión: 1.0
- Estado: Vigente
- Ámbito: teoría útil de algoritmos y estructuras de datos aplicada al backend
- Relacionado con:
 - `26_backend_v_1_performance_escalabilidad_y_cuello_de_botella.md`
 - `29_backend_v_1_modelo_de_datos_sql_migraciones_indices_y_consistencia.md`
 - `30_backend_v_1_testing_estrategia_profesional.md`
 - `35_backend_v_1_mensajeria_eventos_colas_rabbitmq_kafka_cqrs_y_cdn.md`

---

## 1. Propósito

Este documento está escrito con una intención deliberadamente más teórica.

No para alejarte del proyecto, sino para ayudarte a ver algo que muchas veces se aprende demasiado tarde:

un backend no se rompe solo por mala arquitectura de capas o por malas queries SQL.

También puede romperse por decisiones pobres de:

- algoritmos
- estructuras de datos
- representación de estados
- patrones de recorrido o búsqueda

En otras palabras:

un backend puede verse limpio por fuera y aún así estar condenado por dentro a consumir demasiada CPU, memoria o tiempo.

---

## 2. Tesis central

La tesis de este documento es simple:

todo backend ejecuta algoritmos, aunque sus autores no siempre los llamen así.

Cada vez que haces una de estas cosas, estás diseñando un algoritmo:

- validar duplicados
- filtrar una lista
- ordenar resultados
- detectar solapamientos de horario
- buscar una entidad en memoria
- priorizar trabajos
- limpiar sesiones expiradas
- recorrer un árbol o un grafo
- consolidar un reporte

Y cada vez que eliges una de estas cosas, estás eligiendo una estructura de datos:

- lista
- mapa
- conjunto
- cola
- pila
- heap
- árbol
- tabla hash
- índice de base de datos

Por eso algoritmos y estructuras de datos no son un tema aparte del backend. Son una de sus capas invisibles.

---

## 3. Recordatorio formal mínimo

### 3.1 Tamaño de entrada

Normalmente representamos el tamaño del problema por `n`.

Ejemplos:

- `n` estudiantes
- `n` filas de auditoría
- `n` solicitudes pendientes
- `n` clases en un horario

### 3.2 Complejidad temporal

La complejidad temporal describe cómo crece el trabajo conforme crece la entrada.

Ejemplos clásicos:

- `O(1)` acceso ideal a hash
- `O(log n)` búsqueda en estructura ordenada
- `O(n)` recorrido lineal
- `O(n log n)` ordenamiento eficiente típico
- `O(n²)` doble recorrido anidado

### 3.3 Complejidad espacial

No basta con que algo sea rápido. También importa cuánta memoria consume.

Un algoritmo puede ser aceptable en tiempo y desastroso en espacio.

### 3.4 Complejidad amortizada

Algunas operaciones parecen caras de vez en cuando, pero baratas en promedio.

Entender esto ayuda a no sacar conclusiones apresuradas sobre estructuras como arreglos dinámicos o colas con limpieza periódica.

---

## 4. Idea clave para backend: la base de datos también participa

En backend, la teoría de estructuras de datos no vive solo en memoria.

La base de datos también usa estructuras de datos:

- índices B-tree
- hashes
- particiones
- estructuras de ordenamiento

Por eso, cuando hablas de backend, la elección correcta no siempre es:

"¿uso `HashMap` o `TreeMap`?"

A veces la pregunta correcta es:

"¿esto debería resolverse en memoria o delegarse a un índice bien diseñado en la base de datos?"

Ese matiz es fundamental.

---

## 5. Estructuras de datos que un backend engineer sí debería dominar

### 5.1 Listas y arreglos dinámicos

Buenas para:

- recorrido
- mantener orden de inserción
- exposición de colecciones

Malas para:

- búsquedas repetidas por pertenencia
- deduplicación frecuente

### 5.2 HashMap / tablas hash

Buenas para:

- búsquedas por clave
- caches locales
- indexación temporal en memoria

Conceptualmente muy valiosas en backend.

### 5.3 HashSet

Excelente para:

- detectar duplicados
- membresía
- validaciones de unicidad temporales

### 5.4 Árboles y mapas ordenados

Útiles cuando importa:

- orden natural
- rangos
- búsquedas por vecindad

### 5.5 Colas y deques

Muy importantes para:

- workers
- rate limiting
- buffers temporales
- ventanas deslizantes

### 5.6 Heaps o priority queues

Útiles cuando necesitas:

- priorización
- seleccionar el menor o mayor rápidamente
- planificar trabajos por urgencia

### 5.7 Grafos

No aparecen en todos los sistemas, pero sí en problemas como:

- dependencias
- jerarquías complejas
- rutas
- detección de ciclos

### 5.8 Pilas y recursión

Muy relevantes cuando haces:

- parsing
- recorrido de árboles
- procesamiento recursivo

Un mal uso puede terminar en desbordes de pila o en recorridos infinitos.

---

## 6. Escenario 1: validar duplicados con doble bucle

### Situación

Se importa una lista de estudiantes o se procesa una colección grande de entidades y se intenta detectar duplicados así:

para cada elemento, recorrer todos los demás.

### Complejidad

`O(n²)`

### Síntoma

Con pocos datos parece "aceptable". Con cientos o miles, el tiempo crece de forma muy desagradable.

### Error conceptual

Confundir una colección de exposición con una estructura apta para membresía.

### Corrección teórica

Si el problema principal es pertenencia o unicidad, la estructura natural suele ser un conjunto o un mapa.

### Aterrizado a UENS

Esto puede aparecer en:

- importaciones futuras
- validaciones masivas
- consolidación de reportes
- chequeos de entidades repetidas

---

## 7. Escenario 2: usar `List.contains` en un hot path

### Situación

Se recorre una lista grande y en cada paso se pregunta si cierto elemento ya está presente en otra lista.

### Complejidad

Si se hace dentro de otro recorrido, puedes terminar otra vez en algo cercano a `O(n²)`.

### Síntoma

- CPU alta
- endpoints lentos
- validaciones que empeoran mucho al crecer los datos

### Corrección

Cuando la operación clave es "¿ya existe?" o "¿está contenido?", piensa primero en `Set`, no en `List`.

---

## 8. Escenario 3: filtrar y ordenar en memoria lo que debió resolver la base de datos

### Situación

El backend consulta demasiados registros y luego:

- filtra en Java
- ordena en Java
- página en Java

### Error conceptual

Resolver con estructuras locales un problema cuyo volumen ya pertenece a la capa de persistencia.

### Síntoma

- uso de memoria elevado
- respuestas lentas
- paginación inconsistente
- riesgo de timeouts

### Observación teórica

Aquí no basta con saber complejidad algorítmica en memoria. Debes comprender costo de transferencia, materialización de objetos y cardinalidad.

### Aterrizado a UENS

Es especialmente delicado en:

- auditoría
- reportes
- listados con filtros
- descargas masivas

---

## 9. Escenario 4: detectar solapamientos de horarios de forma ingenua

### Situación

Para validar si una clase choca con otra, comparas el nuevo bloque horario contra todos los bloques existentes sin ninguna estructura auxiliar ni criterio de reducción.

### Complejidad

En forma simple:

- `O(n)` por inserción si comparas contra todo el conjunto
- puede escalar a `O(n²)` si haces consolidaciones o cargas masivas

### Síntoma

- el módulo de clases empeora al crecer el número de horarios
- los reintentos o verificaciones masivas se vuelven costosos

### Corrección conceptual

El problema no es solo "iterar". El problema es que aquí estás resolviendo una consulta de intervalos.

Y los problemas de intervalos piden pensar en:

- ordenamiento previo
- partición por día / docente / sección
- índices
- estructuras por rango

No necesitas montar un intervalo tree sofisticado el primer día, pero sí debes reconocer que el problema no es un simple CRUD.

---

## 10. Escenario 5: workers que escanean toda la cola una y otra vez

### Situación

Un worker revisa repetidamente todas las solicitudes para encontrar unas pocas pendientes.

### Error conceptual

Usar un recorrido global periódico cuando el problema real pide selección eficiente.

### Síntoma

- ciclos desperdiciados
- mayor carga en base de datos o memoria
- workers que parecen "trabajar" mucho sin avanzar proporcionalmente

### Corrección

Debes pensar en:

- estructuras de cola reales
- partición por estado
- selección por prioridad
- límites por lote

### Aplicado a UENS

Esto toca directamente:

- cola de reportes
- reintentos
- procesamiento de archivos

---

## 11. Escenario 6: limpieza de expirados con estrategia errónea

### Situación

Sesiones, refresh tokens o intentos de login se limpian recorriendo estructuras completas en momentos desafortunados.

### Síntoma

- picos de latencia
- operaciones rápidas que de pronto se vuelven lentas
- comportamiento errático bajo carga

### Lectura teórica

Aquí aparece el clásico problema de mantenimiento de estructuras mutables en el tiempo:

- limpieza eager
- limpieza lazy
- limpieza por lotes
- costo amortizado

La pregunta no es solo "¿limpio o no limpio?". La pregunta correcta es:

"¿cuándo, cuánto y con qué costo amortizado?"

---

## 12. Escenario 7: exportar todo a memoria antes de escribir archivo

### Situación

Para generar un PDF, XLSX o DOCX se carga todo el dataset en memoria, luego se arma un objeto enorme y al final se serializa.

### Síntoma

- uso excesivo de heap
- pausas del GC
- riesgo de `OutOfMemoryError`

### Error conceptual

Confundir procesamiento secuencial con necesidad de materialización total.

### Corrección

En problemas de exportación grande, conviene pensar en:

- streaming
- lotes
- escritura incremental
- límites explícitos

### Aplicado a UENS

Muy importante en:

- reportes administrativos
- auditoría exportada
- listados grandes

---

## 13. Escenario 8: recursión sin control o grafos con ciclos

### Situación

Se recorre una estructura jerárquica o relacional sin:

- marca de visitados
- control de profundidad
- condición clara de parada

### Riesgo

- bucles infinitos
- `StackOverflowError`
- consumo absurdo de CPU

### Observación docente

Muchos fallos "misteriosos" son, en el fondo, un problema clásico de teoría de grafos mal reconocido.

Cuando hay referencias mutuas, ya no estás en un árbol limpio. Estás en un grafo.

Y los grafos exigen:

- detección de visitados
- control de ciclos
- decisión sobre DFS o BFS

---

## 14. Escenario 9: mezclar estructura de datos equivocada con el patrón de acceso equivocado

Este es el error más común y más profundo.

No basta con saber qué es un `HashMap`.

Debes saber si el patrón dominante es:

- acceso por clave
- recorrido ordenado
- inserción al final
- borrado frecuente
- selección por prioridad
- consulta por rango

La estructura correcta no se elige por costumbre. Se elige por patrón de acceso.

---

## 15. Escenario 10: no reconocer que un índice de base de datos es parte del algoritmo

En backend, muchos errores de "algoritmo" nacen porque el programador piensa solo en Java y olvida la estructura externa.

Ejemplo:

- hacer una búsqueda lineal sobre datos que deberían resolverse con índice
- ordenar masivamente en aplicación lo que la base ya puede ordenar mejor
- consultar sin `WHERE` selectivo y luego refinar en memoria

### Idea fuerte

El algoritmo total del backend incluye:

1. selección en base de datos
2. transferencia de datos
3. materialización
4. transformación
5. serialización

Si optimizas solo el paso 4, puedes seguir perdiendo el partido completo.

---

## 16. Síntomas típicos de mal diseño algorítmico

Un backend puede estar sufriendo por estructura o algoritmo cuando observas:

1. tiempos que crecen desproporcionadamente con pocos miles de registros
2. picos de CPU durante validaciones o reportes
3. consumo de memoria mucho mayor al esperado
4. GC excesivo
5. latencias impredecibles
6. módulos que "funcionan" en desarrollo pero colapsan al crecer

Esto no siempre se debe a la base de datos ni al framework.

A veces el problema es tan elemental como usar una lista donde el problema pedía un conjunto.

---

## 17. Preguntas teóricas que deberías hacerte

Antes de aceptar una solución, acostúmbrate a preguntar:

1. ¿cuál es la operación dominante?
2. ¿cuál es el tamaño esperado de entrada?
3. ¿qué estructura favorece esa operación?
4. ¿el problema es mejor en memoria o en base de datos?
5. ¿estoy repitiendo recorridos innecesarios?
6. ¿la memoria crece también con la entrada?
7. ¿qué ocurre en el peor caso?

Estas preguntas son simples, pero distinguen a quien programa por intuición de quien diseña con criterio.

---

## 18. Qué estructuras conviene estudiar con prioridad

Para backend, mi sugerencia de estudio es esta:

### Nivel 1

- arrays / listas
- hash maps
- hash sets
- pilas y colas
- notación Big O

### Nivel 2

- árboles balanceados
- heaps / priority queues
- búsquedas binarias
- ordenamientos
- intervalos y rangos

### Nivel 3

- grafos
- recorridos DFS / BFS
- detección de ciclos
- estructuras probabilísticas
- análisis amortizado

No porque vayas a usarlas todas cada día, sino porque te enseñan a reconocer la forma del problema.

---

## 19. Ruta de aprendizaje recomendada

### Etapa 1: fundamentos

Aprende bien:

- complejidad temporal
- complejidad espacial
- arrays, listas, mapas, conjuntos
- ordenamiento y búsqueda

### Etapa 2: backend aplicado

Aprende a detectar:

- deduplicación
- membresía
- paginación
- solapamientos
- colas
- prioridades

### Etapa 3: integración con persistencia

Aprende a conectar teoría con:

- índices SQL
- planes de ejecución
- cardinalidad
- streaming de resultados

### Etapa 4: sistemas más complejos

Aprende:

- concurrencia
- estructuras lock-free si alguna vez llegas a necesitarlas
- colas distribuidas
- cachés
- consistencia eventual

---

## 20. Recomendación concreta para UENS

En el contexto de este proyecto, las zonas donde más conviene pensar algorítmicamente son:

1. validación de horarios y solapamientos
2. filtros y paginación
3. auditoría y búsqueda de eventos
4. cola de reportes
5. generación de archivos
6. rate limiting
7. limpieza de sesiones o tokens

No porque UENS sea un sistema "matemático", sino porque esos puntos ya concentran:

- recorridos repetidos
- potencial de crecimiento
- uso intensivo de memoria o CPU
- necesidad de tiempos de respuesta razonables

---

## 21. Cierre

El mal diseño algorítmico no siempre se ve en el código como algo escandaloso.

A veces se ve cómo:

- un doble `for`
- un `contains` aparentemente inocente
- una lista donde hacía falta un set
- un reporte que carga todo a memoria
- un worker que revisa demasiado

Por eso este tema importa.

No para volver el backend un curso abstracto de teoría, sino para recordar una verdad simple:

la elegancia arquitectónica no compensa una mala complejidad.

---

## 22. Siguiente lectura

- `26_backend_v_1_performance_escalabilidad_y_cuello_de_botella.md`

