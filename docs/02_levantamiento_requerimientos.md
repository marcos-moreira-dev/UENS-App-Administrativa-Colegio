# Levantamiento de requerimientos

## Propósito del documento

Este documento especifica los requerimientos funcionales y no funcionales de la fase inicial del sistema para la **Unidad Educativa Niñitos Soñadores**, a partir del levantamiento de información del negocio.

## Alcance del documento

Se detallan las capacidades que el sistema debe ofrecer en la **fase 1**, las restricciones operativas relevantes y las validaciones mínimas necesarias para una operación confiable.

## Límite de esta etapa

Este documento no define todavía el modelo lógico de base de datos, la normalización ni la implementación técnica final; su objetivo es establecer **qué debe hacer el sistema**.

---

## 1) Contexto resumido para los requerimientos

La institución requiere un sistema para centralizar y consultar información escolar básica (estudiantes, representantes legales, docentes, secciones, asignaturas, clases y calificaciones), debido a que actualmente parte de la gestión se realiza con registros manuales y herramientas no integradas.

La fase inicial del sistema busca mejorar la consistencia administrativa, el control de cupos por sección y la consulta de información académica básica, incluyendo el registro de **calificaciones por clase y por parcial (2 parciales)**, además del **control básico de acceso de usuarios sistema administrativo**.

---

## 2) Objetivo funcional de la fase 1

El sistema debe permitir registrar, actualizar y consultar información escolar básica de forma confiable, con énfasis en:

* organización de estudiantes por sección (asignación vigente)
* control de cupos
* gestión de asignaturas
* gestión de clases por sección (oferta académica operativa)
* gestión operativa de docentes por sección (relación muchos a muchos)
* asignación de docentes a clases (según planificación operativa)
* registro de calificaciones por clase y por parcial
* control básico de acceso de usuarios sistema administrativo (login, estado y rol de uso)
* consultas operativas básicas para personal administrativo

---

## 3) Actores del sistema (fase 1)

### 3.1 Personal administrativo / secretaría

Actor principal de la fase 1. Realiza registros, actualizaciones, asignaciones y consultas operativas del sistema.

### 3.2 Docente (actor del negocio, sin acceso al sistema administrativo en fase 1)

Se registra su información, su asociación con secciones y su asignación a clases. En esta fase, el docente **no** tiene acceso al sistema administrativo como usuario operativo.

### 3.3 Usuario sistema administrativo (operación de fase 1)

Corresponde al usuario autorizado para ingresar al sistema con credenciales, estado activo y rol de uso básico. En la fase 1, este uso se aplica a la operación administrativa / secretaría.

---

## 4) Requerimientos funcionales (RF)

### RF-01. Registrar estudiante

El sistema debe permitir registrar un estudiante con sus datos básicos (identificación interna, nombres, apellidos, fecha de nacimiento, estado).

Como política operativa de la fase 1, para registrar un estudiante se debe **identificar o registrar primero** a su **representante legal principal**, de modo que el estudiante quede asociado al representante en el mismo proceso de registro.

### RF-02. Actualizar estudiante

El sistema debe permitir actualizar los datos de un estudiante registrado.

### RF-03. Consultar estudiantes

El sistema debe permitir consultar estudiantes mediante búsquedas básicas (por nombres, apellidos y/o sección).

### RF-04. Registrar representante legal

El sistema debe permitir registrar un representante legal con sus datos básicos (nombres, apellidos, teléfono, correo electrónico).

### RF-05. Actualizar representante legal

El sistema debe permitir actualizar los datos de un representante legal registrado.

### RF-06. Consultar representantes legales

El sistema debe permitir consultar representantes legales registrados.

### RF-07. Asignar representante legal a estudiante

El sistema debe permitir asociar un representante legal a un estudiante, respetando la política operativa definida para la fase 1 (representante legal principal obligatorio para el registro operativo del estudiante).

### RF-08. Registrar docente

El sistema debe permitir registrar docentes con sus datos básicos (nombres, apellidos, estado y datos de contacto opcionales).

### RF-09. Actualizar docente

El sistema debe permitir actualizar los datos de un docente registrado.

### RF-10. Consultar docentes

El sistema debe permitir consultar docentes registrados.

### RF-11. Registrar sección

El sistema debe permitir registrar secciones con sus datos básicos: grado, paralelo, año lectivo, cupo máximo y estado.

### RF-12. Actualizar sección

El sistema debe permitir actualizar datos de una sección, incluyendo su estado y cupo máximo, respetando restricciones operativas.

### RF-13. Consultar secciones

El sistema debe permitir consultar secciones y visualizar su información general, incluyendo cupo máximo y cantidad de estudiantes registrados.

### RF-14. Asignar estudiante a sección (asignación vigente)

El sistema debe permitir registrar la **asignación vigente** de un estudiante a una sección, validando cupo disponible y estado de las entidades involucradas.

### RF-15. Actualizar la sección vigente de un estudiante

El sistema debe permitir actualizar la sección vigente de un estudiante cuando exista un cambio administrativo, manteniendo en fase 1 un único vínculo vigente estudiante–sección.

### RF-16. Consultar estudiantes por sección

El sistema debe permitir listar los estudiantes asociados a una sección.

### RF-17. Registrar asignatura

El sistema debe permitir registrar asignaturas con datos básicos (nombre, área, grado, descripción, estado).

### RF-18. Actualizar asignatura

El sistema debe permitir actualizar los datos de una asignatura registrada.

### RF-19. Consultar asignaturas

El sistema debe permitir consultar asignaturas registradas.

### RF-20. Asociar docentes a secciones

El sistema debe permitir asociar docentes y secciones de forma operativa, considerando que:

* una sección puede tener varios docentes
* un docente puede estar asociado a varias secciones

### RF-21. Consultar docentes por sección

El sistema debe permitir consultar qué docentes están asociados a una sección.

### RF-22. Consultar secciones por docente

El sistema debe permitir consultar en qué secciones está asociado un docente.

### RF-23. Registrar clase (oferta académica operativa)

El sistema debe permitir registrar una **clase** como oferta concreta dentro de una sección, definiendo al menos:

* sección asociada
* asignatura asociada
* franja horaria
* estado

De forma opcional en la fase 1, una clase puede registrarse inicialmente sin docente asignado, para completar la planificación operativa después.

### RF-24. Actualizar clase

El sistema debe permitir actualizar los datos de una clase registrada (por ejemplo: franja horaria, estado y docente asignado si aplica), respetando las validaciones operativas.

### RF-25. Consultar clases

El sistema debe permitir consultar clases registradas y filtrarlas al menos por sección, asignatura, estado y docente (si está asignado).

### RF-26. Registrar calificación

El sistema debe permitir registrar una calificación asociando **estudiante, clase y parcial** (**1 o 2**), con una nota y datos opcionales de registro (fecha/observación).

### RF-27. Actualizar calificación

El sistema debe permitir actualizar una calificación previamente registrada, según flujo administrativo de la fase 1.

### RF-28. Consultar calificaciones por estudiante

El sistema debe permitir consultar las calificaciones registradas de un estudiante.

### RF-29. Consultar calificaciones por clase, asignatura y parcial

El sistema debe permitir consultar calificaciones por:

* clase y parcial, y/o
* asignatura y parcial (derivando la asignatura desde la clase)

### RF-30. Dashboard / consultas operativas básicas

El sistema debe permitir visualizar consultas operativas básicas para apoyo administrativo, incluyendo al menos:

* estudiantes por sección
* docentes por sección
* secciones por docente
* cupos disponibles por sección
* clases por sección
* clases por docente
* calificaciones por estudiante
* calificaciones por clase y parcial
* calificaciones por asignatura y parcial (a partir de clases)

### RF-31. Filtrar listados y consultas

El sistema debe permitir aplicar filtros en los listados y consultas principales, según el módulo, por ejemplo:

* estado (activo/inactivo)
* sección
* grado/paralelo
* año lectivo
* asignatura
* clase
* parcial (1 o 2)
* docente

### RF-32. Búsqueda por texto en listados

El sistema debe permitir búsqueda por texto en listados principales (por ejemplo, nombres y apellidos en estudiantes, representantes, docentes y usuarios sistema administrativo).

### RF-33. Ordenar resultados de consultas

El sistema debe permitir ordenar resultados en listados y consultas principales según criterios aplicables (por ejemplo: nombre, fecha de registro, estado).

### RF-34. Paginación de listados

El sistema debe permitir paginar listados cuando la cantidad de registros lo requiera, para mantener una consulta operativa manejable.

### RF-35. Limpiar filtros y recuperar vista general

El sistema debe permitir limpiar filtros aplicados para volver a la vista general del listado o consulta.

### RF-36. Registrar usuario sistema administrativo

El sistema debe permitir registrar un usuario sistema administrativo con datos básicos: nombre de usuario (login), credencial, estado y rol de uso.

### RF-37. Actualizar usuario sistema administrativo

El sistema debe permitir actualizar datos de un usuario sistema administrativo (incluyendo estado, credencial y rol de uso).

### RF-38. Consultar usuarios sistema administrativo

El sistema debe permitir consultar usuarios sistema administrativo mediante listados y búsqueda básica.

### RF-39. Ingreso al sistema (autenticación básica)

El sistema debe permitir el ingreso mediante nombre de usuario (login) y credencial.

### RF-40. Validar acceso por estado activo y rol de uso básico

Al ingresar al sistema, el sistema debe validar que el usuario sistema administrativo esté en estado **ACTIVO** y tenga un **rol de uso básico** definido para la operación de la fase 1.

---

## 5) Validaciones funcionales y restricciones operativas (VR)

### VR-01. Rango de edad de estudiantes

El sistema debe validar que el estudiante se encuentre dentro del rango operativo de **6 a 13 años**, calculando la edad a partir de la fecha de nacimiento y validándola con respecto a la **fecha de registro/matrícula**.

### VR-02. Límite de cupo por sección

El sistema no debe permitir que la cantidad de estudiantes registrados en una sección exceda su cupo máximo.

### VR-03. Límite institucional de cupo

El sistema no debe permitir definir un cupo máximo mayor a **35** estudiantes por sección.

### VR-04. Estados inactivos

El sistema no debe permitir nuevas asignaciones u operaciones de registro cuando una entidad involucrada esté en estado **INACTIVO** (por ejemplo: estudiante, docente, sección, asignatura, clase o usuario sistema administrativo, según el caso).

### VR-05. Advertencia de posible duplicado de estudiante

El sistema debe advertir al personal administrativo cuando detecte coincidencias exactas de nombres, apellidos y fecha de nacimiento antes de confirmar un nuevo registro de estudiante.

### VR-06. Parciales válidos

El sistema debe permitir registrar calificaciones únicamente para los parciales definidos en la fase 1: **Parcial 1** y **Parcial 2**.

### VR-07. Rango de nota

El sistema debe validar que la nota ingresada cumpla con la escala configurada por la institución (ejemplo: rango numérico institucional).

### VR-08. Coherencia mínima para registro de calificación

El sistema solo debe permitir registrar calificaciones cuando el estudiante y la clase estén en condiciones operativas válidas para la fase 1 (por ejemplo, estados activos y relaciones requeridas ya definidas).

### VR-09. Oferta académica mínima por sección (regla operativa)

El sistema debe permitir validar (o al menos advertir) cuando una sección operativa tenga menos de **5 clases** definidas en su oferta académica del período.

### VR-10. Asignación vigente única de estudiante a sección (fase 1)

El sistema debe mantener **una sola asignación vigente** estudiante–sección por cada estudiante en la fase 1.

### VR-11. Coherencia de clase con grado de la sección

El sistema debe validar que la asignatura asociada a una clase sea coherente con el grado de la sección a la que pertenece esa clase, según la malla o criterio académico definido por la institución.

### VR-12. Representante legal principal obligatorio para registro operativo del estudiante

El sistema debe exigir que cada estudiante quede asociado a **1 representante legal principal** como parte del registro operativo de la fase 1.

No se debe considerar válido el registro operativo de un estudiante si no se ha identificado o registrado previamente (o en la misma operación) a su representante legal principal.

### VR-13. Estado activo para ingreso al sistema

El sistema solo debe permitir ingreso cuando el usuario sistema administrativo esté en estado **ACTIVO**.

### VR-14. Rol de uso básico obligatorio en usuario sistema administrativo

Todo usuario sistema administrativo debe tener un **rol de uso básico** definido para poder operar en la fase 1.

### VR-15. Docente sin acceso al sistema administrativo en fase 1

En la fase 1, el sistema administrativo no debe contemplar acceso operativo para docentes como usuarios del sistema.

---

## 6) Requerimientos no funcionales (RNF) mínimos

### RNF-01. Usabilidad básica

La interfaz debe permitir que el personal administrativo realice registros y consultas sin pasos innecesarios ni navegación compleja.

### RNF-02. Búsqueda básica eficiente

El sistema debe ofrecer búsquedas rápidas por nombres y apellidos en los módulos principales (estudiantes, representantes, docentes y usuarios sistema administrativo).

### RNF-03. Consistencia de datos

El sistema debe reducir errores de captura mediante validaciones básicas de campos obligatorios, estados y relaciones.

### RNF-04. Auditoría mínima

El sistema debe almacenar fechas de creación y actualización de registros para auditoría administrativa básica.

### RNF-05. Privacidad de datos (mínima)

El sistema debe almacenar únicamente la información necesaria para la operación institucional de la fase 1.

### RNF-06. Control básico de acceso

El sistema debe requerir autenticación básica para el ingreso al sistema administrativo y validar estado del usuario sistema administrativo antes de permitir operación.

---

## 7) Alcance funcional de fase 1 (incluido / no incluido)

### 7.1 Incluido en fase 1

* Gestión de estudiantes
* Gestión de representantes legales
* Gestión de docentes
* Gestión de secciones
* Gestión de asignaturas
* Gestión de clases (oferta académica operativa por sección)
* Gestión de usuarios sistema administrativo (login, credencial, estado y rol de uso básico)
* Asociación entre entidades según el modelo de negocio definido
* Gestión operativa docente–sección (muchos a muchos)
* Asignación de docente a clase (según planificación operativa)
* Asignación vigente de estudiante a sección (única en fase 1)
* Registro y consulta de calificaciones por clase y por parcial (2 parciales)
* Consultas operativas básicas (dashboard)
* Autenticación básica para acceso al sistema administrativo

### 7.2 Fuera de alcance en fase 1

* Historial formal de cambios de sección del estudiante
* Gestión de pagos
* Control de asistencia
* Autorización avanzada por roles/permisos (matriz granular)
* Acceso operativo de docentes al sistema administrativo
* Reportes académicos avanzados (promedios generales, boletines formales, historial extendido)
* Notificaciones automáticas
* Integraciones con sistemas externos

---

## 8) Supuestos funcionales de trabajo (para mantener coherencia)

Estos supuestos se usan en la fase 1 y podrán refinarse posteriormente:

* El actor principal de operación del sistema es el personal administrativo / secretaría.
* La asociación **docente–sección** se maneja de forma básica, pero con cardinalidad **muchos a muchos**.
* La asociación **docente–clase** se usa para representar qué docente imparte una clase específica dentro de la planificación operativa.
* La relación **estudiante–sección** se maneja como una **asignación vigente exclusiva** en fase 1.
* El sistema maneja **2 parciales** en esta fase.
* La gestión de calificaciones es administrativa y básica (registro/actualización/consulta), usando como referencia principal la **clase**.
* El registro operativo del estudiante requiere identificar o registrar previamente (o en la misma operación) a su representante legal principal.
* El sistema administrativo de la fase 1 se opera con **usuarios sistema administrativo** en estado activo y con **rol de uso básico**.
* El docente se mantiene como actor del negocio y entidad del dominio, pero **sin acceso operativo** al sistema administrativo en la fase 1.
* Las reglas formales del dominio se detallarán con mayor precisión en `04-reglas-negocio-y-supuestos.md`.

---

## 9) Dependencias con otros documentos de análisis

Este documento debe mantenerse coherente con:

* `01-levantamiento-informacion-negocio.md` (contexto, actores, procesos, alcance del negocio)
* `03-modelo-conceptual-dominio.md` (entidades, relaciones y cardinalidades)
* `04-reglas-negocio-y-supuestos.md` (restricciones del dominio y supuestos formales)
* `05-glosario-alcance-y-limites.md` (términos, alcance y límites)

Si cambian reglas como cupo máximo, parciales, edad, política de asignaciones, cardinalidades docente–sección / docente–clase / estudiante–sección o política de acceso de usuarios sistema administrativo, se deben actualizar de forma consistente todos los documentos relacionados.

---

## 10) Resultado esperado de este documento

Al finalizar este documento, se debe contar con una definición clara de **qué debe hacer el sistema en la fase 1**, suficiente para:

1. construir el modelo conceptual del dominio con mejor precisión,
2. preparar casos de uso y pantallas,
3. pasar luego al modelo lógico de base de datos,
4. iniciar el diseño técnico con menor ambigüedad.
