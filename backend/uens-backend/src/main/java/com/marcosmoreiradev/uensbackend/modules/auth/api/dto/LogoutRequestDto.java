package com.marcosmoreiradev.uensbackend.modules.auth.api.dto;

/**
 * Solicitud de cierre de sesion. El refresh token es opcional para permitir
 * logout local incluso cuando el cliente ya perdio el estado completo.
 *
 * @param refreshToken refresh token actual que debe revocarse
 */
public record LogoutRequestDto(String refreshToken) {
}
