# 18_backend_v_1_acid_transacciones_consistencia_backend

- Version: 1.0
- Estado: Aterrizado sobre implementacion real
- Ámbito: Backend V1 + PostgreSQL
- Fecha de corte: 2026-02-27

---

## 1. Propósito

Explicar como se aplica ACID en el backend UENS, que parte resuelve PostgreSQL, que parte resuelve Spring Boot y que decisiones concretas se tomaron para mantener consistencia transaccional sin sobreingenieria.

---

## 2. Recordatorio breve: que significa ACID

## 2.1 Atomicidad

Una operación transaccional debe ocurrir completa o no ocurrir.

Ejemplo:

- crear un estudiante y asignarlo a una seccion no debe dejar media operación persistida.

## 2.2 Consistencia

La base debe pasar de un estado válido a otro estado válido.

Ejemplo:

- no se debe permitir una calificacion fuera de rango o una referencia a una entidad inexistente.

## 2.3 Aislamiento

Transacciones concurrentes no deben corromperse entre si.

Ejemplo:

- dos workers no deben tomar la misma solicitud de reporte como si ambos fueran propietarios.

## 2.4 Durabilidad

Cuando una transaccion hace `COMMIT`, el dato debe quedar persistido.

Ejemplo:

- una solicitud de reporte confirmada o un evento de auditoria confirmado deben sobrevivir a reinicios normales del proceso.

---

## 3. Como se reparte la responsabilidad en este proyecto

## 3.1 PostgreSQL

PostgreSQL aporta:

1. motor transaccional ACID
2. `COMMIT` / `ROLLBACK`
3. `FK`, `UNIQUE`, `CHECK`
4. locks y aislamiento

## 3.2 Spring Boot / JPA

Spring aporta:

1. limites transaccionales via `@Transactional`
2. propagacion de transacciones
3. separacion entre comandos y consultas
4. control del ciclo de persistencia JPA

## 3.3 Backend UENS

La aplicacion aporta:

1. validaciones funcionales antes de persistir
2. manejo de worker de reportes con claim concurrente
3. compensacion cuando interviene filesystem
4. trazabilidad de auditoria desacoplada

---

## 4. Estado real de ACID en el backend

## 4.1 Atomicidad ya aplicada

Los servicios de escritura principales ya usan `@Transactional`.

Ejemplos:

- `EstudianteCommandService`
- `SeccionCommandService`
- `ClaseCommandService`
- `CalificacionCommandService`
- `ReporteSolicitudCommandService`

Justificacion:

- las operaciones de negocio de escritura deben confirmarse completas o revertirse completas.

## 4.2 Consistencia ya aplicada

La consistencia se apoya en dos capas.

### Base de datos

- `CHECK` para roles, estados, rangos y parciales
- `UNIQUE` para combinaciones operativas
- `FK` para relaciones obligatorias

### Backend

- validadores por módulo
- reglas de negocio previas al `save`
- filtros y `sort` con whitelist en consultas

Justificacion:

- si solo se validara en backend, SQL podria aceptar estados invalidos desde otro punto de acceso.
- si solo se validara en BD, la API responderia tarde y con errores menos expresivos.

## 4.3 Aislamiento ya aplicado

El caso más delicado es la cola de reportes.

El claim usa `FOR UPDATE SKIP LOCKED` sobre `reporte_solicitud_queue`.

Justificacion:

- evita que dos workers tomen la misma solicitud.
- es la parte del backend donde el aislamiento importa más de forma visible.

## 4.4 Durabilidad ya aplicada

Cuando PostgreSQL confirma una transaccion:

- la solicitud de reporte queda persistida
- los cambios funcionales quedan persistidos
- los eventos de auditoria, si confirman, quedan persistidos

---

## 5. Problemas ACID reales que si existian

## 5.1 Archivo de reporte en disco sin compensacion

Problema anterior:

1. el worker generaba archivo fisico
2. luego intentaba persistir resultado en BD
3. si fallaba la persistencia, podia quedar archivo huerfano

Eso no rompe ACID de PostgreSQL, pero si rompe atomicidad de negocio entre:

- filesystem
- metadata SQL

## 5.2 Auditoria dentro de la misma transaccion principal

Problema anterior:

- un evento de auditoria podia formar parte de la misma transaccion del caso de uso
- si la transaccion principal revertia, la evidencia podia perderse

Eso era aceptable para V1 simple, pero debil para trazabilidad operativa.

---

## 6. Mejoras implementadas ahora

## 6.1 Auditoria con transaccion independiente

Archivo:

- `modules/auditoria/application/AuditoriaEventService`

Cambio:

- `@Transactional(propagation = REQUIRES_NEW)`

Justificacion:

1. la auditoria es evidencia operativa
2. conviene que su confirmación no dependa por completo de la transaccion llamadora
3. si el registro de auditoria falla, no debe tumbar el caso de uso principal

Resultado:

- mejor durabilidad de auditoria
- menor acoplamiento transaccional

## 6.2 Compensacion de archivos de reporte

Archivos:

- `modules/reporte/application/ReporteFileGenerationService`
- `modules/reporte/application/ReporteSolicitudWorkerService`

Cambio:

- si el worker ya genero archivo pero luego ocurre error en la persistencia del resultado, se elimina el archivo en disco de forma compensatoria

Justificacion:

1. el filesystem no participa en la transaccion SQL
2. sin compensacion, quedan residuos no trazados
3. la compensacion reduce inconsistencia observable

Resultado:

- mejor atomicidad de negocio entre disco y base de datos

---

## 7. Que no se hizo a proposito

No se implemento:

1. aislamiento `SERIALIZABLE` global
2. outbox pattern
3. sagas
4. storage distribuido transaccional
5. dos fases de commit

Justificacion:

- el proyecto es pedagogico y monolitico
- el costo de complejidad seria mayor que el beneficio en esta fase

---

## 8. Limitaciones que siguen existiendo

## 8.1 Filesystem y base de datos no forman una sola transaccion real

La compensacion mejora mucho el problema, pero no convierte disco + SQL en una transaccion unica.

Interpretacion correcta:

- se implemento consistencia compensatoria, no commit distribuido.

## 8.2 Worker por lote

El procesamiento de pendientes sigue agrupado por lote dentro del worker.

Eso es suficiente para V1, pero a futuro puede endurecerse con:

- una transaccion nueva por item procesado
- servicio dedicado por unidad de trabajo

## 8.3 Auditoria tolerante a fallo

La auditoria sigue capturando excepciones internas y registrando `warn`.

Esto es una decision deliberada:

- priorizar disponibilidad funcional
- no bloquear negocio por una falla secundaria de trazabilidad

---

## 9. Justificacion arquitectonica

La política adoptada es:

1. ACID fuerte dentro de PostgreSQL
2. transacciones Spring en servicios de escritura
3. aislamiento reforzado donde realmente hay concurrencia (`DB queue`)
4. compensacion donde sale del motor transaccional (filesystem)
5. auditoria en transaccion aparte por valor pedagogico y operativo

Es una solucion profesional y realista para este contexto.

---

## 10. Evidencia técnica en codigo

- `modules/reporte/infrastructure/persistence/repository/ReporteSolicitudQueueClaimRepositoryImpl`
  - claim concurrente con `FOR UPDATE SKIP LOCKED`
- `modules/auditoria/application/AuditoriaEventService`
  - auditoria con `REQUIRES_NEW`
- `modules/reporte/application/ReporteSolicitudWorkerService`
  - compensacion y persistencia del resultado
- `modules/reporte/application/ReporteFileGenerationService`
  - eliminacion silenciosa del archivo durante compensacion

---

## 11. Como explicarlo en defensa o documentación

Frase corta útil:

> El backend usa ACID de PostgreSQL para las operaciones críticas, transacciones Spring para delimitar casos de uso, aislamiento explicito en la cola de reportes y compensacion cuando interviene el filesystem.

Frase más técnica:

> En UENS, ACID se garantiza de forma fuerte dentro de la base de datos y de forma pragmatica en integraciones no transaccionales, especialmente en reportes, donde se aplico compensacion de archivos y auditoria desacoplada con `REQUIRES_NEW`.

---

## 12. Siguiente endurecimiento recomendado

Si luego quieres subir un nivel más:

1. procesar cada solicitud de reporte en transaccion independiente
2. agregar pruebas de concurrencia sobre la cola
3. registrar checksum del archivo generado para trazabilidad
4. agregar política de limpieza de archivos huerfanos por tarea programada


