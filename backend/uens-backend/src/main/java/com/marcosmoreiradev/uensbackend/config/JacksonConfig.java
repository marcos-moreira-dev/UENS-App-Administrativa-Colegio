package com.marcosmoreiradev.uensbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Configuration
/**
 * Define la responsabilidad de JacksonConfig dentro del backend UENS.
 * Contexto: modulo core, capa config, arquitectura monolito modular Spring Boot.
 * Alcance: centralizar configuracion transversal del contexto Spring Boot.
 */
public class JacksonConfig {

    @Bean
/**
 * Implementa la operacion 'objectMapper' del modulo core en la capa config.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }
}

