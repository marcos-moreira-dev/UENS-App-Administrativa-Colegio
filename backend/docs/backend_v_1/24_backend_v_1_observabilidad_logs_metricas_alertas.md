# 24_backend_v_1_observabilidad_logs_metricas_alertas

- Versión: 1.0
- Estado: Vigente
- Ámbito: ver y entender que pasa en el backend
- Relacionado con:
 - `16_backend_v_1_base_trazabilidad_backend.md`
 - `23_backend_v_1_mantenimiento_operacion_incidentes.md`
 - `28_backend_v_1_despliegue_realista_dev_stage_prod.md`

---

## 1. Propósito

Si no puedes ver que pasa, no puedes operar bien.

Observabilidad significa tener suficiente información para responder:

1. que fallo
2. cuando fallo
3. a quien afecto
4. donde esta el cuello de botella
5. como distinguir fallo de negocio vs fallo técnico

---

## 2. Las 3 capas clasicas

### Logs

Te dicen que paso.

### Métricas

Te dicen cuanto paso y con que frecuencia.

### Tracing

Te dice por donde paso una solicitud a traves de varios componentes.

Para tu proyecto actual, logs + métricas ya dan un salto enorme.

---

## 3. Logs útiles de verdad

Un log backend bueno debe ayudar a diagnosticar, no solo a llenar disco.

Debe incluir cuando haga sentido:

- timestamp
- nivel
- módulo
- requestId
- endpoint o caso de uso
- actor o usuario
- mensaje claro
- causa técnica

---

## 4. RequestId y MDC

El `requestId` es de las mejores inversiones en soporte.

Flujo recomendado:

1. entra request
2. se genera o propaga requestId
3. se pone en MDC
4. viaja por logs, errores y auditoría

Con eso puedes seguir una falla punta a punta.

---

## 5. Que no deberia contener un log

- password
- token completo
- datos sensibles innecesarios
- dumps gigantes por costumbre

---

## 6. Métricas minimas que si valen la pena

1. total de requests por endpoint
2. tiempo promedio y p95
3. cantidad de 4xx y 5xx
4. intentos fallidos de login
5. solicitudes de reporte pendientes, en proceso, error y completadas
6. tamano y cantidad de archivos generados
7. uso de pool de conexiones

---

## 7. Alertas minimas

No alertes por todo.
Alertar demasiado tambien rompe la operación.

Empieza con:

1. muchos 500 en poco tiempo
2. login fallando de forma anormal
3. cola de reportes atascada
4. espacio de disco bajo
5. servicio caido

---

## 8. Dashboards practicos

Dashboard útil no es "bonito".
Es accionable.

Paneles recomendados:

1. trafico general
2. errores por endpoint
3. auth y seguridad
4. reportes y archivos
5. BD y tiempos de consulta

---

## 9. Tracing: cuando importa

Para un monolito modular pequeno no es el primer paso.

Empieza a importar más cuando:

- llamas muchos servicios externos
- tienes colas
- hay varias instancias o procesos
- aparecen jobs asíncronos complejos

---

## 10. Stack realista para aprender

### Opción mínima

- logs en consola
- archivo rotativo
- requestId

### Opción seria pero manejable

- Logback
- Prometheus
- Grafana
- Actuator

### Opción más avanzada

- OpenTelemetry
- Tempo o Jaeger
- Loki

---

## 11. Aplicado a UENS

Tu siguiente nivel natural seria:

1. mantener `requestId` visible siempre
2. agregar métricas con Actuator + Prometheus
3. dashboard de:
 - login
 - errores
 - reportes
4. alertas simples

Eso ya te mete en práctica real sin convertir el proyecto en una nave espacial.

---

## 12. Siguiente lectura

- `25_backend_v_1_seguridad_practica_backend.md`


