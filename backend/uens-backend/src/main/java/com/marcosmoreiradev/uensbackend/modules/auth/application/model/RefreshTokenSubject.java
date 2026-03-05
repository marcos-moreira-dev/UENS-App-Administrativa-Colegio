package com.marcosmoreiradev.uensbackend.modules.auth.application.model;

/**
 * Identidad funcional asociada a un refresh token emitido por el backend.
 *
 * @param userId identificador del usuario administrativo
 * @param login nombre de login vinculado al token
 * @param role rol resuelto en el momento de la emision
 */
public record RefreshTokenSubject(Long userId, String login, String role) {
}
