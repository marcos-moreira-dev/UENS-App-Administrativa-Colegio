package com.marcosmoreiradev.uensbackend.security.user;

import com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.entity.UsuarioSistemaAdministrativoJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.repository.UsuarioSistemaAdministrativoJpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
/**
 * Define la responsabilidad de CustomUserDetailsService dentro del backend UENS.
 * Contexto: modulo core, capa security, arquitectura monolito modular Spring Boot.
 * Alcance: orquestar comportamiento de aplicacion entre dominio, persistencia y seguridad.
 */
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioSistemaAdministrativoJpaRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioSistemaAdministrativoJpaRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
/**
 * Implementa la operacion 'loadUserByUsername' del modulo core en la capa security.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param username nombre_login del usuario del sistema administrativo para autenticacion
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 * @throws UsernameNotFoundException si el usuario no existe o no puede cargarse para autenticacion.
 */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioSistemaAdministrativoJpaEntity usuario = usuarioRepository.findByNombreLoginIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        boolean active = "ACTIVO".equalsIgnoreCase(usuario.getEstado());
        return new CustomUserDetails(
                usuario.getId(),
                usuario.getNombreLogin(),
                usuario.getPasswordHash(),
                usuario.getRol(),
                active
        );
    }
}
