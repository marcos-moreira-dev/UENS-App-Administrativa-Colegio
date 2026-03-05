# 30_backend_v_1_testing_estrategia_profesional

- Version: 1.0
- Estado: Vigente
- Ámbito: estrategia de pruebas para backend realista
- Relacionado con:
  - `23_backend_v_1_mantenimiento_operacion_incidentes.md`
  - `24_backend_v_1_observabilidad_logs_metricas_alertas.md`
  - `29_backend_v_1_modelo_de_datos_sql_migraciones_indices_y_consistencia.md`

---

## 1. Propósito

Testing serio no es llenar porcentaje.
Es reducir miedo al cambio y detectar regresiones donde más duelen.

---

## 2. Tipos de prueba que importan

### Unit

- reglas
- mappers
- servicios puros
- validaciones

### Integración

- repositorios
- security
- storage
- workers

### API

- contratos HTTP
- codigos de error
- headers
- permisos

### Regresión

- flujos que ya se rompieron antes
- bugs corregidos

---

## 3. Que deberias probar primero

1. auth
2. permisos
3. ownership
4. reportes
5. cambios de estado
6. consultas paginadas importantes

---

## 4. Errores comunes

- probar solo caminos felices
- no probar errores de negocio
- no probar 401 y 403
- no probar storage y archivos
- no agregar prueba después de un bug real

---

## 5. Piramide pragmatica

Para este proyecto sirve:

1. muchas unit
2. suficientes integración
3. algunas API/HTTP de alto valor

No necesitas obsesionarte con E2E de todo.

---

## 6. Aplicado a UENS

Tu backend ya tiene una base muy buena.
Lo siguiente de mayor retorno seria:

1. pruebas HTTP de auth refresh
2. pruebas HTTP de ownership de reportes
3. pruebas de seguridad por rol
4. pruebas de almacenamiento documental por provider

---

## 7. Siguiente lectura

- `31_backend_v_1_patrones_diseno_usados_con_criterio.md`


