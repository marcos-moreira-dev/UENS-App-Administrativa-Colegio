package com.marcosmoreiradev.uensbackend.modules.auth.application.port;

import com.marcosmoreiradev.uensbackend.modules.auth.application.model.RefreshTokenSession;

import java.time.Instant;
import java.util.Optional;

/**
 * Puerto de persistencia para refresh tokens. Permite iniciar con memoria
 * local y migrar despues a Redis, BD o cualquier backend especializado.
 */
public interface RefreshTokenStore {

    /**
     * Guarda o reemplaza el estado de un refresh token.
     *
     * @param session sesion de refresh token a persistir
     */
    void save(RefreshTokenSession session);

    /**
     * Busca una sesion por el hash de token recibido.
     *
     * @param tokenHash hash irreversible del refresh token
     * @return sesion encontrada o vacio
     */
    Optional<RefreshTokenSession> findByTokenHash(String tokenHash);

    /**
     * Revoca una sesion existente si el hash corresponde a un token activo.
     *
     * @param tokenHash hash irreversible del refresh token
     */
    void revoke(String tokenHash);

    /**
     * Elimina o revoca entradas vencidas para contener crecimiento del store.
     *
     * @param now instante de referencia para expiracion
     */
    void purgeExpired(Instant now);
}
