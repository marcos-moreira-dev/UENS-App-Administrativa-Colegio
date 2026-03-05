package com.marcosmoreiradev.uensdesktop.session;

import java.util.Optional;
import java.time.Duration;
import java.time.Instant;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Maintains the authenticated desktop session in JavaFX-observable form.
 *
 * <p>The class centralizes the access token and the logged-in administrative user so navigation,
 * permissions and API calls react to login/logout without duplicating state across controllers.
 */
public final class SessionState {

    private final StringProperty accessToken = new SimpleStringProperty();
    private final StringProperty refreshToken = new SimpleStringProperty();
    private final ObjectProperty<UsuarioSession> usuario = new SimpleObjectProperty<>();
    private final ObjectProperty<Instant> accessTokenExpiresAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Instant> refreshTokenExpiresAt = new SimpleObjectProperty<>();

    /**
     * Returns the current bearer token when the user is authenticated.
     *
     * @return an optional with the current token, or empty when no session is active
     */
    public Optional<String> token() {
        return Optional.ofNullable(accessToken.get());
    }

    /**
     * Returns the administrative user currently associated with the session.
     *
     * @return an optional with the logged-in user, or empty when the desktop is logged out
     */
    public Optional<UsuarioSession> usuario() {
        return Optional.ofNullable(usuario.get());
    }

    /**
     * Returns the current refresh token when the session supports silent renewal.
     *
     * @return an optional with the current refresh token
     */
    public Optional<String> refreshToken() {
        return Optional.ofNullable(refreshToken.get());
    }

    /**
     * Returns the absolute expiration instant of the current access token.
     *
     * @return expiration instant or empty when unknown
     */
    public Optional<Instant> accessTokenExpiresAt() {
        return Optional.ofNullable(accessTokenExpiresAt.get());
    }

    /**
     * Returns the absolute expiration instant of the current refresh token.
     *
     * @return expiration instant or empty when unknown
     */
    public Optional<Instant> refreshTokenExpiresAt() {
        return Optional.ofNullable(refreshTokenExpiresAt.get());
    }

    /**
     * Resolves the active role from the current user snapshot.
     *
     * @return an optional containing the effective role for permission checks
     */
    public Optional<Role> role() {
        return usuario().map(UsuarioSession::rol);
    }

    /**
     * Replaces the in-memory session with a successful authentication result.
     *
     * @param token bearer token returned by the backend
     * @param usuarioSession authenticated user metadata used by the UI
     */
    public void login(String token, UsuarioSession usuarioSession) {
        login(token, null, null, null, usuarioSession);
    }

    /**
     * Replaces the in-memory session with a token pair and expiration metadata.
     *
     * @param token current bearer access token
     * @param accessExpiresAt absolute access token expiration instant
     * @param refreshTokenValue refresh token used for silent renewal
     * @param refreshExpiresAt absolute refresh token expiration instant
     * @param usuarioSession authenticated user metadata used by the UI
     */
    public void login(
            String token,
            Instant accessExpiresAt,
            String refreshTokenValue,
            Instant refreshExpiresAt,
            UsuarioSession usuarioSession
    ) {
        accessToken.set(token);
        refreshToken.set(refreshTokenValue);
        accessTokenExpiresAt.set(accessExpiresAt);
        refreshTokenExpiresAt.set(refreshExpiresAt);
        usuario.set(usuarioSession);
    }

    /**
     * Updates the token pair while preserving the current user snapshot.
     *
     * @param token refreshed access token
     * @param accessExpiresAt new access expiration
     * @param refreshTokenValue rotated refresh token
     * @param refreshExpiresAt new refresh expiration
     */
    public void updateTokens(
            String token,
            Instant accessExpiresAt,
            String refreshTokenValue,
            Instant refreshExpiresAt
    ) {
        accessToken.set(token);
        refreshToken.set(refreshTokenValue);
        accessTokenExpiresAt.set(accessExpiresAt);
        refreshTokenExpiresAt.set(refreshExpiresAt);
    }

    /**
     * Replaces only the user snapshot while keeping the active token pair.
     *
     * @param usuarioSession refreshed authenticated user metadata
     */
    public void updateUsuario(UsuarioSession usuarioSession) {
        usuario.set(usuarioSession);
    }

    /**
     * Indicates whether the access token is near expiration according to the
     * provided safety margin.
     *
     * @param threshold duration used as refresh window
     * @return true when the access token should be renewed before sending a request
     */
    public boolean isAccessTokenExpiringWithin(Duration threshold) {
        Instant expiresAt = accessTokenExpiresAt.get();
        if (expiresAt == null) {
            return false;
        }
        Instant now = Instant.now();
        return !expiresAt.isAfter(now.plus(threshold));
    }

    /**
     * Indicates whether the refresh token is still usable.
     *
     * @return true when a refresh token exists and has not expired
     */
    public boolean hasUsableRefreshToken() {
        String currentRefreshToken = refreshToken.get();
        Instant expiresAt = refreshTokenExpiresAt.get();
        if (currentRefreshToken == null || currentRefreshToken.isBlank()) {
            return false;
        }
        return expiresAt == null || expiresAt.isAfter(Instant.now());
    }

    /**
     * Clears every session value so the desktop behaves as unauthenticated again.
     */
    public void logout() {
        accessToken.set(null);
        refreshToken.set(null);
        accessTokenExpiresAt.set(null);
        refreshTokenExpiresAt.set(null);
        usuario.set(null);
    }

    /**
     * Exposes the token property for bindings that need to react to authentication changes.
     *
     * @return observable property that stores the current bearer token
     */
    public StringProperty accessTokenProperty() {
        return accessToken;
    }

    /**
     * Exposes the logged-in user property for role-aware bindings and guards.
     *
     * @return observable property that stores the current user snapshot
     */
    public ObjectProperty<UsuarioSession> usuarioProperty() {
        return usuario;
    }

    /**
     * @return observable property that stores the current refresh token
     */
    public StringProperty refreshTokenProperty() {
        return refreshToken;
    }

    /**
     * @return observable property with the access token expiration instant
     */
    public ObjectProperty<Instant> accessTokenExpiresAtProperty() {
        return accessTokenExpiresAt;
    }

    /**
     * @return observable property with the refresh token expiration instant
     */
    public ObjectProperty<Instant> refreshTokenExpiresAtProperty() {
        return refreshTokenExpiresAt;
    }
}
