package com.marcosmoreiradev.uensbackend.common.exception;

import com.marcosmoreiradev.uensbackend.common.api.response.ApiErrorResponse;
import com.marcosmoreiradev.uensbackend.common.api.response.ErrorDetailDto;
import com.marcosmoreiradev.uensbackend.common.constants.RequestAttributes;
import com.marcosmoreiradev.uensbackend.common.exception.base.ApiException;
import com.marcosmoreiradev.uensbackend.common.exception.base.SystemException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ApiErrorCodes;
import com.marcosmoreiradev.uensbackend.common.exception.codes.AuthErrorCodes;
import com.marcosmoreiradev.uensbackend.common.exception.codes.CalificacionErrorCodes;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ClaseErrorCodes;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ErrorCode;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ReporteErrorCodes;
import com.marcosmoreiradev.uensbackend.common.exception.codes.SeccionErrorCodes;
import com.marcosmoreiradev.uensbackend.common.exception.codes.StudentErrorCodes;
import com.marcosmoreiradev.uensbackend.common.exception.mapper.ExceptionToApiErrorMapper;
import com.marcosmoreiradev.uensbackend.common.i18n.MessageKeys;
import com.marcosmoreiradev.uensbackend.common.i18n.MessageResolver;
import com.marcosmoreiradev.uensbackend.common.validation.ValidationErrorAssembler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestControllerAdvice
/**
 * Define la responsabilidad de GlobalExceptionHandler dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ExceptionToApiErrorMapper mapper;
    private final ValidationErrorAssembler validationErrorAssembler;
    private final MessageResolver messageResolver;

    public GlobalExceptionHandler(
            ExceptionToApiErrorMapper mapper,
            ValidationErrorAssembler validationErrorAssembler,
            MessageResolver messageResolver
    ) {
        this.mapper = mapper;
        this.validationErrorAssembler = validationErrorAssembler;
        this.messageResolver = messageResolver;
    }

    @ExceptionHandler(ApiException.class)
/**
 * Implementa la operacion 'handleApiException' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param ex dato de entrada relevante para ejecutar esta operacion: 'ex'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
        if (ex instanceof SystemException) {
            log.error(
                    "ApiException de sistema: code={}, path={}, requestId={}, message={}",
                    ex.getErrorCode().code(),
                    request != null ? request.getRequestURI() : null,
                    request != null ? request.getAttribute(RequestAttributes.REQUEST_ID) : null,
                    ex.getMessage(),
                    ex
            );
        }
        ApiErrorResponse body = mapper.fromApiException(ex, request);
        return ResponseEntity.status(ex.getErrorCode().httpStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
/**
 * Implementa la operacion 'handleBeanValidation' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param ex dato de entrada relevante para ejecutar esta operacion: 'ex'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiErrorResponse> handleBeanValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var details = validationErrorAssembler.fromBindingResult(ex.getBindingResult());
        var code = ApiErrorCodes.VR_01_REQUEST_INVALIDO;
        return buildErrorResponse(code, code.defaultMessage(), details, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
/**
 * Implementa la operacion 'handleConstraintViolation' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param ex dato de entrada relevante para ejecutar esta operacion: 'ex'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        var details = validationErrorAssembler.fromConstraintViolations(ex.getConstraintViolations());
        var code = ApiErrorCodes.VR_01_REQUEST_INVALIDO;
        return buildErrorResponse(code, code.defaultMessage(), details, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
/**
 * Implementa la operacion 'handleJsonInvalid' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param ex dato de entrada relevante para ejecutar esta operacion: 'ex'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiErrorResponse> handleJsonInvalid(HttpMessageNotReadableException ex, HttpServletRequest request) {
        var code = ApiErrorCodes.VR_03_CUERPO_JSON_INVALIDO;
        return buildErrorResponse(code, code.defaultMessage(), null, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
/**
 * Implementa la operacion 'handleTypeMismatch' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param ex dato de entrada relevante para ejecutar esta operacion: 'ex'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        boolean isEnum = ex.getRequiredType() != null && ex.getRequiredType().isEnum();
        var code = isEnum ? ApiErrorCodes.VR_06_VALOR_ENUM_INVALIDO : ApiErrorCodes.VR_02_PARAMETRO_INVALIDO;
        var details = List.of(
                ErrorDetailDto.of(
                        ex.getName(),
                        "TypeMismatch",
                        "Valor invalido para el parametro.",
                        ex.getValue()
                )
        );
        return buildErrorResponse(code, code.defaultMessage(), details, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
/**
 * Implementa la operacion 'handleMissingParam' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param ex dato de entrada relevante para ejecutar esta operacion: 'ex'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiErrorResponse> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        var code = ApiErrorCodes.VR_07_CAMPO_REQUERIDO;
        var details = List.of(
                ErrorDetailDto.of(
                        ex.getParameterName(),
                        "MissingParameter",
                        "Falta el parametro requerido."
                )
        );
        return buildErrorResponse(code, code.defaultMessage(), details, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
/**
 * Implementa la operacion 'handleMethodNotSupported' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param ex dato de entrada relevante para ejecutar esta operacion: 'ex'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        var code = ApiErrorCodes.API_05_METODO_NO_PERMITIDO;
        String supportedMethods = ex.getSupportedHttpMethods() == null
                ? null
                : ex.getSupportedHttpMethods().stream().map(Object::toString).collect(Collectors.joining(","));

        var details = List.of(
                ErrorDetailDto.of(
                        "method",
                        "MethodNotSupported",
                        "Metodo HTTP no permitido.",
                        supportedMethods
                )
        );

        return buildErrorResponse(code, code.defaultMessage(), details, request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
/**
 * Implementa la operacion 'handleMediaTypeNotSupported' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param ex dato de entrada relevante para ejecutar esta operacion: 'ex'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        var code = ApiErrorCodes.API_03_TIPO_CONTENIDO_NO_SOPORTADO;
        String contentType = ex.getContentType() != null ? ex.getContentType().toString() : null;
        String supportedMediaTypes = ex.getSupportedMediaTypes().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        var details = List.of(
                ErrorDetailDto.of(
                        "contentType",
                        "MediaTypeNotSupported",
                        "Tipo de contenido no soportado.",
                        contentType
                ),
                ErrorDetailDto.of(
                        "supportedMediaTypes",
                        "SupportedMediaTypes",
                        "Tipos de contenido soportados.",
                        supportedMediaTypes
                )
        );

        return buildErrorResponse(code, code.defaultMessage(), details, request);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
/**
 * Implementa la operacion 'handleMediaTypeNotAcceptable' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param ex dato de entrada relevante para ejecutar esta operacion: 'ex'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiErrorResponse> handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {
        var code = ApiErrorCodes.API_08_RESPUESTA_NO_ACEPTABLE;
        String supportedMediaTypes = ex.getSupportedMediaTypes().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        var details = List.of(
                ErrorDetailDto.of(
                        "accept",
                        "MediaTypeNotAcceptable",
                        "No se puede generar una respuesta compatible con el encabezado Accept.",
                        supportedMediaTypes
                )
        );

        return buildErrorResponse(code, code.defaultMessage(), details, request);
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
/**
 * Implementa la operacion 'handleRouteNotFound' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param ex dato de entrada relevante para ejecutar esta operacion: 'ex'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiErrorResponse> handleRouteNotFound(Exception ex, HttpServletRequest request) {
        var code = ApiErrorCodes.API_06_RUTA_NO_ENCONTRADA;
        return buildErrorResponse(code, code.defaultMessage(), null, request);
    }

    @ExceptionHandler(ResponseStatusException.class)
/**
 * Implementa la operacion 'handleResponseStatus' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param ex dato de entrada relevante para ejecutar esta operacion: 'ex'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        ErrorCode code = mapResponseStatusToErrorCode(ex.getStatusCode(), ex.getReason());
        String message = hasText(ex.getReason()) ? ex.getReason() : resolveMessage(code);

        if (ex.getStatusCode().value() >= 500) {
            log.error("ResponseStatusException no controlada: status={}, reason={}", ex.getStatusCode().value(), ex.getReason(), ex);
        }

        ApiErrorResponse body = mapper.fromCode(code.code(), message, null, request);
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
/**
 * Implementa la operacion 'handleIllegalArgument' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param ex dato de entrada relevante para ejecutar esta operacion: 'ex'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        var code = ApiErrorCodes.VR_02_PARAMETRO_INVALIDO;
        var details = List.of(
                ErrorDetailDto.of(
                        "argument",
                        "IllegalArgument",
                        "Se detecto un argumento invalido.",
                        ex.getMessage()
                )
        );
        return buildErrorResponse(code, code.defaultMessage(), details, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        var code = AuthErrorCodes.AUTH_10_ACCESO_DENEGADO;
        return buildErrorResponse(code, code.defaultMessage(), null, request);
    }

    @ExceptionHandler(Exception.class)
/**
 * Implementa la operacion 'handleUnexpected' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param ex dato de entrada relevante para ejecutar esta operacion: 'ex'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado: {}", ex.getMessage(), ex);

        var code = ApiErrorCodes.SYS_01_ERROR_INTERNO;
        return buildErrorResponse(code, code.defaultMessage(), null, request);
    }

/**
 * Metodo de soporte interno 'buildErrorResponse' para mantener cohesion en GlobalExceptionHandler.
 * Contexto: modulo core, capa common, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param code dato de entrada relevante para ejecutar esta operacion: 'code'
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @param details dato de entrada relevante para ejecutar esta operacion: 'details'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private ResponseEntity<ApiErrorResponse> buildErrorResponse(ErrorCode code, String message, Object details, HttpServletRequest request) {
        String safeMessage = hasText(message) ? message : resolveMessage(code);
        ApiErrorResponse body = mapper.fromCode(code.code(), safeMessage, details, request);
        return ResponseEntity.status(code.httpStatus()).body(body);
    }

/**
 * Metodo de soporte interno 'resolveMessage' para mantener cohesion en GlobalExceptionHandler.
 * Contexto: modulo core, capa common, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param code dato de entrada relevante para ejecutar esta operacion: 'code'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private String resolveMessage(ErrorCode code) {
        String key = mapMessageKey(code);
        if (key == null) {
            return code.defaultMessage();
        }
        return messageResolver.get(key, code.defaultMessage());
    }

/**
 * Metodo de soporte interno 'mapMessageKey' para mantener cohesion en GlobalExceptionHandler.
 * Contexto: modulo core, capa common, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param code dato de entrada relevante para ejecutar esta operacion: 'code'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String mapMessageKey(ErrorCode code) {
        return switch (code.code()) {
            case "VR-01-REQUEST_INVALIDO" -> MessageKeys.ERROR_VR_01_REQUEST_INVALIDO;
            case "VR-02-PARAMETRO_INVALIDO" -> MessageKeys.ERROR_VR_02_PARAMETRO_INVALIDO;
            case "VR-03-CUERPO_JSON_INVALIDO" -> MessageKeys.ERROR_VR_03_CUERPO_JSON_INVALIDO;
            case "VR-06-VALOR_ENUM_INVALIDO" -> MessageKeys.ERROR_VR_06_VALOR_ENUM_INVALIDO;
            case "VR-07-CAMPO_REQUERIDO" -> MessageKeys.ERROR_VR_07_CAMPO_REQUERIDO;

            case "API-01-REQUEST_MALFORMADO" -> MessageKeys.ERROR_API_01_REQUEST_MALFORMADO;
            case "API-03-TIPO_CONTENIDO_NO_SOPORTADO" -> MessageKeys.ERROR_API_03_TIPO_CONTENIDO_NO_SOPORTADO;
            case "API-04-RECURSO_NO_ENCONTRADO" -> MessageKeys.ERROR_API_04_RECURSO_NO_ENCONTRADO;
            case "API-05-METODO_NO_PERMITIDO" -> MessageKeys.ERROR_API_05_METODO_NO_PERMITIDO;
            case "API-06-RUTA_NO_ENCONTRADA" -> MessageKeys.ERROR_API_06_RUTA_NO_ENCONTRADA;
            case "API-07-CONFLICTO_OPERACION" -> MessageKeys.ERROR_API_07_CONFLICTO_OPERACION;
            case "API-08-RESPUESTA_NO_ACEPTABLE" -> MessageKeys.ERROR_API_08_RESPUESTA_NO_ACEPTABLE;
            case "API-10-ENDPOINT_EN_CONSTRUCCION" -> MessageKeys.ERROR_API_10_ENDPOINT_EN_CONSTRUCCION;

            case "AUTH-01-CREDENCIALES_INVALIDAS" -> MessageKeys.ERROR_AUTH_01_CREDENCIALES_INVALIDAS;
            case "AUTH-02-TOKEN_INVALIDO" -> MessageKeys.ERROR_AUTH_02_TOKEN_INVALIDO;
            case "AUTH-03-TOKEN_EXPIRADO" -> MessageKeys.ERROR_AUTH_03_TOKEN_EXPIRADO;
            case "AUTH-04-SIN_PERMISOS" -> MessageKeys.ERROR_AUTH_04_SIN_PERMISOS;
            case "AUTH-05-USUARIO_INACTIVO" -> MessageKeys.ERROR_AUTH_05_USUARIO_INACTIVO;
            case "AUTH-06-LOGIN_TEMPORALMENTE_BLOQUEADO" -> MessageKeys.ERROR_AUTH_06_LOGIN_TEMPORALMENTE_BLOQUEADO;
            case "AUTH-07-RATE_LIMIT_LOGIN_EXCEDIDO" -> MessageKeys.ERROR_AUTH_07_RATE_LIMIT_LOGIN_EXCEDIDO;
            case "AUTH-08-REFRESH_TOKEN_INVALIDO" -> MessageKeys.ERROR_AUTH_08_REFRESH_TOKEN_INVALIDO;
            case "AUTH-09-REFRESH_TOKEN_EXPIRADO" -> MessageKeys.ERROR_AUTH_09_REFRESH_TOKEN_EXPIRADO;
            case "AUTH-10-ACCESO_DENEGADO" -> MessageKeys.ERROR_AUTH_10_ACCESO_DENEGADO;

            case "SYS-01-ERROR_INTERNO" -> MessageKeys.ERROR_SYS_01_ERROR_INTERNO;
            default -> null;
        };
    }

/**
 * Metodo de soporte interno 'mapStatusCodeToErrorCode' para mantener cohesion en GlobalExceptionHandler.
 * Contexto: modulo core, capa common, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param statusCode dato de entrada relevante para ejecutar esta operacion: 'statusCode'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static ErrorCode mapStatusCodeToErrorCode(HttpStatusCode statusCode) {
        if (statusCode == null) {
            return ApiErrorCodes.SYS_01_ERROR_INTERNO;
        }

        int status = statusCode.value();
        return switch (status) {
            case 400 -> ApiErrorCodes.VR_02_PARAMETRO_INVALIDO;
            case 401 -> AuthErrorCodes.AUTH_02_TOKEN_INVALIDO;
            case 403 -> AuthErrorCodes.AUTH_10_ACCESO_DENEGADO;
            case 404 -> ApiErrorCodes.API_04_RECURSO_NO_ENCONTRADO;
            case 405 -> ApiErrorCodes.API_05_METODO_NO_PERMITIDO;
            case 406 -> ApiErrorCodes.API_08_RESPUESTA_NO_ACEPTABLE;
            case 409 -> ApiErrorCodes.API_07_CONFLICTO_OPERACION;
            case 415 -> ApiErrorCodes.API_03_TIPO_CONTENIDO_NO_SOPORTADO;
            case 501 -> ApiErrorCodes.API_10_ENDPOINT_EN_CONSTRUCCION;
            default -> status >= 500
                    ? ApiErrorCodes.SYS_01_ERROR_INTERNO
                    : ApiErrorCodes.API_01_REQUEST_MALFORMADO;
        };
    }

/**
 * Metodo de soporte interno 'mapResponseStatusToErrorCode' para mantener cohesion en GlobalExceptionHandler.
 * Contexto: modulo core, capa common, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param statusCode dato de entrada relevante para ejecutar esta operacion: 'statusCode'
     * @param reason dato de entrada relevante para ejecutar esta operacion: 'reason'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static ErrorCode mapResponseStatusToErrorCode(HttpStatusCode statusCode, String reason) {
        String normalizedReason = normalize(reason);
        if (normalizedReason != null) {
            switch (normalizedReason) {
                case "ya existe una clase con la misma combinacion operativa." ->
                        { return ClaseErrorCodes.RN_CLA_01_REGISTRO_DUPLICADO; }
                case "la seccion ya tiene una clase en ese horario." ->
                        { return ClaseErrorCodes.RN_CLA_04_SOLAPAMIENTO_HORARIO_SECCION; }
                case "el docente ya tiene una clase en ese horario." ->
                        { return ClaseErrorCodes.RN_CLA_03_SOLAPAMIENTO_HORARIO_DOCENTE; }
                case "la relacion academica seccion-asignatura no es valida." ->
                        { return ClaseErrorCodes.RN_CLA_02_CONTEXTO_ACADEMICO_INVALIDO; }
                case "la seccion asociada no esta disponible." ->
                        { return ClaseErrorCodes.RN_CLA_06_SECCION_NO_DISPONIBLE; }

                case "ya existe una calificacion para ese estudiante, clase y parcial." ->
                        { return CalificacionErrorCodes.RN_CAL_02_REGISTRO_DUPLICADO; }
                case "el contexto academico de la calificacion no es valido." ->
                        { return CalificacionErrorCodes.RN_CAL_01_CONTEXTO_ACADEMICO_INVALIDO; }

                case "ya existe una seccion con esa combinacion." ->
                        { return SeccionErrorCodes.RN_SEC_04_SECCION_DUPLICADA; }
                case "la seccion no esta disponible." ->
                        { return StudentErrorCodes.RN_EST_03_SECCION_NO_DISPONIBLE; }
                case "la seccion no tiene cupo disponible." ->
                        { return StudentErrorCodes.RN_EST_04_CUPO_SECCION_AGOTADO; }
                case "ya existe un estudiante con esos datos." ->
                        { return StudentErrorCodes.RN_EST_01_ESTUDIANTE_DUPLICADO; }

                case "el resultado del reporte aun no esta disponible." ->
                        { return ReporteErrorCodes.RN_REP_02_RESULTADO_NO_LISTO; }
                case "el reintento no esta permitido para la solicitud actual." ->
                        { return ReporteErrorCodes.RN_REP_04_REINTENTO_NO_PERMITIDO; }
                case "el tipo de reporte solicitado aun no esta habilitado en v1." ->
                        { return ReporteErrorCodes.RN_REP_01_TIPO_NO_HABILITADO_V1; }
                default -> {
                }
            }
        }

        return mapStatusCodeToErrorCode(statusCode);
    }

/**
 * Metodo de soporte interno 'hasText' para mantener cohesion en GlobalExceptionHandler.
 * Contexto: modulo core, capa common, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

/**
 * Metodo de soporte interno 'normalize' para mantener cohesion en GlobalExceptionHandler.
 * Contexto: modulo core, capa common, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param value valor de entrada que debe validarse o transformarse en este metodo auxiliar
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String normalize(String value) {
        if (!hasText(value)) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
