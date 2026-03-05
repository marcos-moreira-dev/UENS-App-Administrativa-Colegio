package com.marcosmoreiradev.uensbackend.security.user;

import com.marcosmoreiradev.uensbackend.common.constants.SecurityRoles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Define la responsabilidad de CustomUserDetails dentro del backend UENS.
 * Contexto: modulo core, capa security, arquitectura monolito modular Spring Boot.
 * Alcance: cumplir una responsabilidad tecnica concreta dentro del monolito modular UENS.
 */

public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String username;
    private final String passwordHash;
    private final String role;
    private final boolean active;
/**
 * Construye la instancia de CustomUserDetails para operar en el modulo core.
 * Contexto: capa security con dependencias inyectadas segun la arquitectura modular UENS.
 *
     * @param userId identificador del usuario del sistema administrativo autenticado
     * @param username nombre_login del usuario del sistema administrativo para autenticacion
     * @param passwordHash hash persistido de credencial usado por Spring Security para validacion
     * @param role rol funcional del usuario (ADMIN o SECRETARIA) usado para autorizacion
     * @param active bandera de estado operativo del usuario para permitir o bloquear acceso
 */

    public CustomUserDetails(Long userId, String username, String passwordHash, String role, boolean active) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
    }

/**
 * Implementa la operacion 'getUserId' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Long getUserId() {
        return userId;
    }

/**
 * Implementa la operacion 'getRole' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getRole() {
        return role;
    }

    @Override
/**
 * Implementa la operacion 'getAuthorities' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (!SecurityRoles.isSupported(role)) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
/**
 * Implementa la operacion 'getPassword' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getPassword() {
        return passwordHash;
    }

    @Override
/**
 * Implementa la operacion 'getUsername' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public String getUsername() {
        return username;
    }

    @Override
/**
 * Implementa la operacion 'isAccountNonExpired' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
/**
 * Implementa la operacion 'isAccountNonLocked' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
/**
 * Implementa la operacion 'isCredentialsNonExpired' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
/**
 * Implementa la operacion 'isEnabled' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public boolean isEnabled() {
        return active;
    }
}

