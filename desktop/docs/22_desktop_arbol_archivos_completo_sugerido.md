# 22_desktop_arbol_archivos_completo_sugerido

> **Objetivo:** describir el árbol objetivo del módulo desktop dentro del monorepo.
>
> **Regla de lectura:** este documento no es un snapshot exacto del repo; es un objetivo de estructura. La implementación debe avanzar por milestones.

---

## 0) Estado real del scaffold

Ya existe en el repo:
- `pom.xml` agregador en raiz
- `mvnw`, `mvnw.cmd` y `.mvn/wrapper/`
- `desktop/uens-desktop/` como módulo Maven real
- scaffold inicial en `app`, `nav`, `session`, `common`, `api`, `ui`, `modules/auth`, `modules/shell`, `modules/dashboard`
- recursos base
- pruebas automatizadas base para cliente API, polling y ViewModels

Todavía no esta expandido todo el árbol objetivo. Eso se implementa por milestones.

---

## A) Árbol del repositorio (raiz)

```text
Sistema UE Niñitos Soñadores/
|-- pom.xml
|-- mvnw
|-- mvnw.cmd
|-- .mvn/
|   `-- wrapper/
|       |-- maven-wrapper.properties
|       |-- maven-wrapper.cmd
|       `-- MavenWrapperDownloader.java
|-- backend/
|   |-- docs/
|   `-- uens-backend/
|-- db/
|-- desktop/
|   |-- assets/
|   |-- docs/
|   `-- uens-desktop/
|       |-- pom.xml
|       |-- README.md
|       |-- .gitignore
|       |-- src/
|       `-- tools/
`-- tools/
```

---

## B) Árbol objetivo del módulo desktop

### B.1 Java

```text
desktop/uens-desktop/src/main/java/com/marcosmoreiradev/uensdesktop/
|-- app/
|-- nav/
|-- session/
|-- common/
|-- api/
|-- ui/
`-- modules/
    |-- auth/
    |-- shell/
    |-- dashboard/
    |-- estudiantes/
    |-- representantes/
    |-- docentes/
    |-- secciones/
    |-- asignaturas/
    |-- clases/
    |-- calificaciones/
    |-- reportes/
    `-- auditoria/
```

### B.2 Resources

```text
desktop/uens-desktop/src/main/resources/
|-- app.properties
|-- logback.xml
|-- assets/
|-- css/
|-- fxml/
`-- i18n/
```

### B.3 Tests

```text
desktop/uens-desktop/src/test/java/
|-- api/
|-- common/
`-- modules/
```

---

## C) Regla de implementación
- El árbol sirve para fijar nombres y ubicaciones.
- No conviene generar cientos de archivos vacios solo para "cumplir" el árbol.
- Se crean primero las piezas necesarias para el milestone actual.
- Si una parte del árbol todavía no tiene comportamiento ni contrato claro, debe esperar.

---

## D) Prioridad real

Primero:
- `app`
- `nav`
- `session`
- `common`
- `api.client`
- `api.contract`
- `modules/auth`
- `modules/shell`
- `modules/dashboard`

Después:
- `modules/estudiantes`
- componentes reutilizables
- módulos de soporte
- reportes
- auditoría

---

## E) Fuentes de verdad complementarias
- `desktop/docs/18_desktop_arquitectura_paquetes_y_capas_mvvm.md`
- `desktop/docs/21_desktop_roadmap_impl.md`
- `backend/docs/api/API_ENDPOINTS.md`
- `docs/01_levantamiento_informacion_negocio.md`
- `docs/02_levantamiento_requerimientos.md`
- `docs/03_modelo_conceptual_dominio.md`
- `docs/04_reglas_negocio_y_supuestos.md`
- `docs/05_glosario_alcance_y_limites.md`



