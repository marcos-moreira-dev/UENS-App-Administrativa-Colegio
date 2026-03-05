# 40_backend_v_1_concurrencia_locks_transacciones_y_condiciones_de_carrera

- Version: 1.0
- Estado: Vigente
- Ámbito: concurrencia, exclusión mutua, transacciones y fallos por acceso simultáneo
- Relacionado con:
  - `18_backend_v_1_acid_transacciones_consistencia_backend.md`
  - `26_backend_v_1_performance_escalabilidad_y_cuello_de_botella.md`
  - `30_backend_v_1_testing_estrategia_profesional.md`
  - `39_backend_v_1_algoritmos_y_estructuras_de_datos_riesgos_backend.md`

---

## 1. Propósito

Este documento busca aclarar uno de los temas más traicioneros del backend real:

el sistema puede funcionar perfecto con un solo usuario, y aun así romperse cuando dos o más actores tocan el mismo recurso casi al mismo tiempo.

Ese tipo de fallo rara vez se ve bonito.

Se manifiesta como:

- datos inconsistentes
- estados imposibles
- operaciones duplicadas
- pérdidas silenciosas de información
- errores que aparecen "de vez en cuando"

Y por eso mismo cuesta más diagnosticarlos que un bug determinista clásico.

---

## 2. Idea central

Concurrencia no significa solo "usar hilos".

En backend, concurrencia significa que varias operaciones pueden solaparse sobre:

- el mismo registro
- el mismo recurso lógico
- la misma cola
- el mismo archivo
- la misma ventana de tiempo

Aunque tu código no cree hilos manualmente, ya estás en un entorno concurrente si:

- recibes requests HTTP simultáneos
- tienes workers en background
- haces polling
- generas reportes
- procesas reintentos
- ejecutas tareas programadas

Por eso, concurrencia es un problema estructural del sistema, no solo una curiosidad del lenguaje.

---

## 3. Modelo mental mínimo

### 3.1 Sección crítica

Es la parte del flujo donde no puedes permitir que dos ejecuciones modifiquen el mismo estado sin coordinación.

### 3.2 Recurso compartido

Puede ser:

- una fila en base de datos
- un archivo
- un contador
- una solicitud pendiente
- una sesión
- un saldo

### 3.3 Carrera

Hay condición de carrera cuando el resultado depende del orden exacto e impredecible en que se intercalan operaciones concurrentes.

### 3.4 Atomicidad

Una operación atómica se percibe como indivisible respecto al observador que compite con ella.

### 3.5 Exclusión mutua

Principio por el cual solo una ejecución puede operar sobre cierto recurso crítico al mismo tiempo.

---

## 4. Por qué este tema importa tanto en backend

Muchos errores de concurrencia no se ven en desarrollo porque:

1. pruebas con un solo usuario
2. la base de datos local responde muy rápido
3. no hay latencia real
4. no existen workers compitiendo de verdad

Pero en producción aparecen cuando:

- dos personas editan casi a la vez
- el mismo job se dispara doble
- llega un webhook repetido
- un retry entra mientras el primer intento sigue vivo
- dos nodos quieren procesar lo mismo

La dificultad pedagógica aquí es importante:

el error no suele estar en una línea "mal escrita", sino en una suposición falsa sobre el tiempo y el orden de ejecución.

---

## 5. Fallos clásicos de concurrencia

### 5.1 Lost update

Dos operaciones leen el mismo valor, ambas calculan un nuevo valor y la última que escribe pisa a la anterior.

Ejemplo conceptual:

1. proceso A lee estado `X`
2. proceso B lee estado `X`
3. A escribe `Y`
4. B escribe `Z`

El efecto de A desaparece.

### 5.2 Dirty read

Una operación lee datos no confirmados por otra transacción.

No siempre está permitido según el aislamiento de la base.

### 5.3 Non-repeatable read

Lees una fila dos veces dentro del mismo flujo y obtienes valores distintos porque otra transacción la modificó entre ambas lecturas.

### 5.4 Phantom read

Repites una consulta por criterio y aparecen o desaparecen filas nuevas.

### 5.5 Double processing

Dos workers o dos requests procesan la misma unidad de trabajo.

Este es especialmente importante en:

- colas
- pagos
- reportes
- envíos de correo

### 5.6 Deadlock

Dos transacciones quedan bloqueadas esperando recursos que la otra posee.

### 5.7 Starvation

Un flujo queda postergado demasiadas veces y prácticamente nunca avanza.

---

## 6. Transacciones no equivalen a solución total

Un error frecuente de principiante es pensar:

"si pongo `@Transactional`, ya resolví la concurrencia".

Eso es falso.

La transacción ayuda muchísimo, pero no resuelve por sí sola:

- selección concurrente de trabajos
- reintentos duplicados
- coordinación entre procesos distintos
- idempotencia
- sincronización con archivos o sistemas externos

La transacción protege una parte del problema. No todo el problema.

---

## 7. Aislamiento transaccional: lectura práctica

Los niveles de aislamiento existen para controlar cuánto pueden interferirse transacciones concurrentes.

### Read Uncommitted

- muy permisivo
- casi nunca recomendable para negocio serio

### Read Committed

- compromiso razonable común
- evita leer basura no confirmada
- no evita todos los fenómenos concurrentes

### Repeatable Read

- más fuerte
- protege mejor lecturas repetidas

### Serializable

- el más estricto
- se acerca a ejecutar como si todo fuera secuencial
- más costoso y con mayor riesgo de bloqueos o abortos

### Idea importante

No se elige aislamiento "más alto" por reflejo.

Se elige según:

- riesgo del caso de uso
- volumen esperado
- costo aceptable
- probabilidad de conflicto

---

## 8. Locks: qué son y por qué existen

Un lock es un mecanismo de coordinación para impedir interferencias peligrosas.

### 8.1 Locks pesimistas

Asumen que habrá conflicto.

Entonces bloquean temprano.

Ventajas:

- seguridad fuerte sobre el recurso

Desventajas:

- más espera
- más riesgo de deadlock
- peor throughput si se usan mal

### 8.2 Locks optimistas

Asumen que el conflicto no es tan frecuente.

Entonces permiten trabajar y verifican al final si alguien cambió el estado.

Ventajas:

- mejor rendimiento cuando hay pocos conflictos

Desventajas:

- obliga a detectar conflicto y decidir qué hacer

### 8.3 Locks de aplicación vs locks de base de datos

No siempre debes resolver todo en la base.

Pero tampoco debes mover a memoria lo que la base resuelve mejor.

La pregunta correcta es:

"¿dónde vive realmente el recurso crítico?"

Si el recurso crítico es una fila o un conjunto de filas, muchas veces el lock de base de datos es el mecanismo correcto.

---

## 9. Optimistic locking

Conceptualmente suele implementarse con:

- campo de versión
- timestamp de actualización
- comparación al confirmar

### Cuándo encaja bien

- edición de formularios
- cambios administrativos
- recursos con baja probabilidad de conflicto simultáneo

### Qué exige de verdad

No basta con lanzar error.

Debes decidir:

- si muestras conflicto al usuario
- si reintentas
- si fusionas cambios
- si fuerzas recarga del estado

Esto tiene implicaciones de UX, no solo de persistencia.

---

## 10. Pessimistic locking

Conviene cuando el conflicto es probable y el costo de equivocarte es alto.

Ejemplos:

- selección de trabajos pendientes
- recursos únicos con transición crítica
- decremento de stock o cupos escasos

Pero también tiene costes:

- más espera
- más contención
- más posibilidad de deadlock

No es un martillo universal.

---

## 11. Condiciones de carrera típicas en UENS

### 11.1 Reintento de reporte y worker activo

Si una solicitud está en borde entre `EN_PROCESO` y `ERROR`, un reintento mal coordinado puede:

- disparar doble generación
- sobrescribir archivos
- dejar auditoría ambigua

### 11.2 Dos operadores actuando sobre la misma entidad

Ejemplos:

- activar/inactivar
- cambiar estado
- editar datos críticos

Si no hay control de concurrencia, el último en guardar gana aunque su pantalla esté desactualizada.

### 11.3 Polling y cambio de estado simultáneo

El cliente puede observar transiciones intermedias mientras el worker aún no terminó de consolidar todo.

### 11.4 Limpieza y renovación de tokens

Un token puede estar siendo renovado mientras otro proceso intenta invalidarlo o limpiarlo.

### 11.5 Descarga y borrado de archivo

Si en algún momento introduces retención o limpieza automática, puedes entrar en carrera entre:

- quien descarga
- quien elimina
- quien vuelve a generar

---

## 12. Idempotencia: defensa fundamental

Cuando un backend puede recibir la misma intención más de una vez, la idempotencia deja de ser una elegancia. Se vuelve una defensa básica.

Debes pensar en idempotencia cuando hay:

- reintentos
- webhooks
- botones que el usuario pulsa dos veces
- jobs duplicados
- cortes de red con confirmación incierta

### Ejemplo conceptual

Si `reintentarReporte(id)` se ejecuta dos veces casi al mismo tiempo, ¿el sistema:

- genera dos archivos
- crea dos solicitudes
- pisa la anterior
- detecta que ya existe una transición válida?

Esa es una pregunta de idempotencia.

---

## 13. Colas y concurrencia

Los sistemas con colas siempre deben responder tres preguntas:

1. ¿quién puede tomar una tarea?
2. ¿cómo se marca como tomada?
3. ¿qué pasa si el consumidor cae a mitad de camino?

Si no respondes esas tres, tienes una cola frágil.

### Patrones útiles

- claim-and-process
- lease temporal
- estado `PENDIENTE -> EN_PROCESO -> COMPLETADA/ERROR`
- timeout de recuperación
- límite por lote

### Error típico

Leer "pendientes" y procesarlas sin una transición protegida.

Eso invita al double processing.

---

## 14. Deadlocks

Un deadlock no significa simplemente "el sistema está lento".

Significa que dos o más transacciones han quedado atrapadas por dependencia circular de locks.

### Patrón clásico

1. transacción A bloquea recurso 1 y quiere recurso 2
2. transacción B bloquea recurso 2 y quiere recurso 1

### Cómo se reduce el riesgo

1. adquiriendo recursos en orden consistente
2. reduciendo duración de transacciones
3. evitando mezclar demasiados recursos en un solo bloque crítico
4. detectando y reintentando cuando el motor aborta una transacción

---

## 15. Concurrencia en memoria vs concurrencia distribuida

No es lo mismo:

- dos hilos en el mismo proceso
- dos requests en la misma app
- dos instancias del backend
- backend más worker
- backend más proveedor externo

### En memoria

Importan:

- `synchronized`
- locks locales
- estructuras concurrentes
- visibilidad de memoria

### Distribuida

Importan:

- locks distribuidos
- registros de estado en BD
- Redis
- colas
- idempotencia
- timestamps y leases

Un lock local no sirve para coordinar dos procesos distintos.

Ese error aparece muchísimo.

---

## 16. Estructuras concurrentes

En algunos contextos aparecen estructuras como:

- `ConcurrentHashMap`
- colas bloqueantes
- ring buffers
- atomics

Son útiles, pero no resuelven mágicamente consistencia de negocio.

Pueden resolver:

- coordinación interna
- caches efímeras
- buffers

Pero no reemplazan:

- transacciones
- locks sobre datos persistentes
- reglas de idempotencia

---

## 17. Patrón mental para diseñar un caso crítico

Cuando una operación importa de verdad, pregúntate:

1. ¿qué recurso exacto estoy protegiendo?
2. ¿cuál es la ventana crítica?
3. ¿quién más puede tocar esto al mismo tiempo?
4. ¿qué pasa si llega doble request?
5. ¿qué pasa si el proceso cae a mitad?
6. ¿puedo reintentar sin duplicar efectos?
7. ¿qué evidencia quedará para soporte?

Ese cuestionario es más valioso que aplicar locks por intuición.

---

## 18. Qué errores de diseño suelen romper un backend

### Error 1

Leer, decidir y escribir sin asegurar que nadie cambió el estado entre medio.

### Error 2

Usar una verificación previa en memoria como si fuera garantía.

Ejemplo:

- "si no existe, entonces creo"

Eso en concurrencia puede fallar si otro actor crea justo después de tu check.

### Error 3

No modelar estados de transición.

Si solo tienes:

- `PENDIENTE`
- `COMPLETADA`

te faltan estados para coordinar trabajo en curso.

### Error 4

Suponer que los reintentos siempre son raros.

En sistemas reales, los reintentos ocurren.

### Error 5

Pensar que un bug concurrente se arregla siempre "poniendo más transacciones".

No.

A veces pide:

- mejor modelo de estado
- idempotencia
- cambio de algoritmo
- cola más robusta

---

## 19. Cómo se estudia esto de forma seria

No memorices solo definiciones.

Estudia casos.

### Ejercicios mentales útiles

1. dos usuarios editan la misma fila
2. dos workers toman la misma tarea
3. un webhook llega dos veces
4. un usuario hace doble click
5. un proceso cae después de escribir archivo pero antes de marcar éxito

En cada caso, intenta responder:

- dónde está el recurso crítico
- qué falla si no coordinas
- qué patrón te protege mejor

---

## 20. Recomendación concreta para UENS

En este proyecto, conviene reforzar siempre estas zonas con mentalidad concurrente:

1. cola de reportes
2. reintentos
3. edición de entidades sensibles
4. refresh/logout y expiración de tokens
5. limpieza de archivos
6. futura integración con pagos o webhooks

Porque ahí el tiempo, el orden y la duplicación sí importan.

---

## 21. Cierre

La concurrencia castiga al backend que razona como si viviera en un universo secuencial.

Un sistema robusto no es el que "ojalá no reciba dos operaciones a la vez".

Es el que fue diseñado sabiendo que eso ocurrirá.

Por eso locks, transacciones, idempotencia y estados de transición no son sofisticación innecesaria.

Son mecanismos de supervivencia.

---

## 22. Siguiente lectura

- `41_backend_v_1_backend_iot_protocolos_udp_mqtt_coap_y_arquitecturas.md`

