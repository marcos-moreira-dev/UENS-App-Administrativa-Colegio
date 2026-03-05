# Reglas de negocio y supuestos

## Propósito del documento

Este documento formaliza las **reglas de negocio** y los **supuestos de trabajo** de la fase 1 del sistema para la **Unidad Educativa Niñitos Soñadores**, con el fin de mantener consistencia entre:

* el contexto del negocio,
* los requerimientos,
* el modelo conceptual del dominio,
* y las decisiones de alcance de la fase inicial.

## Alcance del documento

Se incluyen reglas operativas y restricciones del dominio relacionadas con:

* estudiantes y representantes legales,
* secciones y cupos,
* docentes,
* asignaturas,
* clases (oferta académica operativa),
* calificaciones,
* usuarios sistema administrativo,
* y supuestos necesarios para practicar e implementar la fase 1 con simplicidad.

## Límite de esta etapa

Este documento **no** define todavía constraints SQL finales ni mecanismos técnicos de implementación; su función es establecer las reglas del dominio y criterios operativos que luego deberán reflejarse en el diseño lógico y en el código.

---

## 1) Convenciones de redacción

* **RN-XX**: Regla de negocio.
* **SA-XX**: Supuesto de análisis / alcance.
* Cuando se menciona “fase 1”, se refiere al alcance inicial definido en los documentos de análisis.
* Las cardinalidades formales del modelo se describen en `03-modelo-conceptual-dominio.md`; aquí se enfatiza la **regla operativa**.
* **Estados (fase 1):** se usan únicamente **ACTIVO/INACTIVO** en las entidades que lo requieran; no se modelan estados adicionales en esta etapa.

---

## 2) Reglas de negocio (RN)

### 2.1 Reglas sobre estudiantes y representantes legales

#### RN-01. Rango operativo de edad del estudiante

El sistema considera como población objetivo de la fase 1 a estudiantes dentro del rango de **6 a 13 años**.

#### RN-02. Cálculo y validación de edad

La edad del estudiante se calcula a partir de la **fecha de nacimiento** y la validación del rango operativo (**6–13**) se realiza con respecto a la **fecha de registro/matrícula**.

#### RN-03. Datos mínimos del estudiante

Para el registro de un estudiante se deben contar, como mínimo, con:

* nombres,
* apellidos,
* fecha de nacimiento,
* estado.

#### RN-04. Detección administrativa de posible duplicado

Antes de confirmar el registro de un nuevo estudiante, el sistema debe permitir advertir posibles duplicados cuando existan coincidencias relevantes (por ejemplo: nombres, apellidos y fecha de nacimiento).

#### RN-05. Estado operativo del estudiante

Un estudiante en estado **INACTIVO** no debe participar en nuevas asignaciones u operaciones que requieran condición activa, salvo consultas históricas/administrativas básicas permitidas por la fase 1.

#### RN-06. Representante legal principal obligatorio (fase 1)

En la fase 1, cada estudiante debe quedar asociado a **1 representante legal principal** para su gestión operativa.

#### RN-07. Política de registro de estudiante con representante

Para registrar operativamente a un estudiante, se debe **identificar o registrar primero** al representante legal principal, de modo que la asociación estudiante–representante quede definida en la misma operación de registro.

#### RN-08. Relación representante–estudiante

Un representante legal puede estar asociado a **uno o varios estudiantes**, de acuerdo con la realidad operativa de la institución.

#### RN-09. Actualización de datos de representante legal

Los datos de contacto del representante legal pueden actualizarse por gestión administrativa, manteniendo la coherencia de la asociación con los estudiantes relacionados.

---

### 2.2 Reglas sobre secciones y cupos

#### RN-10. Definición básica de sección

Una sección se define operativamente por al menos:

* grado,
* paralelo,
* año lectivo,
* cupo máximo,
* estado.

#### RN-11. Límite institucional de cupo por sección

El cupo máximo permitido por sección en la fase 1 es de **35 estudiantes**.

#### RN-12. Control de cupo en asignación de estudiante

No se debe permitir asignar un estudiante a una sección si dicha asignación provoca que la cantidad de estudiantes registrados exceda el cupo máximo de la sección.

#### RN-13. Asignación vigente única estudiante–sección (fase 1)

En la fase 1, cada estudiante mantiene una sola **asignación vigente** a sección.

#### RN-14. Cambio administrativo de sección (fase 1)

Cuando un estudiante cambia de sección, en la fase 1 se actualiza su **asignación vigente** (sin modelar historial formal de cambios dentro de este alcance).

#### RN-15. Estado operativo de sección

Una sección en estado **INACTIVO** no debe admitir nuevas asignaciones de estudiantes ni nuevas operaciones de planificación académica de fase 1.

---

### 2.3 Reglas sobre asignaturas y clases (planificación académica operativa)

#### RN-16. Asignatura como catálogo académico

La asignatura representa una materia del dominio escolar (por ejemplo: Matemática, Lengua, Ciencias) y **no** almacena franja horaria en el modelo de fase 1.

#### RN-17. Clase como unidad de planificación operativa

La **Clase** representa una oferta académica concreta de una asignatura dentro de una sección.

En términos operativos, una clase conecta:

* una **sección**,
* una **asignatura**,
* una **franja horaria**,
* y un **estado**.

#### RN-18. Franja horaria pertenece a Clase

La **franja horaria** es un dato propio de la **Clase** (oferta concreta), no de la asignatura.

#### RN-19. Coherencia de clase con grado de sección

La asignatura asociada a una clase debe ser coherente con el grado de la sección a la que pertenece esa clase, según la malla o criterio académico operativo de la institución.

#### RN-20. Oferta de clases por sección

La planificación académica de la fase 1 se representa mediante la **oferta de clases por sección**.

Una sección puede tener varias clases y cada clase pertenece a una única sección.

#### RN-21. Estado operativo de clase

Una clase en estado **INACTIVO** no debe utilizarse para nuevas operaciones de registro de calificaciones ni para nuevas asignaciones operativas que requieran clase activa, salvo consulta administrativa permitida.

#### RN-22. Oferta académica mínima por sección (regla operativa)

El sistema debe permitir validar o advertir cuando una sección operativa tenga menos de **5 clases** definidas en su oferta académica del período.

---

### 2.4 Reglas sobre docentes

#### RN-23. Registro básico de docente

El docente se registra con datos básicos de identificación y estado; teléfono y correo electrónico pueden ser opcionales en la fase 1.

#### RN-24. Relación operativa general docente–sección

La relación **Docente–Sección** se maneja operativamente como **muchos a muchos** en la fase 1:

* un docente puede trabajar en varias secciones,
* una sección puede tener varios docentes.

#### RN-25. Relación operativa puntual docente–clase

La relación **Docente–Clase** representa qué docente imparte una clase específica dentro de la planificación operativa.

#### RN-26. Asignación de docente a clase (flexible en fase 1)

En la fase 1, una clase puede existir inicialmente **sin docente asignado** (planificación en progreso), por lo que se admite una asignación posterior del docente.

#### RN-27. Docente sin acceso operativo al sistema administrativo (fase 1)

El docente forma parte del dominio académico, pero **no** opera el sistema administrativo como usuario del sistema en la fase 1.

---

### 2.5 Reglas sobre calificaciones

#### RN-28. Unidad de registro de calificación en fase 1

En la fase 1, una calificación se registra usando como referencia principal:

* **estudiante**,
* **clase**,
* **parcial** (1 o 2),
* y **nota**.

#### RN-29. Parciales permitidos

La fase 1 contempla únicamente **2 parciales**: **Parcial 1** y **Parcial 2**.

#### RN-30. Nota dentro de escala institucional

La nota registrada debe cumplir con la escala definida por la institución (el rango exacto puede parametrizarse o definirse en implementación, según alcance técnico).

#### RN-31. Condición operativa mínima para registrar calificación

Para registrar una calificación en la fase 1, el sistema debe verificar condiciones operativas mínimas, al menos:

* estudiante válido para operación,
* clase válida para operación,
* parcial permitido,
* nota válida según escala institucional.

#### RN-32. Consulta de calificaciones por asignatura (derivada)

Aunque el registro de la calificación se hace por **clase**, la consulta administrativa puede visualizar calificaciones por **asignatura** derivando dicha información desde la clase asociada.

#### RN-33. Datos opcionales de calificación

La fecha de registro y la observación de calificación son datos opcionales en la fase 1, según el flujo administrativo implementado.

---

### 2.6 Reglas sobre usuarios sistema administrativo (acceso)

#### RN-34. Usuario sistema administrativo como entidad de acceso

El **Usuario sistema administrativo** representa la cuenta utilizada para ingresar y operar el sistema administrativo en la fase 1.

#### RN-35. Datos mínimos del usuario sistema administrativo

Todo usuario sistema administrativo debe contar, como mínimo, con:

* nombre de usuario (login),
* credencial,
* estado,
* rol de uso.

#### RN-36. Estado activo para ingreso al sistema

Solo un usuario sistema administrativo en estado **ACTIVO** puede ingresar al sistema.

#### RN-37. Rol de uso básico obligatorio (fase 1)

Todo usuario sistema administrativo debe tener un **rol de uso básico** definido para poder operar módulos de la fase 1.

#### RN-38. Aislamiento conceptual respecto al dominio académico

En el modelo conceptual de la fase 1, el usuario sistema administrativo se mantiene aislado del dominio académico (sin relación obligatoria directa con estudiante, docente, sección, asignatura, clase o calificación).

---

### 2.7 Reglas operativas de estados (aplicación transversal)

#### RN-39. Restricción general por estado inactivo

En la fase 1, no se deben permitir nuevas asignaciones u operaciones de registro cuando la entidad principal involucrada esté en estado **INACTIVO**, salvo consultas administrativas permitidas.

#### RN-40. Consulta administrativa de registros inactivos

Los registros inactivos pueden mantenerse visibles para consulta administrativa, filtrado y trazabilidad básica, según el módulo.

---

## 3) Supuestos de análisis y alcance (SA)

### SA-01. Actor principal de operación

El actor principal que opera el sistema en la fase 1 es el **personal administrativo / secretaría**.

### SA-02. Docente como actor del negocio sin acceso al sistema administrativo

El docente se modela como entidad del dominio y actor del negocio, pero no como usuario del sistema administrativo en la fase 1.

### SA-03. Asignación vigente única estudiante–sección

Para simplificar la fase 1, la relación estudiante–sección se maneja como una única asignación vigente, sin historial formal de cambios.

### SA-04. Cardinalidad operativa docente–sección

La relación docente–sección se mantiene como **muchos a muchos** en la fase 1, sin clasificar roles docentes especializados (titular, auxiliar, etc.).

### SA-05. Cardinalidad operativa docente–clase (fase 1)

Para simplificar la planificación inicial, una clase puede existir sin docente asignado y posteriormente asociarse a un docente (equivalente conceptual a `0..1` docente por clase en fase 1).

### SA-06. Gestión de calificaciones simplificada para práctica

La gestión de calificaciones se mantiene administrativa y básica (registro / actualización / consulta), usando como referencia principal la **Clase** para hacer el modelo más simple y autoexplicativo.

### SA-07. Clase como representación de la oferta académica operativa

La planificación académica por sección se representa mediante **Clases**; cada clase vincula una sección con una asignatura y almacena la franja horaria correspondiente.

### SA-08. Escala de notas configurable o definida en implementación

La escala exacta de calificaciones puede definirse en implementación (según requerimientos técnicos), manteniendo en este documento solo la obligación de validar contra una escala institucional.

### SA-09. Control de acceso básico

La fase 1 solo contempla autenticación básica y validación de estado/rol del usuario sistema administrativo, sin autorización granular por permisos.

### SA-10. Persistencia de reglas detalladas en implementación

Las reglas aquí descritas deberán reflejarse de manera consistente en requerimientos, modelo lógico y validaciones de aplicación, aunque el mecanismo técnico específico se defina en la etapa de diseño/implementación.

---

## 4) Criterios de coherencia transversal (checklist de análisis)

Para mantener consistencia entre documentos, se deben respetar estos criterios:

1. **Edad de estudiante**: rango 6–13, calculado desde fecha de nacimiento y validado respecto a la **fecha de registro/matrícula**.
2. **Cupo de sección**: límite institucional máximo de **35** estudiantes.
3. **Representante legal**: cada estudiante debe quedar con **1 representante legal principal** en el registro operativo de fase 1.
4. **Estudiante–Sección**: una sola asignación vigente en fase 1.
5. **Asignatura**: no almacena franja horaria.
6. **Clase**: representa la oferta académica operativa (sección + asignatura + franja horaria + estado).
7. **Calificación**: se registra por estudiante + clase + parcial (no por asignatura directa).
8. **Consulta por asignatura**: se obtiene de forma derivada desde la clase.
9. **Docente–Sección** y **Docente–Clase** pueden coexistir si se interpretan como relación general vs relación puntual.
10. **Usuario sistema administrativo**: entidad de acceso básico, aislada conceptualmente del dominio académico.
11. **Docente**: no tiene acceso operativo al sistema administrativo en fase 1.
12. **Parciales**: solo Parcial 1 y Parcial 2.

---

## 5) Dependencias con otros documentos

Este documento debe mantenerse coherente con:

* `01-levantamiento-informacion-negocio.md` (contexto, procesos y restricciones preliminares)
* `02-levantamiento-requerimientos.md` (RF, VR, RNF)
* `03-modelo-conceptual-dominio.md` (entidades, relaciones y cardinalidades)
* `05-glosario-alcance-y-limites.md` (términos y límites semánticos)

Cuando se modifique la política de registro de estudiantes, la definición de **Clase**, la forma de registrar calificaciones, los estados operativos o la política de acceso de usuarios sistema administrativo, se deben actualizar todos los documentos relacionados.

---

## 6) Resultado esperado de este documento

Al finalizar este documento, se debe contar con reglas de negocio y supuestos claros para:

1. reducir ambigüedades del análisis,
2. mantener coherencia entre requerimientos y modelo conceptual,
3. preparar el modelo lógico de base de datos,
4. facilitar la implementación de validaciones en código durante la fase 1.
