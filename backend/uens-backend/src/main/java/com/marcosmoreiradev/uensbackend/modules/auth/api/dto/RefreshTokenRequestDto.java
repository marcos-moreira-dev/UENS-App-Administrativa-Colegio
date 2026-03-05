package com.marcosmoreiradev.uensbackend.modules.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Solicitud para renovar un access token a partir de un refresh token vigente.
 *
 * @param refreshToken token opaco emitido por el backend para rotacion de sesion
 */
public record RefreshTokenRequestDto(@NotBlank String refreshToken) {
}
