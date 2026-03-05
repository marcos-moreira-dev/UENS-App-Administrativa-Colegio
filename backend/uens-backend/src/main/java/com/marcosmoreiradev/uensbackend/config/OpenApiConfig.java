package com.marcosmoreiradev.uensbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

/**
 * Define la responsabilidad de OpenApiConfig dentro del backend UENS.
 * Contexto: modulo core, capa config, arquitectura monolito modular Spring Boot.
 * Alcance: centralizar configuracion transversal del contexto Spring Boot.
 */
public class OpenApiConfig {
/**
 * Implementa la operacion 'uensOpenApi' del modulo core en la capa config.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param appName dato de entrada relevante para ejecutar esta operacion: 'appName'
     * @param title dato de entrada relevante para ejecutar esta operacion: 'title'
     * @param description dato de entrada relevante para ejecutar esta operacion: 'description'
     * @param version dato de entrada relevante para ejecutar esta operacion: 'version'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */

    @Bean
    public OpenAPI uensOpenApi(
            @Value("${spring.application.name:uens-backend}") String appName,
            @Value("${app.openapi.title:UENS Backend API}") String title,
            @Value("${app.openapi.description:API del Sistema UE Ninitos Sonadores}") String description,
            @Value("${app.openapi.version:v1}") String version
    ) {
        String bearerSchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version)
                        .contact(new Contact().name(appName)))
                .components(new Components().addSecuritySchemes(
                        bearerSchemeName,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ))
                .addSecurityItem(new SecurityRequirement().addList(bearerSchemeName));
    }

    @Bean
/**
 * Implementa la operacion 'apiV1Group' del modulo core en la capa config.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public GroupedOpenApi apiV1Group() {
        return GroupedOpenApi.builder()
                .group("api-v1")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}
