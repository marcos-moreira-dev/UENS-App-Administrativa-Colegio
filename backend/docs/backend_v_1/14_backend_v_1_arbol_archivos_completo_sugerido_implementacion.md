# 14_backend_v_1_arbol_archivos_completo_sugerido_implementacion

## 1. Propósito

Definir un **árbol de archivos completo sugerido** (sin código) para iniciar la implementación del backend V1, obedeciendo:

- diseño de dominio y casos de uso
- BD oficial PostgreSQL (`V2_3FN.sql`)
- arquitectura backend V1 (monolito modular, feature-first + capas)
- contrato API estándar (`ApiResponse`, `ApiErrorResponse`, `PageResponseDto`)
- seguridad JWT stateless
- reportes asíncronos con DB queue
- uso de **`.properties`** en lugar de YAML

> Este documento reemplaza la sugerencia previa de árbol si ya no está alineada.

---

## 2. Principios de este árbol

1. **Feature-first** (módulos por capacidad de negocio)
2. **Capas internas por módulo** (`api`, `application`, `domain`, `infrastructure`)
3. **Transversales controlados** (`common`, `config`, `security`)
4. **Sin entidad persistente `Matricula`** (en V1 actual)
5. **Mappers manuales** (sin esconder lógica en frameworks)
6. **`.properties`** + `MessageSource`
7. Preparado para crecer sin sobreingeniería

---

## 3. Árbol de archivos sugerido (completo, sin código)

```text
backend-v1/
├─ .gitignore
├─ .gitattributes
├─ README.md
├─ LICENSE                              (opcional, recomendado)
├─ NOTICE                               (opcional, recomendado)
├─ THIRD-PARTY-NOTICES.md               (opcional, recomendado)
├─ .env.example
├─ Dockerfile
├─ docker-compose.yml
├─ mvnw
├─ mvnw.cmd
├─ pom.xml
├─ docs/                                (opcional: snapshots de decisiones / ejemplos API)
│  ├─ README_BACKEND_V1.md
│  ├─ openapi-notas.md
│  ├─ contratos-api-resumen.md
│  └─ ejemplos/
│     ├─ auth-login.http
│     ├─ estudiantes.http
│     └─ reportes.http
├─ scripts/                             (opcional, utilidades locales)
│  ├─ run-dev.bat
│  ├─ run-dev.sh
│  ├─ package.bat
│  └─ package.sh
├─ sql/                                 (sin migraciones aún; referencia/control manual)
│  ├─ README_SQL.md
│  ├─ schema/
│  │  └─ V2_3FN.sql                     (copia controlada de la versión oficial o referencia)
│  ├─ seeds/
│  │  ├─ seed_dev_usuarios.sql          (opcional)
│  │  └─ seed_dev_datos_minimos.sql     (opcional)
│  └─ reportes/
│     └─ ddl_reporte_solicitud_queue.sql (cuando implementes módulo reportes)
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ com/
│  │  │     └─ marcosmoreira/           (ajustar a tu package real)
│  │  │        └─ uensbackend/          (ajustar nombre real del proyecto)
│  │  │           ├─ UensBackendApplication.java
│  │  │           │
│  │  │           ├─ config/
│  │  │           │  ├─ JacksonConfig.java
│  │  │           │  ├─ LocaleConfig.java                    (opcional si usas i18n temprano)
│  │  │           │  ├─ MessageSourceConfig.java
│  │  │           │  ├─ OpenApiConfig.java
│  │  │           │  ├─ SchedulingConfig.java                (para @Scheduled de reportes)
│  │  │           │  ├─ TimeZoneConfig.java                  (opcional)
│  │  │           │  └─ properties/
│  │  │           │     ├─ JwtProperties.java
│  │  │           │     ├─ PaginationProperties.java         (opcional)
│  │  │           │     ├─ SwaggerProperties.java            (opcional)
│  │  │           │     └─ ReportQueueProperties.java        (intervalos/backoff/etc.)
│  │  │           │
│  │  │           ├─ common/
│  │  │           │  ├─ api/
│  │  │           │  │  ├─ response/
│  │  │           │  │  │  ├─ ApiResponse.java
│  │  │           │  │  │  ├─ ApiErrorResponse.java
│  │  │           │  │  │  ├─ ErrorDetailDto.java
│  │  │           │  │  │  └─ PageResponseDto.java
│  │  │           │  │  └─ útil/
│  │  │           │  │     └─ ResponseFactory.java           (opcional)
│  │  │           │  │
│  │  │           │  ├─ exception/
│  │  │           │  │  ├─ GlobalExceptionHandler.java
│  │  │           │  │  ├─ base/
│  │  │           │  │  │  ├─ ApplicationException.java
│  │  │           │  │  │  ├─ ValidationException.java
│  │  │           │  │  │  ├─ BusinessRuleException.java
│  │  │           │  │  │  ├─ ApiException.java
│  │  │           │  │  │  ├─ AuthException.java
│  │  │           │  │  │  └─ SystemException.java
│  │  │           │  │  ├─ codes/
│  │  │           │  │  │  ├─ ErrorCode.java                (interface/base contract)
│  │  │           │  │  │  ├─ AuthErrorCodes.java
│  │  │           │  │  │  ├─ ApiErrorCodes.java
│  │  │           │  │  │  ├─ StudentErrorCodes.java
│  │  │           │  │  │  ├─ SeccionErrorCodes.java
│  │  │           │  │  │  ├─ ClaseErrorCodes.java
│  │  │           │  │  │  ├─ CalificacionErrorCodes.java
│  │  │           │  │  │  └─ ReporteErrorCodes.java
│  │  │           │  │  └─ mapper/
│  │  │           │  │     └─ ExceptionToApiErrorMapper.java (opcional)
│  │  │           │  │
│  │  │           │  ├─ i18n/
│  │  │           │  │  ├─ MessageKeys.java                 (opcional, constantes de claves)
│  │  │           │  │  └─ MessageResolver.java
│  │  │           │  │
│  │  │           │  ├─ pagination/
│  │  │           │  │  ├─ ApiPageRequest.java              (objeto interno opcional)
│  │  │           │  │  ├─ PageMapper.java
│  │  │           │  │  ├─ PageSortParser.java
│  │  │           │  │  ├─ SortWhitelistValidator.java
│  │  │           │  │  └─ PageableFactory.java
│  │  │           │  │
│  │  │           │  ├─ validation/
│  │  │           │  │  ├─ ValidationErrorAssembler.java
│  │  │           │  │  ├─ groups/                          (opcional)
│  │  │           │  │  └─ annotations/                     (opcional; solo si crece)
│  │  │           │  │
│  │  │           │  ├─ útil/
│  │  │           │  │  ├─ DateTimeUtils.java               (opcional)
│  │  │           │  │  ├─ JsonUtils.java                   (opcional)
│  │  │           │  │  └─ StringUtils.java                 (opcional, evitar basurero)
│  │  │           │  │
│  │  │           │  └─ constants/
│  │  │           │     ├─ ApiPaths.java                    (opcional)
│  │  │           │     └─ SecurityRoles.java
│  │  │           │
│  │  │           ├─ security/                              (infraestructura de seguridad)
│  │  │           │  ├─ config/
│  │  │           │  │  └─ SecurityConfig.java
│  │  │           │  ├─ jwt/
│  │  │           │  │  ├─ JwtTokenService.java
│  │  │           │  │  ├─ JwtAuthenticationFilter.java
│  │  │           │  │  └─ JwtClaimsFactory.java            (opcional)
│  │  │           │  ├─ user/
│  │  │           │  │  ├─ CustomUserDetails.java
│  │  │           │  │  └─ CustomUserDetailsService.java
│  │  │           │  ├─ handler/
│  │  │           │  │  ├─ RestAuthenticationEntryPoint.java
│  │  │           │  │  └─ RestAccessDeniedHandler.java
│  │  │           │  └─ support/
│  │  │           │     └─ SecurityContextFacade.java       (opcional)
│  │  │           │
│  │  │           ├─ modules/
│  │  │           │  │
│  │  │           │  ├─ auth/
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ AuthController.java
│  │  │           │  │  │  └─ dto/
│  │  │           │  │  │     ├─ LoginRequestDto.java
│  │  │           │  │  │     ├─ LoginResponseDto.java
│  │  │           │  │  │     ├─ AuthUsuarioResumenDto.java
│  │  │           │  │  │     └─ AuthMeResponseDto.java     (opcional)
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ AuthApplicationService.java
│  │  │           │  │  │  ├─ command/
│  │  │           │  │  │  │  └─ LoginCommand.java          (opcional)
│  │  │           │  │  │  └─ mapper/
│  │  │           │  │  │     └─ AuthDtoMapper.java
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  └─ model/                        (normalmente mínimo en auth V1)
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     ├─ persistence/
│  │  │           │  │     │  ├─ entity/
│  │  │           │  │     │  │  └─ UsuarioSistemaAdministrativoJpaEntity.java
│  │  │           │  │     │  ├─ repository/
│  │  │           │  │     │  │  └─ UsuarioSistemaAdministrativoJpaRepository.java
│  │  │           │  │     │  └─ mapper/
│  │  │           │  │     │     └─ UsuarioSistemaAdministrativoJpaMapper.java (opcional)
│  │  │           │  │     └─ service/
│  │  │           │  │        └─ PasswordHashService.java   (opcional wrapper BCrypt)
│  │  │           │  │
│  │  │           │  ├─ estudiante/
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ EstudianteController.java
│  │  │           │  │  │  └─ dto/
│  │  │           │  │  │     ├─ EstudianteCreateRequestDto.java
│  │  │           │  │  │     ├─ EstudianteUpdateRequestDto.java
│  │  │           │  │  │     ├─ EstudiantePatchEstadoRequestDto.java
│  │  │           │  │  │     ├─ AsignarSeccionVigenteRequestDto.java
│  │  │           │  │  │     ├─ EstudianteResponseDto.java
│  │  │           │  │  │     ├─ EstudianteListItemDto.java
│  │  │           │  │  │     └─ EstudianteResumenDto.java  (opcional)
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ EstudianteCommandService.java
│  │  │           │  │  │  ├─ EstudianteQueryService.java
│  │  │           │  │  │  ├─ validator/
│  │  │           │  │  │  │  ├─ EstudianteRequestValidator.java
│  │  │           │  │  │  │  └─ EstudianteFilterValidator.java
│  │  │           │  │  │  ├─ mapper/
│  │  │           │  │  │  │  └─ EstudianteDtoMapper.java
│  │  │           │  │  │  └─ specification/               (opcional)
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  ├─ model/
│  │  │           │  │  │  │  └─ Estudiante.java            (si manejas dominio desacoplado de JPA)
│  │  │           │  │  │  ├─ service/
│  │  │           │  │  │  │  └─ EstudianteDomainService.java (opcional)
│  │  │           │  │  │  └─ repository/
│  │  │           │  │  │     └─ EstudianteRepository.java  (puerto, opcional según estilo)
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     └─ persistence/
│  │  │           │  │        ├─ entity/
│  │  │           │  │        │  └─ EstudianteJpaEntity.java
│  │  │           │  │        ├─ repository/
│  │  │           │  │        │  ├─ EstudianteJpaRepository.java
│  │  │           │  │        │  └─ EstudianteQueryRepository.java (custom queries, opcional)
│  │  │           │  │        └─ specification/
│  │  │           │  │           └─ EstudianteSpecifications.java  (opcional)
│  │  │           │  │
│  │  │           │  ├─ representante/
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ RepresentanteLegalController.java
│  │  │           │  │  │  └─ dto/
│  │  │           │  │  │     ├─ RepresentanteLegalCreateRequestDto.java
│  │  │           │  │  │     ├─ RepresentanteLegalUpdateRequestDto.java
│  │  │           │  │  │     ├─ RepresentanteLegalResponseDto.java
│  │  │           │  │  │     └─ RepresentanteLegalListItemDto.java
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ RepresentanteLegalCommandService.java
│  │  │           │  │  │  ├─ RepresentanteLegalQueryService.java
│  │  │           │  │  │  └─ mapper/
│  │  │           │  │  │     └─ RepresentanteLegalDtoMapper.java
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  └─ model/
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     └─ persistence/
│  │  │           │  │        ├─ entity/
│  │  │           │  │        │  └─ RepresentanteLegalJpaEntity.java
│  │  │           │  │        └─ repository/
│  │  │           │  │           └─ RepresentanteLegalJpaRepository.java
│  │  │           │  │
│  │  │           │  ├─ seccion/
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ SeccionController.java
│  │  │           │  │  │  └─ dto/
│  │  │           │  │  │     ├─ SeccionCreateRequestDto.java
│  │  │           │  │  │     ├─ SeccionUpdateRequestDto.java
│  │  │           │  │  │     ├─ SeccionPatchEstadoRequestDto.java
│  │  │           │  │  │     ├─ SeccionResponseDto.java
│  │  │           │  │  │     ├─ SeccionListItemDto.java
│  │  │           │  │  │     └─ SeccionEstudianteListItemDto.java
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ SeccionCommandService.java
│  │  │           │  │  │  ├─ SeccionQueryService.java
│  │  │           │  │  │  ├─ validator/
│  │  │           │  │  │  │  ├─ SeccionRequestValidator.java
│  │  │           │  │  │  │  └─ SeccionFilterValidator.java
│  │  │           │  │  │  └─ mapper/
│  │  │           │  │  │     └─ SeccionDtoMapper.java
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  ├─ model/
│  │  │           │  │  │  └─ service/
│  │  │           │  │  │     └─ SeccionCupoDomainService.java (opcional)
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     └─ persistence/
│  │  │           │  │        ├─ entity/
│  │  │           │  │        │  └─ SeccionJpaEntity.java
│  │  │           │  │        └─ repository/
│  │  │           │  │           └─ SeccionJpaRepository.java
│  │  │           │  │
│  │  │           │  ├─ docente/
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ DocenteController.java
│  │  │           │  │  │  └─ dto/
│  │  │           │  │  │     ├─ DocenteCreateRequestDto.java
│  │  │           │  │  │     ├─ DocenteUpdateRequestDto.java
│  │  │           │  │  │     ├─ DocentePatchEstadoRequestDto.java
│  │  │           │  │  │     ├─ DocenteResponseDto.java
│  │  │           │  │  │     └─ DocenteListItemDto.java
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ DocenteCommandService.java
│  │  │           │  │  │  ├─ DocenteQueryService.java
│  │  │           │  │  │  └─ mapper/
│  │  │           │  │  │     └─ DocenteDtoMapper.java
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  └─ model/
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     └─ persistence/
│  │  │           │  │        ├─ entity/
│  │  │           │  │        │  └─ DocenteJpaEntity.java
│  │  │           │  │        └─ repository/
│  │  │           │  │           └─ DocenteJpaRepository.java
│  │  │           │  │
│  │  │           │  ├─ asignatura/
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ AsignaturaController.java
│  │  │           │  │  │  └─ dto/
│  │  │           │  │  │     ├─ AsignaturaCreateRequestDto.java
│  │  │           │  │  │     ├─ AsignaturaUpdateRequestDto.java
│  │  │           │  │  │     ├─ AsignaturaPatchEstadoRequestDto.java
│  │  │           │  │  │     ├─ AsignaturaResponseDto.java
│  │  │           │  │  │     └─ AsignaturaListItemDto.java
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ AsignaturaCommandService.java
│  │  │           │  │  │  ├─ AsignaturaQueryService.java
│  │  │           │  │  │  └─ mapper/
│  │  │           │  │  │     └─ AsignaturaDtoMapper.java
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  └─ model/
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     └─ persistence/
│  │  │           │  │        ├─ entity/
│  │  │           │  │        │  └─ AsignaturaJpaEntity.java
│  │  │           │  │        └─ repository/
│  │  │           │  │           └─ AsignaturaJpaRepository.java
│  │  │           │  │
│  │  │           │  ├─ clase/
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ ClaseController.java
│  │  │           │  │  │  └─ dto/
│  │  │           │  │  │     ├─ ClaseCreateRequestDto.java
│  │  │           │  │  │     ├─ ClaseUpdateRequestDto.java
│  │  │           │  │  │     ├─ ClasePatchEstadoRequestDto.java
│  │  │           │  │  │     ├─ ClaseAsignarDocenteRequestDto.java (opcional)
│  │  │           │  │  │     ├─ ClaseResponseDto.java
│  │  │           │  │  │     └─ ClaseListItemDto.java
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ ClaseCommandService.java
│  │  │           │  │  │  ├─ ClaseQueryService.java
│  │  │           │  │  │  ├─ validator/
│  │  │           │  │  │  │  ├─ ClaseRequestValidator.java
│  │  │           │  │  │  │  └─ ClaseHorarioValidator.java     (RN: no solapamiento, si lo implementas ya)
│  │  │           │  │  │  └─ mapper/
│  │  │           │  │  │     └─ ClaseDtoMapper.java
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  ├─ model/
│  │  │           │  │  │  └─ service/
│  │  │           │  │  │     └─ ClaseDomainService.java         (opcional)
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     └─ persistence/
│  │  │           │  │        ├─ entity/
│  │  │           │  │        │  └─ ClaseJpaEntity.java
│  │  │           │  │        ├─ repository/
│  │  │           │  │        │  ├─ ClaseJpaRepository.java
│  │  │           │  │        │  └─ ClaseQueryRepository.java    (custom, opcional)
│  │  │           │  │        └─ specification/
│  │  │           │  │           └─ ClaseSpecifications.java     (opcional)
│  │  │           │  │
│  │  │           │  ├─ calificacion/
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ CalificacionController.java
│  │  │           │  │  │  └─ dto/
│  │  │           │  │  │     ├─ CalificacionCreateRequestDto.java
│  │  │           │  │  │     ├─ CalificacionUpdateRequestDto.java
│  │  │           │  │  │     ├─ CalificacionResponseDto.java
│  │  │           │  │  │     └─ CalificacionListItemDto.java
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ CalificacionCommandService.java
│  │  │           │  │  │  ├─ CalificacionQueryService.java
│  │  │           │  │  │  ├─ validator/
│  │  │           │  │  │  │  └─ CalificacionRequestValidator.java
│  │  │           │  │  │  └─ mapper/
│  │  │           │  │  │     └─ CalificacionDtoMapper.java
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  ├─ model/
│  │  │           │  │  │  └─ service/
│  │  │           │  │  │     └─ CalificacionDomainService.java (opcional)
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     └─ persistence/
│  │  │           │  │        ├─ entity/
│  │  │           │  │        │  └─ CalificacionJpaEntity.java
│  │  │           │  │        └─ repository/
│  │  │           │  │           └─ CalificacionJpaRepository.java
│  │  │           │  │
│  │  │           │  ├─ dashboard/
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ DashboardController.java
│  │  │           │  │  │  └─ dto/
│  │  │           │  │  │     ├─ DashboardResumenResponseDto.java
│  │  │           │  │  │     ├─ DashboardTotalesDto.java       (opcional)
│  │  │           │  │  │     └─ DashboardAlertaDto.java        (opcional)
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  ├─ DashboardQueryService.java
│  │  │           │  │  │  └─ mapper/
│  │  │           │  │  │     └─ DashboardDtoMapper.java
│  │  │           │  │  ├─ domain/
│  │  │           │  │  │  └─ model/                            (normalmente mínimo)
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     └─ query/
│  │  │           │  │        └─ DashboardQueryRepository.java  (consultas agregadas)
│  │  │           │  │
│  │  │           │  ├─ consulta/                               (opcional V1; si realmente tendrás endpoints separados)
│  │  │           │  │  ├─ api/
│  │  │           │  │  │  ├─ ConsultaOperativaController.java
│  │  │           │  │  │  └─ dto/
│  │  │           │  │  │     └─ ConsultaAlertaResponseDto.java (placeholder posible)
│  │  │           │  │  ├─ application/
│  │  │           │  │  │  └─ ConsultaOperativaQueryService.java
│  │  │           │  │  └─ infrastructure/
│  │  │           │  │     └─ query/
│  │  │           │  │        └─ ConsultaOperativaQueryRepository.java
│  │  │           │  │
│  │  │           │  └─ reporte/
│  │  │           │     ├─ api/
│  │  │           │     │  ├─ ReporteSolicitudController.java
│  │  │           │     │  └─ dto/
│  │  │           │     │     ├─ CrearReporteSolicitudRequestDto.java
│  │  │           │     │     ├─ ReporteSolicitudCreadaResponseDto.java
│  │  │           │     │     ├─ ReporteSolicitudDetalleResponseDto.java
│  │  │           │     │     ├─ ReporteSolicitudResultadoResponseDto.java
│  │  │           │     │     ├─ ReporteSolicitudListItemDto.java
│  │  │           │     │     ├─ ReporteSolicitudEstadoResponseDto.java (opcional si separas /estado)
│  │  │           │     │     └─ payload/
│  │  │           │     │        ├─ ListadoEstudiantesPorSeccionPayloadDto.java
│  │  │           │     │        ├─ ResumenSeccionPayloadDto.java
│  │  │           │     │        ├─ CalificacionesPorSeccionParcialPayloadDto.java
│  │  │           │     │        └─ CalificacionesPorEstudiantePayloadDto.java
│  │  │           │     ├─ application/
│  │  │           │     │  ├─ ReporteSolicitudCommandService.java
│  │  │           │     │  ├─ ReporteSolicitudQueryService.java
│  │  │           │     │  ├─ ReporteSolicitudWorkerService.java       (worker/orquestación)
│  │  │           │     │  ├─ processor/
│  │  │           │     │  │  ├─ ReporteDataProcessor.java
│  │  │           │     │  │  ├─ ReporteDataProcessorSelector.java
│  │  │           │     │  │  ├─ ListadoEstudiantesPorSeccionProcessor.java
│  │  │           │     │  │  ├─ ResumenSeccionProcessor.java
│  │  │           │     │  │  ├─ CalificacionesPorSeccionParcialProcessor.java
│  │  │           │     │  │  └─ CalificacionesPorEstudianteProcessor.java
│  │  │           │     │  ├─ worker/
│  │  │           │     │  │  ├─ ReporteSolicitudWorkerScheduler.java
│  │  │           │     │  │  └─ ReporteSolicitudClaimCoordinator.java (opcional)
│  │  │           │     │  ├─ mapper/
│  │  │           │     │  │  ├─ ReporteSolicitudDtoMapper.java
│  │  │           │     │  │  └─ ReportePayloadDtoMapper.java
│  │  │           │     │  └─ validator/
│  │  │           │     │     ├─ CrearReporteSolicitudRequestValidator.java
│  │  │           │     │     └─ ReporteSolicitudFilterValidator.java
│  │  │           │     ├─ domain/
│  │  │           │     │  ├─ model/
│  │  │           │     │  │  ├─ ReporteSolicitudQueue.java
│  │  │           │     │  │  ├─ ReporteSolicitudEstado.java
│  │  │           │     │  │  └─ TipoReporte.java
│  │  │           │     │  ├─ service/
│  │  │           │     │  │  ├─ ReporteSolicitudStateService.java
│  │  │           │     │  │  └─ ReporteRetryPolicyService.java
│  │  │           │     │  └─ repository/
│  │  │           │     │     └─ ReporteSolicitudQueueRepository.java  (puerto opcional)
│  │  │           │     └─ infrastructure/
│  │  │           │        ├─ persistence/
│  │  │           │        │  ├─ entity/
│  │  │           │        │  │  └─ ReporteSolicitudQueueJpaEntity.java
│  │  │           │        │  ├─ repository/
│  │  │           │        │  │  ├─ ReporteSolicitudQueueJpaRepository.java
│  │  │           │        │  │  └─ ReporteSolicitudQueueClaimRepository.java  (custom SKIP LOCKED)
│  │  │           │        │  ├─ mapper/
│  │  │           │        │  │  └─ ReporteSolicitudQueueJpaMapper.java         (opcional)
│  │  │           │        │  └─ specification/
│  │  │           │        │     └─ ReporteSolicitudSpecifications.java          (opcional)
│  │  │           │        └─ serialization/
│  │  │           │           └─ ReportePayloadJsonSerializer.java               (opcional)
│  │  │           │
│  │  │           └─ boot/
│  │  │              ├─ dev/
│  │  │              │  ├─ DevDataInitializer.java         (opcional)
│  │  │              │  └─ DevUserInitializer.java         (opcional)
│  │  │              └─ health/
│  │  │                 └─ StartupLogListener.java         (opcional)
│  │  │
│  │  └─ resources/
│  │     ├─ application.properties
│  │     ├─ application-dev.properties
│  │     ├─ application-prod.properties
│  │     ├─ application-test.properties                  (opcional, recomendado)
│  │     ├─ messages.properties
│  │     ├─ messages_es.properties                       (opcional)
│  │     ├─ messages_en.properties                       (opcional, futuro)
│  │     ├─ banner.txt                                   (opcional)
│  │     ├─ db/
│  │     │  ├─ README.md
│  │     │  ├─ seeds/
│  │     │  │  ├─ seed-dev-usuarios.sql                  (opcional)
│  │     │  │  └─ seed-dev-mínimo.sql                    (opcional)
│  │     │  └─ reportes/
│  │     │     └─ ddl-reporte-solicitud-queue.sql        (si lo ejecutas manualmente)
│  │     └─ logback-spring.xml                           (opcional; si personalizas logging pronto)
│  │
│  └─ test/
│     ├─ java/
│     │  └─ com/
│     │     └─ marcosmoreira/
│     │        └─ uensbackend/
│     │           ├─ UensBackendApplicationTests.java
│     │           ├─ common/
│     │           │  ├─ api/
│     │           │  │  └─ ApiResponseSerializationTest.java      (opcional)
│     │           │  └─ exception/
│     │           │     └─ GlobalExceptionHandlerTest.java         (opcional)
│     │           ├─ security/
│     │           │  ├─ jwt/
│     │           │  │  └─ JwtTokenServiceTest.java
│     │           │  └─ api/
│     │           │     └─ AuthSecurityIntegrationTest.java        (opcional)
│     │           ├─ modules/
│     │           │  ├─ auth/
│     │           │  │  └─ api/
│     │           │  │     └─ AuthControllerTest.java
│     │           │  ├─ estudiante/
│     │           │  │  ├─ application/
│     │           │  │  │  └─ EstudianteCommandServiceTest.java
│     │           │  │  └─ api/
│     │           │  │     └─ EstudianteControllerTest.java
│     │           │  ├─ seccion/
│     │           │  │  └─ application/
│     │           │  │     └─ SeccionCommandServiceTest.java
│     │           │  ├─ calificacion/
│     │           │  │  └─ application/
│     │           │  │     └─ CalificacionCommandServiceTest.java
│     │           │  └─ reporte/
│     │           │     ├─ application/
│     │           │     │  ├─ ReporteSolicitudCommandServiceTest.java
│     │           │     │  ├─ ReporteSolicitudWorkerServiceTest.java
│     │           │     │  └─ processor/
│     │           │     │     └─ ListadoEstudiantesPorSeccionProcessorTest.java
│     │           │     └─ infrastructure/
│     │           │        └─ persistence/
│     │           │           └─ ReporteSolicitudQueueClaimRepositoryIT.java  (si pruebas SKIP LOCKED)
│     │           └─ support/
│     │              ├─ TestDataFactory.java
│     │              ├─ IntegrationTestBase.java
│     │              └─ MockJwtFactory.java
│     └─ resources/
│        ├─ application-test.properties
│        ├─ messages.properties                         (si necesitas override de tests)
│        └─ sql/
│           ├─ seed-test-mínimo.sql                    (opcional)
│           └─ cleanup-test.sql                         (opcional)
└─ .vscode/                                            (opcional, local; idealmente ignorado)
   ├─ settings.json
   └─ extensions.json
```

---

## 4. Notas de diseño clave sobre este árbol

### 4.1 Lo más importante: `src/main/java`
Sí, **la carpeta `src` (especialmente `src/main/java`) es lo más importante**. El resto ayuda, pero si `src` está bien diseñado, el proyecto se sostiene mejor.

### 4.2 `common` controlado (no basurero)
Solo dejar en `common` lo verdaderamente transversal:
- contrato API
- excepciones
- paginación/ordenamiento
- i18n
- helpers mínimos

### 4.3 Seguridad separada de `auth`
- `auth` = casos de uso/endpoints de autenticación
- `security` = infraestructura de Spring Security + JWT + handlers

Esto evita mezclar framework con lógica de caso de uso.

### 4.4 Reportes asíncronos listos para crecer
El módulo `reporte` ya viene preparado para:
- DB queue
- worker con `@Scheduled`
- `FOR UPDATE SKIP LOCKED`
- processors por tipo
- payload DTOs para JavaFX

### 4.5 Matrícula como orquestación (sin entidad persistente)
No se propone:
- `MatriculaJpaEntity`
- `MatriculaRepository`

La asignación de sección vigente vive en el caso de uso de `estudiante`.

---

## 5. Versión mínima realista de arranque (si quieres empezar hoy)

Si quieres recortar el árbol para arrancar ya, el **mínimo operativo** sería:

1. `config/`
2. `common/api`, `common/exception`, `common/pagination`
3. `security/`
4. `modules/auth/`
5. `modules/estudiante/`
6. `modules/seccion/`
7. `modules/reporte/` (solo API + application + infra mínima cuando toque)
8. `resources/*.properties + messages.properties`

Y luego expandes `docente`, `asignatura`, `clase`, `calificacion`, `dashboard`.

---

## 6. Qué NO implica este árbol (para evitar malentendidos)

- No obliga a crear **todas** las clases desde el día 1.
- No obliga a implementar `domain/model` desacoplado en todos los módulos si aún estás empezando (puedes ir pragmático con JPA + services, manteniendo la estructura preparada).
- No obliga a tests exhaustivos desde el inicio, pero sí deja espacio para crecer ordenadamente.
- No obliga a migraciones Flyway/Liquibase en esta fase (se difirió a propósito).

---

## 7. Orden sugerido de creación de archivos (práctico)

### Fase A — Base transversal
1. `UensBackendApplication.java`
2. `config/*` esenciales (`MessageSourceConfig`, `OpenApiConfig`, `SchedulingConfig` cuando toque)
3. `common/api/response/*`
4. `common/exception/*`
5. `resources/application*.properties`
6. `resources/messages.properties`

### Fase B — Seguridad + auth
7. `security/config/SecurityConfig.java`
8. `security/jwt/*`
9. `security/handler/*`
10. `modules/auth/*`

### Fase C — Primer módulo funcional (estudiantes + secciones)
11. `modules/seccion/*`
12. `modules/estudiante/*`
13. Caso de uso `AsignarSeccionVigenteRequestDto` + método en servicio

### Fase D — Módulos académicos centrales
14. `asignatura`, `docente`, `clase`, `calificacion`

### Fase E — Dashboard/consultas
15. `dashboard` (y `consulta` si realmente entra en V1)

### Fase F — Reportes asíncronos
16. `modules/reporte/api + domain + persistence`
17. `worker` + claim repository (`SKIP LOCKED`)
18. processors por tipo de reporte

---

## 8. Verificación de obediencia al diseño (checklist rápido)

- [ ] No existe entidad persistente `Matricula`
- [ ] Se usa `.properties` (no YAML)
- [ ] `messages.properties` está presente
- [ ] Contrato API común está en `common`
- [ ] `401/403` JSON manejados por handlers de seguridad
- [ ] Paginación/filtros/ordenamiento centralizados (no duplicados por controller)
- [ ] Módulo `reporte` contempla worker y claim seguro
- [ ] Entidades JPA respetan nombres y nulabilidad del SQL oficial

---

## 9. Resultado esperado

Con este árbol, ya puedes **entrar a implementación** con un marco estable, sin improvisar carpetas/clases sobre la marcha y sin salirte del diseño oficial.

Si en el proceso aparece una necesidad real de rediseño (por ejemplo, matrícula histórica), se documenta como cambio formal y no como parche escondido.



