package com.marcosmoreiradev.uensbackend.modules.auth.application.mapper;

import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.AuthUsuarioResumenDto;
import com.marcosmoreiradev.uensbackend.modules.auth.api.dto.LoginResponseDto;
import org.springframework.stereotype.Component;

@Component

/**
 * Define la responsabilidad de AuthDtoMapper dentro del backend UENS.
 * Contexto: modulo auth, capa application, arquitectura monolito modular Spring Boot.
 * Alcance: traducir estructuras entre DTOs API, modelos de aplicacion y entidades persistentes.
 */
public class AuthDtoMapper {

    public static final String TOKEN_TYPE_BEARER = "Bearer";

/**
 * Implementa la operacion 'toUsuarioResumenDto' del modulo auth en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public AuthUsuarioResumenDto toUsuarioResumenDto(Long id, String login, String rol, String estado) {
        return new AuthUsuarioResumenDto(id, login, rol, estado);
    }

/**
 * Implementa la operacion 'toLoginResponseDto' del modulo auth en la capa application.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 */
    public LoginResponseDto toLoginResponseDto(
            String accessToken,
            String refreshToken,
            Long expiresInSeconds,
            Long refreshExpiresInSeconds,
            Long userId,
            String login,
            String rol,
            String estado
    ) {
        AuthUsuarioResumenDto usuario = toUsuarioResumenDto(userId, login, rol, estado);
        return new LoginResponseDto(
                accessToken,
                refreshToken,
                TOKEN_TYPE_BEARER,
                expiresInSeconds,
                refreshExpiresInSeconds,
                usuario
        );
    }
}
