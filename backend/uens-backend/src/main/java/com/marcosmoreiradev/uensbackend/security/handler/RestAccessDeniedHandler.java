package com.marcosmoreiradev.uensbackend.security.handler;

import com.marcosmoreiradev.uensbackend.common.api.response.ApiErrorResponse;
import com.marcosmoreiradev.uensbackend.common.constants.ApiHeaders;
import com.marcosmoreiradev.uensbackend.common.constants.RequestAttributes;
import com.marcosmoreiradev.uensbackend.common.exception.codes.AuthErrorCodes;
import com.marcosmoreiradev.uensbackend.common.i18n.MessageKeys;
import com.marcosmoreiradev.uensbackend.common.i18n.MessageResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
/**
 * Define la responsabilidad de RestAccessDeniedHandler dentro del backend UENS.
 * Contexto: modulo core, capa security, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final MessageResolver messageResolver;
/**
 * Construye la instancia de RestAccessDeniedHandler para operar en el modulo core.
 * Contexto: capa security con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param objectMapper serializador JSON usado para claims, payloads o campos JSONB
     * @param messageResolver dato de entrada relevante para ejecutar esta operacion: 'messageResolver'
 */

    public RestAccessDeniedHandler(ObjectMapper objectMapper, MessageResolver messageResolver) {
        this.objectMapper = objectMapper;
        this.messageResolver = messageResolver;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        var body = ApiErrorResponse.of(
                AuthErrorCodes.AUTH_10_ACCESO_DENEGADO.code(),
                messageResolver.get(MessageKeys.ERROR_AUTH_10_ACCESO_DENEGADO, AuthErrorCodes.AUTH_10_ACCESO_DENEGADO.defaultMessage()),
                null,
                request.getRequestURI(),
                resolveRequestId(request)
        );

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }

/**
 * Metodo de soporte interno 'resolveRequestId' para mantener cohesion en RestAccessDeniedHandler.
 * Contexto: modulo core, capa security, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private String resolveRequestId(HttpServletRequest request) {
        Object attr = request.getAttribute(RequestAttributes.REQUEST_ID);
        if (attr != null) {
            return String.valueOf(attr);
        }
        return request.getHeader(ApiHeaders.REQUEST_ID);
    }
}
