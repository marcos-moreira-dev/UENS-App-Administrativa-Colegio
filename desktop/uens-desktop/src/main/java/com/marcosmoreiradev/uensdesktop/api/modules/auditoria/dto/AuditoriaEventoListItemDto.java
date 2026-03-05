package com.marcosmoreiradev.uensdesktop.api.modules.auditoria.dto;

import java.time.LocalDateTime;

/**
 * Row-level audit event summary returned by the audit listing.
 *
 * @param eventoId backend identifier of the audit event
 * @param modulo module where the operation occurred
 * @param accion operation performed
 * @param entidad affected entity name
 * @param entidadId affected entity identifier as text
 * @param resultado outcome of the operation
 * @param actorLogin login that triggered the operation
 * @param actorRol role associated with the actor
 * @param requestId backend request id used for traceability
 * @param ipOrigen client IP reported by the backend
 * @param fechaEvento event timestamp
 */
public record AuditoriaEventoListItemDto(
        Long eventoId,
        String modulo,
        String accion,
        String entidad,
        String entidadId,
        String resultado,
        String actorLogin,
        String actorRol,
        String requestId,
        String ipOrigen,
        LocalDateTime fechaEvento) {
}
