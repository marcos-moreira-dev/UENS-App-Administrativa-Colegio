package com.marcosmoreiradev.uens_backend.modules.auth.application.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.marcosmoreiradev.uensbackend.common.exception.base.AuthException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.AuthErrorCodes;
import com.marcosmoreiradev.uensbackend.config.properties.RefreshTokenProperties;
import com.marcosmoreiradev.uensbackend.modules.auth.application.model.IssuedRefreshToken;
import com.marcosmoreiradev.uensbackend.modules.auth.application.model.RefreshTokenSubject;
import com.marcosmoreiradev.uensbackend.modules.auth.application.support.RefreshTokenService;
import com.marcosmoreiradev.uensbackend.modules.auth.infrastructure.security.InMemoryRefreshTokenStore;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class RefreshTokenServiceTest {

    @Test
    void rotateRevokesPreviousTokenAndReturnsANewOne() {
        MutableClock clock = new MutableClock(Instant.parse("2026-03-03T12:00:00Z"));
        RefreshTokenService service = new RefreshTokenService(
                new RefreshTokenProperties(true, 600L, 32),
                new InMemoryRefreshTokenStore(),
                clock
        );

        IssuedRefreshToken issued = service.issue(new RefreshTokenSubject(1L, "admin", "ADMIN"));
        IssuedRefreshToken rotated = service.rotate(issued.rawToken());

        assertThat(rotated.rawToken()).isNotEqualTo(issued.rawToken());
        assertThat(rotated.subject().login()).isEqualTo("admin");

        assertThatThrownBy(() -> service.rotate(issued.rawToken()))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCodes.AUTH_08_REFRESH_TOKEN_INVALIDO);
    }

    @Test
    void expiredRefreshTokenIsRejectedWithSpecificErrorCode() {
        MutableClock clock = new MutableClock(Instant.parse("2026-03-03T12:00:00Z"));
        RefreshTokenService service = new RefreshTokenService(
                new RefreshTokenProperties(true, 300L, 32),
                new InMemoryRefreshTokenStore(),
                clock
        );

        IssuedRefreshToken issued = service.issue(new RefreshTokenSubject(1L, "admin", "ADMIN"));
        clock.advanceSeconds(301L);

        assertThatThrownBy(() -> service.rotate(issued.rawToken()))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCodes.AUTH_09_REFRESH_TOKEN_EXPIRADO);
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }

        private void advanceSeconds(long seconds) {
            instant = instant.plusSeconds(seconds);
        }
    }
}
