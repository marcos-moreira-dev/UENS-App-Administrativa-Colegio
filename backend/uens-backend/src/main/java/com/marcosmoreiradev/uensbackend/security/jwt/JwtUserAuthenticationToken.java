package com.marcosmoreiradev.uensbackend.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Define la responsabilidad de JwtUserAuthenticationToken dentro del backend UENS.
 * Contexto: modulo core, capa security, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */

public class JwtUserAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
/**
 * Construye la instancia de JwtUserAuthenticationToken para operar en el modulo core.
 * Contexto: capa security con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param principal principal autenticado resuelto desde JWT y/o UserDetailsService
     * @param authorities autoridades Spring derivadas del rol para evaluacion de @PreAuthorize
 */

    public JwtUserAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
/**
 * Implementa la operacion 'getCredentials' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Object getCredentials() {
        return null;
    }

    @Override
/**
 * Implementa la operacion 'getPrincipal' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
 * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Object getPrincipal() {
        return principal;
    }
}
