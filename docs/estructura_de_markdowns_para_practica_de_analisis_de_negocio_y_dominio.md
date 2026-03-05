# Estructura sugerida de archivos Markdown para tu práctica (sin sobrecomplicar)

Este lienzo propone una estructura simple y profesional para practicar el análisis inicial de un sistema antes del modelo lógico y la normalización.

La idea es separar **qué se sabe del negocio**, **qué debe hacer el sistema**, **cómo se entiende el dominio** y **qué reglas/restricciones existen**, sin mezclar todo en un solo documento.

---

## Convención de nombres (recomendada)

Usa prefijos numéricos para mantener orden en Visual Studio Code:

- `01-levantamiento-informacion-negocio.md`
- `02-levantamiento-requerimientos.md`
- `03-modelo-conceptual-dominio.md`
- `04-reglas-negocio-y-supuestos.md`
- `05-glosario-alcance-y-limites.md` *(opcional, pero útil)*

> Si quieres algo más corto, puedes dejar solo 4 archivos (1 al 4) y omitir el 5.

---

## 1) Archivo: `01-levantamiento-informacion-negocio.md`

### Para qué sirve
Este archivo documenta el **contexto real del negocio** tal como se entiende al inicio: cómo trabaja la institución, qué problemas tiene actualmente, quiénes participan y cuál es la necesidad general del sistema.

### Qué debe incluir
- Contexto de la institución (tipo, tamaño, ubicación, operación general)
- Problema actual (cómo registran información hoy)
- Actores del negocio (administración, docentes, representantes, estudiantes)
- Procesos generales observados (matrícula, asignación de secciones, materias, notas)
- Dolencias o riesgos (duplicados, errores, falta de trazabilidad, cupos)
- Objetivo general del sistema (desde negocio, no técnico)

### Qué NO debe incluir (límite)
- Diseño de base de datos
- Tablas físicas
- SQL
- Normalización
- Decisiones técnicas detalladas de implementación

### Introducción sugerida (lista para copiar)
```md
# Levantamiento de información del negocio

## Propósito del documento
Este documento reúne el levantamiento inicial de información del negocio para comprender el contexto operativo de la institución, sus actores, sus procesos principales y los problemas actuales que motivan el desarrollo del sistema.

## Alcance del documento
Este archivo describe el negocio desde una perspectiva funcional y organizativa (qué hace la institución y qué necesita resolver), sin entrar todavía en diseño técnico, modelo lógico de base de datos ni normalización.

## Límite de esta etapa
La información aquí presentada es base para análisis posterior. Puede refinarse conforme se aclaren reglas, procesos y requerimientos.
```

---

## 2) Archivo: `02-levantamiento-requerimientos.md`

### Para qué sirve
Este archivo traduce el entendimiento del negocio en **requerimientos del sistema**: qué debe permitir hacer, qué restricciones debe cumplir y qué consultas mínimas debe soportar.

### Qué debe incluir
- Requerimientos funcionales (registrar, editar, consultar, asignar, validar)
- Requerimientos no funcionales básicos (seguridad mínima, tiempos razonables, trazabilidad simple)
- Restricciones de alcance de fase 1
- Casos de uso resumidos (si quieres)
- Validaciones relevantes (cupos, estados, duplicados, parciales)

### Qué NO debe incluir (límite)
- Estructura final de tablas
- Tipos de datos SQL definitivos
- Índices, constraints SQL exactos
- Arquitectura técnica profunda

### Introducción sugerida (lista para copiar)
```md
# Levantamiento de requerimientos

## Propósito del documento
Este documento especifica los requerimientos funcionales y no funcionales de la fase inicial del sistema, a partir del levantamiento de información del negocio.

## Alcance del documento
Se detallan las capacidades que el sistema debe ofrecer en la fase 1, las restricciones operativas relevantes y las validaciones mínimas necesarias para una operación confiable.

## Límite de esta etapa
Este documento no define todavía el modelo lógico de base de datos ni la implementación técnica final; su objetivo es establecer qué debe hacer el sistema.
```

---

## 3) Archivo: `03-modelo-conceptual-dominio.md`

### Para qué sirve
Este archivo define el **modelo conceptual del dominio**: entidades del negocio, atributos conceptuales, relaciones, cardinalidades y reglas principales.

### Qué debe incluir
- Entidades del dominio (ej. Estudiante, Representante, Docente, Sección, Asignatura, Calificación)
- Atributos conceptuales (mínimos)
- Relaciones entre entidades
- Cardinalidades
- Definiciones cortas del significado de cada entidad/relación

### Qué NO debe incluir (límite)
- Tablas intermedias físicas obligatorias (eso va luego)
- Claves foráneas SQL
- DDL (CREATE TABLE)
- Normalización 1FN/2FN/3FN

### Introducción sugerida (lista para copiar)
```md
# Modelo conceptual del dominio

## Propósito del documento
Este documento describe el dominio del problema mediante un modelo conceptual, identificando entidades, atributos, relaciones y cardinalidades relevantes para el sistema.

## Alcance del documento
El enfoque es conceptual (de negocio/análisis), por lo que se modelan significados y relaciones del dominio sin definir aún estructuras físicas de base de datos.

## Límite de esta etapa
La transformación a modelo lógico relacional y su normalización se realizará en una etapa posterior.
```

---

## 4) Archivo: `04-reglas-negocio-y-supuestos.md`

### Para qué sirve
Este archivo separa y ordena las **reglas del negocio**, restricciones, supuestos y decisiones temporales de alcance para que no queden dispersos en la narrativa.

### Qué debe incluir
- Reglas de edad
- Cupos por sección
- Estados (activo/inactivo)
- Parciales (2 parciales en fase 1)
- Validaciones de notas
- Advertencias de duplicados
- Supuestos de simplificación (ej. un representante principal por estudiante, si aplica)
- Casos excepcionales (virtualidad por contingencia)

### Qué NO debe incluir (límite)
- Procedimientos SQL
- Triggers
- Implementación de lógica en código

### Introducción sugerida (lista para copiar)
```md
# Reglas de negocio y supuestos

## Propósito del documento
Este documento consolida las reglas operativas del negocio, restricciones de validación y supuestos adoptados para la fase inicial del sistema.

## Alcance del documento
Se registran reglas del dominio que afectan el análisis, el diseño posterior y las validaciones del sistema, con el fin de mantener consistencia entre los documentos.

## Límite de esta etapa
Las reglas aquí descritas no representan aún implementación técnica; su formalización en base de datos o código se definirá después.
```

---

## 5) Archivo opcional: `05-glosario-alcance-y-limites.md`

### Para qué sirve
Muy útil para practicar de forma ordenada. Aquí aclaras términos, alcance y exclusiones para evitar ambigüedad.

### Qué puede incluir
- Glosario de términos (sección, parcial, cupo, representante legal, asignatura virtual)
- Alcance de fase 1
- Fuera de alcance (asistencia, pagos, reportes avanzados, autenticación)
- Preguntas abiertas pendientes por validar

### Introducción sugerida (lista para copiar)
```md
# Glosario, alcance y límites

## Propósito del documento
Este documento define términos clave del dominio, alcance funcional de la fase 1 y límites explícitos del proyecto para mantener claridad durante el análisis y diseño.

## Alcance del documento
Incluye definiciones, exclusiones y aclaraciones que sirven como referencia transversal para los demás archivos de análisis.

## Límite de esta etapa
Este archivo no reemplaza el levantamiento de información, requerimientos ni modelo conceptual; actúa como documento de apoyo y control semántico.
```

---

## Ruta práctica recomendada (sin sobrepensar)

1. Escribe primero `01-levantamiento-informacion-negocio.md`
2. Luego `02-levantamiento-requerimientos.md`
3. Después `03-modelo-conceptual-dominio.md`
4. Luego `04-reglas-negocio-y-supuestos.md`
5. (Opcional) `05-glosario-alcance-y-limites.md`
6. Recién después: **modelo lógico + normalización (3FN)**

---

## Nota final para tu práctica

Esta estructura está pensada para practicar análisis con orden, sin forzarte a hacer diseño técnico demasiado temprano. Puedes mantener documentos cortos pero claros, y luego refinarlos cuando pases al modelo lógico relacional.

