package com.marcosmoreiradev.uensbackend.common.api.util;

import com.marcosmoreiradev.uensbackend.common.api.response.ApiErrorResponse;
import com.marcosmoreiradev.uensbackend.common.api.response.ApiResponse;
import com.marcosmoreiradev.uensbackend.common.api.response.PageResponseDto;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.stream.Collectors;

public final class ResponseFactory {
/**
 * Construye la instancia de ResponseFactory para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 */

    private ResponseFactory() {}

/**
 * Implementa la operacion 'ok' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @param data dato de entrada relevante para ejecutar esta operacion: 'data'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, null, Instant.now());
    }

/**
 * Implementa la operacion 'ok' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param data dato de entrada relevante para ejecutar esta operacion: 'data'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, null, data, null, Instant.now());
    }

/**
 * Implementa la operacion 'okMessage' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static ApiResponse<Void> okMessage(String message) {
        return new ApiResponse<>(true, message, null, null, Instant.now());
    }

/**
 * Implementa la operacion 'created' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @param data dato de entrada relevante para ejecutar esta operacion: 'data'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(true, message, data, null, Instant.now());
    }

/**
 * Implementa la operacion 'accepted' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @param data dato de entrada relevante para ejecutar esta operacion: 'data'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static <T> ApiResponse<T> accepted(String message, T data) {
        return new ApiResponse<>(true, message, data, null, Instant.now());
    }

/**
 * Implementa la operacion 'page' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @param page indice de pagina solicitado (base cero) para paginacion de resultados
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static <T> ApiResponse<PageResponseDto<T>> page(String message, Page<T> page) {
        PageResponseDto<T> payload = new PageResponseDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumberOfElements(),
                page.isFirst(),
                page.isLast(),
                summarizeSort(page.getSort())
        );
        return new ApiResponse<>(true, message, payload, null, Instant.now());
    }

/**
 * Implementa la operacion 'error' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param errorCode dato de entrada relevante para ejecutar esta operacion: 'errorCode'
     * @param message mensaje tecnico/funcional usado para construir la excepcion correspondiente
     * @param details dato de entrada relevante para ejecutar esta operacion: 'details'
     * @param path dato de entrada relevante para ejecutar esta operacion: 'path'
     * @param requestId dato de entrada relevante para ejecutar esta operacion: 'requestId'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static ApiErrorResponse error(String errorCode, String message, Object details, String path, String requestId) {
        return new ApiErrorResponse(false, errorCode, message, details, path, Instant.now(), requestId);
    }

/**
 * Implementa la operacion 'error' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param errorCode dato de entrada relevante para ejecutar esta operacion: 'errorCode'
     * @param details dato de entrada relevante para ejecutar esta operacion: 'details'
     * @param path dato de entrada relevante para ejecutar esta operacion: 'path'
     * @param requestId dato de entrada relevante para ejecutar esta operacion: 'requestId'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static ApiErrorResponse error(ErrorCode errorCode, Object details, String path, String requestId) {
        return new ApiErrorResponse(
                false,
                errorCode.code(),
                errorCode.defaultMessage(),
                details,
                path,
                Instant.now(),
                requestId
        );
    }

/**
 * Metodo de soporte interno 'summarizeSort' para mantener cohesion en ResponseFactory.
 * Contexto: modulo core, capa common, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param sort criterios de ordenamiento solicitados por cliente segun whitelist permitida
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private static String summarizeSort(Sort sort) {
        if (sort == null || sort.isUnsorted()) return null;
        return sort.stream()
                .map(o -> o.getProperty() + "," + o.getDirection().name().toLowerCase())
                .collect(Collectors.joining(";"));
    }
}
