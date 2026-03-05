# Plantilla de Arquitectura para Proyectos Similares

## 1) Cómo usar esta plantilla
Usa este archivo al iniciar un sistema nuevo (académico, administrativo o de negocio similar) y complétalo en este orden:

1. Contexto y alcance.
2. Modelo de datos (BD).
3. Arquitectura backend.
4. Arquitectura frontend.
5. Plan de implementación por módulos.
6. Plan de reciclaje de código base.

---

## 2) Contexto del proyecto (rellenable)

## 2.1 Problema principal
- [Describe el problema operativo real]

## 2.2 Actores
- [Actor 1]
- [Actor 2]
- [Actor 3]

## 2.3 Objetivo de la versión 1
- [Qué sí incluye]
- [Qué no incluye]

## 2.4 Reglas de negocio críticas
- [Regla 1]
- [Regla 2]
- [Regla 3]

---

## 3) Modelo de datos (BD) - Orden recomendado

## 3.1 Tablas maestras primero
- usuarios
- catálogos base
- entidades núcleo del dominio

## 3.2 Tablas transaccionales después
- relaciones operativas
- eventos/acciones del dominio

## 3.3 Tablas de operación al final
- colas de trabajo (si aplica)
- auditoría/trazabilidad

## 3.4 Criterios obligatorios
- `CHECK` para estados y rangos.
- `UNIQUE` para reglas de identidad.
- `FK` para integridad relacional.
- índices en consultas críticas.
- seeds de demo y script de reset.

---

## 4) Backend - Orden recomendado de construcción

## 4.1 Base técnica primero
- contrato API uniforme
- manejo centralizado de errores
- paginación/filtros/ordenamiento
- seguridad base (auth, roles, sesiones/tokens)

## 4.2 Módulos de arranque
- `auth`
- `usuario`
- `system` (salud/ping/config)

## 4.3 Módulos maestros del dominio
- [Módulo maestro 1]
- [Módulo maestro 2]
- [Módulo maestro 3]

## 4.4 Módulo estrella
- [Entidad más conectada del dominio]

## 4.5 Módulos transaccionales
- [Módulo transaccional 1]
- [Módulo transaccional 2]

## 4.6 Consultas agregadas
- dashboard
- consultas analíticas mínimas

## 4.7 Operación avanzada
- reportes asíncronos
- auditoría

---

## 5) Frontend - Orden recomendado de construcción

## 5.1 Base de aplicación
- bootstrap
- sesión
- navegación
- cliente API
- manejo de errores

## 5.2 Entrada al sistema
- login
- guardas por rol/permisos

## 5.3 Núcleo de UI
- dashboard
- componentes reutilizables (tabla, formulario, modal, drawer)

## 5.4 Módulos funcionales
- primero el módulo estrella
- luego módulos maestros
- después módulos transaccionales

## 5.5 Cierre
- reportes asíncronos
- auditoría
- i18n
- pruebas de flujos críticos

---

## 6) Reciclaje de código de este mega proyecto (UENS)

## 6.1 Qué reciclar casi directo (alto retorno)
- estructura de monorepo y Maven wrapper
- contrato API base y convención de errores
- utilidades de paginación/filtros
- seguridad base (login, JWT, roles, refresh si aplica)
- cliente HTTP del frontend + parseo + manejo de errores
- sesión global + navegación + feedback UI
- scripts de arranque local (`up-db`, `run-local`, seeds/reset)

## 6.2 Qué reciclar adaptando (requiere mapeo al dominio nuevo)
- módulos CRUD del backend (plantilla de capas `api/application/infrastructure`)
- módulos UI CRUD (flujos, tablas, formularios, validaciones)
- reportes asíncronos (reusar patrón; cambiar tipos y payload)
- auditoría operativa (reusar estructura; cambiar catálogo de acciones)

## 6.3 Qué NO reciclar tal cual
- nombres de entidades de negocio del dominio escolar
- reglas de negocio específicas de UENS
- DTOs y queries amarrados a endpoints concretos de UENS
- textos funcionales de UI y documentación de caso escolar

## 6.4 Estrategia práctica de reciclaje
1. Crea una rama/base plantilla (`template-base`) en tu nuevo repo.
2. Copia primero infraestructura transversal, no módulos de negocio.
3. Renombra paquetes y namespaces del proyecto nuevo.
4. Implementa módulos de dominio desde requerimientos nuevos.
5. Añade tests de contrato para confirmar que no heredaste lógica vieja.

---

## 7) Checklist para iniciar un proyecto nuevo
- [ ] Alcance de V1 cerrado por escrito.
- [ ] Modelo de datos V1 validado con reglas clave.
- [ ] Contrato API base definido antes de UI.
- [ ] Orden de módulos backend acordado.
- [ ] Orden de módulos frontend acordado.
- [ ] Estrategia de reciclaje definida (qué sí, qué no).
- [ ] Entorno reproducible (`.env`, scripts, seeds).

---

## 8) Nota de disciplina arquitectónica
Si cambias una regla de negocio, actualiza en cadena:

1. requerimientos (`docs`),
2. modelo de datos (`db`),
3. validaciones backend,
4. flujo frontend,
5. pruebas.

No cierres una capa sin cerrar las demás.
