# Plantilla — Descripción de empresa y contexto inicial (Documento 00)

## Propósito del documento
Este documento sirve como **punto de partida** para describir una empresa o institución antes de elaborar el levantamiento de información, requerimientos, modelo conceptual y reglas de negocio.

Su objetivo es capturar una visión inicial del negocio de forma clara, práctica y ordenada, para reducir ambigüedad en los siguientes documentos.

## Alcance del documento
Este archivo describe a la empresa desde una perspectiva **de negocio**: quién es, cómo opera, qué problema quiere resolver, qué área será atendida por el sistema y cuáles son los límites iniciales del proyecto.

## Límite de esta etapa
Este documento **no** define todavía:
- modelo lógico de base de datos
- normalización
- implementación técnica
- arquitectura de software detallada
- APIs o pantallas definitivas

---

## 1) Identificación general de la empresa

### 1.1 Datos básicos
- **Nombre de la empresa/institución:**
- **Tipo de organización:** (empresa, institución educativa, taller, comercio, consultora, etc.)
- **Sector / giro del negocio:**
- **Ubicación (referencial):**
- **Tamaño aproximado:** (micro, pequeña, mediana; o número aproximado de personas)

### 1.2 Descripción breve (1 párrafo)
Describe qué hace la empresa, a quién atiende y cuál es su operación principal.

**Plantilla sugerida:**
> La organización **[nombre]** es una **[tipo]** dedicada a **[actividad principal]**, ubicada en **[lugar]**, que atiende principalmente a **[tipo de clientes/usuarios]**. Su operación se centra en **[proceso/servicio principal]**.

---

## 2) Contexto del negocio (situación actual)

### 2.1 ¿Cómo opera hoy?
Describe cómo gestionan actualmente la información o procesos del área que quieres sistematizar.

**Ejemplos de medios actuales (marca/aplica):**
- cuadernos
- hojas sueltas
- Excel / Google Sheets
- WhatsApp
- llamadas
- sistema antiguo
- documentos físicos
- otro: __________

### 2.2 Área o proceso que se quiere mejorar
Indica exactamente qué parte del negocio quieres atender primero.

**Ejemplos:**
- ventas
- inventario
- órdenes de servicio
- clientes
- matrículas / estudiantes
- atención técnica
- pagos
- reportes básicos

### 2.3 Motivo de la necesidad (dolor de negocio)
¿Por qué quieren un sistema o mejora?

**Ejemplos de problemas comunes:**
- duplicados
- pérdida de información
- demora al buscar datos
- errores de registro
- falta de trazabilidad
- dificultad para controlar estados
- reportes manuales lentos

---

## 3) Objetivo inicial del proyecto (visión de negocio)

### 3.1 Objetivo general (negocio, no técnico)
Define en una frase qué se espera lograr.

**Plantilla sugerida:**
> Implementar una solución que permita **[registrar/consultar/controlar]** la información de **[área/proceso]** de forma **[rápida/confiable/ordenada]**, reduciendo **[problemas principales]**.

### 3.2 Resultado esperado en la fase inicial
Describe qué mejora concreta se espera ver primero.

**Ejemplos:**
- centralizar información básica
- tener control de estados
- consultar registros en segundos
- evitar duplicados evidentes
- controlar cupos / stock / órdenes activas

---

## 4) Actores del negocio (quiénes participan)

Lista las personas/roles involucrados en el proceso, aunque todavía no sean usuarios del sistema.

### 4.1 Actor principal operativo
¿Quién usará principalmente el sistema en la primera fase?

- **Rol:**
- **Qué hace hoy:**
- **Qué necesita resolver:**

### 4.2 Otros actores relevantes
Para cada actor, completa:
- **Rol / nombre del actor:**
- **Relación con el proceso:**
- **Información que aporta o consulta:**

**Ejemplos de actores:**
- administración / secretaría
- técnico
- vendedor
- supervisor
- cliente
- proveedor
- docente
- representante legal

---

## 5) Información del negocio que se necesita administrar

> Aquí no pienses aún en tablas físicas. Piensa en “tipos de información” o “conceptos del negocio”.

### 5.1 Lista de información clave (conceptos iniciales)
Completa con nombres simples (sustantivos del negocio).

- **[Concepto 1]** (ej.: Cliente / Estudiante / Producto / Equipo)
- **[Concepto 2]**
- **[Concepto 3]**
- **[Concepto 4]**
- **[Concepto 5]**

### 5.2 Qué datos mínimos se guardan de cada concepto (borrador)
No necesitas detalles técnicos; solo campos de negocio importantes.

**Formato sugerido:**
- **Concepto:**
  - datos mínimos:
  - estado (si aplica):
  - relación con otros conceptos (si ya se conoce):

---

## 6) Procesos generales observados (nivel negocio)

Describe los procesos de forma simple: verbo + objeto.

### 6.1 Procesos principales actuales
- Registrar ________
- Actualizar ________
- Consultar ________
- Asignar / asociar ________
- Controlar ________
- Reportar (básico) ________

### 6.2 Flujo general (resumen en texto)
Describe en 5–10 líneas cómo ocurre el proceso principal de inicio a fin.

---

## 7) Problemas detectados (lista priorizada)

Prioriza de mayor a menor impacto.

### 7.1 Problemas críticos (alta prioridad)
- [ ] Problema 1
- [ ] Problema 2
- [ ] Problema 3

### 7.2 Problemas importantes (media prioridad)
- [ ] Problema 4
- [ ] Problema 5

### 7.3 Problemas menores / mejoras deseables
- [ ] Problema 6
- [ ] Problema 7

---

## 8) Alcance inicial del sistema (fase 1)

### 8.1 Qué sí incluirá la primera fase
Escribe en términos funcionales simples.

- Gestión de ________
- Registro de ________
- Consulta de ________
- Control de ________
- Listados básicos de ________

### 8.2 Qué NO incluirá la primera fase (límites explícitos)
Esto evita sobrecargar el análisis.

- No incluye: ________
- No incluye: ________
- No incluye: ________

### 8.3 Criterio de éxito de la fase 1 (sin KPI formal)
¿Cómo sabrás que la fase 1 ya ayuda?

**Ejemplos:**
- El personal puede registrar y consultar sin usar cuadernos.
- Se reduce el error de duplicados.
- Se controla el estado/cupo/stock de forma consistente.

---

## 9) Reglas de negocio preliminares (borrador)

> Aquí solo anota reglas de negocio evidentes. Luego se formalizan en `04-reglas-negocio-y-supuestos.md`.

**Plantilla de regla:**
- **Regla:**
- **Aplica a:**
- **Motivo de negocio:**
- **Observación (si aplica):**

### 9.1 Ejemplos de reglas típicas (para guiarte)
- Un registro inactivo no participa en nuevas asignaciones.
- Existe un límite máximo (cupo/stock/capacidad) por entidad.
- Ciertos datos son obligatorios para registrar un elemento.
- Se debe advertir posible duplicado ante coincidencias clave.
- Solo ciertos estados/periodos están habilitados para operar.

---

## 10) Supuestos iniciales (para poder avanzar)

Anota simplificaciones temporales del análisis. Esto es buena práctica.

### 10.1 Supuestos de alcance
- Se trabajará primero con el proceso de ________.
- El actor principal del sistema será ________.
- Se manejará una versión simplificada de ________ en fase 1.

### 10.2 Supuestos de datos
- Se almacenarán solo datos mínimos de ________.
- Se usarán estados básicos (ej.: ACTIVO/INACTIVO) para ________.

### 10.3 Supuestos operativos
- La empresa continuará usando temporalmente ________ en paralelo (si aplica).
- Ciertas validaciones avanzadas quedarán para una fase posterior.

---

## 11) Riesgos y puntos a aclarar (preguntas abiertas)

> Este bloque es clave para practicar análisis real.

### 11.1 Preguntas de negocio pendientes
- [ ] ¿Cuál es el proceso exacto de ________?
- [ ] ¿Quién autoriza cambios en ________?
- [ ] ¿Qué campos son realmente obligatorios en ________?
- [ ] ¿Qué excepciones ocurren con frecuencia?

### 11.2 Riesgos de análisis temprano
- [ ] Alcance demasiado amplio
- [ ] Falta de reglas claras
- [ ] Datos inconsistentes en la operación actual
- [ ] Dependencia de una sola persona para responder todo

---

## 12) Buenas prácticas para comenzar (guía rápida)

### 12.1 Por dónde empezar (orden recomendado)
1. **Entender el negocio** (qué hacen y dónde duele).
2. **Delimitar una fase 1 pequeña** (no querer resolver todo).
3. **Identificar actores y conceptos clave** (sin pensar aún en tablas).
4. **Listar procesos principales** (verbo + objeto).
5. **Anotar reglas de negocio evidentes**.
6. Recién después pasar a:
   - `01-levantamiento-informacion-negocio.md`
   - `02-levantamiento-requerimientos.md`
   - `03-modelo-conceptual-dominio.md`
   - `04-reglas-negocio-y-supuestos.md`
   - `05-glosario-alcance-y-limites.md`

### 12.2 Buenas prácticas (muy útiles)
- **Separar negocio de técnica** en esta etapa.
- **Escribir con ejemplos reales** de la empresa.
- **Documentar supuestos** cuando algo no esté claro.
- **Definir límites explícitos** de la fase 1.
- **Mantener consistencia de términos** (usar siempre los mismos nombres).
- **No adelantarse al SQL** demasiado pronto.
- **Priorizar operatividad** sobre perfección teórica inicial.

### 12.3 Errores comunes a evitar
- Mezclar narrativa del negocio con diseño de tablas desde el inicio.
- Querer modelar todos los casos excepcionales en la fase 1.
- No dejar por escrito qué está fuera de alcance.
- Cambiar nombres de conceptos entre documentos (ej.: “cliente” vs “usuario” vs “contacto” sin definir).

---

## 13) Cómo usar este documento con el resto (flujo de trabajo)

Este archivo funciona como **Documento 00** y debe hacerse **antes** de los documentos de análisis formal.

### Flujo recomendado
1. `00-plantilla-descripcion-empresa-y-contexto-inicial.md` *(este documento, ya adaptado al caso real)*
2. `01-levantamiento-informacion-negocio.md`
3. `02-levantamiento-requerimientos.md`
4. `03-modelo-conceptual-dominio.md`
5. `04-reglas-negocio-y-supuestos.md`
6. `05-glosario-alcance-y-limites.md`
7. Modelo lógico relacional
8. Normalización (hasta 3FN)

---

## 14) Plantilla de cierre (resumen ejecutivo corto)

Al finalizar este documento, redacta un resumen de 5–8 líneas que responda:

- ¿Quién es la empresa?
- ¿Qué proceso se quiere mejorar?
- ¿Qué problema principal existe hoy?
- ¿Qué incluirá la fase 1?
- ¿Qué se deja fuera por ahora?

**Formato sugerido:**
> La empresa **[nombre]** requiere mejorar el proceso de **[proceso]** debido a **[problema principal]**. En la fase 1 se trabajará en **[alcance]**, con foco en **[actor principal]**, dejando fuera **[exclusiones]** para mantener un inicio controlado y viable.

