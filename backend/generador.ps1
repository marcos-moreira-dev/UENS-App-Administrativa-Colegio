# =========================
# 1) CONFIGURA TU CARPETA STAGING
# =========================
$ROOT = "C:\...Sistema UE Niñitos Soñadores\backend\Nueva carpeta"   # <-- cambia esto a donde quieras
$BASE_MAIN = Join-Path $ROOT "src\main\java\com\marcosmoreiradev\uens_backend"
$BASE_TEST = Join-Path $ROOT "src\test\java\com\marcosmoreiradev\uens_backend"

function New-EmptyFile {
  param([string]$fullPath)
  $dir = Split-Path $fullPath
  New-Item -ItemType Directory -Path $dir -Force | Out-Null
  New-Item -ItemType File -Path $fullPath -Force | Out-Null
}

# =========================
# 2) LISTA DE ARCHIVOS .java (MAIN)
# =========================
$filesMain = @(
  "UensBackendApplication.java",

  "config\JacksonConfig.java",
  "config\MessageSourceConfig.java",
  "config\OpenApiConfig.java",
  "config\SchedulingConfig.java",
  "config\properties\JwtProperties.java",
  "config\properties\ReportQueueProperties.java",

  "common\api\response\ApiResponse.java",
  "common\api\response\ApiErrorResponse.java",
  "common\api\response\ErrorDetailDto.java",
  "common\api\response\PageResponseDto.java",
  "common\api\util\ResponseFactory.java",

  "common\exception\GlobalExceptionHandler.java",
  "common\exception\base\ApplicationException.java",
  "common\exception\base\ValidationException.java",
  "common\exception\base\BusinessRuleException.java",
  "common\exception\base\ApiException.java",
  "common\exception\base\AuthException.java",
  "common\exception\base\SystemException.java",

  "common\exception\codes\ErrorCode.java",
  "common\exception\codes\AuthErrorCodes.java",
  "common\exception\codes\ApiErrorCodes.java",
  "common\exception\codes\StudentErrorCodes.java",
  "common\exception\codes\SeccionErrorCodes.java",
  "common\exception\codes\ClaseErrorCodes.java",
  "common\exception\codes\CalificacionErrorCodes.java",
  "common\exception\codes\ReporteErrorCodes.java",
  "common\exception\mapper\ExceptionToApiErrorMapper.java",

  "common\i18n\MessageKeys.java",
  "common\i18n\MessageResolver.java",

  "common\pagination\PageMapper.java",
  "common\pagination\PageSortParser.java",
  "common\pagination\SortWhitelistValidator.java",
  "common\pagination\PageableFactory.java",

  "common\validation\ValidationErrorAssembler.java",
  "common\constants\SecurityRoles.java",

  "security\config\SecurityConfig.java",
  "security\jwt\JwtTokenService.java",
  "security\jwt\JwtAuthenticationFilter.java",
  "security\user\CustomUserDetails.java",
  "security\user\CustomUserDetailsService.java",
  "security\handler\RestAuthenticationEntryPoint.java",
  "security\handler\RestAccessDeniedHandler.java",

  "modules\auth\api\AuthController.java",
  "modules\auth\api\dto\LoginRequestDto.java",
  "modules\auth\api\dto\LoginResponseDto.java",
  "modules\auth\api\dto\AuthUsuarioResumenDto.java",
  "modules\auth\application\AuthApplicationService.java",
  "modules\auth\application\mapper\AuthDtoMapper.java",
  "modules\auth\infrastructure\persistence\entity\UsuarioSistemaAdministrativoJpaEntity.java",
  "modules\auth\infrastructure\persistence\repository\UsuarioSistemaAdministrativoJpaRepository.java",

  "modules\estudiante\api\EstudianteController.java",
  "modules\estudiante\api\dto\EstudianteCreateRequestDto.java",
  "modules\estudiante\api\dto\EstudianteUpdateRequestDto.java",
  "modules\estudiante\api\dto\EstudiantePatchEstadoRequestDto.java",
  "modules\estudiante\api\dto\AsignarSeccionVigenteRequestDto.java",
  "modules\estudiante\api\dto\EstudianteResponseDto.java",
  "modules\estudiante\api\dto\EstudianteListItemDto.java",
  "modules\estudiante\application\EstudianteCommandService.java",
  "modules\estudiante\application\EstudianteQueryService.java",
  "modules\estudiante\application\validator\EstudianteRequestValidator.java",
  "modules\estudiante\application\validator\EstudianteFilterValidator.java",
  "modules\estudiante\application\mapper\EstudianteDtoMapper.java",
  "modules\estudiante\infrastructure\persistence\entity\EstudianteJpaEntity.java",
  "modules\estudiante\infrastructure\persistence\repository\EstudianteJpaRepository.java",
  "modules\estudiante\infrastructure\persistence\repository\EstudianteQueryRepository.java",

  "modules\representante\api\RepresentanteLegalController.java",
  "modules\representante\api\dto\RepresentanteLegalCreateRequestDto.java",
  "modules\representante\api\dto\RepresentanteLegalUpdateRequestDto.java",
  "modules\representante\api\dto\RepresentanteLegalResponseDto.java",
  "modules\representante\api\dto\RepresentanteLegalListItemDto.java",
  "modules\representante\application\RepresentanteLegalCommandService.java",
  "modules\representante\application\RepresentanteLegalQueryService.java",
  "modules\representante\application\mapper\RepresentanteLegalDtoMapper.java",
  "modules\representante\infrastructure\persistence\entity\RepresentanteLegalJpaEntity.java",
  "modules\representante\infrastructure\persistence\repository\RepresentanteLegalJpaRepository.java",

  "modules\seccion\api\SeccionController.java",
  "modules\seccion\api\dto\SeccionCreateRequestDto.java",
  "modules\seccion\api\dto\SeccionUpdateRequestDto.java",
  "modules\seccion\api\dto\SeccionPatchEstadoRequestDto.java",
  "modules\seccion\api\dto\SeccionResponseDto.java",
  "modules\seccion\api\dto\SeccionListItemDto.java",
  "modules\seccion\application\SeccionCommandService.java",
  "modules\seccion\application\SeccionQueryService.java",
  "modules\seccion\application\validator\SeccionRequestValidator.java",
  "modules\seccion\application\validator\SeccionFilterValidator.java",
  "modules\seccion\application\mapper\SeccionDtoMapper.java",
  "modules\seccion\infrastructure\persistence\entity\SeccionJpaEntity.java",
  "modules\seccion\infrastructure\persistence\repository\SeccionJpaRepository.java",

  "modules\docente\api\DocenteController.java",
  "modules\docente\api\dto\DocenteCreateRequestDto.java",
  "modules\docente\api\dto\DocenteUpdateRequestDto.java",
  "modules\docente\api\dto\DocentePatchEstadoRequestDto.java",
  "modules\docente\api\dto\DocenteResponseDto.java",
  "modules\docente\api\dto\DocenteListItemDto.java",
  "modules\docente\application\DocenteCommandService.java",
  "modules\docente\application\DocenteQueryService.java",
  "modules\docente\application\mapper\DocenteDtoMapper.java",
  "modules\docente\infrastructure\persistence\entity\DocenteJpaEntity.java",
  "modules\docente\infrastructure\persistence\repository\DocenteJpaRepository.java",

  "modules\asignatura\api\AsignaturaController.java",
  "modules\asignatura\api\dto\AsignaturaCreateRequestDto.java",
  "modules\asignatura\api\dto\AsignaturaUpdateRequestDto.java",
  "modules\asignatura\api\dto\AsignaturaPatchEstadoRequestDto.java",
  "modules\asignatura\api\dto\AsignaturaResponseDto.java",
  "modules\asignatura\api\dto\AsignaturaListItemDto.java",
  "modules\asignatura\application\AsignaturaCommandService.java",
  "modules\asignatura\application\AsignaturaQueryService.java",
  "modules\asignatura\application\mapper\AsignaturaDtoMapper.java",
  "modules\asignatura\infrastructure\persistence\entity\AsignaturaJpaEntity.java",
  "modules\asignatura\infrastructure\persistence\repository\AsignaturaJpaRepository.java",

  "modules\clase\api\ClaseController.java",
  "modules\clase\api\dto\ClaseCreateRequestDto.java",
  "modules\clase\api\dto\ClaseUpdateRequestDto.java",
  "modules\clase\api\dto\ClasePatchEstadoRequestDto.java",
  "modules\clase\api\dto\ClaseResponseDto.java",
  "modules\clase\api\dto\ClaseListItemDto.java",
  "modules\clase\application\ClaseCommandService.java",
  "modules\clase\application\ClaseQueryService.java",
  "modules\clase\application\validator\ClaseRequestValidator.java",
  "modules\clase\application\mapper\ClaseDtoMapper.java",
  "modules\clase\infrastructure\persistence\entity\ClaseJpaEntity.java",
  "modules\clase\infrastructure\persistence\repository\ClaseJpaRepository.java",
  "modules\clase\infrastructure\persistence\repository\ClaseQueryRepository.java",

  "modules\calificacion\api\CalificacionController.java",
  "modules\calificacion\api\dto\CalificacionCreateRequestDto.java",
  "modules\calificacion\api\dto\CalificacionUpdateRequestDto.java",
  "modules\calificacion\api\dto\CalificacionResponseDto.java",
  "modules\calificacion\api\dto\CalificacionListItemDto.java",
  "modules\calificacion\application\CalificacionCommandService.java",
  "modules\calificacion\application\CalificacionQueryService.java",
  "modules\calificacion\application\validator\CalificacionRequestValidator.java",
  "modules\calificacion\application\mapper\CalificacionDtoMapper.java",
  "modules\calificacion\infrastructure\persistence\entity\CalificacionJpaEntity.java",
  "modules\calificacion\infrastructure\persistence\repository\CalificacionJpaRepository.java",

  "modules\dashboard\api\DashboardController.java",
  "modules\dashboard\api\dto\DashboardResumenResponseDto.java",
  "modules\dashboard\application\DashboardQueryService.java",
  "modules\dashboard\application\mapper\DashboardDtoMapper.java",
  "modules\dashboard\infrastructure\query\DashboardQueryRepository.java",

  "modules\reporte\api\ReporteSolicitudController.java",
  "modules\reporte\api\dto\CrearReporteSolicitudRequestDto.java",
  "modules\reporte\api\dto\ReporteSolicitudCreadaResponseDto.java",
  "modules\reporte\api\dto\ReporteSolicitudDetalleResponseDto.java",
  "modules\reporte\api\dto\ReporteSolicitudResultadoResponseDto.java",
  "modules\reporte\api\dto\ReporteSolicitudListItemDto.java",
  "modules\reporte\application\ReporteSolicitudCommandService.java",
  "modules\reporte\application\ReporteSolicitudQueryService.java",
  "modules\reporte\application\ReporteSolicitudWorkerService.java",
  "modules\reporte\application\worker\ReporteSolicitudWorkerScheduler.java",
  "modules\reporte\application\processor\ReporteDataProcessor.java",
  "modules\reporte\application\processor\ReporteDataProcessorSelector.java",
  "modules\reporte\application\processor\ListadoEstudiantesPorSeccionProcessor.java",
  "modules\reporte\application\mapper\ReporteSolicitudDtoMapper.java",
  "modules\reporte\application\mapper\ReportePayloadDtoMapper.java",
  "modules\reporte\application\validator\CrearReporteSolicitudRequestValidator.java",
  "modules\reporte\infrastructure\persistence\entity\ReporteSolicitudQueueJpaEntity.java",
  "modules\reporte\infrastructure\persistence\repository\ReporteSolicitudQueueJpaRepository.java",
  "modules\reporte\infrastructure\persistence\repository\ReporteSolicitudQueueClaimRepository.java",

  "boot\dev\DevDataInitializer.java"
)

# =========================
# 3) LISTA DE ARCHIVOS .java (TEST)
# =========================
$filesTest = @(
  "UensBackendApplicationTests.java",
  "security\jwt\JwtTokenServiceTest.java",
  "modules\auth\api\AuthControllerTest.java",
  "modules\estudiante\application\EstudianteCommandServiceTest.java",
  "modules\estudiante\api\EstudianteControllerTest.java",
  "modules\reporte\application\ReporteSolicitudCommandServiceTest.java",
  "modules\reporte\application\ReporteSolicitudWorkerServiceTest.java",
  "support\TestDataFactory.java",
  "support\IntegrationTestBase.java",
  "support\MockJwtFactory.java"
)

# =========================
# 4) CREAR ARCHIVOS
# =========================
New-Item -ItemType Directory -Path $ROOT -Force | Out-Null

foreach ($f in $filesMain) {
  New-EmptyFile (Join-Path $BASE_MAIN $f)
}

foreach ($f in $filesTest) {
  New-EmptyFile (Join-Path $BASE_TEST $f)
}

Write-Host "Listo. Archivos .java vacíos creados en: $ROOT"

# Opcional: ver el árbol
# cmd /c "tree `"$ROOT\src`" /f"