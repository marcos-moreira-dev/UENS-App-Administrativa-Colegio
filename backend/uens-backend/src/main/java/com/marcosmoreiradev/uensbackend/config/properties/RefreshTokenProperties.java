package com.marcosmoreiradev.uensbackend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;

/**
 * Parametriza el ciclo de vida del refresh token para sesiones desktop con
 * renovacion automatica del access token.
 */
@Validated
@ConfigurationProperties(prefix = "app.security.refresh-token")
public record RefreshTokenProperties(
        Boolean enabled,
        @Min(300) Long expirationSeconds,
        @Min(16) Integer tokenBytesLength
) {

    public RefreshTokenProperties {
        enabled = enabled == null ? Boolean.TRUE : enabled;
        expirationSeconds = expirationSeconds == null ? 604800L : expirationSeconds;
        tokenBytesLength = tokenBytesLength == null ? 48 : tokenBytesLength;
    }
}
