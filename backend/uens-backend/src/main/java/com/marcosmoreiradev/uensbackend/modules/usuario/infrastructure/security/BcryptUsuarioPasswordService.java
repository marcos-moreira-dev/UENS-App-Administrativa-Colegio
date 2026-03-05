package com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.security;

import com.marcosmoreiradev.uensbackend.modules.usuario.application.port.UsuarioPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adapter que desacopla la capa de aplicacion de la implementacion concreta de
 * hash provista por Spring Security.
 */
@Component
public class BcryptUsuarioPasswordService implements UsuarioPasswordService {

    private final PasswordEncoder passwordEncoder;

    public BcryptUsuarioPasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        return passwordEncoder.matches(rawPassword, passwordHash);
    }
}
