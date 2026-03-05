package com.marcosmoreiradev.uensbackend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.report.queue")
/**
 * Define la responsabilidad de ReportQueueProperties dentro del backend UENS.
 * Contexto: modulo core, capa config, arquitectura monolito modular Spring Boot.
 * Alcance: encapsular propiedades tipadas de configuracion por modulo.
 */
public record ReportQueueProperties(
        @DefaultValue("true") boolean enabled,
        @DefaultValue("5000") long initialDelayMs,
        @DefaultValue("10000") long fixedDelayMs,
        @DefaultValue("2") int schedulerPoolSize,
        @DefaultValue("10") int claimBatchSize,
        @DefaultValue("3") int maxAttempts
) {
}

