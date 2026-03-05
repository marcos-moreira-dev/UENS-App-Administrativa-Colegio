package com.marcosmoreiradev.uensbackend.config;

import com.marcosmoreiradev.uensbackend.config.properties.JwtProperties;
import com.marcosmoreiradev.uensbackend.config.properties.LoginProtectionProperties;
import com.marcosmoreiradev.uensbackend.config.properties.RefreshTokenProperties;
import com.marcosmoreiradev.uensbackend.config.properties.ReportOutputProperties;
import com.marcosmoreiradev.uensbackend.config.properties.ReportQueueProperties;
import com.marcosmoreiradev.uensbackend.config.properties.SecurityCorsProperties;
import com.marcosmoreiradev.uensbackend.config.properties.SecurityHeadersProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties({
        JwtProperties.class,
        RefreshTokenProperties.class,
        ReportQueueProperties.class,
        ReportOutputProperties.class,
        LoginProtectionProperties.class,
        SecurityCorsProperties.class,
        SecurityHeadersProperties.class
})
/**
 * Define la responsabilidad de SecurityPropertiesConfig dentro del backend UENS.
 * Contexto: modulo core, capa config, arquitectura monolito modular Spring Boot.
 * Alcance: centralizar configuracion transversal del contexto Spring Boot.
 */
public class SecurityPropertiesConfig {

    /**
     * Expone un reloj inyectable para hacer testeable la logica temporal de
     * tokens, lockout y otras politicas de seguridad.
     *
     * @return reloj UTC del proceso
     */
    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }
}
