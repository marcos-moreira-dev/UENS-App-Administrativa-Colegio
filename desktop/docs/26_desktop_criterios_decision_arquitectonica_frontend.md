# 26_desktop_criterios_decision_arquitectonica_frontend

- **Proyecto:** UENS Desktop (JavaFX)
- **Objetivo:** dejar documentados los criterios practicos usados para tomar decisiones de arquitectura, UI y organización del código en el frontend desktop.

---

## 1) Propósito
Este documento responde una pregunta simple:

**por que el proyecto eligio esta solución y no otra?**

Sirve para:

1. justificar decisiones técnicas;
2. evitar cambios por intuicion o moda;
3. dejar reglas útiles para estudiantes y futuros mantenedores;
4. convertir la arquitectura en algo ensenable.

---

## 2) Criterios maestros

### 2.1 Claridad
Una solución se prefiere si deja más clara la responsabilidad de cada pieza.

**Ejemplo:** `Query Object` hace más clara la consulta que una firma larga en un servicio.

### 2.2 Cohesion
Una pieza debe encargarse de una familia de responsabilidades cercanas entre si.

**Ejemplo:** `UiFeedbackService` concentra dialogs y confirmaciones; no deberian vivir mezclados en cada controller.

### 2.3 Bajo acoplamiento
La UI no debe depender de detalles de red, URLs o parseo HTTP.

**Ejemplo:** un controller no llama `ApiClient` directamente; usa un servicio de aplicación.

### 2.4 Reuso real
Una abstraccion solo vale si puede ser aprovechada por más de un módulo o más de un flujo.

**Ejemplo:** `DrawerCoordinator` no es un helper local; sirve a varios módulos.

### 2.5 Escalabilidad controlada
El sistema debe poder crecer sin reescritura agresiva, pero sin meter complejidad prematura.

### 2.6 Valor docente
Este proyecto también es educativo. Por eso se favorecen soluciones que:
- se puedan explicar bien;
- muestren responsabilidades limpias;
- conecten teoria con código real.

---

## 3) Criterios para estructura de capas

### 3.1 La vista no hace HTTP
**Motivo:** mezcla presentación con infraestructura y rompe el rol del controller.

### 3.2 El ViewModel expone estado observable
**Motivo:** la vista debe reaccionar a propiedades, no a sincronizacion manual caotica.

### 3.3 La capa application orquesta casos de uso
**Motivo:** evita que la UI conozca detalles del backend.

### 3.4 La infraestructura queda encapsulada
**Ejemplo:** `ApiClient`, descargas, parseo, log export.

---

## 4) Criterios para patrones de diseño

### 4.1 Cuando introducir Query Object
Usarlo si una consulta tiene:
- paginación;
- búsqueda;
- tres o más filtros;
- o varias fechas/opciones de orden.

### 4.2 Cuando introducir Presenter
Usarlo si el controller:
- traduce estados;
- formatea fechas;
- arma badges;
- compone texto visible para usuarios.

### 4.3 Cuando introducir Command
Usarlo si una acción:
- tiene varios pasos;
- necesita loading + resultado + feedback;
- o se repite en varios handlers.

### 4.4 Cuando introducir una fachada de UI
Usarla si dialogs, toasts o mensajes ya se repiten y la UX debe ser consistente.

### 4.5 Cuando NO introducir un patron
No introducirlo si:
- solo cambia nombres;
- agrega clases sin bajar complejidad;
- no se puede reaprovechar;
- cuesta más explicarlo que mantener la solución actual.

---

## 5) Criterios para diseño de interfaces

### 5.1 Un bloque, una acción principal
No debe haber dos CTAs dominantes en el mismo bloque.

### 5.2 La jerarquía visual debe guiar el trabajo
Titulos, labels, filtros y acciones deben competir lo menos posible.

### 5.3 Las tablas deben privilegiar lectura operativa
Por eso se ajustaron headers oscuros, seleccion verde oscura y filtros más claros.

### 5.4 La ayuda debe hablar en lenguaje del dominio
Los tooltips no deben decir solo "abre módulo" sino explicar para que sirve a un administrador.

### 5.5 Las tipografias deben ser serias y legibles
Se favorecen fuentes cargadas desde `resources`, con perfiles comerciales válidos y sin extravagancia visual.

---

## 6) Criterios para feedback y errores

### 6.1 Error de listado no es lo mismo que error global
Un error local debe ir como banner o mensaje de la pantalla, no siempre como modal.

### 6.2 La confirmación debe usarse solo para acciones sensibles
Cambios de estado, cierre obligatorio de sesión, salida controlada.

### 6.3 Los mensajes deben dejar rastro útil
Cuando se pueda, incluir `requestId`, ruta de descarga o explicacion operativa.

### 6.4 La UX de feedback debe ser consistente
Por eso existe `UiFeedbackService`.

---

## 7) Criterios para concurrencia

### 7.1 Toda la UI se actualiza en JavaFX Application Thread
No se negocia.

### 7.2 Todo I/O va en background
Requests HTTP, exportaciones, descargas, polling.

### 7.3 La asincronía debe ser legible
Por eso se usa `FxExecutors` y no lógica repetida de hilos en cada controller.

---

## 8) Criterios para navegación y sesión

### 8.1 La navegación debe pasar por un punto central
Por eso se usa `Navigator`.

### 8.2 El rol debe afectar visibilidad y acceso
No basta con esconder botones; también debe existir control de flujo.

### 8.3 El cierre de ventana debe respetar auditoría
Por eso la salida con `X` intenta logout y deja trazabilidad.

---

## 9) Criterios para documentación técnica

### 9.1 La documentación debe seguir al código real
No puede describir arquitectura inventada.

### 9.2 La documentación debe ser útil para ensenar
No basta con listar clases; hay que explicar por que existen.

### 9.3 El glosario debe crecer con los terminos del repo
Si aparece un termino raro nuevo, debe documentarse.

---

## 10) Preguntas de revisión antes de aprobar un cambio

1. Esta pieza quedo más clara o solo más sofisticada?
2. Baja duplicacion real?
3. Se puede explicar facilmente a un estudiante?
4. Desacopla responsabilidades o las mezcla?
5. La UI sigue entendible para el usuario administrativo?
6. La solución esta alineada con MVVM y Navigator?

Si la respuesta a varias de estas preguntas es "no", el cambio debe revisarse.

---

## 11) Relacion con otros documentos
- `18_desktop_arquitectura_paquetes_y_capas_mvvm.md`
- `23_desktop_glosario_frontend_y_arquitectura.md`
- `24_desktop_patrones_diseno_usados_y_recomendados.md`
- `25_desktop_patrones_diseno_ejemplos_antes_despues.md`


