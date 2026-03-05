# 25_backend_v_1_seguridad_practica_backend

- Version: 1.0
- Estado: Vigente
- Ámbito: seguridad backend para trabajo realista
- Relacionado con:
  - `20_backend_v_1_hardening_seguridad_login_rate_limit_cors_headers_ownership.md`
  - `24_backend_v_1_observabilidad_logs_metricas_alertas.md`
  - `28_backend_v_1_despliegue_realista_dev_stage_prod.md`

---

## 1. Propósito

Seguridad práctica significa proteger el sistema con medidas que realmente puedes operar y mantener.

No significa solo "poner JWT".

---

## 2. Capas que debes mirar

1. autenticación
2. autorización
3. validación de entrada
4. proteccion de archivos
5. secretos y configuración
6. base de datos y backups
7. despliegue
8. monitoreo de abuso

---

## 3. OWASP que si deberias conocer

No necesitas memorizar toda la lista, pero si entender al menos:

1. broken access control
2. cryptographic failures
3. injection
4. insecure design
5. security misconfiguration
6. vulnerable components
7. identification and authentication failures
8. logging and monitoring failures

---

## 4. Preguntas prácticas de seguridad

Antes de exponer un endpoint, preguntate:

1. quien puede llamarlo
2. que pasa si manipulan un ID
3. que pasa si reintentan muchas veces
4. que pasa si el token expira a mitad del flujo
5. que pasa si el archivo o la metadata fueron alterados

---

## 5. Controles mínimos recomendados

### Auth

- password hash
- expiración de token
- refresh token rotativo
- lockout y rate limit

### Authz

- roles
- ownership
- reglas a nivel application

### HTTP

- CORS estricto
- `nosniff`
- no cachear respuestas sensibles
- error JSON uniforme

### Datos

- queries parametrizadas
- validación fuerte
- constraints en BD

---

## 6. Secretos y configuración

Nunca mezcles seguridad con comodidad.

Debes saber manejar:

- variables de entorno
- `.env.example`
- secretos fuera del repo
- usuarios de BD con permisos mínimos

---

## 7. Archivos y storage

Los archivos abren muchos riesgos:

- path traversal
- nombres manipulados
- MIME falso
- fuga de recursos de otro usuario
- crecimiento sin control

Por eso es correcta tu direccion actual:

- puerto de storage
- adapter concreto
- ownership
- saneamiento de nombres

---

## 8. Seguridad operativa

Ademas del codigo, debes cuidar:

1. actualizaciones de dependencias
2. acceso SSH o acceso al proveedor
3. backups
4. rotacion de secretos
5. cuentas con mínimo privilegio

---

## 9. Errores comunes de gente que empieza

- confiar en que el frontend oculto resuelve permisos
- dejar secretos hardcodeados
- loggear cosas sensibles
- usar una cuenta de BD con privilegios de sobra
- abrir CORS a todo por pereza

---

## 10. Aplicado a UENS

Tu proyecto ya hace varias cosas bien:

- JWT
- lockout y rate limit
- ownership
- headers
- requestId
- auditoria

Lo siguiente razonable seria:

1. endurecer despliegue
2. backups
3. usuario de BD mínimo
4. integración de métricas de auth

---

## 11. Siguiente lectura

- `26_backend_v_1_performance_escalabilidad_y_cuello_de_botella.md`


