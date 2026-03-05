package com.marcosmoreiradev.uens_backend;

import com.marcosmoreiradev.uensbackend.UensBackendApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = UensBackendApplication.class,
        properties = "spring.profiles.active=default"
)
/**
 * Define la responsabilidad de UensBackendApplicationTests dentro del backend UENS.
 * Contexto: modulo core, capa core, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
class UensBackendApplicationTests {

    @Test
    void contextLoads() {
    }
}
