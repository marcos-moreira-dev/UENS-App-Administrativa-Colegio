package com.marcosmoreiradev.uensdesktop.api.modules.auth.dto;

import com.marcosmoreiradev.uensdesktop.session.UsuarioSession;

/**
 * Successful authentication payload returned by the backend.
 *
 * @param accessToken bearer token to attach to subsequent authenticated requests
 * @param refreshToken refresh token used by the desktop to renew the bearer token
 * @param tokenType token scheme reported by the backend
 * @param expiresInSeconds token lifetime expressed in seconds
 * @param refreshExpiresInSeconds refresh token lifetime expressed in seconds
 * @param usuario authenticated user snapshot associated with the token
 */
public record LoginResponseDto(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        long refreshExpiresInSeconds,
        UsuarioSession usuario
) {
}
