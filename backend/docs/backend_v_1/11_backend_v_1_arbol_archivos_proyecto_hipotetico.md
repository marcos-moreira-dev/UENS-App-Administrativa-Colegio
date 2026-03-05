# 11_backend_v1_arbol_archivos_proyecto_hipotetico

> Árbol hipotético **completo** (V1) para un backend Spring Boot + Java 21 + PostgreSQL + JWT + Swagger + Docker.
>
> **Nota:** es un mapa de archivos/carpetas (sin código). Incluye archivos **reales**, **opcionales** y **futuros** para tener visión completa sin sobreingeniería en implementación.

```text
backend-v1/
├─ .editorconfig
├─ .gitattributes
├─ .gitignore
├─ .env.example
├─ README.md
├─ LICENSE                          (opcional)
├─ NOTICE                           (opcional)
├─ THIRD-PARTY-NOTICES.md           (opcional)
├─ docker-compose.yml
├─ docker-compose.dev.yml           (opcional)
├─ docker-compose.prod.yml          (opcional/futuro)
├─ Dockerfile
├─ Dockerfile.dev                   (opcional)
├─ Makefile                         (opcional)
├─ mvnw
├─ mvnw.cmd
├─ pom.xml
├─ settings.xml                     (opcional, Maven local/team)
├─ .mvn/
│  └─ wrapper/
│     ├─ maven-wrapper.properties
│     └─ maven-wrapper.jar
│
├─ docs/
│  ├─ backend-v1/
│  │  ├─ 00_backend_v1_indice_y_mapa_documental.md
│  │  ├─ 01_backend_v1_vision_y_alcance.md
│  │  ├─ 02_backend_v1_arquitectura_general.md
│  │  ├─ 03_backend_v1_convenciones_y_estandares_codigo.md
│  │  ├─ 04_backend_v1_modelado_aplicacion_y_modulos.md
│  │  ├─ 05_backend_v1_diseno_api_contrato_respuestas_y_errores.md
│  │  ├─ 06_backend_v1_api_endpoints_y_casos_de_uso.md
│  │  ├─ 07_backend_v1_validaciones_reglas_negocio_y_excepciones.md
│  │  ├─ 08_backend_v1_paginacion_filtros_ordenamiento_y_consultas.md
│  │  ├─ 09_backend_v1_seguridad_documentacion_y_despliegue_minimo.md
│  │  ├─ 10_backend_v1_reporte_solicitudes_cola_simple_db_queue.md
│  │  └─ 11_backend_v1_arbol_archivos_proyecto_hipotetico.md
│  │
│  ├─ negocio_base/                 (referencias del contexto funcional)
│  │  ├─ 01_levantamiento_informacion_negocio.md
│  │  ├─ 02_levantamiento_requerimientos.md
│  │  ├─ 03_modelo_conceptual_dominio.md
│  │  ├─ 04_reglas_negocio_y_supuestos.md
│  │  ├─ 05_glosario_alcance_y_limites.md
│  │  └─ V2_3FN.sql
│  │
│  ├─ api/
│  │  ├─ openapi-export.yaml        (opcional, exportado de Swagger)
│  │  ├─ openapi-export.json        (opcional)
│  │  └─ postman/
│  │     ├─ backend_v1_collection.json   (opcional)
│  │     └─ backend_v1_environment.json  (opcional)
│  │
│  ├─ decisiones/
│  │  ├─ ADR-001-api-response.md         (opcional)
│  │  ├─ ADR-002-jwt-stateless.md        (opcional)
│  │  ├─ ADR-003-db-queue-reportes.md    (opcional)
│  │  └─ ADR-004-mappers-manuales.md     (opcional)
│  │
│  ├─ despliegue/
│  │  ├─ variables_entorno.md            (opcional)
│  │  ├─ docker_local.md                 (opcional)
│  │  └─ checklist_release_v1.md         (opcional)
│  │
│  └─ ejemplos/
│     ├─ requests_http.md                (opcional)
│     ├─ ejemplos_errores_api.md         (opcional)
│     └─ ejemplos_paginacion_filtros.md  (opcional)
│
├─ scripts/                        (opcional pero útil)
│  ├─ dev/
│  │  ├─ run-local.sh
│  │  ├─ run-local.ps1
│  │  ├─ up-db.sh
│  │  └─ down-db.sh
│  ├─ db/
│  │  ├─ reset-dev-db.sh                (peligroso, dev only)
│  │  ├─ seed-dev-data.sh               (opcional)
│  │  └─ backup-dev-db.sh               (opcional)
│  ├─ quality/
│  │  ├─ format-check.sh                (opcional)
│  │  └─ lint-check.sh                  (opcional)
│  └─ ci/
│     ├─ build.sh                       (opcional)
│     └─ smoke-test.sh                  (opcional)
│
├─ infra/                          (opcional, si quieres separar infraestructura local)
│  ├─ docker/
│  │  ├─ postgres/
│  │  │  ├─ init/
│  │  │  │  ├─ 001_schema.sql          (opcional si no usas migraciones)
│  │  │  │  └─ 002_seed_dev.sql        (opcional)
│  │  │  └─ conf/                      (opcional)
│  │  └─ app/
│  │     └─ .dockerignore             (opcional si no usas raíz)
│  ├─ nginx/                          (futuro/opcional)
│  │  ├─ nginx.conf
│  │  └─ default.conf
│  └─ monitoring/                     (futuro)
│     ├─ prometheus.yml
│     └─ grafana/...
│
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ com/
│  │  │     └─ tuorganizacion/
│  │  │        └─ backendv1/
│  │  │           ├─ BackendV1Application.java
│  │  │           │
│  │  │           ├─ config/
│  │  │           │  ├─ JacksonConfig.java                (opcional)
│  │  │           │  ├─ OpenApiConfig.java
│  │  │           │  ├─ CorsConfig.java                   (opcional)
│  │  │           │  ├─ LocaleConfig.java                 (opcional)
│  │  │           │  ├─ MessageSourceConfig.java          (si usas i18n)
│  │  │           │  ├─ AsyncConfig.java                  (opcional/futuro)
│  │  │           │  ├─ SchedulingConfig.java            (opcional)
│  │  │           │  ├─ WebMvcConfig.java                (opcional)
│  │  │           │  └─ BeanNamingConfig.java            (opcional)
│  │  │           │
│  │  │           ├─ shared/
│  │  │           │  ├─ api/
│  │  │           │  │  ├─ response/
│  │  │           │  │  │  ├─ ApiResponse.java
│  │  │           │  │  │  ├─ ApiErrorResponse.java
│  │  │           │  │  │  ├─ ApiMeta.java               (opcional)
│  │  │           │  │  │  ├─ PageResponseDto.java
│  │  │           │  │  │  └─ ErrorDetailDto.java        (opcional)
│  │  │           │  │  ├─ pagination/
│  │  │           │  │  │  ├─ PageRequestCriteria.java
│  │  │           │  │  │  ├─ SortCriterion.java
│  │  │           │  │  │  ├─ SortDirection.java
│  │  │           │  │  │  ├─ PaginationConstants.java
│  │  │           │  │  │  └─ PageMapper.java            (opcional)
│  │  │           │  │  ├─ filter/
│  │  │           │  │  │  ├─ FilterParser.java          (opcional)
│  │  │           │  │  │  └─ QueryParamUtils.java       (opcional)
│  │  │           │  │  └─ docs/
│  │  │           │  │     ├─ ApiErrorExamples.java      (opcional)
│  │  │           │  │     └─ ApiSchemas.java            (opcional)
│  │  │           │  │
│  │  │           │  ├─ exception/
│  │  │           │  │  ├─ GlobalExceptionHandler.java
│  │  │           │  │  ├─ ApiException.java
│  │  │           │  │  ├─ ValidationException.java
│  │  │           │  │  ├─ BusinessRuleException.java
│  │  │           │  │  ├─ ResourceNotFoundException.java
│  │  │           │  │  ├─ ConflictException.java        (opcional)
│  │  │           │  │  ├─ UnauthorizedException.java    (opcional)
│  │  │           │  │  ├─ ForbiddenException.java       (opcional)
│  │  │           │  │  └─ ErrorCodeCatalog.java         (opcional)
│  │  │           │  │
│  │  │           │  ├─ validation/
│  │  │           │  │  ├─ ValidationGroups.java         (opcional)
│  │  │           │  │  ├─ annotations/
│  │  │           │  │  │  ├─ ValidSortField.java        (opcional)
│  │  │           │  │  │  ├─ ValidPageSize.java         (opcional)
│  │  │           │  │  │  └─ ...
│  │  │           │  │  └─ validators/
│  │  │           │  │     ├─ ValidSortFieldValidator.java
│  │  │           │  │     ├─ ValidPageSizeValidator.java
│  │  │           │  │     └─ ...
│  │  │           │  │
│  │  │           │  ├─ mapper/
│  │  │           │  │  ├─ CommonMapperUtils.java        (opcional)
│  │  │           │  │  └─ DateTimeMapper.java           (opcional)
│  │  │           │  │
│  │  │           │  ├─ útil/
│  │  │           │  │  ├─ DateTimeUtils.java            (opcional)
│  │  │           │  │  ├─ JsonUtils.java                (opcional)
│  │  │           │  │  ├─ StringUtilsExt.java           (opcional)
│  │  │           │  │  ├─ CollectionUtilsExt.java       (opcional)
│  │  │           │  │  └─ IdGenerator.java              (opcional)
│  │  │           │  │
│  │  │           │  ├─ constants/
│  │  │           │  │  ├─ ApiPaths.java                 (opcional)
│  │  │           │  │  ├─ SecurityConstants.java        (opcional)
│  │  │           │  │  └─ RegexConstants.java           (opcional)
│  │  │           │  │
│  │  │           │  ├─ logging/
│  │  │           │  │  ├─ RequestLoggingFilter.java     (opcional)
│  │  │           │  │  ├─ CorrelationIdFilter.java      (opcional recomendado)
│  │  │           │  │  └─ MdcKeys.java                  (opcional)
│  │  │           │  │
│  │  │           │  └─ domain/                          (opcional, shared-kernel mínimo)
│  │  │           │     ├─ valueobject/
│  │  │           │     │  ├─ EmailAddress.java          (opcional)
│  │  │           │     │  └─ ...
│  │  │           │     └─ enums/
│  │  │           │        └─ EstadoRegistro.java        (opcional)
│  │  │           │
│  │  │           ├─ security/
│  │  │           │  ├─ config/
│  │  │           │  │  └─ SecurityConfig.java
│  │  │           │  ├─ jwt/
│  │  │           │  │  ├─ JwtTokenService.java
│  │  │           │  │  ├─ JwtClaimsFactory.java         (opcional)
│  │  │           │  │  ├─ JwtAuthenticationFilter.java
│  │  │           │  │  ├─ JwtProperties.java
│  │  │           │  │  └─ JwtTokenParser.java           (opcional)
│  │  │           │  ├─ auth/
│  │  │           │  │  ├─ CustomUserDetailsService.java
│  │  │           │  │  ├─ AuthenticatedUserPrincipal.java (opcional)
│  │  │           │  │  ├─ PasswordEncoderProvider.java  (opcional)
│  │  │           │  │  └─ CurrentUserProvider.java      (opcional recomendado)
│  │  │           │  ├─ handler/
│  │  │           │  │  ├─ RestAuthenticationEntryPoint.java
│  │  │           │  │  └─ RestAccessDeniedHandler.java
│  │  │           │  ├─ annotation/
│  │  │           │  │  ├─ CurrentUser.java              (opcional)
│  │  │           │  │  └─ ...
│  │  │           │  └─ permission/
│  │  │           │     ├─ Roles.java
│  │  │           │     ├─ Permissions.java              (opcional/futuro)
│  │  │           │     └─ AuthorizationRules.java       (opcional)
│  │  │           │
│  │  │           ├─ modules/
│  │  │           │  │
│  │  │           │  ├─ auth/
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ AuthController.java
│  │  │           │  │  │  ├─ dto/
│  │  │           │  │  │  │  ├─ LoginRequestDto.java
│  │  │           │  │  │  │  ├─ LoginResponseDto.java
│  │  │           │  │  │  │  └─ AuthenticatedUserDto.java (opcional)
│  │  │           │  │  │  └─ mapper/
│  │  │           │  │  │     └─ AuthApiMapper.java      (opcional)
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ AuthenticationService.java
│  │  │           │  │  │  ├─ usecase/
│  │  │           │  │  │  │  └─ LoginUseCase.java       (opcional si separas interfaz)
│  │  │           │  │  │  └─ command/
│  │  │           │  │  │     └─ LoginCommand.java       (opcional)
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  ├─ model/
│  │  │           │  │  │  │  └─ AuthSessionInfo.java    (opcional)
│  │  │           │  │  │  └─ service/
│  │  │           │  │  │     └─ CredentialValidationDomainService.java (opcional)
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     └─ ...                        (normalmente poco aquí)
│  │  │           │  │
│  │  │           │  ├─ usuarios_sistema/                (si existe módulo en V1)
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ UsuarioSistemaController.java
│  │  │           │  │  │  ├─ dto/
│  │  │           │  │  │  │  ├─ UsuarioSistemaCreateRequestDto.java
│  │  │           │  │  │  │  ├─ UsuarioSistemaUpdateRequestDto.java
│  │  │           │  │  │  │  ├─ UsuarioSistemaResponseDto.java
│  │  │           │  │  │  │  └─ UsuarioSistemaListItemDto.java
│  │  │           │  │  │  └─ mapper/
│  │  │           │  │  │     └─ UsuarioSistemaApiMapper.java
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ UsuarioSistemaCommandService.java
│  │  │           │  │  │  ├─ UsuarioSistemaQueryService.java
│  │  │           │  │  │  ├─ usecase/
│  │  │           │  │  │  │  ├─ CrearUsuarioSistemaUseCase.java
│  │  │           │  │  │  │  ├─ ActualizarUsuarioSistemaUseCase.java
│  │  │           │  │  │  │  ├─ ListarUsuariosSistemaUseCase.java
│  │  │           │  │  │  │  └─ ObtenerUsuarioSistemaUseCase.java
│  │  │           │  │  │  ├─ command/
│  │  │           │  │  │  │  ├─ CrearUsuarioSistemaCommand.java
│  │  │           │  │  │  │  └─ ActualizarUsuarioSistemaCommand.java
│  │  │           │  │  │  └─ query/
│  │  │           │  │  │     └─ UsuarioSistemaSearchCriteria.java
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  ├─ model/
│  │  │           │  │  │  │  ├─ UsuarioSistema.java
│  │  │           │  │  │  │  ├─ RolSistema.java
│  │  │           │  │  │  │  └─ EstadoUsuarioSistema.java
│  │  │           │  │  │  ├─ repository/
│  │  │           │  │  │  │  └─ UsuarioSistemaRepository.java
│  │  │           │  │  │  ├─ service/
│  │  │           │  │  │  │  └─ UsuarioSistemaDomainService.java (opcional)
│  │  │           │  │  │  └─ exception/
│  │  │           │  │  │     ├─ UsuarioSistemaNoEncontradoException.java
│  │  │           │  │  │     └─ LoginDuplicadoException.java
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     ├─ persistence/
│  │  │           │  │     │  ├─ entity/
│  │  │           │  │     │  │  └─ UsuarioSistemaJpaEntity.java
│  │  │           │  │     │  ├─ repository/
│  │  │           │  │     │  │  ├─ SpringDataUsuarioSistemaJpaRepository.java
│  │  │           │  │     │  │  ├─ UsuarioSistemaRepositoryImpl.java
│  │  │           │  │     │  │  └─ UsuarioSistemaQueryRepositoryCustom.java (opcional)
│  │  │           │  │     │  └─ mapper/
│  │  │           │  │     │     └─ UsuarioSistemaPersistenceMapper.java
│  │  │           │  │     └─ spec/
│  │  │           │  │        └─ UsuarioSistemaSpecifications.java (opcional)
│  │  │           │  │
│  │  │           │  ├─ estudiantes/
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ EstudianteController.java
│  │  │           │  │  │  ├─ dto/
│  │  │           │  │  │  │  ├─ EstudianteCreateRequestDto.java
│  │  │           │  │  │  │  ├─ EstudianteUpdateRequestDto.java
│  │  │           │  │  │  │  ├─ EstudianteResponseDto.java
│  │  │           │  │  │  │  ├─ EstudianteListItemDto.java
│  │  │           │  │  │  │  └─ EstudianteFilterRequestDto.java (opcional)
│  │  │           │  │  │  └─ mapper/
│  │  │           │  │  │     └─ EstudianteApiMapper.java
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ EstudianteCommandService.java
│  │  │           │  │  │  ├─ EstudianteQueryService.java
│  │  │           │  │  │  ├─ usecase/
│  │  │           │  │  │  │  ├─ CrearEstudianteUseCase.java
│  │  │           │  │  │  │  ├─ ActualizarEstudianteUseCase.java
│  │  │           │  │  │  │  ├─ ObtenerEstudianteUseCase.java
│  │  │           │  │  │  │  ├─ ListarEstudiantesUseCase.java
│  │  │           │  │  │  │  └─ CambiarEstadoEstudianteUseCase.java (opcional)
│  │  │           │  │  │  ├─ command/
│  │  │           │  │  │  │  ├─ CrearEstudianteCommand.java
│  │  │           │  │  │  │  └─ ActualizarEstudianteCommand.java
│  │  │           │  │  │  └─ query/
│  │  │           │  │  │     └─ EstudianteSearchCriteria.java
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  ├─ model/
│  │  │           │  │  │  │  ├─ Estudiante.java
│  │  │           │  │  │  │  ├─ EstadoEstudiante.java
│  │  │           │  │  │  │  └─ IdentificacionEstudiante.java (opcional VO)
│  │  │           │  │  │  ├─ repository/
│  │  │           │  │  │  │  └─ EstudianteRepository.java
│  │  │           │  │  │  ├─ service/
│  │  │           │  │  │  │  └─ EstudianteDomainService.java (opcional)
│  │  │           │  │  │  └─ exception/
│  │  │           │  │  │     ├─ EstudianteNoEncontradoException.java
│  │  │           │  │  │     └─ EstudianteDuplicadoException.java
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     ├─ persistence/
│  │  │           │  │     │  ├─ entity/
│  │  │           │  │     │  │  └─ EstudianteJpaEntity.java
│  │  │           │  │     │  ├─ repository/
│  │  │           │  │     │  │  ├─ SpringDataEstudianteJpaRepository.java
│  │  │           │  │     │  │  ├─ EstudianteRepositoryImpl.java
│  │  │           │  │     │  │  └─ EstudianteQueryRepositoryCustom.java
│  │  │           │  │     │  └─ mapper/
│  │  │           │  │     │     └─ EstudiantePersistenceMapper.java
│  │  │           │  │     └─ spec/
│  │  │           │  │        └─ EstudianteSpecifications.java (opcional)
│  │  │           │  │
│  │  │           │  ├─ secciones/
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ SeccionController.java
│  │  │           │  │  │  ├─ dto/
│  │  │           │  │  │  │  ├─ SeccionCreateRequestDto.java
│  │  │           │  │  │  │  ├─ SeccionUpdateRequestDto.java
│  │  │           │  │  │  │  ├─ SeccionResponseDto.java
│  │  │           │  │  │  │  └─ SeccionListItemDto.java
│  │  │           │  │  │  └─ mapper/
│  │  │           │  │  │     └─ SeccionApiMapper.java
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ SeccionCommandService.java
│  │  │           │  │  │  ├─ SeccionQueryService.java
│  │  │           │  │  │  ├─ usecase/
│  │  │           │  │  │  │  ├─ CrearSeccionUseCase.java
│  │  │           │  │  │  │  ├─ ActualizarSeccionUseCase.java
│  │  │           │  │  │  │  ├─ ListarSeccionesUseCase.java
│  │  │           │  │  │  │  └─ ObtenerSeccionUseCase.java
│  │  │           │  │  │  ├─ command/
│  │  │           │  │  │  │  ├─ CrearSeccionCommand.java
│  │  │           │  │  │  │  └─ ActualizarSeccionCommand.java
│  │  │           │  │  │  └─ query/
│  │  │           │  │  │     └─ SeccionSearchCriteria.java
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  ├─ model/
│  │  │           │  │  │  │  ├─ Seccion.java
│  │  │           │  │  │  │  └─ EstadoSeccion.java      (opcional)
│  │  │           │  │  │  ├─ repository/
│  │  │           │  │  │  │  └─ SeccionRepository.java
│  │  │           │  │  │  └─ exception/
│  │  │           │  │  │     └─ SeccionNoEncontradaException.java
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     ├─ persistence/
│  │  │           │  │     │  ├─ entity/
│  │  │           │  │     │  │  └─ SeccionJpaEntity.java
│  │  │           │  │     │  ├─ repository/
│  │  │           │  │     │  │  ├─ SpringDataSeccionJpaRepository.java
│  │  │           │  │     │  │  └─ SeccionRepositoryImpl.java
│  │  │           │  │     │  └─ mapper/
│  │  │           │  │     │     └─ SeccionPersistenceMapper.java
│  │  │           │  │     └─ spec/
│  │  │           │  │        └─ SeccionSpecifications.java (opcional)
│  │  │           │  │
│  │  │           │  ├─ matriculas/                      (si existe en dominio)
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ MatriculaController.java
│  │  │           │  │  │  ├─ dto/
│  │  │           │  │  │  │  ├─ MatriculaCreateRequestDto.java
│  │  │           │  │  │  │  ├─ MatriculaResponseDto.java
│  │  │           │  │  │  │  └─ MatriculaListItemDto.java
│  │  │           │  │  │  └─ mapper/
│  │  │           │  │  │     └─ MatriculaApiMapper.java
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ MatriculaCommandService.java
│  │  │           │  │  │  ├─ MatriculaQueryService.java
│  │  │           │  │  │  ├─ usecase/
│  │  │           │  │  │  │  ├─ MatricularEstudianteUseCase.java
│  │  │           │  │  │  │  ├─ AnularMatriculaUseCase.java        (opcional)
│  │  │           │  │  │  │  └─ ListarMatriculasUseCase.java
│  │  │           │  │  │  ├─ command/
│  │  │           │  │  │  │  └─ MatricularEstudianteCommand.java
│  │  │           │  │  │  └─ query/
│  │  │           │  │  │     └─ MatriculaSearchCriteria.java
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  ├─ model/
│  │  │           │  │  │  │  ├─ Matricula.java
│  │  │           │  │  │  │  └─ EstadoMatricula.java
│  │  │           │  │  │  ├─ repository/
│  │  │           │  │  │  │  └─ MatriculaRepository.java
│  │  │           │  │  │  └─ exception/
│  │  │           │  │  │     ├─ CupoInsuficienteException.java
│  │  │           │  │  │     └─ MatriculaDuplicadaException.java
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     ├─ persistence/
│  │  │           │  │     │  ├─ entity/
│  │  │           │  │     │  │  └─ MatriculaJpaEntity.java
│  │  │           │  │     │  ├─ repository/
│  │  │           │  │     │  │  ├─ SpringDataMatriculaJpaRepository.java
│  │  │           │  │     │  │  └─ MatriculaRepositoryImpl.java
│  │  │           │  │     │  └─ mapper/
│  │  │           │  │     │     └─ MatriculaPersistenceMapper.java
│  │  │           │  │     └─ spec/
│  │  │           │  │        └─ MatriculaSpecifications.java (opcional)
│  │  │           │  │
│  │  │           │  ├─ calificaciones/                  (si aplica a tu V1)
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ CalificacionController.java
│  │  │           │  │  │  ├─ dto/
│  │  │           │  │  │  │  ├─ CalificacionCreateRequestDto.java
│  │  │           │  │  │  │  ├─ CalificacionUpdateRequestDto.java
│  │  │           │  │  │  │  ├─ CalificacionResponseDto.java
│  │  │           │  │  │  │  └─ CalificacionListItemDto.java
│  │  │           │  │  │  └─ mapper/
│  │  │           │  │  │     └─ CalificacionApiMapper.java
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ CalificacionCommandService.java
│  │  │           │  │  │  ├─ CalificacionQueryService.java
│  │  │           │  │  │  ├─ usecase/
│  │  │           │  │  │  │  ├─ RegistrarCalificacionUseCase.java
│  │  │           │  │  │  │  ├─ ActualizarCalificacionUseCase.java
│  │  │           │  │  │  │  └─ ListarCalificacionesUseCase.java
│  │  │           │  │  │  ├─ command/
│  │  │           │  │  │  │  ├─ RegistrarCalificacionCommand.java
│  │  │           │  │  │  │  └─ ActualizarCalificacionCommand.java
│  │  │           │  │  │  └─ query/
│  │  │           │  │  │     └─ CalificacionSearchCriteria.java
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  ├─ model/
│  │  │           │  │  │  │  ├─ Calificacion.java
│  │  │           │  │  │  │  └─ TipoParcial.java        (opcional)
│  │  │           │  │  │  ├─ repository/
│  │  │           │  │  │  │  └─ CalificacionRepository.java
│  │  │           │  │  │  └─ exception/
│  │  │           │  │  │     ├─ CalificacionNoEncontradaException.java
│  │  │           │  │  │     └─ RangoCalificacionInvalidoException.java
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     ├─ persistence/
│  │  │           │  │     │  ├─ entity/
│  │  │           │  │     │  │  └─ CalificacionJpaEntity.java
│  │  │           │  │     │  ├─ repository/
│  │  │           │  │     │  │  ├─ SpringDataCalificacionJpaRepository.java
│  │  │           │  │     │  │  └─ CalificacionRepositoryImpl.java
│  │  │           │  │     │  └─ mapper/
│  │  │           │  │     │     └─ CalificacionPersistenceMapper.java
│  │  │           │  │     └─ spec/
│  │  │           │  │        └─ CalificacionSpecifications.java (opcional)
│  │  │           │  │
│  │  │           │  ├─ dashboard/                       (módulo de orquestación/consulta)
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ DashboardController.java
│  │  │           │  │  │  ├─ dto/
│  │  │           │  │  │  │  ├─ DashboardResumenResponseDto.java
│  │  │           │  │  │  │  └─ DashboardWidgetDto.java (opcional)
│  │  │           │  │  │  └─ mapper/
│  │  │           │  │  │     └─ DashboardApiMapper.java (opcional)
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ DashboardQueryService.java
│  │  │           │  │  │  ├─ usecase/
│  │  │           │  │  │  │  └─ ObtenerResumenDashboardUseCase.java
│  │  │           │  │  │  └─ query/
│  │  │           │  │  │     └─ DashboardQuery.java     (opcional)
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  └─ model/
│  │  │           │  │  │     └─ DashboardResumen.java   (opcional)
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     └─ query/
│  │  │           │  │        └─ DashboardJdbcQueryRepository.java (opcional)
│  │  │           │  │
│  │  │           │  └─ reportes/
│  │  │           │     ├─ api/
│  │  │           │     │  ├─ ReporteSolicitudController.java
│  │  │           │     │  ├─ dto/
│  │  │           │     │  │  ├─ CrearReporteSolicitudRequestDto.java
│  │  │           │     │  │  ├─ ReporteSolicitudCreadaResponseDto.java
│  │  │           │     │  │  ├─ ReporteSolicitudDetalleResponseDto.java
│  │  │           │     │  │  ├─ ReporteSolicitudResultadoDto.java
│  │  │           │     │  │  ├─ ReporteSolicitudListItemDto.java
│  │  │           │     │  │  ├─ ReporteSolicitudFilterRequestDto.java (opcional)
│  │  │           │     │  │  └─ ReportePayloadResponseDto.java        (opcional)
│  │  │           │     │  └─ mapper/
│  │  │           │     │     ├─ ReporteSolicitudApiMapper.java
│  │  │           │     │     └─ ReporteResultadoApiMapper.java        (opcional)
│  │  │           │     ├─ application/
│  │  │           │     │  ├─ ReporteSolicitudCommandService.java
│  │  │           │     │  ├─ ReporteSolicitudQueryService.java
│  │  │           │     │  ├─ ReporteSolicitudQueueProcessor.java
│  │  │           │     │  ├─ ReporteDataPreparationService.java       (opcional si centralizas)
│  │  │           │     │  ├─ usecase/
│  │  │           │     │  │  ├─ CrearReporteSolicitudUseCase.java
│  │  │           │     │  │  ├─ ObtenerReporteSolicitudUseCase.java
│  │  │           │     │  │  ├─ ListarReporteSolicitudesUseCase.java
│  │  │           │     │  │  ├─ ObtenerResultadoReporteUseCase.java
│  │  │           │     │  │  ├─ ProcesarSiguienteReporteSolicitudUseCase.java (opcional)
│  │  │           │     │  │  ├─ ReintentarReporteSolicitudUseCase.java (opcional)
│  │  │           │     │  │  └─ CancelarReporteSolicitudUseCase.java  (opcional)
│  │  │           │     │  ├─ command/
│  │  │           │     │  │  ├─ CrearReporteSolicitudCommand.java
│  │  │           │     │  │  ├─ ReintentarReporteSolicitudCommand.java (opcional)
│  │  │           │     │  │  └─ CancelarReporteSolicitudCommand.java  (opcional)
│  │  │           │     │  ├─ query/
│  │  │           │     │  │  └─ ReporteSolicitudSearchCriteria.java
│  │  │           │     │  └─ worker/
│  │  │           │     │     ├─ ReporteSolicitudWorkerScheduler.java
│  │  │           │     │     ├─ ReporteSolicitudWorkerProperties.java  (opcional)
│  │  │           │     │     └─ ReporteSolicitudProcessingResult.java  (opcional)
│  │  │           │     ├─ domain/
│  │  │           │     │  ├─ model/
│  │  │           │     │  │  ├─ ReporteSolicitudQueue.java
│  │  │           │     │  │  ├─ EstadoReporteSolicitud.java
│  │  │           │     │  │  ├─ TipoReporte.java
│  │  │           │     │  │  ├─ ReporteParametros.java              (opcional VO)
│  │  │           │     │  │  └─ ReporteResultadoPayload.java        (opcional)
│  │  │           │     │  ├─ repository/
│  │  │           │     │  │  ├─ ReporteSolicitudRepository.java
│  │  │           │     │  │  └─ ReporteSolicitudClaimRepository.java (repo custom)
│  │  │           │     │  ├─ service/
│  │  │           │     │  │  ├─ ReporteSolicitudStateService.java   (opcional)
│  │  │           │     │  │  ├─ ReporteIdempotencyService.java      (opcional)
│  │  │           │     │  │  └─ ReporteProcessorSelector.java       (recomendado)
│  │  │           │     │  ├─ processor/
│  │  │           │     │  │  ├─ ReporteDataProcessor.java
│  │  │           │     │  │  ├─ ListadoEstudiantesPorSeccionProcessor.java
│  │  │           │     │  │  ├─ ResumenSeccionProcessor.java        (opcional)
│  │  │           │     │  │  ├─ CalificacionesPorSeccionProcessor.java (opcional)
│  │  │           │     │  │  └─ CalificacionesPorEstudianteProcessor.java (opcional)
│  │  │           │     │  └─ exception/
│  │  │           │     │     ├─ ReporteSolicitudNoEncontradaException.java
│  │  │           │     │     ├─ ResultadoReporteNoListoException.java
│  │  │           │     │     ├─ TipoReporteNoSoportadoException.java
│  │  │           │     │     ├─ ReporteNoReintentableException.java   (opcional)
│  │  │           │     │     └─ ReporteProcessingException.java       (opcional)
│  │  │           │     └─ infrastructure/
│  │  │           │        ├─ persistence/
│  │  │           │        │  ├─ entity/
│  │  │           │        │  │  └─ ReporteSolicitudQueueJpaEntity.java
│  │  │           │        │  ├─ repository/
│  │  │           │        │  │  ├─ SpringDataReporteSolicitudJpaRepository.java
│  │  │           │        │  │  ├─ ReporteSolicitudRepositoryImpl.java
│  │  │           │        │  │  ├─ ReporteSolicitudClaimRepositoryImpl.java
│  │  │           │        │  │  └─ ReporteSolicitudQueryRepositoryCustom.java (opcional)
│  │  │           │        │  ├─ mapper/
│  │  │           │        │  │  └─ ReporteSolicitudPersistenceMapper.java
│  │  │           │        │  └─ spec/
│  │  │           │        │     └─ ReporteSolicitudSpecifications.java (opcional)
│  │  │           │        └─ serialization/
│  │  │           │           ├─ ReportePayloadJsonSerializer.java      (opcional)
│  │  │           │           └─ ReportePayloadJsonDeserializer.java    (opcional)
│  │  │           │
│  │  │           ├─ infrastructure/
│  │  │           │  ├─ persistence/
│  │  │           │  │  ├─ jpa/
│  │  │           │  │  │  ├─ BaseJpaEntity.java                    (opcional)
│  │  │           │  │  │  ├─ AuditableJpaEntity.java               (opcional)
│  │  │           │  │  │  └─ JpaAuditingConfig.java                (opcional)
│  │  │           │  │  ├─ converters/
│  │  │           │  │  │  ├─ JsonNodeAttributeConverter.java       (opcional)
│  │  │           │  │  │  ├─ UuidListAttributeConverter.java       (opcional)
│  │  │           │  │  │  └─ ...
│  │  │           │  │  └─ query/
│  │  │           │  │     ├─ QuerydslSupport.java                  (opcional)
│  │  │           │  │     └─ JdbcQuerySupport.java                 (opcional)
│  │  │           │  ├─ db/
│  │  │           │  │  ├─ migration/                               (si usas Flyway/Liquibase custom path java)
│  │  │           │  │  └─ seed/
│  │  │           │  │     └─ DevDataSeeder.java                    (opcional)
│  │  │           │  ├─ time/
│  │  │           │  │  ├─ ClockProvider.java                       (opcional recomendado)
│  │  │           │  │  └─ SystemClockProvider.java                 (opcional)
│  │  │           │  ├─ uuid/
│  │  │           │  │  └─ UuidGenerator.java                       (opcional)
│  │  │           │  └─ startup/
│  │  │           │     ├─ StartupBannerLogger.java                 (opcional)
│  │  │           │     └─ StartupSanityChecks.java                 (opcional)
│  │  │           │
│  │  │           └─ support/                                       (opcional, para utilidades por módulo)
│  │  │              └─ ...
│  │  │
│  │  └─ resources/
│  │     ├─ application.properties
│  │     ├─ application-dev.properties
│  │     ├─ application-prod.properties
│  │     ├─ application-test.properties                 (opcional/futuro)
│  │     ├─ banner.txt                           (opcional)
│  │     ├─ messages.properties                  (si usas MessageSource)
│  │     ├─ messages_es.properties               (opcional)
│  │     ├─ messages_en.properties               (opcional)
│  │     │
│  │     ├─ db/
│  │     │  ├─ migration/                        (Flyway recomendado)
│  │     │  │  ├─ V001__baseline_schema.sql
│  │     │  │  ├─ V002__indices_iniciales.sql
│  │     │  │  ├─ V003__tabla_reporte_solicitud_queue.sql
│  │     │  │  ├─ V004__indices_reporte_queue.sql
│  │     │  │  ├─ V005__seed_catalogos_dev.sql  (opcional si separas por perfil)
│  │     │  │  └─ Vxxx__...sql
│  │     │  ├─ seed/
│  │     │  │  ├─ dev/
│  │     │  │  │  ├─ 001_usuarios.sql           (opcional)
│  │     │  │  │  ├─ 002_estudiantes.sql        (opcional)
│  │     │  │  │  └─ 003_secciones.sql          (opcional)
│  │     │  │  └─ test/
│  │     │  │     └─ ...
│  │     │  └─ templates/                       (opcional)
│  │     │     └─ queries_reportes.sql          (opcional)
│  │     │
│  │     ├─ openapi/
│  │     │  └─ examples/                        (opcional)
│  │     │     ├─ auth-login-200.json
│  │     │     ├─ api-error-401.json
│  │     │     ├─ api-error-403.json
│  │     │     ├─ estudiantes-list-200.json
│  │     │     └─ reporte-solicitud-201.json
│  │     │
│  │     ├─ static/                             (normalmente vacío en API)
│  │     │  └─ .gitkeep                         (opcional)
│  │     └─ logback-spring.xml                  (opcional recomendado)
│  │
│  └─ test/
│     ├─ java/
│     │  └─ com/
│     │     └─ tuorganizacion/
│     │        └─ backendv1/
│     │           ├─ BackendV1ApplicationTests.java
│     │           │
│     │           ├─ support/
│     │           │  ├─ TestDataFactory.java                    (opcional)
│     │           │  ├─ TestClockConfig.java                    (opcional)
│     │           │  ├─ TestSecurityConfig.java                 (opcional)
│     │           │  ├─ BaseIntegrationTest.java                (opcional)
│     │           │  ├─ BaseControllerIntegrationTest.java      (opcional)
│     │           │  ├─ BaseRepositoryIntegrationTest.java      (opcional)
│     │           │  ├─ BaseUseCaseTest.java                    (opcional)
│     │           │  └─ builders/
│     │           │     ├─ EstudianteTestBuilder.java           (opcional)
│     │           │     ├─ SeccionTestBuilder.java              (opcional)
│     │           │     └─ ReporteSolicitudTestBuilder.java     (opcional)
│     │           │
│     │           ├─ shared/
│     │           │  ├─ api/
│     │           │  │  ├─ ApiResponseSerializationTest.java    (opcional)
│     │           │  │  └─ PaginationContractTest.java          (opcional)
│     │           │  ├─ exception/
│     │           │  │  └─ GlobalExceptionHandlerTest.java      (opcional)
│     │           │  └─ validation/
│     │           │     └─ ...
│     │           │
│     │           ├─ security/
│     │           │  ├─ SecurityConfigTest.java                 (opcional)
│     │           │  ├─ JwtTokenServiceTest.java                (opcional)
│     │           │  ├─ JwtAuthenticationFilterTest.java        (opcional)
│     │           │  ├─ AuthControllerIntegrationTest.java      (opcional)
│     │           │  └─ handler/
│     │           │     ├─ RestAuthenticationEntryPointTest.java (opcional)
│     │           │     └─ RestAccessDeniedHandlerTest.java      (opcional)
│     │           │
│     │           ├─ modules/
│     │           │  ├─ estudiantes/
│     │           │  │  ├─ api/
│     │           │  │  │  └─ EstudianteControllerIntegrationTest.java
│     │           │  │  ├─ application/
│     │           │  │  │  ├─ CrearEstudianteUseCaseTest.java   (opcional)
│     │           │  │  │  └─ ListarEstudiantesUseCaseTest.java (opcional)
│     │           │  │  ├─ domain/
│     │           │  │  │  └─ EstudianteDomainTest.java         (opcional)
│     │           │  │  └─ infrastructure/
│     │           │  │     └─ EstudianteRepositoryImplIT.java   (opcional)
│     │           │  │
│     │           │  ├─ secciones/
│     │           │  │  ├─ api/
│     │           │  │  │  └─ SeccionControllerIntegrationTest.java
│     │           │  │  ├─ application/
│     │           │  │  │  └─ ...
│     │           │  │  └─ infrastructure/
│     │           │  │     └─ ...
│     │           │  │
│     │           │  ├─ matriculas/
│     │           │  │  ├─ api/
│     │           │  │  │  └─ MatriculaControllerIntegrationTest.java
│     │           │  │  ├─ application/
│     │           │  │  │  └─ MatricularEstudianteUseCaseTest.java (opcional)
│     │           │  │  └─ domain/
│     │           │  │     └─ MatriculaDomainRulesTest.java       (opcional)
│     │           │  │
│     │           │  ├─ calificaciones/
│     │           │  │  ├─ api/
│     │           │  │  │  └─ CalificacionControllerIntegrationTest.java
│     │           │  │  ├─ application/
│     │           │  │  │  └─ ...
│     │           │  │  └─ domain/
│     │           │  │     └─ ...
│     │           │  │
│     │           │  ├─ dashboard/
│     │           │  │  └─ api/
│     │           │  │     └─ DashboardControllerIntegrationTest.java (opcional)
│     │           │  │
│     │           │  └─ reportes/
│     │           │     ├─ api/
│     │           │     │  ├─ ReporteSolicitudControllerIntegrationTest.java
│     │           │     │  └─ ReporteResultadoControllerIntegrationTest.java (opcional)
│     │           │     ├─ application/
│     │           │     │  ├─ CrearReporteSolicitudUseCaseTest.java
│     │           │     │  ├─ ReporteSolicitudQueueProcessorTest.java (opcional)
│     │           │     │  ├─ ReporteSolicitudWorkerSchedulerTest.java (opcional)
│     │           │     │  └─ ReporteProcessorSelectorTest.java        (opcional)
│     │           │     ├─ domain/
│     │           │     │  ├─ ReporteSolicitudQueueDomainTest.java     (opcional)
│     │           │     │  └─ EstadoReporteSolicitudTransitionsTest.java (opcional)
│     │           │     └─ infrastructure/
│     │           │        ├─ ReporteSolicitudClaimRepositoryImplIT.java (importante si haces SKIP LOCKED)
│     │           │        └─ ReporteSolicitudRepositoryImplIT.java      (opcional)
│     │           │
│     │           └─ smoke/
│     │              ├─ HealthEndpointIT.java               (opcional)
│     │              └─ OpenApiAvailabilityIT.java          (opcional)
│     │
│     └─ resources/
│        ├─ application-test.properties
│        ├─ testdata/
│        │  ├─ usuarios/
│        │  │  └─ usuarios_base.json            (opcional)
│        │  ├─ estudiantes/
│        │  │  └─ estudiantes_base.json         (opcional)
│        │  ├─ reportes/
│        │  │  ├─ crear_solicitud_request.json  (opcional)
│        │  │  └─ resultado_payload_ejemplo.json (opcional)
│        │  └─ ...
│        └─ db/
│           ├─ migration/
│           │  └─ ...                           (si reusas migraciones)
│           └─ seed/
│              └─ test_seed.sql                 (opcional)
│
├─ .github/                         (opcional/futuro, pero típico)
│  ├─ workflows/
│  │  ├─ ci.yml
│  │  ├─ build.yml                   (opcional)
│  │  ├─ test.yml                    (opcional)
│  │  └─ release.yml                 (opcional/futuro)
│  ├─ ISSUE_TEMPLATE/
│  │  ├─ bug_report.md               (opcional)
│  │  └─ feature_request.md          (opcional)
│  └─ pull_request_template.md       (opcional)
│
├─ .idea/                            (NO subir; IDE local)
├─ .vscode/                          (opcional; puede omitirse del repo)
│  ├─ extensions.json
│  ├─ launch.json
│  ├─ settings.json
│  └─ tasks.json
│
├─ target/                           (generado; NO subir)
│  ├─ *.jar
│  ├─ classes/
│  └─ ...
│
└─ tmp/                              (opcional local; NO subir)
   ├─ exports/
   ├─ logs/
   └─ scratch/
```

## Variante mínima realista (para empezar sin abrumarte)

```text
backend-v1/
├─ .env.example
├─ .gitignore
├─ README.md
├─ Dockerfile
├─ docker-compose.yml
├─ mvnw
├─ mvnw.cmd
├─ pom.xml
├─ docs/
│  └─ backend-v1/
│     ├─ 00_backend_v1_indice_y_mapa_documental.md
│     ├─ ...
│     └─ 11_backend_v1_arbol_archivos_proyecto_hipotetico.md
├─ src/
│  ├─ main/
│  │  ├─ java/com/tuorganizacion/backendv1/
│  │  │  ├─ BackendV1Application.java
│  │  │  ├─ config/
│  │  │  ├─ shared/
│  │  │  ├─ security/
│  │  │  └─ modules/
│  │  │     ├─ auth/
│  │  │     ├─ estudiantes/
│  │  │     ├─ secciones/
│  │  │     ├─ matriculas/
│  │  │     ├─ calificaciones/
│  │  │     ├─ dashboard/
│  │  │     └─ reportes/
│  │  └─ resources/
│  │     ├─ application.properties
│  │     ├─ application-dev.properties
│  │     ├─ application-prod.properties
│  │     └─ db/migration/
│  └─ test/
│     ├─ java/
│     └─ resources/
└─ scripts/ (opcional)
```

## Nota de uso práctico

- Este árbol es **hipotético y amplio** para que tengas mapa mental completo.
- **No necesitas crear todo** desde el día 1.
- La implementación V1 puede arrancar con la **variante mínima realista** y crecer módulo por módulo.



