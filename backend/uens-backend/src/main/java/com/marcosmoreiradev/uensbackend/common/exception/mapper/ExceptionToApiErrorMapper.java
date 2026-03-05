package com.marcosmoreiradev.uensbackend.common.exception.mapper;

import com.marcosmoreiradev.uensbackend.common.api.response.ApiErrorResponse;
import com.marcosmoreiradev.uensbackend.common.constants.ApiHeaders;
import com.marcosmoreiradev.uensbackend.common.constants.RequestAttributes;
import com.marcosmoreiradev.uensbackend.common.exception.base.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
/**
 * Define la responsabilidad de ExceptionToApiErrorMapper dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: traducir estructuras entre DTOs API, modelos de aplicacion y entidades persistentes.
 */
public class ExceptionToApiErrorMapper {

/**
 * Implementa la operacion 'fromApiException' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param ex dato de entrada relevante para ejecutar esta operacion: 'ex'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ApiErrorResponse fromApiException(ApiException ex, HttpServletRequest request) {
        var code = ex.getErrorCode();
        return ApiErrorResponse.of(
                code.code(),
                ex.getMessage(),
                ex.getDetails(),
                safePath(request),
                resolveRequestId(request)
        );
    }

/**
 * Implementa la operacion 'fromCode' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param code dato de entrada relevante para ejecutar esta operacion: 'code'
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @param details dato de entrada relevante para ejecutar esta operacion: 'details'
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ApiErrorResponse fromCode(String code, String message, Object details, HttpServletRequest request) {
        return ApiErrorResponse.of(code, message, details, safePath(request), resolveRequestId(request));
    }

/**
 * Metodo de soporte interno 'safePath' para mantener cohesion en ExceptionToApiErrorMapper.
 * Contexto: modulo core, capa common, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private String safePath(HttpServletRequest request) {
        return request != null ? request.getRequestURI() : null;
    }

/**
 * Metodo de soporte interno 'resolveRequestId' para mantener cohesion en ExceptionToApiErrorMapper.
 * Contexto: modulo core, capa common, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private String resolveRequestId(HttpServletRequest request) {
        if (request == null) return null;
        Object attr = request.getAttribute(RequestAttributes.REQUEST_ID);
        return attr != null ? String.valueOf(attr) : request.getHeader(ApiHeaders.REQUEST_ID);
    }
}

