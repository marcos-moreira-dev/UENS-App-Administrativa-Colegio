package com.marcosmoreiradev.uensdesktop.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class SessionStateTest {

    @Test
    void loginStoresTokenUserAndDerivedRole() {
        SessionState sessionState = new SessionState();
        UsuarioSession usuario = new UsuarioSession(4L, "secretaria.demo", Role.SECRETARIA, "ACTIVO");

        sessionState.login("token-demo", usuario);

        assertThat(sessionState.token()).contains("token-demo");
        assertThat(sessionState.usuario()).contains(usuario);
        assertThat(sessionState.role()).contains(Role.SECRETARIA);
    }

    @Test
    void logoutClearsAllSessionState() {
        SessionState sessionState = new SessionState();
        sessionState.login(
                "token-demo",
                Instant.parse("2026-03-04T10:00:00Z"),
                "refresh-demo",
                Instant.parse("2026-03-10T10:00:00Z"),
                new UsuarioSession(1L, "admin", Role.ADMIN, "ACTIVO")
        );

        sessionState.logout();

        assertThat(sessionState.token()).isEmpty();
        assertThat(sessionState.refreshToken()).isEmpty();
        assertThat(sessionState.usuario()).isEmpty();
        assertThat(sessionState.role()).isEmpty();
        assertThat(sessionState.accessTokenProperty().get()).isNull();
        assertThat(sessionState.refreshTokenProperty().get()).isNull();
        assertThat(sessionState.usuarioProperty().get()).isNull();
    }

    @Test
    void refreshMetadataSupportsSilentRenewalChecks() {
        SessionState sessionState = new SessionState();
        Instant now = Instant.now();

        sessionState.login(
                "token-demo",
                now.plusSeconds(30),
                "refresh-demo",
                now.plusSeconds(300),
                new UsuarioSession(1L, "admin", Role.ADMIN, "ACTIVO")
        );

        assertThat(sessionState.hasUsableRefreshToken()).isTrue();
        assertThat(sessionState.isAccessTokenExpiringWithin(Duration.ofSeconds(60))).isTrue();

        sessionState.updateTokens(
                "token-nuevo",
                now.plusSeconds(3600),
                "refresh-nuevo",
                now.plusSeconds(7200)
        );

        assertThat(sessionState.token()).contains("token-nuevo");
        assertThat(sessionState.refreshToken()).contains("refresh-nuevo");
        assertThat(sessionState.isAccessTokenExpiringWithin(Duration.ofSeconds(60))).isFalse();
    }
}
