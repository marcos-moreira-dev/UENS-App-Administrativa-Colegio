package com.marcosmoreiradev.uensbackend.common.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
/**
 * Define la responsabilidad de MessageResolver dentro del backend UENS.
 * Contexto: modulo core, capa common, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */
public class MessageResolver {

    private final MessageSource messageSource;
/**
 * Construye la instancia de MessageResolver para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param messageSource dato de entrada relevante para ejecutar esta operacion: 'messageSource'
 */

    public MessageResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

/**
 * Implementa la operacion 'get' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param key dato de entrada relevante para ejecutar esta operacion: 'key'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String get(String key) {
        return get(key, null, key, LocaleContextHolder.getLocale());
    }

/**
 * Implementa la operacion 'get' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param key dato de entrada relevante para ejecutar esta operacion: 'key'
     * @param fallback dato de entrada relevante para ejecutar esta operacion: 'fallback'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String get(String key, String fallback) {
        return get(key, null, fallback, LocaleContextHolder.getLocale());
    }

/**
 * Implementa la operacion 'get' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param key dato de entrada relevante para ejecutar esta operacion: 'key'
     * @param args argumentos de arranque del runtime JVM/Spring usados al inicializar el backend UENS
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String get(String key, Object[] args) {
        return get(key, args, key, LocaleContextHolder.getLocale());
    }

/**
 * Implementa la operacion 'get' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param key dato de entrada relevante para ejecutar esta operacion: 'key'
     * @param args argumentos de arranque del runtime JVM/Spring usados al inicializar el backend UENS
     * @param fallback dato de entrada relevante para ejecutar esta operacion: 'fallback'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String get(String key, Object[] args, String fallback) {
        return get(key, args, fallback, LocaleContextHolder.getLocale());
    }

/**
 * Implementa la operacion 'get' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param key dato de entrada relevante para ejecutar esta operacion: 'key'
     * @param args argumentos de arranque del runtime JVM/Spring usados al inicializar el backend UENS
     * @param fallback dato de entrada relevante para ejecutar esta operacion: 'fallback'
     * @param locale dato de entrada relevante para ejecutar esta operacion: 'locale'
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String get(String key, Object[] args, String fallback, Locale locale) {
        Locale resolvedLocale = locale != null ? locale : LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(key, args, resolvedLocale);
        } catch (NoSuchMessageException ignored) {
            return fallback != null ? fallback : key;
        }
    }
}

