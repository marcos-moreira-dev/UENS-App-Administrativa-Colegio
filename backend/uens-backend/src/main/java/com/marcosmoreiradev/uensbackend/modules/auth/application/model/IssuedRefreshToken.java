package com.marcosmoreiradev.uensbackend.modules.auth.application.model;

import java.time.Instant;

/**
 * Resultado de emision o rotacion de refresh token listo para devolverse al
 * cliente y actualizar el estado de sesion.
 *
 * @param subject usuario asociado al token
 * @param rawToken valor opaco que solo conoce el cliente
 * @param expiresAt instante absoluto de expiracion
 * @param expiresInSeconds tiempo restante expresado para contratos HTTP
 */
public record IssuedRefreshToken(
        RefreshTokenSubject subject,
        String rawToken,
        Instant expiresAt,
        long expiresInSeconds
) {
}
