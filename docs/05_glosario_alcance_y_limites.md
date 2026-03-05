# Glosario, alcance y límites

## Propósito del documento

Este documento consolida:

* términos clave del dominio,
* criterios semánticos de uso,
* alcance funcional de la fase 1,
* y límites del análisis actual,

con el objetivo de mantener coherencia entre el levantamiento del negocio, los requerimientos, el modelo conceptual del dominio y las reglas de negocio.

## Alcance del documento

Aplica a la **fase 1** del sistema de la **Unidad Educativa Niñitos Soñadores** y funciona como referencia de vocabulario y fronteras del análisis.

## Límite de esta etapa

Este documento no sustituye al modelo conceptual, a los requerimientos ni a las reglas de negocio; su función es ayudar a que todos esos documentos usen el mismo lenguaje y el mismo alcance.

---

## 1) Glosario de términos clave (fase 1)

### 1.1 Estudiante

Niño matriculado en la institución dentro del rango operativo considerado en la fase 1 (**6 a 13 años**).

**Notas de uso:**

* Su edad se calcula a partir de la fecha de nacimiento.
* La validación del rango de edad se realiza con respecto a la **fecha de registro/matrícula**.
* En fase 1, debe quedar asociado a **1 representante legal principal** para su gestión operativa.
* Maneja una sola asignación vigente a sección (sin historial formal en esta etapa).

---

### 1.2 Representante legal

Adulto responsable del estudiante para fines de contacto y gestión institucional.

**Notas de uso:**

* Puede estar asociado a uno o varios estudiantes.
* En fase 1, el registro operativo del estudiante exige identificar o registrar primero a su representante legal principal.

---

### 1.3 Docente

Profesor registrado por la institución para la organización académica de secciones y clases.

**Notas de uso:**

* Es una entidad del dominio académico.
* Puede estar asociado a secciones (relación general de trabajo).
* Puede impartir clases específicas (relación puntual).
* En fase 1, no tiene acceso operativo al sistema administrativo como usuario del sistema.

---

### 1.4 Sección

Agrupación académica operativa de estudiantes, definida por **grado** (1ro, 2do, 3ro… de Educación General Básica), paralelo y año lectivo.

**Notas de uso:**

* Tiene cupo máximo.
* Agrupa estudiantes en fase 1.
* Participa en la oferta de clases.
* En el análisis actual se considera un límite institucional de **35 estudiantes** por sección.

---

### 1.5 Asignatura

Materia académica del dominio escolar (por ejemplo: Matemática, Lengua, Ciencias Naturales).

**Notas de uso:**

* Se modela como catálogo académico.
* Tiene nombre, área, grado, descripción y estado.
* En fase 1, se registra **una asignatura por grado** (ej.: “Matemática” grado=1, “Matemática” grado=4).
* Recomendación para el modelo lógico: `UNIQUE(nombre, grado)` para evitar duplicados.
* **No** almacena franja horaria en fase 1.
* La franja horaria se modela en la **Clase**.

---

### 1.6 Clase

Unidad de planificación académica operativa en fase 1.

Representa una **oferta concreta** de una asignatura dentro de una sección.

**Ejemplo conceptual:** “Matemática de 5to A” en una franja horaria específica.

**Notas de uso:**

* Vincula operativamente:

 * una **Sección**,
 * una **Asignatura**,
 * una **franja horaria**,
 * y un **estado**.
* Puede tener docente asignado (según planificación operativa).
* Se usa como referencia principal para el registro de calificaciones.

---

### 1.7 Calificación

Registro de una nota de un estudiante en una **clase** y un **parcial** determinado.

**Notas de uso:**

* En fase 1 se manejan **2 parciales** (Parcial 1 y Parcial 2).
* La referencia principal del registro es **estudiante + clase + parcial**.
* La consulta por asignatura se considera **derivada** desde la clase asociada.
* Puede incluir observación y fecha de registro como datos opcionales.

---

### 1.8 Usuario sistema administrativo

Cuenta de acceso utilizada para ingresar y operar el sistema administrativo en fase 1.

**Notas de uso:**

* Debe tener login, credencial, estado y rol de uso.
* `credencial` (conceptual) se implementa como **`password_hash`** (hash de contraseña).
* Solo usuarios en estado **ACTIVO** pueden ingresar.
* En el modelo conceptual de fase 1 se mantiene aislado del dominio académico (sin relación obligatoria directa con entidades académicas).

---

### 1.9 Estado (activo/inactivo)

Condición operativa básica usada en entidades del sistema para permitir o restringir nuevas operaciones.

**Notas de uso:**

* Un registro inactivo puede seguir siendo visible para consulta administrativa.
* Las reglas de cuándo bloquear operaciones por estado se detallan en el documento de reglas de negocio.
* En fase 1, el estado se limita estrictamente a **ACTIVO/INACTIVO** (no se manejan otros estados).

---

### 1.10 Asignación vigente (estudiante–sección)

Relación operativa de fase 1 que indica en qué sección se encuentra actualmente el estudiante.

**Notas de uso:**

* En esta etapa se maneja una sola asignación vigente por estudiante.
* No se modela historial formal de cambios de sección.

---

### 1.11 Oferta de clases por sección

Conjunto de clases definidas para una sección en la fase 1, como representación de la planificación académica operativa.

**Notas de uso:**

* Reemplaza la idea de planificar solamente con una relación directa sección–asignatura.
* Hace explícita la franja horaria y el estado de cada oferta concreta (Clase).

---

### 1.12 Rol de uso (básico)

Clasificación operativa mínima de un usuario sistema administrativo en fase 1 para habilitar el uso del sistema.

**Notas de uso:**

* No equivale aún a una matriz avanzada de permisos.
* La fase 1 solo requiere validar la existencia de un rol de uso básico definido.

---

## 2) Criterios semánticos para mantener coherencia entre documentos

Para evitar ambigüedades, se adoptan los siguientes criterios de redacción y significado:

### 2.1 Calificaciones

**Usar como referencia principal:**

* “calificación por estudiante + clase + parcial” ✅

**Evitar como formulación principal en fase 1:**

* “calificación por estudiante + asignatura + parcial” ❌ (puede usarse solo como consulta derivada)

---

### 2.2 Franja horaria

**Uso correcto en fase 1:**

* La franja horaria pertenece a **Clase** ✅

**Uso que debe evitarse en el análisis actual:**

* Franja horaria como atributo de **Asignatura** ❌

---

### 2.3 Registro de estudiante y representante

**Criterio adoptado para fase 1:**

* El registro operativo del estudiante requiere identificar o registrar primero al representante legal principal, y dejar la asociación definida en la misma operación ✅

**Evitar en el alcance actual:**

* Flujo de estudiante “pendiente” sin representante legal principal como política normal de operación ❌

---

### 2.4 Usuario del sistema

**Nombre adoptado:**

* **Usuario sistema administrativo** ✅

**Evitar para mantener consistencia documental:**

* “Usuario interno del sistema” (si el resto de documentos ya fue actualizado) ❌

---

### 2.5 Planeación académica en fase 1

**Criterio adoptado:**

* La planificación académica operativa por sección se representa mediante **Clases** ✅

**Evitar como formulación simplificada principal (si oculta la clase):**

* Tratar toda la planificación solo como relación directa sección–asignatura ❌

---

## 3) Alcance consolidado de fase 1 (visión semántica y funcional)

### 3.1 Incluye (fase 1)

* Gestión de estudiantes
* Gestión de representantes legales
* Gestión de docentes
* Gestión de secciones
* Gestión de asignaturas
* Gestión de **clases** (oferta académica operativa por sección)
* Gestión de usuarios sistema administrativo (login, credencial, estado y rol de uso básico)
* Asignación vigente de estudiante a sección (única en fase 1)
* Asociación docente–sección (relación operativa general, muchos a muchos)
* Asignación de docente a clase (relación operativa puntual)
* Registro y consulta de calificaciones por estudiante, clase y parcial (2 parciales)
* Consultas derivadas de calificaciones por asignatura (a partir de la clase)
* Consultas operativas básicas para apoyo administrativo
* Autenticación básica para acceso al sistema administrativo

---

### 3.2 No incluye (fase 1)

* Historial formal de cambios de sección del estudiante
* Pagos y facturación
* Control de asistencia
* Acceso operativo de docentes al sistema administrativo
* Matriz avanzada de permisos / autorización granular
* Reportes académicos avanzados (boletines formales, promedios complejos, historial extendido)
* Notificaciones automáticas
* Integraciones con sistemas externos
* Gestión académica avanzada de períodos más allá de los 2 parciales definidos

---

## 4) Límites conceptuales y de modelado en esta etapa

Para mantener simple el análisis (objetivo de práctica), en esta etapa:

1. **No** se modela historial de asignaciones estudiante–sección.
2. **No** se modela una estructura avanzada de períodos o quimestres.
3. **No** se modela una matriz de permisos por módulo/acción.
4. **No** se modelan relaciones de auditoría completas (solo referencias mínimas en requerimientos).
5. **No** se fuerza modelado complejo de reglas escolares; se prioriza coherencia operativa básica.
6. **Sí** se usa la entidad **Clase** para que el modelo sea más claro y autoexplicativo.

---

## 5) Relaciones conceptuales clave (resumen de lectura)

Este bloque no reemplaza al modelo conceptual, pero resume cómo deben interpretarse los vínculos principales en fase 1:

* **Representante legal** representa a **Estudiante**.
* **Sección** agrupa **Estudiante**.
* **Docente** trabaja en **Sección** (relación general).
* **Sección** oferta **Clase**.
* **Asignatura** se dicta como **Clase**.
* **Docente** imparte **Clase** (relación puntual).
* **Estudiante** obtiene **Calificación**.
* **Calificación** corresponde a **Clase**.
* **Usuario sistema administrativo** opera el sistema administrativo (sin relación obligatoria directa con entidades académicas en el modelo conceptual de fase 1).

---

## 6) Reglas de referencia rápida (semánticas) para revisión documental

Estas reglas sirven como guía rápida al editar documentos relacionados:

1. Si aparece “franja horaria”, revisar que esté asociada a **Clase**.
2. Si aparece “calificación”, revisar que la referencia principal sea **Clase**.
3. Si aparece “registro de estudiante”, revisar que se mencione representante legal principal obligatorio.
4. Si aparece “usuario del sistema”, usar **Usuario sistema administrativo**.
5. Si aparece “edad”, mantener que se valida respecto a la **fecha de registro/matrícula**.
6. Si aparece “planeación académica por sección”, priorizar el término **clases** / **oferta de clases**.

---

## 7) Dependencias con los demás documentos

Este documento debe mantenerse coherente con:

* `01-levantamiento-informacion-negocio.md`
* `02-levantamiento-requerimientos.md`
* `03-modelo-conceptual-dominio.md`
* `04-reglas-negocio-y-supuestos.md`

Cualquier cambio en términos clave (por ejemplo: **Clase**, **Calificación**, **Usuario sistema administrativo**, política de registro de estudiante, criterio de edad o alcance de fase 1) debe reflejarse de forma consistente en todos los documentos.

---

## 8) Resultado esperado de este documento

Al finalizar este archivo, se debe contar con una referencia semántica y de alcance que permita:

1. escribir documentos consistentes entre sí,
2. evitar contradicciones de vocabulario,
3. mantener claro qué entra y qué no entra en la fase 1,
4. facilitar el paso al modelo lógico y a la implementación sin confusión conceptual.
