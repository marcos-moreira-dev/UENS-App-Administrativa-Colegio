# 21_desktop_roadmap_impl

- **Proyecto:** UENS Desktop (JavaFX)
- **Objetivo:** roadmap de implementacion por milestones, con estado real del repo y decisiones técnicas cerradas.
- **Arquitectura:** MVVM + Navigator + ApplicationServices + componentes reutilizables
- **JDK:** Eclipse Temurin 21

---

## 0) Decisiones cerradas

### 0.1 Build
- **Maven**
- uso obligatorio de **Maven Wrapper** (`mvnw`, `mvnw.cmd`)

### 0.2 Toolchains
El proyecto debe apoyarse en `~/.m2/toolchains.xml` para asegurar compilacion con **Temurin 21**.

### 0.3 JavaFX
- JavaFX **21.0.2**
- plugin de ejecución: `org.openjfx:javafx-maven-plugin:0.0.8`

### 0.4 HTTP + JSON
- HTTP: `java.net.http.HttpClient`
- JSON: **Jackson**

### 0.5 Logging
- API: **SLF4J**
- implementacion: **Logback**

### 0.6 Configuración
- `app.properties` con base URL y timeout
- overrides por sistema y entorno

### 0.7 Sesión
- token en memoria
- bootstrap via `auth/me`

### 0.8 Selectores
- relaciones grandes: `SearchableComboBoxSupport`
- rangos numericos acotados: `Spinner`
- enums pequenos: `ComboBox`

---

## 1) Versiones fijadas

### 1.1 JavaFX
- `org.openjfx:javafx-controls:21.0.2`
- `org.openjfx:javafx-fxml:21.0.2`

### 1.2 Plugin JavaFX
- `org.openjfx:javafx-maven-plugin:0.0.8`

### 1.3 JSON
- `com.fasterxml.jackson:jackson-bom:2.21.1`
- `jackson-databind`
- `jackson-datatype-jsr310`

### 1.4 Logging
- `org.slf4j:slf4j-api:2.0.17`
- `ch.qos.logback:logback-classic:1.5.32`

### 1.5 Tests
- `org.junit:junit-bom:5.14.3`
- `maven-surefire-plugin:3.5.5`
- `org.assertj:assertj-core:3.27.7`

---

## 2) Estructura Maven elegida

La base actual del repo ya usa:
- `pom.xml` en raiz
- módulo `backend/uens-backend`
- módulo `desktop/uens-desktop`

El wrapper ya existe en repo:
- `mvnw`
- `mvnw.cmd`
- `.mvn/wrapper/`

---

## 3) Estado real del scaffold

Ya materializado y validado:
- `desktop/uens-desktop/pom.xml`
- `src/main/java` con base en `app`, `nav`, `session`, `common`, `api`, `ui`
- módulos operativos de auth, shell, dashboard, estudiantes, representantes, docentes, secciones, asignaturas, clases, calificaciones, reportes y auditoría
- `src/main/resources` con CSS/FXML operativos, `app.properties`, `logback.xml`, i18n base, fonts runtime, iconografía y assets
- `src/test/java` con pruebas reales de `ApiClient`, polling, ViewModels y utilidades nuevas

Validación ejecutada:
- `mvn -pl desktop/uens-desktop test`

Límites actuales:
- faltan más pruebas de controllers y flujos integrados;
- la migracion i18n sigue parcial;
- algunos módulos CRUD secundarios todavia pueden recibir presenter/mapper si siguen creciendo.

---

## 4) Milestones

### M0 - Bootstrap mínimo runnable
Estado:
- **Completado**

Incluye:
- `AppLauncher`
- `MainApp`
- `AppBootstrap`
- `AppContext`
- `AppShell.fxml`

### M1 - Infra común
Estado:
- **Completado**

Incluye:
- `ApiClient`
- `ApiResponse<T>`
- `PageResponse<T>`
- `ApiErrorResponse`
- `ErrorInfo`
- `ApplicationServices`

### M2 - Sesión + Login + auth/me
Estado:
- **Completado**

### M3 - Navigator + guards por rol
Estado:
- **Completado**

### M4 - Component Kit reutilizable
Estado:
- **Completado**

Incluye:
- `UiFeedbackService`
- `DrawerCoordinator`
- `UiCommand` / `UiCommands`
- `FxExecutors`
- `SearchableComboBoxSupport`
- `TypographyManager`
- `StatusBadge`

### M5 - Dashboard real
Estado:
- **Completado**

### M6 - Estudiantes
Estado:
- **Completado**

### M7 - CRUDs de soporte
Estado:
- **Completado**

### M8 - Calificaciones
Estado:
- **Completado**

### M9 - Reportes asíncronos
Estado:
- **Completado**

### M10 - Auditoría
Estado:
- **Completado**

### M11 - i18n total
Estado:
- **Parcial**

### M12 - Logging y diagnostico
Estado:
- **Completado en versión operativa**

### M13 - Hardening final
Estado:
- **Continuo**

---

## 5) Configuración elegida

### 5.1 Archivo
- `desktop/uens-desktop/src/main/resources/app.properties`

Contenido base:
- `uens.api.baseUrl=http://localhost:8080`
- `uens.api.timeoutSeconds=15`

### 5.2 Override por entorno
- `UENS_API_BASE_URL`
- `UENS_LOCALE`

---

## 6) Política de logs
- `logs/` en desarrollo
- exportación de informe frontend bajo demanda
- soporte a dialogo "Guardar como" en Desktop del usuario

---

## 7) Regla de uso de este roadmap
- Este documento manda sobre la implementacion técnica del desktop.
- Si el repo real difiere de la documentación, primero se corrige la documentación o se justifica la excepción.
- No se debe avanzar a más código estructural sin que este roadmap siga siendo compatible con el estado real del repo.


