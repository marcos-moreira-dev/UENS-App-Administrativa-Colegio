package com.marcosmoreiradev.uensbackend.modules.auth.application.support;

import com.marcosmoreiradev.uensbackend.common.exception.base.AuthException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.AuthErrorCodes;
import com.marcosmoreiradev.uensbackend.config.properties.LoginProtectionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Protege el flujo de login con dos controles complementarios:
 * un rate limit por IP y un bloqueo temporal por identidad cuando se acumulan
 * fallos consecutivos dentro de una ventana acotada.
 *
 * La implementacion es intencionalmente in-memory porque este backend es un
 * monolito didactico de una sola instancia. En despliegues distribuidos este
 * estado deberia externalizarse.
 */
@Service
public class LoginProtectionService {

    private static final Logger log = LoggerFactory.getLogger(LoginProtectionService.class);

    private final LoginProtectionProperties properties;
    private final Clock clock;
    private final Map<String, FailedLoginState> failedAttemptsByLogin = new HashMap<>();
    private final Map<String, IpRateLimitState> rateLimitByIp = new HashMap<>();

    public LoginProtectionService(LoginProtectionProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    /**
     * Verifica si el intento de login puede procesarse segun la politica actual.
     *
     * @param login login normalizado o recibido del cliente
     * @param clientIp IP remota usada para rate limiting
     */
    public synchronized void assertLoginAllowed(String login, String clientIp) {
        if (!properties.enabled()) {
            return;
        }

        Instant now = clock.instant();
        String normalizedLogin = normalizeLogin(login);
        String normalizedIp = normalizeIp(clientIp);

        enforceIpRateLimit(normalizedIp, now);

        if (normalizedLogin == null) {
            return;
        }

        FailedLoginState state = failedAttemptsByLogin.get(normalizedLogin);
        if (state == null) {
            return;
        }

        state.resetIfExpired(now, failureWindow());
        if (state.isLocked(now)) {
            long retryAfter = secondsUntil(state.lockedUntil, now);
            log.warn("Login bloqueado temporalmente. login={}, ip={}, retryAfterSeconds={}", normalizedLogin, normalizedIp, retryAfter);
            throw new AuthException(
                    AuthErrorCodes.AUTH_06_LOGIN_TEMPORALMENTE_BLOQUEADO,
                    AuthErrorCodes.AUTH_06_LOGIN_TEMPORALMENTE_BLOQUEADO.defaultMessage(),
                    Map.of("retryAfterSeconds", retryAfter)
            );
        }

        if (state.isEmpty()) {
            failedAttemptsByLogin.remove(normalizedLogin);
        }
    }

    /**
     * Registra un login fallido para la identidad indicada.
     *
     * @param login login afectado por el fallo
     * @param clientIp IP desde donde se realizo el intento
     */
    public synchronized void registerFailure(String login, String clientIp) {
        if (!properties.enabled()) {
            return;
        }

        String normalizedLogin = normalizeLogin(login);
        if (normalizedLogin == null) {
            return;
        }

        Instant now = clock.instant();
        FailedLoginState state = failedAttemptsByLogin.computeIfAbsent(normalizedLogin, key -> new FailedLoginState());
        state.recordFailure(now, failureWindow(), properties.maxFailedAttempts(), lockDuration());

        if (state.isLocked(now)) {
            long retryAfter = secondsUntil(state.lockedUntil, now);
            log.warn("Umbral de fallos de login alcanzado. login={}, ip={}, retryAfterSeconds={}", normalizedLogin, normalizeIp(clientIp), retryAfter);
        }
    }

    /**
     * Limpia el historial de fallos al autenticarse correctamente.
     *
     * @param login login autenticado
     * @param clientIp IP desde la que se autentico el usuario
     */
    public synchronized void registerSuccess(String login, String clientIp) {
        if (!properties.enabled()) {
            return;
        }

        String normalizedLogin = normalizeLogin(login);
        if (normalizedLogin != null) {
            failedAttemptsByLogin.remove(normalizedLogin);
        }
    }

    private void enforceIpRateLimit(String clientIp, Instant now) {
        IpRateLimitState state = rateLimitByIp.computeIfAbsent(clientIp, key -> new IpRateLimitState(now));
        state.resetIfExpired(now, ipWindow());
        if (!state.tryConsume(properties.maxRequestsPerIpWindow())) {
            long retryAfter = secondsUntil(state.windowStartedAt.plus(ipWindow()), now);
            log.warn("Rate limit de login alcanzado. ip={}, retryAfterSeconds={}", clientIp, retryAfter);
            throw new AuthException(
                    AuthErrorCodes.AUTH_07_RATE_LIMIT_LOGIN_EXCEDIDO,
                    AuthErrorCodes.AUTH_07_RATE_LIMIT_LOGIN_EXCEDIDO.defaultMessage(),
                    Map.of("retryAfterSeconds", retryAfter)
            );
        }
    }

    private Duration failureWindow() {
        return Duration.ofSeconds(properties.failureWindowSeconds());
    }

    private Duration ipWindow() {
        return Duration.ofSeconds(properties.ipWindowSeconds());
    }

    private Duration lockDuration() {
        return Duration.ofSeconds(properties.lockDurationSeconds());
    }

    private static long secondsUntil(Instant target, Instant now) {
        return Math.max(1L, Duration.between(now, target).getSeconds());
    }

    private static String normalizeLogin(String login) {
        if (login == null) {
            return null;
        }
        String normalized = login.trim().toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }

    private static String normalizeIp(String clientIp) {
        if (clientIp == null || clientIp.isBlank()) {
            return "unknown";
        }
        return clientIp.trim();
    }

    private static final class FailedLoginState {
        private Instant firstFailureAt;
        private Instant lastFailureAt;
        private Instant lockedUntil;
        private int failedAttempts;

        private void resetIfExpired(Instant now, Duration failureWindow) {
            if (lockedUntil != null && !now.isBefore(lockedUntil)) {
                clear();
                return;
            }
            if (firstFailureAt != null && now.isAfter(firstFailureAt.plus(failureWindow))) {
                clear();
            }
        }

        private void recordFailure(Instant now, Duration failureWindow, int maxFailedAttempts, Duration lockDuration) {
            resetIfExpired(now, failureWindow);
            if (firstFailureAt == null) {
                firstFailureAt = now;
            }
            lastFailureAt = now;
            failedAttempts++;
            if (failedAttempts >= maxFailedAttempts) {
                lockedUntil = now.plus(lockDuration);
            }
        }

        private boolean isLocked(Instant now) {
            return lockedUntil != null && now.isBefore(lockedUntil);
        }

        private boolean isEmpty() {
            return failedAttempts == 0 && firstFailureAt == null && lockedUntil == null;
        }

        private void clear() {
            firstFailureAt = null;
            lastFailureAt = null;
            lockedUntil = null;
            failedAttempts = 0;
        }
    }

    private static final class IpRateLimitState {
        private Instant windowStartedAt;
        private int consumed;

        private IpRateLimitState(Instant windowStartedAt) {
            this.windowStartedAt = windowStartedAt;
        }

        private void resetIfExpired(Instant now, Duration window) {
            if (now.isAfter(windowStartedAt.plus(window))) {
                windowStartedAt = now;
                consumed = 0;
            }
        }

        private boolean tryConsume(int maxAllowed) {
            if (consumed >= maxAllowed) {
                return false;
            }
            consumed++;
            return true;
        }
    }
}
