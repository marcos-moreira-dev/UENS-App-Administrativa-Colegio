package com.marcosmoreiradev.uensbackend.modules.usuario.application.port;

/**
 * Puerto de aplicacion para encapsular el algoritmo de hash de credenciales.
 */
public interface UsuarioPasswordService {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}
