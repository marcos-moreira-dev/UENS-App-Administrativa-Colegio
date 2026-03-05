# 19_desktop_i18n_textos_y_mensajes

- **Proyecto:** UENS Desktop (JavaFX)
- **Objetivo:** fijar una política realista de textos, localizacion e i18n para el frontend desktop.

---

## 1) Propósito
Este documento responde cuatro cosas:

1. que significa i18n dentro de este desktop;
2. cual es el alcance real de idiomas;
3. que parte ya esta preparada y que parte sigue en migracion;
4. como deben escribirse los textos visibles del sistema.

Nota:
- `i18n` significa *internationalization*;
- en esta base se traduce como infraestructura para soportar más de un idioma sin reescribir la UI.

---

## 2) Alcance oficial
El desktop queda oficialmente limitado a:

- **Espanol** (`es`) como idioma por defecto;
- **Ingles** (`en`) como idioma secundario opcional.

No se contemplan más idiomas en esta fase.

Configuración soportada:
- `-Duens.locale=es`
- `-Duens.locale=en`
- variable de entorno `UENS_LOCALE`

Si el valor es invalido, el sistema vuelve a `es`.

---

## 3) Estado real del codigo

### 3.1 Infraestructura ya existente
- `common.i18n.I18n`
- `resources/i18n/messages_es.properties`
- `resources/i18n/messages_en.properties`
- `ResourceBundle` integrado en bootstrap y navegación

### 3.2 Lo que ya esta soportado
- seleccion de locale `es/en`;
- idioma por defecto `es`;
- bootstrap preparado para `ResourceBundle`;
- algunas claves globales ya externalizadas.

### 3.3 Lo que sigue parcial
- muchos labels de FXML;
- varios mensajes de controllers;
- copy de drawers, banners y tooltips;
- textos de formularios y confirmaciones.

Conclusion real:
- la infraestructura i18n **si existe**;
- la migracion funcional **todavia es parcial**.

La documentación no debe prometer una externalizacion total mientras esa migracion no se complete.

---

## 4) Regla editorial del texto visible
Todo texto que vea el usuario debe cumplir:

- espanol correcto;
- tildes y signos cuando correspondan;
- vocabulario estable del dominio;
- tono administrativo claro;
- mensajes breves y accionables.

Ejemplos correctos:
- `Sesión`
- `Contraseña`
- `Seccion`
- `Pagina`
- `Calificacion`
- `Auditoria`
- `Información`

Nota de implementacion:
- en codigo Java se aceptan escapes Unicode;
- en FXML pueden usarse entidades XML si hace falta evitar problemas de encoding;
- lo importante es que **la UI renderizada** muestre el espanol correctamente.

Queda prohibido degradar el copy visible a texto sin acentos por comodidad técnica.

---

## 5) Vocabulario de dominio obligatorio
Cuando aplique, la UI debe usar estas etiquetas:

- Estudiante
- Representante legal
- Docente
- Seccion
- Asignatura
- Clase
- Calificacion
- Reporte
- Auditoria
- Sesión

Regla:
- no inventar sinonimos distintos por módulo;
- si el backend expone nombres técnicos, la UI los traduce al vocabulario operativo visible.

---

## 6) Estrategia recomendada

### 6.1 Bundle para texto transversal
Usar `ResourceBundle` para:
- titulos globales;
- navegación;
- acciones comunes;
- estados compartidos;
- labels repetidos;
- mensajes globales de sesión o shell.

### 6.2 Texto directo permitido, con limites
Durante la migracion se acepta texto directo en:
- controllers;
- FXML;
- presenters.

Solo si:
- es especifico del módulo;
- todavia no compensa crear una key compartida;
- mantiene redaccion coherente con el resto del sistema.

### 6.3 Regla de crecimiento
Cuando un texto se repite en 2 o más módulos, debe pasar a bundle.

---

## 7) Estructura recomendada de keys
- `app.*`
- `nav.*`
- `common.*`
- `entity.*`
- `state.*`
- `module.<módulo>.*`
- `error.*`
- `confirm.*`

Ejemplos:
- `app.title`
- `nav.reportes`
- `common.save`
- `entity.seccion`
- `state.active`
- `module.reportes.create_request`

---

## 8) Backend vs frontend
Regla práctica:

- el backend manda mensajes operativos y de error;
- el frontend no debe reescribirlos arbitrariamente;
- el frontend si puede envolverlos con contexto de UI si mejora claridad.

Ejemplo correcto:
- backend: `No existe la seccion solicitada`
- frontend: mostrar ese mensaje en banner o dialogo, sin cambiar su sentido.

---

## 9) Checklist realista
- [x] La app soporta `es` y `en`.
- [x] El idioma por defecto es `es`.
- [x] Existe infraestructura i18n operativa.
- [ ] Toda la UI esta externalizada al bundle.
- [x] Los textos visibles nuevos deben escribirse en espanol correcto.
- [x] La documentación reconoce que la migracion sigue parcial.

---

## 10) Criterio de coherencia
La verdad actual del proyecto es:

- si existe infraestructura i18n;
- si existe decision cerrada de soporte solo para `es` y `en`;
- no toda la UI esta traducida ni externalizada;
- si debemos mantener el espanol visible correctamente escrito desde ya;
- si conviene seguir migrando texto transversal al bundle en cada iteracion importante.


