package com.marcosmoreiradev.uensbackend.modules.auth.application.model;

import java.time.Instant;

/**
 * Estado persistible de un refresh token dentro del store seleccionado.
 *
 * @param tokenHash hash irreversible del token opaco
 * @param subject identidad asociada al token
 * @param issuedAt fecha de emision
 * @param expiresAt fecha de expiracion absoluta
 * @param revoked indicador de revocacion explicita o por rotacion
 */
public record RefreshTokenSession(
        String tokenHash,
        RefreshTokenSubject subject,
        Instant issuedAt,
        Instant expiresAt,
        boolean revoked
) {

    /**
     * Marca la sesion como revocada manteniendo el resto de metadata para
     * trazabilidad del store.
     *
     * @return copia revocada del token
     */
    public RefreshTokenSession revoke() {
        return new RefreshTokenSession(tokenHash, subject, issuedAt, expiresAt, true);
    }
}
