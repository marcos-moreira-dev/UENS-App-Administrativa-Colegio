package com.marcosmoreiradev.uensbackend.common.constants;

import java.util.Set;

public final class SecurityRoles {
/**
 * Construye la instancia de SecurityRoles para operar en el modulo core.
 * Contexto: capa common con dependencias inyectadas segun la arquitectura modular UENS.
 */

    private SecurityRoles() {
    }

    public static final String ADMIN = "ADMIN";
    public static final String SECRETARIA = "SECRETARIA";

    public static final String ROLE_ADMIN = "ROLE_" + ADMIN;
    public static final String ROLE_SECRETARIA = "ROLE_" + SECRETARIA;
/**
 * Implementa la operacion 'of' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param ADMIN dato de entrada relevante para ejecutar esta operacion: 'ADMIN'
     * @param SECRETARIA dato de entrada relevante para ejecutar esta operacion: 'SECRETARIA'
 */

    public static final Set<String> ALL = Set.of(ADMIN, SECRETARIA);

/**
 * Implementa la operacion 'isSupported' del modulo core en la capa common.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param role rol funcional del usuario (ADMIN o SECRETARIA) usado para autorizacion
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public static boolean isSupported(String role) {
        return role != null && ALL.contains(role);
    }
}
