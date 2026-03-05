# Levantamiento de información del negocio

## Propósito del documento

Este documento reúne el levantamiento inicial de información del negocio para comprender el contexto operativo de la **Unidad Educativa Niñitos Soñadores**, sus actores, sus procesos principales y los problemas actuales que motivan el desarrollo del sistema.

## Alcance del documento

Este archivo describe el negocio desde una perspectiva funcional y organizativa (qué hace la institución y qué necesita resolver), sin entrar todavía en diseño técnico, modelo lógico de base de datos ni normalización.

## Límite de esta etapa

La información aquí presentada sirve como base para el análisis posterior y puede refinarse conforme se aclaren reglas, procesos y requerimientos.

---

## 1) Identificación general del negocio

**Nombre de la institución:** Unidad Educativa Niñitos Soñadores
**Tipo de institución:** Escuela pequeña/mediana
**Ubicación referencial:** Norte de Guayaquil
**Población objetivo:** Niños de **6 a 13 años** (Educación General Básica)

La institución requiere un sistema para organizar y consultar información escolar básica de forma confiable, ya que actualmente parte de la gestión se realiza con registros manuales y herramientas no integradas.

---

## 2) Situación actual (cómo opera hoy)

Actualmente, la información académica y administrativa se maneja mediante:

* cuadernos
* archivos sueltos
* listados informales
* comunicación por WhatsApp

Este esquema de trabajo provoca dificultades operativas, especialmente cuando se necesita confirmar información con rapidez, mantener consistencia entre registros o verificar cambios realizados durante el año lectivo.

---

## 3) Problemas detectados en la operación actual

A partir del levantamiento inicial, se identifican los siguientes problemas:

### 3.1 Duplicidad de registros

* Posibilidad de registrar estudiantes más de una vez por errores de transcripción o falta de validación.
* Dificultad para detectar coincidencias de nombres, apellidos y fechas de nacimiento.

### 3.2 Control deficiente de cupos

* No siempre se conoce con precisión cuántos estudiantes hay por sección.
* Riesgo de exceder el cupo permitido por sección.

### 3.3 Información dispersa

* Datos de estudiantes, representantes, docentes, secciones, asignaturas, clases y calificaciones se encuentran repartidos en diferentes medios.
* La consulta depende de revisar varias fuentes manualmente.

### 3.4 Baja trazabilidad administrativa

* Dificultad para saber cambios de sección, correcciones o estado actualizado de registros.
* Falta de una referencia central para validaciones administrativas.

### 3.5 Dificultad de consulta académica

* Complejidad para revisar rápidamente qué clases se ofertan por sección y qué asignaturas corresponden a esas clases.
* Complejidad para consultar qué nota obtuvo cada estudiante por parcial dentro de una clase.

### 3.6 Control de acceso no centralizado

* No existe un control básico unificado de usuarios del sistema para restringir quién puede ingresar y operar módulos.
* Se requiere diferenciar al menos el tipo de uso (rol) del usuario del sistema administrativo para ordenar la operación.

---

## 4) Actores del negocio identificados

En el contexto de la fase inicial, se reconocen los siguientes actores principales:

### 4.1 Personal administrativo / secretaría

Responsable principal de registrar, actualizar y consultar información general del sistema (estudiantes, representantes, docentes, secciones, asignaturas, clases y calificaciones).

### 4.2 Docentes

Participan en la operación académica de las secciones y en el contexto institucional de las clases impartidas. Según la organización real del plantel, un docente puede trabajar con varias secciones y una sección puede tener varios docentes. Además, un docente puede impartir varias clases en la planificación operativa.

### 4.3 Estudiantes

Niños matriculados en la institución dentro del rango de edad establecido (6 a 13 años), organizados por grado/paralelo en un año lectivo.

### 4.4 Representantes legales

Adultos responsables de los estudiantes para fines de contacto y gestión institucional.

### 4.5 Usuario sistema administrativo

Persona autorizada para ingresar al sistema con credenciales y un rol de uso básico, con el fin de operar módulos permitidos en la fase 1.

---

## 5) Información del negocio que se necesita administrar

Con base en el contexto levantado, la institución necesita gestionar al menos la siguiente información:

### 5.1 Estudiantes

* Identificación interna
* Nombres
* Apellidos
* Fecha de nacimiento
* Estado (activo/inactivo)
* Edad (dato derivado a partir de la fecha de nacimiento, usado en validación operativa)
* Asociación con representante legal principal (fase 1)
* Asociación con sección (asignación vigente en fase 1)

### 5.2 Representantes legales

* Identificación interna
* Nombres
* Apellidos
* Teléfono
* Correo electrónico
* Relación con uno o más estudiantes (según operación administrativa)

### 5.3 Docentes

* Identificación interna
* Nombres
* Apellidos
* Estado
* Datos de contacto básicos (si aplica)
* Asociación con secciones (relación operativa muchos a muchos en fase 1)
* Asociación con clases impartidas (planificación operativa)

### 5.4 Secciones

* Identificación interna
* Grado
* Paralelo
* Año lectivo
* Cupo máximo
* Estado
* Cantidad de estudiantes registrados (para control de cupo)

### 5.5 Asignaturas

* Identificación interna
* Nombre de la asignatura
* Área
* Grado al que corresponde
* Descripción
* Estado

### 5.6 Clases (fase 1)

> En este contexto, una **Clase** representa una oferta concreta de una asignatura dentro de una sección (por ejemplo, “Matemática de 5to A” en una franja horaria determinada).

* Identificación interna
* Franja horaria
* Estado
* Asociación con sección (la sección que la oferta)
* Asociación con asignatura (la asignatura que se dicta como clase)
* Asociación con docente (cuando se define quién la imparte)

### 5.7 Calificaciones (fase 1)

* Identificación interna
* Nota por estudiante
* Clase
* Parcial (**Parcial 1** y **Parcial 2**)
* Fecha de registro (si aplica)
* Observación (si aplica)

> A nivel de consulta administrativa, las calificaciones también pueden visualizarse por asignatura y parcial, derivando la asignatura desde la clase.

### 5.8 Usuarios sistema administrativo (fase 1)

* Identificación interna
* Nombre de usuario (login)
* Credencial
* Estado (activo/inactivo)
* Rol de uso (básico)

---

## 6) Procesos generales observados (nivel negocio, no técnico)

Durante el levantamiento inicial se identifican los siguientes procesos generales del negocio:

### 6.1 Registro de estudiantes

Ingreso y actualización de datos básicos del estudiante, validando consistencia de información y evitando duplicados evidentes.

Como parte del flujo operativo de la fase 1, para registrar un estudiante se debe **identificar o registrar primero** a su representante legal principal, de modo que el estudiante quede asociado al representante en el mismo proceso de registro.

### 6.2 Registro de representantes legales

Ingreso y actualización de datos de contacto del responsable del estudiante.

### 6.3 Organización por secciones

Asignación de estudiantes a una sección (grado/paralelo) dentro de un año lectivo, con control de cupo máximo.

En la fase 1 se maneja una **asignación vigente única** por estudiante (sin historial formal de cambios de sección).

### 6.4 Gestión de docentes por sección

Asociación de docentes a secciones según la organización institucional.

En la operación observada, una sección puede trabajar con varios docentes y un docente puede trabajar con varias secciones.

### 6.5 Planificación académica por sección (clases)

Definición de las **clases** ofertadas por sección durante el año lectivo. Cada clase corresponde a una asignatura y puede incluir su franja horaria, procurando coherencia entre el grado de la sección y el grado de la asignatura.

### 6.6 Asignación de docentes a clases

Definición de qué docente imparte una clase específica dentro de la planificación operativa, cuando esa asignación esté disponible.

### 6.7 Registro de calificaciones por parcial

Registro y consulta de notas por estudiante, **clase** y parcial (2 parciales en la fase inicial).

A nivel de consulta, también puede requerirse visualización por asignatura y parcial a partir de la clase asociada.

### 6.8 Control básico de acceso al sistema

Gestión de usuarios sistema administrativo para permitir ingreso con credenciales, validar estado activo y aplicar un rol de uso básico durante la operación de la fase 1.

---

## 7) Necesidad del sistema (visión de negocio)

La institución necesita un sistema que permita:

* centralizar la información escolar básica
* reducir errores de registro y duplicados
* controlar cupos por sección
* consultar rápidamente estudiantes, representantes, docentes y secciones
* organizar clases ofertadas por sección (asociadas a asignaturas)
* asignar docentes a clases dentro de la planificación operativa
* registrar y consultar calificaciones por clase y por parcial
* consultar calificaciones por asignatura y parcial (a partir de las clases)
* controlar de forma básica el acceso de usuarios sistema administrativo (login, estado y rol de uso)
* mejorar la consistencia administrativa durante el año lectivo

El objetivo principal de esta fase no es automatizar todos los procesos escolares, sino establecer una base confiable de gestión académica y administrativa.

---

## 8) Alcance general de la fase inicial (visión de negocio)

En esta etapa inicial, el enfoque del sistema estará orientado a:

* registro y consulta de información básica de estudiantes, representantes, docentes, secciones, asignaturas y clases
* organización de estudiantes por sección con control de cupo (asignación vigente única en fase 1)
* gestión operativa de asociación docente–sección (relación muchos a muchos, sin roles docentes especializados)
* definición de clases por sección (asignatura + franja horaria + estado)
* asignación de docente a clase (según planificación operativa de fase 1)
* registro básico de calificaciones por clase y por parcial (2 parciales)
* gestión básica de usuarios sistema administrativo (login, credencial, estado y rol de uso)
* consultas operativas básicas para apoyo administrativo

No forma parte del alcance inicial (por ahora):

* historial formal de cambios de sección del estudiante
* pagos
* asistencia
* autorización avanzada por roles/permisos (matriz granular)
* reportes académicos complejos
* historial académico ampliado

---

## 9) Restricciones y consideraciones del negocio identificadas (preliminares)

Estas consideraciones se registran aquí como parte del levantamiento y se formalizarán después en el documento de reglas de negocio y requerimientos:

* El rango de edad operativo considerado para estudiantes es de **6 a 13 años**.
* La validación del rango de edad (6–13) se realiza con respecto a la **fecha de registro/matrícula**.
* Existe un límite institucional de **35 estudiantes por sección**.
* Se deben evitar nuevas asignaciones con registros marcados como inactivos.
* En la fase inicial, el sistema debe manejar **2 parciales** de evaluación.
* En la fase 1, cada estudiante debe quedar asociado a **1 representante legal principal** para su gestión operativa.
* En la fase 1, el registro operativo de un estudiante requiere identificar o registrar previamente a su representante legal principal.
* En la fase 1, la relación docente–sección se maneja operativamente como **muchos a muchos**.
* La planificación académica por sección se representa mediante **clases**; cada clase corresponde a una asignatura y puede incluir franja horaria.
* La asignatura asociada a una clase debe ser coherente con el grado de la sección a la que pertenece esa clase.
* Solo usuarios sistema administrativo en estado **ACTIVO** deben poder operar el sistema.
* Cada usuario sistema administrativo debe tener un **rol de uso básico** definido para la operación de fase 1.
* Pueden presentarse condiciones logísticas (por ejemplo, contingencias climáticas) que afecten la operación presencial de clases en ciertos períodos.

---

## 10) Resultado esperado de este levantamiento

Como resultado de este levantamiento de información del negocio, se espera contar con una base clara para:

1. definir requerimientos funcionales y no funcionales de la fase 1,
2. construir el modelo conceptual del dominio,
3. documentar reglas de negocio y supuestos,
4. preparar posteriormente el modelo lógico y la normalización.

---

## 11) Observación de coherencia para los siguientes documentos

Este documento describe el **negocio y su contexto**.
En los siguientes archivos se deberá mantener coherencia con esta información:

* `02-levantamiento-requerimientos.md` → qué debe hacer el sistema.
* `03-modelo-conceptual-dominio.md` → entidades, relaciones y cardinalidades.
* `04-reglas-negocio-y-supuestos.md` → restricciones formales y decisiones de alcance.
* `05-glosario-alcance-y-limites.md` → términos, alcance y límites semánticos.

Cualquier cambio relevante en actores, procesos, alcance o restricciones debe reflejarse en todos los documentos relacionados para mantener consistencia de análisis.
