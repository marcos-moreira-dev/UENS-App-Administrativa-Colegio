package com.marcosmoreiradev.uens_backend.modules.auth.application.support;

import com.marcosmoreiradev.uensbackend.common.exception.base.AuthException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.AuthErrorCodes;
import com.marcosmoreiradev.uensbackend.config.properties.LoginProtectionProperties;
import com.marcosmoreiradev.uensbackend.modules.auth.application.support.LoginProtectionService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginProtectionServiceTest {

    @Test
    void blocksLoginAfterConfiguredFailedAttemptsAndUnlocksLater() {
        MutableClock clock = new MutableClock(Instant.parse("2026-03-03T12:00:00Z"));
        LoginProtectionService service = new LoginProtectionService(
                new LoginProtectionProperties(true, 3, 300L, 120L, 10, 60L),
                clock
        );

        service.registerFailure("admin", "127.0.0.1");
        service.registerFailure("admin", "127.0.0.1");
        service.registerFailure("admin", "127.0.0.1");

        assertThatThrownBy(() -> service.assertLoginAllowed("admin", "127.0.0.1"))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCodes.AUTH_06_LOGIN_TEMPORALMENTE_BLOQUEADO);

        clock.advanceSeconds(121L);

        service.assertLoginAllowed("admin", "127.0.0.1");
    }

    @Test
    void rateLimitsRepeatedRequestsFromSameIp() {
        MutableClock clock = new MutableClock(Instant.parse("2026-03-03T12:00:00Z"));
        LoginProtectionService service = new LoginProtectionService(
                new LoginProtectionProperties(true, 5, 300L, 120L, 2, 60L),
                clock
        );

        service.assertLoginAllowed("admin", "127.0.0.1");
        service.assertLoginAllowed("admin", "127.0.0.1");

        assertThatThrownBy(() -> service.assertLoginAllowed("admin", "127.0.0.1"))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCodes.AUTH_07_RATE_LIMIT_LOGIN_EXCEDIDO);

        clock.advanceSeconds(61L);

        service.assertLoginAllowed("admin", "127.0.0.1");
    }

    @Test
    void successClearsAccumulatedFailures() {
        MutableClock clock = new MutableClock(Instant.parse("2026-03-03T12:00:00Z"));
        LoginProtectionService service = new LoginProtectionService(
                new LoginProtectionProperties(true, 3, 300L, 120L, 10, 60L),
                clock
        );

        service.registerFailure("admin", "127.0.0.1");
        service.registerFailure("admin", "127.0.0.1");
        service.registerSuccess("admin", "127.0.0.1");

        service.assertLoginAllowed("admin", "127.0.0.1");
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
