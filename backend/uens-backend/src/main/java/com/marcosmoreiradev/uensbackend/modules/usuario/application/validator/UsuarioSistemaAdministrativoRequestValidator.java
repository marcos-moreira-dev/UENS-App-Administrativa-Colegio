package com.marcosmoreiradev.uensbackend.modules.usuario.application.validator;

import com.marcosmoreiradev.uensbackend.common.api.response.ErrorDetailDto;
import com.marcosmoreiradev.uensbackend.common.constants.SecurityRoles;
import com.marcosmoreiradev.uensbackend.common.exception.base.ValidationException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ApiErrorCodes;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * Valida reglas de formato y catalogo para usuarios administrativos antes de
 * ejecutar logica de negocio.
 */
@Component
public class UsuarioSistemaAdministrativoRequestValidator {

    public void validarRol(String rol) {
        String normalized = normalizeUpper(rol);
        if (normalized != null && SecurityRoles.isSupported(normalized)) {
            return;
        }

        throw new ValidationException(
                ApiErrorCodes.VR_06_VALOR_ENUM_INVALIDO,
                "El rol indicado no es valido.",
                List.of(ErrorDetailDto.of("rol", "InvalidRole", "Solo se permiten ADMIN o SECRETARIA.", rol))
        );
    }

    public void validarEstado(String estado) {
        String normalized = normalizeUpper(estado);
        if ("ACTIVO".equals(normalized) || "INACTIVO".equals(normalized)) {
            return;
        }

        throw new ValidationException(
                ApiErrorCodes.VR_06_VALOR_ENUM_INVALIDO,
                "El estado indicado no es valido.",
                List.of(ErrorDetailDto.of("estado", "InvalidState", "Solo se permiten ACTIVO o INACTIVO.", estado))
        );
    }

    public void validarPasswordOpcional(String password) {
        if (password == null) {
            return;
        }
        if (!password.isBlank()) {
            return;
        }

        throw new ValidationException(
                ApiErrorCodes.VR_07_CAMPO_REQUERIDO,
                "La contrasena no puede ser solo espacios cuando se envia.",
                List.of(ErrorDetailDto.of("password", "BlankPassword", "La contrasena no puede estar vacia.", password))
        );
    }

    private static String normalizeUpper(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized.toUpperCase(Locale.ROOT);
    }
}
