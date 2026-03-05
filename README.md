# Sistema UENS

![Logo UENS](images/v1/logo.png)

UENS es un proyecto educativo que muestra como puede verse un sistema relativamente completo cuando se documenta de punta a punta: levantamiento basico de informacion, modelo de datos, backend, frontend desktop y operacion tecnica.

No es una demo aislada. Es una referencia de producto para estudiar como conectar negocio, datos, API y experiencia de usuario dentro de un alcance realista.

## Que hace este repositorio

- Base de datos relacional alineada al dominio escolar.
- Backend Spring Boot modular con seguridad, trazabilidad, reportes y auditoria.
- Frontend JavaFX con modulos operativos y flujo administrativo real.
- Documentacion extensa para estudio tecnico y mantenimiento.

## Vista general por capa

### Base de datos

La BD sostiene entidades academicas y administrativas: estudiantes, representantes, docentes, secciones, clases, calificaciones, usuarios, reportes y auditoria.

Referencias clave:

- [Levantamiento de informacion](docs/01_levantamiento_informacion_negocio.md)
- [Requerimientos](docs/02_levantamiento_requerimientos.md)
- [Modelo conceptual](docs/03_modelo_conceptual_dominio.md)
- [Reglas de negocio](docs/04_reglas_negocio_y_supuestos.md)
- [Esquema SQL V2 3FN](db/V2/docs/Diagramas%20y%20query%20de%20creaci%C3%B3n/V2_3FN.sql)
- [Diccionario de datos](db/V2/docs/diccionario_de_datos_uens_v_2_3_fn.md)

### Backend

Implementado como monolito modular (Spring Boot + Java 21), con separacion por modulos y capas.

Incluye:

- autenticacion y sesion renovable
- seguridad y politicas base
- paginacion, filtros y contrato API uniforme
- reportes asincronos con descarga
- auditoria y trazabilidad operativa

Referencias:

- [README backend](backend/uens-backend/README.md)
- [Indice documental backend](backend/docs/backend_v_1/00_backend_v_1_indice_y_mapa_documental.md)

### Frontend desktop

Aplicacion JavaFX orientada a operacion escolar/administrativa: login, dashboard, CRUDs, reportes y auditoria.

Referencias:

- [README desktop](desktop/uens-desktop/README.md)
- [Indice documental frontend](desktop/docs/00_desktop_indice_y_mapa_documental.md)

## Galeria visual del producto (V1)

Estas son las capturas reales publicadas en `images/v1`.

### Login

![Login](images/v1/login.png)

Pantalla de autenticacion del sistema. Es la entrada al flujo seguro y el inicio de sesion por rol.

### Dashboard

![Dashboard](images/v1/dashboard.png)

Vista principal tras autenticarse. Resume navegacion, contexto y acceso rapido a modulos.

### CRUD Estudiantes

![CRUD Estudiantes](images/v1/crud_estudiantes.png)

Modulo central de gestion de estudiantes, con listado, filtros, busqueda y acciones operativas.

### CRUD Representantes

![CRUD Representantes](images/v1/crud_representantes.png)

Gestion de representantes legales y su relacion con estudiantes en el contexto administrativo.

### Crear Docente

![Crear Docente](images/v1/crear_docente.png)

Formulario de alta de docentes para mostrar captura de datos, validaciones y persistencia.

### Reportes

![Reportes](images/v1/reportes_pantalla.png)

Pantalla de reportes asincronos para solicitud, seguimiento y salida de archivos.

### Solicitar reporte de auditoria

![Solicitar reporte de auditoria](images/v1/solicitar_reporte_auditor%C3%ADa.png)

Flujo especifico para solicitar reportes de trazabilidad administrativa.

### Auditoria

![Auditoria](images/v1/auditor%C3%ADa_pantalla.png)

Vista de eventos auditables para control interno, soporte y analisis de operaciones.

### Instalador 1

![Instalador 1](images/v1/installer%201.png)

Primera vista del instalador MSI en Windows, pensada para distribucion del cliente desktop.

### Instalador 2

![Instalador 2](images/v1/installer%202.png)

Segunda vista del instalador para evidenciar el flujo de instalacion y su presentacion final.

### Modelo conceptual del dominio

![Modelo conceptual](images/v1/ModeloConceptual.drawio.png)

Diagrama de referencia del dominio para conectar analisis, datos y arquitectura.

## Documentacion de apoyo

### Negocio y dominio

- [Contexto inicial](docs/00_plantilla_descripcion_empresa_y_contexto_inicial.md)
- [Levantamiento de informacion](docs/01_levantamiento_informacion_negocio.md)
- [Levantamiento de requerimientos](docs/02_levantamiento_requerimientos.md)
- [Modelo conceptual](docs/03_modelo_conceptual_dominio.md)
- [Reglas y supuestos](docs/04_reglas_negocio_y_supuestos.md)
- [Glosario, alcance y limites](docs/05_glosario_alcance_y_limites.md)

### Backend

- [Mapa documental backend](backend/docs/backend_v_1/00_backend_v_1_indice_y_mapa_documental.md)
- [Arquitectura general](backend/docs/backend_v_1/02_backend_v_1_arquitectura_general.md)
- [Contrato API y errores](backend/docs/backend_v_1/05_backend_v_1_diseno_api_contrato_respuestas_y_errores.md)
- [Seguridad y despliegue minimo](backend/docs/backend_v_1/09_backend_v_1_seguridad_documentacion_y_despliegue_minimo.md)

### Frontend

- [Mapa documental frontend](desktop/docs/00_desktop_indice_y_mapa_documental.md)
- [Vision y criterios UX/UI](desktop/docs/01_desktop_vision_alcance_y_criterios_ux_ui.md)
- [Layout y navegacion](desktop/docs/05_desktop_layout_shell_y_navegacion.md)
- [Cliente API y DTOs UI](desktop/docs/08_desktop_cliente_api_contratos_y_dtos_ui.md)
- [Flujo de reportes async](desktop/docs/15_desktop_flujo_reportes_async.md)

## Estructura del workspace

```text
.
├─ backend/
│  ├─ docs/
│  └─ uens-backend/
├─ db/
│  └─ V2/
├─ desktop/
│  ├─ docs/
│  └─ uens-desktop/
├─ docs/
├─ images/
│  └─ v1/
└─ pom.xml
```

## Como arrancarlo rapido

- Backend: [backend/uens-backend/README.md](backend/uens-backend/README.md)
- Desktop: [desktop/uens-desktop/README.md](desktop/uens-desktop/README.md)

## Configuracion de secretos

Para entorno local, usa plantilla y archivo privado:

1. Copia [backend/uens-backend/.env.example](backend/uens-backend/.env.example) como `backend/uens-backend/.env`.
2. Ajusta credenciales y claves (DB, JWT, etc.) solo en tu archivo `.env` local.
3. No subas `.env` al repositorio.

En produccion, no uses archivos versionados para secretos. Deben inyectarse por variables de entorno o un gestor de secretos (vault, secret manager del proveedor cloud, etc.).

## Licencia

El workspace usa licencia copyleft fuerte: **AGPL-3.0-or-later**.

- [LICENSE raiz](LICENSE)
- [Aviso licencia DB](db/LICENSE)
- [Aviso licencia backend](backend/uens-backend/LICENSE)
- [Aviso licencia desktop](desktop/uens-desktop/LICENSE)

## Aviso

Proyecto con fines educativos y de portafolio tecnico. Su valor principal es mostrar un flujo completo y documentado de construccion de producto.
