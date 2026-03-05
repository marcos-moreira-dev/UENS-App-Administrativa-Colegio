# README de Arquitectura (Levantamiento -> Implementación)

## 1) Qué es este documento y para quién
Este README no es solo un índice. Es un mapa de ejecución para construir proyectos completos sin improvisar.

Está pensado para cuando te enfrentas a un sistema con:

- negocio real y reglas que cambian;
- base de datos relacional;
- backend con seguridad y trazabilidad;
- frontend con módulos operativos;
- y necesidad de mantener el sistema en el tiempo.

La referencia usada aquí es UENS, cuyo stack y arquitectura ya están cerrados.

---

## 2) Idea central: primero arquitectura, luego velocidad
El error más caro en proyectos grandes no es “programar lento”. Es programar rápido en el orden incorrecto.

Orden correcto significa:

1. cerrar lenguaje de negocio;
2. cerrar reglas de datos;
3. cerrar contrato backend;
4. recién después escalar UI y features.

Si inviertes eso, terminas reescribiendo capas enteras.

---

## 3) Arquitectura cerrada de referencia (UENS)

### 3.1 Base de datos (PostgreSQL, V2 3FN)
Estructura actual de tablas:

1. `usuario_sistema_administrativo`
2. `representante_legal`
3. `seccion`
4. `docente`
5. `asignatura`
6. `estudiante`
7. `clase`
8. `calificacion`
9. `reporte_solicitud_queue`
10. `auditoria_evento`

Referencias:
- `db/V2/docs/Diagramas y query de creación/V2_3FN.sql`
- `db/V2/docs/diccionario_de_datos_uens_v_2_3_fn.md`

### 3.2 Backend (Spring Boot, monolito modular)
Módulos actuales:

- `auth`, `usuario`, `system`
- `dashboard`, `consultaacademica`
- `representante`, `seccion`, `docente`, `asignatura`
- `estudiante`, `clase`, `calificacion`
- `reporte`, `auditoria`

Referencias:
- `backend/uens-backend/README.md`
- `backend/docs/backend_v_1/00_backend_v_1_indice_y_mapa_documental.md`
- `backend/docs/api/API_ENDPOINTS.md`

### 3.3 Frontend desktop (JavaFX, MVVM)
Módulos actuales:

- `auth`, `shell`, `dashboard`
- `estudiantes`, `representantes`, `docentes`, `secciones`, `asignaturas`, `clases`, `calificaciones`
- `reportes`, `auditoria`

Referencias:
- `desktop/uens-desktop/README.md`
- `desktop/docs/00_desktop_indice_y_mapa_documental.md`
- `desktop/docs/18_desktop_arquitectura_paquetes_y_capas_mvvm.md`
- `desktop/docs/21_desktop_roadmap_impl.md`

---

## 4) Por qué este orden de implementación sí funciona

### 4.1 Del negocio a la técnica
La arquitectura es una traducción de reglas de negocio. Si no tienes reglas claras, cualquier diseño técnico “bonito” se rompe rápido.

### 4.2 De BD a backend
La base de datos fija límites de integridad (`FK`, `UNIQUE`, `CHECK`). El backend orquesta, pero no debería inventar consistencia que la BD ignora.

### 4.3 De backend a frontend
El frontend debe consumir contratos estables. Si el backend no está cerrado, la UI queda acoplada a cambios constantes.

### 4.4 De operación al final, pero no como parche
Reportes asíncronos, auditoría, observabilidad y hardening no son extras. Son requisitos de mantenibilidad.

---

## 5) Orden recomendado para proyectos similares

## Fase A: Levantamiento y cierre funcional
1. Contexto y dolores reales (`01`).
2. Requerimientos funcionales y no funcionales (`02`).
3. Modelo conceptual (`03`).
4. Reglas y supuestos (`04`).
5. Glosario, límites y exclusiones (`05`).

Salida esperada:
- alcance cerrado;
- entidades principales;
- reglas obligatorias;
- restricciones explícitas.

## Fase B: Base de datos primero (alineada al negocio)
1. Entidades maestras.
2. Entidades transaccionales.
3. Entidades operativas (`queue`, auditoría, bitácoras).
4. Constraints reales y consistentes.
5. Seeds y scripts de reset.

Regla:
- si la BD no protege reglas mínimas, el backend acumula deuda estructural.

## Fase C: Backend por capas y por riesgo
1. Base transversal:
 - contrato API;
 - errores;
 - paginación/filtros;
 - seguridad.
2. Módulos de acceso y arranque:
 - `auth`, `usuario`, `system`.
3. Módulos maestros:
 - `representante`, `seccion`, `docente`, `asignatura`.
4. Módulo estrella:
 - `estudiante`.
5. Módulos académicos/transaccionales:
 - `clase`, `calificacion`.
6. Consultas agregadas:
 - `dashboard`, `consultaacademica`.
7. Operación:
 - `reporte` asíncrono -> `auditoria`.
8. Hardening:
 - seguridad operativa, ownership, observabilidad, pruebas y despliegue.

## Fase D: Frontend después del contrato backend
1. Base app: bootstrap, sesión, navegación, cliente API, manejo de errores.
2. Login y guardas por rol.
3. Dashboard.
4. Patrones UI reutilizables.
5. Módulo estrella.
6. CRUDs de soporte.
7. Flujos transaccionales.
8. Reportes asíncronos.
9. Auditoría.
10. Cierre de calidad (i18n, logging, pruebas críticas).

---

## 6) Matriz de prioridad para decidir “qué va primero”
Cuando tengas dudas entre dos módulos, prioriza así:

1. impacto en reglas críticas del negocio;
2. nivel de acoplamiento con otras entidades;
3. riesgo de inconsistencias de datos;
4. dependencias de seguridad/autorización;
5. dependencias de UX (cuántas pantallas lo requieren).

Ejemplo típico:
- `estudiante` suele ir antes que `calificacion`, porque calificación depende de estudiante, clase y reglas de parcial.

---

## 7) Definición de “arquitectura bien cerrada”
Antes de escalar nuevas features, valida:

- [ ] reglas de negocio mapeadas en backend;
- [ ] constraints críticos también en BD;
- [ ] endpoints documentados y coherentes con DTOs reales;
- [ ] frontend sin contratos paralelos;
- [ ] tareas pesadas en flujo asíncrono;
- [ ] auditoría para acciones sensibles;
- [ ] permisos y roles alineados entre capas;
- [ ] entorno reproducible con scripts y seeds.

Si una casilla falla, no escales módulos nuevos todavía.

---

## 8) Errores de secuencia que debes evitar
- Empezar por UI sin cerrar reglas.
- Tratar todo como CRUD plano.
- Resolver consistencia solo con validaciones de frontend.
- Dejar seguridad y trazabilidad para “después”.
- Crear reportes pesados síncronos.
- Mezclar responsabilidades de capas.

---

## 9) Ruta de lectura recomendada en este repo
1. `docs/01_levantamiento_informacion_negocio.md`
2. `docs/02_levantamiento_requerimientos.md`
3. `docs/03_modelo_conceptual_dominio.md`
4. `docs/04_reglas_negocio_y_supuestos.md`
5. `db/V2/docs/diccionario_de_datos_uens_v_2_3_fn.md`
6. `backend/docs/backend_v_1/00_backend_v_1_indice_y_mapa_documental.md`
7. `backend/docs/api/API_ENDPOINTS.md`
8. `desktop/docs/00_desktop_indice_y_mapa_documental.md`
9. `desktop/docs/18_desktop_arquitectura_paquetes_y_capas_mvvm.md`
10. `desktop/docs/21_desktop_roadmap_impl.md`

---

## 10) Plantilla reusable
Para iniciar un proyecto similar desde cero:

- `docs/plantilla_arquitectura_proyecto_similar.md`
