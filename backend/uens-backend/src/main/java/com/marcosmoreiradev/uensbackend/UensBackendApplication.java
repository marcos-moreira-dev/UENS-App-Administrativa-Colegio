package com.marcosmoreiradev.uensbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/**
 * Define la responsabilidad de UensBackendApplication dentro del backend UENS.
 * Contexto: modulo core, capa core, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class UensBackendApplication {
/**
 * Implementa la operacion 'main' del modulo core en la capa core.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
   * @param args argumentos de arranque del runtime JVM/Spring usados al inicializar el backend UENS
 */
  public static void main(String[] args) {
    SpringApplication.run(UensBackendApplication.class, args);
  }
}
