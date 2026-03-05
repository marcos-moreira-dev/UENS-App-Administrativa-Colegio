# 29_backend_v_1_modelo_de_datos_sql_migraciones_indices_y_consistencia

- Versión: 1.0
- Estado: Vigente
- Ámbito: disciplina de datos y evolucion de esquema
- Relacionado con:
 - `V2_3FN.sql`
 - `18_backend_v_1_acid_transacciones_consistencia_backend.md`
 - `26_backend_v_1_performance_escalabilidad_y_cuello_de_botella.md`

---

## 1. Propósito

Un backend fuerte no vive solo de endpoints.
Vive de una base de datos gobernada con criterio.

---

## 2. Lo que debes saber si quieres trabajar serio con SQL

1. modelado relacional
2. PK y FK
3. índices
4. restricciones
5. nullabilidad
6. transacciones
7. migraciones
8. compatibilidad hacia atras

---

## 3. Reglas prácticas

### No cambies esquema a ciegas

Siempre piensa:

1. que código usa esa tabla
2. que consulta se rompe
3. que datos antiguos existen
4. si el frontend depende del contrato actual

### La BD tambien valida

No dejes toda la proteccion al backend.
Usa:

- `NOT NULL`
- `UNIQUE`
- `CHECK`
- FK

---

## 4. Índices

Debes aprender a decidir:

- que columnas se filtran mucho
- que columnas se ordenan mucho
- que joins se hacen seguido

Índice malo:
- agrega costo de escritura sin mejorar lectura real

Índice bueno:
- responde a una consulta frecuente y medida

---

## 5. Migraciones

Herramientas comunes:

- Flyway
- Liquibase

Lo importante no es la herramienta.
Es la disciplina:

1. cambios versionados
2. repetibles
3. auditables
4. aplicables por ambiente

---

## 6. Compatibilidad hacia atras

Si cambias BD y API al mismo tiempo sin pensar, rompes clientes.

Regla sana:

1. agregar primero
2. migrar uso
3. quitar después

Esto vale para columnas, enums, constraints y contratos.

---

## 7. Aplicado a UENS

Tu proyecto depende mucho del SQL y del frontend.
Por eso debes cuidar especialmente:

1. ids y relaciones
2. enums persistidos
3. filtros usados por listados
4. datos historicos de auditoría y reportes

---

## 8. Siguiente lectura

- `30_backend_v_1_testing_estrategia_profesional.md`


