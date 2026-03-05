package com.marcosmoreiradev.uensbackend.modules.auth.application.support;

import com.marcosmoreiradev.uensbackend.common.exception.base.AuthException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.AuthErrorCodes;
import com.marcosmoreiradev.uensbackend.config.properties.RefreshTokenProperties;
import com.marcosmoreiradev.uensbackend.modules.auth.application.model.IssuedRefreshToken;
import com.marcosmoreiradev.uensbackend.modules.auth.application.model.RefreshTokenSession;
import com.marcosmoreiradev.uensbackend.modules.auth.application.model.RefreshTokenSubject;
import com.marcosmoreiradev.uensbackend.modules.auth.application.port.RefreshTokenStore;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;

/**
 * Gestiona la emision, validacion, rotacion y revocacion de refresh tokens.
 *
 * La persistencia se abstrae via puerto para que la logica de aplicacion no
 * dependa del almacenamiento concreto.
 */
@Service
public class RefreshTokenService {

    private final RefreshTokenProperties properties;
    private final RefreshTokenStore refreshTokenStore;
    private final Clock clock;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenProperties properties,
            RefreshTokenStore refreshTokenStore,
            Clock clock
    ) {
        this.properties = properties;
        this.refreshTokenStore = refreshTokenStore;
        this.clock = clock;
    }

    /**
     * Emite un refresh token nuevo para el usuario indicado.
     *
     * @param subject usuario autenticado asociado al token
     * @return token emitido listo para devolverse al cliente
     */
    public IssuedRefreshToken issue(RefreshTokenSubject subject) {
        if (!properties.enabled()) {
            throw new AuthException(
                    AuthErrorCodes.AUTH_08_REFRESH_TOKEN_INVALIDO,
                    "La renovacion de sesion no esta habilitada."
            );
        }

        Instant now = clock.instant();
        refreshTokenStore.purgeExpired(now);

        String rawToken = generateOpaqueToken();
        String tokenHash = hash(rawToken);
        Instant expiresAt = now.plusSeconds(properties.expirationSeconds());

        refreshTokenStore.save(new RefreshTokenSession(tokenHash, subject, now, expiresAt, false));
        return new IssuedRefreshToken(subject, rawToken, expiresAt, properties.expirationSeconds());
    }

    /**
     * Rota un refresh token existente y revoca el anterior.
     *
     * @param rawToken token opaco recibido desde el cliente
     * @return nuevo refresh token emitido para la misma identidad
     */
    public IssuedRefreshToken rotate(String rawToken) {
        RefreshTokenSession session = resolveActiveSession(rawToken);
        refreshTokenStore.revoke(session.tokenHash());
        return issue(session.subject());
    }

    /**
     * Revoca el refresh token indicado si sigue vigente en el store.
     *
     * @param rawToken token opaco recibido desde el cliente
     */
    public void revoke(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }
        refreshTokenStore.purgeExpired(clock.instant());
        refreshTokenStore.revoke(hash(rawToken));
    }

    private RefreshTokenSession resolveActiveSession(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new AuthException(
                    AuthErrorCodes.AUTH_08_REFRESH_TOKEN_INVALIDO,
                    AuthErrorCodes.AUTH_08_REFRESH_TOKEN_INVALIDO.defaultMessage()
            );
        }

        Instant now = clock.instant();
        RefreshTokenSession session = refreshTokenStore.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new AuthException(
                        AuthErrorCodes.AUTH_08_REFRESH_TOKEN_INVALIDO,
                        AuthErrorCodes.AUTH_08_REFRESH_TOKEN_INVALIDO.defaultMessage()
                ));

        if (session.revoked()) {
            throw new AuthException(
                    AuthErrorCodes.AUTH_08_REFRESH_TOKEN_INVALIDO,
                    AuthErrorCodes.AUTH_08_REFRESH_TOKEN_INVALIDO.defaultMessage()
            );
        }
        if (!now.isBefore(session.expiresAt())) {
            refreshTokenStore.revoke(session.tokenHash());
            refreshTokenStore.purgeExpired(now);
            throw new AuthException(
                    AuthErrorCodes.AUTH_09_REFRESH_TOKEN_EXPIRADO,
                    AuthErrorCodes.AUTH_09_REFRESH_TOKEN_EXPIRADO.defaultMessage()
            );
        }
        refreshTokenStore.purgeExpired(now);
        return session;
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[properties.tokenBytesLength()];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(hash.length * 2);
            for (byte value : hash) {
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("No fue posible calcular hash de refresh token.", ex);
        }
    }
}
