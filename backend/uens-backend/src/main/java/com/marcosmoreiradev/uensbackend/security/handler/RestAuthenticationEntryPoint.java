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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
/**
 * Define la responsabilidad de RestAuthenticationEntryPoint dentro del backend UENS.
 * Contexto: modulo core, capa security, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final String ATTR_AUTH_ERROR_CODE = RequestAttributes.AUTH_ERROR_CODE;

    private final ObjectMapper objectMapper;
    private final MessageResolver messageResolver;
/**
 * Construye la instancia de RestAuthenticationEntryPoint para operar en el modulo core.
 * Contexto: capa security con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param objectMapper serializador JSON usado para claims, payloads o campos JSONB
     * @param messageResolver dato de entrada relevante para ejecutar esta operacion: 'messageResolver'
 */

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper, MessageResolver messageResolver) {
        this.objectMapper = objectMapper;
        this.messageResolver = messageResolver;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        String codeFromAttr = (String) request.getAttribute(RequestAttributes.AUTH_ERROR_CODE);
        AuthErrorCodes resolvedCode = AuthErrorCodes.fromCode(codeFromAttr);
        String message = resolveMessage(resolvedCode);

        var body = ApiErrorResponse.of(
                resolvedCode.code(),
                message,
                null,
                request.getRequestURI(),
                resolveRequestId(request)
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }

/**
 * Metodo de soporte interno 'resolveRequestId' para mantener cohesion en RestAuthenticationEntryPoint.
 * Contexto: modulo core, capa security, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param request payload de entrada validado desde la API con datos del caso de uso
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private String resolveRequestId(HttpServletRequest request) {
        Object attr = request.getAttribute(RequestAttributes.REQUEST_ID);
        if (attr != null) return String.valueOf(attr);
        return request.getHeader(ApiHeaders.REQUEST_ID);
    }

/**
 * Metodo de soporte interno 'resolveMessage' para mantener cohesion en RestAuthenticationEntryPoint.
 * Contexto: modulo core, capa security, con foco en reglas y consistencia tecnica de fase 1.
 *
     * @param code dato de entrada relevante para ejecutar esta operacion: 'code'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    private String resolveMessage(AuthErrorCodes code) {
        String key = switch (code) {
            case AUTH_01_CREDENCIALES_INVALIDAS -> MessageKeys.ERROR_AUTH_01_CREDENCIALES_INVALIDAS;
            case AUTH_02_TOKEN_INVALIDO -> MessageKeys.ERROR_AUTH_02_TOKEN_INVALIDO;
            case AUTH_03_TOKEN_EXPIRADO -> MessageKeys.ERROR_AUTH_03_TOKEN_EXPIRADO;
            case AUTH_04_SIN_PERMISOS -> MessageKeys.ERROR_AUTH_04_SIN_PERMISOS;
            case AUTH_05_USUARIO_INACTIVO -> MessageKeys.ERROR_AUTH_05_USUARIO_INACTIVO;
            case AUTH_06_LOGIN_TEMPORALMENTE_BLOQUEADO -> MessageKeys.ERROR_AUTH_06_LOGIN_TEMPORALMENTE_BLOQUEADO;
            case AUTH_07_RATE_LIMIT_LOGIN_EXCEDIDO -> MessageKeys.ERROR_AUTH_07_RATE_LIMIT_LOGIN_EXCEDIDO;
            case AUTH_08_REFRESH_TOKEN_INVALIDO -> MessageKeys.ERROR_AUTH_08_REFRESH_TOKEN_INVALIDO;
            case AUTH_09_REFRESH_TOKEN_EXPIRADO -> MessageKeys.ERROR_AUTH_09_REFRESH_TOKEN_EXPIRADO;
            case AUTH_10_ACCESO_DENEGADO -> MessageKeys.ERROR_AUTH_10_ACCESO_DENEGADO;
        };
        return messageResolver.get(key, code.defaultMessage());
    }
}
