package com.marcosmoreiradev.uensbackend.security.user;

import com.marcosmoreiradev.uensbackend.common.exception.base.AuthException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.AuthErrorCodes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Actua como fachada entre la capa application y el contexto de seguridad
 * activo para evitar que los servicios dependan de APIs de Spring Security.
 */
@Service
public class CurrentAuthenticatedUserService {

    /**
     * Obtiene el usuario autenticado actual si el principal expone la metadata
     * esperada del backend.
     *
     * @return usuario autenticado actual o vacio si el contexto no es util
     */
    public Optional<AuthenticatedUserContext> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return Optional.of(new AuthenticatedUserContext(
                    details.getUserId(),
                    details.getUsername(),
                    details.getRole()
            ));
        }
        return Optional.empty();
    }

    /**
     * Expone el id del usuario actual cuando esta disponible.
     *
     * @return id actual o {@code null} si el principal no lo provee
     */
    public Long getCurrentUserIdOrNull() {
        return getCurrentUser().map(AuthenticatedUserContext::userId).orElse(null);
    }

    /**
     * Indica si el contexto autenticado actual corresponde a un administrador.
     *
     * @return {@code true} cuando el rol resuelto es ADMIN
     */
    public boolean isAdmin() {
        return getCurrentUser().map(AuthenticatedUserContext::isAdmin).orElse(false);
    }

    /**
     * Exige que el contexto actual corresponda a un administrador.
     *
     * @param message mensaje funcional cuando el usuario no cumple la politica
     */
    public void ensureAdmin(String message) {
        if (!isAdmin()) {
            throw new AuthException(
                    AuthErrorCodes.AUTH_04_SIN_PERMISOS,
                    message == null || message.isBlank()
                            ? AuthErrorCodes.AUTH_04_SIN_PERMISOS.defaultMessage()
                            : message
            );
        }
    }
}
