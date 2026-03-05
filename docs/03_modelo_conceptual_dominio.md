# Modelo conceptual del dominio

## Propósito del documento

Este documento describe el **modelo conceptual del dominio** para la fase 1 del sistema de la **Unidad Educativa Niñitos Soñadores**, identificando:

* entidades principales del negocio,
* atributos conceptuales relevantes,
* relaciones entre entidades,
* cardinalidades,
* y observaciones de significado del dominio.

Su objetivo es representar **cómo se organiza la información del negocio** antes de pasar al modelo lógico de base de datos.

## Alcance del documento

Este archivo se enfoca en el nivel conceptual del dominio para la fase 1, incluyendo los elementos necesarios para:

* gestión de estudiantes y representantes legales,
* organización por secciones,
* gestión de docentes,
* gestión de asignaturas,
* planificación académica operativa mediante **clases**,
* registro de calificaciones por clase y parcial,
* control básico de acceso mediante **usuario sistema administrativo**.

## Límite de esta etapa

Este documento **no** define todavía:

* tablas físicas,
* tipos de datos SQL,
* claves foráneas implementadas,
* normalización,
* ni reglas técnicas de implementación.

---

## 1) Convenciones conceptuales usadas

Para mantener consistencia en la lectura del modelo conceptual:

* **pk_id** representa un identificador interno conceptual (clave primaria conceptual).
* Los atributos marcados como **opcionales** pueden no estar presentes en todos los registros de la fase 1.
* Los atributos marcados como **derivados** se calculan a partir de otros datos del dominio (por ejemplo, edad).
* Las cardinalidades se expresan en formato conceptual (por ejemplo: `0..M`, `1`, `0..1`, `0..35`).
* **Convención de lectura de cardinalidades:** la multiplicidad que aparece junto a una entidad indica cuántas instancias de **esa** entidad pueden asociarse con **una** instancia del otro lado.
* **Convención de estados (fase 1):** cuando una entidad tenga `estado`, sus valores se limitan a **ACTIVO/INACTIVO** (no se modelan estados adicionales en esta etapa).
* **Seguridad (fase 1):** `credencial` es un concepto de acceso; al pasar al modelo lógico/backend se implementa como `password_hash` (hash de contraseña), nunca como texto plano.
* La validación detallada de reglas (edad, cupos, estados, etc.) se formaliza en `04-reglas-negocio-y-supuestos.md`.

---

## 2) Entidades conceptuales del dominio

### 2.1 Estudiante

Representa a un niño matriculado en la institución dentro del rango de edad operativo de la fase 1.

**Atributos conceptuales:**

* `pk_id`
* `nombres`
* `apellidos`
* `fecha_nacimiento`
* `estado`
* `edad` *(derivado de `fecha_nacimiento`)*

**Observaciones conceptuales:**

* En la fase 1, cada estudiante debe estar asociado a **1 representante legal principal** para su gestión operativa.
* En la fase 1, el estudiante maneja una **asignación vigente** a sección (sin historial formal de cambios en este modelo conceptual).

---

### 2.2 Representante legal

Representa al adulto responsable del estudiante para fines de contacto y gestión institucional.

**Atributos conceptuales:**

* `pk_id`
* `nombres`
* `apellidos`
* `telefono`
* `correo_electronico`

**Observaciones conceptuales:**

* Un representante legal puede estar asociado a uno o varios estudiantes según la operación del negocio.

---

### 2.3 Sección

Representa una agrupación académica operativa de estudiantes para un período lectivo, definida por grado y paralelo.

**Atributos conceptuales:**

* `pk_id`
* `grado`
* `paralelo`
* `cupo_maximo`
* `anio_lectivo`
* `estado`
* `cantidad_estudiantes_registrados` *(derivado)*

**Observaciones conceptuales:**

* La sección agrupa estudiantes dentro de la fase 1.
* La sección también participa en la planificación académica mediante la **oferta de clases**.

---

### 2.4 Asignatura

Representa una materia académica del dominio escolar (por ejemplo: Matemática, Lengua, Ciencias Naturales).

**Atributos conceptuales:**

* `pk_id`
* `nombre`
* `area`
* `grado`
* `descripcion`
* `estado`

**Observaciones conceptuales:**

* La asignatura **no** almacena franja horaria en este modelo conceptual.
* La franja horaria pertenece a la **Clase** (oferta concreta en una sección).

---

### 2.5 Clase

Representa una **oferta académica concreta** de una asignatura dentro de una sección.

Ejemplo conceptual: *"Matemática de 5to A"* en una franja horaria determinada.

**Atributos conceptuales:**

* `pk_id`
* `franja_horaria`
* `estado`

**Observaciones conceptuales:**

* La clase conecta operativamente una **sección** con una **asignatura**.
* Una clase puede tener docente asignado en la planificación operativa.
* La gestión de calificaciones en fase 1 usa como referencia principal a la **Clase**.

---

### 2.6 Docente

Representa al profesor registrado por la institución para la organización académica de secciones y clases.

**Atributos conceptuales:**

* `pk_id`
* `nombres`
* `apellidos`
* `telefono` *(opcional)*
* `correo_electronico` *(opcional)*
* `estado`

**Observaciones conceptuales:**

* El docente puede estar asociado a secciones (relación operativa general).
* El docente también puede impartir clases específicas (relación operativa puntual).
* En la fase 1, el docente es entidad del dominio, pero **no** usuario operativo del sistema administrativo.

---

### 2.7 Calificación

Representa una nota registrada para un estudiante en una **clase** específica y un **parcial** determinado.

**Atributos conceptuales:**

* `pk_id`
* `numero_parcial`
* `nota`
* `observacion` *(opcional)*
* `fecha_registro` *(opcional)*

**Observaciones conceptuales:**

* En la fase 1, se consideran **2 parciales**.
* La calificación se interpreta principalmente en relación con una **Clase** (no con una asignatura abstracta).
* Las consultas por asignatura pueden obtenerse a partir de la clase asociada.

---

### 2.8 Usuario sistema administrativo

Representa la cuenta de acceso al sistema utilizada para la operación administrativa en fase 1.

**Atributos conceptuales:**

* `pk_id`
* `nombre_login`
* `credencial`
* `estado`
* `rol`

**Observaciones conceptuales:**

* Esta entidad modela acceso básico al sistema administrativo.
* En el modelo conceptual de fase 1, se mantiene **aislada** del dominio académico (sin relación obligatoria directa con estudiante, docente o sección).

---

## 3) Relaciones conceptuales y cardinalidades

A continuación se describen las relaciones del modelo conceptual final de fase 1.

### 3.1 Representante legal — **Representa** — Estudiante

**Significado:** un representante legal representa a uno o más estudiantes en la operación institucional.

**Cardinalidades:**

* Un **Representante legal** puede representar `0..M` **Estudiantes**.
* Cada **Estudiante** debe estar asociado a `1` **Representante legal** (principal en fase 1).

**Observación:**

* La política operativa del registro de estudiantes exige que el representante legal principal se identifique o registre antes (o dentro de la misma operación) del registro del estudiante.

---

### 3.2 Sección — **Agrupa** — Estudiante

**Significado:** una sección agrupa estudiantes para la operación académica del período.

**Cardinalidades:**

* Una **Sección** puede agrupar `0..35` **Estudiantes**.
* Un **Estudiante** pertenece a `0..1` **Sección** como asignación vigente en fase 1.

**Observación:**

* El valor `35` responde al límite institucional definido para fase 1.

---

### 3.3 Docente — **Trabaja** — Sección

**Significado:** relación operativa general que indica en qué secciones trabaja un docente.

**Cardinalidades:**

* Un **Docente** puede trabajar en `0..M` **Secciones**.
* Una **Sección** puede tener `0..M` **Docentes**.

**Observación:**

* Esta relación representa asociación operativa general docente–sección.
* La asignación puntual de un docente a una clase específica se modela aparte con la relación **Imparte**.

---

### 3.4 Sección — **Oferta** — Clase

**Significado:** una sección ofrece clases como parte de su planificación académica operativa.

**Cardinalidades:**

* Una **Sección** puede ofertar `0..M` **Clases**.
* Cada **Clase** pertenece a `1` **Sección**.

**Observación:**

* Esta relación reemplaza el modelado directo de planificación entre sección y asignatura como relación simple, haciendo explícita la entidad **Clase**.

---

### 3.5 Asignatura — **Se dicta como** — Clase

**Significado:** una asignatura se materializa operativamente como clases concretas en secciones.

**Cardinalidades:**

* Una **Asignatura** puede darse como `0..M` **Clases**.
* Cada **Clase** corresponde a `1` **Asignatura**.

**Observación:**

* Esto permite que una misma asignatura exista en múltiples secciones/horarios mediante clases distintas.

---

### 3.6 Docente — **Imparte** — Clase

**Significado:** un docente puede impartir clases específicas dentro de la planificación operativa.

**Cardinalidades (fase 1):**

* Un **Docente** puede impartir `0..M` **Clases**.
* Cada **Clase** puede tener `0..1` **Docente** asignado.

**Observación:**

* Se permite `0..1` en clase para admitir planificación inicial sin docente asignado.

---

### 3.7 Estudiante — **Obtiene** — Calificación

**Significado:** un estudiante obtiene calificaciones registradas en el sistema.

**Cardinalidades:**

* Un **Estudiante** puede tener `0..M` **Calificaciones**.
* Cada **Calificación** pertenece a `1` **Estudiante**.

---

### 3.8 Calificación — **Corresponde a** — Clase

**Significado:** cada calificación se registra respecto de una clase concreta.

**Cardinalidades:**

* Una **Clase** puede tener `0..M` **Calificaciones**.
* Cada **Calificación** corresponde a `1` **Clase**.

**Observación:**

* Este modelado hace más explícita la lógica del dominio para fase 1, evitando asociar la calificación directamente a una asignatura abstracta.

---

## 4) Lectura integrada del dominio (visión conceptual)

La operación conceptual de la fase 1 puede leerse así:

1. Un **Representante legal** representa a uno o más **Estudiantes**.
2. Cada **Estudiante** puede tener una asignación vigente a una **Sección**.
3. Una **Sección** oferta **Clases**.
4. Cada **Clase** corresponde a una **Asignatura**.
5. Un **Docente** puede trabajar con secciones y, además, impartir clases específicas.
6. Un **Estudiante** obtiene **Calificaciones**.
7. Cada **Calificación** corresponde a una **Clase** y a un parcial.
8. El **Usuario sistema administrativo** permite operar el sistema, pero se mantiene separado del dominio académico en el modelo conceptual.

Esta lectura busca que el modelo sea **simple, autoexplicativo y coherente** con el flujo operativo de la fase 1.

---

## 5) Observaciones conceptuales de coherencia (fase 1)

### 5.1 Sobre edad del estudiante

* `edad` es un atributo **derivado** de `fecha_nacimiento`.
* La regla exacta de validación del rango (6–13) se formaliza en el documento de reglas y requerimientos, con referencia a la **fecha de registro/matrícula**.

### 5.2 Sobre cupo de sección

* `cantidad_estudiantes_registrados` es un atributo **derivado** para control operativo.
* La validación del cupo máximo se formaliza en reglas de negocio / requerimientos.

### 5.3 Sobre la coexistencia de Docente–Sección y Docente–Clase

* **Trabaja (Docente–Sección)** modela una asociación operativa general.
* **Imparte (Docente–Clase)** modela la asignación específica de impartición.

Ambas relaciones pueden coexistir en la fase 1 sin contradicción si se interpretan con ese alcance.

### 5.4 Sobre calificaciones y clases

* En este modelo conceptual, la referencia principal de una calificación es la **Clase**.
* Esto facilita la lectura del dominio y reduce ambigüedad al momento de pasar al modelo lógico.

---

## 6) Límites conceptuales del modelo (qué aún no se representa)

Para mantener simplicidad en la fase 1, este modelo conceptual **no** representa explícitamente:

* historial de cambios de sección del estudiante,
* períodos de evaluación más allá de 2 parciales,
* permisos granulares por usuario,
* asistencia,
* pagos,
* boletines o reportes académicos complejos,
* historial formal de versiones de planificación académica.

---

## 7) Coherencia con los demás documentos

Este modelo conceptual debe mantenerse alineado con:

* `01-levantamiento-informacion-negocio.md` (contexto y procesos del negocio)
* `02-levantamiento-requerimientos.md` (RF, VR, RNF de fase 1)
* `04-reglas-negocio-y-supuestos.md` (reglas formales del dominio)
* `05-glosario-alcance-y-limites.md` (definiciones semánticas y límites)

Si se modifica el significado de **Clase**, las cardinalidades de **Docente–Clase**, **Docente–Sección**, **Estudiante–Sección**, o el criterio de gestión de calificaciones, se debe actualizar este documento y los demás archivos relacionados.

---

## 8) Resultado esperado de este documento

Al finalizar este documento, se debe contar con una representación conceptual clara del dominio que permita:

1. validar coherencia del análisis con el negocio,
2. preparar el modelo lógico de base de datos,
3. reducir ambigüedades antes del diseño técnico,
4. servir de referencia para pantallas, formularios y validaciones de la fase 1.
