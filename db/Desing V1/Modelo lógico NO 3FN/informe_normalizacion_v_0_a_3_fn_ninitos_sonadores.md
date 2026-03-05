# Informe de normalización (diagnóstico inicial y plan) — V0 → 3FN

## Contexto

Partimos de una **V0** en PostgreSQL generada desde el modelo conceptual (a propósito, todavía no normalizada del todo) para observar la evolución hacia un diseño más limpio.

**Objetivo de este informe:**
- Dar un **diagnóstico inicial** del estado actual.
- Definir **qué se va a normalizar** y **en qué orden**.
- Separar el trabajo por etapas: **1FN**, **2FN** y **3FN**.

---

## Alcance de esta iteración

Este informe se enfoca en la estructura actual de las tablas:
- `usuario_sistema_administrativo`
- `representante_legal`
- `seccion`
- `docente`
- `asignatura`
- `estudiante`
- `clase`
- `calificacion`

No se modifica todavía el alcance funcional del sistema (fase 1):
- sin pagos,
- sin asistencia,
- sin historial de cambios de sección,
- sin permisos avanzados,
- sin múltiples representantes por estudiante.

---

## Diagnóstico inicial (estado V0)

## 1) Lo que ya está bien (base sólida)

### a) Separación de entidades principales
El diseño ya separa correctamente los conceptos centrales del dominio:
- estudiante,
- representante legal,
- sección,
- docente,
- asignatura,
- clase,
- calificación,
- usuario administrativo.

Esto es una muy buena base para normalizar sin “romper” el dominio.

### b) Relaciones principales ya están modeladas con FK
Ya existen relaciones claras con claves foráneas, por ejemplo:
- `estudiante -> representante_legal`
- `estudiante -> seccion`
- `clase -> seccion`
- `clase -> asignatura`
- `clase -> docente`
- `calificacion -> estudiante`
- `calificacion -> clase`

### c) Restricciones importantes ya presentes
Hay decisiones de dominio ya representadas en la V0:
- estados (`ACTIVO` / `INACTIVO`),
- cupo máximo,
- parciales 1 y 2,
- unicidad operativa de clase,
- unicidad de calificación por estudiante + clase + parcial.

---

## 2) Qué está “intencionalmente sucio” (y se normalizará luego)

Estas dos columnas fueron incluidas a propósito para visualizar la evolución, pero **deberían salir** al llegar a 3FN:

### a) `estudiante.edad`
- Es **derivable** desde `fecha_nacimiento` (y una fecha de referencia).
- Guardarla genera riesgo de inconsistencia (la edad cambia con el tiempo).

### b) `seccion.cantidad_estudiantes_registrados`
- Es **derivable** contando estudiantes asignados a esa sección.
- Guardarla genera desincronización si no se actualiza siempre.

---

## 3) Riesgos / focos de revisión por normalización (sin afirmar aún que estén “mal”)

### a) `franja_horaria` como texto libre (`clase.franja_horaria`)
Actualmente está como `VARCHAR`, lo cual es práctico para V0, pero puede mezclar varias piezas en un solo campo (por ejemplo: día + hora inicio + hora fin).

**Pregunta de normalización posterior:**
- ¿Se mantiene como texto (por simplicidad de fase 1)?
- ¿Se separa en estructura más formal (ej. día, hora_inicio, hora_fin)?

> Esto afecta más a calidad de datos y validación que a 1FN de forma estricta, pero conviene decidirlo.

### b) `anio_lectivo` como texto (`seccion.anio_lectivo`)
Está bien para V0 (`"2026-2027"`), pero más adelante puede evaluarse si conviene:
- dejarlo como texto,
- o crear entidad/catálogo `anio_lectivo`.

No es obligatorio para llegar a 3FN en esta fase, pero sí es una decisión de diseño importante.

### c) Campos de contacto (`telefono`, `correo_electronico`)
En V0 hay un solo teléfono/correo por persona (representante/docente), lo cual encaja con tu alcance de práctica.

**Nota:** si en el futuro quisieras múltiples teléfonos/correos por persona, eso sí exigiría nuevas tablas (y rompería la simplicidad actual).

### d) Estados repetidos en varias tablas
No es una violación automática de 3FN, pero sí es un patrón repetido de dominio.

Para fase 1 está bien mantener:
- `estado VARCHAR(10)` + `CHECK ('ACTIVO','INACTIVO')`

Más adelante (si crecen estados o lógica), se podría usar catálogo/enum. **No es prioridad ahora.**

---

## Diagnóstico por forma normal

## 1FN (Primera Forma Normal) — diagnóstico preliminar

### Estado general: **casi cumplida / muy cerca de cumplirse formalmente**

Porque:
- Cada tabla tiene PK.
- Los valores son escalares (no hay listas/arrays/columnas repetidas tipo `telefono1`, `telefono2`, etc.).
- Las relaciones están separadas en tablas (no embebidas en una sola tabla gigante).

### Posibles puntos a revisar para cerrar 1FN con criterio fuerte
- Revisar si `franja_horaria` se considera “atómico suficiente” para esta fase.
- Estandarizar formatos de texto (ej. `anio_lectivo`, teléfono).
- Evitar valores ambiguos en campos que deberían seguir formato fijo.

**Conclusión 1FN:** La V0 está muy bien encaminada y probablemente requiera ajustes menores, no una reestructuración grande.

---

## 2FN (Segunda Forma Normal) — diagnóstico preliminar

### Estado general: **prácticamente bien encaminada**

La mayoría de tablas usan PK sustituta simple (`pk_id`), por lo que la dependencia parcial no aparece fácilmente.

### Punto importante a documentar (didácticamente)
Aunque `calificacion` usa PK sustituta (`pk_id`), también tiene una **clave natural/candidata** muy importante:
- `(estudiante_id, clase_id, numero_parcial)`

La lógica de negocio dice que los atributos de la calificación (`nota`, `fecha_registro`, `observacion`) dependen de esa combinación completa.

**Qué se verificará en 2FN:**
- Que no exista ningún atributo en `calificacion` que dependa solo de una parte de esa clave natural.
- Que el mismo criterio se documente también para `clase` respecto a su unicidad operativa (`seccion_id, asignatura_id, franja_horaria`).

**Conclusión 2FN:** Es probable que no requiera cambios grandes en estructura, pero sí una buena **documentación de dependencias funcionales**.

---

## 3FN (Tercera Forma Normal) — diagnóstico preliminar

### Estado general: **todavía no cumple del todo (a propósito, por la V0)**

El principal motivo es la presencia de atributos derivados almacenados:
- `estudiante.edad`
- `seccion.cantidad_estudiantes_registrados`

Estos introducen redundancia y posibles inconsistencias.

### Otros puntos de 3FN a revisar (según nivel de rigor que quieras)
- si `anio_lectivo` permanece texto o pasa a catálogo;
- si `franja_horaria` permanece texto o se formaliza;
- si algunos dominios (`rol`, `area`) conviene catalogarlos o no.

**Importante:**
- Catalogar todo **no es obligatorio** para 3FN en esta fase.
- Tu objetivo de práctica manda: normalizar lo suficiente sin burocracia innecesaria.

---

## Qué se va a normalizar (resumen ejecutivo)

## Cambios casi seguros (para llegar a 3FN en esta fase)
1. **Eliminar `estudiante.edad`** (se calcula por consulta/lógica de aplicación).
2. **Eliminar `seccion.cantidad_estudiantes_registrados`** (se deriva por `COUNT(*)`).
3. Mantener claves y relaciones, fortaleciendo validaciones donde haga falta.
4. Documentar dependencias funcionales por tabla (para justificar 1FN/2FN/3FN).

## Cambios opcionales (según cuánto quieras refinar ahora)
1. Formalizar `franja_horaria`.
2. Separar `anio_lectivo` en tabla catálogo.
3. Catalogar `area`, `rol` o estados.

> Recomendación para tu práctica actual: **hacer primero la normalización mínima y correcta (sin sobre-diseño)**.

---

## Plan de trabajo (pasos que seguiré)

## Paso 0 — Congelar la V0 como línea base
- Mantener el script V0 actual como referencia histórica.
- Tomar captura/diagrama de pgAdmin (ya hecho).
- Registrar decisiones de alcance (fase 1).

**Entregable:** versión V0 documentada y diagrama base.

---

## Paso 1 — Verificación y ajuste a 1FN

### Qué revisaré
- Atomicidad razonable de campos (especialmente `franja_horaria`).
- Ausencia de grupos repetitivos.
- Consistencia de tipos (texto vs fecha vs numérico).
- Estándares de formato básicos (`anio_lectivo`, teléfono, correos opcionales).

### Qué podría cambiar en 1FN (solo si hace falta)
- Ajustes de nombres/formatos.
- Restricciones `CHECK` adicionales simples.
- (Opcional) descomposición de algún campo si realmente está almacenando múltiples datos en uno.

**Entregable:** diagnóstico 1FN + script V1.1 (si se hacen ajustes).

---

## Paso 2 — Verificación y justificación de 2FN

### Qué revisaré
- Dependencias funcionales por tabla.
- Claves candidatas relevantes (además de `pk_id`).
- Riesgos de dependencias parciales en tablas con unicidades compuestas (especialmente `calificacion` y `clase`).

### Resultado esperado
- Confirmar que la estructura está bien respecto a 2FN (probablemente con pocos o ningún cambio físico).
- Dejar justificado por escrito por qué cumple 2FN.

**Entregable:** informe 2FN (dependencias funcionales) + ajustes SQL si aparecen.

---

## Paso 3 — Normalización a 3FN

### Qué cambiaré casi seguro
- Quitar columnas derivadas:
  - `estudiante.edad`
  - `seccion.cantidad_estudiantes_registrados`

### Qué revisaré adicionalmente (sin sobrecomplicar)
- Dependencias transitivas evitables.
- Dominios repetidos que valga la pena formalizar (solo si aporta valor real a la práctica).
- Reglas de coherencia entre `seccion.grado` y `asignatura.grado` al crear `clase` (regla de negocio / validación).

**Entregable:** script SQL V1 (3FN práctica) + comparación V0 vs V1.

---

## Criterio de “listo para backend” después de 3FN

Se considerará listo cuando:
- no haya atributos derivados persistidos innecesariamente,
- las relaciones principales estén claras y consistentes,
- existan PK/FK/UNIQUE/CHECK suficientes para proteger datos básicos,
- el modelo siga siendo simple para fase 1,
- y el esquema sea cómodo para implementar CRUD y consultas en backend.

---

## Recomendación de ejecución (orden real de trabajo)

1. **Diagnóstico inicial (este documento)** ✅
2. 1FN (revisión + ajustes mínimos)
3. 2FN (dependencias funcionales + justificación)
4. 3FN (eliminar derivadas + limpieza)
5. Script PostgreSQL V1 final
6. Re-generar diagrama en pgAdmin
7. Empezar backend

---

## Nota final (enfoque de práctica)

Tu enfoque es correcto: construir una **V0 imperfecta pero funcional**, visualizarla en pgAdmin, y luego **refinar con criterio** hasta 3FN.

Eso te da aprendizaje real porque ves:
- cómo nace el modelo desde el dominio,
- cómo se transforma a tablas,
- y cómo mejora con normalización sin perder el propósito del sistema.

