# 31_backend_v_1_patrones_diseno_usados_con_criterio

- Versión: 1.0
- Estado: Vigente
- Ámbito: patrones útiles para backend sin caer en sobreingenieria
- Relacionado con:
 - `04_backend_v_1_modelado_aplicacion_y_modulos.md`
 - `21_backend_v_1_sesion_renovable_y_repositorio_documental_local.md`
 - `27_backend_v_1_integraciones_externas_y_storage_providers.md`

---

## 1. Propósito

Los patrones no son decoracion.
Sirven cuando reducen acoplamiento, complejidad o repeticion.

---

## 2. Patrones que si debes dominar

### Dependency Injection

Base de Spring.

### Ports and Adapters

Muy útil para storage, auth stores, integraciones.

### Adapter

Implementacion concreta de un puerto o contrato.

### Facade

Oculta complejidad técnica y expone algo simple.

### Strategy

Muy bueno para exportadores, validadores o seleccion por tipo.

### Specification

Útil para queries flexibles y filtradas.

### Factory

Útil cuando crear objetos ya no es trivial.

---

## 3. Patrones que debes usar con cuidado

### Builder

Solo cuando el objeto realmente tiene armado complejo.

### CQRS

Bueno en ciertos contextos.
Malo si se mete por moda en un proyecto pequeno.

### Saga

Solo cuando de verdad tienes flujos distribuidos largos.

### Event Sourcing

Muy potente, muy caro de operar.
No es para ponerlo porque se ve avanzado.

---

## 4. Regla de oro

Antes de meter un patron, preguntate:

1. que dolor resuelve
2. que pasaria si no lo uso
3. si el equipo lo va a entender y mantener

---

## 5. Aplicado a UENS

Los patrones bien elegidos hasta ahora son:

1. DI con Spring
2. Ports and Adapters en refresh token y storage
3. Strategy en exportadores y ensambladores de reportes
4. Facade en contexto de usuario autenticado

Eso ya es una base muy buena.

No necesitas meter CQRS completo, Event Sourcing ni microservicios para que el backend sea serio.

---

## 6. Siguiente lectura

- `32_backend_v_1_freelance_backend_y_trabajo_autonomo.md`


