package com.marcosmoreiradev.uensbackend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;

/**
 * Agrupa la configuracion operativa del endurecimiento de login para limitar
 * abuso por IP y bloquear temporalmente identidades sometidas a intentos
 * repetidos de autenticacion.
 */
@Validated
@ConfigurationProperties(prefix = "app.security.login-protection")
public record LoginProtectionProperties(
        Boolean enabled,
        @Min(1) Integer maxFailedAttempts,
        @Min(1) Long failureWindowSeconds,
        @Min(1) Long lockDurationSeconds,
        @Min(1) Integer maxRequestsPerIpWindow,
        @Min(1) Long ipWindowSeconds
) {

    public LoginProtectionProperties {
        enabled = enabled == null ? Boolean.TRUE : enabled;
        maxFailedAttempts = maxFailedAttempts == null ? 5 : maxFailedAttempts;
        failureWindowSeconds = failureWindowSeconds == null ? 900L : failureWindowSeconds;
        lockDurationSeconds = lockDurationSeconds == null ? 900L : lockDurationSeconds;
        maxRequestsPerIpWindow = maxRequestsPerIpWindow == null ? 20 : maxRequestsPerIpWindow;
        ipWindowSeconds = ipWindowSeconds == null ? 300L : ipWindowSeconds;
    }
}
