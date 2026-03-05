package com.marcosmoreiradev.uensbackend.security.user;

import com.marcosmoreiradev.uensbackend.common.constants.SecurityRoles;

/**
 * Representa el usuario autenticado que interesa a la capa application sin
 * exponer directamente detalles de Spring Security.
 *
 * @param userId identificador tecnico del usuario autenticado
 * @param login nombre de login autenticado
 * @param role rol funcional resuelto desde el token o el principal
 */
public record AuthenticatedUserContext(Long userId, String login, String role) {

    /**
     * Indica si el usuario actual posee alcance administrativo global.
     *
     * @return {@code true} cuando el rol es ADMIN
     */
    public boolean isAdmin() {
        return SecurityRoles.ADMIN.equalsIgnoreCase(role);
    }
}
